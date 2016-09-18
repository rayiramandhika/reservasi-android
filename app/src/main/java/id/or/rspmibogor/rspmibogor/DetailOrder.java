package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.or.rspmibogor.rspmibogor.Class.ImageClass;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;

public class DetailOrder extends AppCompatActivity {

    private static final String TAG = "DetailOrder";

    Toolbar toolbar;
    TextView dokter_name;
    TextView layanan_name;
    TextView tanggal;
    TextView jam;
    TextView pasien_name;
    TextView confirmedTxt;

    ImageView dokter_foto;

    Button buttonBatal;
    Button buttonConfirm;

    String status;
    Integer id;
    Boolean checkIn;

    ProgressBar spinner;
    LinearLayout container;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

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
        container = (LinearLayout) findViewById(R.id.container);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        Bundle b = getIntent().getExtras();

        position_list = b.getInt("position_list");

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


        final AlertDialog.Builder builder; builder = new AlertDialog.Builder(this);


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

        JodaTimeAndroid.init(this);


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            if(confirmed.equals(1)){

                builder.setTitle("Konfirmasi Pendaftaran ")
                        .setMessage("Anda sudah melakukan konfirmasi sebelumnya.")
                        .setNegativeButton(android.R.string.yes, null).show();

            }else{

                if(hariH.equals(1)){

                    if(confirm.equals(1)){
                        builder.setTitle("Konfirmasi Pendaftaran ")
                                .setMessage("Apakah Anda yakin akan melakukan konfirmasi kedatangan pada pendaftaran ini?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        confirmOrder(id, tanggal.getText().toString());
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }else{

                        builder.setTitle("Konfirmasi Pendaftaran ")
                                .setMessage("Mohon maaf Anda sudah melewati batas waktu konfirmasi untuk pendaftaran ini.")
                                .setNegativeButton(android.R.string.yes, null).show();

                    }

                }else{

                    builder.setTitle("Konfirmasi Pendaftaran ")
                            .setMessage("Mohon maaf Anda hanya dapat melakukan konfirmasi pada hari H.")
                            .setNegativeButton(android.R.string.yes, null).show();

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


        String url = "http://api.rspmibogor.or.id/v1/order/"+ b.getString("id")+"?populate=pasien,dokter,layanan,detailjadwal";

        container.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        Log.d(TAG, "init Data set loaded" );
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
        jam.setText(detailjadwal.getString("jamMulai") + " - " + detailjadwal.getString("jamTutup"));
        pasien_name.setText(pasien.getString("nama"));

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
            buttonBatal.setBackground(getDrawable(R.drawable.badge_oval_gray));
            buttonBatal.setEnabled(false);
            buttonBatal.setClickable(false);
            buttonBatal.setText("Telah dibatalkan");
            buttonBatal.setPadding(12, 12, 12, 12);
        }

        //init konfirmasi
        DateTimeZone timezone = DateTimeZone.forID("Asia/Jakarta");
        DateTimeFormatter df = DateTimeFormat.forPattern("dd-MMM-yyyy");
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

        Log.d(TAG, "orderDate: " + orderDate);
        Log.d(TAG, "today: " + today);
        Log.d(TAG, "jamPraktek: " + jamPraktek);
        Log.d(TAG, "shiftPagi: " + shiftPagi);
        Log.d(TAG, "shiftSiang: " + shiftSiang);
        Log.d(TAG, "timeToday: " + timeToday);
        Log.d(TAG, "confirm: " + confirm);
        Log.d(TAG, "hariH: " + hariH);

        Log.d(TAG, "diffDay: " + diffDay);
        Log.d(TAG, "diffPraktek: " + diffPraktek);
        Log.d(TAG, "diffTodayPagi: " + diffTodayPagi);
        Log.d(TAG, "diffTodaySiang: " + diffTodaySiang);


    }

    private void cancelOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.rspmibogor.or.id/v1/order/cancel/" + id;

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


                        Toast.makeText(getBaseContext(), "Pendaftaran berhasil dibatalkan.", Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new MessageEvent("cancelOrder", position_list));
                        finish();

                        Log.d("cancelOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Pendaftaran Gagal dibatalkan.", Toast.LENGTH_SHORT).show();
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

        queue.add(putRequest);
    }

    private void confirmOrder(Integer id, String tanggal)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.rspmibogor.or.id/v1/order/" + id;

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


                        /*Toast.makeText(getBaseContext(), "Konfirmasi berhasil.", Toast.LENGTH_SHORT).show();*/

                        /*EventBus.getDefault().post(new MessageEvent("cancelOrder", position_list));
                        finish();*/

                        builder.setTitle("Konfirmasi Berhasil ")
                                .setMessage("Terima Kasih. Anda telah berhasil melakukan konfirmasi. \nMohon untuk menunjukan detail pendaftaran ini pada bagian pendaftaran di RS PMI Bogor.")
                                .setNegativeButton(android.R.string.yes, null).show();

                        confirmed = 1;
                        confirmedTxt.setText("Sudah Konfirmasi");

                        Log.d("confirmOrder - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getBaseContext(), "Konfirmasi Gagal.", Toast.LENGTH_SHORT).show();
                        Log.d("confirmOrder - Error.Response", String.valueOf(error));
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
            dokter_foto.setImageDrawable(getDrawable(R.drawable.noprofile));
        }else {
            String url = "http://api.rspmibogor.or.id/v1/dokter/foto/" + uriFoto;
            ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Log.d("Main Activity", "ImageRequest - response" + response);

                    dokter_foto.setImageBitmap(response);
                }
            }, 0, 0, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    dokter_foto.setImageDrawable(getDrawable(R.drawable.noprofile));
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(ir);
        }
    }
}
