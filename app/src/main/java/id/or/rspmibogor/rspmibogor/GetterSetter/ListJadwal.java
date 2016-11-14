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
    private Integer jadwal_sisaKuota;
    private String dokter_nama;
    private String dokter_foto;
    private Integer dokter_id;
    private String layanan_nama;
    private Integer layanan_id;
    private String poliklinik_nama;
    private Integer poliklinik_id;
    private String jadwal_status;
    private String keterangan;

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

    public String getJadwal_status() { return jadwal_status; }
    public void setJadwal_status(String jadwal_status) { this.jadwal_status = jadwal_status; }

    public String getKeterangan() {
        return keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Integer getJadwal_sisaKuota() {
        return jadwal_sisaKuota;
    }

    public void setJadwal_sisaKuota(Integer jadwal_sisaKuota) {
        this.jadwal_sisaKuota = jadwal_sisaKuota;
    }

    public Integer getPoliklinik_id() {
        return poliklinik_id;
    }

    public void setPoliklinik_id(Integer poliklinik_id) {
        this.poliklinik_id = poliklinik_id;
    }

    public String getPoliklinik_nama() {
        return poliklinik_nama;
    }

    public void setPoliklinik_nama(String poliklinik_nama) {
        this.poliklinik_nama = poliklinik_nama;
    }
}
