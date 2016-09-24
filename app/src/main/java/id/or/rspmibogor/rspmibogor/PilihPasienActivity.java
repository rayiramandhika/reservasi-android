package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import id.or.rspmibogor.rspmibogor.Adapter.PilihPasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;

public class PilihPasienActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "PilihPasienActivity";
    private String last_updated;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PilihPasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;
    private Integer last_id = 0;

    SharedPreferences sharedPreferences;
    String jwTokenSP;
    Integer user_id;

    RelativeLayout nodata, errorLayout;
    LinearLayout container;

    FloatingActionButton btnTryAgain;

    static PilihPasienActivity pilihPasienActivity;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_pasien);

        pilihPasienActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pilih Pasien");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nodata = (RelativeLayout) findViewById(R.id.nodata);
        container = (LinearLayout) findViewById(R.id.container);
        errorLayout = (RelativeLayout) findViewById(R.id.error);

        btnTryAgain = (FloatingActionButton) findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });


        listPasien = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePilihPasien);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);
        user_id = sharedPreferences.getInt("id", 0);

        initData();

        Bundle b = getIntent().getExtras();
        Integer dokter_id = b.getInt("dokter_id");
        Integer detailjadwal_id = b.getInt("detailjadwal_id");
        Integer layanan_id = b.getInt("layanan_id");
        String tanggal = b.getString("tanggal");
        String hari = b.getString("hari");
        String jam = b.getString("jam");
        String layanan_name = b.getString("layanan_name");
        String dokter_name = b.getString("dokter_name");

        mAdapter = new PilihPasienAdapter(listPasien, this, detailjadwal_id, layanan_id, dokter_id,
                tanggal, hari, jam, layanan_name, dokter_name);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PasienAddActivity.class);
                startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public static PilihPasienActivity getInstance(){
        return pilihPasienActivity;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());

        String msg = event.getPesan().toString();

        if(msg.equals("addPasien"))
        {
            getNewData();
        }
    }

    private void initData()
    {
        String url = "http://103.23.22.46:1337/v1/pasien?sort=id%20DESC";
        spinner.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        Log.d(TAG, "init Data set loaded" );
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseData(data);

                            //Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        spinner.setVisibility(View.INVISIBLE);
                        errorLayout.setVisibility(View.VISIBLE);

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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    //This method will parse json data
    private void parseData(JSONArray array){
        if(array.length() > 0) {

            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);
                    if (i == 0) {
                        last_id = json.getInt("id");
                        Log.d(TAG, "last_id: " + last_id);
                    }


                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listPasien.add(pasien);
            }
            mAdapter.notifyDataSetChanged();
        }else {
            nodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshData();
    }

    private void refreshData()
    {
        String url = "http://103.23.22.46:1337/v1/pasien?sort=id%20DESC";
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {

                        }
                        swipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseRefreshData(data);
                            //Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getBaseContext(), "Gagal memuat data baru", Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    private void parseRefreshData(JSONArray array) {

        if (array.length() > 0) {

            nodata.setVisibility(View.INVISIBLE);

            listPasien.removeAll(listPasien);

            for (int i = 0; i < array.length(); i++) {

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);
                    Integer aLength = array.length();
                    if (i == (aLength - 1)) {
                        last_id = json.getInt("id");
                        //Log.d(TAG, "last_id: " + last_id);
                    }


                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listPasien.add(pasien);
            }
            mAdapter.notifyDataSetChanged();
        }else {
            nodata.setVisibility(View.VISIBLE);
        }
    }

    private void getNewData()
    {
        String url = "http://103.23.22.46:1337/v1/pasien?where={%22id%22:{%22>%22:"+last_id+"},%22user%22:"+user_id+"}";
        spinner.setVisibility(View.VISIBLE);
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {

                        }
                        spinner.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataNew(data);
                            //Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        spinner.setVisibility(View.INVISIBLE);
                        Toast.makeText(getBaseContext(), "Gagal memuat data baru", Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    private void parseDataNew(JSONArray array) {

        if (array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);
                    Integer aLength = array.length();
                    if (i == (aLength - 1)) {
                        last_id = json.getInt("id");
                        //Log.d(TAG, "last_id: " + last_id);
                    }


                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listPasien.size() > 0) {
                    listPasien.add(0, pasien);
                } else {
                    listPasien.add(pasien);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
