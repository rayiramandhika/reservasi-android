package id.or.rspmibogor.rspmibogor;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.DotStepper;
import com.github.fcannizzaro.materialstepper.style.ProgressStepper;
import com.github.fcannizzaro.materialstepper.style.TabStepper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasKeluarga;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasPasien;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.Pembayaran;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.TempatTinggal;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditCheckingData;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditIdentitasDiri;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditIdentitasKeluarga;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditPembayaran;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditTempatTinggal;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;

public class PasienEditActivity extends DotStepper {
    private String TAG = "PasienEditActivity";

    int i = 1;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    JSONObject data;

    private String pasien_nama;
    private String pasien_umur;
    private String pasien_noRekamMedik;
    private Integer pasien_id;
    private String pasien_tempatLahir;
    private String pasien_tanggalLahir;
    private String pasien_noID;
    private String pasien_wargaNegara;
    private String pasien_noTelp;
    private String pasien_jenisKelamin;
    private String pasien_agama;
    private String pasien_pendidikan;
    private String pasien_pekerjaan;
    private String pasien_golonganDarah;


    private String pasien_statusMarital;
    private String pasien_namaPasutri;
    private String pasien_namaAyah;
    private String pasien_namaIbu;

    private String pasien_alamat;
    private String pasien_provinsi;
    private String pasien_kota;
    private String pasien_kecamatan;
    private String pasien_desa;

    private String pasien_type;
    private String pasien_jenisPembayaran;
    private String pasien_namaPenjamin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle b = getIntent().getExtras();
        pasien_id = b.getInt("id");
        pasien_noID = b.getString("noID");
        pasien_noRekamMedik = b.getString("noRekamMedik");
        pasien_nama = b.getString("nama");
        pasien_tempatLahir = b.getString("tempatLahir");
        pasien_tanggalLahir = b.getString("tanggalLahir");
        pasien_jenisKelamin= b.getString("jenisKelamin");
        pasien_umur = b.getString("umur");
        pasien_wargaNegara = b.getString("wargaNegara");
        pasien_noTelp = b.getString("noTelp");
        pasien_agama = b.getString("agama");
        pasien_pendidikan = b.getString("pendidikan");
        pasien_pekerjaan = b.getString("pekerjaan");
        pasien_golonganDarah = b.getString("golonganDarah");

        pasien_statusMarital = b.getString("statusMarital");
        pasien_namaPasutri = b.getString("namaPasutri");
        pasien_namaAyah = b.getString("namaAyah");
        pasien_namaIbu = b.getString("namaIbu");

        pasien_alamat = b.getString("alamat");
        pasien_provinsi = b.getString("provinsi");
        pasien_kota = b.getString("kota");
        pasien_kecamatan = b.getString("kecamatan");
        pasien_desa = b.getString("desa");

        pasien_jenisPembayaran = b.getString("jenisPembayaran");
        pasien_namaPenjamin = b.getString("namaPenjamin");
        pasien_type = b.getString("type");


        setErrorTimeout(1500);
        setTitle("Ubah Pasien");
        setStateAdapter();

        addStep(createFragment(new EditIdentitasDiri()));
        addStep(createFragment(new EditIdentitasKeluarga()));
        addStep(createFragment(new EditTempatTinggal()));
        addStep(createFragment(new EditPembayaran()));
        addStep(createFragment(new EditCheckingData()));

        EventBus.getDefault().register(this);

        super.onCreate(savedInstanceState);
    }




    private AbstractStep createFragment(AbstractStep fragment) {
        Log.d(TAG, "fragment: " + fragment);
        Bundle b = new Bundle();

        b.putInt("position", i++);
        b.putInt("pasien_id", pasien_id);


        b.putString("nama", pasien_nama);
        b.putString("tempatLahir", pasien_tempatLahir);
        b.putString("wargaNegara", pasien_wargaNegara);
        b.putString("noRekamMedik", pasien_noRekamMedik);
        b.putString("noID", pasien_noID);
        b.putString("umur", pasien_umur);
        b.putString("noTelp", pasien_noTelp);
        b.putString("agama", pasien_agama);
        b.putString("golonganDarah", pasien_golonganDarah);
        b.putString("pendidikan",pasien_pendidikan);
        b.putString("pekerjaan", pasien_pekerjaan);
        b.putString("jenisKelamin", pasien_jenisKelamin);
        b.putString("tanggalLahir", pasien_tanggalLahir);


        b.putString("statusMarital", pasien_statusMarital);
        b.putString("namaPasutri", pasien_namaPasutri);
        b.putString("namaAyah", pasien_namaAyah);
        b.putString("namaIbu", pasien_namaIbu);


        b.putString("alamat", pasien_alamat);
        b.putString("provinsi", pasien_provinsi);
        b.putString("kota", pasien_kota);
        b.putString("kecamatan", pasien_kecamatan);
        b.putString("desa", pasien_desa);


        b.putString("jenisPembayaran", pasien_jenisPembayaran);
        b.putString("namaPenjamin", pasien_namaPenjamin);
        b.putString("type", pasien_type);

        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        //Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());
    }


}
