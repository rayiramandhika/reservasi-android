package id.or.rspmibogor.rspmibogor.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.NewOrderAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.LoginActivity;
import id.or.rspmibogor.rspmibogor.R;


public class NewOrderFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected NewOrderAdapter mAdapter;

    private List<NewOrder> listNewOrder;

    private ProgressBar spinner;


    SharedPreferences sharedPreferences;
    String jwTokenSP;


    public NewOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listNewOrder = new ArrayList<>();

        sharedPreferences = this.getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(jwTokenSP == null){
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d(TAG, "onCreateView - loaded");
        View viewRoot =  inflater.inflate(R.layout.fragment_old_order, container, false);
        spinner = (ProgressBar) viewRoot.findViewById(R.id.progress_bar);


        mRecyclerView = (RecyclerView) viewRoot.findViewById(R.id.recycleOldOrder);
        mLayoutManager = new LinearLayoutManager(this.getContext());

        initDataset();

        mAdapter = new NewOrderAdapter(listNewOrder, this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        EventBus.getDefault().register(this);

        return viewRoot;
    }

    private void initDataset() {

        String url = "http://103.43.44.211:1337/v1/getorder/new";
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseData(data);

                            Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + jwTokenSP);
                return params;
            }
        };

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());

        //Adding request to the queue
        requestQueue.add(req);

    }



    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i < array.length(); i++) {

            NewOrder newOrder = new NewOrder();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                Log.d(TAG, "parseData - json" + json);

                //oldOrder.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                JSONObject pasien = json.getJSONObject("pasien");
                JSONObject detailjadwal = json.getJSONObject("detailjadwal");
                JSONObject user = json.getJSONObject("user");
                JSONObject dokter = json.getJSONObject("dokter");
                JSONObject layanan = json.getJSONObject("layanan");

                newOrder.setPasien_name(pasien.getString("nama"));
                newOrder.setPasien_norekammedik(pasien.getString("noRekamMedik"));
                newOrder.setDetailjadwal_hari(detailjadwal.getString("hari"));
                newOrder.setDetailjadwal_jammulai(detailjadwal.getString("jamMulai"));
                newOrder.setDetailjadwal_jamtutup(detailjadwal.getString("jamTutup"));
                newOrder.setUser_id(user.getInt("id"));
                newOrder.setUser_name(user.getString("nama"));
                newOrder.setOrder_id(json.getInt("id"));
                newOrder.setOrder_noUrut(json.getInt("noUrut"));
                newOrder.setDokter_id(dokter.getInt("id"));
                newOrder.setDokter_name(dokter.getString("nama"));
                newOrder.setLayanan_id(layanan.getInt("id"));
                newOrder.setLayanan_name(layanan.getString("nama"));
                newOrder.setOrder_tanggal(json.getString("tanggal"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listNewOrder.add(newOrder);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());

        String msg = event.getPesan();

        if(msg.equals("cancelOrder"))
        {
            Integer pos = event.getPosition_list();
            Log.d(TAG, "onEvent - loaded - event - position_list: " + pos );
            //mAdapter.notifyDataSetChanged();

            NewOrder newOrder = listNewOrder.get(pos);
            listNewOrder.remove(newOrder);
            mAdapter.notifyItemRemoved(pos);
            mAdapter.notifyItemRangeChanged(pos, listNewOrder.size());
        }

    }

}
