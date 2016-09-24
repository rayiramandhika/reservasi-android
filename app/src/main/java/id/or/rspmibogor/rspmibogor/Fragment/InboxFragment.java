package id.or.rspmibogor.rspmibogor.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import id.or.rspmibogor.rspmibogor.Adapter.NewOrderAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.LoginActivity;
import id.or.rspmibogor.rspmibogor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {
    private static final String TAG = "InboxFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected InboxAdapter mAdapter;

    private List<Inbox> listInbox;

    ProgressBar spinner;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listInbox = new ArrayList<>();

        sharedPreferences = this.getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        if(jwTokenSP == null){
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_inbox, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleInbox);
        mLayoutManager = new LinearLayoutManager(this.getContext());

        spinner = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        initDataset();

        mAdapter = new InboxAdapter(listInbox, this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    private void initDataset() {

        String url = "http://103.23.22.46:1337/v1/inbox?sort=createdAt%20DESC";
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

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(request);

    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i < array.length(); i++) {

            Inbox inbox = new Inbox();
            JSONObject json = null;
            String desc = "";
            String desc2 = "";
            try {

                json = array.getJSONObject(i);

                desc = json.getString("body");
                if(desc.length() > 100)
                {
                    desc2 = desc.substring(0,100)  + " ......";
                }else{
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
    }

}
