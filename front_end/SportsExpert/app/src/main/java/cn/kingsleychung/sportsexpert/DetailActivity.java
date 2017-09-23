package cn.kingsleychung.sportsexpert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JJ on 2017/9/23.
 */
public class DetailActivity extends Activity {
    private Handler handler;
    private String Name;
    private String Identity;
    private String Introduce;
    private String ImgsUrl;
    private TextView NameTview;
    private TextView IdentityTview;
    private ViewPager PhotoShow;
    //private LinearLayout PhotoLView;
    private TextView IntroduceTview;
    private SimpleAdapter simpleAdapter;
    private GridView BasicInfoView;
    private MyPagerAdapter myAdapter;
    private Button NewsButton;
    private Button ArticleButton;
    private List<String> Info1;
    private List<String> Info2;
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getSupportActionBar().hide();
        Info1 = new ArrayList<String>();
        Info2 = new ArrayList<String>();
        NameTview = (TextView) findViewById(R.id.Name);
        IdentityTview = (TextView) findViewById(R.id.Identity);
        PhotoShow = (ViewPager)findViewById(R.id.Photo);
        //PhotoLView = (LinearLayout) findViewById(R.id.PhotoShow);
        IntroduceTview = (TextView)findViewById(R.id.Introduce);
        BasicInfoView = (GridView)findViewById(R.id.BasicInfo);
        myAdapter = new MyPagerAdapter();
        PhotoShow.setAdapter(myAdapter);
        NewsButton = (Button)findViewById(R.id.News);
        ArticleButton = (Button)findViewById(R.id.Article);
        NewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailActivity.this,"正在前往新闻界面",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailActivity.this,NewsActivity.class);
                startActivity(intent);
            }
        });

        ArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailActivity.this,"正在前往文章界面",Toast.LENGTH_SHORT).show();
            }
        });
        GetMessage();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) {
                    NameTview.setText(Name);
                    IdentityTview.setText(Identity);
                    IntroduceTview.setText(Introduce);
                    List<Map<String,Object>>listems = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < Info1.size(); i++) {
                        Map<String, Object>listem = new HashMap<String,Object>();
                        listem.put("MessageDT",Info1.get(i));
                        listem.put("MessageDD",Info2.get(i));
                        listems.add(listem);
                    }
                    simpleAdapter = new SimpleAdapter(DetailActivity.this,listems,R.layout.gridview_item, new String[] {"MessageDT","MessageDD"},new int[]{R.id.MessageDT,R.id.MessageDD});
                    BasicInfoView.setAdapter(simpleAdapter);
                    setGridViewHeightBasedOnChildren(BasicInfoView);
                }
            }
        };
    }

    public void GetMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(getResources().getString(R.string.KoBeDetail)).get();
                    Elements Title = document.select("dd.lemmaWgt-lemmaTitle-title");
                    Name = Title.select("h1").text();
                    Identity = Title.select("h2").text();
                    String tempUrl = document.select("div.summary-pic>a").attr("href");
                    ImgsUrl = getResources().getString(R.string.BaiduBaike);
                    ImgsUrl+= tempUrl;
                    Document document1 = Jsoup.connect(ImgsUrl).get();
                    Elements ImgSrcs = document1.select("div.pic-list>a");
                    Introduce = document.select("div.lemma-summary").text();
                    Elements InfoLeftDT = document.select("dl.basicInfo-block.basicInfo-left").select("dt");
                    Elements InfoLeftDD = document.select("dl.basicInfo-block.basicInfo-left").select("dd");
                    for (int i = 0; i < InfoLeftDT.size(); i++) {
                        Info1.add(InfoLeftDT.get(i).text());
                        Info2.add(InfoLeftDD.get(i).text());
                    }
                    for(int i = 0; i < ImgSrcs.size(); i++) {
                        String temp1 = getResources().getString(R.string.BaiduBaike);
                        temp1 += ImgSrcs.get(i).attr("href");
                        Document document2 = Jsoup.connect(temp1).get();
                        temp1 = document2.select("img#imgPicture").attr("src");
                        loadImage(temp1);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView) {
        if(simpleAdapter == null){
            return;
        }
        // 固定列宽，有多少列
        int col = 1;//listView.getNumColumns();
        int totalHeight = 0;
        for(int i = 0; i < simpleAdapter.getCount(); i+=col){
            // 获取listview的每一个item
            View listItem = simpleAdapter.getView(i, null, gridView);
            listItem.measure(0,0);
            // 获取item的高度和  
            totalHeight += listItem.getMeasuredHeight();
        }
        // 获取listview的布局参数  
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度  
        params.height = totalHeight;
        // 设置参数  
        gridView.setLayoutParams(params);
    }

    //采用Handler+Thread+封装外部接口
    private void loadImage(final String url) {
//如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
        Drawable cacheImage = asyncImageLoader.loadDrawable(url,new AsyncImageLoader.ImageCallback() {
            //请参见实现：如果第一次加载url时下面方法会执行
            public void imageLoaded(Drawable imageDrawable) {
                View Inflate = View.inflate(DetailActivity.this,R.layout.linear_item,null);
                ImageView imageView = (ImageView)Inflate.findViewById(R.id.image);
                imageView.setImageDrawable(imageDrawable);
                myAdapter.AddView(Inflate);
                PhotoShow.setAdapter(myAdapter);
                //PhotoLView.addView(Inflate);
            }
        });
        if(cacheImage!=null){
            View Inflate = View.inflate(DetailActivity.this,R.layout.linear_item,null);
            ImageView imageView = (ImageView)Inflate.findViewById(R.id.image);
            imageView.setImageDrawable(cacheImage);
            myAdapter.AddView(Inflate);
            PhotoShow.setAdapter(myAdapter);
            //PhotoLView.addView(Inflate);
        }
    }
}
