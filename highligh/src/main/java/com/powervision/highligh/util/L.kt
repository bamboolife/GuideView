package com.powervision.highligh.util

import android.util.Log

/**
 * Created by zhy on 15/9/23.
 */
object L {
    private val TAG = "HighLight"
    private val debug = true

    fun e(msg: String) {
        if (debug)
            Log.e(TAG, msg)
    }

}
