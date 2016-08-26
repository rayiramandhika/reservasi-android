package id.or.rspmibogor.rspmibogor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailInbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.ListJadwal;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 26/08/16.
 */
public class ListJadwalAdapter extends RecyclerView.Adapter<ListJadwalAdapter.ViewHolder> {


    private static final String TAG = "ListJadwalAdapter";
    private android.content.Context Context;

    List<ListJadwal> ListJadwal;

    public ListJadwalAdapter(List<ListJadwal> listJadwal, Context context)
    {
        super();
        this.ListJadwal = listJadwal;
        this.Context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView hari;
        private final TextView tanggal;
        private final TextView jam;
        private final TextView kuota;
        private final TextView pesan;



        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ListJadwal listJadwal =  ListJadwal.get(getPosition());

                    Bundle b = new Bundle();
                    //b.putInt("id", inbox.getId());

                    //Intent intent = new Intent(Context, DetailInbox.class);
                    //intent.putExtras(b);
                    //Context.startActivity(intent);
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                }
            });
            hari = (TextView) v.findViewById(R.id.hari);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
            jam = (TextView) v.findViewById(R.id.jam);
            kuota = (TextView) v.findViewById(R.id.kuota);
            pesan = (TextView) v.findViewById(R.id.pesan);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.jadwal_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ListJadwal listJadwal =  ListJadwal.get(position);

        viewHolder.hari.setText(listJadwal.getJadwal_hari());
        viewHolder.tanggal.setText(listJadwal.getJadwal_tanggal());
        viewHolder.jam.setText(listJadwal.getJadwal_jamMulai() + " - " + listJadwal.getJadwal_jamTutup());
        viewHolder.kuota.setText("Kuota Tersedia: " + listJadwal.getJadwal_kuota());

        if(listJadwal.getJadwal_kuota() == 0){
            viewHolder.pesan.setBackgroundResource(R.drawable.bagde_oval_soldout);
            viewHolder.pesan.setText("Kuota Habis");
            viewHolder.pesan.setPadding(16, 0, 16, 0);
        }else{
            viewHolder.pesan.setBackgroundResource(R.drawable.badge_oval);
            viewHolder.pesan.setText("Pesan");
            viewHolder.pesan.setPadding(16, 0, 16, 0);
        }
    }

    @Override
    public int getItemCount() {
        return ListJadwal.size();
    }

}
