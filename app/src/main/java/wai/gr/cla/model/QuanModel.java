package wai.gr.cla.model;

/**
 * Created by lenovo on 2017/10/22.
 */

public class QuanModel {

    /**
     * id : 12
     * uid : 3579
     * coupon_id : 13
     * cdate : 2017-10-22 13:10:50
     * type_name : 新用户优惠券
     * expiration_status : 1
     * expiration : 快过期
     * status : 1
     * coupon_price : 5.00
     * coupon_code : KeGlPw
     * expiration_date : 2018-01-22 13:10:50
     */

    private int id;
    private int uid;
    private int coupon_id;
    private String cdate;
    private String type_name;
    private int expiration_status;
    private String expiration;
    private int status;
    private String coupon_price;
    private String coupon_code;
    private String expiration_date;

    //满减字段
    private String price;//当前价格
    private Double amount;//满的价格

    private Double reduce_amount;//减去的价格
    private Double new_price;//最新价格

    public String getPrice() {
        return price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Double getReduce_amount() {
        return reduce_amount;
    }

    public void setReduce_amount(Double reduce_amount) {
        this.reduce_amount = reduce_amount;
    }

    public Double getNew_price() {
        return new_price;
    }

    public void setNew_price(Double new_price) {
        this.new_price = new_price;
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

    public int getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(int coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getExpiration_status() {
        return expiration_status;
    }

    public void setExpiration_status(int expiration_status) {
        this.expiration_status = expiration_status;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCoupon_price() {
        return coupon_price;
    }

    public void setCoupon_price(String coupon_price) {
        this.coupon_price = coupon_price;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }
}
