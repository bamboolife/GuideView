package com.powervision.highligh.interfaces

/**
 * 控制高亮控件的接口
 * Created by David on 2017/8/27.
 */

interface HighLightInterface {
    /**
     * 移除
     */
    fun remove()

    /**
     * 显示
     */
    fun show()

    interface OnClickCallback{
        fun onClick()
    }

    /**
     * 显示回调监听
     */
    interface OnShowCallback {
        fun onShow()
    }

    /**
     * 移除回调监听
     */
   open interface OnRemoveCallback {
        fun onRemove()
    }

}
