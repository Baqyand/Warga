package com.saku.warga.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.*
import com.saku.warga.adapter.KartuAdapter
import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.models.IuranData
import kotlinx.android.synthetic.main.fragment_kartu.*
import kotlinx.android.synthetic.main.fragment_kartu.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class KartuFragment : Fragment() {
    private val tahunArray= mutableListOf<String>()
    private lateinit var dataAdapter: KartuAdapter
    var preferences  = Preferences()
    private lateinit var myview : View
    val library= Library()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferences.setPreferences(context)
        dataAdapter = KartuAdapter(context)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_kartu, container, false)
        myview.lunasi.setOnClickListener {
            val intent = Intent(context,IuranActivity::class.java)
            startActivity(intent)
        }
        val tah = library.getYear().toInt()
        for (i in 2015..tah) {
            tahunArray.add("Tahun $i")
        }
        tahunArray.reverse()
        myview.spinner.setItems(tahunArray)
        myview.spinner.selectedIndex = 0
        myview.spinner.setOnItemSelectedListener { view, position, id, item ->
            getIuran(item.toString().substringAfter(" "))
        }

        return myview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview.recyclerview.layoutManager = LinearLayoutManager(context)
        myview.recyclerview.adapter = dataAdapter
        getIuran(library.getYear())
//        dataArray.add(ModelKartu("Jan","150000.0","150000.0"))
//        dataArray.add(ModelKartu("Feb","150000.0","150000.0"))
//        dataArray.add(ModelKartu("Mar","150000.0","0.0"))
//        dataArray.add(ModelKartu("Apr","150000.0","0.0"))
//        dataAdapter.initData(dataArray)
//        var tunggakan=0.0
//        for(i in 0 until dataArray.size){
//            tunggakan += dataArray[i].tagihan.substringBefore(".").toDouble() - dataArray[i].bayar.substringBefore(".").toDouble()
//        }
//        val localeID = Locale("in", "ID")
//        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        var sortBayar=true
        var sortTagihan=true
        sort_bayar.setOnClickListener {
            sortBayar = if(sortBayar){
                dataAdapter.bayarDescending()
                false
            }else{
                dataAdapter.bayarAscending()
                true
            }
        }
        sort_tagihan.setOnClickListener {
            sortTagihan = if(sortTagihan){
                dataAdapter.tagihanDescending()
                false
            }else{
                dataAdapter.tagihanAscending()
                true
            }
        }
    }

    fun getIuran(tahun:String){
        dataAdapter.clearData()
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.kartu_iuran(tahun)?.enqueue(object : Callback<ResponseBody?> {
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
                            var tunggakan = 0.0
                            var pembayaran = 0.0

                            for(a in 0 until library.getMonthM().toInt()){
                                tunggakan += data[a].bill.toDouble()
                                pembayaran += data[a].bayar.toDouble()
                            }
                            myview.tunggakan.text = "Tunggakan ${library.toRupiah(tunggakan-pembayaran)}"
                            if(tunggakan-pembayaran>1){
                                myview.layout_tunggakan.visibility = View.VISIBLE
                            }
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
