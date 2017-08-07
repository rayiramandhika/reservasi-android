package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.Services.FirebaseInstanceIDService;

public class SplashScreen extends AppCompatActivity {
    private final String Tag = "SplashScreen";

    private static int splashInterval = 10000;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesFirsTime;
    String jwTokenSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        initBanner();

        checkVersion();
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void updateFCMToken()
    {
        /** update FCM token to server **/
        Integer idSP = sharedPreferences.getInt("id", 0);

        if(idSP != 0){
            FirebaseInstanceIDService firebase = new FirebaseInstanceIDService();
            String token;
            token = firebase.getToken();

            User user = new User();
            user.updateFCMToken(token, idSP, jwTokenSP, this.getBaseContext());

            // Log.d("Firebase", "token: " + token);
        }
        /** update FCM token to server **/
    }

    private void refreshingToken()
    {
        User user = new User();
        user.refreshToken(jwTokenSP, this.getBaseContext());
    }

    private void initBanner()
    {
        final List<String> images = new ArrayList<>();
        final List<String> title = new ArrayList<>();

        String url =  "http://api.rspmibogor.or.id/v1" + "/banner?show=1&sort=order%20ASC";
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array = response.getJSONArray("data");

                            //parseBanner(data);


                            if(array.length() > 0) {

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject json = null;
                                    try {

                                        json = array.getJSONObject(i);

                                        final String link = json.getString("link");
                                        final String uri = "http://api.rspmibogor.or.id/v1" + "/getbanner/" + link.toString();
                                        //Log.d(Tag, "uri: " + uri.toString());

                                        final String titleBanner = json.getString("title");
                                        title.add(titleBanner);
                                        images.add(uri);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }



                            }else{

                                Uri url = Uri.parse("android.resource://"+getPackageName()+"/drawable/ic_slider_default");
                                title.add("Selamat Datang");
                                images.add(String.valueOf(url));

                            }

                            parseBanner(images, title);

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Uri url = Uri.parse("android.resource://"+getPackageName()+"/drawable/ic_slider_default");
                            title.add("Selamat Datang");
                            images.add(String.valueOf(url));

                            parseBanner(images, title);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Uri url = Uri.parse("android.resource://"+getPackageName()+"/drawable/ic_slider_default");
                        title.add("Selamat Datang");
                        images.add(String.valueOf(url));

                        parseBanner(images, title);
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    private void parseBanner(List<String> images, List<String> title){

        SharedPreferences prefs = getSharedPreferences("RS PMI Banner", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        String arrImg = Arrays.toString(images.toArray()).replace("[", "").replace("]", "");
        String arrTitle = Arrays.toString(title.toArray()).replace("[", "").replace("]", "");
        edit.putString("listBanner", arrImg);
        edit.putString("listTitle", arrTitle);
        edit.commit();

    }

    private void checkVersion()
    {
        String url =  "http://api.rspmibogor.or.id/v1" + "/version/android";
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                        try {
                            JSONObject data = response.getJSONObject("data");

                            Integer versionCodeAPI = data.getInt("versionCode");
                            Integer versionCode = BuildConfig.VERSION_CODE;

                            if(versionCode < versionCodeAPI){
                                String status = data.getString("status");
                                if(status.equals("urgent")){
                                    builder.setTitle("Pembaruan Tersedia")
                                            .setMessage("Aplikasi telah usang. Mohon untuk memperbarui aplikasi ini!")
                                            .setCancelable(false)
                                            .setPositiveButton("Perbarui", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                }
                                            }).show();
                                }else{
                                    builder.setTitle("Pembaruan Tersedia")
                                            .setMessage("Tersedia pembaruan untuk aplikasi ini. Apakah anda ingin memperbarui nya ?")
                                            .setPositiveButton("Perbarui", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                }

                                            })
                                            .setNegativeButton("Tidak Sekarang", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    nextNavigaton();
                                                }
                                            })
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialog) {
                                                    nextNavigaton();
                                                }
                                            })
                                            .show();
                                }
                            }else{
                                nextNavigaton();
                            }
                        }catch (JSONException e){
                            nextNavigaton();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                        if(error instanceof NoConnectionError)
                        {
                            builder.setTitle("Tidak Ada Koneksi")
                                    .setMessage("Tidak ada koneksi internet pada perangkat anda. Sambungkan ke jaringan Wi-fi atau seluler")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            nextNavigaton();
                                        }

                                    })
                                    .setNegativeButton("Setelan", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            nextNavigaton();
                                        }
                                    })
                                    .show();
                        }else{
                            nextNavigaton();
                        }
                    };
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeOut = 5000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        requestQueue.add(req);
    }

    private void nextNavigaton()
    {

        sharedPreferencesFirsTime = this.getSharedPreferences("IS FIRST TIME", Context.MODE_PRIVATE);
        Boolean isFirstTime = sharedPreferencesFirsTime.getBoolean("isFirstTime", true);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final Intent i;
        if(isFirstTime.equals(true)){

            i = new Intent(SplashScreen.this, IntroActivity.class);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                    finish();
                }
            }, splashInterval);

        }else{

            if(jwTokenSP == null)
            {
                i = new Intent(SplashScreen.this, LoginActivity.class);
            }else
            {
                refreshingToken();
                updateFCMToken();
                i = new Intent(SplashScreen.this, MainActivity.class);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                    finish();
                }
            }, splashInterval);
        }
    }
}
