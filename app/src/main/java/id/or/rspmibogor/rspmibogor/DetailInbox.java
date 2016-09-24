package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailInbox extends AppCompatActivity {
    private static final String TAG = "DetailInbox";

    Toolbar toolbar;
    TextView title;
    TextView tanggal;
    TextView body;

    ProgressBar spinner;
    LinearLayout container;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inbox);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        title = (TextView) findViewById(R.id.judul_inbox);
        tanggal = (TextView) findViewById(R.id.tanggal_inbox);
        body = (TextView) findViewById(R.id.body);

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (LinearLayout) findViewById(R.id.content);

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        Bundle b = getIntent().getExtras();
        id = b.getString("id");

        initData();
        updateRead();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Detail Inbox");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initData()
    {




        String url = "http://103.23.22.46:1337/v1/inbox/"+ id;
        //final ProgressDialog loading = ProgressDialog.show(this.getActivity() ,"Loading Data", "Please wait...",false,false);
        container.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        spinner.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                        try {
                            JSONObject data = response.getJSONObject("data");
                            parseData(data);

                            //Log.d(TAG, "onResponse - initData - data" + data.toString());

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

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(req);
    }

    private void parseData(JSONObject data) throws JSONException {

        title.setText(data.getString("title"));
        tanggal.setText(data.getString("tanggal"));
        body.setText(data.getString("body"));

        toolbar.setTitle(data.getString("title"));

    }

    private void updateRead(){
        JSONObject object = new JSONObject();
        try {
            object.put("read", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://103.23.22.46:1337/v1/inbox/"+ id;
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                       // Log.d("updateRead - Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Log.d("Error.Response", String.valueOf(error));
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
        requestQueue.add(putRequest);
    }

}
