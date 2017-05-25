package ca.llamabagel.elemetronome

import android.os.Handler
import android.os.SystemClock

/**
 * Created by isaac on 5/24/2017.
 */
abstract class AccurateTimer(val countDownInterval: Long, var timeOfNextTick: Long = SystemClock.uptimeMillis() + countDownInterval) {
    
}