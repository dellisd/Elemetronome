package ca.llamabagel.elemetronome

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by derek on 5/24/2017.
 * General ViewPager adapter for use by the app for the 3 main screens
 */
class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MetronomeFragment.newInstance("M1")
            1 -> TunerFragment.newInstance("M2")
            else -> MetronomeFragment.newInstance("M3")
        }
    }

    override fun getCount(): Int = 3
}