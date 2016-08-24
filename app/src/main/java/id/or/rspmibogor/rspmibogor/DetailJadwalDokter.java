package id.or.rspmibogor.rspmibogor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailJadwalDokter extends AppCompatActivity {

    private String TAG = "DetailJadwalDokter";
    private Integer dokter_id;

    ImageView dokter_foto;
    TextView dokter_name;
    TextView layanan_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jadwal_dokter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dokter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        dokter_id = b.getInt("id");

        dokter_foto = (ImageView) findViewById(R.id.dokter_foto);
        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);

        initData();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_reservasi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initData()
    {
        String url = "http://103.43.44.211:1337/v1/dokter/" + dokter_id + "?populate=layanan";
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

                                dokter_name.setText(data.getString("nama"));
                                layanan_name.setText(layanan.getString("nama"));


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
                });

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);
    }

}
