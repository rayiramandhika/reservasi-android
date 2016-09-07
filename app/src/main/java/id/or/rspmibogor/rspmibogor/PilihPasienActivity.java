package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

import id.or.rspmibogor.rspmibogor.Adapter.PilihPasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;

public class PilihPasienActivity extends AppCompatActivity {

    private static final String TAG = "PilihPasienActivity";
    private String last_updated;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PilihPasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_pasien);

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

        listPasien = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePilihPasien);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(jwTokenSP == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

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
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(view.getContext(), PasienAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData()
    {
        String url = "http://103.43.44.211:1337/v1/pasien?sort=createdAt%20DESC";
        spinner.setVisibility(View.VISIBLE);
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
        for(int i = 0; i < array.length(); i++) {

            Pasien pasien = new Pasien();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur") + " Tahun");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listPasien.add(pasien);
        }
        mAdapter.notifyDataSetChanged();
    }

}
