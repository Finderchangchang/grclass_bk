package wai.gr.cla.model;

import java.io.Serializable;

/**
 * Created by Finder丶畅畅 on 2017/10/20 07:28
 * QQ群481606175
 */

public class AddressModel implements Serializable{

    /**
     * id : 12
     * uid : 39
     * province : 河北省
     * city : 保定市新市区
     * address : 百花竞开快快乐乐
     * tel : 13521622479
     * name : 张艳江
     * qq : 814643107
     * cdate : 2017-10-19 15:24:24
     * mdate : 2017-10-19 15:24:24
     */

    private int id;
    private int uid;
    private String province;
    private String city;
    private String area;//区
    private String address;
    private String tel;
    private String name;
    private String qq;
    private String cdate;
    private String mdate;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
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
}
