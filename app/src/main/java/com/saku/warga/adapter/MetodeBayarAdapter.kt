package com.saku.warga.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saku.warga.R
import com.saku.warga.models.MetodeBayarData
import kotlinx.android.synthetic.main.list_metode_bayar.view.*

class MetodeBayarAdapter(private val context: Context) : RecyclerView.Adapter<MetodeBayarAdapter.NamaKelompokViewHolder>() {
    private val dataArray= mutableListOf<MetodeBayarData>()
    var checked = -1
    fun initData(data:MutableList<MetodeBayarData>){
        dataArray.clear()
        dataArray.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NamaKelompokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_metode_bayar, parent, false)
        return NamaKelompokViewHolder(view)
    }

    override fun onBindViewHolder(holder: NamaKelompokViewHolder, position: Int) {
        holder.radio.isChecked = checked==position
        if(dataArray[position].id=="bni"||dataArray[position].id=="linkaja"){
            Glide.with(context).load(dataArray[position].image).fitCenter().into(holder.image)
//            holder.image.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        holder.card.setOnClickListener {
            checked=position
            notifyDataSetChanged()
            val mydata = Intent("metode_bayar_trigger")
            mydata.putExtra("id_metode", dataArray[position].id)
            LocalBroadcastManager.getInstance(context).sendBroadcast(mydata)
        }
        Glide.with(context).load(dataArray[position].image).into(holder.image)
//        holder.image.setImageResource(dataArray[position].image)
        holder.namaMetodeBayar.text = dataArray[position].namaMetode
    }



    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class NamaKelompokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radio: RadioButton = itemView.radio
        val card: LinearLayout = itemView.cardview
        val image: ImageView = itemView.logo_metode_bayar
        val namaMetodeBayar: TextView = itemView.nama_metode_bayar
    }

}