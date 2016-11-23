package id.or.rspmibogor.rspmibogor.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.GetterSetter.Pasien;
import id.or.rspmibogor.rspmibogor.Models.User;
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
    private ProgressDialog progressDialog;
    final ArrayList<String> listAsuransi;
    final ArrayList<String> listAsuransiId;
    private Integer refreshToken = 0;


    public PasienAdapter(List<Pasien> pasien, Activity activity)
    {
        super();
        this.Pasien = pasien;
        this.activity = activity;

        listAsuransi = new ArrayList<String>();
        listAsuransiId = new ArrayList<String>();
        sharedPreferences = activity.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pasien_name;
        private final TextView pasien_noRekamMedik;
        private final TextView pasien_umur;
        private final ImageView menuMore;
        private final ImageView pasienFoto;
        private final RelativeLayout rltvMenuEditPasien;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            pasien_name = (TextView) v.findViewById(R.id.pasien_name);
            pasien_noRekamMedik = (TextView) v.findViewById(R.id.pasien_noRekamMedik);
            pasien_umur = (TextView) v.findViewById(R.id.pasien_umur);
            menuMore = (ImageView) v.findViewById(R.id.menu_pasien);
            pasienFoto = (ImageView) v.findViewById(R.id.pasien_foto);
            rltvMenuEditPasien = (RelativeLayout) v.findViewById(R.id.rltvMenuEditPasien);
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


        viewHolder.pasien_name.setText(pasien.getPasien_name());
        viewHolder.pasien_noRekamMedik.setText(pasien.getPasien_noRekamMedik());
        viewHolder.pasien_umur.setText(pasien.getPasien_umur());

        String jenisKelamin = pasien.getPasien_jenisKelamin();

        if(jenisKelamin == null) viewHolder.pasienFoto.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_pasien_pria));
        else if(jenisKelamin.equals("Laki-laki")) viewHolder.pasienFoto.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_pasien_pria));
        else viewHolder.pasienFoto.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_pasien_wanita));

        viewHolder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(activity, viewHolder.menuMore);

                popup.getMenuInflater()
                        .inflate(R.menu.menu_pasien, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Integer pasien_id = pasien.getPasien_id();
                        String title = item.getTitle().toString();
                        switch (title){
                            case "Edit":
                                editPasien(pasien);
                                break;
                            case "Hapus":
                                deletePasien(pasien_id, position);
                                break;
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        viewHolder.rltvMenuEditPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, viewHolder.menuMore);

                popup.getMenuInflater()
                        .inflate(R.menu.menu_pasien, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Integer pasien_id = pasien.getPasien_id();
                        String title = item.getTitle().toString();
                        switch (title){
                            case "Edit":
                                editPasien(pasien);
                                break;
                            case "Hapus":
                                deletePasien(pasien_id, position);
                                break;
                        }
                        return true;
                    }
                });

                popup.show();
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
                .setMessage("Apa Anda yakin akan menghapus pasien?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFromServer(id, position);
                    }})

                .setNegativeButton(android.R.string.no, null).show();
    }

    private void editPasien(final Pasien pasien)
    {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang memuat data...");
        progressDialog.show();

        listAsuransi.removeAll(listAsuransi);
        listAsuransiId.removeAll(listAsuransiId);

        sharedPreferences = activity.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://103.23.22.46:1337/v1/asuransi";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray data = response.getJSONArray("data");
                            parseDataAsuransi(data, pasien);
                        } catch (JSONException e) {
                            Toast.makeText(activity, "Gagal mengambil data, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Get Asuransi - Error get JSON Array: " + e.toString());
                        }

                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, "Get Asuransi - Error VolleyError: " + error.toString());
                        progressDialog.dismiss();
                        if(error instanceof NoConnectionError)
                        {
                            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                Log.d(TAG, "OS: " + Build.VERSION_CODES.JELLY_BEAN_MR2);
                                if(refreshToken <= 5)
                                {
                                    if(jwTokenSP != null){
                                        User user = new User();
                                        user.refreshToken(jwTokenSP, activity);
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, activity.getBaseContext());
                            }
                        }

                        Toast.makeText(activity, "Gagal memuat data, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                        //Log.d("deleteFromServer - Error.Response", String.valueOf(error));
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

        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

    private void parseDataAsuransi(JSONArray data, Pasien pasien) {

        listAsuransi.add(0, "");
        listAsuransiId.add(0, "");

        if(data.length() > 0)
        {
            for (int i = 0; i < data.length(); i++) {

                JSONObject json = null;
                try {

                    json = data.getJSONObject(i);
                    Integer idx = json.getInt("id");
                    String nama = json.getString("nama");

                    listAsuransi.add(nama);
                    listAsuransiId.add(idx.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //Log.d(TAG, "listAsuransi: " + listAsuransi.toString());

        final Bundle b = new Bundle();
        b.putInt("id", pasien.getPasien_id());
        b.putString("noID", pasien.getPasien_noID());
        b.putString("noRekamMedik", pasien.getPasien_noRekamMedik());
        b.putString("nama", pasien.getPasien_name());
        b.putString("tempatLahir", pasien.getPasien_tempatLahir());
        b.putString("tanggalLahir", pasien.getPasien_tanggalLahir());
        b.putString("jenisKelamin", pasien.getPasien_jenisKelamin());
        b.putString("umur", pasien.getPasien_umur());
        b.putString("wargaNegara", pasien.getPasien_wargaNegara());
        b.putString("noTelp", pasien.getPasien_noTelp());
        b.putString("agama", pasien.getPasien_agama());
        b.putString("pendidikan", pasien.getPasien_pendidikan());
        b.putString("pekerjaan", pasien.getPasien_pekerjaan());
        b.putString("golonganDarah", pasien.getPasien_golonganDarah());

        b.putString("statusMarital", pasien.getPasien_statusMarital());
        b.putString("namaPasutri", pasien.getPasien_namaPasutri());
        b.putString("namaAyah", pasien.getPasien_namaAyah());
        b.putString("namaIbu", pasien.getPasien_namaIbu());

        b.putString("alamat", pasien.getPasien_alamat());
        b.putString("provinsi", pasien.getPasien_provinsi());
        b.putString("kota", pasien.getPasien_kota());
        b.putString("kecamatan", pasien.getPasien_kecamatan());
        b.putString("desa", pasien.getPasien_desa());

        b.putString("jenisPembayaran", pasien.getPasien_jenisPembayaran());
        b.putString("namaPenjamin", pasien.getPasien_namaPenjamin());
        b.putString("type", pasien.getPasien_type());
        b.putStringArrayList("asuransi", listAsuransi);
        b.putStringArrayList("idAsuransi", listAsuransiId);

        Intent intent = new Intent(activity, PasienEditActivity.class);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    private void deleteFromServer(final Integer id, final Integer position)
    {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang menghapus pasien...");
        progressDialog.show();

        sharedPreferences = activity.getSharedPreferences("RS PMI BOGOR MOBILE APPS", Context.MODE_PRIVATE);
        final String jwTokenSP = sharedPreferences.getString("jwtToken", null);
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://103.23.22.46:1337/v1/pasien/" + id;

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        Pasien pasien = Pasien.get(position);
                        Pasien.remove(pasien);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, Pasien.size());


                        Toast.makeText(activity, "Pasien berhasil dihapus.", Toast.LENGTH_SHORT).show();
                        //Log.d("deleteFromServer - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if(error instanceof NoConnectionError)
                        {
                            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                Log.d(TAG, "OS: " + Build.VERSION_CODES.JELLY_BEAN_MR2);
                                if(refreshToken <= 5)
                                {
                                    if(jwTokenSP != null){
                                        User user = new User();
                                        user.refreshToken(jwTokenSP, activity);
                                    }

                                    refreshToken++;
                                }
                            }
                        }else if(error instanceof AuthFailureError)
                        {
                            if(jwTokenSP != null){
                                User user = new User();
                                user.refreshToken(jwTokenSP, activity.getBaseContext());
                            }
                        }

                        Toast.makeText(activity, "Pasien Gagal dihapus, Silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                        //Log.d("deleteFromServer - Error.Response", String.valueOf(error));
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
        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }
}
