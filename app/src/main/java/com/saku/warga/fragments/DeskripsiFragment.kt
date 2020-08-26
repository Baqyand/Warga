package com.saku.warga.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saku.warga.Preferences

import com.saku.warga.R

/**
 * A simple [Fragment] subclass.
 */
class DeskripsiFragment(id_donasi:String) : Fragment() {
    var preferences  = Preferences()
    private lateinit var myview : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview =inflater.inflate(R.layout.fragment_deskripsi, container, false)

        return myview
    }

}
