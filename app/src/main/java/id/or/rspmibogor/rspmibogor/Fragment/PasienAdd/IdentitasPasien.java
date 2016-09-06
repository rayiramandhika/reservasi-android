package id.or.rspmibogor.rspmibogor.Fragment.PasienAdd;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 27/08/16.
 */
public class IdentitasPasien extends AbstractStep {
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
    private RadioButton jenisKelaminRadio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_diri, container, false);

        Toolbar toolbar = mStepper.getToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_48px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

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

        int selectedId = jenisKelaminGroup.getCheckedRadioButtonId();
        jenisKelaminRadio = (RadioButton) v.findViewById(selectedId);

        initSpinner();


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
        String pendidikanTxt = pendidikan.getText().toString();
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
        String pendidikanTxt = agama.getText().toString();
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
                "01",
                "02",
                "03",
                "04",
                "05",
                "06",
                "07",
                "08",
                "09",
                "10",
                "11",
                "12"

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
