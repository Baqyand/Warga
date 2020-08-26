package com.saku.warga.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saku.warga.DetailDonasiActivity
import com.saku.warga.Library
import com.saku.warga.R
import com.saku.warga.models.DonasiData
import kotlinx.android.synthetic.main.list_donasi.view.*

class DonasiAdapter(private val context: Context) : RecyclerView.Adapter<DonasiAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<DonasiData>()
    val library=Library()
    fun addData(data:MutableList<DonasiData>){
        dataArray.addAll(data)
        notifyDataSetChanged()
    }
    fun initData(data:MutableList<DonasiData>){
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_donasi, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        if(dataArray[position].status=="wajib"){
            holder.wajib.visibility = View.VISIBLE
        }
        holder.title.text = dataArray[position].judul
        holder.target.text = "dari " +library.toRupiah(dataArray[position].target.toDouble())
        Glide.with(context).load(dataArray[position].gambar).into(holder.gambar)
        holder.progress.text = library.toRupiah(dataArray[position].progress.toDouble()) + " terkumpul"
        holder.limit.text = dataArray[position].limit
        holder.progressbar.max = dataArray[position].target.toInt()
        holder.progressbar.progress = dataArray[position].progress.toInt()
        holder.donasi.setOnClickListener {
            val intent = Intent(context, DetailDonasiActivity::class.java)
            intent.putExtra("id_donasi",dataArray[position].id)
            context.startActivity(intent)
        }
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gambar: ImageView = itemView.gambar
        val title: TextView = itemView.judul
        val progress: TextView = itemView.progress
        val wajib: TextView = itemView.wajib
        val target: TextView = itemView.target
        val limit: TextView = itemView.limit
        val donasi: Button = itemView.btn_donasi
        val progressbar: ProgressBar = itemView.progress_bar
        val layout: CardView = itemView.layout_kartu
    }

}