package com.powervision.highligh.shape

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.powervision.highligh.HighLight


/**
 * Created by caizepeng on 16/8/20.
 * Edited by isanwenyu@163.com 16/10/26.
 */
class RectLightShape : BaseLightShape() {
  override  protected fun drawShape(bitmap: Bitmap, viewPosInfo: HighLight.ViewPosInfo) {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.isDither = true
        paint.isAntiAlias = true
        paint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
        canvas.drawRoundRect(viewPosInfo.rectF, 6f, 6f, paint)
    }

    override fun resetRectF4Shape(viewPosInfoRectF: RectF, dx: Float, dy: Float) {
        viewPosInfoRectF.inset(dx, dy)
    }
}
