package id.or.rspmibogor.rspmibogor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TentangActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtVersion;
    private ImageView imgLocation;
    private ImageView imgPhone;
    private ImageView imgFax;
    private ImageView imgEmail;
    private ImageView imgWeb;
    private LinearLayout lnrLokasi;
    private LinearLayout lnrPhone;
    private LinearLayout lnrEmail;
    private LinearLayout lnrWeb;
    private LinearLayout lnrFacebook;
    private LinearLayout lnrInstagram;

    public static String FACEBOOK_URL = "https://www.facebook.com/RS-PMI-Bogor-164113236944568";
    public static String FACEBOOK_PAGE_ID = "RS-PMI-Bogor-164113236944568";

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

        lnrLokasi = (LinearLayout) findViewById(R.id.lnrLayoutLokasi);
        lnrLokasi.setOnClickListener(this);
        lnrPhone = (LinearLayout) findViewById(R.id.lnrLayoutPhone);
        lnrPhone.setOnClickListener(this);
        lnrEmail = (LinearLayout) findViewById(R.id.lnrLayoutEmail);
        lnrEmail.setOnClickListener(this);
        lnrWeb = (LinearLayout) findViewById(R.id.lnrLayoutWeb);
        lnrWeb.setOnClickListener(this);
        lnrFacebook = (LinearLayout) findViewById(R.id.lnrLayoutFacebook);
        lnrFacebook.setOnClickListener(this);
        lnrInstagram = (LinearLayout) findViewById(R.id.lnrLayoutInstagram);
        lnrInstagram.setOnClickListener(this);

    }



    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.lnrLayoutLokasi)
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

        }else if(v.getId() == R.id.lnrLayoutPhone)
        {
            try {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "02518324080", null)));
            }catch (ActivityNotFoundException e)
            {
                Toast.makeText(this, "Gagal memanggil nomor telepon, Aplikasi Telepon tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }

        }else if(v.getId() == R.id.lnrLayoutEmail){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"admin@rspmibogor.or.id"});
            try {
                startActivity(Intent.createChooser(i, "Kirim email"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Gagal mengirim email, Aplikasi Email tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.lnrLayoutWeb)
        {
            Uri uriUrl = Uri.parse("http://rspmibogor.or.id");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
            try {
                startActivity(Intent.createChooser(intent, "Buka Website"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Gagal membuka website, Aplikasi Browser tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.lnrLayoutFacebook)
        {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURL(this);
            facebookIntent.setData(Uri.parse(facebookUrl));

            try {
                startActivity(facebookIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
                Uri uriUrl = Uri.parse(FACEBOOK_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);

                try {
                    startActivity(Intent.createChooser(intent, "Buka Facebook PMI"));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "Gagal membuka Facebook, Aplikasi Facebook / Browser tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(v.getId() == R.id.lnrLayoutInstagram){
            Uri uri = Uri.parse("http://instagram.com/_u/rspmibogor");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException ex) {

                Uri uriUrl =  Uri.parse("http://instagram.com/rspmibogor");
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);

                try {
                    startActivity(Intent.createChooser(intent, "Buka Instagram PMI"));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "Gagal membuka Instagram, Aplikasi Instagram / Browser tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
