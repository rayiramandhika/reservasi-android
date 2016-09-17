package id.or.rspmibogor.rspmibogor.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import id.or.rspmibogor.rspmibogor.Fragment.NewOrderFragment;
import id.or.rspmibogor.rspmibogor.Fragment.OldOrderFragment;

/**
 * Created by iqbalprabu on 15/08/16.
 */

public class OrderAdapter extends FragmentPagerAdapter {

    String[] title = new String[]{
            "Pendaftaran Baru", "Pendaftaran Lama"
    };

    public OrderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if(position == 0)
        {
            fragment = new NewOrderFragment();
        }
        else if(position == 1)
        {
            fragment = new OldOrderFragment();
        }
        return fragment;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

}
