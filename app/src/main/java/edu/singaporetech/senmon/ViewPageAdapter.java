package edu.singaporetech.senmon;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Meixi on 24/6/2016.
 */
public class ViewPageAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "GRAPH 1", "GRAPH 2", "GRAPH 3" };
    private Context context;

    public ViewPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return GraphFragment.newInstance(8);
        else if(position == 1)
            return GraphFragment.newInstance(10);
        else
            return GraphFragment.newInstance(12);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
