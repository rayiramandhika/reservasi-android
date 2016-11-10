package id.or.rspmibogor.rspmibogor;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TentangActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtVersion;
    private ImageView imgLocation;
    private ImageView imgPhone;
    private ImageView imgFax;
    private ImageView imgEmail;
    private ImageView imgWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tentang");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtVersion.setText("Versi Aplikasi: " + BuildConfig.VERSION_NAME);


        imgLocation = (ImageView) findViewById(R.id.imgLocation);
        imgLocation.setOnClickListener(this);
        imgPhone = (ImageView) findViewById(R.id.imgPhone);
        imgPhone.setOnClickListener(this);
        imgFax = (ImageView) findViewById(R.id.imgFax);
        imgEmail = (ImageView) findViewById(R.id.imgEmail);
        imgEmail.setOnClickListener(this);
        imgWeb = (ImageView) findViewById(R.id.imgWeb);
        imgWeb.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgLocation)
        {
            double latitude = -6.598526;
            double longitude = 106.804209;
            String uriBegin = "geo:" + 0 + "," + 0;
            String query = "Rumah Sakit PMI Bogor";
            String uriString = uriBegin + "?q=" + query;
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            try{
                startActivity(Intent.createChooser(intent, "Lihat Lokasi"));
            }catch (ActivityNotFoundException e)
            {
                Toast.makeText(this, "Gagal memuat lokasi, Aplikasi Map tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }

        }else if(v.getId() == R.id.imgPhone)
        {
            try {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "02518324080", null)));
            }catch (ActivityNotFoundException e)
            {
                Toast.makeText(this, "Gagal memanggil nomor telepon, Aplikasi Telepon tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }

        }else if(v.getId() == R.id.imgEmail){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"admin@rspmibogor.or.id"});
            try {
                startActivity(Intent.createChooser(i, "Kirim email"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Gagal mengirim email, Aplikasi Email tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.imgWeb)
        {
            Uri uriUrl = Uri.parse("http://rspmibogor.or.id");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
            try {
                startActivity(Intent.createChooser(intent, "Buka Website"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Gagal membuka website, Aplikasi Browser tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }else{

        }
    }
}
