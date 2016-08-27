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
import id.or.rspmibogor.rspmibogor.GetterSetter.Dokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 22/08/16.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {


    private static final String TAG = "DokterAdapter";
    private Context Context;


    List<Inbox> Inbox;

    public InboxAdapter(List<Inbox> inbox, Context context)
    {
        super();
        this.Inbox = inbox;
        this.Context = context;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView desc;
        private final TextView tanggal;
        private final TextView unread;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Inbox inbox =  Inbox.get(getPosition());

                    Bundle b = new Bundle();
                    b.putInt("id", inbox.getId());

                    Intent intent = new Intent(Context, DetailInbox.class);
                    intent.putExtras(b);
                    Context.startActivity(intent);
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                }
            });
            title = (TextView) v.findViewById(R.id.judul_inbox);
            desc = (TextView) v.findViewById(R.id.desc_inbox);
            tanggal = (TextView) v.findViewById(R.id.tanggalInbox);
            unread = (TextView) v.findViewById(R.id.unread_message);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.inbox_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Inbox inbox =  Inbox.get(position);

        if(inbox.getRead() == false){
            viewHolder.title.setTypeface(null , Typeface.BOLD);
            viewHolder.desc.setTypeface(null , Typeface.BOLD);
            viewHolder.unread.setVisibility(View.VISIBLE);
        }else{
            viewHolder.unread.setVisibility(View.GONE);
        }

        viewHolder.title.setText(inbox.getTitle());
        viewHolder.desc.setText(inbox.getDesc());
        viewHolder.tanggal.setText(inbox.getTanggal());
    }

    @Override
    public int getItemCount() {
        return Inbox.size();
    }


}
