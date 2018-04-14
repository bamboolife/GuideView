package com.powervision.highligh.position

import android.graphics.RectF
import com.powervision.highligh.HighLight


/**
 * Created by caizepeng on 16/8/20.
 */
class OnBottomPosCallback : OnBaseCallback {
    constructor() {}

    constructor(offset: Float) : super(offset) {}

   override fun getPosition(rightMargin: Float, bottomMargin: Float, rectF: RectF, marginInfo: HighLight.MarginInfo) {
        marginInfo.rightMargin = rightMargin
        marginInfo.topMargin = rectF.top + rectF.height() + offset
    }

}
