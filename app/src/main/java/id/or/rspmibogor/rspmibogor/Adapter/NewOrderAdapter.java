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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailInbox;
import id.or.rspmibogor.rspmibogor.DetailOrder;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 18/08/16.
 */
public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.ViewHolder> {
    private static final String TAG = "NewOrderAdpater";

    private Context context;


    List<NewOrder> NewOrder;

    public NewOrderAdapter(List<NewOrder> newOrder, Context context)
    {
        super();
        this.NewOrder = newOrder;
        this.context = context;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pasien_name;
        private final TextView dokter_name;
        private final TextView layanan_name;
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

                    NewOrder newOrder = NewOrder.get(getPosition());

                    Bundle b = new Bundle();
                    b.putInt("id", newOrder.getOrder_id());
                    b.putInt("position_list", getPosition());

                    Intent intent = new Intent(context, DetailOrder.class);
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
                .inflate(R.layout.new_order_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        NewOrder newOrder =  NewOrder.get(position);

        viewHolder.pasien_name.setText(newOrder.getPasien_name());
        viewHolder.hari.setText(newOrder.getDetailjadwal_hari());
        viewHolder.jadwal.setText(newOrder.getDetailjadwal_jammulai() + " . " + newOrder.getDetailjadwal_jamtutup());
        viewHolder.no_antrian.setText(String.valueOf(newOrder.getOrder_noUrut()));
        viewHolder.dokter_name.setText(newOrder.getDokter_name());
        viewHolder.layanan_name.setText(newOrder.getLayanan_name());
        viewHolder.tanggal.setText(newOrder.getOrder_tanggal());
    }

    @Override
    public int getItemCount() {
        return NewOrder.size();
    }

}
