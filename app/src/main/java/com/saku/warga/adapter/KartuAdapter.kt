package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.saku.warga.Library
import com.saku.warga.R
import com.saku.warga.models.IuranData
import kotlinx.android.synthetic.main.list_kartu.view.*
import java.text.NumberFormat
import java.util.*

class KartuAdapter(private val context: Context) : RecyclerView.Adapter<KartuAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<IuranData>()
    val library= Library()
    lateinit var view:View
    fun initData(data:MutableList<IuranData>){
        dataArray.clear()
        dataArray.addAll(data)
        notifyDataSetChanged()
    }

    fun tagihanDescending(){
        dataArray.sortByDescending { it.bill }
        notifyDataSetChanged()
    }

    fun tagihanAscending(){
        dataArray.sortBy { it.bill }
        notifyDataSetChanged()
    }

    fun bayarDescending(){
        dataArray.sortByDescending { it.bayar }
        notifyDataSetChanged()
    }

    fun bayarAscending(){
        dataArray.sortBy { it.bayar }
        notifyDataSetChanged()
    }

    fun clearData(){
        dataArray.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NamaKelompokViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.list_kartu, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        val tagihan = dataArray[position].bill.substringBefore(".").toDouble()
        val bayar = dataArray[position].bayar.substringBefore(".").toDouble()
        holder.bayar.text = library.toRupiah(bayar)
        holder.bulan.text = dataArray[position].periode
        holder.tagihan.text = library.toRupiah(tagihan)
        holder.layout.setOnClickListener {
            Snackbar.make(view.rootView,"Fitur belum tersedia",
                Snackbar.LENGTH_SHORT).show()

//            Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show()
        }
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bulan: TextView = itemView.bulan
        val tagihan: TextView = itemView.tagihan
        val bayar: TextView = itemView.bayar
        val layout: LinearLayout = itemView.layout_kartu
    }

}