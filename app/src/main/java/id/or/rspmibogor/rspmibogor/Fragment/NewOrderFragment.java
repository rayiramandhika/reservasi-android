package id.or.rspmibogor.rspmibogor.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
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
import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.R;


public class NewOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "NewOrderFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected NewOrderAdapter mAdapter;

    private List<NewOrder> listNewOrder;

    private ProgressBar spinner;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    RelativeLayout nodata, errorLayout;
    LinearLayout container;

    FloatingActionButton btnTryAgain;

    SwipeRefreshLayout swipeRefreshLayout;

    private Integer skip = 0;
    private Integer numRows = 0;

    public NewOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listNewOrder = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewRoot =  inflater.inflate(R.layout.fragment_new_order, container, false);

        nodata = (RelativeLayout) viewRoot.findViewById(R.id.nodata);
        container = (LinearLayout) viewRoot.findViewById(R.id.container);
        errorLayout = (RelativeLayout) viewRoot.findViewById(R.id.error);

        spinner = (ProgressBar) viewRoot.findViewById(R.id.progress_bar);

        btnTryAgain = (FloatingActionButton) viewRoot.findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDataset();
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) viewRoot.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));


        mRecyclerView = (RecyclerView) viewRoot.findViewById(R.id.recylceNewOrder);
        mLayoutManager = new LinearLayoutManager(this.getContext());

        initDataset();

        mAdapter = new NewOrderAdapter(listNewOrder, this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setLoadMoreListener(new NewOrderAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(skip < numRows)
                        {
                            int index = listNewOrder.size() - 1;
                            loadMore(index);// a method which requests remote data
                        }else {
                            mAdapter.setMoreDataAvailable(false);
                        }
                    }
                });
            }
        });

        EventBus.getDefault().register(this);

        return viewRoot;
    }

    private void initDataset() {

        String url = "http://103.23.22.46:1337/v1/getorder/new";
        spinner.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        sharedPreferences = getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseData(data);

                            JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinner.setVisibility(View.INVISIBLE);
                        errorLayout.setVisibility(View.VISIBLE);

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getContext());
                            }
                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(req);

    }

    private void parseData(JSONArray array){
        if(array.length() > 0) {
            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                NewOrder newOrder = new NewOrder();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);

                    //Log.d(TAG, "parseData - json" + json);

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
        }else{
            nodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
       // Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());

        String msg = event.getPesan();

        if(msg.equals("cancelOrder"))
        {
            try{

                Integer pos = event.getPosition_list();
                //Log.d(TAG, "onEvent - loaded - event - position_list: " + pos );

                NewOrder newOrder = listNewOrder.get(pos);
                listNewOrder.remove(newOrder);
                mAdapter.notifyItemRemoved(pos);
                mAdapter.notifyItemRangeChanged(pos, listNewOrder.size());

            }catch (EventBusException e){

                listNewOrder.removeAll(listNewOrder);
                initDataset();

            }

        }

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshData();
    }

    private void refreshData() {

        sharedPreferences = getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://103.23.22.46:1337/v1/getorder/new?limit="+skip.toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseRefreshData(data);

                            JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getContext());
                            }
                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(req);

    }

    private void parseRefreshData(JSONArray array){
        if(array.length() > 0) {

            nodata.setVisibility(View.INVISIBLE);
            listNewOrder.removeAll(listNewOrder);

            for (int i = 0; i < array.length(); i++) {

                NewOrder newOrder = new NewOrder();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);

                    //Log.d(TAG, "parseData - json" + json);

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
        }else{
            nodata.setVisibility(View.VISIBLE);
        }
    }

    private void loadMore(Integer index)
    {
        listNewOrder.add(null);
        mAdapter.notifyItemInserted(listNewOrder.size()-1);

        sharedPreferences = getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);


        String url = "http://103.23.22.46:1337/v1/getorder/new?skip="+skip.toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        listNewOrder.remove(listNewOrder.size()-1);
                        //mAdapter.setMoreDataAvailable(false);

                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseLoadMoreData(data);

                            JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        listNewOrder.remove(listNewOrder.size()-1);
                        mAdapter.notifyDataChanged();

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getContext());
                            }
                        }
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

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(req);
    }

    private void parseLoadMoreData(JSONArray array){
        if(array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {

                NewOrder newOrder = new NewOrder();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);

                    //Log.d(TAG, "parseData - json" + json);

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
            //mAdapter.notifyDataSetChanged();
        }

        mAdapter.notifyDataChanged();
    }

}
