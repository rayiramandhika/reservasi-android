package id.or.rspmibogor.rspmibogor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;

public class CompleteOrderActivity extends AppCompatActivity {

    TextView dokter_name;
    TextView layanan_name;
    TextView hari;
    TextView jam;
    TextView pasien_name;
    TextView pasien_umur;

    Integer detailjadwal_id;
    Integer dokter_id;
    Integer layanan_id;
    Integer pasien_id;
    String tgl;

    Button btnNext;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Review Pendaftaran");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        dokter_name = (TextView) findViewById(R.id.dokter_name);
        layanan_name = (TextView) findViewById(R.id.layanan_name);
        hari = (TextView) findViewById(R.id.hari);
        jam = (TextView) findViewById(R.id.jam);
        pasien_name = (TextView) findViewById(R.id.pasien_name);
        pasien_umur = (TextView) findViewById(R.id.pasien_umur);

        Bundle b = getIntent().getExtras();

        dokter_name.setText(b.getString("dokter_name"));
        layanan_name.setText(b.getString("layanan_name"));
        hari.setText(b.getString("hari") + ", " + b.get("tanggal"));
        jam.setText(b.getString("jam"));
        pasien_name.setText(b.getString("pasien_name"));
        pasien_umur.setText(b.getString("pasien_umur"));


        dokter_id = b.getInt("dokter_id");
        detailjadwal_id = b.getInt("detailjadwal_id");
        layanan_id = b.getInt("layanan_id");
        pasien_id = b.getInt("pasien_id");
        tgl = b.getString("tanggal");

        btnNext = (Button) findViewById(R.id.btnNext);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Pendaftaran")
                        .setMessage("Apa kamu yakin data pendaftaran sudah benar?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Order();

                            }})

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.rspmibogor.or.id/v1/order/new/" + detailjadwal_id;

        JSONObject object = new JSONObject();
        try {
            object.put("tanggal", tgl);
            object.put("pasien_id", pasien_id);
            object.put("detailjadwal_id", detailjadwal_id);
            object.put("dokter_id", dokter_id);
            object.put("layanan_id", layanan_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Pendaftaran Berhasil")
                        .setMessage("Anda diminta untuk melakukan konfirmasi pada hari H paling lambat pukul 07.30. \n" +
                                "Silahkan melakukan konfirmasi di halaman detail pendaftaran. \n \nTerima Kasih.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intent = new Intent(activity, PendaftaranActivity.class);
                                activity.startActivity(intent);

                                JadwalDokterActivity.getInstance().finish();
                                DetailJadwalDokter.getInstance().finish();
                                PilihPasienActivity.getInstance().finish();
                                finish();
                            }
                        }).show();

                        Log.d("cancelOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        progressDialog.dismiss();

                        try {
                            String body = new String(error.networkResponse.data,"UTF-8");
                            JSONObject data = new JSONObject(body);
                            message = data.getString("message");
                            Log.d("login - Error.Response", data.getString("message"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        onOrderFailed(message);
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

        queue.add(putRequest);
    }

    public void onOrderFailed(String message) {
        String msg = null;
        if(message == null) msg = "Pendaftaran Gagal";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    /*static PilihPasienActivity pilihPasienActivity;
    static JadwalDokterActivity jadwalDokterActivity;
    static DetailJadwalDokter detailJadwalDokter;*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*pilihPasienActivity.finish();
        jadwalDokterActivity.finish();
        detailJadwalDokter.finish();*/

    }


}
