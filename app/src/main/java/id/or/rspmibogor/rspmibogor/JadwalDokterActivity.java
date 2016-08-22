package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.or.rspmibogor.rspmibogor.Adapter.DokterAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.OldOrder;

public class JadwalDokterActivity extends AppCompatActivity {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected DokterAdapter mAdapter;

    private List<Dokter> listDokter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_dokter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dokter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listDokter = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleDokter);
        mLayoutManager = new LinearLayoutManager(this);

        initDataset();

        mAdapter = new DokterAdapter(listDokter, this);
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

    private void initDataset() {

        String url = "http://103.43.44.211:1337/v1/dokter?populate=layanan";
        final ProgressDialog loading = ProgressDialog.show(this ,"Loading Data", "Please wait...",false,false);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
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
                });

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);

    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i < array.length(); i++) {

            Dokter dokter = new Dokter();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                //oldOrder.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                JSONObject layanan = json.getJSONObject("layanan");

                dokter.setDokter_name(json.getString("nama"));
                dokter.setDokter_foto(json.getString("foto"));
                dokter.setDokter_id(json.getInt("id"));
                dokter.setLayanan_name(layanan.getString("nama"));
                dokter.setLayanan_id(layanan.getInt("id"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listDokter.add(dokter);
        }
        mAdapter.notifyDataSetChanged();
    }
}
