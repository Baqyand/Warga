package com.saku.warga

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.adapter.LaporanPageAdapter
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.models.PeriodeData
import com.saku.warga.models.TahunData
import kotlinx.android.synthetic.main.activity_laporan.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class LaporanActivity : AppCompatActivity() {
    var preferences  = Preferences()
    val dataBulan = mutableListOf<String>()
    val intBulan = mutableListOf<String>()
    val tahun = mutableListOf<String>()
    val periodeBulan = mutableListOf<String>()
    val library=Library()
    var positionRT=0
    var positionRW=0
    var positionTahun=0
    lateinit var bulan:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)
        preferences.setPreferences(this)
        back.setOnClickListener {
            onBackPressed()
        }
        getTahun()
        getBulan()
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as MarginLayoutParams
            p.setMargins(17, 0, 17, 0)
            tab.requestLayout()
        }
        val pageAdapter =
            LaporanPageAdapter(
                supportFragmentManager,
                tabLayout.tabCount
            )
        viewpager.offscreenPageLimit = 3
        viewpager.adapter = pageAdapter
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
                when (tab.position) {
                    0 -> {
                        filter_spinner.setItems(dataBulan)
                        filter_spinner.selectedIndex = positionRT
                        filter_spinner.setOnItemSelectedListener { _, position, _, _ ->
                            positionRT=position
                            val mydata = Intent("periode_rt_trigger")
                            mydata.putExtra("periode_rt", periodeBulan[position])
                            LocalBroadcastManager.getInstance(this@LaporanActivity).sendBroadcast(mydata)
                        }
                    }
                    1 -> {
                        filter_spinner.setItems(dataBulan)
                        filter_spinner.selectedIndex = positionRW
                        filter_spinner.setOnItemSelectedListener { _, position, _, _ ->
                            positionRW=position
                            val mydata = Intent("periode_rt_trigger")
                            mydata.putExtra("periode_rw", periodeBulan[position])
                            LocalBroadcastManager.getInstance(this@LaporanActivity).sendBroadcast(mydata)
                        }
                    }
                    else -> {
                        filter_spinner.setItems(tahun)
                        filter_spinner.selectedIndex = positionTahun
                        filter_spinner.setOnItemSelectedListener { _, position, _, _ ->
                            positionTahun=position
                            val mydata = Intent("periode_rt_trigger")
                            mydata.putExtra("tahun", tahun[position].substringAfter(" "))
                            LocalBroadcastManager.getInstance(this@LaporanActivity).sendBroadcast(mydata)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


//        tabLayout.setupWithViewPager(viewpager)
    }

    private fun getBulan(){
        val apiservice = UtilsApi().getAPIService(this@LaporanActivity)
        apiservice?.list_bulan()?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            dataBulan.clear()
                            intBulan.clear()
                            val obj = JSONObject(response.body()!!.string())
                            val gson = Gson()
                            val type: Type = object :
                                TypeToken<ArrayList<PeriodeData?>?>() {}.type
                            val data: ArrayList<PeriodeData> =
                                gson.fromJson(obj.optString("data"), type)
                            data.sortByDescending { it.periode }
                            for(i in 0 until data.size){

                                dataBulan.add(data[i].nama + " - "+data[i].tahun)
                                periodeBulan.add(data[i].periode)
                            }
//
                            filter_spinner.setItems(dataBulan)
//                            filter_spinner.selec = library.getMonthM().toInt()-1
//
//                            dataAdapter.initData(data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@LaporanActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@LaporanActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(this@LaporanActivity, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    finishAffinity()
                    Toast.makeText(this@LaporanActivity, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@LaporanActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(this@LaporanActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@LaporanActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTahun(){
        val apiservice = UtilsApi().getAPIService(this@LaporanActivity)
        apiservice?.list_tahun()?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
//                            dataBulan.clear()
//                            intBulan.clear()
//                            val dataobj:JSONArray = obj.optJSONArray("data")
//                            for(i in 0 until dataobj.length()){
//                                val myobj = dataobj.optJSONObject(i)
//                                dataBulan.add(myobj.optString("nama")+" "+library.getYear())
//                                intBulan.add(myobj.optString("bulan")+" "+library.getYear())
//                                intBulan.add(myobj.optString("bulan")+" "+library.getYear())
//                            }
//                            dataBulan.reverse()
//                            intBulan.reverse()


                            val obj = JSONObject(response.body()!!.string())
                            val gson = Gson()
                            val type: Type = object :
                                TypeToken<MutableList<TahunData?>?>() {}.type
                            val data: MutableList<TahunData> =
                                gson.fromJson(obj.optString("data"), type)
                            data.sortByDescending { it.tahun }
                            for(i in 0 until data.size){
                                tahun.add("Tahun "+data[i].tahun)
                            }
//                            filter_spinner.setItems(dataBulan)
//                            filter_spinner.selec = library.getMonthM().toInt()-1
//
//                            dataAdapter.initData(data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@LaporanActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@LaporanActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(this@LaporanActivity, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    finishAffinity()
                    Toast.makeText(this@LaporanActivity, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@LaporanActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(this@LaporanActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@LaporanActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
