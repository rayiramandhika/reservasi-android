package id.or.rspmibogor.rspmibogor.Fragment.PasienAdd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 27/08/16.
 */
public class Pembayaran extends AbstractStep {

    private final String TAG = "Pembayaran";

    private TextView namaPenjamin;
    private MaterialBetterSpinner jenisPembayaran;
    private SearchableSpinner spinnerAsuransi;

    ArrayList<String> listAsuransi;
    ArrayList<String> listAsuransiId;

    String nmPenjamin = "";
    private TextView error_asuransi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_jenispembayaran, container, false);

        jenisPembayaran = (MaterialBetterSpinner) v.findViewById(R.id.jenisPembayaran);
        //namaPenjamin = (TextView) v.findViewById(R.id.namaPenjamin);
        error_asuransi = (TextView) v.findViewById(R.id.error_asuransi);
        spinnerAsuransi = (SearchableSpinner) v.findViewById(R.id.spinnerAsuransi);
        spinnerAsuransi.setTitle("Pilih Asuransi");

        listAsuransi = getArguments().getStringArrayList("asuransi");
        listAsuransiId = getArguments().getStringArrayList("idAsuransi");

        initSpinner();

        jenisPembayaran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "setOnItemClickListener - statusMarital position: " + position);
                //Log.d(TAG, "setOnItemClickListener - statusMarital id: " + id);

                if (position == 0)
                {
                    spinnerAsuransi.setEnabled(false);
                }else{
                    spinnerAsuransi.setEnabled(true);
                }
            }
        });

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
    public void onStepVisible() {

        String jnsPembayaran = jenisPembayaran.getText().toString();
        if (jnsPembayaran != null)
        {
            if(jnsPembayaran.equals("Tunai"))
            {
                spinnerAsuransi.setEnabled(false);
            }else {
                spinnerAsuransi.setEnabled(true);
            }
        }
    }

    @Override
    public void onNext() {
       // Log.d(TAG, "onNext");

        String idAsuransi = "";
        String jnsPembayaran = jenisPembayaran.getText().toString();
        if(jnsPembayaran.equals("Asuransi"))
        {
            if(spinnerAsuransi.getSelectedItemPosition() > 0){
                nmPenjamin = listAsuransi.get(spinnerAsuransi.getSelectedItemPosition());
                idAsuransi = listAsuransiId.get(spinnerAsuransi.getSelectedItemPosition());
            }
        }else{
            nmPenjamin = "";
            idAsuransi = "";
        }

        int poss = this.getArguments().getInt("position");
        Bundle b = getStepDataFor(poss);

        b.putString("jenisPembayaran", jnsPembayaran);
        b.putString("namaPenjamin", nmPenjamin);
        b.putString("asuransi_id", idAsuransi);

    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    @Override
    public boolean nextIf() {
       // Log.d(TAG, "nextIf");

        Integer i = 0;

        String jnsPembayaran = jenisPembayaran.getText().toString();
        if(spinnerAsuransi.getSelectedItemPosition() > 0){
            nmPenjamin = listAsuransi.get(spinnerAsuransi.getSelectedItemPosition());
        }

        if(jnsPembayaran.isEmpty()){
            jenisPembayaran.setError("Jenis Pembayaran harus dipilih");
            i++;
        } else {
            jenisPembayaran.setError(null);
        }

        if(nmPenjamin.isEmpty() && jnsPembayaran.equals("Asuransi")){
            error_asuransi.setVisibility(View.VISIBLE);
            i++;
        } else {
            error_asuransi.setVisibility(View.INVISIBLE);
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

        ArrayAdapter<String> layanan = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, listAsuransi);
        spinnerAsuransi.setAdapter(layanan);

    }


}
