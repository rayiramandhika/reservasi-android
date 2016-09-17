package id.or.rspmibogor.rspmibogor;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import id.or.rspmibogor.rspmibogor.Class.ImageClass;
import id.or.rspmibogor.rspmibogor.Fragment.HomeFragment;
import id.or.rspmibogor.rspmibogor.Fragment.InboxFragment;
import id.or.rspmibogor.rspmibogor.Fragment.OrderFragment;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.Services.FirebaseInstanceIDService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    SharedPreferences sharedPreferences;

    NavigationView navigationView;
    DrawerLayout drawer;
    View header;

    TextView emailText;
    TextView namaText;
    ImageView imageHeader;

    CardView imageJadwaldokter;
    CardView imagePasien;
    CardView imagePendaftaran;
    CardView imageInbox;

    TextView unread;


    String jwTokenSP;

    CarouselView carouselView;
    int[] sliderImage = {R.drawable.csm_laparoskopi_ab6621e110, R.drawable.csm_main_banner_eswl_510b76606f, R.drawable.csm_main_banner_flamboyan2_89df9c8f6d};
    //List<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unread = (TextView) findViewById(R.id.unread);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        Bundle b = getIntent().getExtras();
        Boolean login = false;

        if(b != null){
            if (b.getBoolean("loginSuccess")) login = true;
            else login = false;
        }

        //Log.d("Checking login", "jwtToken: " + jwTokenSP);
        //Log.d("Checking login", "loginSuccess: " + login);

        if(jwTokenSP == null && login != true){
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }

        /*if(jwTokenSP != null){
            updateFCMToken();
            //refreshingToken();
        }*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        String namaSP = sharedPreferences.getString("nama", null);
        String emailSP = sharedPreferences.getString("email", null);
        String profilePictureSP = sharedPreferences.getString("profilePicture", null);

        setHeader(emailSP, namaSP, profilePictureSP);

        checkingUnreadMessage();

        imageJadwaldokter = (CardView) findViewById(R.id.menu_jadwaldokter);
        imagePasien = (CardView) findViewById(R.id.menu_pasien);
        imagePendaftaran = (CardView) findViewById(R.id.menu_pendaftaran);
        imageInbox = (CardView) findViewById(R.id.menu_inbox);

        imageJadwaldokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), JadwalDokterActivity.class);
                startActivity(intent);
            }
        });

        imagePasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PasienActivity.class);
                startActivity(intent);
            }
        });

        imagePendaftaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PendaftaranActivity.class);
                startActivity(intent);
            }
        });

        imageInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InboxActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences prefs = getSharedPreferences("RS PMI Banner", Context.MODE_PRIVATE);

        Set<String> set = prefs.getStringSet("listBanner", null);
        final List<String> images = new ArrayList<String>(set);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(images.size());
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(final int position, ImageView imageView) {

                Log.d(TAG, "image: " + images.get(position).toString());

                Glide.with(getBaseContext())
                        .load(images.get(position).toString())
                        .centerCrop()
                        .placeholder(R.drawable.image_placeholder)
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*String imageUrl = images.get(position);
                        System.out.println("ImageView clicked! " + imageUrl); // outputs the imageUrl registered*/
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
        checkingUnreadMessage();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.dashboard) {

        } else if (id == R.id.pasien) {

            Intent intent = new Intent(this, PasienActivity.class);
            startActivity(intent);

        } else if (id == R.id.order) {

            Intent intent = new Intent(this, PendaftaranActivity.class);
            startActivity(intent);


        } else if (id == R.id.inbox) {

            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);


        } else if (id == R.id.jadwaldokter) {

            Intent intent = new Intent(this, JadwalDokterActivity.class);
            startActivity(intent);

        } else if (id == R.id.logout) {

            SharedPreferences preferences = getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setHeader(String email, String nama, String profilePicture)
    {
        header = navigationView.getHeaderView(0);

        emailText = (TextView) header.findViewById(R.id.header_email);
        namaText = (TextView) header.findViewById(R.id.header_name);
        //imageHeader = (ImageView) header.findViewById(R.id.header_image);

        emailText.setText(email);
        namaText.setText(nama);

        /*String url = "http://api.rspmibogor.or.id/v1/user/profilepicture/" + profilePicture;
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.d("Main Activity", "ImageRequest - response" + response);

                ImageClass imageClass = new ImageClass();

                Bitmap image = imageClass.getRoundedShape(response);
                imageHeader.setImageBitmap(image);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                imageHeader.setImageDrawable(getDrawable(R.drawable.noprofile));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(ir);*/
    }

    private void updateFCMToken()
    {
        Integer idSP = sharedPreferences.getInt("id", 0);

        if(idSP != 0){
            FirebaseInstanceIDService firebase = new FirebaseInstanceIDService();
            String token;
            token = firebase.getToken();

            User user = new User();
            user.updateFCMToken(token, idSP, jwTokenSP, this.getBaseContext());
        }
    }

    private void refreshingToken()
    {
        User user = new User();
        user.refreshToken(jwTokenSP, this.getBaseContext());
    }

    private void checkingUnreadMessage()
    {
        String url =  "http://api.rspmibogor.or.id/v1/count/unread";

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Integer count = response.getInt("data");
                            if(count > 0){

                                TextView view = (TextView) navigationView.getMenu().findItem(R.id.inbox).getActionView();
                                view.setText(count > 0 ? String.valueOf(count) : null);

                                unread.setVisibility(View.VISIBLE);

                                Log.d(TAG, "checkingUnreadMessage: " + String.valueOf(count));
                            }

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

}
