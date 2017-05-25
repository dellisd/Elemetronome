package ca.llamabagel.elemetronome

import android.media.AudioManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Changes the volume control stream so that the volume buttons will work properly
        volumeControlStream = AudioManager.STREAM_MUSIC

        viewPager.adapter = PagerAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val ids = listOf(R.id.navigation_metronome, R.id.navigation_tuner, R.id.navigation_tempo)
                navigation.selectedItemId = ids[position]
            }

            override fun onPageSelected(position: Int) {}
        })

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_metronome -> viewPager.currentItem = 0
                R.id.navigation_tuner -> viewPager.currentItem = 1
                R.id.navigation_tempo -> viewPager.currentItem = 2
            }

            true
        }
    }
}
