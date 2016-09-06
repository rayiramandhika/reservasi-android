package id.or.rspmibogor.rspmibogor;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Class.ImageClass;
import id.or.rspmibogor.rspmibogor.Fragment.HomeFragment;
import id.or.rspmibogor.rspmibogor.Fragment.InboxFragment;
import id.or.rspmibogor.rspmibogor.Fragment.OrderFragment;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.Services.FirebaseInstanceIDService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    HomeFragment HomeFrag = new HomeFragment();
    InboxFragment InboxFrag = new InboxFragment();
    OrderFragment OrderFrag = new OrderFragment();

    SharedPreferences sharedPreferences;

    //BottomBar bottomBar;
    NavigationView navigationView;
    DrawerLayout drawer;
    View header;

    TextView emailText;
    TextView namaText;
    ImageView imageHeader;

    //Main menu dashboard
    CardView imageJadwaldokter;
    CardView imagePasien;
    CardView imagePendaftaran;
    CardView imageInbox;

    //BottomBarBadge unreadMessages;

    int MenuIdActive;

    String jwTokenSP;

    CarouselView carouselView;
    int[] sliderImage = {R.drawable.header, R.drawable.header, R.drawable.header, R.drawable.header, R.drawable.header};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Checking if user has login **/

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        Bundle b = getIntent().getExtras();
        Boolean login = false;

        if(b != null){
            if (b.getBoolean("loginSuccess")) login = true;
            else login = false;
        }


        Log.d("Checking login", "jwtToken: " + jwTokenSP);
        Log.d("Checking login", "loginSuccess: " + login);

        if(jwTokenSP == null && login != true){
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }

        if(jwTokenSP != null){
            updateFCMToken();
            refreshingToken();
        }

        /** Checking if user has login **/

        /** Checking if home fragment not added first **/
        if (savedInstanceState == null && !HomeFrag.isAdded()) {

           /** FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.container, HomeFrag);
            transaction.commit();**/
        }
        /** Checking if home fragment not added first **/


        /** setting toolbar and navigation drawer **/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        /** setting toolbar and navigation drawer **/


        /** Setting Carrousel **/
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sliderImage.length);
        carouselView.setImageListener(imageListener);

        imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sliderImage[position]);
            }
        };
        /** **/


        /** get data from shared preferences **/
        String namaSP = sharedPreferences.getString("nama", null);
        String emailSP = sharedPreferences.getString("email", null);
        String profilePictureSP = sharedPreferences.getString("profilePicture", null);

        //set header
        setHeader(emailSP, namaSP, profilePictureSP);
        /** get data from shared preferences **/

        /** Bottom NavBar
        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                switch (itemId) {
                    case R.id.home:
                        //Toast.makeText(getApplicationContext(),"Beranda dipilih",Toast.LENGTH_SHORT).show();

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(0).setChecked(true);

                        transaction.replace(R.id.container, HomeFrag);
                        transaction.commit();


                        break;
                    case R.id.order:
                        //Toast.makeText(getApplicationContext(),"Pesanan dipilih",Toast.LENGTH_SHORT).show();

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(1).setChecked(true);

                        transaction.replace(R.id.container, OrderFrag);
                        transaction.commit();

                        break;
                    case R.id.inbox:
                        //Toast.makeText(getApplicationContext(),"Kotak Masuk dipilih",Toast.LENGTH_SHORT).show();

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(2).setChecked(true);

                        transaction.replace(R.id.container, InboxFrag);
                        transaction.commit();

                        break;
                }
            }
        });

        bottomBar.setActiveTabColor("#D32F2F");

         Bottom NavBar **/

        /**Checking unread Message **/
        //checkingUnreadMessage();
        /**Checking unread Message **/

        /** checking listening main menu **/
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == MenuIdActive) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        MenuIdActive = item.getItemId();

        if (id == R.id.dashboard) {

           /* transaction.replace(R.id.container, HomeFrag);
            transaction.commit();

            //bottomBar.selectTabAtPosition(0, false);*/

        } else if (id == R.id.pasien) {

            Intent intent = new Intent(this, PasienActivity.class);
            startActivity(intent);

        } else if (id == R.id.order) {

            /*transaction.replace(R.id.container, OrderFrag);
            transaction.commit();

            //bottomBar.selectTabAtPosition(1, false);*/

            Intent intent = new Intent(this, PendaftaranActivity.class);
            startActivity(intent);


        } else if (id == R.id.inbox) {

           /* transaction.replace(R.id.container, InboxFrag);
            transaction.commit();

            bottomBar.selectTabAtPosition(2, false);*/
            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);


        } else if (id == R.id.jadwaldokter) {

            Intent intent = new Intent(this, JadwalDokterActivity.class);
            startActivity(intent);


        } else if (id == R.id.myacccount) {

        } else if (id == R.id.logout) {

            sharedPreferences.edit().clear().commit();

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
        imageHeader = (ImageView) header.findViewById(R.id.header_image);

        emailText.setText(email);
        namaText.setText(nama);

        String url = "http://103.43.44.211:1337/v1/user/profilepicture/" + profilePicture;
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


                imageHeader.setImageDrawable(getDrawable(R.drawable.no_profilepicture));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(ir);
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

    private void checkingUnreadMessage()
    {
        String url =  "http://103.43.44.211:1337/v1/count/unread";

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Integer count = response.getInt("data");
                            if(count > 0){
                               /* unreadMessages = bottomBar.makeBadgeForTabAt(2, "#D32F2F", count);
                                unreadMessages.show();
                                unreadMessages.setAnimationDuration(200);*/
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sliderImage[position]);
        }
    };

}
