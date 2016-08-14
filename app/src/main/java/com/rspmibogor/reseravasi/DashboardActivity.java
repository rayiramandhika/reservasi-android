package com.rspmibogor.reseravasi;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.rspmibogor.reseravasi.Fragment.HomeFragment;
import com.rspmibogor.reseravasi.Fragment.InboxFragment;
import com.rspmibogor.reseravasi.Fragment.OrderFragment;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


public class DashboardActivity extends AppCompatActivity {

    Toolbar toolbar;
    CarouselView carouselView;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    HomeFragment HomeFrag = new HomeFragment();
    InboxFragment InboxFrag = new InboxFragment();
    OrderFragment OrderFrag = new OrderFragment();

    //init slider image
    int[] sliderImage = {R.drawable.header, R.drawable.header, R.drawable.header, R.drawable.header, R.drawable.header};

    public DashboardActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //Make Carousel Banner
        //carouselView = (CarouselView) findViewById(R.id.carouselView);
        //carouselView.setPageCount(sliderImage.length);
        //carouselView.setImageListener(imageListener);


        //Navigation Drawer
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Mengatur Navigasi View Item yang akan dipanggil untuk menangani item klik menu navigasi
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.navigation1:
                        Toast.makeText(getApplicationContext(), "Beranda Telah Dipilih", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation2:
                        Toast.makeText(getApplicationContext(),"Profil Telah Dipilih",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation3:
                        Toast.makeText(getApplicationContext(),"Daftar Telah Dipilih",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation4:
                        Toast.makeText(getApplicationContext(),"Setting telah dipilih",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation5:
                        Toast.makeText(getApplicationContext(),"About telah dipilih",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Kesalahan Terjadi ",Toast.LENGTH_SHORT).show();
                        return true;
                }

            }

        });

        // Menginisasi Drawer Layout dan ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Kode di sini akan merespons setelah drawer menutup disini kita biarkan kosong
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //  Kode di sini akan merespons setelah drawer terbuka disini kita biarkan kosong
                super.onDrawerOpened(drawerView);
            }

        };

        //Mensetting actionbarToggle untuk drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //memanggil synstate
        actionBarDrawerToggle.syncState();


        //Bottom NavBar
        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                switch (itemId) {
                    case R.id.home:
                        //Toast.makeText(getApplicationContext(),"Beranda dipilih",Toast.LENGTH_SHORT).show();

                        transaction.replace(R.id.container, HomeFrag);
                        transaction.commit();

                        break;
                    case R.id.order:
                        //Toast.makeText(getApplicationContext(),"Pesanan dipilih",Toast.LENGTH_SHORT).show();

                        transaction.replace(R.id.container, OrderFrag);
                        transaction.commit();

                        break;
                    case R.id.inbox:
                        //Toast.makeText(getApplicationContext(),"Kotak Masuk dipilih",Toast.LENGTH_SHORT).show();

                        transaction.replace(R.id.container, InboxFrag);
                        transaction.commit();


                        break;
                }
            }
        });

        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        bottomBar.setActiveTabColor("#C2185B");

        // Use the dark theme. Ignored on mobile when there are more than three tabs.
        //bottomBar.useDarkTheme(true);

        // Use custom text appearance in tab titles.
        //bottomBar.setTextAppearance(R.style.MyTextAppearance);

        // Use custom typeface that's located at the "/src/main/assets" directory. If using with
        // custom text appearance, set the text appearance first.
        //bottomBar.setTypeFace("MyFont.ttf");


    }




}
