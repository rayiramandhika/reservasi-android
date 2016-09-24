package id.or.rspmibogor.rspmibogor.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Models.User;

//Class extending FirebaseInstanceIdService
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat 
        //Log.d(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        Integer idSP = sharedPreferences.getInt("id", 0);
        String jwtToken = sharedPreferences.getString("token", null);

        if(idSP != 0) {
            User user = new User();
            user.updateFCMToken(refreshedToken, idSP, jwtToken, this.getBaseContext());
        }

    }

    public String getToken(){

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        return refreshedToken;

    }
}