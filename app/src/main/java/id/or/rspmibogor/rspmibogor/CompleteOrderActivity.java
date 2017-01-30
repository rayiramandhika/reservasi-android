package id.or.rspmibogor.rspmibogor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.Models.User;

public class CompleteOrderActivity extends AppCompatActivity {

    TextView dokter_name;
    TextView layanan_name;
    TextView poliklinik_name;
    TextView hari;
    TextView jam;
    TextView pasien_name;
    TextView pasien_umur;

    Integer detailjadwal_id;
    Integer dokter_id;
    Integer layanan_id;
    Integer poliklinik_id;
    Integer pasien_id;
    String tgl;

    Button btnNext;

    SharedPreferences sharedPreferences;

    Intent intent;
    private String TAG = "CompleteOrder - Response";
    private Integer refreshToken = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Review Pendaftaran");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);
        poliklinik_name = (TextView) findViewById(R.id.poliklinik_name);
        hari = (TextView) findViewById(R.id.hari);
        jam = (TextView) findViewById(R.id.jam);
        pasien_name = (TextView) findViewById(R.id.pasien_name);
        pasien_umur = (TextView) findViewById(R.id.pasien_umur);

        Bundle b = getIntent().getExtras();

        dokter_name.setText(b.getString("dokter_name"));
        layanan_name.setText("Klinik " + b.getString("layanan_name"));
        poliklinik_name.setText("Poliklinik " + b.getString("poliklinik_name"));
        hari.setText(b.getString("hari") + ", " + b.get("tanggal"));
        jam.setText(b.getString("jam"));
        pasien_name.setText(b.getString("pasien_name"));
        pasien_umur.setText(b.getString("pasien_umur"));


        dokter_id = b.getInt("dokter_id");
        detailjadwal_id = b.getInt("detailjadwal_id");
        layanan_id = b.getInt("layanan_id");
        poliklinik_id = b.getInt("poliklinik_id");
        pasien_id = b.getInt("pasien_id");
        tgl = b.getString("tanggal");

        btnNext = (Button) findViewById(R.id.btnNext);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Pendaftaran")
                        .setMessage("Apa Anda yakin data pendaftaran sudah benar?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Order();

                            }})

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void Order()
    {
        final Activity activity = this;

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.20.160:1337/v1/order/new/" + detailjadwal_id;


        JSONObject object = new JSONObject();
        try {
            object.put("tanggal", tgl);
            object.put("pasien_id", pasien_id);
            object.put("detailjadwal_id", detailjadwal_id);
            object.put("dokter_id", dokter_id);
            object.put("layanan_id", layanan_id);
            object.put("poliklinik_id", poliklinik_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Pendaftaran Anda sedang di proses..");
        progressDialog.show();

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Pendaftaran Berhasil")
                        .setCancelable(false)
                        .setMessage("Anda diminta untuk melakukan konfirmasi pada hari H sebelum pukul 07.30 ( jika jadwal praktek dokter dimulai sebelum jam 15.00) atau sebelum jam 14.30 (jika jadwal praktek dokter dimulai setelah jam 15.00). \n" +
                                "Silahkan melakukan konfirmasi di halaman detail pendaftaran. \n \n" +
                                "Jika Anda tidak melakukan konfirmasi atau melakukan konfirmasi melebihi ketentuan waktu yang telah di tetapkan maka pendaftaran di anggap batal. \n \n" +
                                "Setiap jadwal dokter yang berubah kami akan memberikan informasi melalui notifikasi / pada menu kotak masuk.\nPastikan sebelum Anda datang ke RS PMI Bogor bahwa tidak ada perubahan jadwal yang Anda terima di notifikasi / menu kotak masuk.\n \n" +

                                "Terima Kasih.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent = new Intent(activity, PendaftaranActivity.class);
                                activity.startActivity(intent);

                                if(JadwalDokterActivity.getInstance() != null) JadwalDokterActivity.getInstance().finish();
                                if(DetailJadwalDokter.getInstance() != null) DetailJadwalDokter.getInstance().finish();
                                if(PilihPasienActivity.getInstance() != null) PilihPasienActivity.getInstance().finish();
                                if(SearchActivity.getInstance() != null) SearchActivity.getInstance().finish();
                                finish();
                            }
                        }).show();

                        //Log.d(TAG, response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
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

                            onOrderFailed(message);
                        }else if(error instanceof TimeoutError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Mohon tunggu")
                                    .setCancelable(false)
                                    .setMessage("Pendaftaran sedang diproses. \n" +
                                                "Jika dalam waktu 5 menit Pendaftaran yang Anda lakukan tidak ada di halaman pendaftaran, silahkan mengulangi pendaftaran tersebut." +
                                                "\n \nTerima Kasih.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            intent = new Intent(activity, PendaftaranActivity.class);
                                            activity.startActivity(intent);

                                            if(JadwalDokterActivity.getInstance() != null) JadwalDokterActivity.getInstance().finish();
                                            if(DetailJadwalDokter.getInstance() != null) DetailJadwalDokter.getInstance().finish();
                                            if(PilihPasienActivity.getInstance() != null) PilihPasienActivity.getInstance().finish();
                                            if(SearchActivity.getInstance() != null) SearchActivity.getInstance().finish();
                                            finish();
                                        }
                                    }).show();
                        } else if(error instanceof AuthFailureError)
                        {

                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }

                            onOrderFailed(message);

                        }else {
                            if(error.networkResponse != null && error.networkResponse.data != null)
                            {
                                String body = null;
                                try {
                                    body = new String(error.networkResponse.data,"UTF-8");
                                    try {

                                        //Log.d("login - Error.body", body.toString());
                                        JSONObject data = new JSONObject(body);
                                        message = data.getString("message");

                                    }  catch (JSONException e) {
                                        message = body;
                                        e.printStackTrace();
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            onOrderFailed(message);
                        }
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

        int socketTimeOut = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);

    }

    public void onOrderFailed(String message) {
        String msg = null;
        if(message == null) msg = "Pendaftaran Gagal";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*pilihPasienActivity.finish();
        jadwalDokterActivity.finish();
        detailJadwalDokter.finish();*/

    }


}
