package com.saku.warga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saku.warga.Library
import com.saku.warga.R
import com.saku.warga.models.IuranData
import kotlinx.android.synthetic.main.list_iuran_bs.view.*

class RincianIuranAdapter(private val context: Context) : RecyclerView.Adapter<RincianIuranAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<IuranData>()
    val library=Library()
    fun addData(data:MutableList<IuranData>){
        dataArray.addAll(data)
        notifyDataSetChanged()
    }

    fun initData(data:MutableList<IuranData>){
        dataArray.clear()
        dataArray.addAll(data)
        dataArray.sortByDescending { it.tanggal }
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_iuran_bs, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        holder.bulan.text = dataArray[position].periode
        holder.nilai.text = library.toRupiah(dataArray[position].bayar.toDouble())
        if(dataArray[position].bayar.toDouble()<dataArray[position].bill.toDouble()&&position<=library.getMonthM().toInt()-1){
            holder.bulan.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
            holder.nilai.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
        }else if(position>library.getMonthM().toInt()-1){
            holder.bulan.setTextColor(ContextCompat.getColor(context,R.color.colorHint))
            holder.nilai.setTextColor(ContextCompat.getColor(context,R.color.colorHint))
        }

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
        val bulan: TextView = itemView.bulan
        val nilai: TextView = itemView.bayar
        val layout: LinearLayout = itemView.layout_kartu
    }

}