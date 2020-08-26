package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saku.warga.Library
import com.saku.warga.R
import kotlinx.android.synthetic.main.list_annual.view.*
import kotlinx.android.synthetic.main.list_pemasukan.view.*
import kotlinx.android.synthetic.main.list_pemasukan.view.judul

class LaporanAnnualAdapter(private val context: Context) : RecyclerView.Adapter<LaporanAnnualAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<String>()
    val library = Library()
    fun addData(data:MutableList<String>){
        dataArray.addAll(data)
        notifyDataSetChanged()
    }
    fun initData(data:MutableList<String>){
        dataArray.clear()
        dataArray.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NamaKelompokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_annual, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        holder.title.text = dataArray[position]
        holder.next.setOnClickListener {

        }
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.judul
        val next: RelativeLayout = itemView.next
    }

}