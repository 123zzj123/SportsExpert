package cn.kingsleychung.sportsexpert;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JJ on 2017/9/19.
 */
public class KnowledgeActivity extends FragmentActivity{
    private LinearLayout linear01;
    private IndicatorView indicatorView;
    private ArrayList<View> listViews;
    private List<Map<String, Object>> listView;
    private int next = 0;
    private ViewPager adViewPager;
    private AdPageAdapter adapter;
    private Drawable[] drawables;
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private boolean isContinue = true;
    private List<View> gridViewlist = new ArrayList<View>();
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    private int count = 600;
    private String[] URLPath;
    //private Boolean[][] IsDoneLoad = new Boolean[12][50];
    private Button KnowLedge;
    private Button Camera;
    private Button Voice;
    private Handler handler;
    private String[] SportsItem;
    private List<Data> data = new ArrayList<Data>();
    private int[] ItemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        initIndicator();
        IniMessage();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) {
                    initItem();
                    initView();
                    iniButton();
                }
            }
        };
    }

    private  void IniMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                JSONArray items = new JSONArray();
                jsonObject = tool.getList();
                try {
                    items = jsonObject.getJSONArray("result");
                    SportsItem = new String[items.length()];
                    ItemNum = new int[items.length()];
                    for(int i = 0; i < items.length(); i++) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("sid",i);
                        JSONObject jsonObject2 = tool.getObject(jsonObject1);
                        JSONArray item = jsonObject2.getJSONArray("result");
                        ItemNum[i] = item.length();
                        for(int j = 0; j < item.length(); j++) {
                            JSONObject jsonObject3 = item.getJSONObject(j);
                            Data temp = new Data();
                            temp.sid = i;
                            temp.name = jsonObject3.getString("name");
                            temp.pos = j;
                            temp.picAddress = jsonObject3.getString("picAddress");
                            temp.contantAddress = jsonObject3.getString("contantAddress");
                            temp.newsAddress = jsonObject3.getString("newsAddress");
                            data.add(temp);
                        }
                        SportsItem[i] = items.getJSONObject(i).getString("sname");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    //顶部导航栏
    private void initIndicator(){
        indicatorView = (IndicatorView)findViewById(R.id.indicator);
        Resources resources = getResources();
        indicatorView.color(resources.getColor(android.R.color.black),
                resources.getColor(android.R.color.holo_red_light),
                resources.getColor(android.R.color.darker_gray))
                .textSize(sp2px(this, 16))
                .padding(new int[]{dip2px(this, 14), dip2px(this, 14), dip2px(this, 14), dip2px(this, 14)})
                .text(resources.getStringArray(R.array.sports))
                .defaultSelect(0).lineHeight(dip2px(this, 3))
                .listener(new IndicatorView.OnIndicatorChangedListener(){

                    @Override
                    public void onChanged(int position){
                        Toast.makeText(KnowledgeActivity.this,"点击了第"+ position + "项", Toast.LENGTH_SHORT).show();
                        adViewPager.setCurrentItem(position);
                    }
                }).commit();
    }

    private void initItem() {
        indicatorView.text(SportsItem).commit();
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue){
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        indicatorView.release();
    }

    //GridView与ViewPager结合
    private void initView() {
        linear01 = (LinearLayout) findViewById(R.id.view_pager_content);
        listView = new ArrayList<Map<String, Object>>();
        // 创建ViewPager
        adViewPager = new ViewPager(this);
        adViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicatorView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        linear01.addView(adViewPager);
        IniImageURL();
        getGridView();
        // 将GridView添加到ViewPager显示
        adViewPager.setAdapter(adapter);
        //adViewPager.setOnPageChangeListener(new AdPageChangeListener());

        for (int i = 0; i < gridViewlist.size(); i++) {
            GridView view = (GridView) gridViewlist.get(i);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(KnowledgeActivity.this,"hello"+ parent.getId(),Toast.LENGTH_SHORT).show();
                    int Start = 0;
                    for(int i = 0; i < parent.getId(); i++) {
                        Start += ItemNum[i];
                    }
                    Intent intent = new Intent(KnowledgeActivity.this,DetailActivity.class);
                    intent.putExtra("contantAddress",data.get(Start + position).contantAddress);
                    intent.putExtra("newsAddress",data.get(Start + position).newsAddress);
                    startActivity(intent);
                }
            });
        }
    }

    private void getGridView() {
        boolean bool = true;
        int num = 0;
        for(int i = 0; i < ItemNum.length; i++) {
            GridView gridView = new GridView(this);
            gridView.setId(i);
            gridView.setNumColumns(3);
            List<Map<String, Object>> gridlist = new ArrayList<Map<String, Object>>();
            for(int j = 0; j < ItemNum[i]; j++) {
                gridlist.add(listView.get(num));
                num++;
            }
            MyAdapter myAdapter = new MyAdapter(gridlist,i,ItemNum[i]);
            gridView.setAdapter(myAdapter);
            gridViewlist.add(gridView);
        }
        adapter = new AdPageAdapter(gridViewlist);
    }

    //初始化图片链接
    private  void  IniImageURL() {
        URLPath = new String[data.size()];
        drawables = new Drawable[data.size()];
        for(int i = 0; i < data.size(); i++) {
            Map<String,Object> temp = new HashMap<String, Object>();
            temp.put("image",R.mipmap.ic_launcher);
            URLPath[i] = data.get(i).picAddress;
            loadImage(URLPath[i],i,data.get(i).sid,data.get(i).pos);
            listView.add(temp);
        }
    }

    //采用Handler+Thread+封装外部接口
    private void loadImage(final String url, final int id, final int sid, final int pos) {
//如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
        Drawable cacheImage = asyncImageLoader.loadDrawable(url,new AsyncImageLoader.ImageCallback() {
            //请参见实现：如果第一次加载url时下面方法会执行
            public void imageLoaded(Drawable imageDrawable) {
                drawables[id] = imageDrawable;
                GridView view = (GridView) gridViewlist.get(sid);
                ((MyAdapter)view.getAdapter()).SetDownLaod(pos);
                ((MyAdapter)view.getAdapter()).notifyDataSetChanged();
            }
        });
        if(cacheImage!=null){
            drawables[id] = cacheImage;
            GridView view = (GridView) gridViewlist.get(sid);
            ((MyAdapter)view.getAdapter()).SetDownLaod(pos);
            ((MyAdapter)view.getAdapter()).notifyDataSetChanged();
        }
    }

    //按钮点击事件
    private void iniButton() {
        KnowLedge = (Button)findViewById(R.id.Knowledge);
        Camera = (Button)findViewById(R.id.Camera);
        Voice = (Button)findViewById(R.id.Voice);
        KnowLedge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(KnowledgeActivity.this,"当前位于知识库",Toast.LENGTH_SHORT).show();
            }
        });

        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(KnowledgeActivity.this,"正在前往拍照界面",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(KnowledgeActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });

        Voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(KnowledgeActivity.this,"正在前往声音界面",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        List<Map<String, Object>> Listgrid;
        Boolean[] DoneLoad;
        int ID;
        //List<Map<String, Drawable>> listgrid;

        private MyAdapter(List<Map<String, Object>> listgrid, int id, int Size) {
            this.Listgrid = listgrid;
            DoneLoad = new Boolean[Size];
            for(int i = 0; i < Size; i++) {
                DoneLoad[i] = false;
            }
            ID = 0;
            for(int i = 0; i < id; i++) {
                ID += ItemNum[i];
            }
        }

        public void SetDownLaod(int pos){
            DoneLoad[pos] = true;
        }

        @Override
        public int getCount() {
            return Listgrid.size();
        }

        @Override
        public Object getItem(int position) {
            return Listgrid.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.grid_view_item, null);
            ImageView getViewLinear = (ImageView) convertView.findViewById(R.id.getViewLinear);
            if(!DoneLoad[position]) {
                getViewLinear.setBackgroundResource(Integer.parseInt(Listgrid.get(position).get("image").toString()));
            }
            else {
                getViewLinear.setImageDrawable(drawables[ID + position]);
            }
            return convertView;
        }
    }
}
