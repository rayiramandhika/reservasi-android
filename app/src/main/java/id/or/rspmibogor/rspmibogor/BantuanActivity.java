package id.or.rspmibogor.rspmibogor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.or.rspmibogor.rspmibogor.Adapter.BantuanAdapter;

public class BantuanActivity extends AppCompatActivity {

    private static ExpandableListView expandableListView;
    private static BantuanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bantuan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.simple_expandable_listview);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);

        setItems();
        setListener();


    }

    // Setting headers and childs to expandable listview
    void setItems() {

        ArrayList<String> header = new ArrayList<String>();
        ArrayList<String> child = new ArrayList<String>();
        ArrayList<String> child1 = new ArrayList<String>();
        ArrayList<String> child2 = new ArrayList<String>();
        ArrayList<String> child3 = new ArrayList<String>();
        ArrayList<String> child4 = new ArrayList<String>();
        ArrayList<String> child5 = new ArrayList<String>();



        HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();

        for (int i = 1; i < 7; i++) {

            if(i == 1)
            {
                header.add("Tentang Pendaftaran Online - RS PMI Bogor");
                child.add("Pendaftaran Online adalah aplikasi yang dapat membantu anda untuk melakukan pendaftaran secara online untuk rawat jalan Poliklinik Afiat di RS PMI Bogor.");
            }else if(i == 2) {

                header.add("Cara Melakukan Pendaftaran ");
                child1.add("1. Untuk melakukan pendataftaran anda dapat masuk ke menu jadwal dokter, lalu memilih dokter yang akan anda kunjungi. \n \n  \n" +
                        "2. Setelah muncul halaman list jadwal dokter pilih jadwal yang akan di kunjungi (Jadwal hanya dapat di pesan H+1 s.d H+7). \n \n  \n" +
                        "3. Setalah anda memliih jadwal yang akan dikunjungi anda harus memilih pasien yang akan di daftarkan pada pendaftaran tersebut (Jika Pasien belum ada , anda dapat menambahankan nya dengan masuk ke menu pasien atau dengan menekan tombol + di bawah kanan). \n \n  \n" +
                        "4. Setiap pendaftaran membutuhkan konfirmasi kedatangan pada hari H, untuk melakukan konfirmasi anda bisa pilih menu pendaftaran" +
                        ", Lalu pilih pendaftaran yang akan anda konfirmasi  \n" +
                        ", Setelah masuk ke detail pendaftaran , anda dapat menekan tombol konfirmasi dibawah. \n  \n  \n" +
                        "4.1 Konfirmasi hanya bisa dilakukan pada hari H. \n  \n" +
                        "4.2 Konfirmasi hanya bisa dilakukan sebelum jam 07.30 ( jika jadwal praktek dokter dimulai sebelum jam 15.00) atau sebelum jam 14.30 (jika jadwal praktek dokter dimulai setelah jam 15.00). \n  \n" +
                        "4.3 Jika anda tidak melakukan konfirmasi atau melakukan konfirmasi melebihi ketentuan waktu maka pendaftaran di anggap batal. ");

            }else if(i == 3) {

                header.add("Car Beri Penilaian Pelayanan RS PMI Bogor");
                child2.add("1. Untuk memberi penialaian terhadap pelayanan RS PMI Bogor anda dapat masuk ke menu pendaftaran dan memilih tab pendaftaran lama. \n \n" +
                        "2. Lalu pilih pendaftaran yang akan diberi penilaian. \n \n" +
                        "3. Setelah masuk ke detail pendaftaran lama ada silahkan pilih rating yang akan anda berikan sesuai gambar / icon yang telah disediakan. \n \n" +
                        "4. Lalu mengisi form Saran yang perlu di perbaiki.");


            }else if(i == 4) {

            header.add("Cara Konfirmasi Kedatangan Pendaftaran");
            child3.add("1. Setiap pendaftaran membutuhkan konfirmasi kedatangan pada hari H, untuk melakukan konfirmasi anda bisa pilih menu pendaftaran" +
                        ", Lalu pilih pendaftaran yang akan anda konfirmasi" +
                        ", Setelah masuk ke detail pendaftaran , anda dapat menekan tombol konfirmasi dibawah. \n  \n  \n" +
                        "1.1 Konfirmasi hanya bisa dilakukan pada hari H. \n  \n" +
                        "1.2 Konfirmasi hanya bisa dilakukan sebelum jam 07.30 ( jika jadwal praktek dokter dimulai sebelum jam 15.00) atau sebelum jam 14.30 (jika jadwal praktek dokter dimulai setelah jam 15.00). \n  \n" +
                        "1.3 Jika anda tidak melakukan konfirmasi atau melakukan konfirmasi melebihi ketentuan waktu maka pendaftaran di anggap batal.");


            }else if(i == 5) {

                header.add("Perubahan Jadwal Dokter");
                child4.add("Setiap perubahan jadwal dokter yang telah anda daftarakan, kami akan beritahu melalui notifikasi / pada menu kotak masuk. Pastikan sebelum anda datang ke RS PMI Bogor bahwa tidak ada peruabahan jadwal yang anda terima di notifikasi / menu kotak masuk.");


            }else if(i == 6) {

                header.add("Ketentuan Layanan");
                child5.add("1. Pendaftaran sementara hanya dapat dilakukan untuk poliklinik Afiat\n \n" +
                        "2. Jadwal Dokter dapat berubah sesuai kebijakan. Setiap jadwal yang berubah kami akan memberikan informasi melalui notifikasi / kotak masuk (Jika anda telah melakukan pendaftaran pada jadwal berubah tersebut) \n \n" +
                        "3. Jadwal Dokter untuk Pendaftaran Online hanya tersedia H+1 s.d H+7. \n \n" +
                        "4. Anda dapat memberi penilaian terhadap layanan RS PMI Bogor di halaman detail pendaftaran. \n \n" +
                        "5. Setiap pendaftaran yang telah anda lakukan harus melakukan konfirmasi kedatangan pada hari H agar memastikan bahwa anda akan datang ke RS PMI Bogor. \n \n" +
                        "6. Anda dapat menambahkan lebih dari 1 pasien , agar memberi kemudahan dalam melakukan pendaftaran online. \n \n" +
                        "7. Jika anda telah berhasil melakukan konfirmasi pendaftaran , Mohon untuk menunjukan detail pendaftaran kepada bagian pendaftaran di RS PMI Bogor. \n \n " +
                        "8. Jadwal Dokter memiliki Kuota yang sudah ditentukan oleh RS PMI Bogor. Jika keterangan Pasien via pendaftaran Online penuh maka Anda disarankan untuk mendaftar secara offline di RS PMI Bogor.");


            }



        }


        // Adding header and childs to hash map
        hashMap.put(header.get(0), child);
        hashMap.put(header.get(1), child1);
        hashMap.put(header.get(2), child2);
        hashMap.put(header.get(3), child3);
        hashMap.put(header.get(4), child4);
        hashMap.put(header.get(5), child5);

        adapter = new BantuanAdapter(BantuanActivity.this, header, hashMap);

        expandableListView.setAdapter(adapter);
    }

    void setListener() {

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView listview, View view,
                                        int group_pos, long id) {


                return false;
            }
        });

        expandableListView
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)

                            expandableListView.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }

                });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView listview, View view,
                                        int groupPos, int childPos, long id) {

                return false;
            }
        });
    }

}
