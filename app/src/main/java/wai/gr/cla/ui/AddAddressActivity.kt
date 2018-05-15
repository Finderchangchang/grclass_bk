package wai.gr.cla.ui

import android.graphics.Color
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_add_address.*

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import java.util.regex.Pattern
import com.lljjcoder.city_20170724.bean.DistrictBean
import com.lljjcoder.city_20170724.bean.CityBean
import com.lljjcoder.city_20170724.bean.ProvinceBean
import com.lljjcoder.city_20170724.CityPickerView



/**
 * 地址添加
 * */
class AddAddressActivity : BaseActivity() {
    var address = AddressModel()
    override fun setLayout(): Int {
        return R.layout.activity_add_address
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
    }

    /**
     * true 不是手机号
     * */
    fun isNoCellphone(str: String): Boolean {
        val pattern = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$")
        val matcher = pattern.matcher(str)
        return !matcher.matches()
    }

    override fun initEvents() {
        address = intent.getSerializableExtra("model") as AddressModel
        if (address.id != 0) {
            et_address_name.setText(address.name)
            et_address_qq.setText(address.qq)
            et_address_tel.setText(address.tel)
            et_address_city.text = address.province + address.city + address.area
            et_address_address.setText(address.address)
        }
        et_address_city_ll.setOnClickListener {
            /*val dialog = BottomDialog(this)
            dialog.setOnAddressSelectedListener { province, city, county, street ->
                //toast(province.name + city.name + county.name + street.name)
                address.province = province!!.name
                address.city = city!!.name
                address.area = county.name
                et_address_city.text = address.province + address.city + address.area
                dialog.dismiss()
            }
            dialog.show()*/

            val cityPicker = CityPickerView.Builder(this@AddAddressActivity)
                    .textSize(20)
                    .title("地址选择")
                    .backgroundPop(0xa0000000.toInt())
                    //.titleBackgroundColor("#234Dfa")
                    .titleTextColor("#000000")
                    .backgroundPop(0xa0000000.toInt())
                    .confirTextColor("#000000")
                    .cancelTextColor("#000000")
                    .province("河北省")
                    .city("保定市")
                    .district("新市区")
                    .textColor(Color.parseColor("#000000"))
                    .provinceCyclic(true)
                    .cityCyclic(false)
                    .districtCyclic(false)
                    .visibleItemsCount(7)
                    .itemPadding(10)
                    .onlyShowProvinceAndCity(false)
                    .build()
            cityPicker.show()

            //监听方法，获取选择结果
            cityPicker.setOnCityItemClickListener(object : CityPickerView.OnCityItemClickListener {
                override fun onSelected(province: ProvinceBean, city: CityBean, district: DistrictBean) {
                    //返回结果
                    //ProvinceBean 省份信息
                    //CityBean     城市信息
                    //DistrictBean 区县信息
                    address.province = province!!.name
                    address.city = city!!.name
                    address.area = district.name
                    et_address_city.text = address.province + address.city + address.area
                    //dialog.dismiss()
                }

                override fun onCancel() {

                }
            })
        }

        toolbar.setRightClick {
            //保存
            if (checkvalue()) {
                getValue()
                //向服务器提交
                var ok = OkGo.post(url().auth_api + "save_user_address")
                if (address.id != 0) {
                    ok.params("id", address.id)
                }
                ok.params("name", address.name)// 请求方式和请求url
                        .params("tel", address.tel)// 请求方式和请求url
                        .params("province", address.province)// 请求方式和请求url
                        .params("address", address.address)
                        .params("area", address.area)
                        .params("qq", address.qq)
                        .params("city", address.city)// 请求方式和请求url
                        .execute(object : JsonCallback<LzyResponse<MyBussinessModel>>() {
                            override fun onSuccess(t: LzyResponse<MyBussinessModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    toast(t.msg!!)
                                    setResult(77)
                                    finish()
                                }
                            }

                            override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
        }


    }

    //验证数据
    fun checkvalue(): Boolean {
        if (et_address_name.text.toString().trim().equals("")) {
            toast("请填写收件人姓名")
            return false
        } else if (et_address_qq.text.toString().trim().equals("")) {
            toast("请填写联系QQ号码")
            return false
        } else if (isNoCellphone(et_address_tel.text.toString().trim()) || et_address_tel.text.toString().trim().equals("")) {
            toast("请填写联系方式")
            return false
        } else if (address.province!!.toString().equals("") || address.city!!.toString().equals("")) {
            toast("请选择所在省份和城市")
            return false
        } else if (et_address_address.text.toString().trim().equals("")) {
            toast("请填写详细地址")
            return false
        }

        return true
    }

    //获取数据
    fun getValue() {
        address.address = et_address_address.text.toString().trim()
        address.tel = et_address_tel.text.toString().trim()
        address.qq = et_address_qq.text.toString().trim()
        address.name = et_address_name.text.toString().trim()

    }

}
