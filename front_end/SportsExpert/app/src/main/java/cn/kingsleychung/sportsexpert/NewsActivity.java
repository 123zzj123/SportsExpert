package cn.kingsleychung.sportsexpert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
public class NewsActivity extends Activity implements AdapterView.OnItemClickListener{
    private Handler handler;
    private Elements Title;
    private LoadMoreListView mListView;
    private List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
    private List<String> NewsUrl = new ArrayList<String>();
    private MyAdapter myAdapter;
    private int end = 0;
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mListView= (LoadMoreListView) findViewById(R.id.List);
        init();
        myAdapter = new MyAdapter(NewsActivity.this,data);
        mListView.setAdapter(myAdapter);

        mListView.setOnItemClickListener(this);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) {
                    myAdapter.UpdateData(data);
                    myAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(getResources().getString(R.string.KoBe)).get();
                    Title = document.select("div.result");
                    Map<String,Object> map;
                    end = end + 8 > Title.size() ? Title.size():end + 8;
                    for(int i = 0; i < end; i++) {
                        map = new HashMap<String, Object>();
                        //map.put("img",R.mipmap.ic_launcher);
                        map.put("title",Title.get(i).select("h3.c-title").text());
                        String From = Title.get(i).select("p.c-author").text();
                        map.put("from",From);
                        String Content = Title.get(i).select("div.c-span18.c-span-last").text();
                        if(Content != "") {
                            Content = Content.substring(From.length(),Content.length()-20);
                            map.put("content",Content);
                        }
                        else {
                            Content = Title.get(i).select("div.c-summary.c-row").text();
                            Content = Content.substring(From.length(),Content.length()-20);
                            map.put("content",Content);
                        }
                        String t = Title.get(i).select("h3.c-title>a").attr("href");
                        NewsUrl.add(t);
                        data.add(map);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        mListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                loadMore();
            }
        });
    }

    private void AddData() {
        data = new ArrayList<Map<String, Object>>();
        NewsUrl = new ArrayList<String>();
        Map<String,Object> map;
        end = end + 8 > Title.size() ? Title.size():end + 8;
        for(int i = 0; i < end; i++) {
            map = new HashMap<String, Object>();
            //map.put("img",R.mipmap.ic_launcher);
            map.put("title",Title.get(i).select("h3.c-title").text());
            String From = Title.get(i).select("p.c-author").text();
            map.put("from",From);
            String Content = Title.get(i).select("div.c-span18.c-span-last").text();
            if(Content != "") {
                Content = Content.substring(From.length(),Content.length()-20);
                map.put("content",Content);
            }
            else {
                Content = Title.get(i).select("div.c-summary.c-row").text();
                Content = Content.substring(From.length(),Content.length()-20);
                map.put("content",Content);
            }
            String t = Title.get(i).select("h3.c-title>a").attr("href");
            NewsUrl.add(t);
            data.add(map);
        }
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    private void loadMore() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AddData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setLoadCompleted();
                    }
                });
            }
        }.start();
    }

    //采用Handler+Thread+封装外部接口
    private void loadImage(final String url, final int id) {
//如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
        Drawable cacheImage = asyncImageLoader.loadDrawable(url,new AsyncImageLoader.ImageCallback() {
            //请参见实现：如果第一次加载url时下面方法会执行
            public void imageLoaded(Drawable imageDrawable) {
                data.get(id).put("img",imageDrawable);
            }
        });
        if(cacheImage!=null){
            data.get(id).put("img",cacheImage);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(NewsActivity.this,NewsDisplayActvivity.class);
        intent.putExtra("news_url",NewsUrl.get(position));
        startActivity(intent);
    }
}
