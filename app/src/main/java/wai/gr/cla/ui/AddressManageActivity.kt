package wai.gr.cla.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import com.lljjcoder.citylist.CityListSelectActivity
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AlertDialog
import android.view.View
import chihane.jdaddressselector.BottomDialog
import chihane.jdaddressselector.model.Province
import com.google.gson.Gson
import com.lljjcoder.citylist.bean.CityInfoBean
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.umeng.socialize.utils.DeviceConfig.context
import kotlinx.android.synthetic.main.activity_address_manage.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.common
import wai.gr.cla.model.AddressModel
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.MyBussinessModel
import wai.gr.cla.model.url


class AddressManageActivity : BaseActivity() {
    var addressmodel = AddressModel()
    override fun setLayout(): Int {
        return R.layout.activity_address_manage
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
        del_ll.setOnClickListener {
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("删除")
            dialog.setMessage("确定要删除当前地址吗？")
            dialog.setPositiveButton("取消") { dialog, which -> }
            dialog.setNegativeButton("确定") { dialog, which ->
                OkGo.post(url().auth_api + "delete_address")
                        .params("id", addressmodel.id)// 请求方式和请求url
                        .execute(object : JsonCallback<LzyResponse<MyBussinessModel>>() {
                            override fun onSuccess(t: LzyResponse<MyBussinessModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    toast("删除成功")
                                    initEvents()
                                } else {
                                    toast("删除失败" + t.msg!!)
                                }
                            }

                            override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
            dialog.show()
        }
    }

    override fun initEvents() {
        edit_ll.setOnClickListener {
            startActivityForResult(Intent(this@AddressManageActivity, AddAddressActivity::class.java)
                    .putExtra("model", addressmodel), 11)

        }

        add_btn.setOnClickListener {
            //startActivity(Intent(this@AddressManageActivity, AddAddressActivity::class.java))
            startActivityForResult(Intent(this@AddressManageActivity, AddAddressActivity::class.java)
                    .putExtra("model", AddressModel()), 11)
        }
        OkGo.post(url().auth_api + "get_our_address")
                .execute(object : JsonCallback<LzyResponse<AddressModel>>() {
                    override fun onSuccess(model: LzyResponse<AddressModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (model.data != null) {
                            addressmodel = model.data as AddressModel
                            name_tv.text = model.data!!.name
                            tel_tv.text = model.data!!.tel
                            address_tv.text = model.data!!.province + model.data!!.city + model.data!!.area+model.data!!.address
                            qq_tv.text = "QQ：" + model.data!!.qq
                            ll_addressview.visibility = View.VISIBLE
                            add_btn.visibility = View.GONE
                        } else {
                            ll_addressview.visibility = View.GONE
                            add_btn.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        ll_addressview.visibility = View.GONE
                        add_btn.visibility = View.VISIBLE
                        //toast(common().toast_error(e!!))
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == 77) {
            //刷新
            initEvents()
        }
    }
}
