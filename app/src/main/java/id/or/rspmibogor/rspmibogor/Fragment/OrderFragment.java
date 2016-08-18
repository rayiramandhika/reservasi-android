package id.or.rspmibogor.rspmibogor.Fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.or.rspmibogor.rspmibogor.Adapter.OrderAdapter;
import id.or.rspmibogor.rspmibogor.R;

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
        View rootView =  inflater.inflate(R.layout.fragment_order, container, false);

        //inisialisasi tab dan pager
        pager = (ViewPager) rootView.findViewById(R.id.order_pager);
        tabs = (TabLayout) rootView.findViewById(R.id.order_tabs);

        //set object adapter kedalam ViewPager
        pager.setAdapter(new OrderAdapter(getChildFragmentManager()));

        //Manimpilasi sedikit untuk set TextColor pada Tab
        tabs.setTabTextColors(R.color.colorPrimaryDark, R.color.colorPrimary);

        //set tab ke ViewPager
        tabs.setupWithViewPager(pager);

        //konfigurasi Gravity Fill untuk Tab berada di posisi yang proposional
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_addOrder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}
