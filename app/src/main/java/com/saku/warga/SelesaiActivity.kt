package com.saku.warga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_selesai.*

class SelesaiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selesai)
        button_bukti.setOnClickListener {
            val intent= Intent(this,BuktiActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
