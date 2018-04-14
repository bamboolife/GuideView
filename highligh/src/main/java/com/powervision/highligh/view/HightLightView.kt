package com.powervision.highligh.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.powervision.highligh.HighLight


/**
 * Created by zhy on 15/10/8.
 */
class HightLightView(context: Context, private val mHighLight: HighLight, maskColor: Int, private val mViewRects: List<HighLight.ViewPosInfo>, // added by isanwenyu@163.com
                     private val isNext: Boolean//next模式标志
) : FrameLayout(context) {

    private var mMaskBitmap: Bitmap? = null
    private var mLightBitmap: Bitmap? = null
    private var mPaint: Paint? = null
    private val mInflater: LayoutInflater

    //some config
    //    private boolean isBlur = true;
    private var maskColor = 0xCC000000.toInt()
    private var mPosition = -1//当前显示的提示布局位置
    private var mViewPosInfo: HighLight.ViewPosInfo? = null//当前显示的高亮布局位置信息

    init {
        mInflater = LayoutInflater.from(context)
        this.maskColor = maskColor
        setWillNotDraw(false)
        init()
    }//        this.isBlur = isBlur;

    private fun init() {
        mPaint = Paint()
        mPaint!!.isDither = true
        mPaint!!.isAntiAlias = true
        //        if (isBlur)
        //            mPaint.setMaskFilter(new BlurMaskFilter(DEFAULT_WIDTH_BLUR, BlurMaskFilter.Blur.SOLID));
        mPaint!!.style = Paint.Style.FILL

        addViewForTip()


    }

    private fun addViewForTip() {
        if (isNext) {
            //校验mPosition
            if (mPosition < -1 || mPosition > mViewRects.size - 1) {
                //重置位置
                mPosition = 0
            } else if (mPosition == mViewRects.size - 1) {
                //移除当前布局
                mHighLight.remove()
                return
            } else {
                //mPosition++
                mPosition++
            }
            mViewPosInfo = mViewRects[mPosition]
            //移除所有tip再添加当前位置的tip布局
            removeAllTips()
            addViewForEveryTip(mViewPosInfo!!)
        } else {
            for (viewPosInfo in mViewRects) {
                addViewForEveryTip(viewPosInfo)
            }
        }
    }

    /**
     * 移除当前高亮布局的所有提示布局

     */
    private fun removeAllTips() {
        removeAllViews()
    }

    /**
     * 添加每个高亮布局
     * @param viewPosInfo 高亮布局信息
     * *
     * @author isanwenyu@163.com
     */
    private fun addViewForEveryTip(viewPosInfo: HighLight.ViewPosInfo) {
        val view = mInflater.inflate(viewPosInfo.layoutId, this, false)
        val lp = buildTipLayoutParams(view, viewPosInfo) ?: return

        lp.leftMargin = viewPosInfo.marginInfo!!.leftMargin.toInt()
        lp.topMargin = viewPosInfo.marginInfo!!.topMargin.toInt()
        lp.rightMargin = viewPosInfo.marginInfo!!.rightMargin.toInt()
        lp.bottomMargin = viewPosInfo.marginInfo!!.bottomMargin.toInt()

        //fix the bug can't set gravity  LEFT|BOTTOM  or RIGHT|TOP
        //            if (lp.leftMargin == 0 && lp.topMargin == 0)
        //            {
        //                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        //            }

        if (lp.rightMargin != 0) {
            lp.gravity = Gravity.RIGHT
        } else {
            lp.gravity = Gravity.LEFT
        }

        if (lp.bottomMargin != 0) {
            lp.gravity = lp.gravity or Gravity.BOTTOM
        } else {
            lp.gravity = lp.gravity or Gravity.TOP
        }
        addView(view, lp)
    }

    /**
     * 切换下个提示布局
     * @author isanwenyu@16.com
     */
    operator fun next() {
        if (isNext) addViewForTip()
    }

    private fun buildMask() {
        mMaskBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mMaskBitmap!!)
        canvas.drawColor(maskColor)
        mPaint!!.xfermode = MODE_DST_OUT
        mHighLight.updateInfo()
        mLightBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)

        if (isNext)
        //如果是next模式添加每个提示布局的背景形状
        {
            //添加当前提示布局的高亮形状背景
            addViewEveryTipShape(mViewPosInfo!!)
        } else {
            for (viewPosInfo in mViewRects) {
                addViewEveryTipShape(viewPosInfo)
            }
        }
        canvas.drawBitmap(mLightBitmap!!, 0f, 0f, mPaint)
    }

    /**
     * 添加提示布局的背景形状
     * @param viewPosInfo //提示布局的位置信息
     * *
     * @author isanwenyu@16.com
     */
    private fun addViewEveryTipShape(viewPosInfo: HighLight.ViewPosInfo) {
        viewPosInfo.lightShape!!.shape(mLightBitmap!!, viewPosInfo)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), //
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        setMeasuredDimension(width, height)


    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //        if (changed) edited by isanwenyu@163.com for next mode
        run {
            buildMask()
            updateTipPos()
        }

    }

    private fun updateTipPos() {
        if (isNext)
        //如果是next模式 只有一个子控件 刷新当前位置tip
        {
            val view = getChildAt(0)

            val lp = buildTipLayoutParams(view, mViewPosInfo!!) ?: return
            view.layoutParams = lp

        } else {
            var i = 0
            val n = childCount
            while (i < n) {
                val view = getChildAt(i)
                val viewPosInfo = mViewRects[i]

                val lp = buildTipLayoutParams(view, viewPosInfo)
                if (lp == null) {
                    i++
                    continue
                }
                view.layoutParams = lp
                i++
            }
        }
    }

    private fun buildTipLayoutParams(view: View, viewPosInfo: HighLight.ViewPosInfo): FrameLayout.LayoutParams? {
        val lp = view.layoutParams as FrameLayout.LayoutParams
        if (lp.leftMargin == viewPosInfo.marginInfo!!.leftMargin.toInt() &&
                lp.topMargin == viewPosInfo.marginInfo!!.topMargin.toInt() &&
                lp.rightMargin == viewPosInfo.marginInfo!!.rightMargin.toInt() &&
                lp.bottomMargin == viewPosInfo.marginInfo!!.bottomMargin.toInt())
            return null

        lp.leftMargin = viewPosInfo.marginInfo!!.leftMargin.toInt()
        lp.topMargin = viewPosInfo.marginInfo!!.topMargin.toInt()
        lp.rightMargin = viewPosInfo.marginInfo!!.rightMargin.toInt()
        lp.bottomMargin = viewPosInfo.marginInfo!!.bottomMargin.toInt()

        //fix the bug can't set gravity  LEFT|BOTTOM  or RIGHT|TOP
        //        if (lp.leftMargin == 0 && lp.topMargin == 0)
        //        {
        //            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        //        }
        if (lp.rightMargin != 0) {
            lp.gravity = Gravity.RIGHT
        } else {
            lp.gravity = Gravity.LEFT
        }

        if (lp.bottomMargin != 0) {
            lp.gravity = lp.gravity or Gravity.BOTTOM
        } else {
            lp.gravity = lp.gravity or Gravity.TOP
        }
        return lp
    }


    override fun onDraw(canvas: Canvas) {

        canvas.drawBitmap(mMaskBitmap!!, 0f, 0f, null)
        super.onDraw(canvas)

    }

    companion object {
        private val DEFAULT_WIDTH_BLUR = 15
        private val DEFAULT_RADIUS = 6
        private val MODE_DST_OUT = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }
}
