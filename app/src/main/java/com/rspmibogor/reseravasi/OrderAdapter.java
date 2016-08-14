package com.rspmibogor.reseravasi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rspmibogor.reseravasi.Fragment.NewOrderFragment;
import com.rspmibogor.reseravasi.Fragment.OldOrderFragment;

/**
 * Created by iqbalprabu on 14/08/16.
 */
public class OrderAdapter extends FragmentPagerAdapter {

    //nama tab nya
    String[] title = new String[]{
            "Pesanan Baru", "Pesanan Lama"
    };

    public OrderAdapter(FragmentManager fm) {
        super(fm);
    }
    //method ini yang akan memanipulasi penampilan Fragment dilayar
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if(position == 0)
        {
            fragment = new NewOrderFragment();
            return fragment;
        }
        else
        {
            fragment = new OldOrderFragment();
            return fragment;
        }

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
