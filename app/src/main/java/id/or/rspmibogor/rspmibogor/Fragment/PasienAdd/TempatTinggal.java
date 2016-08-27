package id.or.rspmibogor.rspmibogor.Fragment.PasienAdd;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 27/08/16.
 */
public class TempatTinggal extends AbstractStep {

    private TextView namaPasutri;
    private TextView namaAyah;
    private TextView namaIbu;
    private MaterialBetterSpinner statusMarital;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.identitas_keluarga, container, false);
        namaPasutri = (TextView) v.findViewById(R.id.namaPasutri);
        namaAyah = (TextView) v.findViewById(R.id.namaAyah);
        namaIbu = (TextView) v.findViewById(R.id.namaIbu);
        statusMarital = (MaterialBetterSpinner) v.findViewById(R.id.statusMarital);

        initSpinner();

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

    /**@Override
    public boolean isOptional() {
    return true;
    }**/


    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {
        System.out.println("onNext");
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    /**@Override
    public String optional() {
    return "You can skip";
    }

     @Override
     public boolean nextIf() {
     //return i > 1;
     }**/

    @Override
    public String error() {
        return "<b>You must click!</b> <small>this is the condition!</small>";
    }

    public void initSpinner()
    {
        ArrayAdapter<CharSequence> Marital = ArrayAdapter.createFromResource(this.getContext(),
                R.array.StatusMarital, android.R.layout.simple_dropdown_item_1line);
        Marital.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        statusMarital.setAdapter(Marital);

    }
}
