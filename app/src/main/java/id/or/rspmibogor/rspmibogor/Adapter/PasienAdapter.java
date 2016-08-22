package id.or.rspmibogor.rspmibogor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailInbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 22/08/16.
 */
public class PasienAdapter extends RecyclerView.Adapter<PasienAdapter.ViewHolder> {

    private static final String TAG = "PasienAdapter";
    private Activity activity;

    List<Pasien> Pasien;

    public PasienAdapter(List<Pasien> pasien, Activity activity)
    {
        super();
        this.Pasien = pasien;
        this.activity = activity;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pasien_name;
        private final TextView pasien_noRekamMedik;
        private final TextView pasien_umur;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /**Pasien pasien =  Pasien.get(getPosition());

                    Bundle b = new Bundle();
                    //b.putInt("id", Pasien.);

                    Intent intent = new Intent(Context, DetailInbox.class);
                    intent.putExtras(b);
                    Context.startActivity(intent);**/
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                }
            });
            pasien_name = (TextView) v.findViewById(R.id.pasien_name);
            pasien_noRekamMedik = (TextView) v.findViewById(R.id.pasien_noRekamMedik);
            pasien_umur = (TextView) v.findViewById(R.id.pasien_umur);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.pasien_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Pasien pasien =  Pasien.get(position);

        //viewHolder.title.setText(pasien.getTitle());
        viewHolder.pasien_name.setText(pasien.getPasien_name());
        viewHolder.pasien_noRekamMedik.setText(pasien.getPasien_noRekamMedik());
        viewHolder.pasien_umur.setText(pasien.getPasien_umur());

    }

    @Override
    public int getItemCount() {
        return Pasien.size();
    }


}
