package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.Models.User;

public class DetailOrder extends AppCompatActivity {

    private static final String TAG = "DetailOrder";

    Toolbar toolbar;
    TextView dokter_name;
    TextView layanan_name;
    TextView tanggal;
    TextView jam;
    TextView pasien_name;
    TextView confirmedTxt;

    private String paramsTanggal;

    ImageView dokter_foto;

    Button buttonBatal;
    Button buttonConfirm;

    String status;
    Integer id;
    Boolean checkIn;

    ProgressBar spinner;
    RelativeLayout nodata, errorLayout, container;

    FloatingActionButton btnTryAgain;

    SharedPreferences sharedPreferences;

    Integer position_list;

    Integer confirm = 0;
    Integer confirmed = 0;
    Integer hariH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Pendaftaran");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dokter_name = (TextView) findViewById(R.id.dokter_name);
        dokter_foto = (ImageView) findViewById(R.id.dokter_foto);
        layanan_name = (TextView) findViewById(R.id.layanan_name);
        tanggal = (TextView) findViewById(R.id.tanggal);
        jam = (TextView) findViewById(R.id.jam);
        pasien_name = (TextView) findViewById(R.id.pasien_name);
        confirmedTxt = (TextView) findViewById(R.id.confirmed);

        buttonBatal = (Button) findViewById(R.id.btnBatal);
        buttonConfirm = (Button) findViewById(R.id.btnConfirm);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (RelativeLayout) findViewById(R.id.container);
        nodata = (RelativeLayout) findViewById(R.id.nodata);
        errorLayout = (RelativeLayout) findViewById(R.id.error);

