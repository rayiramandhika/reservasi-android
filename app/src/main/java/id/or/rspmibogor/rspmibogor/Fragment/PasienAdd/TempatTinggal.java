package id.or.rspmibogor.rspmibogor.Fragment.PasienAdd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.LoginActivity;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 27/08/16.
 */
public class TempatTinggal extends AbstractStep {
    private final String TAG = "TempatTinggal";

    private TextView alamat;
    private TextView provinsi;
    private TextView kota;
    private TextView desa;
    private TextView kecamatan;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_alamat, container, false);

        alamat = (TextView) v.findViewById(R.id.alamat);
        provinsi = (TextView) v.findViewById(R.id.provinsi);
        kota = (TextView) v.findViewById(R.id.kota);
        kecamatan = (TextView) v.findViewById(R.id.kecamatan);
        desa = (TextView) v.findViewById(R.id.desa);




        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public String name() {
        return "Identitas Tempat Tinggal";
    }


    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {
        Log.d(TAG, "onNext");

        String alamatTxt = alamat.getText().toString();
        String provinsiTxt = provinsi.getText().toString();
        String kotaTxt = kota.getText().toString();
        String kecTxt = kecamatan.getText().toString();
        String desaTxt = desa.getText().toString();

        int poss = this.getArguments().getInt("position");
        Bundle b = getStepDataFor(poss);

        b.putString("alamat", alamatTxt);
        b.putString("provinsi", provinsiTxt);
        b.putString("kota", kotaTxt);
        b.putString("kecamatan", kecTxt);
        b.putString("desa", desaTxt);
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    @Override
    public boolean nextIf() {

        Log.d(TAG, "nextIf");

        Integer i = 0;

        String alamatTxt = alamat.getText().toString();
        String provinsiTxt = provinsi.getText().toString();
        String kotaTxt = kota.getText().toString();
        String kecTxt = kecamatan.getText().toString();
        String desaTxt = desa.getText().toString();

        if(alamatTxt.isEmpty()){
            alamat.setError("Alamat harus diisi");
            i++;
        } else {
            alamat.setError(null);
        }

        if(provinsiTxt.isEmpty()){
            provinsi.setError("Provinsi harus diisi");
            i++;
        } else {
            provinsi.setError(null);
        }

        if(kotaTxt.isEmpty()){
            kota.setError("Kota harus diisi");
            i++;
        } else {
            kota.setError(null);
        }

        if(kecTxt.isEmpty()){
            kecamatan.setError("Kecamatan harus diisi");
            i++;
        } else {
            kecamatan.setError(null);
        }

        if(desaTxt.isEmpty()){
            desa.setError("Desa harus diisi");
            i++;
        } else {
            desa.setError(null);
        }

        if(i == 0) return true;
        return false;
    }

    @Override
    public String error() {
        return "Mohon lengkapi data";
    }

}
