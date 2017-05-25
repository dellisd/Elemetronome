package ca.llamabagel.elemetronome

import android.os.Handler
import android.os.Message
import android.os.SystemClock

/**
 * Created by isaac on 5/24/2017.
 */
abstract class AccurateTimer(val countDownInterval: Long, var timeOfNextTick: Long = SystemClock.uptimeMillis() + countDownInterval, private var handler: Handler = Handler()) {
    companion object {
        const val MSG = 1
    }

    @Synchronized fun start() : AccurateTimer {
        handler.sendMessageAtTime(handler.obtainMessage(MSG), timeOfNextTick);
        return this;
    }

    fun cancel() {
        handler.removeCallbacks(null);
    }

    init {
        handler = Handler();
        val runnableCode = object : Runnable {
            override fun run() {
                val code = handler.obtainMessage().what;
                if (code == 1){
                    synchronized(AccurateTimer){
                        onTick();

                        val currentTime = SystemClock.uptimeMillis();
                        do {
                            timeOfNextTick += countDownInterval;
                        } while (currentTime > timeOfNextTick);

                        handler.sendMessageAtTime(handler.obtainMessage(MSG), timeOfNextTick);
                    }
                }
            }
        }
        handler.postAtTime(runnableCode, timeOfNextTick);
    }

    abstract fun onTick();

    abstract fun onFinish();

}