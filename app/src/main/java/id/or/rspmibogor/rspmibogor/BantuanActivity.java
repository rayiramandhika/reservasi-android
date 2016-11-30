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

        expandableListView.setGroupIndicator(null);

        setItems();
        setListener();


    }

    void setItems() {

        ArrayList<String> header = new ArrayList<String>();
        ArrayList<String> child = new ArrayList<String>();
        ArrayList<String> child1 = new ArrayList<String>();
        ArrayList<String> child2 = new ArrayList<String>();
        ArrayList<String> child3 = new ArrayList<String>();
        ArrayList<String> child4 = new ArrayList<String>();
        ArrayList<String> child5 = new ArrayList<String>();
        ArrayList<String> child6 = new ArrayList<String>();
        ArrayList<String> child7 = new ArrayList<String>();
        ArrayList<String> child8 = new ArrayList<String>();
        ArrayList<String> child9 = new ArrayList<String>();



        HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();

        for (int i = 1; i < 11; i++) {

            if(i == 1)
            {
                header.add("Tentang Pendaftaran Online - RS PMI Bogor");
                child.add("Pendaftaran Online adalah aplikasi yang dapat membantu Anda untuk melakukan pendaftaran secara online untuk rawat jalan Poliklinik Afiat di RS PMI Bogor.");
            }else if(i == 2) {

                header.add("Ketentuan Layanan");
                child1.add("1. Pendaftaran hanya dapat dilakukan untuk poliklinik Afiat\n \n" +
                        "2. Jadwal Dokter dapat berubah sesuai kebijakan. Setiap jadwal yang berubah kami akan memberikan informasi melalui notifikasi / pada menu kotak masuk (Jika Anda telah melakukan pendaftaran pada jadwal yang berubah tersebut).\nPastikan sebelum Anda datang ke RS PMI Bogor bahwa tidak ada perubahan jadwal yang Anda terima di notifikasi / menu kotak masuk. \n \n" +
                        "3. Jadwal Dokter untuk Pendaftaran Online hanya tersedia untuk H-7 s.d H-1. \n \n" +
                        "4. Anda dapat memberi penilaian terhadap layanan RS PMI Bogor di halaman detail pendaftaran. \n \n" +
                        "5. Setiap pendaftaran yang telah Anda lakukan membutuhkan konfirmasi kedatangan pada hari H agar memastikan bahwa Anda akan datang ke RS PMI Bogor. \n \n" +
                        "6. Anda dapat menambahkan lebih dari 1 pasien , agar memberi kemudahan dalam melakukan pendaftaran online. \n \n" +
                        "7. Jika Anda telah berhasil melakukan konfirmasi pendaftaran , Mohon untuk menunjukan detail pendaftaran kepada bagian pendaftaran di RS PMI Bogor. \n \n " +
                        "8. Jadwal Dokter memiliki Kuota yang sudah ditentukan oleh RS PMI Bogor. Jika keterangan Pasien via pendaftaran Online penuh maka Anda disarankan untuk melakukan pendaftaran secara offline di RS PMI Bogor.");


            }else if(i == 3) {

                header.add("Cara Melakukan Pendaftaran ");
                child2.add("1. Untuk melakukan pendataftaran Anda dapat masuk ke menu jadwal dokter, lalu memilih dokter yang akan Anda kunjungi. \n \n  \n" +
                        "2. Setelah muncul halaman cari dokter silahkan pilih klinik yang akan Anda kunjungi (misal: Klinik Anak).\n \n  \n" +
                        "3. Setelah muncul halaman list jadwal dokter pilih jadwal yang akan di kunjungi (Jadwal hanya dapat di pesan H-7 s.d H-1). \n \n  \n" +
                        "4. Setalah Anda memliih jadwal yang akan dikunjungi Anda harus memilih pasien (pasien akan difilter berdasarkan kriteria Jadwak Dokter yang Anda pilih) yang akan di daftarkan pada pendaftaran tersebut (Jika Pasien belum ada , Anda dapat menambahankan nya dengan masuk ke menu pasien atau dengan menekan tombol + di bawah kanan). \n \n  \n" +
                        "5. Setiap pendaftaran membutuhkan konfirmasi kedatangan pada hari H, untuk melakukan konfirmasi Anda bisa pilih menu pendaftaran" +
                        ", Lalu pilih pendaftaran yang akan Anda konfirmasi  \n" +
                        ", Setelah masuk ke detail pendaftaran, Anda dapat menekan tombol konfirmasi dibawah. \n  \n  \n" +
                        "5.1 Konfirmasi hanya bisa dilakukan pada hari H. \n  \n" +
                        "5.2 Konfirmasi hanya bisa dilakukan sebelum jam 07.30 ( jika jadwal praktek dokter dimulai sebelum jam 15.00) atau sebelum jam 14.30 (jika jadwal praktek dokter dimulai setelah jam 15.00). \n  \n" +
                        "5.3 Jika Anda tidak melakukan konfirmasi atau melakukan konfirmasi melebihi ketentuan waktu yang telah di tetapkan maka pendaftaran di anggap batal. ");

            }else if(i == 4) {

                header.add("Car Beri Penilaian Pelayanan RS PMI Bogor");
                child3.add("1. Untuk memberi penilaian terhadap pelayanan RS PMI Bogor Anda dapat masuk ke menu pendaftaran dan memilih tab pendaftaran lama. \n \n" +
                        "2. Lalu pilih pendaftaran yang akan diberi penilaian. \n \n" +
                        "3. Setelah masuk ke detail pendaftaran lama, Silahkan pilih rating yang akan Anda berikan sesuai gambar / icon yang telah disediakan. \n \n" +
                        "4. Lalu mengisi form Saran yang perlu di perbaiki. \n \n" +
                        "5. Kemudian tekan tombol kirim disamping Kanan form Saran.");


            }else if(i == 5) {

            header.add("Cara Konfirmasi Kedatangan Pendaftaran");
            child4.add("1. Untuk melakukan konfirmasi Anda bisa pilih menu pendaftaran" +
                        ", Lalu pilih pendaftaran yang akan Anda konfirmasi" +
                        ", Setelah masuk ke detail pendaftaran , Anda dapat menekan tombol konfirmasi berwarna biru yang ada dibawah. \n  \n  \n" +
                        "1.1 Konfirmasi hanya bisa dilakukan pada hari H. \n  \n" +
                        "1.2 Konfirmasi hanya bisa dilakukan sebelum jam 07.30 ( jika jadwal praktek dokter dimulai sebelum jam 15.00) atau sebelum jam 14.30 (jika jadwal praktek dokter dimulai setelah jam 15.00). \n  \n" +
                        "1.3 Jika Anda tidak melakukan konfirmasi atau melakukan konfirmasi melebihi ketentuan waktu yang telah di tetapkan maka pendaftaran di anggap batal.");


            }else if(i == 6) {

                header.add("Cara Membatalkan Pendaftaran");
                child5.add("1. Untuk  Membatalkan Pendaftaran Anda dapat masuk ke menu pendaftaran\n \n" +
                        "2. Kemudia pilih tab pendaftaran baru. \n \n" +
                        "3. Pilih pendaftaran yang akan dibatalkan. \n \n" +
                        "4. Setalah muncul halaman detail pendaftaran. Silahkan tekan tombol Batalkan Pendaftaran yang ada dibawah.");


            }else if(i == 7) {

                header.add("Perubahan Jadwal Dokter");
                child6.add("Setiap jadwal dokter yang berubah kami akan memberikan informasi melalui notifikasi / pada menu kotak masuk (Jika Anda telah melakukan pendaftaran pada jadwal yang berubah tersebut).\nPastikan sebelum Anda datang ke RS PMI Bogor bahwa tidak ada perubahan jadwal yang Anda terima di notifikasi / menu kotak masuk.");


            }else if(i == 8) {

                header.add("Cara Menambah Pasien");
                child7.add("1. Untuk Menambah Pasien Anda dapat masuk ke menu pasien\n \n" +
                        "2. Kemudian tekan tombol + dibawah kanan. \n \n" +
                        "3. Setelah muncul halaman tambah pasien Anda diminta untuk mengisi form pasien yang terdiri dari 5 bagian. \n \n" +
                        "4. Bagian ke-1 yaitu identitas diri. \n \n" +
                        "5. Bagian ke-2 yaitu identitas keluarga. \n \n" +
                        "6. Bagian ke-3 yaitu identitas tempat tinggal. \n \n" +
                        "7. Bagian ke-4 yaitu jenis pembayaran untuk pasien tersebut TUNAI atau Asuransi ( Jika Asuransi silahkan pilih nama Asuransi). \n \n " +
                        "8. Bagian ke-5 yaitu halaman review untuk memastikan bahwa data yang di input sudah benar. \n \n" +
                        "9. Jika Anda sudah yakin data yang di input benar silahkan tekan tombol COMPLETE ");


            }else if(i == 9) {

                header.add("Cara Menghapus Pasien");
                child8.add("1. Untuk Menghapus Pasien Anda dapat masuk ke menu pasien\n \n" +
                        "2. Kemudian pilih pasien yang akan di hapus. \n \n" +
                        "3. Tekan tombol disamping pasien tersebut. \n \n" +
                        "4. Akan muncul pilihan edit/hapus. \n \n" +
                        "5. Pilih Hapus.");


            }else if(i == 10) {

                header.add("Cara Mengubah Pasien");
                child9.add("1. Untuk Mengubah Pasien Anda dapat masuk ke menu pasien\n \n" +
                        "2. Kemudian pilih pasien yang akan di ubah. \n \n" +
                        "3. Tekan tombol disamping pasien tersebut. \n \n" +
                        "4. Akan muncul pilihan edit/hapus. \n \n" +
                        "5. Pilih Edit.");


            }



        }


        // Adding header and childs to hash map
        hashMap.put(header.get(0), child);
        hashMap.put(header.get(1), child1);
        hashMap.put(header.get(2), child2);
        hashMap.put(header.get(3), child3);
        hashMap.put(header.get(4), child4);
        hashMap.put(header.get(5), child5);
        hashMap.put(header.get(6), child6);
        hashMap.put(header.get(7), child7);
        hashMap.put(header.get(8), child8);
        hashMap.put(header.get(9), child9);

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
