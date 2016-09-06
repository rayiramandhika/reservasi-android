package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import id.or.rspmibogor.rspmibogor.Adapter.ListJadwalAdapter;
import id.or.rspmibogor.rspmibogor.Class.DividerItemDecoration;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.ListJadwal;

public class DetailJadwalDokter extends AppCompatActivity {

    private String TAG = "DetailJadwalDokter";
    private Integer dokter_id;

    ImageView dokter_foto;
    TextView dokter_name;
    TextView layanan_name;

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ListJadwalAdapter mAdapter;

    ProgressBar spinner;

    private List<ListJadwal> listJadwalDokter;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jadwal_dokter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dokter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        dokter_id = b.getInt("id");

        dokter_foto = (ImageView) findViewById(R.id.dokter_foto);
        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);

        listJadwalDokter = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerDetailJadwal);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(jwTokenSP == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        initData();

        mAdapter = new ListJadwalAdapter(listJadwalDokter, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData()
    {
        String url = "http://103.43.44.211:1337/v1/getjadwal/" + dokter_id;
        //spinner.setVisibility(View.VISIBLE);
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        //spinner.setVisibility(View.GONE);

                        try {
                            JSONObject data = response.getJSONObject("data");

                            try {

                                JSONObject layanan = data.getJSONObject("layanan");
                                toolbar.setTitle(data.getString("nama"));
                                dokter_name.setText(data.getString("nama"));
                                layanan_name.setText(layanan.getString("nama"));

                                parseData(data);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG, "onResponse - detailjadwal" + data.toString());

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
    private void parseData(JSONObject data) throws JSONException {

        JSONArray array = data.getJSONArray("listJadwal");
        JSONObject layanan = data.getJSONObject("layanan");
        JSONObject jadwalDokter = data.getJSONObject("jadwalDokter");

        for(int i = 0; i < array.length(); i++) {

            ListJadwal listJadwal = new ListJadwal();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                //oldOrder.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                //JSONObject layanan = json.getJSONObject("layanan");

                listJadwal.setDokter_id(data.getInt("id"));
                listJadwal.setDokter_foto(data.getString("foto"));
                listJadwal.setDokter_nama(data.getString("nama"));
                listJadwal.setLayanan_id(layanan.getInt("id"));
                listJadwal.setLayanan_nama(layanan.getString("nama"));

                listJadwal.setJadwal_hari(json.getString("hari"));
                listJadwal.setJadwal_id(json.getInt("id"));
                listJadwal.setJadwal_jamMulai(json.getString("jamMulai"));
                listJadwal.setJadwal_jamTutup(json.getString("jamTutup"));
                listJadwal.setJadwal_tanggal(json.getString("tanggal"));
                listJadwal.setJadwal_kuota(json.getInt("sisaKuota"));




            } catch (JSONException e) {
                e.printStackTrace();
            }
            listJadwalDokter.add(listJadwal);
        }
        mAdapter.notifyDataSetChanged();
    }

}
