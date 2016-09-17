package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.PasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;

public class PasienActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PasienActivity";
    private String last_updated;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;
    private Integer last_id = 0;

    SharedPreferences sharedPreferences;
    String jwTokenSP;
    Integer user_id;

    RelativeLayout nodata;
    LinearLayout container;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien);

        EventBus myEventBus = EventBus.getDefault();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pasien");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nodata = (RelativeLayout) findViewById(R.id.nodata);
        container = (LinearLayout) findViewById(R.id.container);

        listPasien = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePasien);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);
        user_id = sharedPreferences.getInt("id", 0);

        initData();

        mAdapter = new PasienAdapter(listPasien, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasienActivity.this.finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PasienAddActivity.class);
                startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);

        //Init Swipe Refresh Layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary));

    }

    /*@Override
    public void onStart() {
        super.onStart();

    }*/

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        Log.d(TAG, "onEvent - loaded - event: " + event.getPesan());

        String msg = event.getPesan();

        if(msg.equals("addPasien"))
        {
            getNewData();

        }else if(msg.equals("editPasien"))
        {
            listPasien.removeAll(listPasien);
            initData();
        }
    }

    private void initData()
    {
        String url = "http://api.rspmibogor.or.id/v1/pasien?sort=id%20DESC";
        //final ProgressDialog loading = ProgressDialog.show(this ,"Loading Data", "Please wait...",false,false);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
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
                        error.printStackTrace();
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

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);
                    if (i == 0) {
                        last_id = json.getInt("id");
                        Log.d(TAG, "last_id: " + last_id);
                    }


                /*pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur") + " Tahun");*/

                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_noID(json.getString("noID"));
                    pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_tempatLahir(json.getString("tempatLahir"));
                    pasien.setPasien_tanggalLahir(json.getString("tanggalLahir"));
                    pasien.setPasien_jenisKelamin(json.getString("jenisKelamin"));
                    pasien.setPasien_umur(json.getString("umur")  + " Tahun");
                    pasien.setPasien_wargaNegara(json.getString("wargaNegara"));
                    pasien.setPasien_noTelp(json.getString("noTelp"));
                    pasien.setPasien_agama(json.getString("agama"));
                    pasien.setPasien_pendidikan(json.getString("pendidikan"));
                    pasien.setPasien_pekerjaan(json.getString("pekerjaan"));
                    pasien.setPasien_golonganDarah(json.getString("golonganDarah"));

                    pasien.setPasien_statusMarital(json.getString("statusMarital"));
                    pasien.setPasien_namaPasutri(json.getString("namaPasutri"));
                    pasien.setPasien_namaAyah(json.getString("namaAyah"));
                    pasien.setPasien_namaIbu(json.getString("namaIbu"));

                    pasien.setPasien_alamat(json.getString("alamat"));
                    pasien.setPasien_provinsi(json.getString("provinsi"));
                    pasien.setPasien_kota(json.getString("kota"));
                    pasien.setPasien_kecamatan(json.getString("kecamatan"));
                    pasien.setPasien_desa(json.getString("desa"));

                    pasien.setPasien_jenisPembayaran(json.getString("jenisPembayaran"));
                    pasien.setPasien_namaPenjamin(json.getString("namaPenjamin"));
                    pasien.setPasien_type(json.getString("type"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listPasien.add(pasien);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }

    private void getNewData()
    {
        String url = "http://api.rspmibogor.or.id/v1/pasien?where={%22id%22:{%22>%22:"+last_id+"},%22user%22:"+user_id+"}";
        Log.d(TAG, "url: " + url);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {

                        }
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataNew(data);
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
                        error.printStackTrace();
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

    private void parseDataNew(JSONArray array){

        if(array.length() > 0) {

            container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);
            for (int i = 0; i < array.length(); i++) {

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);
                    Integer aLength = array.length();
                    if (i == (aLength - 1)) {
                        last_id = json.getInt("id");
                        Log.d(TAG, "last_id: " + last_id);
                    }


                /*pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur"));
                pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));*/

                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_noID(json.getString("noID"));
                    pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_tempatLahir(json.getString("tempatLahir"));
                    pasien.setPasien_tanggalLahir(json.getString("tanggalLahir"));
                    pasien.setPasien_jenisKelamin(json.getString("jenisKelamin"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");
                    pasien.setPasien_wargaNegara(json.getString("wargaNegara"));
                    pasien.setPasien_noTelp(json.getString("noTelp"));
                    pasien.setPasien_agama(json.getString("agama"));
                    pasien.setPasien_pendidikan(json.getString("pendidikan"));
                    pasien.setPasien_pekerjaan(json.getString("pekerjaan"));
                    pasien.setPasien_golonganDarah(json.getString("golonganDarah"));

                    pasien.setPasien_statusMarital(json.getString("statusMarital"));
                    pasien.setPasien_namaPasutri(json.getString("namaPasutri"));
                    pasien.setPasien_namaAyah(json.getString("namaAyah"));
                    pasien.setPasien_namaIbu(json.getString("namaIbu"));

                    pasien.setPasien_alamat(json.getString("alamat"));
                    pasien.setPasien_provinsi(json.getString("provinsi"));
                    pasien.setPasien_kota(json.getString("kota"));
                    pasien.setPasien_kecamatan(json.getString("kecamatan"));
                    pasien.setPasien_desa(json.getString("desa"));

                    pasien.setPasien_jenisPembayaran(json.getString("jenisPembayaran"));
                    pasien.setPasien_namaPenjamin(json.getString("namaPenjamin"));
                    pasien.setPasien_type(json.getString("type"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listPasien.add(0, pasien);
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

    private void refreshData()
    {
        String url = "http://api.rspmibogor.or.id/v1/pasien";

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {

                        }
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataRefresh(data);
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
                        error.printStackTrace();
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

    private void parseDataRefresh(JSONArray array){

        if(array.length() > 0) {

            container.setVisibility(View.VISIBLE);
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
                        Log.d(TAG, "last_id: " + last_id);
                    }


                /*pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur"));
                pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));*/

                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_noID(json.getString("noID"));
                    pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_tempatLahir(json.getString("tempatLahir"));
                    pasien.setPasien_tanggalLahir(json.getString("tanggalLahir"));
                    pasien.setPasien_jenisKelamin(json.getString("jenisKelamin"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");
                    pasien.setPasien_wargaNegara(json.getString("wargaNegara"));
                    pasien.setPasien_noTelp(json.getString("noTelp"));
                    pasien.setPasien_agama(json.getString("agama"));
                    pasien.setPasien_pendidikan(json.getString("pendidikan"));
                    pasien.setPasien_pekerjaan(json.getString("pekerjaan"));
                    pasien.setPasien_golonganDarah(json.getString("golonganDarah"));

                    pasien.setPasien_statusMarital(json.getString("statusMarital"));
                    pasien.setPasien_namaPasutri(json.getString("namaPasutri"));
                    pasien.setPasien_namaAyah(json.getString("namaAyah"));
                    pasien.setPasien_namaIbu(json.getString("namaIbu"));

                    pasien.setPasien_alamat(json.getString("alamat"));
                    pasien.setPasien_provinsi(json.getString("provinsi"));
                    pasien.setPasien_kota(json.getString("kota"));
                    pasien.setPasien_kecamatan(json.getString("kecamatan"));
                    pasien.setPasien_desa(json.getString("desa"));

                    pasien.setPasien_jenisPembayaran(json.getString("jenisPembayaran"));
                    pasien.setPasien_namaPenjamin(json.getString("namaPenjamin"));
                    pasien.setPasien_type(json.getString("type"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listPasien.add(0, pasien);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }
}
