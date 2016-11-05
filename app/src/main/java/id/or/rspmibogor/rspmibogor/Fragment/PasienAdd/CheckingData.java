package id.or.rspmibogor.rspmibogor.Fragment.PasienAdd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 13/09/16.
 */
public class CheckingData extends AbstractStep {

    private final String TAG = "CheckingData";

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
        typePasien.setText(identitas_diri.getString("type"));
        wargaNegara.setText(identitas_diri.getString("wargaNegara"));
        noRekamMedik.setText(identitas_diri.getString("noRekamMedik"));
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
        namaPasutri.setText(identitas_keluarga.getString("namaPasutri"));
        namaAyah.setText(identitas_keluarga.getString("namaAyah"));
        namaIbu.setText(identitas_keluarga.getString("namaIbu"));
        alamat.setText(identitas_alamat.getString("alamat"));
        provinsi.setText( identitas_alamat.getString("provinsi"));
        kota.setText( identitas_alamat.getString("kota"));
        kecamatan.setText(identitas_alamat.getString("kecamatan"));
        desa.setText(identitas_alamat.getString("desa"));
        jenisPembayaran.setText(identitas_jenisPembayaran.getString("jenisPembayaran"));
        namaPenjamin.setText(identitas_jenisPembayaran.getString("namaPenjamin"));

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
        if(identitas_jenisPembayaran.getString("jenisPembayaran").equals("Asuransi"))
        {
            namaPenjamin.setText(identitas_jenisPembayaran.getString("namaPenjamin"));
        }else{
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
        String type = identitas_diri.getString("type");
        String wargaNegara = identitas_diri.getString("wargaNegara");
        String noRekamMedik = identitas_diri.getString("noRekamMedik");
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


        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://103.23.22.46:1337/v1/pasien";

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
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final Activity activity = this.getActivity();
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Pasien berhasil disimpan", Toast.LENGTH_SHORT).show();

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        activity.finish();
                                        EventBus.getDefault().post(new MessageEvent("addPasien", 0));
                                    }
                                },
                                1000);

                        //Log.d(TAG, "SaveData - Response" +response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, mStepper.getBaseContext());
                            }
                        }

                        progressDialog.dismiss();
                        Toast.makeText(activity, "Pasien Gagal disimpan", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "SaveData - Error.Response" + String.valueOf(error));
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
