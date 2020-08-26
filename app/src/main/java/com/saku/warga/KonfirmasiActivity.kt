package com.saku.warga

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_konfirmasi.*

class KonfirmasiActivity : AppCompatActivity() {
    lateinit var nilaiPembayaran:String
    val library=Library()
    lateinit var clipData: ClipData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi)
        back.setOnClickListener {
            onBackPressed()
        }
        nilaiPembayaran= intent.getStringExtra("nilai_bayar")
        nilai_pembayaran.text = library.toRupiah(nilaiPembayaran.toDouble())
        button.setOnClickListener {
            val intent= Intent(this,SelesaiActivity::class.java)
            startActivity(intent)
        }

        copy_norek.setOnClickListener {
            library.copyStringToClipboard(no_rek.text.toString().trim().replace(" ",""),this)
        }
        copy_nilai.setOnClickListener {
            library.copyDoubleToClipboard(nilaiPembayaran.toDouble(),this)
        }

    }
}
