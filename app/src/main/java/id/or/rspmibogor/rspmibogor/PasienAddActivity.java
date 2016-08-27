package id.or.rspmibogor.rspmibogor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.TabStepper;

import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasKeluarga;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasPasien;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.Pembayaran;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.TempatTinggal;


public class PasienAddActivity extends TabStepper {

    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setErrorTimeout(1500);
        setLinear(false);
        setTitle("Tambah Pasien");

        setAlternativeTab(false);
        setDisabledTouch();
        setPreviousVisible();

        addStep(createFragment(new IdentitasPasien()));
        addStep(createFragment(new IdentitasKeluarga()));
        addStep(createFragment(new TempatTinggal()));
        addStep(createFragment(new Pembayaran()));

        Toolbar toolbar = getToolbar();
        if(toolbar == null) Log.d("toolbar","null");
        //Log.d("PassienAddActivity", "toolbar: " + toolbar.toString());

        super.onCreate(savedInstanceState);
    }

    private AbstractStep createFragment(AbstractStep fragment) {
        Bundle b = new Bundle();
        b.putInt("position", i++);
        fragment.setArguments(b);
        return fragment;
    }

}
