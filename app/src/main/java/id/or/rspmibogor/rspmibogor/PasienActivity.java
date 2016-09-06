package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.DokterAdapter;
import id.or.rspmibogor.rspmibogor.Adapter.PasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;

public class PasienActivity extends AppCompatActivity {

    private static final String TAG = "PasienActivity";
    private String last_updated;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;
    private Integer last_id;

    SharedPreferences sharedPreferences;
    String jwTokenSP;
    Integer user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien);

        EventBus myEventBus = EventBus.getDefault();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pasien");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listPasien = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePasien);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);
        user_id = sharedPreferences.getInt("id", 0);

        if(jwTokenSP == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        initData();

        mAdapter = new PasienAdapter(listPasien, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PasienAddActivity.class);
                startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);

    }

    /*@Override
    public void onStart() {
        super.onStart();

    }*/

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());

        String msg = event.getPesan().toString();

        if(msg.equals("addPasien"))
        {
            getNewData();
        }
    }

    private void initData()
    {
        String url = "http://103.43.44.211:1337/v1/pasien?sort=id%20DESC";
        //final ProgressDialog loading = ProgressDialog.show(this ,"Loading Data", "Please wait...",false,false);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseData(data);

                            Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
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

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);
    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i < array.length(); i++) {

            Pasien pasien = new Pasien();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);
                if(i == 0){
                    last_id = json.getInt("id");
                    Log.d(TAG, "last_id: " + last_id);
                }


                pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur") + " Tahun");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listPasien.add(pasien);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void getNewData()
    {
        String url = "http://103.43.44.211:1337/v1/pasien?where={%22id%22:{%22>%22:"+last_id+"}}";
        //final ProgressDialog loading = ProgressDialog.show(this ,"Loading Data", "Please wait...",false,false);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        try {
                            last_updated = response.getString("last_update");
                        } catch (JSONException e) {

                        }
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataNew(data);
                            Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
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

    //This method will parse json data
    private void parseDataNew(JSONArray array){
        for(int i = 0; i < array.length(); i++) {

            Pasien pasien = new Pasien();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);
                Integer aLength = array.length();
                if(i == (aLength - 1)){
                    last_id = json.getInt("id");
                    Log.d(TAG, "last_id: " + last_id);
                }


                pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur") + " Tahun");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listPasien.add(0, pasien);
        }
        mAdapter.notifyDataSetChanged();
    }

}
