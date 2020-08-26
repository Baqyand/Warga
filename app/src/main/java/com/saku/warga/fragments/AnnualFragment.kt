package com.saku.warga.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saku.warga.Library
import com.saku.warga.Preferences

import com.saku.warga.R
import com.saku.warga.adapter.LaporanAnnualAdapter
import kotlinx.android.synthetic.main.fragment_annual.view.*

class AnnualFragment : Fragment() {
    var preferences  = Preferences()
    private lateinit var myview : View
    val data=ArrayList<String>()
    lateinit var adapter: LaporanAnnualAdapter
    var library = Library()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview =inflater.inflate(R.layout.fragment_annual, container, false)
        data.add("Laporan tahun 2019")
        data.add("Laporan tahun 2018")
        data.add("Laporan tahun 2017")
        data.add("Laporan tahun 2016")
        data.add("Laporan tahun 2015")
        data.add("Laporan tahun 2014")
        adapter = LaporanAnnualAdapter(context!!)
        myview.recyclerview.layoutManager = LinearLayoutManager(context)
        myview.recyclerview.adapter = adapter
        adapter.initData(data)
        return myview
    }

}
