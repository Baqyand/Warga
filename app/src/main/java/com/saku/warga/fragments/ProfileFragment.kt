package com.saku.warga.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.saku.warga.LoginActivity
import com.saku.warga.Preferences
import com.saku.warga.R
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.bottomsheet.DataDiri
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    lateinit var myview : View
    var nama :String? = null
    var tglLahir :String? = null
    var noHp :String? = null
    var stsPenghuni :String? = null
    var alias :String? = null
    var foto :String? = null
    var preferences  = Preferences()
    val mydata = DataDiri()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_profile, container, false)
        preferences.setPreferences(context!!)
        return myview
    }

    override fun onResume() {
        super.onResume()
        initData()
        val prev = fragmentManager!!.findFragmentByTag("Data Diri Bottomsheet")
        if(prev != null){
            mydata.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    fun initView(){
        myview.btn_data_pribadi.setOnClickListener {
            mydata.initData(nama!!,noHp!!,tglLahir!!,alias!!,foto!!)
            fragmentManager?.let { it1 -> mydata.show(it1,"Data Diri Bottomsheet") }
        }
        myview.btn_bantuan.setOnClickListener { Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show() }
        myview.btn_keluar.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            preferences.preferencesLogout()
            activity?.finishAffinity() }
        myview.btn_info_rumah.setOnClickListener { Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show() }
    }

    fun initData() {
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.profile_warga()?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {

                            val obj = JSONObject(response.body()!!.string())
                            val data = JSONArray(obj.optString("user"))
                            for (counter in 0 until data.length()) {
                                val jsonObj: JSONObject = data.getJSONObject(counter)
                                nama = jsonObj.optString("nama")
                                noHp = jsonObj.optString("no_hp")
                                alias = jsonObj.optString("alias")
                                tglLahir = jsonObj.optString("tgl_lahir")
                                stsPenghuni = jsonObj.optString("status_huni")
                                foto = jsonObj.optString("foto")
                            }
                            myview.nama.text = nama
                            myview.status_penghuni.text = stsPenghuni
                            Glide.with(context!!).load(foto).error(R.drawable.ic_avatar).into(myview.image_profile)
                        } catch (e: Exception) {

                        }
                    }else{
                        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    activity?.finish()
                    Toast.makeText(context, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(context, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(context, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    interface BottomSheetListener {
//        fun onButtonClicked(text: String?)
//    }
//
//
//    override fun onButtonClicked(text: String?) {
//        TODO("Not yet implemented")
//    }
}
