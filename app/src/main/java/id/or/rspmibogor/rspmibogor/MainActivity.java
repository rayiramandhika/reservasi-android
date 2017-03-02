package id.or.rspmibogor.rspmibogor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
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
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    CarouselView carouselView;
    //List<String> images = new ArrayList<>();

    private Integer refreshToken =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unread = (TextView) findViewById(R.id.unread);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        Bundle b = getIntent().getExtras();
        Boolean login = false;

        if(b != null){
            if (b.getBoolean("loginSuccess")) login = true;
            else login = false;
        }

        //Log.d("Checking login", "jwtToken: " + jwTokenSP);
        //Log.d("Checking login", "loginSuccess: " + login);

        /*if(jwTokenSP == null && login != true){
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }*/

        if(login == true)
        {
            updateFCMToken();
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
        checkFeedback();

        imageJadwaldokter = (CardView) findViewById(R.id.menu_jadwaldokter);
        imagePasien = (CardView) findViewById(R.id.menu_pasien);
        imagePendaftaran = (CardView) findViewById(R.id.menu_pendaftaran);
        imageInbox = (CardView) findViewById(R.id.menu_inbox);

        imageJadwaldokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SearchActivity.class);
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

        //Set<String> set = prefs.getStringSet("listBanner", null);
        String arrImg = prefs.getString("listBanner", null);
        final List<String> images = new ArrayList<String>(Arrays.asList(arrImg.split(",")));

        String arrTitle = prefs.getString("listTitle", null);
        final List<String> title = new ArrayList<String>(Arrays.asList(arrTitle.split(",")));

        //Log.d(TAG, "images: " + images.toString());

        if(images == null)
        {
            Uri url = Uri.parse("android.resource://"+this.getPackageName()+"/drawable/ic_slider_default");
            images.add(String.valueOf(url));
        }

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(images.size());


        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(final int position, ImageView imageView) {

                //Log.d(TAG, "image: " + images.get(position));
                //Log.d(TAG, "position: " + position);

                String uri = images.get(position).trim().toString();

                Glide.with(getBaseContext())
                        .load(uri)
                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.drawable.image_placeholder)
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    String imageUrl = images.get(position).trim().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder.create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.dialog_imageview, null);

                    ImageView imgView = (ImageView) dialogLayout.findViewById(R.id.imageView);

                        Glide.with(MainActivity.this)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(imgView);

                        dialog.setView(dialogLayout);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                    }
                });
            }
        });



    }

    private void checkFeedback() {

        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        String url =  R.string.ip_api + "/order/checkfeedback";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang memuat data...");
        progressDialog.show();


        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        sendFeedback(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.hide();
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

    private void sendFeedback(JSONObject response) {

        final Integer[] Rating = new Integer[1];
        final Integer order_id;

        TextView tanggal;
        TextView jam;
        TextView pasien_name;

        ImageView ratingBoo;
        ImageView ratingArgh;
        ImageView ratingOk;
        ImageView ratingAha;
        ImageView ratingWow;
        final ImageView rating;

        ImageView btnSend;

        final EditText saranEditTxt;

        try {
            final JSONObject data = response.getJSONObject("data");
            final JSONObject pasien = data.getJSONObject("pasien");
            final JSONObject detailjadwal = data.getJSONObject("detailjadwal");
            Rating[0] = 0;
            order_id = data.getInt("id");

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_feedback);
            dialog.setTitle("Berikan Penilaian Anda Untuk Pelayanan RS PMI Bogor");

            tanggal = (TextView) dialog.findViewById(R.id.tanggal);
            jam = (TextView) dialog.findViewById(R.id.jam);
            pasien_name = (TextView) dialog.findViewById(R.id.pasien_name);

            tanggal.setText(data.getString("tanggal"));
            jam.setText(detailjadwal.getString("jamMulai") + " - " + detailjadwal.getString("jamTutup"));
            pasien_name.setText(pasien.getString("nama"));

            final TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);

            ratingBoo = (ImageView) dialog.findViewById(R.id.ratingBoo);
            ratingArgh = (ImageView) dialog.findViewById(R.id.ratingArgh);
            ratingOk = (ImageView) dialog.findViewById(R.id.ratingOk);
            ratingAha = (ImageView) dialog.findViewById(R.id.ratingAha);
            ratingWow = (ImageView) dialog.findViewById(R.id.ratingWow);
            rating = (ImageView) dialog.findViewById(R.id.ratingImage);
            rating.setImageDrawable(null);

            saranEditTxt = (EditText) dialog.findViewById(R.id.saranEditTxt);
            btnSend = (ImageView) dialog.findViewById(R.id.btnSend);

            dialog.show();

            ratingBoo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rating.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                    rating.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rating_boo));
                    Rating[0] = 1;
                }
            });

            ratingArgh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rating.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                    rating.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rating_argh));
                    Rating[0] = 2;
                }
            });

            ratingOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rating.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                    rating.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rating_ok));
                    Rating[0] = 3;
                }
            });

            ratingAha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rating.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                    rating.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rating_aha));
                    Rating[0] = 4;
                }
            });

            ratingWow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rating.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                    rating.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rating_wow));
                    Rating[0] = 5;
                }
            });

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(Rating[0] == 0)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(false)
                                .setMessage("Mohon untuk memilih penilaian terlebih dahulu")
                                .setPositiveButton(android.R.string.yes, null)
                                .show();
                    }else{
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Mohon Tunggu");
                        progressDialog.setMessage("Sedang mengirim penilaian...");
                        progressDialog.show();

                        final String saran = saranEditTxt.getText().toString();
                        final String rating = Rating[0].toString();

                        JSONObject object = new JSONObject();
                        try {
                            object.put("rating", rating);
                            object.put("saran", saran);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                        String url = R.string.ip_api + "/order/" + order_id + "/feedback";

                        sharedPreferences = getBaseContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
                        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

                        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // response
                                        progressDialog.dismiss();
                                        dialog.hide();
                                        try {
                                            JSONObject data = response.getJSONObject("data");

                                            Toast.makeText(getBaseContext(), "Berhasil mengirim penilaian.", Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //Log.d("btnSend - Response", response.toString());
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
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

                                        Toast.makeText(getBaseContext(), "Gagal mengirim penilaian.", Toast.LENGTH_SHORT).show();

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
                        int socketTimeOut = 10000;
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        putRequest.setRetryPolicy(policy);
                        queue.add(putRequest);
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);

        } else if (id == R.id.help) {

            Intent intent = new Intent(this, BantuanActivity.class);
            startActivity(intent);

        } else if (id == R.id.tentang) {

            Intent intent = new Intent(this, TentangActivity.class);
            startActivity(intent);

        }  else if (id == R.id.logout) {

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

    }

    private void updateFCMToken()
    {
        Integer idSP = sharedPreferences.getInt("id", 0);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

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
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        User user = new User();
        user.refreshToken(jwTokenSP, this.getBaseContext());
    }

    private void checkingUnreadMessage()
    {
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        String url =  R.string.ip_api + "/count/unread";


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

                                //Log.d(TAG, "checkingUnreadMessage: " + String.valueOf(count));
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

}
