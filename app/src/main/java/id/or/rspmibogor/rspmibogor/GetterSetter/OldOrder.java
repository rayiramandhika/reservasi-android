package id.or.rspmibogor.rspmibogor.GetterSetter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.or.rspmibogor.rspmibogor.Fragment.OrderFragment;

/**
 * Created by iqbalprabu on 17/08/16.
 */
public class OldOrder {
    //Data Variables
    private String pasien_name;
    private String pasien_norekammedik;
    private String user_name;
    private Integer user_id;
    private String detailjadwal_hari;
    private String detailjadwal_jammulai;
    private String detailjadwal_jamtutup;
    private String order_noUrut;
    private Integer order_id;
    private String order_tanggal;

    public String getPasien_name(){
        return pasien_name;
    }

    public void setPasien_name(String pasien_name){
        this.pasien_name = pasien_name;
    }

    public String getPasien_norekammedik(){
        return pasien_norekammedik;
    }

    public void setPasien_norekammedik(String pasien_norekammedik){
        this.pasien_norekammedik = pasien_norekammedik;
    }

    public String getUser_name(){
        return user_name;
    }

    public void setUser_name(String user_name){
        this.user_name = user_name;
    }

    public Integer getUser_id(){
        return user_id;
    }

    public void setUser_id(Integer user_id){
        this.user_id = user_id;
    }

    public String getDetailjadwal_hari()
    {
        return detailjadwal_hari;
    }

    public void setDetailjadwal_hari(String detailjadwal_hari){
        this.detailjadwal_hari = detailjadwal_hari;
    }


    public String getDetailjadwal_jammulai()
    {
        return detailjadwal_jammulai;
    }

    public void setDetailjadwal_jammulai(String detailjadwal_jammulai){
        this.detailjadwal_jammulai = detailjadwal_jammulai;
    }

    public String getDetailjadwal_jamtutup()
    {
        return detailjadwal_jamtutup;
    }

    public void setDetailjadwal_jamtutup(String detailjadwal_jamtutup){
        this.detailjadwal_jamtutup = detailjadwal_jamtutup;
    }

    public String getOrder_noUrut()
    {
        return order_noUrut;
    }

    public void setOrder_noUrut(String order_noUrut){
        this.order_noUrut = order_noUrut;
    }

    public String getOrder_tanggal()
    {
        return order_noUrut;
    }

    public void setOrder_tanggal(String order_tanggal){
        this.order_tanggal = order_tanggal;
    }

    public Integer getOrder_id()
    {
        return order_id;
    }

    public void setOrder_id(Integer order_id){
        this.order_id = order_id;
    }


}
