package com.saku.warga.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.Library
import com.saku.warga.LoginActivity
import com.saku.warga.Preferences
import com.saku.warga.R
import com.saku.warga.adapter.LaporanDataAdapter
import com.saku.warga.adapter.TransaksiTerakhirAdapter
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.models.PemasukanData
import com.saku.warga.models.TransaksiData
import kotlinx.android.synthetic.main.fragment_laporan.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class MonthFragment(val jenis:String) : Fragment() {
    var preferences  = Preferences()
    private lateinit var myview : View
    val data=ArrayList<PemasukanData>()
    lateinit var adapterPenerimaan:LaporanDataAdapter
    lateinit var adapterPengeluaran:LaporanDataAdapter
    lateinit var adapterPenerimaanThn:LaporanDataAdapter
    lateinit var adapterPengeluaranThn:LaporanDataAdapter
    var library = Library()
    private lateinit var dataAdapter: TransaksiTerakhirAdapter
    lateinit var mycontext :Context
    var totalPenerimaan=0.0
    var totalPengeluaran=0.0

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(intent.hasExtra("periode_rt")) {
                getRekapRT(intent.getStringExtra("periode_rt"))
            }
            if(intent.hasExtra("periode_rw")){
                getRekapRW(intent.getStringExtra("periode_rw"))
            }
            if(intent.hasExtra("tahun")){
                getRekapRWTahun(intent.getStringExtra("tahun"))
            }
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferences.setPreferences(context)
        dataAdapter = TransaksiTerakhirAdapter(context)
        adapterPenerimaan = LaporanDataAdapter(context)
        adapterPengeluaran = LaporanDataAdapter(context)
        adapterPenerimaanThn = LaporanDataAdapter(context)
        adapterPengeluaranThn = LaporanDataAdapter(context)
        mycontext=context

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(mycontext)
            .registerReceiver(mMessageReceiver, IntentFilter("periode_rt_trigger"))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(mycontext)
            .unregisterReceiver(mMessageReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (jenis) {
            "rt" -> {
                myview.recyclerview.layoutManager = LinearLayoutManager(context)
                myview.recyclerview.adapter = dataAdapter
                getRekapRT(library.getPeriode())
            }
            "annual" -> {
                myview.recyclerview_pemasukan_tahun.layoutManager = LinearLayoutManager(context)
                myview.recyclerview_pengeluaran_tahun.layoutManager = LinearLayoutManager(context)
                myview.recyclerview_pemasukan_tahun.adapter = adapterPenerimaanThn
                myview.recyclerview_pengeluaran_tahun.adapter = adapterPengeluaranThn
                myview.collapse_pemasukan_tahun.setOnClickListener {
                    if(myview.recyclerview_pemasukan_tahun.visibility == View.VISIBLE){
                        myview.recyclerview_pemasukan_tahun.visibility = View.GONE
                    }else{
                        myview.recyclerview_pemasukan_tahun.visibility = View.VISIBLE
                    }
                }
                myview.collapse_pembayaran_tahun.setOnClickListener {
                    if(myview.recyclerview_pengeluaran_tahun.visibility == View.VISIBLE){
                        myview.recyclerview_pengeluaran_tahun.visibility = View.GONE
                    }else{
                        myview.recyclerview_pengeluaran_tahun.visibility = View.VISIBLE
                    }
                }
                getRekapRWTahun(library.getYear())
            }
            else -> {
                myview.recyclerview_pemasukan.layoutManager = LinearLayoutManager(context)
                myview.recyclerview_pengeluaran.layoutManager = LinearLayoutManager(context)
                myview.recyclerview_pemasukan.adapter = adapterPenerimaan
                myview.recyclerview_pengeluaran.adapter = adapterPengeluaran
                myview.collapse_pemasukan.setOnClickListener {
                    if(myview.recyclerview_pemasukan.visibility == View.VISIBLE){
                        myview.recyclerview_pemasukan.visibility = View.GONE
                    }else{
                        myview.recyclerview_pemasukan.visibility = View.VISIBLE
                    }
                }
                myview.collapse_pembayaran.setOnClickListener {
                    if(myview.recyclerview_pengeluaran.visibility == View.VISIBLE){
                        myview.recyclerview_pengeluaran.visibility = View.GONE
                    }else{
                        myview.recyclerview_pengeluaran.visibility = View.VISIBLE
                    }
                }
                getRekapRW(library.getPeriode())

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview =inflater.inflate(R.layout.fragment_laporan, container, false)
        when (jenis) {
            "rt" -> {
                myview.transaksi_layout.visibility = View.VISIBLE
                myview.saldo_akhir_rt.visibility = View.VISIBLE
                myview.transaksi_layout_text.visibility = View.VISIBLE
            }
            "annual" -> {
                myview.card_pemasukkan_tahun.visibility = View.VISIBLE
                myview.card_pengeluaran_tahun.visibility = View.VISIBLE
                myview.saldo_akhir_rw_tahun.visibility = View.VISIBLE
            }
            else -> {
                myview.card_pemasukkan.visibility = View.VISIBLE
                myview.card_pengeluaran.visibility = View.VISIBLE
                myview.saldo_akhir_rw.visibility = View.VISIBLE
            }
        }

        return myview
    }


    fun getRekapRW(periode: String) {
        val tahun=periode.substring(0,4)
        val bulan = periode.substring(4)
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.rekap_rw_bulan(tahun,bulan)?.enqueue(object : Callback<ResponseBody?> {
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
                                TypeToken<MutableList<PemasukanData?>?>() {}.type
                            val penerimaan: MutableList<PemasukanData> =
                                gson.fromJson(obj.optString("penerimaan"), type)
                            val pengeluaran: MutableList<PemasukanData> =
                                gson.fromJson(obj.optString("pengeluaran"), type)

                            for(i in 0 until penerimaan.size){
                                totalPenerimaan+=penerimaan[i].total.toDouble()
                            }
                            for(i in 0 until pengeluaran.size){
                                totalPengeluaran+=pengeluaran[i].total.toDouble()
                            }

                            adapterPenerimaan.initData(penerimaan)
                            adapterPengeluaran.initData(pengeluaran)

                            myview.total_pemasukan.text = library.toRupiah(totalPenerimaan)
                            myview.total_pengeluaran.text = library.toRupiah(totalPengeluaran)
                            myview.saldo_akhir_rw.text = library.toRupiah(obj.optDouble("saldo_akhir"))

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

    fun getRekapRWTahun(tahun: String) {
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.rekap_rw_tahun(tahun)?.enqueue(object : Callback<ResponseBody?> {
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
                                TypeToken<MutableList<PemasukanData?>?>() {}.type
                            val penerimaan: MutableList<PemasukanData> =
                                gson.fromJson(obj.optString("penerimaan"), type)
                            val pengeluaran: MutableList<PemasukanData> =
                                gson.fromJson(obj.optString("pengeluaran"), type)

                            for(i in 0 until penerimaan.size){
                                totalPenerimaan+=penerimaan[i].total.toDouble()
                            }
                            for(i in 0 until pengeluaran.size){
                                totalPengeluaran+=pengeluaran[i].total.toDouble()
                            }

                            adapterPenerimaanThn.initData(penerimaan)
                            adapterPengeluaranThn.initData(pengeluaran)

                            myview.total_pemasukan_tahun.text = library.toRupiah(totalPenerimaan)
                            myview.total_pengeluaran_tahun.text = library.toRupiah(totalPengeluaran)
                            myview.saldo_akhir_rw_tahun.text = library.toRupiah(obj.optDouble("saldo_akhir"))

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

    fun getRekapRT(periode:String){
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.riwayat_transaksi(periode)?.enqueue(object : Callback<ResponseBody?> {
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
                                TypeToken<MutableList<TransaksiData?>?>() {}.type
                            val data: MutableList<TransaksiData> =
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
                                        myview.empty_view.visibility = View.VISIBLE
                                        myview.recyclerview.visibility = View.GONE
                                    } else {
                                        myview.empty_view.visibility = View.GONE
                                        myview.recyclerview.visibility = View.VISIBLE
                                    }

                                }
                            })
                            myview.saldo_akhir_rt.text = library.toRupiah(obj.optDouble("saldo"))
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
