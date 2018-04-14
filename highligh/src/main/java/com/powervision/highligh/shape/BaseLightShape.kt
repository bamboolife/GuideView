package com.powervision.highligh.shape

import android.graphics.Bitmap
import android.graphics.RectF
import com.powervision.highligh.HighLight


/**
 * <pre>
 * 高亮形状的超类
 * Created by isanwenyu on 2016/10/26.
 * Copyright (c) 2016 isanwenyu@163.com. All rights reserved.
</pre> *
 */
abstract class BaseLightShape : HighLight.LightShape {
    protected var dx: Float = 0.toFloat()
    protected var dy: Float = 0.toFloat()

    constructor() {}

    constructor(dx: Float, dy: Float) {
        this.dx = dx
        this.dy = dy
    }

   override fun shape(bitmap: Bitmap, viewPosInfo: HighLight.ViewPosInfo) {
        resetRectF4Shape(viewPosInfo.rectF!!, dx, dy)
        drawShape(bitmap, viewPosInfo)
    }

    /**
     * reset RectF for Shape by dx and dy.
     * @param viewPosInfoRectF
     * *
     * @param dx
     * *
     * @param dy
     */
    protected abstract fun resetRectF4Shape(viewPosInfoRectF: RectF, dx: Float, dy: Float)

    /**
     * draw shape into bitmap
     * @param bitmap
     * *
     * @param viewPosInfo
     * *
     * @see zhy.com.highlight.view.HightLightView.addViewForEveryTip
     * @see HightLightView.buildMask
     */
    protected abstract fun drawShape(bitmap: Bitmap, viewPosInfo: HighLight.ViewPosInfo)

}
