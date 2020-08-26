package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saku.warga.R
import com.saku.warga.models.InformasiData
import kotlinx.android.synthetic.main.list_pusat_informasi.view.*

class InformasiAdapter(private val context: Context) : RecyclerView.Adapter<InformasiAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<InformasiData>()
    fun addData(data:MutableList<InformasiData>){
        dataArray.addAll(data)
//        dataArray.sortByDescending { it.tgl_input }
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_pusat_informasi, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        when (dataArray[position].status) {
            "P1" -> {
                holder.title.background = ContextCompat.getDrawable(context,R.drawable.informasi_background_grey)
                holder.title.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }
            "P3" -> {
                holder.title.background = ContextCompat.getDrawable(context,R.drawable.informasi_background_blue)
                holder.title.setTextColor(ContextCompat.getColor(context,android.R.color.white))
            }
            "P2" -> {
                holder.title.background = ContextCompat.getDrawable(context,R.drawable.informasi_background_red)
                holder.title.setTextColor(ContextCompat.getColor(context,android.R.color.white))
            }
            else -> {
                holder.title.background = ContextCompat.getDrawable(context,R.drawable.informasi_background_grey)
                holder.title.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }
        }
        holder.title.text = dataArray[position].pesan
        holder.layout.setOnClickListener {
            Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show()
        }
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.informasi_title
        val layout: LinearLayout = itemView.layout_informasi
    }

}