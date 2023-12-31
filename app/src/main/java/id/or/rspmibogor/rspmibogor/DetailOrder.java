package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import org.w3c.dom.Text;

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
    private Integer refreshToken = 0;
    private TextView confirmTime;
    private TextView jenis_pembayaran;
    private LinearLayout container_no_antrian;
    private TextView no_antrian;

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
        confirmTime = (TextView) findViewById(R.id.timeConfirm);
        jenis_pembayaran = (TextView) findViewById(R.id.jenis_pembayaran);
        container_no_antrian = (LinearLayout) findViewById(R.id.container_no_antrian);
        no_antrian = (TextView) findViewById(R.id.no_antrian);

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
                        .setMessage("Apa Anda yakin akan membatalkan pendaftaran?")
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_info)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailOrder.this);
            builder.setTitle("Informasi")
                    .setCancelable(false)
                    .setMessage("1. Konfirmasi pendaftaran dapat dilakukan pada hari H sebelum pukul 07.30 ( jika jadwal praktek dokter dimulai sebelum jam 15.00) atau sebelum jam 14.30 (jika jadwal praktek dokter dimulai setelah jam 15.00)." +
                            "\n \n2. Jika Anda tidak melakukan konfirmasi atau melakukan konfirmasi melebihi ketentuan waktu yang telah di tetapkan maka pendaftaran di anggap batal." +
                            "\n \n 3. Setiap jadwal dokter yang berubah kami akan memberikan informasi melalui notifikasi / pada menu kotak masuk.\nPastikan sebelum Anda datang ke RS PMI Bogor bahwa tidak ada perubahan jadwal yang Anda terima di notifikasi / menu kotak masuk.")
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
        }

        return true;
    }

    private void initData()
    {
        Bundle b = getIntent().getExtras();

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://api.rspmibogor.or.id/v1" + "/order/"+ b.getString("id")+"?populate=pasien,dokter,layanan,detailjadwal";

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
        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
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

        layanan_name.setText("Klinik " + layanan.getString("nama"));
        tanggal.setText(detailjadwal.getString("hari") + ", " + data.getString("tanggal"));
        jam.setText("Pkl. " + detailjadwal.getString("jamMulai") + " - " + detailjadwal.getString("jamTutup"));
        pasien_name.setText(pasien.getString("nama"));

        paramsTanggal = data.getString("tanggal");

        status = data.getString("status");
        id = data.getInt("id");
        checkIn = data.getBoolean("checkIn");

        Boolean statusConfirm = data.getBoolean("confirmed");
        //Log.d(TAG,"statusConfirm: " + statusConfirm);

        String noUrut = " - ";
        noUrut =  data.getString("noUrut");

        if(statusConfirm.equals(true))
        {
            confirmed = 1;
            confirmedTxt.setText("Sudah Konfirmasi");
            no_antrian.setText("No Antrian Anda: " + noUrut);
        }else{
            confirmed = 0;
            confirmedTxt.setText("Belum Konfirmasi");
            no_antrian.setText("No Antrian di dapat setelah konfirmasi");
        }


        //Log.d(TAG, "status: " + status);
        if(status.toString().equals("Dibatalkan Oleh User"))
        {
            //buttonBatal.setBackground(getDrawable(R.drawable.badge_oval_gray));
            buttonBatal.setBackgroundResource(R.drawable.badge_oval_gray);
            buttonBatal.setEnabled(false);
            buttonBatal.setClickable(false);
            buttonBatal.setText("Telah dibatalkan");
            buttonBatal.setPadding(12, 12, 12, 12);
        }

        //jenis pembayaran
        String jenisPembayaran = pasien.getString("jenisPembayaran");
        if(jenisPembayaran.equals("Tunai")){
            jenis_pembayaran.setText("Tunai");
        }else if(jenisPembayaran.equals("Asuransi")){
            jenis_pembayaran.setText("Asuransi / " + pasien.getString("namaPenjamin"));
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

        //set text Max. Time Confirm
        if(diffPraktek < 0){
            confirmTime.setText("Max. waktu konfirmasi pendaftaran ini adalah Pkl. 07.30, Jika Anda melakukan konfirmasi melebihi waktu tersebut atau tidak melakukan konfirmasi maka pendaftaran di anggap batal.");
        }else if(diffPraktek >= 0){
            confirmTime.setText("Max. waktu konfirmasi pendaftaran ini adalah Pkl. 14.30, Jika Anda melakukan konfirmasi melebihi waktu tersebut atau tidak melakukan konfirmasi maka pendaftaran di anggap batal.");
        }

    }

    private void cancelOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.rspmibogor.or.id/v1" + "/order/cancel/" + id;

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Pembatalan pendaftaran Anda sedang di proses...");
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

                        Toast.makeText(getBaseContext(), "Pendaftaran gagal dibatalkan, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
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

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);

        queue.add(putRequest);
    }

    private void confirmOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.rspmibogor.or.id/v1" + "/order/" + id;

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Konfirmasi pendaftaran Anda sedang di proses...");
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
                        container_no_antrian.setVisibility(View.VISIBLE);
                        try {
                            JSONObject data = response.getJSONObject("data");
                            no_antrian.setText("No Antrian Anda: " + data.getString("noUrut"));
                        } catch (JSONException e) {
                            no_antrian.setText("No Antrian Anda: -");
                            e.printStackTrace();
                        }

                        // Log.d("confirmOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

                        error.printStackTrace();
                        Toast.makeText(getBaseContext(), "Konfirmasi pendaftaran gagal, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();

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

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

    private void initFoto(String uriFoto)
    {
        if(uriFoto.isEmpty())
        {
            dokter_foto.setImageDrawable(ContextCompat.getDrawable(DetailOrder.this, R.drawable.noprofile));
        }else {
            Glide.with(this)
                    .load("http://api.rspmibogor.or.id/v1" + "/dokter/foto/" + uriFoto)
                    .centerCrop()
                    .crossFade()
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.noprofile)
                    .into(dokter_foto);
        }
    }
}