        btnTryAgain = (FloatingActionButton) findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);

        Bundle b = getIntent().getExtras();
        position_list = b.getInt("position_list");

        initData();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final AlertDialog.Builder builderCancelOrder = new AlertDialog.Builder(this);


        buttonBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builderCancelOrder.setTitle("Batalkan Pendaftaran ")
                        .setMessage("Apa kamu yakin akan membatalkan pendaftaran?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancelOrder(id, paramsTanggal);
                            }})

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        JodaTimeAndroid.init(this);


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailOrder.this);


            if(confirmed.equals(1)){

                builder.setTitle("Konfirmasi Pendaftaran ")
                        .setMessage("Anda sudah melakukan konfirmasi sebelumnya.")
                        .setPositiveButton(android.R.string.yes, null).show();

            }else{

                if(hariH.equals(1)){

                    if(confirm.equals(1)){
                        builder.setTitle("Konfirmasi Pendaftaran ")
                                .setMessage("Apakah Anda yakin akan melakukan konfirmasi kedatangan pada pendaftaran ini?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        confirmOrder(id, paramsTanggal);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }else{

                        builder.setTitle("Konfirmasi Pendaftaran ")
                                .setMessage("Mohon maaf Anda sudah melewati batas waktu konfirmasi untuk pendaftaran ini.")
                                .setPositiveButton(android.R.string.yes, null).show();

                    }

                }else{

                    builder.setTitle("Konfirmasi Pendaftaran ")
                            .setMessage("Mohon maaf Anda hanya dapat melakukan konfirmasi pada hari H.")
                            .setPositiveButton(android.R.string.yes, null).show();

                }

            }




            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void initData()
    {
        Bundle b = getIntent().getExtras();

        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        String url = "http://103.23.22.46:1337/v1/order/"+ b.getString("id")+"?populate=pasien,dokter,layanan,detailjadwal";

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
                            parseData(data);

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

                        if(error instanceof AuthFailureError)
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

        paramsTanggal = data.getString("tanggal");

        status = data.getString("status");
        id = data.getInt("id");
        checkIn = data.getBoolean("checkIn");

        Boolean statusConfirm = data.getBoolean("confirmed");
        Log.d(TAG,"statusConfirm: " + statusConfirm);

        if(statusConfirm.equals(true))
        {
            confirmed = 1;
            confirmedTxt.setText("Sudah Konfirmasi");
        }else{
            confirmed = 0;
            confirmedTxt.setText("Belum Konfirmasi");
        }


        Log.d(TAG, "status: " + status);
        if(status.toString() == "Dibatalkan Oleh User")
        {
            //buttonBatal.setBackground(getDrawable(R.drawable.badge_oval_gray));
            buttonBatal.setBackgroundResource(R.drawable.badge_oval_gray);
            buttonBatal.setEnabled(false);
            buttonBatal.setClickable(false);
            buttonBatal.setText("Telah dibatalkan");
            buttonBatal.setPadding(12, 12, 12, 12);
        }

        //init konfirmasi
        DateTimeZone timezone = DateTimeZone.forID("Asia/Jakarta");
        DateTimeFormatter df = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");

        DateTime orderDate = null;
        DateTime today = new DateTime(new DateTime(), timezone);

        DateTime jamPraktek = null;
        DateTime shiftPagi = null;
        DateTime shiftSiang = null;
        DateTime timeToday = null;

        String time = today.getHourOfDay() + ":" + today.getMinuteOfHour();



        orderDate = df.parseDateTime(data.getString("tanggal")).withZone(timezone);
        jamPraktek = tf.parseDateTime(detailjadwal.getString("jamMulai")).withZone(timezone);
        shiftPagi = tf.parseDateTime("07:30").withZone(timezone);
        shiftSiang = tf.parseDateTime("14:30").withZone(timezone);
        timeToday = tf.parseDateTime(time);


        long diffDay = Days.daysBetween(orderDate.toLocalDate(), today.toLocalDate()).getDays();

        long diffPraktek = Minutes.minutesBetween(shiftSiang, jamPraktek).getMinutes();

        long diffTodayPagi = Minutes.minutesBetween(shiftPagi, timeToday).getMinutes();
        long diffTodaySiang = Minutes.minutesBetween(shiftSiang, timeToday).getMinutes();

        if(diffDay >= 0)
        {

            hariH = 1;

            //cek jadwal prakterk apakah lewat dari jam 14:30

            if(diffPraktek < 0){

                //jika tidak
                //cek jam sekarang apakah kurang dari jam 7:30
                if(diffTodayPagi < 0){

                    confirm = 1;

                }else{

                    confirm = 0;

                }

            //cek jadwal prakterk apakah lewat dari jam 14:30

            }else if(diffPraktek >= 0){

                //jika ya
                //cek jam sekarang apakah kurang dari jam 14:30
                if(diffTodaySiang < 0){

                    confirm = 1;

                }else{

                    confirm = 0;

                }

            }

        }else if(diffDay < 0){

            confirm = 0;

        }

    }

    private void cancelOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/order/cancel/" + id;
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Batalkan Pendaftaran");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("order_id", id);
            object.put("tanggal", tanggal);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        Toast.makeText(getBaseContext(), "Pendaftaran berhasil dibatalkan.", Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new MessageEvent("cancelOrder", position_list));
                        finish();

                        //Log.d("cancelOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }
                        }

                        Toast.makeText(getBaseContext(), "Pendaftaran Gagal dibatalkan.", Toast.LENGTH_SHORT).show();
                        //Log.d("cancelOrder - Error.Response", String.valueOf(error));
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

    private void confirmOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/order/" + id;
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Konfirmasi Pendaftaran");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("confirmed", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        builder.setTitle("Konfirmasi Berhasil ")
                                .setMessage("Terima Kasih. Anda telah berhasil melakukan konfirmasi. \nMohon untuk menunjukan detail pendaftaran ini pada bagian pendaftaran di RS PMI Bogor.")
                                .setNegativeButton(android.R.string.yes, null).show();

                        confirmed = 1;
                        confirmedTxt.setText("Sudah Konfirmasi");

                       // Log.d("confirmOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, getBaseContext());
                            }
                        }

                        error.printStackTrace();
                        Toast.makeText(getBaseContext(), "Konfirmasi Gagal.", Toast.LENGTH_SHORT).show();

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

    private void initFoto(String uriFoto)
    {
        if(uriFoto.isEmpty())
        {
            dokter_foto.setImageDrawable(ContextCompat.getDrawable(DetailOrder.this, R.drawable.noprofile));
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
