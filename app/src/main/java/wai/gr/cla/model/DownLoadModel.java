package wai.gr.cla.model;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/5/30.
 */

public class DownLoadModel implements Serializable {
    private String name;
    private String imgpath;
    private String cdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }
}
