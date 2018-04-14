package com.powervision.highligh.position

import android.graphics.RectF
import com.powervision.highligh.HighLight


/**
 * Created by caizepeng on 16/8/20.
 */
class OnTopPosCallback : OnBaseCallback {
    constructor() {}

    constructor(offset: Float) : super(offset) {}

   override fun getPosition(rightMargin: Float, bottomMargin: Float, rectF: RectF, marginInfo: HighLight.MarginInfo) {
        marginInfo.leftMargin = rectF.right - rectF.width() / 2
        marginInfo.bottomMargin = bottomMargin + rectF.height() + offset
    }
}
