package id.or.rspmibogor.rspmibogor.Fragment;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
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

import id.or.rspmibogor.rspmibogor.Adapter.OldOrderAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.OldOrder;
import id.or.rspmibogor.rspmibogor.LoginActivity;
import id.or.rspmibogor.rspmibogor.R;


public class OldOrderFragment extends Fragment {


    private static final String TAG = "OldOrderFragment";


    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected OldOrderAdapter mAdapter;

    private ProgressBar spinner;

    private List<OldOrder> listOldOlder;

    SharedPreferences sharedPreferences;
    String jwTokenSP;


    public OldOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listOldOlder = new ArrayList<>();

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
        View viewRoot =  inflater.inflate(R.layout.fragment_old_order, container, false);

        mRecyclerView = (RecyclerView) viewRoot.findViewById(R.id.recycleOldOrder);
        mLayoutManager = new LinearLayoutManager(this.getContext());

        spinner = (ProgressBar) viewRoot.findViewById(R.id.progress_bar);

        initDataset();

        mAdapter = new OldOrderAdapter(listOldOlder, this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return viewRoot;
    }

    private void initDataset() {

        String url = "http://103.43.44.211:1337/v1/getorder/old";
        //final ProgressDialog loading = ProgressDialog.show(this.getActivity() ,"Loading Data", "Please wait...",false,false);
        spinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "init Data set loaded" );
        //Creating a json array request
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
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

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());

        //Adding request to the queue
        requestQueue.add(req);

    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i < array.length(); i++) {

            OldOrder oldOrder = new OldOrder();
            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                //oldOrder.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));
                JSONObject pasien = json.getJSONObject("pasien");
                JSONObject detailjadwal = json.getJSONObject("detailjadwal");
                JSONObject user = json.getJSONObject("user");
                JSONObject dokter = json.getJSONObject("dokter");
                JSONObject layanan = json.getJSONObject("layanan");

                oldOrder.setPasien_name(pasien.getString("nama"));
                oldOrder.setPasien_norekammedik(pasien.getString("noRekamMedik"));
                oldOrder.setDetailjadwal_hari(detailjadwal.getString("hari"));
                oldOrder.setDetailjadwal_jammulai(detailjadwal.getString("jamMulai"));
                oldOrder.setDetailjadwal_jamtutup(detailjadwal.getString("jamTutup"));
                oldOrder.setUser_id(user.getInt("id"));
                oldOrder.setUser_name(user.getString("nama"));
                oldOrder.setOrder_id(json.getInt("id"));
                oldOrder.setOrder_noUrut(json.getString("noUrut"));
                oldOrder.setDokter_id(dokter.getInt("id"));
                oldOrder.setDokter_name(dokter.getString("nama"));
                oldOrder.setLayanan_id(layanan.getInt("id"));
                oldOrder.setLayanan_name(layanan.getString("nama"));
                oldOrder.setOrder_tanggal(json.getString("tanggal"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listOldOlder.add(oldOrder);
        }
        mAdapter.notifyDataSetChanged();
    }

}
