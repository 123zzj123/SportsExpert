package cn.kingsleychung.sportsexpert;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by JJ on 2017/8/6.
 */
final class AdPageAdapter extends PagerAdapter {
    private List<View> views = null;

    public AdPageAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(View container, final int position) {
        ((ViewPager) container).addView(views.get(position), 0);

        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}