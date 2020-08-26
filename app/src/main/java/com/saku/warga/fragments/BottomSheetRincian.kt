package com.saku.warga.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.*
import com.saku.warga.adapter.RincianIuranAdapter
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.models.IuranData
import kotlinx.android.synthetic.main.bottom_sheet_rincian.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class BottomSheetRincian: BottomSheetDialogFragment() {
    lateinit var myview : View
    val library = Library()
    var tunggakan = 0.0
    var pembayaran = 0.0
    var total = 0.0
    lateinit var mycontext: Context
    var preferences  = Preferences()
    private lateinit var dataAdapter: RincianIuranAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mycontext=context
        preferences.setPreferences(context)
        dataAdapter = RincianIuranAdapter(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview.bs_bayar.setOnClickListener {
            val intent = Intent(context,BayarActivity::class.java)
            intent.putExtra("use","iuran")
            intent.putExtra("beban",(total).toString())
            startActivity(intent)
        }
        myview.recyclerview.layoutManager = LinearLayoutManager(context)
        myview.recyclerview.adapter = dataAdapter
        getIuran()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.bottom_sheet_rincian, container, false)
        myview.bs_tutup.setOnClickListener { dismiss() }
        myview.total_rincian.text = library.toRupiah(total)
        return myview
    }

    fun setTotalRincian(total: Double){
        this.total=total
    }

    fun getIuran(){
        val apiservice = UtilsApi().getAPIService(mycontext)
        apiservice?.kartu_iuran(library.getYear())?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            val gson = Gson()
                            val type: Type = object :
                                TypeToken<MutableList<IuranData?>?>() {}.type
                            val data: MutableList<IuranData> =
                                gson.fromJson(obj.optString("data"), type)
//                            for(a in 0 until library.getMonthM().toInt()){
//                                tunggakan += data[a].bill.toDouble()
//                                pembayaran += data[a].bayar.toDouble()
//                            }
                            dataAdapter.initData(data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    activity?.finishAffinity()
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

}