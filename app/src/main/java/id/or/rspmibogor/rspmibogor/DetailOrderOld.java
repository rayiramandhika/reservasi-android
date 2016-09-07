package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailOrderOld extends AppCompatActivity {
    private static final String TAG = "DetailOrderOld";

    Toolbar toolbar;

    Integer Rating = 3;

    ImageView ratingBoo;
    ImageView ratingArgh;
    ImageView ratingOk;
    ImageView ratingAha;
    ImageView ratingWow;
    ImageView rating;

    ImageView btnSend;

    TextView no_antrian;
    TextView dokter_name;
    TextView layanan_name;
    TextView tanggal;
    TextView jam;
    TextView pasien_name;
    TextView saranText;

    EditText saranEditTxt;

    LinearLayout ratingLayout;
    LinearLayout saranFormLayout;
    LinearLayout saranTextLayout;


    ProgressBar spinner;
    LinearLayout container;

    SharedPreferences sharedPreferences;
    String jwTokenSP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_old);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Pendaftaran");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //init
        btnSend = (ImageView) findViewById(R.id.btnSend);

        ratingLayout = (LinearLayout) findViewById(R.id.ratingLayout);
        saranFormLayout = (LinearLayout) findViewById(R.id.saranFormlayout);
        saranTextLayout = (LinearLayout) findViewById(R.id.saranTextLayout);
        saranText = (TextView) findViewById(R.id.txtSaran);

        ratingBoo = (ImageView) findViewById(R.id.ratingBoo);
        ratingArgh = (ImageView) findViewById(R.id.ratingArgh);
        ratingOk = (ImageView) findViewById(R.id.ratingOk);
        ratingAha = (ImageView) findViewById(R.id.ratingAha);
        ratingWow = (ImageView) findViewById(R.id.ratingWow);
        rating = (ImageView) findViewById(R.id.ratingImage);

        saranEditTxt = (EditText) findViewById(R.id.saranEditTxt);

        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);
        tanggal = (TextView) findViewById(R.id.tanggal);
        jam = (TextView) findViewById(R.id.jam);
        pasien_name = (TextView) findViewById(R.id.pasien_name);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (LinearLayout) findViewById(R.id.container);


        ratingBoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.rating_boo));
                Rating = 1;
            }
        });

        ratingArgh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.rating_argh));
                Rating = 2;
            }
        });

        ratingOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.rating_ok));
                Rating = 3;
            }
        });

        ratingAha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.rating_aha));
                Rating = 4;
            }
        });

        ratingWow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.rating_wow));
                Rating = 5;
            }
        });

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);
        Log.d(TAG, "jwtTokenSP: " + jwTokenSP);
        if(jwTokenSP == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        initData();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(DetailOrderOld.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                Bundle b = getIntent().getExtras();

                final String saran = saranEditTxt.getText().toString();
                final String rating = Rating.toString();

                JSONObject object = new JSONObject();
                try {
                    object.put("rating", rating);
                    object.put("saran", saran);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(DetailOrderOld.this);
                String url = "http://103.43.44.211:1337/v1/order/" + b.getInt("id");

                JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // response
                                progressDialog.dismiss();
                                try {
                                    JSONObject data = response.getJSONObject("data");

                                    saranFormLayout.setVisibility(View.GONE);
                                    saranTextLayout.setVisibility(View.VISIBLE);
                                    saranText.setText(saranEditTxt.getText().toString());
                                    ratingLayout.setVisibility(View.GONE);

                                    Toast.makeText(getBaseContext(), "Berhasil.", Toast.LENGTH_SHORT).show();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("btnSend - Response", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                progressDialog.dismiss();


                                Toast.makeText(getBaseContext(), "Gagal.", Toast.LENGTH_SHORT).show();

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
                queue.add(putRequest);

            }
        });

    }



    private void initData()
    {
        Bundle b = getIntent().getExtras();


        String url = "http://103.43.44.211:1337/v1/order/"+ b.getInt("id")+"?populate=pasien,dokter,layanan,detailjadwal";

        container.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        Log.d(TAG, "init Data set loaded" );
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        spinner.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                        try {
                            JSONObject data = response.getJSONObject("data");
                            parseData(data);

                            String rate = data.getString("rating").trim().toString();

                            if(rate != "null" && !rate.trim().isEmpty())
                            {
                                Log.d(TAG, "rate" + rate);

                                saranFormLayout.setVisibility(View.GONE);
                                saranTextLayout.setVisibility(View.VISIBLE);
                                ratingLayout.setVisibility(View.GONE);
                                saranText.setText(data.getString("saran"));


                                if (rate.equals("1")){
                                    Rating = 1;
                                    rating.setImageDrawable(getDrawable(R.drawable.rating_boo));
                                }else if(rate.equals("2")){
                                    Rating = 2;
                                    rating.setImageDrawable(getDrawable(R.drawable.rating_argh));
                                }else if(rate.equals("3")) {
                                    Rating = 3;
                                    rating.setImageDrawable(getDrawable(R.drawable.rating_ok));
                                }else if(rate.equals("4")) {
                                    Rating = 4;
                                    rating.setImageDrawable(getDrawable(R.drawable.rating_aha));
                                }else if(rate.equals("5")) {
                                    Rating = 5;
                                    rating.setImageDrawable(getDrawable(R.drawable.rating_wow));
                                }


                            }else if(rate == "null" && rate.trim().isEmpty()){
                                saranFormLayout.setVisibility(View.VISIBLE);
                                saranTextLayout.setVisibility(View.GONE);
                            }

                            Log.d(TAG, "onResponse - initData - data" + data.toString());

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

    private void parseData(JSONObject data) throws JSONException {

        JSONObject pasien = data.getJSONObject("pasien");
        JSONObject dokter = data.getJSONObject("dokter");
        JSONObject detailjadwal = data.getJSONObject("detailjadwal");
        JSONObject layanan = data.getJSONObject("layanan");

        dokter_name.setText(dokter.getString("nama"));
        layanan_name.setText(layanan.getString("nama"));
        tanggal.setText(detailjadwal.getString("hari") + ", " + data.getString("tanggal"));
        jam.setText(detailjadwal.getString("jamMulai") + " - " + detailjadwal.getString("jamTutup"));
        pasien_name.setText(pasien.getString("nama"));



    }
}
