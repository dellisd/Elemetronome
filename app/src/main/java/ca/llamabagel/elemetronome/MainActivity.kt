package ca.llamabagel.elemetronome

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    // Audio recording permission stuff
    var permissionToRecordAccepted = false
    val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Changes the volume control stream so that the volume buttons will work properly
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Get the tab that was last open when the user last closed the app
        val openTab = PreferenceManager.getDefaultSharedPreferences(this).getInt("open_tab", 0)

        viewPager.adapter = PagerAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val ids = listOf(R.id.navigation_metronome, R.id.navigation_tuner, R.id.navigation_tone_generator)
                navigation.selectedItemId = ids[position]
            }
        })
        viewPager.currentItem = openTab

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_metronome -> viewPager.currentItem = 0
                R.id.navigation_tuner -> viewPager.currentItem = 1
                R.id.navigation_tone_generator -> viewPager.currentItem = 2
            }

            true
        }

        // Request audio recording permissions (for the actual tuner and stuff)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> grantResults[0] == PackageManager.PERMISSION_GRANTED
            else -> false
        }

        if (!permissionToRecordAccepted) finish()
    }

    private fun saveTabState() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("open_tab", viewPager?.currentItem ?: 0)
                .apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveTabState()
    }

    override fun onPause() {
        super.onPause()
        saveTabState()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        saveTabState()
    }
}
