package id.or.rspmibogor.rspmibogor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Models.User;

public class SearchActivity extends AppCompatActivity {

    SearchableSpinner spinnerLayanan;

    SharedPreferences sharedPreferences;

    LinearLayout container;
    RelativeLayout errorLayout;
    ProgressBar spinner;

    FloatingActionButton btnTryAgain;

    Button btnCariDokter;
    EditText dokter_name;

    ArrayList<String> listLayanan = new ArrayList<String>();
    ArrayList<Integer> listIdLayanan = new ArrayList<Integer>();
    private String TAG = "SearchActivity";

    private Integer refreshToken = 0;

    static SearchActivity searchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cari Dokter - Poliklinik Afiat");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        spinner = (ProgressBar) findViewById(R.id.progress_bar);
        container = (LinearLayout) findViewById(R.id.container);
        errorLayout = (RelativeLayout) findViewById(R.id.error);

        btnTryAgain = (FloatingActionButton) findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSpinner();
            }
        });

        dokter_name = (EditText) findViewById(R.id.dokter_name) ;
        btnCariDokter = (Button) findViewById(R.id.btnCari);
        btnCariDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dokterNama = dokter_name.getText().toString().trim();
                Integer layananId = listIdLayanan.get(spinnerLayanan.getSelectedItemPosition());

                //Log.d(TAG, "dokterName: " + dokterNama);
                //Log.d(TAG, "layananId: " + layananId);

                Bundle b = new Bundle();
                b.putString("dokter_name", dokterNama);
                b.putString("layanan_id", layananId.toString());

                Intent intent = new Intent(getBaseContext(), JadwalDokterActivity.class);
                intent.putExtras(b);

                startActivity(intent);

            }
        });

        initSpinner();


    }

    public static SearchActivity getInstance(){
        return searchActivity;
    }

    private void initSpinner()
    {

        spinnerLayanan = (SearchableSpinner) findViewById(R.id.spinnerLayanan);
        spinnerLayanan.setTitle("Pilih Layanan");

        sharedPreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://103.23.22.46:1337/v1/layanan/";

        container.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);

        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        spinner.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);

                        try {
                            JSONArray data = response.getJSONArray("data");
                            JSONObject json = null;



                            for (int i = 0; i < data.length(); i++) {

                                json = data.getJSONObject(i);

                                int idx = json.getInt("id");
                                String nama = json.getString("nama");
                                listIdLayanan.add(idx);
                                listLayanan.add(nama);

                            }

                            ArrayAdapter<String> layanan = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, listLayanan);
                            spinnerLayanan.setAdapter(layanan);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

                        spinner.setVisibility(View.GONE);
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
        requestQueue.add(req);

    }
}
