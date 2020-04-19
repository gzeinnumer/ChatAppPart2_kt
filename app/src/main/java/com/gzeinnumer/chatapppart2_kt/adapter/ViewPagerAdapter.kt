package com.gzeinnumer.chatapppart2_kt.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList
import java.util.HashMap

//todo 20
class ViewPagerAdapter(
    fm: FragmentManager,
    behavior: Int
) : FragmentPagerAdapter(fm, behavior) {

    private val fragments: ArrayList<Fragment> = ArrayList()
    private val titles: ArrayList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(
        fragment: Fragment,
        title: String
    ) {
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}