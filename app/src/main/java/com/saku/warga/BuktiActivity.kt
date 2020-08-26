package com.saku.warga

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_bukti.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class BuktiActivity : AppCompatActivity() {
    private lateinit var file : File
    lateinit var fileUri : Uri
    private var imagepath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bukti)
        back.setOnClickListener {
            onBackPressed()
        }
        expand.setOnClickListener {
            if(langkah_pembayaran.visibility == View.GONE){
                langkah_pembayaran.visibility = View.VISIBLE
                bukti.scaleX = 0.15F
                bukti.scaleY = 0.15F
            }else{
                langkah_pembayaran.visibility = View.GONE
                bukti.scaleX = 0.1F
                bukti.scaleY = 0.1F
            }
        }

        bukti.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .saveDir(File(Environment.getExternalStorageDirectory(), "Warga"))
                .start()
        }
        button.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                fileUri = data?.data!!

                //You can get File object from intent
                file= ImagePicker.getFile(data)!!
                //You can also get File Path from intent
                imagepath = ImagePicker.getFilePath(data).toString()
                bukti.scaleX = 1F
                bukti.scaleY = 1F
                buktitext.visibility = View.GONE
                bukti.setImageURI(fileUri)

//                dialog.civ_image.setImageURI(fileUri)
//                dialog.btn_edit.visibility=View.VISIBLE
//                dialog.image_avatar.visibility=View.GONE
//                dialog.image_text.visibility=View.GONE

            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this@BuktiActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this@BuktiActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
