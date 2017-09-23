package cn.kingsleychung.sportsexpert;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Boolean[][] IsDoneLoad = new Boolean[12][50];
    private Button KnowLedge;
    private Button Camera;
    private Button Voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        initIndicator();
        initView();
        iniButton();
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
        listViews = new ArrayList<View>();
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
        //listView = new ArrayList<Map<String, Drawable>>();
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
        getView();
        // 将GridView添加到ViewPager显示
        adViewPager.setAdapter(adapter);
        //adViewPager.setOnPageChangeListener(new AdPageChangeListener());

        for (int i = 0; i < gridViewlist.size(); i++) {
            GridView view = (GridView) gridViewlist.get(i);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
                    //Toast.makeText(getApplicationContext(), item.get("image").toString(), 0).show();
                    Toast.makeText(KnowledgeActivity.this,"hello"+ view.getId(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(KnowledgeActivity.this,DetailActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void getView() {
        int[] intView = new int[600];
        for (int i = 0; i < intView.length; i++) {
            intView[i] = R.mipmap.ic_launcher;
            Map<String, Object> mapView = new HashMap<String, Object>();
            mapView.put("image", intView[i]);
            listView.add(mapView);
        }
        getGridView();
    }

    private void getGridView() {
        boolean bool = true;
        while (bool) {
            //int result = next + 10;
            int result = next + 50;
            if (listView.size() != 0 && result <= listView.size()) {
                GridView gridView = new GridView(this);
                gridView.setId(result/50 - 1);
                gridView.setNumColumns(3);
                List<Map<String, Object>> gridlist = new ArrayList<Map<String, Object>>();
                //List<Map<String, Drawable>> gridlist = new ArrayList<Map<String, Drawable>>();
                for (int i = next; i < result; i++) {
                    gridlist.add(listView.get(i));
                }
                MyAdapter myAdapter = new MyAdapter(gridlist,IsDoneLoad[result/50 - 1],result/50-1);
                gridView.setAdapter(myAdapter);
                next = result;
                gridViewlist.add(gridView);

            } else if (result - listView.size() < 50) {
                List<Map<String, Object>> gridlist = new ArrayList<Map<String, Object>>();
                //List<Map<String, Drawable>> gridlist = new ArrayList<Map<String, Drawable>>();
                for (int i = next; i < listView.size(); i++) {
                    gridlist.add(listView.get(i));
                }
                GridView gridView = new GridView(this);
                gridView.setNumColumns(3);
                MyAdapter myAdapter = new MyAdapter(gridlist,IsDoneLoad[result/50 - 1], result/50-1);
                gridView.setAdapter(myAdapter);
                next = listView.size() - 1;
                gridViewlist.add(gridView);
                bool = false;
            } else {
                bool = false;
            }
        }
        adapter = new AdPageAdapter(gridViewlist);

    }

    //初始化图片链接
    private  void  IniImageURL() {
        URLPath = new String[600];
        drawables = new Drawable[600];
        for(int i = 0; i < 600; i++) {
            URLPath[i] = "http://pic39.nipic.com/20140226/18071023_164300608000_2.jpg";
            IsDoneLoad[i/50][i%50] = false;
            loadImage(URLPath[i],i);
        }
    }

    //采用Handler+Thread+封装外部接口
    private void loadImage(final String url, final int id) {
//如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
        Drawable cacheImage = asyncImageLoader.loadDrawable(url,new AsyncImageLoader.ImageCallback() {
            //请参见实现：如果第一次加载url时下面方法会执行
            public void imageLoaded(Drawable imageDrawable) {
                drawables[id] = imageDrawable;
                IsDoneLoad[id/50][id%50] = true;
                GridView view = (GridView) gridViewlist.get(id/50);
                ((MyAdapter)view.getAdapter()).SetDownLaod(IsDoneLoad[id/50]);
                ((MyAdapter)view.getAdapter()).notifyDataSetChanged();
            }
        });
        if(cacheImage!=null){
            drawables[id] = cacheImage;
            IsDoneLoad[id/50][id%50] = true;
            GridView view = (GridView) gridViewlist.get(id/50);
            ((MyAdapter)view.getAdapter()).SetDownLaod(IsDoneLoad[id/50]);
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
        Boolean[] DoneLoad = new Boolean[50];
        int ID;
        //List<Map<String, Drawable>> listgrid;

        private MyAdapter(List<Map<String, Object>> listgrid, Boolean[] doneLoad, int id) {
            this.Listgrid = listgrid;
            DoneLoad = doneLoad;
            ID = id;
        }

        public void SetDownLaod(Boolean[] doneLoad){
            DoneLoad = doneLoad;
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
                getViewLinear.setImageDrawable(drawables[ID * 50 + position]);
            }
            return convertView;
        }
    }
}
