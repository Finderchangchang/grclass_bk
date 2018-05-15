package wai.gr.cla.model;

import java.io.Serializable;

/**
 * 创业模型
 * Created by Administrator on 2017/5/18.
 */

public class MyBussinessModel implements Serializable {
    private String name;//姓名
    private String tel;//手机号
    private String subject;//项目名称
    private String content;//项目内容

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
