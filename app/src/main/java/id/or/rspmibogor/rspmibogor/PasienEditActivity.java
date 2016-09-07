package id.or.rspmibogor.rspmibogor;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasKeluarga;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasPasien;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.Pembayaran;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.TempatTinggal;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditIdentitasDiri;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditIdentitasKeluarga;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditPembayaran;
import id.or.rspmibogor.rspmibogor.Fragment.PasienEdit.EditTempatTinggal;

public class PasienEditActivity extends DotStepper {
    private String TAG = "PasienEditActivity";

    int i = 1;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    Integer pasien_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        Bundle b = getIntent().getExtras();
        pasien_id = b.getInt("pasien_id");

        setErrorTimeout(1500);
        setTitle("Ubah Pasien");
        setStateAdapter();

        addStep(createFragment(new EditIdentitasDiri()));
        addStep(createFragment(new EditIdentitasKeluarga()));
        addStep(createFragment(new EditTempatTinggal()));
        addStep(createFragment(new EditPembayaran()));


        super.onCreate(savedInstanceState);
    }




    private AbstractStep createFragment(AbstractStep fragment) {
        Log.d(TAG, "fragment: " + fragment);
        Bundle b = new Bundle();

        b.putInt("position", i++);
        b.putInt("pasien_id", pasien_id);

        fragment.setArguments(b);
        return fragment;
    }

}
