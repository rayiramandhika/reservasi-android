package id.or.rspmibogor.rspmibogor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.or.rspmibogor.rspmibogor.GetterSetter.ListJadwal;
import id.or.rspmibogor.rspmibogor.PilihPasienActivity;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 26/08/16.
 */
public class ListJadwalAdapter extends RecyclerView.Adapter<ListJadwalAdapter.ViewHolder> {


    private static final String TAG = "ListJadwalAdapter";
    private Activity activity;

    List<ListJadwal> ListJadwal;

    public ListJadwalAdapter(List<ListJadwal> listJadwal, Activity activity)
    {
        super();
        this.ListJadwal = listJadwal;
        this.activity = activity;
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

                    if(listJadwal.getJadwal_kuota() == 0){
                        Toast.makeText(activity, "Mohon maaf kuota sudah habis.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String jam = listJadwal.getJadwal_jamMulai() + " - " + listJadwal.getJadwal_jamTutup();

                    Bundle b = new Bundle();
                    b.putInt("detailjadwal_id", listJadwal.getJadwal_id());
                    b.putInt("dokter_id", listJadwal.getDokter_id());
                    b.putString("dokter_name", listJadwal.getDokter_nama());
                    b.putInt("layanan_id", listJadwal.getLayanan_id());
                    b.putString("layanan_name", listJadwal.getLayanan_nama());
                    b.putString("tanggal", listJadwal.getJadwal_tanggal());
                    b.putString("hari", listJadwal.getJadwal_hari());
                    b.putString("jam", jam);

                    Intent intent = new Intent(activity, PilihPasienActivity.class);
                    intent.putExtras(b);
                    activity.startActivity(intent);
                    Log.d(TAG, "Element " + getPosition() + " clicked.");

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
            viewHolder.pesan.setText("Daftar");
            viewHolder.pesan.setPadding(16, 0, 16, 0);
        }
    }

    @Override
    public int getItemCount() {
        return ListJadwal.size();
    }

}
