package id.or.rspmibogor.rspmibogor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.DetailInbox;
import id.or.rspmibogor.rspmibogor.DetailJadwalDokter;
import id.or.rspmibogor.rspmibogor.GetterSetter.Inbox;
import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;
import id.or.rspmibogor.rspmibogor.PasienEditActivity;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by iqbalprabu on 22/08/16.
 */
public class PasienAdapter extends RecyclerView.Adapter<PasienAdapter.ViewHolder> {

    private static final String TAG = "PasienAdapter";
    private Activity activity;

    SharedPreferences sharedPreferences;
    String jwTokenSP;

    final List<Pasien> Pasien;

    public PasienAdapter(List<Pasien> pasien, Activity activity)
    {
        super();
        this.Pasien = pasien;
        this.activity = activity;

        sharedPreferences = activity.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        jwTokenSP = sharedPreferences.getString("jwtToken", null);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pasien_name;
        private final TextView pasien_noRekamMedik;
        private final TextView pasien_umur;
        private final ImageView menuMore;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /**Pasien pasien =  Pasien.get(getPosition());

                    Bundle b = new Bundle();
                    //b.putInt("id", Pasien.);

                    Intent intent = new Intent(Context, DetailInbox.class);
                    intent.putExtras(b);
                    Context.startActivity(intent);**/
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                }
            });
            pasien_name = (TextView) v.findViewById(R.id.pasien_name);
            pasien_noRekamMedik = (TextView) v.findViewById(R.id.pasien_noRekamMedik);
            pasien_umur = (TextView) v.findViewById(R.id.pasien_umur);
            menuMore = (ImageView) v.findViewById(R.id.menu_pasien);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.pasien_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Pasien pasien =  Pasien.get(position);

        //viewHolder.title.setText(pasien.getTitle());
        viewHolder.pasien_name.setText(pasien.getPasien_name());
        viewHolder.pasien_noRekamMedik.setText(pasien.getPasien_noRekamMedik());
        viewHolder.pasien_umur.setText(pasien.getPasien_umur());
        viewHolder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, viewHolder.menuMore);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_pasien, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Integer pasien_id = pasien.getPasien_id();
                        String title = item.getTitle().toString();
                        switch (title){
                            case "Edit":
                                editPasien(pasien_id);
                                break;
                            case "Hapus":
                                deletePasien(pasien_id, position);
                                break;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

    }

    @Override
    public int getItemCount() {
        return Pasien.size();
    }


    private void deletePasien(final Integer id, final Integer position)
    {

        new AlertDialog.Builder(activity)
                .setTitle("Hapus Pasien")
                .setMessage("Apa kamu yakin akan menghapus pasien?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFromServer(id, position);
                    }})

                .setNegativeButton(android.R.string.no, null).show();
    }

    private void editPasien(Integer id)
    {
        Bundle b = new Bundle();
        b.putInt("pasien_id", id);

        Intent intent = new Intent(activity, PasienEditActivity.class);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    private void deleteFromServer(final Integer id, final Integer position)
    {
        Log.d(TAG, "position: "+ position);

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://103.43.44.211:1337/v1/pasien/" + id;

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Pasien pasien = Pasien.get(position);
                        Pasien.remove(pasien);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, Pasien.size());
                        //notifyDataSetChanged();

                        Toast.makeText(activity, "Pasien berhasil dihapus.", Toast.LENGTH_SHORT).show();
                        Log.d("deleteFromServer - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("deleteFromServer - Error.Response", String.valueOf(error));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + jwTokenSP);
                return params;
            }
        };

        queue.add(putRequest);
    }
}
