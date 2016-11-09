package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
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
import id.or.rspmibogor.rspmibogor.Adapter.NewOrderAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.Models.User;

public class InboxActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "InboxActivity";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected InboxAdapter mAdapter;

    private List<Inbox> listInbox;

    ProgressBar spinner;

    SharedPreferences sharedPreferences;

    RelativeLayout nodata, errorLayout;
    LinearLayout container;

    FloatingActionButton btnTryAgain;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Integer skip = 0;
    private Integer numRows = 0;
    private Integer refreshToken = 0;

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
        errorLayout = (RelativeLayout) findViewById(R.id.error);

        btnTryAgain = (FloatingActionButton) findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDataset();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        listInbox = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycleInbox);
        mLayoutManager = new LinearLayoutManager(this);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);

        initDataset();

        mAdapter = new InboxAdapter(listInbox, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setLoadMoreListener(new InboxAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(skip < numRows)
                        {
                            int index = listInbox.size() - 1;
                            loadMore(index);// a method which requests remote data
                        }else {
                            mAdapter.setMoreDataAvailable(false);
                        }
                    }
                });
            }
        });


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary));

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void initDataset() {

        String url = "http://103.23.22.46:1337/v1/inbox?sort=createdAt%20DESC";

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        spinner.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        spinner.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseData(data);

                            JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        };
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

                        spinner.setVisibility(View.INVISIBLE);
                        errorLayout.setVisibility(View.VISIBLE);
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
        String url = "http://103.23.22.46:1337/v1/inbox?sort=createdAt%20DESC&limit="+skip.toString();

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        //Log.d(TAG, "onResponse - response" + response.toString());
                        swipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseRefreshData(data);

                           // Log.d(TAG, "onResponse - data" + data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getBaseContext(), "Gagal memuat data baru", Toast.LENGTH_SHORT).show();
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
            nodata.setVisibility(View.VISIBLE);
        }
    }

    private void loadMore(Integer index) {

        listInbox.add(null);
        mAdapter.notifyItemInserted(listInbox.size()-1);

        String url = "http://103.23.22.46:1337/v1/inbox?sort=createdAt%20DESC&skip="+skip.toString();

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        listInbox.remove(listInbox.size()-1);

                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseLoadMore(data);

                            JSONObject metadata = response.getJSONObject("metadata");

                            numRows = metadata.getInt("numrows");
                            skip = skip + metadata.getInt("limit");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        };
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

                        listInbox.remove(listInbox.size()-1);
                        mAdapter.notifyDataChanged();

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
    private void parseLoadMore(JSONArray array){
        if(array.length() > 0) {

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
            //mAdapter.notifyDataSetChanged();
        }

        mAdapter.notifyDataChanged();
    }


}
