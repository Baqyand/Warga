package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.saku.warga.R
import com.saku.warga.models.NotifikasiData
import kotlinx.android.synthetic.main.list_notifikasi.view.*

class NotifikasiAdapter(private val context: Context) : RecyclerView.Adapter<NotifikasiAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<NotifikasiData>()
    fun addData(data:MutableList<NotifikasiData>){
        dataArray.addAll(data)
        dataArray.sortByDescending { it.tgl_input }
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_notifikasi, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        when (dataArray[position].icon) {
            "ic_iuran" -> {
                holder.iconSatpam.visibility = View.VISIBLE
                holder.icon.setImageResource(R.drawable.ic_iuran_mini)
                holder.icon.visibility = View.GONE
            }
            "ic_laporan" -> {
                holder.icon.setImageResource(R.drawable.ic_laporan_mini)
            }
            "ic_donasi" -> {
                holder.icon.setImageResource(R.drawable.ic_donation_mini)
            }
            "ic_satpam" -> {
                holder.icon.visibility = View.GONE
                holder.iconSatpam.visibility = View.VISIBLE
            }
            else -> {
                holder.icon.setImageResource(R.drawable.ic_notification_mini)
            }
        }
        holder.title.text = dataArray[position].judul
        holder.tanggal.text = dataArray[position].tgl_input
        holder.subtitle.text = dataArray[position].subtitle
        holder.keterangan.text = dataArray[position].pesan
        holder.layout.setOnClickListener {
            Toast.makeText(context,"Fitur belum tersedia",Toast.LENGTH_SHORT).show()
        }
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.icon_notif
        val iconSatpam: TextView = itemView.ic_satpam
        val title: TextView = itemView.title
        val tanggal: TextView = itemView.tanggal
        val subtitle: TextView = itemView.subtitle
        val keterangan: TextView = itemView.keterangan
        val layout: LinearLayout = itemView.layout_notifikasi
    }

}