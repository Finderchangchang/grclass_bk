package wai.gr.cla.model;

/**
 * Created by Administrator on 2018/1/15.
 */

public class FKModel {

    /**
     * id : 175
     * uid : 3925
     * content : 444
     * reply_content :
     * reply_date : 0000-00-00 00:00:00
     * r_id : 0
     * cdate : 2018-01-15 15:24:44
     * mdate : 2018-01-15 15:24:44
     * name : QQ用户
     */

    private int id;
    private int uid;
    private String content;
    private String reply_content;
    private String reply_date;
    private int r_id;
    private String cdate;
    private String mdate;
    private String name;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public String getReply_date() {
        return reply_date;
    }

    public void setReply_date(String reply_date) {
        this.reply_date = reply_date;
    }

    public int getR_id() {
        return r_id;
    }

    public void setR_id(int r_id) {
        this.r_id = r_id;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
