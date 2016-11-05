package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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

import id.or.rspmibogor.rspmibogor.Adapter.InboxAdapter;
import id.or.rspmibogor.rspmibogor.Adapter.PasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;
import id.or.rspmibogor.rspmibogor.Models.User;

public class PasienActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PasienActivity";
    private ProgressDialog progressDialog;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;
    private Integer last_id = 0;

    SharedPreferences sharedPreferences;
    Integer user_id;

    RelativeLayout nodata, errorLayout;
    LinearLayout container;

    FloatingActionButton btnTryAgain;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Integer skip = 0;
    private Integer numRows = 0;

    ArrayList<String> listAsuransi;
    ArrayList<String> listAsuransiId;


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
        errorLayout = (RelativeLayout) findViewById(R.id.error);

        btnTryAgain = (FloatingActionButton) findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });

        listPasien = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePasien);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getInt("id", 0);

        initData();

        mAdapter = new PasienAdapter(listPasien, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        listAsuransi = new ArrayList<String>();
        listAsuransiId = new ArrayList<String>();

        /*mAdapter.setLoadMoreListener(new PasienAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(skip < numRows)
                        {
                            int index = listPasien.size() - 1;
                            loadMore(index);// a method which requests remote data
                        }else {
                            mAdapter.setMoreDataAvailable(false);
                        }
                    }
                });
            }
        });*/

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
                addPasien();
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
        //Log.d(TAG, "onEvent - loaded - event: " + event.getPesan());

        String msg = event.getPesan();

        if(msg.equals("addPasien"))
        {
            refreshData();

        }else if(msg.equals("editPasien"))
        {
            listPasien.removeAll(listPasien);
            initData();
        }
    }

    private void initData()
    {
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://103.23.22.46:1337/v1/pasien/all";



        spinner.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseData(data);

                            /*JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }
                        }

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


    private void parseData(JSONArray array){

        if(array.length() > 0) {

            //container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);
                    if (i == 0) {
                        last_id = json.getInt("id");
                        //Log.d(TAG, "last_id: " + last_id);
                    }

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
            //container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }

    private void getNewData()
    {
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://103.23.22.46:1337/v1/pasien?where={%22id%22:{%22>%22:"+last_id+"},%22user%22:"+user_id+"}";
        spinner.setVisibility(View.VISIBLE);

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        spinner.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataNew(data);
                            //Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }
                        }

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

    private void parseDataNew(JSONArray array){

        if(array.length() > 0) {

            //container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);
            skip = skip+1;

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
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshData();
    }

    private void refreshData()
    {
        String url = "http://103.23.22.46:1337/v1/pasien/all";
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        swipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataRefresh(data);

                            /*JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");*/

                           // Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }
                        }

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

    private void parseDataRefresh(JSONArray array){

        if(array.length() > 0) {

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
                    //pasien.setPasien_namaPenjamin(json.getString("namaPenjamin"));
                    pasien.setPasien_type(json.getString("type"));

                    String jenisPembayaran = json.getString("jenisPembayaran");
                    if(jenisPembayaran.equals("Asuransi"))
                    {
                        JSONObject asuransi = json.getJSONObject("asuransi");
                        pasien.setPasien_namaPenjamin(asuransi.getString("nama"));
                        Integer asuransi_id = asuransi.getInt("id");
                        pasien.setAsuransi_id(asuransi_id.toString());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listPasien.add(pasien);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            nodata.setVisibility(View.VISIBLE);
        }
    }

    private void addPasien()
    {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang memuat data...");
        progressDialog.show();

        listAsuransi.removeAll(listAsuransi);
        listAsuransiId.removeAll(listAsuransiId);

        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/asuransi";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataAsuransi(data);
                        } catch (JSONException e) {
                            Toast.makeText(PasienActivity.this, "Pasien Gagal mengambil data, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Get Asuransi - Error get JSON Array: " + e.toString());
                        }

                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, "Get Asuransi - Error VolleyError: " + error.toString());
                        progressDialog.hide();
                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, PasienActivity.this);
                            }
                        }

                        Toast.makeText(PasienActivity.this, "Pasien Gagal mengambil data, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                        //Log.d("deleteFromServer - Error.Response", String.valueOf(error));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + jwTokenSP);
                return params;
            }
        };

        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

    private void parseDataAsuransi(JSONArray data) {

        listAsuransi.add(0, "");
        listAsuransiId.add(0, "");

        if(data.length() > 0)
        {
            for (int i = 0; i < data.length(); i++) {

                JSONObject json = null;
                try {

                    json = data.getJSONObject(i);
                    Integer idx = json.getInt("id");
                    String nama = json.getString("nama");

                    listAsuransi.add(nama);
                    listAsuransiId.add(idx.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //Log.d(TAG, "listAsuransi: " + listAsuransi.toString());

        final Bundle b = new Bundle();

        b.putStringArrayList("asuransi", listAsuransi);
        b.putStringArrayList("idAsuransi", listAsuransiId);

        Intent intent = new Intent(PasienActivity.this, PasienAddActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    /*private void loadMore(Integer index)
    {

        listPasien.add(null);
        mAdapter.notifyItemInserted(listPasien.size()-1);

        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        String url = "http://103.23.22.46:1337/v1/pasien?sort=id%20DESC&skip="+skip.toString();


        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                        listPasien.remove(listPasien.size()-1);

                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseLoadMore(data);

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

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }
                        }

                        error.printStackTrace();

                        listPasien.remove(listPasien.size()-1);
                        mAdapter.notifyDataChanged();
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


    private void parseLoadMore(JSONArray array){

        if(array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {

                Pasien pasien = new Pasien();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);

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
        }

        mAdapter.notifyDataChanged();
    }*/
}
