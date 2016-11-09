package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Models.User;

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
    ImageView dokter_foto;
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
    LinearLayout container, containerData;
    RelativeLayout nodata, errorLayout;

    FloatingActionButton btnTryAgain;

    SharedPreferences sharedPreferences;

    String id;

    private Integer refreshToken = 0;

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
        dokter_foto = (ImageView) findViewById(R.id.dokter_foto);
        layanan_name = (TextView) findViewById(R.id.layanan_name);
        tanggal = (TextView) findViewById(R.id.tanggal);
        jam = (TextView) findViewById(R.id.jam);
        pasien_name = (TextView) findViewById(R.id.pasien_name);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (LinearLayout) findViewById(R.id.container);
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

        ratingBoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_boo));
                Rating = 1;
            }
        });

        ratingArgh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_argh));
                Rating = 2;
            }
        });

        ratingOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_ok));
                Rating = 3;
            }
        });

        ratingAha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_aha));
                Rating = 4;
            }
        });

        ratingWow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_wow));
                Rating = 5;
            }
        });

        Bundle b = getIntent().getExtras();
        id = b.getString("id");

        initData();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(DetailOrderOld.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

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
                String url = "http://103.23.22.46:1337/v1/order/" + id;

                sharedPreferences = getBaseContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
                final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

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
                                //Log.d("btnSend - Response", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
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
                                        user.refreshToken(jwTokenSP, getBaseContext());
                                    }
                                }

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

    @Override
    public void onBackPressed() {
        finish();
    }



    private void initData()
    {

        String url = "http://103.23.22.46:1337/v1/order/"+ id +"?populate=pasien,dokter,layanan,detailjadwal";

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        spinner.setVisibility(View.VISIBLE);
        container.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        //Log.d(TAG, "init Data set loaded" );
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
                                    rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_boo));
                                }else if(rate.equals("2")){
                                    Rating = 2;
                                    rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_argh));
                                }else if(rate.equals("3")) {
                                    Rating = 3;
                                    rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_ok));
                                }else if(rate.equals("4")) {
                                    Rating = 4;
                                    rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_aha));
                                }else if(rate.equals("5")) {
                                    Rating = 5;
                                    rating.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.rating_wow));
                                }


                            }else if(rate == "null" && rate.trim().isEmpty()){
                                saranFormLayout.setVisibility(View.VISIBLE);
                                saranTextLayout.setVisibility(View.GONE);
                            }

                            //Log.d(TAG, "onResponse - initData - data" + data.toString());

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
        requestQueue.add(req);
    }

    private void parseData(JSONObject data) throws JSONException {

        JSONObject pasien = data.getJSONObject("pasien");
        JSONObject dokter = data.getJSONObject("dokter");
        JSONObject detailjadwal = data.getJSONObject("detailjadwal");
        JSONObject layanan = data.getJSONObject("layanan");

        dokter_name.setText(dokter.getString("nama"));

        String uriFoto = dokter.getString("foto");
        initFoto(uriFoto);

        layanan_name.setText(layanan.getString("nama"));
        tanggal.setText(detailjadwal.getString("hari") + ", " + data.getString("tanggal"));
        jam.setText("Pkl. " + detailjadwal.getString("jamMulai") + " - " + detailjadwal.getString("jamTutup"));
        pasien_name.setText(pasien.getString("nama"));

    }

    private void initFoto(String uriFoto)
    {
        if(uriFoto.isEmpty())
        {
            dokter_foto.setImageDrawable(ContextCompat.getDrawable(DetailOrderOld.this, R.drawable.noprofile));
        }else {
            Glide.with(this)
                    .load("http://103.23.22.46:1337/v1/dokter/foto/" + uriFoto)
                    .centerCrop()
                    .crossFade()
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.noprofile)
                    .into(dokter_foto);
        }
    }
}
