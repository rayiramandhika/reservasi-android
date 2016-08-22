package id.or.rspmibogor.rspmibogor.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pasien_name;
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
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });
            pasien_name = (TextView) v.findViewById(R.id.name_pasien);
            dokter_name = (TextView) v.findViewById(R.id.name_dokter);
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
        //viewHolder.dokter_name.setText(oldOrder.getDokter_name());
        viewHolder.hari.setText(newOrder.getDetailjadwal_hari());
        viewHolder.jadwal.setText(newOrder.getDetailjadwal_jammulai() + " . " + newOrder.getDetailjadwal_jamtutup());
        viewHolder.no_antrian.setText(newOrder.getOrder_noUrut());
        viewHolder.tanggal.setText((CharSequence) newOrder.getOrder_tanggal());
    }

    @Override
    public int getItemCount() {
        return NewOrder.size();
    }

}
