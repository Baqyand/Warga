package com.saku.warga

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.saku.warga.apihelper.UtilsApi
import kotlinx.android.synthetic.main.activity_ubah_data_diri.*
import kotlinx.android.synthetic.main.activity_ubah_data_diri.btn_edit
import kotlinx.android.synthetic.main.activity_ubah_data_diri.civ_image
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UbahDataDiriActivity : AppCompatActivity() {
    var preferences  = Preferences()
    private var imagepath = ""
    private lateinit var file : File
    private lateinit var `fun` : Functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_data_diri)
        preferences.setPreferences(this)
        `fun` = Functions(applicationContext)
        getDataFromIntent()
        btn_edit.setOnClickListener {
            ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .saveDir(File(Environment.getExternalStorageDirectory(), "Warga"))
            .start() }
        btn_simpan.setOnClickListener {
            when {
                et_nama.text.toString().trim()=="" -> {
                    et_nama.requestFocus()
                    et_nama.error = "Data belum terisi"
                }
                et_notelp.text.toString().trim()=="" -> {
                    et_notelp.requestFocus()
                    et_notelp.error = "Data belum terisi"
                }
                et_alias.text.toString().trim()=="" -> {
                    et_alias.requestFocus()
                    et_alias.error = "Data belum terisi"
                }
                imagepath=="" -> {
                    ubahData(et_nama.text.toString(),et_alias.text.toString(),et_notelp.text.toString())
                }
                else -> {
                    val fileReqBody: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val imagedata: MultipartBody.Part =
                        MultipartBody.Part.createFormData("foto", file.name, fileReqBody)
                    ubahDataWithImage(imagedata,`fun`.toRequestBody(et_nama.text.toString()),`fun`.toRequestBody(et_alias.text.toString()),`fun`.toRequestBody(et_notelp.text.toString()))
                }
            }
        }
    }

    fun getDataFromIntent(){
        if(intent.hasExtra("nama")){
            try {
                Glide.with(this).load(intent.getStringExtra("foto")).error(R.drawable.ic_avatar).into(civ_image)
                et_nama.setText(intent.getStringExtra("nama"))
                et_notelp.setText(intent.getStringExtra("no_telp"))
                et_alias.setText(intent.getStringExtra("alias"))
            } catch (e: Exception) {
            }
        }
    }

    fun ubahData(nama:String,alias:String,no_hp:String) {
        val apiservice = UtilsApi().getAPIService(this@UbahDataDiriActivity)
        apiservice?.ubahProfile(nama,alias,no_hp)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            Toast.makeText(this@UbahDataDiriActivity, obj.optString("message"), Toast.LENGTH_SHORT).show()
                            finish()
                        } catch (e: Exception) {

                        }
                    }else{
                        Toast.makeText(this@UbahDataDiriActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@UbahDataDiriActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(this@UbahDataDiriActivity, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    finishAffinity()
                    Toast.makeText(this@UbahDataDiriActivity, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@UbahDataDiriActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(this@UbahDataDiriActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@UbahDataDiriActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun ubahDataWithImage(foto:MultipartBody.Part,nama:RequestBody,alias:RequestBody,no_hp:RequestBody) {
        val apiservice = UtilsApi().getAPIService(this@UbahDataDiriActivity)
        apiservice?.ubahProfileWithImage(foto,nama,alias,no_hp)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            Toast.makeText(this@UbahDataDiriActivity, obj.optString("message"), Toast.LENGTH_SHORT).show()
                            finish()
                        } catch (e: Exception) {

                        }
                    }else{
                        Toast.makeText(this@UbahDataDiriActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@UbahDataDiriActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    val intent = Intent(this@UbahDataDiriActivity, LoginActivity::class.java)
                    startActivity(intent)
                    preferences.preferencesLogout()
                    finishAffinity()
                    Toast.makeText(this@UbahDataDiriActivity, "Sesi telah berakhir, silahkan login kembali", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@UbahDataDiriActivity, "Unauthorized", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404){
                    Toast.makeText(this@UbahDataDiriActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@UbahDataDiriActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                civ_image.setImageURI(fileUri)

                //You can get File object from intent
                file= ImagePicker.getFile(data)!!

                //You can also get File Path from intent
                imagepath = ImagePicker.getFilePath(data).toString()
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
