package com.saku.warga.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.saku.warga.Functions
import com.saku.warga.LoginActivity
import com.saku.warga.Preferences

import com.saku.warga.R
import com.saku.warga.apihelper.UtilsApi
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

class BerandaFragment : Fragment() {
    var preferences  = Preferences()
    lateinit var myview : View
    private var imagepath = ""
    private var nama :String? = null
    private var alias :String? = null
    private var noHp :String? = null
    private lateinit var file : File
    private var fileUri : Uri? = null
    lateinit var dialog : Dialog
    private lateinit var `fun` : Functions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myview = inflater.inflate(R.layout.fragment_beranda, container, false)
        `fun` = Functions(context!!)
        preferences.setPreferences(context!!)
        dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_input)
        initData()
//        showDialog(activity)
        return myview
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
                            var foto : String
                            for (counter in 0 until data.length()) {
                                val jsonObj: JSONObject = data.getJSONObject(counter)
                                foto = jsonObj.optString("foto")
                                nama= jsonObj.optString("nama")
                                alias= jsonObj.optString("alias")
                                noHp= jsonObj.optString("no_hp")
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
//        val dialog = Dialog(activity!!)
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

    fun ubahDataWithImage(foto: MultipartBody.Part, nama: RequestBody, alias: RequestBody, no_hp: RequestBody) {
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
                } else if(response.code() == 404){
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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview.btn_iuran_menu.setOnClickListener { Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show() }
        myview.btn_laporan_menu.setOnClickListener { Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show() }
        myview.btn_donasi_menu.setOnClickListener { Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show() }
    }
}
