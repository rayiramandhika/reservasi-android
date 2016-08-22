package id.or.rspmibogor.rspmibogor.GetterSetter;

import id.or.rspmibogor.rspmibogor.PasienAddActivity;

/**
 * Created by iqbalprabu on 20/08/16.
 */
public class Dokter {

    private String dokter_name;
    private Integer dokter_id;
    private String dokter_foto;
    private String layanan_name;
    private Integer layanan_id;

    public String getDokter_name()
    {
        return dokter_name;
    }

    public void setDokter_name(String dokter_name)
    {
        this.dokter_name = dokter_name;
    }

    public Integer getDokter_id()
    {
        return dokter_id;
    }

    public void setDokter_id(Integer dokter_id)
    {
        this.dokter_id = dokter_id;
    }

    public String getDokter_foto()
    {
        return  getDokter_name();
    }

    public void setDokter_foto(String dokter_foto){
        this.dokter_foto = dokter_foto;
    }

    public String getLayanan_name()
    {
        return layanan_name;
    }

    public void setLayanan_name(String layanan_name)
    {
        this.layanan_name = layanan_name;
    }

    public Integer getLayanan_id()
    {
        return layanan_id;
    }

    public void setLayanan_id(Integer layanan_id){
        this.layanan_id = layanan_id;
    }

}
