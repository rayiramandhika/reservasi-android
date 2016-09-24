package id.or.rspmibogor.rspmibogor.Fragment.PasienEdit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 27/08/16.
 */
public class EditIdentitasKeluarga  extends AbstractStep {

    private final String TAG = "IdentitasKeluarga";

    private TextView namaPasutri;
    private TextView namaAyah;
    private TextView namaIbu;
    private MaterialBetterSpinner statusMarital;


    SharedPreferences sharedPreferences;
    String jwTokenSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_keluarga, container, false);
        namaPasutri = (TextView) v.findViewById(R.id.namaPasutri);
        namaAyah = (TextView) v.findViewById(R.id.namaAyah);
        namaIbu = (TextView) v.findViewById(R.id.namaIbu);
        statusMarital = (MaterialBetterSpinner) v.findViewById(R.id.statusMarital);

        initSpinner();

        namaPasutri.setText(getArguments().getString("namaPasutri"));
        namaAyah.setText(getArguments().getString("namaAyah"));
        namaIbu.setText(getArguments().getString("namaIbu"));
        statusMarital.setText(getArguments().getString("statusMarital"));

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public String name() {
        return "Identitas Keluarga";
    }

    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {

        //Log.d(TAG, "onNext");


        String nmPasutri = namaPasutri.getText().toString();
        String marital = statusMarital.getText().toString();
        String nmAyah = namaAyah.getText().toString();
        String nmIbu = namaIbu.getText().toString();

        int poss = this.getArguments().getInt("position");
        Bundle b = getStepDataFor(poss);

        b.putString("namaPasutri", nmPasutri);
        b.putString("statusMarital", marital);
        b.putString("namaAyah", nmAyah);
        b.putString("namaIbu", nmIbu);

    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }


    @Override
    public boolean nextIf() {

       // Log.d(TAG, "nextIf");

        Integer i = 0;

        String nmPasutri = namaPasutri.getText().toString();
        String marital = statusMarital.getText().toString();
        String nmAyah = namaAyah.getText().toString();
        String nmIbu = namaIbu.getText().toString();

        if (marital.isEmpty()){
            statusMarital.setError("Status Marital harus dipilih");
            i++;
        } else {
            statusMarital.setError(null);
        }

        if (nmPasutri.isEmpty() && marital.equals("Sudah Kawin")){
            namaPasutri.setError("Nama Pasutri harus diisi");
            i++;
        } else {
            namaPasutri.setError(null);
        }

        if (nmAyah.isEmpty()){
            namaAyah.setError("Nama Ayah harus diisi");
            i++;
        } else {
            namaAyah.setError(null);
        }

        if (nmIbu.isEmpty()){
            namaIbu.setError("Nama Ibu harus diisi");
            i++;
        } else{
            namaIbu.setError(null);
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
        ArrayAdapter<CharSequence> Marital = ArrayAdapter.createFromResource(this.getContext(),
                R.array.StatusMarital, android.R.layout.simple_dropdown_item_1line);
        Marital.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        statusMarital.setAdapter(Marital);

    }
}
