package id.or.rspmibogor.rspmibogor.GetterSetter;

/**
 * Created by iqbalprabu on 26/08/16.
 */
public class ListJadwal {

    private String jadwal_hari;
    private String jadwal_tanggal;
    private Integer jadwal_id;
    private String jadwal_jamMulai;
    private String jadwal_jamTutup;
    private Integer jadwal_kuota;
    private String dokter_nama;
    private String dokter_foto;
    private Integer dokter_id;
    private String layanan_nama;
    private Integer layanan_id;

    public String getJadwal_hari() {return  jadwal_hari;}
    public void setJadwal_hari(String jadwal_hari) {this.jadwal_hari = jadwal_hari;}

    public String getJadwal_tanggal() {return jadwal_tanggal;}
    public void setJadwal_tanggal(String jadwal_tanggal) {this.jadwal_tanggal = jadwal_tanggal;}

    public Integer getJadwal_id() {return jadwal_id;}
    public void setJadwal_id(Integer jadwal_id) {this.jadwal_id = jadwal_id;}

    public String getJadwal_jamMulai() {return jadwal_jamMulai;}
    public void setJadwal_jamMulai(String jadwal_jamMulai) {this.jadwal_jamMulai = jadwal_jamMulai;}

    public String getJadwal_jamTutup() {return jadwal_jamTutup;}
    public void setJadwal_jamTutup(String jadwal_jamTutup){this.jadwal_jamTutup = jadwal_jamTutup;}

    public Integer getJadwal_kuota() {return jadwal_kuota;}
    public void setJadwal_kuota(Integer jadwal_kuota) {this.jadwal_kuota = jadwal_kuota;}

    public String getDokter_nama() {return dokter_nama;}
    public void setDokter_nama(String dokter_nama) { this.dokter_nama = dokter_nama; }

    public String getDokter_foto() {return dokter_foto;}
    public void setDokter_foto(String dokter_foto) { this.dokter_foto = dokter_foto; }

    public Integer getDokter_id() {return dokter_id;}
    public void setDokter_id(Integer dokter_id) {this.dokter_id = dokter_id;}

    public String getLayanan_nama() {return layanan_nama;}
    public void setLayanan_nama(String layanan_nama) { this.layanan_nama = layanan_nama; }

    public Integer getLayanan_id() {return layanan_id;}
    public void setLayanan_id(Integer layanan_id) {this.layanan_id = layanan_id;}



}
