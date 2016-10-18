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
import id.or.rspmibogor.rspmibogor.DetailOrderOld;
import id.or.rspmibogor.rspmibogor.GetterSetter.MessageEvent;
import id.or.rspmibogor.rspmibogor.GetterSetter.NewOrder;
import id.or.rspmibogor.rspmibogor.GetterSetter.OldOrder;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 18/08/16.
 */
public class OldOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;

    static Context context;
    List<OldOrder> oldOrders;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    /*
    * isLoading - to set the remote loading and complete status to fix back to back load more call
    * isMoreDataAvailable - to set whether more data from server available or not.
    * It will prevent useless load more request even after all the server data loaded
    * */


    public OldOrderAdapter(List<OldOrder> oldOrders, Context context) {
        this.context = context;
        this.oldOrders = oldOrders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType==TYPE_MOVIE){
            return new OldOrderHolder(inflater.inflate(R.layout.old_order_cardview,parent,false));
        }else{
            return new LoadHolder(inflater.inflate(R.layout.progress_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if(getItemViewType(position)==TYPE_MOVIE){
            ((OldOrderHolder)holder).bindData(oldOrders.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if(oldOrders.get(position) != null){
            return TYPE_MOVIE;
        }else{
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount() {
        return oldOrders.size();
    }

    /* VIEW HOLDERS */

    class OldOrderHolder extends RecyclerView.ViewHolder{

        private final TextView pasien_name;
        private final TextView dokter_name;
        private final TextView layanan_name;
        private final TextView hari;
        private final TextView jadwal;
        private final TextView tanggal;

        public OldOrderHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OldOrder oldOrder = oldOrders.get(getAdapterPosition());

                    Bundle b = new Bundle();
                    b.putString("id", oldOrder.getOrder_id().toString());

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
            tanggal = (TextView) v.findViewById(R.id.tanggal);
        }

        void bindData(OldOrder oldOrder){

            pasien_name.setText(oldOrder.getPasien_name());
            hari.setText(oldOrder.getDetailjadwal_hari());
            jadwal.setText("Pkl. " + oldOrder.getDetailjadwal_jammulai() + " . " + oldOrder.getDetailjadwal_jamtutup());
            dokter_name.setText(oldOrder.getDokter_name());
            layanan_name.setText(oldOrder.getLayanan_name());
            tanggal.setText(oldOrder.getOrder_tanggal());
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public Boolean loadingStatus()
    {
        return this.isLoading;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}