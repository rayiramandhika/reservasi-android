package id.or.rspmibogor.rspmibogor.Fragment.PasienEdit;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 05/09/16.
 */
public class EditIdentitasDiri extends AbstractStep {
    private final String TAG = "IdentitasPasien";

    private TextView nama;
    private TextView tempatLahir;
    private TextView umur;
    private TextView noID;
    private TextView wargaNegara;
    private TextView noRekamMedik;
    private TextView noTelp;
    private MaterialBetterSpinner tanggalLahir;
    private MaterialBetterSpinner bulanLahir;
    private MaterialBetterSpinner tahunLahir;
    private MaterialBetterSpinner agama;
    private MaterialBetterSpinner pendidikan;
    private MaterialBetterSpinner pekerjaan;
    private MaterialBetterSpinner golonganDarah;
    private RadioGroup jenisKelaminGroup;
    private RadioButton rgLaki;
    private RadioButton rgPerempuan;
    private RadioButton jenisKelaminRadio;

    SharedPreferences sharedPreferences;
    String jwTokenSP;


    JSONObject data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_diri, container, false);
        nama = (TextView) v.findViewById(R.id.nama);
        tempatLahir = (TextView) v.findViewById(R.id.tempatLahir);
        agama = (MaterialBetterSpinner) v.findViewById(R.id.agama);
        umur = (TextView) v.findViewById(R.id.umur);
        wargaNegara = (TextView) v.findViewById(R.id.wargaNegara);
        noRekamMedik = (TextView) v.findViewById(R.id.noRekamMedik);
        noID = (TextView) v.findViewById(R.id.noID);
        noTelp = (TextView) v.findViewById(R.id.noTelp);
        pendidikan = (MaterialBetterSpinner) v.findViewById(R.id.pendidikan);
        pekerjaan = (MaterialBetterSpinner) v.findViewById(R.id.pekerjaan);
        golonganDarah = (MaterialBetterSpinner) v.findViewById(R.id.golonganDarah);
        tanggalLahir = (MaterialBetterSpinner) v.findViewById(R.id.tanggalLahir);
        bulanLahir = (MaterialBetterSpinner) v.findViewById(R.id.bulanLahir);
        tahunLahir = (MaterialBetterSpinner) v.findViewById(R.id.tahunLahir);
        jenisKelaminGroup = (RadioGroup) v.findViewById(R.id.jenisKelamin);

        rgLaki = (RadioButton) v.findViewById(R.id.lakiLaki);
        rgPerempuan = (RadioButton) v.findViewById(R.id.perempuan);

        int selectedId = jenisKelaminGroup.getCheckedRadioButtonId();
        jenisKelaminRadio = (RadioButton) v.findViewById(selectedId);

        initSpinner();

        Toolbar toolbar = mStepper.getToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_48px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        sharedPreferences = getActivity().getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);

        String url = "http://103.43.44.211:1337/v1/pasien/" + getArguments().getInt("pasien_id");
        //spinner.setVisibility(View.VISIBLE);
        JsonObjectRequest req = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loading.dismiss();
                        //spinner.setVisibility(View.GONE);

                        try {
                            data = response.getJSONObject("data");

                            nama.setText(data.getString("nama"));
                            tempatLahir.setText(data.getString("tempatLahir"));
                            wargaNegara.setText(data.getString("wargaNegara"));
                            noRekamMedik.setText(data.getString("noRekamMedik"));
                            noID.setText(data.getString("noID"));
                            umur.setText(data.getString("umur"));
                            noTelp.setText(data.getString("noTelp"));
                            agama.setText(data.getString("agama"));
                            golonganDarah.setText(data.getString("golonganDarah"));
                            pendidikan.setText(data.getString("pendidikan"));
                            pekerjaan.setText(data.getString("pekerjaan"));

                            String date = data.getString("tanggalLahir");
                            String[] split = date.split("-");

                            if(split.length > 0)
                            {
                                tanggalLahir.setText(split[0]);
                                bulanLahir.setText(split[1]);
                                tahunLahir.setText(split[2]);
                            }

                            String jl = data.getString("jenisKelamin");
                            Log.d(TAG, "jenis kelamin" + jl);
                            if(jl == "Laki-laki") rgLaki.setChecked(true);
                            else if(jl == "Perempuan") rgPerempuan.setChecked(true);

                            Log.d(TAG, "onResponse - pasien" + data.toString());

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
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + jwTokenSP);
                return params;
            }
        };

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());

        //Adding request to the queue
        requestQueue.add(req);


        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public String name() {
        return "Identitas Diri";
    }

    @Override
    public void onNext() {
        Log.d(TAG, "onNext");

        int poss = this.getArguments().getInt("position");
        Bundle b = getStepDataFor(poss);

        String namaTxt = nama.getText().toString();
        String noIDTxt = noID.getText().toString();
        String noRekamMdk = noRekamMedik.getText().toString();
        String wnTxt = wargaNegara.getText().toString();
        String umurTxt = umur.getText().toString();
        String tmptLahirTxt = tempatLahir.getText().toString();
        String tglLahirTxt = tanggalLahir.getText().toString();
        String blnLahirTxt = bulanLahir.getText().toString();
        String thnLahirTxt = tahunLahir.getText().toString();
        String jlTxt = jenisKelaminRadio.getText().toString();
        String noTelpTxt = noTelp.getText().toString();
        String agamaTxt = agama.getText().toString();
        String pendidikanTxt = agama.getText().toString();
        String pekerjaanTxt = pekerjaan.getText().toString();
        String gdTxt = golonganDarah.getText().toString();

        String tanggalLahir =  thnLahirTxt + '-' + blnLahirTxt + '-' + tglLahirTxt;

        b.putInt("position", poss);
        b.putString("nama", namaTxt);
        b.putString("noID", noIDTxt);
        b.putString("umur", umurTxt);
        b.putString("noTelp", noTelpTxt);
        b.putString("noRekamMedik", noRekamMdk);
        b.putString("wargaNegara", wnTxt);
        b.putString("tempatLahir", tmptLahirTxt);
        b.putString("tanggalLahir", tanggalLahir);
        b.putString("jenisKelamin", jlTxt);
        b.putString("agama", agamaTxt);
        b.putString("pendidikan", pendidikanTxt);
        b.putString("pekerjaan", pekerjaanTxt);
        b.putString("golonganDarah", gdTxt);
        //set data for other step


    }

    @Override
    public void onPrevious() {
        Log.d(TAG, "onPrevious");
    }


    @Override
    public boolean nextIf() {
        Log.d(TAG, "nextIf");

        Integer i = 0;

        String namaTxt = nama.getText().toString();
        String tmptLahirTxt = tempatLahir.getText().toString();
        String tglLahirTxt = tanggalLahir.getText().toString();
        String blnLahirTxt = bulanLahir.getText().toString();
        String thnLahirTxt = tahunLahir.getText().toString();
        String jlTxt = jenisKelaminRadio.getText().toString();
        String noIDTxt = noID.getText().toString();
        String wnTxt = wargaNegara.getText().toString();
        String umurTxt = umur.getText().toString();
        String agamaTxt = agama.getText().toString();
        String pendidikanTxt = pendidikan.getText().toString();
        String pekerjaanTxt = pekerjaan.getText().toString();
        String gdTxt = golonganDarah.getText().toString();


        if (namaTxt.isEmpty()){
            nama.setError("Nama Harus diisi");
            i++;
        } else {
            nama.setError(null);
        }

        if (noIDTxt.isEmpty()){
            noID.setError("No. ID harus diisi");
            i++;
        } else {
            noID.setError(null);
        }

        if (wnTxt.isEmpty()){
            wargaNegara.setError("No. ID harus diisi");
            i++;
        } else {
            wargaNegara.setError(null);
        }

        if (umurTxt.isEmpty()){
            umur.setError("Umur harus diisi");
            i++;
        } else {
            umur.setError(null);
        }

        if (tmptLahirTxt.isEmpty()){
            tempatLahir.setError("Tempat Lahir harus diisi");
            i++;
        } else {
            tempatLahir.setError(null);
        }

        if (tglLahirTxt.isEmpty()){
            tanggalLahir.setError("Tanggal Lahir harus diisi");
            i++;
        } else {
            tanggalLahir.setError(null);
        }

        if (blnLahirTxt.isEmpty()){
            bulanLahir.setError("Bulan Lahir harus diisi");
            i++;
        } else {
            bulanLahir.setError(null);
        }

        if (thnLahirTxt.isEmpty()){
            tahunLahir.setError("Tahun Lahir harus diisi");
            i++;
        } else {
            tahunLahir.setError(null);
        }

        if(jlTxt.isEmpty()){
            jenisKelaminRadio.setError("Jenis Kelamin harus dipilih");
            i++;
        } else {
            jenisKelaminRadio.setError(null);
        }

        if(agamaTxt.isEmpty()){
            agama.setError("Agama harus dipilih");
            i++;
        } else {
            agama.setError(null);
        }

        if(pendidikanTxt.isEmpty()){
            pendidikan.setError("Pendidikan harus dipilih");
            i++;
        } else {
            pendidikan.setError(null);
        }

        if(pekerjaanTxt.isEmpty()){
            pekerjaan.setError("Pekerjaan harus dipilih");
            i++;
        } else {
            pekerjaan.setError(null);
        }

        if(gdTxt.isEmpty()){
            golonganDarah.setError("Golongan Darah harus dipilih");
            i++;
        } else {
            golonganDarah.setError(null);
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
        ArrayAdapter<CharSequence> agamaAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.Agama, android.R.layout.simple_dropdown_item_1line);
        agamaAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        agama.setAdapter(agamaAdapter);

        ArrayAdapter<CharSequence> pendidikanAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.pendidikan, android.R.layout.simple_dropdown_item_1line);
        pendidikanAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        pendidikan.setAdapter(pendidikanAdapter);

        ArrayAdapter<CharSequence> pekerjaanAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.pekerjaan, android.R.layout.simple_dropdown_item_1line);
        pekerjaanAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        pekerjaan.setAdapter(pekerjaanAdapter);

        ArrayAdapter<CharSequence> golonganDarahAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.golonganDarah, android.R.layout.simple_dropdown_item_1line);
        golonganDarahAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        golonganDarah.setAdapter(golonganDarahAdapter);

        ArrayAdapter<CharSequence> tanggalLahirAdapter = new ArrayAdapter<CharSequence>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line);

        for (int i = 1; i <= 31; i++)
        {
            tanggalLahirAdapter.add(String.valueOf(i));
        }

        tanggalLahirAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tanggalLahir.setAdapter(tanggalLahirAdapter);

        // Initializing a String Array
        String[] Bulan = new String[]{
                "Januari",
                "Febuari",
                "Maret",
                "April",
                "Mei",
                "Juni",
                "Juli",
                "Agustus",
                "September",
                "Oktober",
                "November",
                "Desember"

        };

        final List<String> bulanList = new ArrayList<>(Arrays.asList(Bulan));

        ArrayAdapter<String> bulanLahirAdapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, bulanList);
        bulanLahirAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        bulanLahir.setAdapter(bulanLahirAdapter);

        ArrayAdapter<CharSequence> tahunLahirAdapter = new ArrayAdapter<CharSequence>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line);

        for (int i = 2016; i >= 1900; i--)
        {
            tahunLahirAdapter.add(String.valueOf(i));
        }

        tahunLahirAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tahunLahir.setAdapter(tahunLahirAdapter);

    }
}
