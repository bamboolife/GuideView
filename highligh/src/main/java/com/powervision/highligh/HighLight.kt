package com.powervision.highligh

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.powervision.highligh.interfaces.HighLightInterface
import com.powervision.highligh.shape.RectLightShape
import com.powervision.highligh.util.ViewUtils
import com.powervision.highligh.view.HightLightView

import java.util.ArrayList


/**
 * Created by zhy on 15/10/8.
 */
class HighLight(private val mContext: Context) : HighLightInterface {
    class ViewPosInfo {
        var layoutId = -1
        var rectF: RectF? = null
        var marginInfo: MarginInfo? = null
        var view: View? = null
        var onPosCallback: OnPosCallback? = null
        var lightShape: LightShape? = null
    }

    interface LightShape {
        fun shape(bitmap: Bitmap, viewPosInfo: ViewPosInfo)
    }

    class MarginInfo {
        var topMargin: Float = 0.toFloat()
        var leftMargin: Float = 0.toFloat()
        var rightMargin: Float = 0.toFloat()
        var bottomMargin: Float = 0.toFloat()

    }

    interface OnPosCallback {
        fun getPos(rightMargin: Float, bottomMargin: Float, rectF: RectF, marginInfo: MarginInfo)
    }


    private var mAnchor: View? = null
    private val mViewRects: MutableList<ViewPosInfo>
    private var mHightLightView: HightLightView? = null
    private val clickCallback: HighLightInterface.OnClickCallback? = null

    private var intercept = true
    //    private boolean shadow = true;
    private var maskColor = 0xCC000000.toInt()

    //added by isanwenyu@163.com
    private var autoRemove = true//点击是否自动移除 默认为true
    /**
     * 返回是否是next模式

     * @return
     * *
     * @author isanwenyu@163.com
     */
    var isNext = false
        private set//next模式标志 默认为false
    /**
     * @return Whether the dialog is currently showing.
     * *
     * @author isanwenyu@163.com
     */
    var isShowing: Boolean = false
        private set//是否显示
    private var mShowMessage: Message? = null
    private var mRemoveMessage: Message? = null
    private var mClickMessage: Message? = null
    private val mListenersHandler: ListenersHandler

    init {
        mViewRects = ArrayList<ViewPosInfo>()
        mAnchor = (mContext as Activity).findViewById<View>(android.R.id.content)
        mListenersHandler = ListenersHandler()
    }

    fun anchor(anchor: View): HighLight {
        mAnchor = anchor
        return this
    }

    fun intercept(intercept: Boolean): HighLight {
        this.intercept = intercept
        return this
    }

    //    public HighLight shadow(boolean shadow)
    //    {
    //        this.shadow = shadow;
    //        return this;
    //    }

    fun maskColor(maskColor: Int): HighLight {
        this.maskColor = maskColor
        return this
    }


    fun addHighLight(viewId: Int, decorLayoutId: Int, onPosCallback: OnPosCallback, lightShape: LightShape): HighLight {
        val parent = mAnchor as ViewGroup?
        val view = parent!!.findViewById<View>(viewId)
        addHighLight(view, decorLayoutId, onPosCallback, lightShape)
        return this
    }

    fun updateInfo() {
        val parent = mAnchor as ViewGroup?
        for (viewPosInfo in mViewRects) {

            val rect = RectF(ViewUtils.getLocationInView(parent, viewPosInfo.view))
            //            if (!rect.equals(viewPosInfo.rectF))//TODO bug dismissed...fc...
            run {
                viewPosInfo.rectF = rect
                viewPosInfo.onPosCallback!!.getPos(parent!!.width - rect.right, parent.height - rect.bottom, rect, viewPosInfo.marginInfo!!)
            }
        }

    }


    fun addHighLight(view: View, decorLayoutId: Int, onPosCallback: OnPosCallback?, lightShape: LightShape?): HighLight {
        if (onPosCallback == null && decorLayoutId != -1) {
            throw IllegalArgumentException("onPosCallback can not be null.")
        }
        val parent = mAnchor as ViewGroup?
        val rect = RectF(ViewUtils.getLocationInView(parent, view))
        //if RectF is empty return  added by isanwenyu 2016/10/26.
        if (rect.isEmpty()) return this
        val viewPosInfo = ViewPosInfo()
        viewPosInfo.layoutId = decorLayoutId
        viewPosInfo.rectF = rect
        viewPosInfo.view = view
        val marginInfo = MarginInfo()
        onPosCallback!!.getPos(parent!!.width - rect.right, parent.height - rect.bottom, rect, marginInfo)
        viewPosInfo.marginInfo = marginInfo
        viewPosInfo.onPosCallback = onPosCallback
        viewPosInfo.lightShape = lightShape ?: RectLightShape()
        mViewRects.add(viewPosInfo)

        return this
    }

    // 一个场景可能有多个步骤的高亮。一个步骤完成之后再进行下一个步骤的高亮
    // 添加点击事件，将每次点击传给应用逻辑
    fun setClickCallback(clickCallback: HighLightInterface.OnClickCallback?): HighLight {
        if (clickCallback != null) {
            mClickMessage = mListenersHandler.obtainMessage(CLICK, clickCallback)
        } else {
            mClickMessage = null
        }
        return this
    }

