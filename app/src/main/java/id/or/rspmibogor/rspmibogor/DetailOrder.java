package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;

public class DetailOrder extends AppCompatActivity {

    private static final String TAG = "DetailOrder";

    Toolbar toolbar;
    TextView no_antrian;
    TextView dokter_name;
    TextView layanan_name;
    TextView tanggal;
    TextView jam;
    TextView pasien_name;
    Button buttonBatal;

    String status;
    Integer id;
    Boolean checkIn;

    ProgressBar spinner;
    LinearLayout container;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    Integer position_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Pendaftaran");

        no_antrian = (TextView) findViewById(R.id.no_antrian);
        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);
        tanggal = (TextView) findViewById(R.id.tanggal);
        jam = (TextView) findViewById(R.id.jam);
        pasien_name = (TextView) findViewById(R.id.pasien_name);
        buttonBatal = (Button) findViewById(R.id.btnBatal);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (LinearLayout) findViewById(R.id.container);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(jwTokenSP == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        Bundle b = getIntent().getExtras();

        position_list = b.getInt("position_last");

        initData();


        //toolbar.setTitle("Dokter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        buttonBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Batalkan Pendaftaran ")
                        .setMessage("Apa kamu yakin akan membatalkan pendaftaran?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancelOrder(id, tanggal.getText().toString());
                            }})

                        .setNegativeButton(android.R.string.no, null).show();
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


        no_antrian.setText(data.getString("noUrut"));
        dokter_name.setText(dokter.getString("nama"));
        layanan_name.setText(layanan.getString("nama"));
        tanggal.setText(detailjadwal.getString("hari") + ", " + data.getString("tanggal"));
        jam.setText(detailjadwal.getString("jamMulai") + " - " + detailjadwal.getString("jamTutup"));
        pasien_name.setText(pasien.getString("nama"));

        status = data.getString("status");
        id = data.getInt("id");
        checkIn = data.getBoolean("checkIn");


        Log.d(TAG, "status: " + status);
        if(status.toString() == "Dibatalkan Oleh User")
        {
            buttonBatal.setBackground(getDrawable(R.drawable.badge_oval_gray));
            buttonBatal.setEnabled(false);
            buttonBatal.setClickable(false);
            buttonBatal.setText("Telah dibatalkan");
            buttonBatal.setPadding(12, 12, 12, 12);
        }

    }

    private void cancelOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.43.44.211:1337/v1/order/cancel/" + id;

        JSONObject object = new JSONObject();
        try {
            object.put("order_id", id);
            object.put("tanggal", tanggal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new MessageEvent("cancelOrder", position_list));
        finish();

        /*JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {


                        Toast.makeText(getBaseContext(), "PendaftaranActivity berhasil dibatalkan.", Toast.LENGTH_SHORT).show();



                        finish();
                        Log.d("cancelOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "PendaftaranActivity Gagal dibatalkan.", Toast.LENGTH_SHORT).show();
                        Log.d("cancelOrder - Error.Response", String.valueOf(error));
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

        queue.add(putRequest);*/
    }
}
