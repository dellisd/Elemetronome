package ca.llamabagel.elemetronome

import android.os.Handler
import android.os.Message
import android.os.SystemClock

/**
 * Created by isaac on 5/24/2017.
 */
abstract class AccurateTimer(var timeOfNextTick: Long = SystemClock.uptimeMillis(), var countDownInterval: Long) {
    /**
     * The message code for the ticking of the timer
     */
    companion object {
        const val MSG = 1
    }

    private var handler: Handler

    init {
        // Make sure that we update the next time that the timer should tick
        timeOfNextTick += countDownInterval

        /**
         * Handles the timing of the message being sent, and calls the onTick method when
         * receiving a message.
         */
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val code = msg.what

                // If this is a tick message
                if (code == 1) {
                    synchronized(this@AccurateTimer) {
                        onTick()

                        // Make sure that the time of the next tick is in the future
                        val currentTime = SystemClock.uptimeMillis()
                        do {
                            timeOfNextTick += countDownInterval
                        } while (currentTime > timeOfNextTick)

                        sendMessageAtTime(obtainMessage(MSG), timeOfNextTick)
                    }
                }
            }
        }
    }

    /**
     * Starts the repeated sending of messages at the specified interval
     */
    @Synchronized fun start() : AccurateTimer {
        handler.sendMessageAtTime(handler.obtainMessage(MSG), timeOfNextTick)
        return this
    }

    /**
     * Stops the handler from sending more messages and calls the onFinish method defined by the user
     */
    fun cancel() {
        handler.removeMessages(MSG)
        onFinish()
    }

    /**
     * Should be implemented by user
     */
    abstract fun onTick()

    /**
     * Should be implemented by user
     */
    abstract fun onFinish()

}