    fun setOnShowCallback(onShowCallback: HighLightInterface.OnShowCallback?): HighLight {
        if (onShowCallback != null) {
            mShowMessage = mListenersHandler.obtainMessage(SHOW, onShowCallback)
        } else {
            mShowMessage = null
        }
        return this
    }

    fun setOnRemoveCallback(onRemoveCallback: HighLightInterface.OnRemoveCallback?): HighLight {
        if (onRemoveCallback != null) {
            mRemoveMessage = mListenersHandler.obtainMessage(REMOVE, onRemoveCallback)
        } else {
            mRemoveMessage = null
        }
        return this
    }

    /**
     * 点击后是否自动移除
     * @see .show
     * @see .remove
     * @return 链式接口 返回自身
     * *
     * @author isanwenyu@163.com
     */
    fun autoRemove(autoRemove: Boolean): HighLight {
        this.autoRemove = autoRemove
        return this
    }

    /**
     * 获取高亮布局 如果要获取decorLayout中布局请在[.show]后调用
     *
     *
     * 高亮布局的id在[.show]中hightLightView.setId(R.id.high_light_view)设置
     * @return 返回id为R.id.high_light_view的高亮布局对象
     * *
     * @see .show
     * @author isanwenyu@163.com
     */
    val hightLightView: HightLightView?
        get() {
            if (mHightLightView != null) return mHightLightView
            if ((mContext as Activity).findViewById<FrameLayout>(R.id.high_light_view) != null)
                mHightLightView = mContext.findViewById<FrameLayout>(R.id.high_light_view) as HightLightView
            return mHightLightView

        }

    /**
     * 开启next模式
     * @see .show
     * @return 链式接口 返回自身
     * *
     * @author isanwenyu@163.com
     */
    fun enableNext(): HighLight {
        this.isNext = true
        return this
    }

    /**
     * 切换到下个提示布局
     * @return HighLight自身对象
     * *
     * @author isanwenyu@163.com
     */
    operator fun next(): HighLight {
        if (hightLightView != null)
            hightLightView!!.next()
        else
            throw NullPointerException("The HightLightView is null,you must invoke show() before this!")
        return this
    }

    override fun show() {

        if (isShowing && hightLightView != null) {
            mHightLightView = hightLightView
            return
        } else {   //如果View rect 容器为空 直接返回 added by isanwenyu 2016/10/26.
            if (mViewRects.isEmpty()) return
            val hightLightView = HightLightView(mContext, this, maskColor, mViewRects, isNext)
            //add high light view unique id by isanwenyu@163.com  on 2016/9/28.
            hightLightView.setId(R.id.high_light_view)
            //compatible with AutoFrameLayout ect.
            if (mAnchor is FrameLayout) {
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                (mAnchor as ViewGroup).addView(hightLightView, (mAnchor as ViewGroup).childCount, lp)

            } else {
                val frameLayout = FrameLayout(mContext)
                val parent = mAnchor!!.parent as ViewGroup
                parent.removeView(mAnchor)
                parent.addView(frameLayout, mAnchor!!.layoutParams)
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                frameLayout.addView(mAnchor, lp)

                frameLayout.addView(hightLightView)
            }

            if (intercept) {
                hightLightView.setOnClickListener(View.OnClickListener {
                    //added autoRemove by isanwenyu@163.com
                    if (autoRemove) remove()

                    sendClickMessage()
                })
                //如果拦截才响应显示回调
                sendShowMessage()
            }

            mHightLightView = hightLightView
            isShowing = true

        }
    }

    override fun remove() {
        if (mHightLightView == null || !isShowing) return
        val parent = mHightLightView!!.getParent() as ViewGroup
        if (parent is RelativeLayout || parent is FrameLayout) {
            parent.removeView(mHightLightView)
        } else {
            parent.removeView(mHightLightView)
            val origin = parent.getChildAt(0)
            val graParent = parent.parent as ViewGroup
            graParent.removeView(parent)
            graParent.addView(origin, parent.layoutParams)
        }
        mHightLightView = null
        if (intercept) {   //如果拦截才响应移除回调
            sendRemoveMessage()
        }
        isShowing = false
    }


    private fun sendClickMessage() {
        if (mClickMessage != null) {
            // Obtain a new message so this dialog can be re-used
            Message.obtain(mClickMessage).sendToTarget()
        }
    }

    private fun sendRemoveMessage() {
        if (mRemoveMessage != null) {
            // Obtain a new message so this dialog can be re-used
            Message.obtain(mRemoveMessage).sendToTarget()
        }
    }

    private fun sendShowMessage() {
        if (mShowMessage != null) {
            // Obtain a new message so this dialog can be re-used
            Message.obtain(mShowMessage).sendToTarget()
        }
    }

    /**
     * @see android.app.Dialog.ListenersHandler
     */
    private class ListenersHandler : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CLICK -> (msg.obj as HighLightInterface.OnClickCallback).onClick()
                REMOVE -> (msg.obj as HighLightInterface.OnRemoveCallback).onRemove()
                SHOW -> (msg.obj as HighLightInterface.OnShowCallback).onShow()
            }
        }
    }

    companion object {

        private val CLICK = 0x40
        private val REMOVE = 0x41
        private val SHOW = 0x42
    }
}
