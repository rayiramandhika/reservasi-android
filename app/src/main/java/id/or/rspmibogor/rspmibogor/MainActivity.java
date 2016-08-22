package id.or.rspmibogor.rspmibogor;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import id.or.rspmibogor.rspmibogor.Fragment.HomeFragment;
import id.or.rspmibogor.rspmibogor.Fragment.InboxFragment;
import id.or.rspmibogor.rspmibogor.Fragment.OrderFragment;
import id.or.rspmibogor.rspmibogor.Models.User;
import id.or.rspmibogor.rspmibogor.Services.FirebaseInstanceIDService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    HomeFragment HomeFrag = new HomeFragment();
    InboxFragment InboxFrag = new InboxFragment();
    OrderFragment OrderFrag = new OrderFragment();

    BottomBar bottomBar;
    NavigationView navigationView;
    DrawerLayout drawer;

    int MenuIdActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("savedInstanceState", String.valueOf(savedInstanceState));

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null && !HomeFrag.isAdded()) {

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.container, HomeFrag);
            transaction.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        //Bottom NavBar
        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                switch (itemId) {
                    case R.id.home:
                        //Toast.makeText(getApplicationContext(),"Beranda dipilih",Toast.LENGTH_SHORT).show();

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(0).setChecked(true);

                        transaction.replace(R.id.container, HomeFrag);
                        transaction.commit();


                        break;
                    case R.id.order:
                        //Toast.makeText(getApplicationContext(),"Pesanan dipilih",Toast.LENGTH_SHORT).show();

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(2).setChecked(true);

                        transaction.replace(R.id.container, OrderFrag);
                        transaction.commit();

                        break;
                    case R.id.inbox:
                        //Toast.makeText(getApplicationContext(),"Kotak Masuk dipilih",Toast.LENGTH_SHORT).show();

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        navigationView.getMenu().getItem(4).setChecked(true);

                        transaction.replace(R.id.container, InboxFrag);
                        transaction.commit();

                        break;
                }
            }
        });

        bottomBar.setActiveTabColor("#D32F2F");

        //set Icon badge
        TextView view = (TextView) navigationView.getMenu().getItem(4).getActionView();
        view.setText("2");

        FirebaseInstanceIDService firebase = new FirebaseInstanceIDService();
        String token;
        token = firebase.getToken();

        User user = new User();
        user.updateFCMToken(token, this.getBaseContext());

        Log.d("Firebase", "token: " + token);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        int id = item.getItemId();

        if (id == MenuIdActive) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        MenuIdActive = item.getItemId();

        if (id == R.id.dashboard) {

            transaction.replace(R.id.container, HomeFrag);
            transaction.commit();

            bottomBar.selectTabAtPosition(0, false);

        } else if (id == R.id.pasien) {

            Intent intent = new Intent(this, PasienActivity.class);
            startActivity(intent);

        } else if (id == R.id.order) {

            transaction.replace(R.id.container, OrderFrag);
            transaction.commit();

            bottomBar.selectTabAtPosition(1, false);

        } else if (id == R.id.inbox) {

            transaction.replace(R.id.container, InboxFrag);
            transaction.commit();

            bottomBar.selectTabAtPosition(2, false);

        } else if (id == R.id.jadwaldokter) {

            Intent intent = new Intent(this, JadwalDokterActivity.class);
            startActivity(intent);


        } else if (id == R.id.myacccount) {

        } else if (id == R.id.logout) {

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
