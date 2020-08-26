package com.saku.warga.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.saku.warga.fragments.DeskripsiFragment
import com.saku.warga.fragments.DokumenFragment
import com.saku.warga.fragments.PenyumbangFragment


class PageAdapter internal constructor(fm: FragmentManager?, private val numOfTabs: Int,
                                       private val id_donasi: String
) :
    FragmentPagerAdapter(fm!!,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DeskripsiFragment(id_donasi)
            1 -> DokumenFragment(id_donasi)
            2 -> PenyumbangFragment(id_donasi)
            else -> DeskripsiFragment(id_donasi)
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }

}