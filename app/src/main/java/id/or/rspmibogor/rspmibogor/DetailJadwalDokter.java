package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.ListJadwalAdapter;
import id.or.rspmibogor.rspmibogor.Class.DividerItemDecoration;
import id.or.rspmibogor.rspmibogor.GetterSetter.ListJadwal;
import id.or.rspmibogor.rspmibogor.Models.User;

public class DetailJadwalDokter extends AppCompatActivity {

    private String TAG = "DetailJadwalDokter";
    private Integer dokter_id;
    private Integer jadwal_id;

    ImageView dokter_foto;
    TextView dokter_name;
    TextView layanan_name;

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ListJadwalAdapter mAdapter;

    ProgressBar spinner;
    LinearLayout containerData;
    RelativeLayout nodata, errorLayout, container;

    FloatingActionButton btnTryAgain;

    private List<ListJadwal> listJadwalDokter;

    SharedPreferences sharedPreferences;

    Toolbar toolbar;

    static DetailJadwalDokter detailJadwalDokter;

    private Integer refreshToken = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jadwal_dokter);

        detailJadwalDokter = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Jadwal Dokter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (RelativeLayout) findViewById(R.id.container);
        containerData = (LinearLayout) findViewById(R.id.containerData);
        nodata = (RelativeLayout) findViewById(R.id.nodata);
        errorLayout = (RelativeLayout) findViewById(R.id.error);

        btnTryAgain = (FloatingActionButton) findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });

        Bundle b = getIntent().getExtras();
        dokter_id = b.getInt("id");
        jadwal_id = b.getInt("jadwal_id");

        dokter_foto = (ImageView) findViewById(R.id.dokter_foto);
        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);

        listJadwalDokter = new ArrayList<>();
        listJadwalDokter.removeAll(listJadwalDokter);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerDetailJadwal);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        initData();

        mAdapter = new ListJadwalAdapter(listJadwalDokter, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public static DetailJadwalDokter getInstance(){
        return detailJadwalDokter;
    }

    private void initData()
    {
        //Log.d(TAG, "init data set");

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://103.23.22.46:1337/v1/getjadwal/" + dokter_id + "?jadwal_id=" + jadwal_id;

        spinner.setVisibility(View.VISIBLE);
        container.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        spinner.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);

                        try {
                            JSONObject data = response.getJSONObject("data");

                            try {

                                JSONObject layanan = data.getJSONObject("layanan");
                                JSONObject poliklinik = data.getJSONObject("poliklinik");
                                dokter_name.setText(data.getString("nama"));
                                layanan_name.setText("Klinik " + layanan.getString("nama"));

                                String uriFoto = data.getString("foto");
                                initFoto(uriFoto);
                                parseData(data, layanan, poliklinik);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                          // Log.d(TAG, "onResponse - detailjadwal" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        };
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

                        spinner.setVisibility(View.GONE);
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

    private void parseData(JSONObject data, JSONObject layanan, JSONObject poliklinik) {

        JSONArray array = new JSONArray();

        try {
            array = data.getJSONArray("listJadwal");
        } catch (JSONException e) {
            e.printStackTrace();
            containerData.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }

        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {

                ListJadwal listJadwal = new ListJadwal();
                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);

                    listJadwal.setDokter_id(data.getInt("id"));
                    listJadwal.setDokter_foto(data.getString("foto"));
                    listJadwal.setDokter_nama(data.getString("nama"));
                    listJadwal.setLayanan_id(layanan.getInt("id"));
                    listJadwal.setLayanan_nama(layanan.getString("nama"));
                    listJadwal.setPoliklinik_id(poliklinik.getInt("id"));
                    listJadwal.setPoliklinik_nama(poliklinik.getString("nama"));

                    listJadwal.setJadwal_hari(json.getString("hari"));
                    listJadwal.setJadwal_id(json.getInt("id"));
                    listJadwal.setJadwal_jamMulai(json.getString("jamMulai"));
                    listJadwal.setJadwal_jamTutup(json.getString("jamTutup"));
                    listJadwal.setJadwal_tanggal(json.getString("tanggal"));
                    listJadwal.setJadwal_kuota(json.getInt("kuota"));
                    listJadwal.setJadwal_sisaKuota(json.getInt("sisaKuota"));
                    listJadwal.setJadwal_status(json.getString("status"));
                    listJadwal.setKeterangan(json.getString("keterangan"));
                    listJadwal.setTimeOff(0);
                    if(json.getString("status").equals("cuti"))
                    {
                        listJadwal.setKeteranganCuti(json.getString("keteranganCuti"));
                    }else {
                        listJadwal.setKeteranganCuti("");
                    }


                } catch (JSONException e) {
                    //Log.d(TAG, "ListJadwal JSONException: " + e.toString());
                    e.printStackTrace();
                }
                listJadwalDokter.add(listJadwal);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initFoto(String uriFoto)
    {

        //Log.d(TAG, "uriFoto: " + uriFoto);
        if(uriFoto.isEmpty())
        {
            dokter_foto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.noprofile));
        }else {

            Glide.with(this)
                    .load("http://103.23.22.46:1337/v1/dokter/foto/" + uriFoto)
                    .centerCrop()
                    .crossFade()
                    .override(150, 150)
                    .fitCenter()
                    .error(R.drawable.noprofile)
                    .into(dokter_foto);
        }
    }

}
