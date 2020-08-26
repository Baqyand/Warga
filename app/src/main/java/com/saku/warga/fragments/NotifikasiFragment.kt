package com.saku.warga.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.Functions
import com.saku.warga.LoginActivity
import com.saku.warga.Preferences

import com.saku.warga.R
import com.saku.warga.adapter.NotifikasiAdapter
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.models.NotifikasiData
import kotlinx.android.synthetic.main.fragment_notifikasi.*
import kotlinx.android.synthetic.main.fragment_notifikasi.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 * A simple [Fragment] subclass.
 */
class NotifikasiFragment : Fragment() {
    private lateinit var myview : View
    private lateinit var functions: Functions
    private val dataArray= mutableListOf<NotifikasiData>()
    private lateinit var dataAdapter:NotifikasiAdapter
    var preferences  = Preferences()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_notifikasi, container, false)
        dataAdapter = NotifikasiAdapter(context!!)
        functions = Functions(context!!)
        preferences.setPreferences(context!!)
        myview.recyclerview.layoutManager = LinearLayoutManager(context)
        myview.recyclerview.adapter = dataAdapter
        return myview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        dataArray.add(NotifikasiData("donasi","Donasi","08 Juli 2020","Donasi Baru","Pemasukan donasi dari jamaah Masjid sebesar Rp. 50.000"))
//        dataArray.add(NotifikasiData("laporan","Laporan","06 Juli 2020","Laporan Keuangan RT","Total Saldo Dana RT 05 saat ini adalah Rp. 243.000.000 "))
//        dataArray.add(NotifikasiData("iuran","Iuran","03 Juli 2020","Pembayaran Sukses","Pembayaran telah diverifikasi pada tanggal 09 Juli 2020 Jam 15.00"))
//        dataAdapter.addData(dataArray)
        getNotifikasi()
        refresh_layout.setOnRefreshListener { getNotifikasi() }
    }

    fun getNotifikasi(){
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.notifikasi()?.enqueue(object : Callback<ResponseBody?> {
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
                                TypeToken<MutableList<NotifikasiData?>?>() {}.type
                            val data: MutableList<NotifikasiData> =
                                gson.fromJson(obj.optString("data"), type)
                            dataAdapter.registerAdapterDataObserver(object :
                                RecyclerView.AdapterDataObserver() {
                                override fun onChanged() {
                                    super.onChanged()
                                    checkEmpty()
                                }

                                override fun onItemRangeInserted(
                                    positionStart: Int,
                                    itemCount: Int
                                ) {
                                    super.onItemRangeInserted(positionStart, itemCount)
                                    checkEmpty()
                                }

                                override fun onItemRangeRemoved(
                                    positionStart: Int,
                                    itemCount: Int
                                ) {
                                    super.onItemRangeRemoved(positionStart, itemCount)
                                    checkEmpty()
                                }

                                fun checkEmpty() {
                                    if (dataAdapter.itemCount == 0 || dataAdapter.itemCount < 1) {
                                        empty_view.visibility = View.VISIBLE
                                        recyclerview.visibility = View.GONE
                                    } else {
                                        empty_view.visibility = View.GONE
                                        recyclerview.visibility = View.VISIBLE
                                    }

                                }
                            })
                            dataAdapter.clearData()
                            dataAdapter.addData(data)
                            myview.refresh_layout.isRefreshing = false
                        } catch (e: Exception) {
                            myview.refresh_layout.isRefreshing = false
                        }
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                        myview.refresh_layout.isRefreshing = false
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    myview.refresh_layout.isRefreshing = false
                } else if(response.code() == 401){
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    activity?.finish()
                    Toast.makeText(context, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show()
                    myview.refresh_layout.isRefreshing = false
                } else if(response.code() == 404){
                    Toast.makeText(context, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                    myview.refresh_layout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(context, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
                myview.refresh_layout.isRefreshing = false
            }
        })
    }


    fun getNotif(){
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.notifikasi()?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {

                            val obj = JSONObject(response.body()!!.string())
//                            val data = JSONArray(obj.optString("user"))
//                            var foto : String
//                            for (counter in 0 until data.length()) {
//                                val jsonObj: JSONObject = data.getJSONObject(counter)
//                                foto = jsonObj.optString("foto")
//                                nama= jsonObj.optString("nama")
//                                alias= jsonObj.optString("alias")
//                                noHp= jsonObj.optString("no_hp")
//                                if(foto.trim()=="-"){
//                                    showDialog()
//                                }
//                            }

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
}
