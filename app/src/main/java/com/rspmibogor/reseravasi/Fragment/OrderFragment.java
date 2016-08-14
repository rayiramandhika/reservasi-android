package com.rspmibogor.reseravasi.Fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rspmibogor.reseravasi.OrderAdapter;
import com.rspmibogor.reseravasi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private ViewPager pager;
    private TabLayout tabs;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_order, container, false);

        View rootView = inflater.inflate(R.layout.fragment_order,
                container, false);

        //inisialisasi tab dan pager
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        tabs = (TabLayout) rootView.findViewById(R.id.tabs);

        //set object adapter kedalam ViewPager
        pager.setAdapter(new OrderAdapter(getActivity().getSupportFragmentManager()));

        //Manimpilasi sedikit untuk set TextColor pada Tab
        tabs.setTabTextColors(getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary));

        //set tab ke ViewPager
        tabs.setupWithViewPager(pager);

        //konfigurasi Gravity Fill untuk Tab berada di posisi yang proposional
        //tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}
