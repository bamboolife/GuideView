package com.powervision.highligh.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View

/**
 * Created by zhy on 15/10/8.
 */
object ViewUtils {
    private val FRAGMENT_CON = "NoSaveStateFrameLayout"

    fun getLocationInView(parent: View?, child: View?): Rect {
        if (child == null || parent == null) {
            throw IllegalArgumentException("parent and child can not be null .")
        }

        var decorView: View? = null
        val context = child.context
        if (context is Activity) {
            decorView = context.window.decorView
        }

        val result = Rect()
        val tmpRect = Rect()

        var tmp: View = child

        if (child === parent) {
            child.getHitRect(result)
            return result
        }
        while (tmp !== decorView && tmp !== parent) {
            tmp.getHitRect(tmpRect)

            if (!tmp.javaClass.equals(FRAGMENT_CON)) {
                result.left += tmpRect.left
                result.top += tmpRect.top
            }
            tmp = tmp.parent as View
        }
        result.right = result.left + child.measuredWidth
        result.bottom = result.top + child.measuredHeight
        return result
    }
}
