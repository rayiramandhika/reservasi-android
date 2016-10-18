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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.or.rspmibogor.rspmibogor.DetailInbox;
import id.or.rspmibogor.rspmibogor.DetailJadwalDokter;
import id.or.rspmibogor.rspmibogor.DetailOrder;
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 22/08/16.
 */
public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;

    static Context context;
    List<Inbox> inboxList;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    public InboxAdapter(List<Inbox> inboxList, Context context) {
        this.context = context;
        this.inboxList = inboxList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType==TYPE_MOVIE){
            return new InboxHolder(inflater.inflate(R.layout.inbox_cardview,parent,false));
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
            ((InboxHolder)holder).bindData(inboxList.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if(inboxList.get(position) != null){
            return TYPE_MOVIE;
        }else{
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    /* VIEW HOLDERS */

    class InboxHolder extends RecyclerView.ViewHolder{

        private final TextView title;
        private final TextView desc;
        private final TextView tanggal;
        private final TextView unread;

        public InboxHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView judul = (TextView) v.findViewById(R.id.judul_inbox);
                    TextView desk = (TextView) v.findViewById(R.id.desc_inbox);
                    TextView read = (TextView) v.findViewById(R.id.unread_message);

                    judul.setTypeface(null , Typeface.NORMAL);
                    desk.setTypeface(null , Typeface.NORMAL);
                    read.setVisibility(View.INVISIBLE);

                    Inbox inbox =  inboxList.get(getAdapterPosition());

                    inbox.setRead(true);

                    Bundle b = new Bundle();
                    b.putString("id", inbox.getId().toString());

                    Intent intent = new Intent(context, DetailInbox.class);
                    intent.putExtras(b);
                    context.startActivity(intent);

                }
            });

            title = (TextView) v.findViewById(R.id.judul_inbox);
            desc = (TextView) v.findViewById(R.id.desc_inbox);
            tanggal = (TextView) v.findViewById(R.id.tanggalInbox);
            unread = (TextView) v.findViewById(R.id.unread_message);
        }

        void bindData(Inbox inbox){

            if(inbox.getRead() == false){
                title.setTypeface(null , Typeface.BOLD);
                desc.setTypeface(null , Typeface.BOLD);
                unread.setVisibility(View.VISIBLE);
            }else{
                title.setTypeface(null , Typeface.NORMAL);
                desc.setTypeface(null , Typeface.NORMAL);
                unread.setVisibility(View.GONE);
            }

            title.setText(inbox.getTitle());
            desc.setText(inbox.getDesc());
            tanggal.setText(inbox.getTanggal());
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
