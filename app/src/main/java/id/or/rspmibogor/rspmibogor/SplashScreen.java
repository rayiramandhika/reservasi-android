package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.Services.FirebaseInstanceIDService;

public class SplashScreen extends AppCompatActivity {
    private final String Tag = "SplashScreen";

    private static int splashInterval = 3000;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        final Intent i;
        if(jwTokenSP == null)
        {
            i = new Intent(SplashScreen.this, LoginActivity.class);
        }else
        {
            updateFCMToken();
            refreshingToken();
            i = new Intent(SplashScreen.this, MainActivity.class);
        }

        initBanner();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // TODO Auto-generated method stub


                startActivity(i);
                this.finish();
            }

            private void finish() {
                // TODO Auto-generated method stub

            }
        }, splashInterval);

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
        String url =  "http://api.rspmibogor.or.id/v1/banner?show=1";

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray data = response.getJSONArray("data");

                            parseBanner(data);

                            Log.d(Tag, "initBanner - response" + response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    private void parseBanner(JSONArray array){

        List<String> images = new ArrayList<>();

        if(array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {

                JSONObject json = null;
                try {

                    json = array.getJSONObject(i);

                    final String link = json.getString("link");
                    final String uri = "http://api.rspmibogor.or.id/v1/getbanner/" + link.toString();

                    images.add(uri);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }else{

            Uri url = Uri.parse("android.resource://"+this.getPackageName()+"/drawable/csm_laparoskopi_ab6621e110");
            images.add(String.valueOf(url));

        }

        SharedPreferences prefs = getSharedPreferences("RS PMI Banner", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=prefs.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(images);
        edit.putStringSet("listBanner", set);
        edit.commit();

        Log.d(Tag, "prefs banner: " + prefs);
    }
}
