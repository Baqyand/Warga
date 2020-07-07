package com.saku.warga.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saku.warga.Preferences
import com.saku.warga.R
import com.saku.warga.UbahDataDiriActivity
import kotlinx.android.synthetic.main.bottom_sheet_data.view.*

class DataDiri : BottomSheetDialogFragment() {
    private lateinit var myview:View
    private var preferences  = Preferences()
    var nama : String? = null
    private var alias : String? = null
    private var tglLahir : String? = null
    private var noTelp : String? = null
    private var foto : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.bottom_sheet_data, container, false)
        preferences.setPreferences(context!!)
        return myview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview.nama.text = nama
        myview.no_telp.text = noTelp
        myview.tgl_lahir.text = tglLahir
        myview.bs_tutup.setOnClickListener { this.dismiss() }
        myview.bs_edit.setOnClickListener {
            val intent = Intent(context, UbahDataDiriActivity::class.java)
            intent.putExtra("nama",nama)
            intent.putExtra("no_telp",noTelp)
            intent.putExtra("alias",alias)
            intent.putExtra("foto",foto)
            startActivity(intent)
        }
    }

    fun initData(nama:String, no_telp:String, tgl_lahir:String,alias:String,foto:String){
        this.nama = nama
        this.noTelp = no_telp
        this.tglLahir = tgl_lahir
        this.alias = alias
        this.foto = foto
    }


//    interface BottomSheetListener {
//        fun onButtonClicked(text: String?)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            bsListener = context as BottomSheetListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException(
//                context.toString()
//                        + " must implement BottomSheetListener"
//            )
//        }
//    }
}