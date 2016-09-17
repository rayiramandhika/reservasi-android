package id.or.rspmibogor.rspmibogor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.DokterAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.OldOrder;

public class JadwalDokterActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected DokterAdapter mAdapter;

    ProgressBar spinner;

    private List<Dokter> listDokter;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    RelativeLayout nodata;
    LinearLayout container;

    private SwipeRefreshLayout swipeRefreshLayout;

    static JadwalDokterActivity jadwalDokterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_dokter);

        jadwalDokterActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dokter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nodata = (RelativeLayout) findViewById(R.id.nodata);
        container = (LinearLayout) findViewById(R.id.container);

        listDokter = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleDokter);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        initDataset();

        mAdapter = new DokterAdapter(listDokter, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JadwalDokterActivity.this.finish();
            }
        });


        //Init Swipe Refresh Layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public static JadwalDokterActivity getInstance(){
        return jadwalDokterActivity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
        new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                mAdapter.setFilter(listDokter);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true; // Return true to expand action view
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Dokter> filteredModelList = filter(listDokter, newText);
        mAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Dokter> filter(List<Dokter> models, String query) {
        query = query.toLowerCase();

        final List<Dokter> filteredModelList = new ArrayList<>();
        for (Dokter model : models) {
            final String text = model.getDokter_name().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void initDataset() {

        String url = "http://api.rspmibogor.or.id/v1/jadwaldokter?poliklinik_id="+1+"&populate=layanan,dokter,poliklinik";
        //final ProgressDialog loading = ProgressDialog.show(this ,"Loading Data", "Please wait...",false,false);
        spinner.setVisibility(View.VISIBLE);
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(url,
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);

    }

    //This method will parse json data
    private void parseData(JSONArray array){

        if(array.length() > 0) {

            container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                Dokter dokter = new Dokter();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);


                    //oldOrder.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                    JSONObject layanan = json.getJSONObject("layanan");
                    JSONObject dokter_obj = json.getJSONObject("dokter");
                    JSONObject poliklinik = json.getJSONObject("poliklinik");

                    dokter.setDokter_name(dokter_obj.getString("nama"));
                    dokter.setDokter_foto(dokter_obj.getString("foto"));
                    dokter.setDokter_id(dokter_obj.getInt("id"));
                    dokter.setLayanan_name(layanan.getString("nama"));
                    dokter.setLayanan_id(layanan.getInt("id"));
                    dokter.setPoliklinik_name(poliklinik.getString("nama"));
                    dokter.setPoliklinik_id(poliklinik.getInt("id"));
                    dokter.setJadwal_id(json.getInt("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listDokter.add(dokter);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        refreshData();
    }

    private void refreshData() {

        String url = "http://api.rspmibogor.or.id/v1/jadwaldokter?poliklinik_id="+1+"&populate=layanan,dokter,poliklinik";
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parserRefreshData(data);

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);

    }

    //This method will parse json data
    private void parserRefreshData(JSONArray array){

        if(array.length() > 0) {

            listDokter.removeAll(listDokter);

            container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                Dokter dokter = new Dokter();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);


                    //oldOrder.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                    JSONObject layanan = json.getJSONObject("layanan");
                    JSONObject dokter_obj = json.getJSONObject("dokter");
                    JSONObject poliklinik = json.getJSONObject("poliklinik");

                    dokter.setDokter_name(dokter_obj.getString("nama"));
                    dokter.setDokter_foto(dokter_obj.getString("foto"));
                    dokter.setDokter_id(dokter_obj.getInt("id"));
                    dokter.setLayanan_name(layanan.getString("nama"));
                    dokter.setLayanan_id(layanan.getInt("id"));
                    dokter.setPoliklinik_name(poliklinik.getString("nama"));
                    dokter.setPoliklinik_id(poliklinik.getInt("id"));
                    dokter.setJadwal_id(json.getInt("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listDokter.add(dokter);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }
}
