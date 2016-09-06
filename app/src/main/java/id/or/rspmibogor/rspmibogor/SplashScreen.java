package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.Services.FirebaseInstanceIDService;

public class SplashScreen extends AppCompatActivity {

    //Set waktu lama splashscreen
    private static int splashInterval = 2000;

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

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i;
                if(jwTokenSP == null)
                {
                    i = new Intent(SplashScreen.this, LoginActivity.class);
                }else
                {
                    updateFCMToken();
                    refreshingToken();
                    i = new Intent(SplashScreen.this, MainActivity.class);
                }

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
}
