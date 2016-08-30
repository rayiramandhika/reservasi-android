package id.or.rspmibogor.rspmibogor;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class DetailOrderOld extends AppCompatActivity {

    Toolbar toolbar;

    String Rating = "Baik";

    ImageView ratingGood;
    ImageView ratingSad;
    ImageView rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_old);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Pesanan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //init
        ratingGood = (ImageView) findViewById(R.id.ratingGood);
        ratingSad = (ImageView) findViewById(R.id.ratingSad);
        rating = (ImageView) findViewById(R.id.rating);


        ratingGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.ic_mood_black_24dp));
            }
        });

        ratingSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating.setImageDrawable(getDrawable(R.drawable.ic_mood_bad_black_24dp));
            }
        });

    }

}
