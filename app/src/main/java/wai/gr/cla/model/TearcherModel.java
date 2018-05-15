package wai.gr.cla.model;

import java.io.Serializable;

/**
 * 老师模型
 * Created by Administrator on 2017/5/12.
 */

public class TearcherModel implements Serializable{

    private String imgPath;
    private String name;
    private String sex;
    private String type;
    private String content;
    private String work;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }
}
