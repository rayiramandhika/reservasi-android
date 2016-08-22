package id.or.rspmibogor.rspmibogor.GetterSetter;

/**
 * Created by iqbalprabu on 22/08/16.
 */
public class Inbox {

    private String title;
    private String body;
    private String desc;
    private String tanggal;
    private Integer id;
    private Boolean read;
    private Integer user_id;


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String body() { return body; }
    public void setBody(String body) { this.body = this.body; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

}
