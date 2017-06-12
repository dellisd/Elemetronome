package ca.llamabagel.elemetronome

import android.widget.LinearLayout
import android.content.Context
import android.widget.TextView
import android.R.attr.name
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView


/**
 * Created by isaac on 6/12/2017.
 */
class BeatsView @JvmOverloads constructor(val ctx: Context?, val attrs: AttributeSet? = null, val defStyleAttr: Int = 0, val defStyleRes: Int = 0) : LinearLayout(ctx, attrs, defStyleAttr, defStyleRes){
    private var tickerList: ArrayList<ImageView> = ArrayList()

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

    private var numBeats = 4

    private var currentBeat = 1

    // Create the animation that makes the screen pulsate
    val fadeOut = AnimationUtils.loadAnimation(ctx, R.anim.fade_out)

    init {
        // Set up the number of ticker views that are required
        for (i in 1..numBeats){
            tickerList.add(inflater.inflate(R.layout.metronome_ticker_view, this, false) as ImageView)
            this.addView(tickerList[i - 1])
        }
    }

    fun setNumBeats(newBeats: Int){
        currentBeat = 1
        while (newBeats > numBeats) {
            tickerList.add(inflater.inflate(R.layout.metronome_ticker_view, this, false) as ImageView)
            this.addView(tickerList[tickerList.size - 1], 0)
            numBeats++
        }
        while (newBeats < numBeats) {
            tickerList.remove(tickerList[tickerList.size - 1])
            numBeats--
        }
    }

    fun getNumBeats(): Int{
        return numBeats
    }

    fun onTick(animationDuration: Long) {
        // Make sure that the duration is set to something reasonable from the start
        fadeOut.duration = animationDuration

        tickerList[currentBeat - 1].startAnimation(fadeOut)

        currentBeat++
        if (currentBeat > numBeats) currentBeat = 1
    }

    fun resetBeat(){
        currentBeat = 1
    }

}