package com.bytedance.minitiktok.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentViewPagerAdapter : FragmentPagerAdapter
{
    constructor(fm: FragmentManager):super(fm)

    private var fragments = ArrayList<Fragment>()


    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment)
    {
        fragments.add(fragment)
    }



}