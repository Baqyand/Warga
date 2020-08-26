package com.saku.warga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.adapter.IuranAdapter
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.fragments.BottomSheetRincian
import com.saku.warga.models.IuranData
import com.saku.warga.models.RiwayatIuranData
import kotlinx.android.synthetic.main.activity_iuran.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class IuranActivity : AppCompatActivity() {
    private lateinit var dataAdapter: IuranAdapter
    var preferences  = Preferences()
    val library=Library()
    var tunggakan = 0.0
    var pembayaran = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iuran)
        preferences.setPreferences(this)
        back.setOnClickListener {
            onBackPressed()
        }
        rincian.setOnClickListener {
            val bs =BottomSheetRincian()
            bs.setTotalRincian(tunggakan-pembayaran)
            bs.show(supportFragmentManager,"BottomSheetRincian")
        }

        bayar.setOnClickListener {
            val intent = Intent(this@IuranActivity, BayarActivity::class.java)
            intent.putExtra("use","iuran")
            intent.putExtra("beban",(tunggakan-pembayaran).toString())
            startActivity(intent)
        }

        dataAdapter = IuranAdapter(this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = dataAdapter
        getIuran()
        getRiwayatIuran()
    }

    fun getIuran(){

        val apiservice = UtilsApi().getAPIService(this@IuranActivity)
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
                            for(a in 0 until library.getMonthM().toInt()){
                                tunggakan += data[a].bill.toDouble()
                                pembayaran += data[a].bayar.toDouble()
                            }
                            beban_bayar.text = library.toRupiah(tunggakan-pembayaran)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@IuranActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@IuranActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(this@IuranActivity, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    finishAffinity()
                    Toast.makeText(this@IuranActivity, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@IuranActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(this@IuranActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@IuranActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun getRiwayatIuran(){
        val apiservice = UtilsApi().getAPIService(this@IuranActivity)
        apiservice?.riwayat_iuran(preferences.getKodePP()!!,preferences.getKodeRumah()!!,library.getPeriode())?.enqueue(object : Callback<ResponseBody?> {
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
                                TypeToken<MutableList<RiwayatIuranData?>?>() {}.type
                            val data: MutableList<RiwayatIuranData> =
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
                            dataAdapter.initData(data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@IuranActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@IuranActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(this@IuranActivity, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    finishAffinity()
                    Toast.makeText(this@IuranActivity, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@IuranActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(this@IuranActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@IuranActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
