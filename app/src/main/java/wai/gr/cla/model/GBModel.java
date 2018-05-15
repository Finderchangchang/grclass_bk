package wai.gr.cla.model;

/**
 * Created by Finder丶畅畅 on 2017/6/29 08:04
 * QQ群481606175
 */

public class GBModel {

    /**
     * id : 62
     * uid : 1
     * guanbi : 0.01
     * comment : 购买订阅：
     * cdate : 2017-06-27 11:58:23
     */

    private int id;
    private int uid;
    private String guanbi;
    private String comment;
    private String cdate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getGuanbi() {
        return guanbi;
    }

    public void setGuanbi(String guanbi) {
        this.guanbi = guanbi;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }
}
