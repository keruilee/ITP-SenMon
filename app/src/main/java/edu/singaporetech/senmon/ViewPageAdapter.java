package edu.singaporetech.senmon;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Meixi on 24/6/2016.
 */
public class ViewPageAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    public static final String tabTitles[] = new String[] { "TEMPERATURE", "VELOCITY", "DAILY\nANALYSIS" };

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem = null;
    public static String machineId;

    public ViewPageAdapter(FragmentManager fm, Context context, String id) {
        super(fm);
        mFragmentManager = fm;
        machineId = id;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
            return GraphFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    @Override
    public boolean isViewFromObject(View view, Object fragment) {
        return ((Fragment) fragment).getView() == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            throw new IllegalArgumentException("current transaction must not be null");
        }
        String fragmentTag = makeFragmentName(container.getId(), position);
        Fragment fragment = (Fragment) mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment, fragmentTag);
        }

        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            //fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            throw new IllegalArgumentException("current transaction must not be null");
        }
        mCurTransaction.detach((Fragment) object);
        //mCurTransaction.remove((Fragment)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        //super.setPrimaryItem(container, position, object);
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                // this command unexpectedly changes the state of the fragment which leads to a warning message and possible some strange behaviour
                //mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                //fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
        if (mCurTransaction != null) {
            throw new IllegalArgumentException("current transaction must not be null");
        }
        mCurTransaction = mFragmentManager.beginTransaction();
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commit();
            mCurTransaction = null;
            //mFragmentManager.executePendingTransactions();
        } else {
            throw new IllegalArgumentException("current transaction must not be null");
        }
    }

    private String makeFragmentName(int viewId, int position) {
        if (viewId <= 0)
            throw new IllegalArgumentException("viewId " + viewId);
        return "tabpageradptr:" + getPageTitle(position) + ":" + viewId + ":" + position;
    }

}
