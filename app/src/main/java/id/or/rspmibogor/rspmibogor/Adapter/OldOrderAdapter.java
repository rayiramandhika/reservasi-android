package id.or.rspmibogor.rspmibogor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailOrder;
import id.or.rspmibogor.rspmibogor.DetailOrderOld;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.R;
import id.or.rspmibogor.rspmibogor.GetterSetter.OldOrder;

/**
 * Created by iqbalprabu on 18/08/16.
 */
public class OldOrderAdapter extends RecyclerView.Adapter<OldOrderAdapter.ViewHolder> {
    private static final String TAG = "OldOrderAdpater";

    private Context context;


    List<OldOrder> OldOrder;

    public OldOrderAdapter(List<OldOrder> oldOrder, Context context)
    {
        super();
        this.OldOrder = oldOrder;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pasien_name;
        private final TextView layanan_name;
        private final TextView dokter_name;
        private final TextView hari;
        private final TextView jadwal;
        private final TextView no_antrian;
        private final TextView tanggal;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OldOrder oldOrder = OldOrder.get(getPosition());

                    Bundle b = new Bundle();
                    b.putInt("id", oldOrder.getOrder_id());

                    Intent intent = new Intent(context, DetailOrderOld.class);
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
            });
            pasien_name = (TextView) v.findViewById(R.id.name_pasien);
            dokter_name = (TextView) v.findViewById(R.id.name_dokter);
            layanan_name = (TextView) v.findViewById(R.id.name_layanan);
            hari = (TextView) v.findViewById(R.id.hari);
            jadwal = (TextView) v.findViewById(R.id.jam);
            no_antrian = (TextView) v.findViewById(R.id.no_antrian);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.old_order_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        OldOrder oldOrder =  OldOrder.get(position);

        viewHolder.pasien_name.setText(oldOrder.getPasien_name());
        viewHolder.hari.setText(oldOrder.getDetailjadwal_hari());
        viewHolder.jadwal.setText(oldOrder.getDetailjadwal_jammulai() + " . " + oldOrder.getDetailjadwal_jamtutup());
        viewHolder.no_antrian.setText(oldOrder.getOrder_noUrut());
        viewHolder.dokter_name.setText(oldOrder.getDokter_name());
        viewHolder.layanan_name.setText(oldOrder.getLayanan_name());
        viewHolder.tanggal.setText(oldOrder.getOrder_tanggal());
    }

    @Override
    public int getItemCount() {
        return OldOrder.size();
    }

}
