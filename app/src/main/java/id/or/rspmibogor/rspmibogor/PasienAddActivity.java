package id.or.rspmibogor.rspmibogor;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.DotStepper;
import com.github.fcannizzaro.materialstepper.style.ProgressStepper;
import com.github.fcannizzaro.materialstepper.style.TabStepper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.CheckingData;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasKeluarga;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.IdentitasPasien;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.Pembayaran;
import id.or.rspmibogor.rspmibogor.Fragment.PasienAdd.TempatTinggal;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;


public class PasienAddActivity extends DotStepper {
    private final String TAG = "PasienAddActivity";
    int i = 1;

    ArrayList<String> listAsuransi;
    ArrayList<String> listAsuransiId;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setErrorTimeout(1500);
        setTitle("Tambah Pasien");
        setStateAdapter();

        Bundle b = getIntent().getExtras();
        listAsuransi = new ArrayList<String>();
        listAsuransiId = new ArrayList<String>();

        listAsuransi = b.getStringArrayList("asuransi");
        listAsuransiId = b.getStringArrayList("idAsuransi");


        addStep(createFragment(new IdentitasPasien()));
        addStep(createFragment(new IdentitasKeluarga()));
        addStep(createFragment(new TempatTinggal()));
        addStep(createFragment(new Pembayaran()));
        addStep(createFragment(new CheckingData()));

        EventBus.getDefault().register(this);

        super.onCreate(savedInstanceState);
    }

    private AbstractStep createFragment(AbstractStep fragment) {
        //Log.d(TAG, "fragment: " + fragment.name());

        Bundle b = new Bundle();
        b.putInt("position", i++);

        b.putStringArrayList("asuransi", listAsuransi);
        b.putStringArrayList("idAsuransi", listAsuransiId);

        fragment.setArguments(b);
        return fragment;
    }

    /*@Override
    public void onStart() {
        super.onStart();

    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        //Log.d(TAG, "onEvent - loaded - event: " + event.getPesan().toString());
    }
}
