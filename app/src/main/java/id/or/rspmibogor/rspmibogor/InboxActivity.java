package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.InboxAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;

public class InboxActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "InboxFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected InboxAdapter mAdapter;

    private List<Inbox> listInbox;

    ProgressBar spinner;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    RelativeLayout nodata;
    LinearLayout container;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Kotak Masuk");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InboxActivity.this.finish();
            }
        });

        nodata = (RelativeLayout) findViewById(R.id.nodata);
        container = (LinearLayout) findViewById(R.id.container);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        listInbox = new ArrayList<>();

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleInbox);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        initDataset();

        mAdapter = new InboxAdapter(listInbox, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary));

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void initDataset() {

        String url = "http://api.rspmibogor.or.id/v1/inbox?sort=createdAt%20DESC";
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        Log.d(TAG, "onResponse - response" + response.toString());
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
        requestQueue.add(request);

    }

    //This method will parse json data
    private void parseData(JSONArray array){
        if(array.length() > 0) {

            container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                Inbox inbox = new Inbox();
                JSONObject json = null;
                String desc = "";
                String desc2 = "";
                try {

                    json = array.getJSONObject(i);

                    desc = json.getString("body");
                    if (desc.length() > 100) {
                        desc2 = desc.substring(0, 100) + " ......";
                    } else {
                        desc2 = desc;
                    }


                    inbox.setTitle(json.getString("title"));
                    inbox.setDesc(desc2);
                    inbox.setBody(json.getString("body"));
                    inbox.setTanggal(json.getString("tanggal"));
                    inbox.setRead(json.getBoolean("read"));
                    inbox.setId(json.getInt("id"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listInbox.add(inbox);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            //container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        refreshData();
    }

    private void refreshData()
    {
        String url = "http://api.rspmibogor.or.id/v1/inbox?sort=createdAt%20DESC";
        //spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        Log.d(TAG, "onResponse - response" + response.toString());
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseRefreshData(data);

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
        requestQueue.add(request);
    }

    private void parseRefreshData(JSONArray array){
        if(array.length() > 0) {

            listInbox.removeAll(listInbox);

            container.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.INVISIBLE);

            for (int i = 0; i < array.length(); i++) {

                Inbox inbox = new Inbox();
                JSONObject json = null;
                String desc = "";
                String desc2 = "";
                try {

                    json = array.getJSONObject(i);

                    desc = json.getString("body");
                    if (desc.length() > 100) {
                        desc2 = desc.substring(0, 100) + " ......";
                    } else {
                        desc2 = desc;
                    }


                    inbox.setTitle(json.getString("title"));
                    inbox.setDesc(desc2);
                    inbox.setBody(json.getString("body"));
                    inbox.setTanggal(json.getString("tanggal"));
                    inbox.setRead(json.getBoolean("read"));
                    inbox.setId(json.getInt("id"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listInbox.add(inbox);
            }
            mAdapter.notifyDataSetChanged();
        }else{
            //container.setVisibility(View.INVISIBLE);
            nodata.setVisibility(View.VISIBLE);
        }
    }


}
