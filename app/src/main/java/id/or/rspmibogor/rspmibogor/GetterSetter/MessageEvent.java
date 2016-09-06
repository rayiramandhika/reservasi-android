package id.or.rspmibogor.rspmibogor.GetterSetter;

/**
 * Created by iqbalprabu on 01/09/16.
 */
public class MessageEvent {

    private final String pesan;

    private final Integer position_list;

    public MessageEvent(String pesan, Integer position_list) {
        this.pesan = pesan;
        this.position_list = position_list;
    }

    public String getPesan() {
        return pesan;
    }

    public Integer getPosition_list() {
        return position_list;
    }

}
