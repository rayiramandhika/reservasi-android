package id.or.rspmibogor.rspmibogor.Fragment.PasienAdd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 27/08/16.
 */
public class Pembayaran extends AbstractStep {

    private final String TAG = "Pembayaran";

    private TextView namaPenjamin;
    private MaterialBetterSpinner jenisPembayaran;
    private RadioGroup typePasienGroup;
    private RadioButton typePasienRadio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_jenispembayaran, container, false);

        jenisPembayaran = (MaterialBetterSpinner) v.findViewById(R.id.jenisPembayaran);
        namaPenjamin = (TextView) v.findViewById(R.id.namaPenjamin);
        typePasienGroup = (RadioGroup) v.findViewById(R.id.typePasien);

        int selectedId = typePasienGroup.getCheckedRadioButtonId();
        typePasienRadio = (RadioButton) v.findViewById(selectedId);

        initSpinner();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public String name() {
        return "Jenis Pembayaran";
    }

    @Override
    public boolean isOptional() {
        return true;
    }


    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {
        Log.d(TAG, "onNext");

        String jnsPembayaran = jenisPembayaran.getText().toString();
        String nmPenjamin = namaPenjamin.getText().toString();
        String typePasien = typePasienRadio.getText().toString();

        int poss = this.getArguments().getInt("position");
        Bundle b = getStepDataFor(poss);

        String type = null;
        if(typePasien == "Ya")
        {
            type = "Pasien Lama";
        }else{
            type = "Pasien Baru";
        }


        b.putString("jenisPembayaran", jnsPembayaran);
        b.putString("namaPenjamin", nmPenjamin);
        b.putString("type", type);

        saveData();
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    @Override
    public String optional() {
        return "You can skip";
    }

    @Override
    public boolean nextIf() {
        Log.d(TAG, "nextIf");

        Integer i = 0;

        String jnsPembayaran = jenisPembayaran.getText().toString();
        String nmPenjamin = namaPenjamin.getText().toString();
        String typePasien = typePasienRadio.getText().toString();


        if(jnsPembayaran.isEmpty()){
            jenisPembayaran.setError("Golongan Darah harus dipilih");
            i++;
        } else {
            jenisPembayaran.setError(null);
        }

        if(nmPenjamin.isEmpty() && jnsPembayaran == "Asuransi"){
            namaPenjamin.setError("Golongan Darah harus diisi");
            i++;
        } else {
            namaPenjamin.setError(null);
        }


        if(i == 0) return true;
        return false;
    }

    @Override
    public String error() {
        return "Mohon lengkapi data";
    }

    public void initSpinner()
    {
        ArrayAdapter<CharSequence> jsPembayaranAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.jenisPembayaran, android.R.layout.simple_dropdown_item_1line);
        jsPembayaranAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        jenisPembayaran.setAdapter(jsPembayaranAdapter);

    }

    private void saveData()
    {
        Bundle identitas_diri = getStepDataFor(1);
        Bundle identitas_keluarga = getStepDataFor(2);
        Bundle identitas_alamat = getStepDataFor(3);
        Bundle identitas_jenisPembayaran = getStepDataFor(4);

        String noID = identitas_diri.getString("noID");
        String wargaNegara = identitas_diri.getString("wargaNegara");
        String noRekamMedik = identitas_diri.getString("noRekamMedik");
        String noTelp = identitas_diri.getString("noTelp");
        String umur = identitas_diri.getString("umur");
        String nama = identitas_diri.getString("nama");
        String tempatLahir = identitas_diri.getString("tempatLahir");
        String tanggalLahir = identitas_diri.getString("tanggalLahir");
        String jenisKelamin = identitas_diri.getString("jenisKelamin");
        String agama = identitas_diri.getString("agama");
        String pendidikan = identitas_diri.getString("pendidikan");
        String pekerjaan = identitas_diri.getString("pekerjaan");
        String golonganDarah = identitas_diri.getString("golonganDarah");
        String statusMarital = identitas_keluarga.getString("statusMarital");
        String namaPasutri = identitas_keluarga.getString("namaPasutri");
        String namaAyah = identitas_keluarga.getString("namaAyah");
        String namaIbu = identitas_keluarga.getString("namaIbu");
        String alamat = identitas_alamat.getString("alamat");
        String provinsi = identitas_alamat.getString("provinsi");
        String kota = identitas_alamat.getString("kota");
        String kecamatan = identitas_alamat.getString("kecamatan");
        String desa = identitas_alamat.getString("desa");
        String jenisPembayaran = identitas_jenisPembayaran.getString("jenisPembayaran");
        String namaPenjamin = identitas_jenisPembayaran.getString("namaPenjamin");
        String type = identitas_jenisPembayaran.getString("type");

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        //Log.d(TAG, "saveData - identitas_jenisPembayaran: " + namaPenjamin);


        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://103.43.44.211:1337/v1/pasien";

        JSONObject object = new JSONObject();
        try {
            object.put("noID", noID);
            object.put("noRekamMedik", noRekamMedik);
            object.put("nama", nama);
            object.put("type", type);
            object.put("tempatLahir", tempatLahir);
            object.put("tanggalLahir", tanggalLahir);
            object.put("wargaNegara", wargaNegara);
            object.put("umur", umur);
            object.put("noTelp", noTelp);
            object.put("pendidikan", pendidikan);
            object.put("pekerjaan", pekerjaan);
            object.put("golonganDarah", golonganDarah);
            object.put("agama", agama);
            object.put("jenisKelamin", jenisKelamin);
            object.put("statusMarital", statusMarital);
            object.put("namaPasutri", namaPasutri);
            object.put("namaAyah", namaAyah);
            object.put("namaIbu", namaIbu);
            object.put("provinsi", provinsi);
            object.put("kota", kota);
            object.put("kecamatan", kecamatan);
            object.put("desa", desa);
            object.put("alamat", alamat);
            object.put("jenisPembayaran", jenisPembayaran);
            object.put("namaPenjamin", namaPenjamin);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this.getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final Activity activity = this.getActivity();
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Pasien berhasil disimpan", Toast.LENGTH_SHORT).show();

                        new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                activity.finish();
                            }
                        },
                        500);

                        Log.d("SaveData - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("getDataFromToken - Error.Response", String.valueOf(error));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + jwTokenSP);
                return params;
            }
        };

        queue.add(putRequest);
    }
}
