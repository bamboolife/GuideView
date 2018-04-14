package com.powervision.highligh.position

import android.graphics.RectF
import com.powervision.highligh.HighLight

/**
 * Created by caizepeng on 16/8/20.
 */
abstract class OnBaseCallback : HighLight.OnPosCallback {
    protected var offset: Float = 0.toFloat()

    constructor() {}

    constructor(offset: Float) {
        this.offset = offset
    }

    /**
     * 如果需要调整位置,重写该方法
     * @param rightMargin
     * *
     * @param bottomMargin
     * *
     * @param rectF
     * *
     * @param marginInfo
     */
    fun posOffset(rightMargin: Float, bottomMargin: Float, rectF: RectF, marginInfo: HighLight.MarginInfo) {}

    override fun getPos(rightMargin: Float, bottomMargin: Float, rectF: RectF, marginInfo: HighLight.MarginInfo) {
        getPosition(rightMargin, bottomMargin, rectF, marginInfo)
        posOffset(rightMargin, bottomMargin, rectF, marginInfo)
    }

    abstract fun getPosition(rightMargin: Float, bottomMargin: Float, rectF: RectF, marginInfo: HighLight.MarginInfo)
}
