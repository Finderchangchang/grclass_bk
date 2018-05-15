package wai.gr.cla.model;

import java.io.Serializable;

/**
 * Created by Finder丶畅畅 on 2017/10/20 22:48
 * QQ群481606175
 */

public class CarModel implements Serializable {

    /**
     * id : 37
     * uid : 39
     * course_id : 3
     * cdate : 2017-10-20 22:12:14
     * mdate : 2017-10-20 22:12:14
     * course_title : 浅谈汉语言
     * price : 0.01
     * thumbnail : /uploads/course/2017082117044315033062835778.jpg
     */

    private int id;
    private int uid;
    private int course_id;
    private String cdate;
    private String mdate;
    private String course_title;
    private String price;
    private String thumbnail;
    private int is_full_cut;

    public int getIs_full_cut() {
        return is_full_cut;
    }

    public void setIs_full_cut(int is_full_cut) {
        this.is_full_cut = is_full_cut;
    }

    private boolean isChecked;//true：点击。false:没点击

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

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

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
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

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
