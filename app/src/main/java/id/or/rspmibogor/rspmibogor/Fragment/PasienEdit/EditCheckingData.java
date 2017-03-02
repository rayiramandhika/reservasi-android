package id.or.rspmibogor.rspmibogor.Fragment.PasienEdit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 13/09/16.
 */
public class EditCheckingData extends AbstractStep {

    private final String TAG = "EditCheckingData";

    TextView noID;
    TextView noRekamMedik;
    TextView typePasien;
    TextView nama;
    TextView tempatLahir;
    TextView tanggalLahir;
    TextView jenisKelamin;
    TextView wargaNegara;
    TextView noTelp;
    TextView agama;
    TextView pendidikan;
    TextView pekerjaan;
    TextView golonganDarah;
    TextView statusMarital;
    TextView namaPasutri;
    TextView namaAyah;
    TextView namaIbu;
    TextView alamat;
    TextView desa;
    TextView kecamatan;
    TextView kota;
    TextView provinsi;
    TextView jenisPembayaran;
    TextView namaPenjamin;

    private Integer refreshToken = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_checking, container, false);

        mStepper.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        noID =  (TextView) v.findViewById(R.id.noID);
        noRekamMedik =  (TextView) v.findViewById(R.id.noRekamMedik);
        typePasien =  (TextView) v.findViewById(R.id.typePasien);
        nama =  (TextView) v.findViewById(R.id.nama);
        tempatLahir =  (TextView) v.findViewById(R.id.tempatLahir);
        tanggalLahir =  (TextView) v.findViewById(R.id.tanggalLahir);
        jenisKelamin =  (TextView) v.findViewById(R.id.jenisKelamin);
        wargaNegara =  (TextView) v.findViewById(R.id.wargaNegara);
        noTelp =  (TextView) v.findViewById(R.id.noTelp);
        agama =  (TextView) v.findViewById(R.id.agama);
        pendidikan =  (TextView) v.findViewById(R.id.pendidikan);
        pekerjaan =  (TextView) v.findViewById(R.id.pekerjaan);
        golonganDarah =  (TextView) v.findViewById(R.id.golonganDarah);
        statusMarital =  (TextView) v.findViewById(R.id.statusMarital);
        namaPasutri =  (TextView) v.findViewById(R.id.namaPasutri);
        namaAyah =  (TextView) v.findViewById(R.id.namaAyah);
        namaIbu =  (TextView) v.findViewById(R.id.namaIbu);
        alamat =  (TextView) v.findViewById(R.id.alamat);
        desa =  (TextView) v.findViewById(R.id.desa);
        kecamatan =  (TextView) v.findViewById(R.id.kecamatan);
        kota =  (TextView) v.findViewById(R.id.kota);
        provinsi =  (TextView) v.findViewById(R.id.provinsi);
        jenisPembayaran =  (TextView) v.findViewById(R.id.jenisPembayaran);
        namaPenjamin = (TextView) v.findViewById(R.id.namaPenjamin);

        Bundle identitas_diri = getStepDataFor(1);
        Bundle identitas_keluarga = getStepDataFor(2);
        Bundle identitas_alamat = getStepDataFor(3);
        Bundle identitas_jenisPembayaran = getStepDataFor(4);

        noID.setText(identitas_diri.getString("noID"));
        wargaNegara.setText(identitas_diri.getString("wargaNegara"));
        typePasien.setText(identitas_diri.getString("type"));
        if(identitas_diri.getString("type").equals("Pasien Lama"))
        {
            noRekamMedik.setText(identitas_diri.getString("noRekamMedik"));
        }else {
            noRekamMedik.setText("");
        }

        noTelp.setText(identitas_diri.getString("noTelp"));
        nama.setText(identitas_diri.getString("nama"));
        tempatLahir.setText(identitas_diri.getString("tempatLahir"));
        tanggalLahir.setText(identitas_diri.getString("tanggalLahir"));
        jenisKelamin.setText(identitas_diri.getString("jenisKelamin"));
        agama.setText(identitas_diri.getString("agama"));
        pendidikan.setText(identitas_diri.getString("pendidikan"));
        pekerjaan.setText(identitas_diri.getString("pekerjaan"));
        golonganDarah.setText(identitas_diri.getString("golonganDarah"));
        statusMarital.setText(identitas_keluarga.getString("statusMarital"));
        if(identitas_keluarga.getString("statusMarital").equals("Sudah Kawin"))
        {
            namaPasutri.setText(identitas_keluarga.getString("namaPasutri"));
        }else{
            namaPasutri.setText(identitas_keluarga.getString(""));
        }
        namaAyah.setText(identitas_keluarga.getString("namaAyah"));
        namaIbu.setText(identitas_keluarga.getString("namaIbu"));
        alamat.setText(identitas_alamat.getString("alamat"));
        provinsi.setText( identitas_alamat.getString("provinsi"));
        kota.setText( identitas_alamat.getString("kota"));
        kecamatan.setText(identitas_alamat.getString("kecamatan"));
        desa.setText(identitas_alamat.getString("desa"));
        jenisPembayaran.setText(identitas_jenisPembayaran.getString("jenisPembayaran"));


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

        Bundle identitas_jenisPembayaran = getStepDataFor(4);
        jenisPembayaran.setText(identitas_jenisPembayaran.getString("jenisPembayaran"));
        if(identitas_jenisPembayaran.getString("jenisPembayaran").equals("Asuransi")) {
            namaPenjamin.setText(identitas_jenisPembayaran.getString("namaPenjamin"));
        }else
        {
            namaPenjamin.setText("");
        }

    }

    @Override
    public void onNext() {
        //Log.d(TAG, "onNext");
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

        return false;
    }

    @Override
    public String error() {
        return "Mohon lengkapi data";
    }

    private void saveData()
    {
        Bundle identitas_diri = getStepDataFor(1);
        Bundle identitas_keluarga = getStepDataFor(2);
        Bundle identitas_alamat = getStepDataFor(3);
        Bundle identitas_jenisPembayaran = getStepDataFor(4);

        String noID = identitas_diri.getString("noID");

        String noRekamMedik = "";
        String type = identitas_diri.getString("type");
        if(type.equals("Pasien Lama"))
        {
            noRekamMedik = identitas_diri.getString("noRekamMedik");
        }
        String wargaNegara = identitas_diri.getString("wargaNegara");
        String noTelp = identitas_diri.getString("noTelp");
        String nama = identitas_diri.getString("nama");
        String tempatLahir = identitas_diri.getString("tempatLahir");
        String tanggalLahir = identitas_diri.getString("tanggalLahir");
        String jenisKelamin = identitas_diri.getString("jenisKelamin");
        String agama = identitas_diri.getString("agama");
        String pendidikan = identitas_diri.getString("pendidikan");
        String pekerjaan = identitas_diri.getString("pekerjaan");
        String golonganDarah = identitas_diri.getString("golonganDarah");
        String statusMarital = identitas_keluarga.getString("statusMarital");

        String namaPasutri = "";
        if(statusMarital.equals("Sudah Kawin"))
        {
            namaPasutri = (identitas_keluarga.getString("namaPasutri"));
        }else{
            namaPasutri = "";
        }

        String namaAyah = identitas_keluarga.getString("namaAyah");
        String namaIbu = identitas_keluarga.getString("namaIbu");
        String alamat = identitas_alamat.getString("alamat");
        String provinsi = identitas_alamat.getString("provinsi");
        String kota = identitas_alamat.getString("kota");
        String kecamatan = identitas_alamat.getString("kecamatan");
        String desa = identitas_alamat.getString("desa");
        String jenisPembayaran = identitas_jenisPembayaran.getString("jenisPembayaran");
        String namaPenjamin = identitas_jenisPembayaran.getString("namaPenjamin");

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);


        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = R.string.ip_api + "/pasien/" + getArguments().getInt("pasien_id");

        JSONObject object = new JSONObject();
        try {
            object.put("noID", noID);
            object.put("noRekamMedik", noRekamMedik);
            object.put("nama", nama);
            object.put("type", type);
            object.put("tempatLahir", tempatLahir);
            object.put("tanggalLahir", tanggalLahir);
            object.put("wargaNegara", wargaNegara);
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
            if(identitas_jenisPembayaran.getString("jenisPembayaran").equals("Asuransi")) {
                String asuransi_id = identitas_jenisPembayaran.getString("asuransi_id");
                object.put("asuransi_id", asuransi_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang proses mengubah pasien...");
        progressDialog.show();

        final Activity activity = this.getActivity();
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        EventBus.getDefault().post(new MessageEvent("editPasien", 0));

                        Toast.makeText(activity, "Pasien berhasil diubah", Toast.LENGTH_SHORT).show();

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        activity.finish();
                                    }
                                },
                                500);

                        //Log.d("SaveData - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
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
                                        user.refreshToken(jwTokenSP, activity);
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, mStepper.getBaseContext());
                            }
                        }

                        progressDialog.dismiss();
                        Toast.makeText(activity, "Pasien gagal diubah, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                       // Log.d("getDataFromToken - Error.Response", String.valueOf(error));
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

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

}
