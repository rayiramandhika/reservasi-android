package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
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

import id.or.rspmibogor.rspmibogor.Adapter.PilihPasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;
import id.or.rspmibogor.rspmibogor.Models.User;

public class PilihPasienActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "PilihPasienActivity";
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PilihPasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;
    private Integer last_id = 0;

    SharedPreferences sharedPreferences;
    Integer user_id;

    RelativeLayout nodata, errorLayout;
    LinearLayout container;

    FloatingActionButton btnTryAgain;

    static PilihPasienActivity pilihPasienActivity;

    private SwipeRefreshLayout swipeRefreshLayout;

    Integer dokter_id;
    Integer detailjadwal_id;
    Integer layanan_id;
    Integer poliklinik_id;
    String tanggal;
    String hari;
    String jam;
    String layanan_name = "";
    String poliklinik_name = "";
    String dokter_name;

    private Integer refreshToken = 0;

    ArrayList<String> listAsuransi;
    ArrayList<String> listAsuransiId;

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

        listAsuransi = new ArrayList<String>();
        listAsuransiId = new ArrayList<String>();

        listPasien = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePilihPasien);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getInt("id", 0);

        Bundle b = getIntent().getExtras();
        dokter_id = b.getInt("dokter_id");
        detailjadwal_id = b.getInt("detailjadwal_id");
        layanan_id = b.getInt("layanan_id");
        poliklinik_id = b.getInt("poliklinik_id");
        tanggal = b.getString("tanggal");
        hari = b.getString("hari");
        jam = b.getString("jam");
        layanan_name = b.getString("layanan_name");
        poliklinik_name = b.getString("poliklinik_name");
        dokter_name = b.getString("dokter_name");

        initData();

        mAdapter = new PilihPasienAdapter(listPasien, this, detailjadwal_id, layanan_id, dokter_id,
                tanggal, hari, jam, layanan_name, dokter_name, poliklinik_id, poliklinik_name);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPasien();
            }
        });

        EventBus.getDefault().register(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_info)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(PilihPasienActivity.this);
            builder.setTitle("Informasi")
                    .setCancelable(false)
                    .setMessage("Daftar Pasien yang tersedia dihalaman ini telah di filter berdasarkan kriteria Jadwal Dokter yang Anda pilih.")
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
        }

        return true;
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
        //Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());

        String msg = event.getPesan().toString();

        if(msg.equals("addPasien"))
        {
            refreshData();
        }
    }

    private void initData()
    {

        String url = null;

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(layanan_name.equals("Kebidanan & Kandungan"))
        {
            url = "http://103.23.22.46:1337/v1/pasien/women?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }else if(layanan_name.equals("Bedah Anak")){
            url = "http://103.23.22.46:1337/v1/pasien/child?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }else if(layanan_name.equals("Anak")){
            url = "http://103.23.22.46:1337/v1/pasien/child?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }else{
            url = "http://103.23.22.46:1337/v1/pasien/all?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }

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

                            //Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError)
                        {
                            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                //Log.d(TAG, "OS: " + Build.VERSION_CODES.JELLY_BEAN_MR2);
                                if(refreshToken <= 5)
                                {
                                    if(jwTokenSP != null){
                                        User user = new User();
                                        user.refreshToken(jwTokenSP, getBaseContext());
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
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
        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
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
                        //Log.d(TAG, "last_id: " + last_id);
                    }


                    pasien.setPasien_name(json.getString("nama"));
                    pasien.setPasien_id(json.getInt("id"));
                    pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");
                    pasien.setPasien_jenisKelamin(json.getString("jenisKelamin"));


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
        String url = null;

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(layanan_name.equals("Kebidanan & Kandungan"))
        {
            url = "http://103.23.22.46:1337/v1/pasien/women?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }else if(layanan_name.equals("Bedah Anak")){
            url = "http://103.23.22.46:1337/v1/pasien/child?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }else if(layanan_name.equals("Anak")){
            url = "http://103.23.22.46:1337/v1/pasien/child?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }else{
            url = "http://103.23.22.46:1337/v1/pasien/all?detailjadwal_id="+detailjadwal_id+"&dokter_id="+dokter_id+"&layanan_id="+layanan_id+"&tanggal="+tanggal;
        }


        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        swipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseRefreshData(data);
                            //Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError)
                        {
                            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                //Log.d(TAG, "OS: " + Build.VERSION_CODES.JELLY_BEAN_MR2);
                                if(refreshToken <= 5)
                                {
                                    if(jwTokenSP != null){
                                        User user = new User();
                                        user.refreshToken(jwTokenSP, getBaseContext());
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
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
        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
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
                    pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");
                    pasien.setPasien_jenisKelamin(json.getString("jenisKelamin"));


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

        String url = null;

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(layanan_name.equals("Kebidanan & Kandungan"))
        {
            url = "http://103.23.22.46:1337/v1/pasien/women";
        }else if(layanan_name.equals("Bedah Anak")){
            url = "http://103.23.22.46:1337/v1/pasien/child";
        }else if(layanan_name.equals("Anak")){
            url = "http://103.23.22.46:1337/v1/pasien/child";
        }else{
            url = "http://103.23.22.46:1337/v1/pasien/all";
        }

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
                        ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof NoConnectionError)
                        {
                            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                //Log.d(TAG, "OS: " + Build.VERSION_CODES.JELLY_BEAN_MR2);
                                if(refreshToken <= 5)
                                {
                                    if(jwTokenSP != null){
                                        User user = new User();
                                        user.refreshToken(jwTokenSP, getBaseContext());
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
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
        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        requestQueue.add(req);
    }

    private void parseDataNew(JSONArray array) {

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
                    pasien.setPasien_noRekamMedik(json.getString("noRekamMedik"));
                    pasien.setPasien_umur(json.getString("umur") + " Tahun");
                    pasien.setPasien_jenisKelamin(json.getString("jenisKelamin"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listPasien.add(pasien);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addPasien()
    {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang memuat data...");
        progressDialog.show();

        listAsuransi.removeAll(listAsuransi);
        listAsuransiId.removeAll(listAsuransiId);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/asuransi";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataAsuransi(data);
                        } catch (JSONException e) {
                            Toast.makeText(PilihPasienActivity.this, "Gagal memuat data, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Get Asuransi - Error get JSON Array: " + e.toString());
                        }

                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, "Get Asuransi - Error VolleyError: " + error.toString());
                        progressDialog.dismiss();

                        if(error instanceof NoConnectionError)
                        {
                            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                //Log.d(TAG, "OS: " + Build.VERSION_CODES.JELLY_BEAN_MR2);
                                if(refreshToken <= 5)
                                {
                                    if(jwTokenSP != null){
                                        User user = new User();
                                        user.refreshToken(jwTokenSP, getBaseContext());
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, PilihPasienActivity.this);
                            }
                        }

                        Toast.makeText(PilihPasienActivity.this, "Gagal memuat data, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(PilihPasienActivity.this, PasienAddActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

}
