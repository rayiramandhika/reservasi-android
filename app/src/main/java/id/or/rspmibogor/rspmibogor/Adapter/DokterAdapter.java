package id.or.rspmibogor.rspmibogor.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailJadwalDokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 20/08/16.
 */
public class DokterAdapter extends RecyclerView.Adapter<DokterAdapter.ViewHolder>  {
    private static final String TAG = "DokterAdapter";
    private Activity activity;


    List<Dokter> Dokter;

    public DokterAdapter(List<Dokter> dokter, Activity activity)
    {
        super();
        this.Dokter = dokter;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dokter_name;
        private final TextView layanan_name;
        private final ImageView foto;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dokter dokter =  Dokter.get(getPosition());

                    Bundle b = new Bundle();
                    b.putInt("id", dokter.getDokter_id());

                    Log.d(TAG, "dokter_id: " + dokter.getDokter_id());

                    Intent intent = new Intent(activity, DetailJadwalDokter.class);
                    intent.putExtras(b);
                    activity.startActivity(intent);
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                }
            });
            dokter_name = (TextView) v.findViewById(R.id.dokter_name);
            layanan_name = (TextView) v.findViewById(R.id.layanan_name);
            foto = (ImageView) v.findViewById(R.id.dokter_foto);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dokter_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Dokter dokter =  Dokter.get(position);

        viewHolder.dokter_name.setText(dokter.getDokter_name());
        viewHolder.layanan_name.setText(dokter.getLayanan_name());
    }

    @Override
    public int getItemCount() {
        return Dokter.size();
    }
}
