package com.saku.warga.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.saku.warga.fragments.AnnualFragment
import com.saku.warga.fragments.DeskripsiFragment
import com.saku.warga.fragments.MonthFragment
import com.saku.warga.fragments.PenyumbangFragment


class LaporanPageAdapter internal constructor(fm: FragmentManager?, private val numOfTabs: Int) :
    FragmentPagerAdapter(fm!!,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MonthFragment("rt")
            1 -> MonthFragment("rw")
            2 -> MonthFragment("annual")
//            2 -> AnnualFragment()
            else -> MonthFragment("rt")
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }

}