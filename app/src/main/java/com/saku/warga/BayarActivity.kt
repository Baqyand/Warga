package com.saku.warga

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.saku.warga.adapter.MetodeBayarAdapter
import com.saku.warga.models.MetodeBayarData
import kotlinx.android.synthetic.main.activity_bayar.*
import kotlinx.android.synthetic.main.activity_bayar.recyclerview
import kotlinx.android.synthetic.main.fragment_kartu.*
import kotlinx.android.synthetic.main.layout_transaksi.*

class BayarActivity : AppCompatActivity() {
    private val dataArray= mutableListOf<MetodeBayarData>()
    private lateinit var dataAdapter:MetodeBayarAdapter
    var totalBayar = 0.0
    var bebanBayar = 0.0
    val library=Library()
    lateinit var metode : String

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(intent.hasExtra("id_metode")){
                metode = intent.getStringExtra("id_metode")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("metode_bayar_trigger"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(mMessageReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayar)
        val use = intent.getStringExtra("use")
        back.setOnClickListener {
            onBackPressed()
        }
        title_layout.text = use.capitalize()
        if(use=="donasi"){
            layout_anonim.visibility = View.VISIBLE
            beban_bayar.text = "Donasi Sukarela"
            anonim_text.setOnClickListener {
                anonim.isChecked = anonim.isChecked != true
            }
        }else{
            bebanBayar=intent.getStringExtra("beban").toDouble()
            totalBayar=intent.getStringExtra("beban").toDouble()
            beban_bayar.text = library.toRupiah(bebanBayar)
            total_bayar.setText(library.toRupiah(bebanBayar))
            total_bayar.setSelection(total_bayar.text.length)
        }
        dataAdapter = MetodeBayarAdapter(this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = dataAdapter
        total_bayar.setOnClickListener { total_bayar.setSelection(total_bayar.text.length) }

        total_bayar.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (total_bayar.length() < 2 || !total_bayar.text.startsWith("Rp")||total_bayar.text.toString()=="Rp") {
                    total_bayar.setText("Rp0")
                    total_bayar.setSelection(total_bayar.length())
                }
                else if(total_bayar.length()>3 && total_bayar.text.startsWith("Rp0")){
                    total_bayar.setText("Rp"+s.toString().substringAfter("0"))
                    total_bayar.setSelection(total_bayar.length())
                }
                else{
                    totalBayar=s.toString().replace(".","").substringAfter("p").toDouble()
                    if(totalBayar>99999999){
                        Snackbar.make(findViewById(android.R.id.content),"Nilai tidak boleh melebihi batas maksimum",Snackbar.LENGTH_SHORT).show()
                        totalBayar= 99999999.0
                        total_bayar.removeTextChangedListener(this)
                        total_bayar.setText(library.toRupiah(totalBayar))
                        total_bayar.addTextChangedListener(this)
                        total_bayar.setSelection(total_bayar.length())
                    }else{
                        total_bayar.removeTextChangedListener(this)
                        total_bayar.setText(library.toRupiah(totalBayar))
                        total_bayar.addTextChangedListener(this)
                        total_bayar.setSelection(total_bayar.length())
                    }

                }
            }
        })

        dataArray.add(MetodeBayarData("linkaja",R.drawable.bank_linkaja,"LinkAja"))
        dataArray.add(MetodeBayarData("mandiri",R.drawable.bank_mandiri,"Bank Mandiri"))
        dataArray.add(MetodeBayarData("bni",R.drawable.bank_bni,"Bank BNI"))
        dataArray.add(MetodeBayarData("bca",R.drawable.bank_bca,"Bank BCA"))
        dataArray.add(MetodeBayarData("bri",R.drawable.bank_bri,"Bank BRI"))
        dataAdapter.initData(dataArray)

        tambah.setOnClickListener {
            if(totalBayar>99999999){
                Snackbar.make(findViewById(android.R.id.content), "Nilai tidak boleh melebihi batas maksimum.", Snackbar.LENGTH_SHORT).show()
                totalBayar= 99999999.0
                total_bayar.setText(library.toRupiah(totalBayar))
                total_bayar.setSelection(total_bayar.length())
            }else{
                totalBayar+=50000
                total_bayar.setText(library.toRupiah(totalBayar))
                total_bayar.setSelection(total_bayar.length())
            }
        }
        kurang.setOnClickListener {
            if(totalBayar>0)
            totalBayar-=50000
            total_bayar.setText(library.toRupiah(totalBayar))
            total_bayar.setSelection(total_bayar.length())
        }

        btn_bayar.setOnClickListener {
            if(totalBayar<10000){
                Snackbar.make(findViewById(android.R.id.content),"Nilai transfer tidak valid.",Snackbar.LENGTH_SHORT).show()
            }else{
                if(this::metode.isInitialized){
                    val intent=Intent(this@BayarActivity,KonfirmasiActivity::class.java)
                    intent.putExtra("nilai_bayar",totalBayar.toString())
                    startActivity(intent)
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Anda belum memilih metode pembayaran.",Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
