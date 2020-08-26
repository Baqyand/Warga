package com.saku.warga.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saku.warga.*
import com.saku.warga.adapter.InformasiAdapter

import com.saku.warga.apihelper.UtilsApi
import com.saku.warga.models.InformasiData
import kotlinx.android.synthetic.main.fragment_beranda.*
import kotlinx.android.synthetic.main.fragment_beranda.view.*
import kotlinx.android.synthetic.main.popup_input.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.reflect.Type

class BerandaFragment : Fragment() {
    var preferences  = Preferences()
    private lateinit var myview : View
    private var imagepath = ""
    private var nama :String? = null
    private var alias :String? = null
    private var noHp :String? = null
    private lateinit var file : File
    private var fileUri : Uri? = null
    private lateinit var dialog : Dialog
    private lateinit var `fun` : Functions
    private val dataArray= mutableListOf<InformasiData>()
    private lateinit var dataAdapter:InformasiAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        `fun` = Functions(context)
        preferences.setPreferences(context)
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_input)
        dataAdapter = InformasiAdapter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myview = inflater.inflate(R.layout.fragment_beranda, container, false)

        myview.recyclerview.layoutManager = LinearLayoutManager(context)
        myview.recyclerview.adapter = dataAdapter
        initData()
        return myview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview.btn_iuran_menu.setOnClickListener { val intent = Intent(context, IuranActivity::class.java)
            startActivity(intent) }
        myview.btn_laporan_menu.setOnClickListener { val intent = Intent(context, LaporanActivity::class.java)
            startActivity(intent) }
        myview.btn_donasi_menu.setOnClickListener { val intent = Intent(context, DonasiActivity::class.java)
            startActivity(intent) }
//        dataArray.add(InformasiData("Tagihan Iuran sudah jatuh tempo","2"))
//        dataArray.add(InformasiData("Pembayaran sedang dalam proses verifikasi","1"))
//        dataArray.add(InformasiData("Mohon lakukan verifikasi pembayaran","0"))
        dataAdapter.addData(dataArray)
        getInformasi()
        myview.refresh_layout.setOnRefreshListener { getInformasi() }
    }

    private fun getInformasi(){
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.informasi()?.enqueue(object : Callback<ResponseBody?> {
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
                                TypeToken<MutableList<InformasiData?>?>() {}.type
                            val data: MutableList<InformasiData> =
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



//    fun getNotif() = CoroutineScope(Dispatchers.IO).launch{
//        val response = ma.api.profile()
//        when {
//            response.isSuccessful -> {
//                if (response.body() != null) {
//                    try {
//                        val obj = JSONObject(response.body()!!.string())
//                        Log.e("Response:", obj.toString())
//
//                    } catch (e: Exception) {
//
//                    }
//                }else{
////                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
//                }
//            }
//            response.code() == 422 -> {
////                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
//            }
//            response.code() == 401 -> {
////                val intent = Intent(context, LoginActivity::class.java)
////                startActivity(intent)
////                preferences.preferencesLogout()
////                activity?.finish()
////                Toast.makeText(context, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
//            }
//            response.code() == 403 -> {
////                Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show()
//            }
//            response.code() == 404 -> {
////                Toast.makeText(context, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


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
                            var foto : String
                            for (counter in 0 until data.length()) {
                                val jsonObj: JSONObject = data.getJSONObject(counter)
                                foto = jsonObj.optString("foto")
                                nama= jsonObj.optString("nama")
                                alias= jsonObj.optString("alias")
                                noHp= jsonObj.optString("no_hp")
                                preferences.saveKodePP(jsonObj.optString("kode_pp"))
                                preferences.saveKodeRumah(jsonObj.optString("no_rumah"))
                                if(foto.trim()=="-"){
                                    showDialog()
                                }
                            }

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

    fun showDialog() {
        dialog.setCancelable(false)
        dialog.btn_close.setOnClickListener { dialog.hide() }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.input_image.setOnClickListener{
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .saveDir(File(Environment.getExternalStorageDirectory(), "Warga"))
                .start()
        }
        dialog.btn_edit.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .saveDir(File(Environment.getExternalStorageDirectory(), "Warga"))
                .start()
        }
        dialog.show()
    }

    private fun ubahDataWithImage(foto: MultipartBody.Part, nama: RequestBody, alias: RequestBody, no_hp: RequestBody) {
        val apiservice = UtilsApi().getAPIService(context!!)
        apiservice?.ubahProfileWithImage(foto,nama,alias,no_hp)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            Toast.makeText(context, obj.optString("message"), Toast.LENGTH_SHORT).show()

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
                    activity?.finishAffinity()
                    Toast.makeText(context, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404 || response.code()==405){
                    Toast.makeText(context, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(context, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                fileUri = data?.data

                //You can get File object from intent
                file= ImagePicker.getFile(data)!!
                //You can also get File Path from intent
                imagepath = ImagePicker.getFilePath(data).toString()

                dialog.civ_image.setImageURI(fileUri)
                dialog.btn_edit.visibility=View.VISIBLE
                dialog.image_avatar.visibility=View.GONE
                dialog.image_text.visibility=View.GONE
                val fileReqBody: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val imagedata: MultipartBody.Part =
                    MultipartBody.Part.createFormData("foto", file.name, fileReqBody)
                ubahDataWithImage(imagedata,`fun`.toRequestBody(nama!!),`fun`.toRequestBody(alias!!),`fun`.toRequestBody(noHp!!))
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
