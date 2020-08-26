package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saku.warga.Library
import com.saku.warga.R
import com.saku.warga.models.TransaksiData
import kotlinx.android.synthetic.main.list_transaksi_terakhir.view.*

class TransaksiTerakhirAdapter(private val context: Context) : RecyclerView.Adapter<TransaksiTerakhirAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<TransaksiData>()
    val library=Library()
    fun addData(data:MutableList<TransaksiData>){
        dataArray.addAll(data)
        notifyDataSetChanged()
    }

    fun initData(data:MutableList<TransaksiData>){
        dataArray.clear()
        dataArray.addAll(data)
        dataArray.sortByDescending { it.status }
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_transaksi_terakhir, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        holder.title.text = dataArray[position].keterangan
//        Glide.with(context).load(dataArray[position].gambar).into(holder.gambar)
        holder.tanggal.text = dataArray[position].tgl
        holder.nilai.text = library.toRupiah(dataArray[position].nilai.toDouble())
//        when (dataArray[position].status) {
//            "3" -> {
//                holder.status.text = dataArray[position].pesan
//                holder.status.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
//            }
//            "1" -> {
//                holder.status.text = dataArray[position].pesan
//                holder.status.setTextColor(ContextCompat.getColor(context,R.color.colorHint))
//            }
//            "2" -> {
//                holder.status.text = dataArray[position].pesan
//                holder.status.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
//            }
//        }
        holder.layout.setOnClickListener {
//            val intent = Intent(context, DetailDonasiActivity::class.java)
//            intent.putExtra("id_donasi",dataArray[position].id)
//            context.startActivity(intent)
        }
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gambar: ImageView = itemView.logo_metode_bayar
        val title: TextView = itemView.judul
        val tanggal: TextView = itemView.tanggal
        val nilai: TextView = itemView.nilai
        val status: TextView = itemView.status
        val layout: CardView = itemView.cardview
    }

}