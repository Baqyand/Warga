package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saku.warga.Library
import com.saku.warga.R
import com.saku.warga.models.PemasukanData
import kotlinx.android.synthetic.main.list_pemasukan.view.*

class LaporanDataAdapter(private val context: Context) : RecyclerView.Adapter<LaporanDataAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<PemasukanData>()
    val library = Library()
    fun addData(data:MutableList<PemasukanData>){
        dataArray.addAll(data)
        notifyDataSetChanged()
    }
    fun initData(data:MutableList<PemasukanData>){
        dataArray.clear()
        dataArray.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData(){
        dataArray.clear()
        notifyDataSetChanged()
    }

    fun subtractData(){
        dataArray.removeAt(dataArray.size-1)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NamaKelompokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_pemasukan, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        holder.title.text = dataArray[position].nama
        holder.nilai.text = library.toRupiah(dataArray[position].total.toDouble())
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.judul
        val nilai: TextView = itemView.nilai
    }

}