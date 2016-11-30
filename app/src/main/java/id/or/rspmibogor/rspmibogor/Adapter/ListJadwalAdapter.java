package id.or.rspmibogor.rspmibogor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailJadwalDokter;
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

    final AlertDialog.Builder builder;

    public ListJadwalAdapter(List<ListJadwal> listJadwal, Activity activity)
    {
        super();
        this.ListJadwal = listJadwal;
        this.activity = activity;

        builder = new AlertDialog.Builder(activity);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView hari, tanggal, jam, kuota, pesan, keterangan;



        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ListJadwal listJadwal =  ListJadwal.get(getPosition());

                    String status = listJadwal.getJadwal_status();
                    String hari = listJadwal.getJadwal_hari();
                    if(status.equals("cuti"))
                    {
                        builder.setTitle("Mohon maaf")
                                .setMessage("\nPada jadwal tersebut " + listJadwal.getKeteranganCuti() +".")
                                .setNegativeButton(android.R.string.yes, null).show();
                        return;
                    }else{
                        if(listJadwal.getJadwal_sisaKuota().equals(0)){
                            builder.setTitle("Kuota Penuh")
                                    .setMessage("Mohon maaf.\nPada hari " + hari + " Pendaftaran via online sudah penuh. Silahkan melakukan pendaftaran secara offline di RS PMI Bogor.")
                                    .setNegativeButton(android.R.string.yes, null).show();
                            return;
                        }
                    }


                    String jam = listJadwal.getJadwal_jamMulai() + " - " + listJadwal.getJadwal_jamTutup();

                    Bundle b = new Bundle();
                    b.putInt("detailjadwal_id", listJadwal.getJadwal_id());
                    b.putInt("dokter_id", listJadwal.getDokter_id());
                    b.putString("dokter_name", listJadwal.getDokter_nama());
                    b.putInt("layanan_id", listJadwal.getLayanan_id());
                    b.putString("layanan_name", listJadwal.getLayanan_nama());
                    b.putInt("poliklinik_id", listJadwal.getPoliklinik_id());
                    b.putString("poliklinik_name", listJadwal.getPoliklinik_nama());
                    b.putString("tanggal", listJadwal.getJadwal_tanggal());
                    b.putString("hari", listJadwal.getJadwal_hari());
                    b.putString("jam", jam);

                    Intent intent = new Intent(activity, PilihPasienActivity.class);
                    intent.putExtras(b);
                    activity.startActivity(intent);
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                }
            });
            hari = (TextView) v.findViewById(R.id.hari);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
            jam = (TextView) v.findViewById(R.id.jam);
            kuota = (TextView) v.findViewById(R.id.kuota);
            pesan = (TextView) v.findViewById(R.id.pesan);
            keterangan = (TextView) v.findViewById(R.id.keterangan);
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
        viewHolder.jam.setText("Pkl. " + listJadwal.getJadwal_jamMulai() + " - " + listJadwal.getJadwal_jamTutup());
        viewHolder.kuota.setText("Kuota Tersedia: " + listJadwal.getJadwal_sisaKuota() + " dari " + listJadwal.getJadwal_kuota());

        String keterangan = listJadwal.getKeterangan();

        if(keterangan.isEmpty()) viewHolder.keterangan.setText("");
        else viewHolder.keterangan.setText("( " + listJadwal.getKeterangan() + " )");

        int kuota = listJadwal.getJadwal_sisaKuota();

        String status = listJadwal.getJadwal_status();

        if(status.equals("cuti"))
        {
            viewHolder.pesan.setBackgroundResource(R.drawable.bagde_oval_soldout);
            viewHolder.pesan.setText(listJadwal.getKeteranganCuti());
            viewHolder.pesan.setPadding(16, 5, 16, 5);

        }else{

            if(kuota == 0){
                viewHolder.pesan.setBackgroundResource(R.drawable.bagde_oval_soldout);
                viewHolder.pesan.setText("Pendaftaran via Online Penuh");

                viewHolder.pesan.setPadding(16, 5, 16, 5);
            }else{
                viewHolder.pesan.setBackgroundResource(R.drawable.badge_oval);
                viewHolder.pesan.setText("Daftar");
                viewHolder.pesan.setPadding(16, 5, 16, 5);
            }

        }


    }

    @Override
    public int getItemCount() {
        return ListJadwal.size();
    }

}
