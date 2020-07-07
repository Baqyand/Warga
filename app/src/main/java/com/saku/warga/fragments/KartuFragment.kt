package com.saku.warga.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.saku.warga.R

/**
 * A simple [Fragment] subclass.
 */
class KartuFragment : Fragment() {
    var myview : View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_kartu, container, false)
        return myview
    }

}
