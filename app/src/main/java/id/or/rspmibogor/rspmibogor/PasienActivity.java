package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Adapter.DokterAdapter;
import id.or.rspmibogor.rspmibogor.Adapter.PasienAdapter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;

public class PasienActivity extends AppCompatActivity {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected PasienAdapter mAdapter;

    ProgressBar spinner;

    private List<Pasien> listPasien;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien);

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
                onBackPressed();
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

        /**Menu pasien in list
        final ImageView menumore = (ImageView) findViewById(R.id.menu_pasien);
        menumore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(PasienActivity.this, menumore);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_pasien, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                PasienActivity.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method**/
    }

    private void initData()
    {
        String url = "http://103.43.44.211:1337/v1/pasien";
        //final ProgressDialog loading = ProgressDialog.show(this ,"Loading Data", "Please wait...",false,false);
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

                pasien.setPasien_name(json.getString("nama"));
                pasien.setPasien_id(json.getInt("id"));
                pasien.setPasien_umur(json.getString("umur") + " Tahun");

                if(json.getString("noRekamMedik") != "") {
                    pasien.setPasien_noRekamMedik("noRekamMedik");
                }else{
                    pasien.setPasien_noRekamMedik("Belum ada No. Rekam Medik");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listPasien.add(pasien);
        }
        mAdapter.notifyDataSetChanged();
    }

}
