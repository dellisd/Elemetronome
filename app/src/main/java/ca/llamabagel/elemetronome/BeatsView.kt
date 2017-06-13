package ca.llamabagel.elemetronome

import android.widget.LinearLayout
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import android.view.View


/**
 * Created by isaac on 6/12/2017.
 */
class BeatsView @JvmOverloads constructor(val ctx: Context?, val attrs: AttributeSet? = null, val defStyleAttr: Int = 0, val defStyleRes: Int = 0) : LinearLayout(ctx, attrs, defStyleAttr, defStyleRes){
    private var tickerList: ArrayList<View> = ArrayList()

    var selectedList: ArrayList<Boolean> = ArrayList()

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

    var currentBeat = 1

    // Create the animation that makes the screen pulsate
    val fadeIn = AnimationUtils.loadAnimation(ctx, R.anim.fade_in)


    var numBeats = 4
        set(newBeats) {
            // Reset the current beat to one
            currentBeat = 1

            // Measure the change that needs to be made
            val change = newBeats - numBeats
            if (change > 0){
                // Check if we can fit any more onto the screen first
                val screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels

                // Get the layout of the ticker display
                val lp: LayoutParams = tickerList[tickerList.size - 1].layoutParams as LayoutParams

                // Calculate the possible number of tickers we can have on screen
                val numTickers = (screenWidth / (tickerList[tickerList.size - 1].width + lp.marginEnd + lp.marginStart)).toInt()

                // If we can fit no more, then return
                if (numTickers - numBeats <= 0) return

                for(i in 1..change) {
                    // Inflate a new ticker
                    tickerList.add(inflater.inflate(R.layout.metronome_ticker_view, this, false) as View)

                    // Add a tracker for if it has been selected
                    selectedList.add(false)

                    // Allow the user to change the emphasis of the current beat
                    tickerList[tickerList.size - 1].setOnClickListener(object: View.OnClickListener{
                        override fun onClick(v: View?) {
                            selectedList[indexOfChild(v)] = !selectedList[indexOfChild(v)]

                            // Make emphasized when purple
                            if (selectedList[indexOfChild(v)]){
                                v?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                            } else{
                                v?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                            }
                        }
                    })

                    // Finally, add it to the layou
                    this.addView(tickerList[tickerList.size - 1])
                }
            }
            else {
                for (i in 1..(-change)) {
                    // Remove it from the tickerList, the selectedList and the layout
                    this.removeView(tickerList[tickerList.size - 1])
                    tickerList.removeAt(tickerList.size - 1)
                    selectedList.removeAt(selectedList.size - 1)
                }
            }
            field = newBeats
        }

    init {
        for (i in 1..numBeats){
            //Initialize the starting beats
            tickerList.add(inflater.inflate(R.layout.metronome_ticker_view, this, false) as View)
            selectedList.add(false)
            tickerList[i - 1].setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    selectedList[indexOfChild(v)] = !selectedList[indexOfChild(v)]
                    if (selectedList[indexOfChild(v)]){
                        v?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    } else{
                        v?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    }
                }
            })
            this.addView(tickerList[i - 1])
        }
    }

    fun onTick(animationDuration: Long) {
        // Make sure that the duration is set to something reasonable from the start
        fadeIn.duration = animationDuration

        // Start the animation
        tickerList[currentBeat - 1].startAnimation(fadeIn)

        currentBeat++
        if (currentBeat > numBeats)
            currentBeat = 1
    }

    fun resetBeat(){
        currentBeat = 1
    }

}