package com.powervision.layerdemo

import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.powervision.highligh.HighLight
import com.powervision.highligh.interfaces.HighLightInterface
import com.powervision.highligh.position.OnBottomPosCallback
import com.powervision.highligh.position.OnLeftPosCallback
import com.powervision.highligh.position.OnRightPosCallback
import com.powervision.highligh.position.OnTopPosCallback
import com.powervision.highligh.shape.BaseLightShape
import com.powervision.highligh.shape.CircleLightShape
import com.powervision.highligh.shape.RectLightShape

class MainActivity : AppCompatActivity() {

    private var mHightLight: HighLight? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showTipView(view: View) {
        mHightLight = HighLight(this@MainActivity)//
                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.btn_rightLight, R.layout.info_gravity_left_down, OnLeftPosCallback(45F), RectLightShape())
                .addHighLight(R.id.btn_light, R.layout.info_gravity_left_down, OnRightPosCallback(5f), CircleLightShape())
                .addHighLight(R.id.btn_bottomLight, R.layout.info_gravity_left_down, OnTopPosCallback(), CircleLightShape())
                .addHighLight(view, R.layout.info_gravity_left_down, OnBottomPosCallback(60f), CircleLightShape())
        mHightLight!!.show()
    }

    /**
     * 显示next模式提示布局
     * @param view
     * *
     * @author isanwenyu@163.com
     */
    fun showNextTipView(view: View) {
        mHightLight = HighLight(this@MainActivity)//
                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.btn_rightLight, R.layout.info_gravity_left_down, OnLeftPosCallback(45f), RectLightShape())
                .addHighLight(R.id.btn_light, R.layout.info_gravity_left_down, OnRightPosCallback(5f), CircleLightShape())
                .addHighLight(R.id.btn_bottomLight, R.layout.info_gravity_left_down, OnTopPosCallback(), CircleLightShape())
                .addHighLight(view, R.layout.info_gravity_left_down, OnBottomPosCallback(60f), CircleLightShape())
                .autoRemove(false)
                .enableNext()
                .setClickCallback(object : HighLightInterface.OnClickCallback {
                   override fun onClick() {
                        Toast.makeText(this@MainActivity, "clicked and show next tip view by yourself", Toast.LENGTH_SHORT).show()
                        mHightLight!!.next()
                    }
                })
        mHightLight!!.show()
    }

    /**
     * 显示我知道了提示高亮布局
     * @param view id为R.id.iv_known的控件
     * *
     * @author isanwenyu@163.com
     */
    fun showKnownTipView(view: View) {
        mHightLight = HighLight(this@MainActivity)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(false)//设置拦截属性为false 高亮布局不影响后面布局的滑动效果 而且使下方点击回调失效
                .setClickCallback(object : HighLightInterface.OnClickCallback {
                  override  fun onClick() {
                        Toast.makeText(this@MainActivity, "clicked and remove HightLight view by yourself", Toast.LENGTH_SHORT).show()
                        remove(null)
                    }
                })
                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.btn_rightLight, R.layout.info_known, OnLeftPosCallback(45f), RectLightShape())
                .addHighLight(R.id.btn_light, R.layout.info_known, OnRightPosCallback(5f), CircleLightShape())
                .addHighLight(R.id.btn_bottomLight, R.layout.info_known, OnTopPosCallback(), CircleLightShape())
                .addHighLight(view, R.layout.info_known, OnBottomPosCallback(10f), RectLightShape())
        mHightLight!!.show()

        //        //added by isanwenyu@163.com 设置监听器只有最后一个添加到HightLightView的knownView响应了事件
        //        //优化在布局中声明onClick方法 {@link #clickKnown(view)}响应所有R.id.iv_known的控件的点击事件
        //        View decorLayout = mHightLight.getHightLightView();
        //        ImageView knownView = (ImageView) decorLayout.findViewById(R.id.iv_known);
        //        knownView.setOnClickListener(new View.OnClickListener()
        //          {
        //            @Override
        //            public void onClick(View view) {
        //                remove(null);
        //            }
        //        });
    }

    /**
     * 显示 next模式 我知道了提示高亮布局
     * @param view id为R.id.iv_known的控件
     * *
     * @author isanwenyu@163.com
     */
    fun showNextKnownTipView(view: View) {
        mHightLight = HighLight(this@MainActivity)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                //                .intercept(false)//设置拦截属性为false 高亮布局不影响后面布局的滑动效果
                .intercept(true)//拦截属性默认为true 使下方callback生效
                .enableNext()//开启next模式并通过show方法显示 然后通过调用next()方法切换到下一个提示布局，直到移除自身
                //                .setClickCallback(new HighLight.OnClickCallback() {
                //                    @Override
                //                    public void onClick() {
                //                        Toast.makeText(MainActivity.this, "clicked and remove HightLight view by yourself", Toast.LENGTH_SHORT).show();
                //                        remove(null);
                //                    }
                //                })
                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.btn_rightLight, R.layout.info_known, OnLeftPosCallback(45f), RectLightShape())
                .addHighLight(R.id.btn_light, R.layout.info_known, OnRightPosCallback(5f), object : BaseLightShape(5f, 5f) {
                    override protected fun resetRectF4Shape(viewPosInfoRectF: RectF, dx: Float, dy: Float) {
                        //缩小高亮控件范围
                        viewPosInfoRectF.inset(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dx, resources.displayMetrics), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dy, resources.displayMetrics))
                    }

                    override protected fun drawShape(bitmap: Bitmap, viewPosInfo: HighLight.ViewPosInfo) {
                        //custom your hight light shape 自定义高亮形状
                        val canvas = Canvas(bitmap)
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                        paint.isDither = true
                        paint.isAntiAlias = true
                        paint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
                        val rectF = viewPosInfo.rectF
                        canvas.drawOval(rectF, paint)
                    }
                })
                .addHighLight(R.id.btn_bottomLight, R.layout.info_known, OnTopPosCallback(), CircleLightShape())
                .addHighLight(view, R.layout.info_known, OnBottomPosCallback(10f), RectLightShape())
                .setOnRemoveCallback(object : HighLightInterface.OnRemoveCallback {
                    //监听移除回调 intercept为true时生效
                    override fun onRemove() {
                        Toast.makeText(this@MainActivity, "The HightLight view has been removed", Toast.LENGTH_SHORT).show()

                    }
                })
                .setOnShowCallback(object : HighLightInterface.OnShowCallback{
                    //监听显示回调 intercept为true时生效
                    override fun onShow() {
                        Toast.makeText(this@MainActivity, "The HightLight view has been shown", Toast.LENGTH_SHORT).show()
                    }
                })
        mHightLight!!.show()
    }

    /**
     * 响应所有R.id.iv_known的控件的点击事件
     *
     *
     * 移除高亮布局
     *

     * @param view
     */
    fun clickKnown(view: View) {
        if (mHightLight!!.isShowing && mHightLight!!.isNext)
        //如果开启next模式
        {
            mHightLight!!.next()
        } else {
            remove(null)
        }
    }

    private fun showTipMask() {
        //        mHightLight = new HighLight(MainActivity.this)//
        //                .anchor(findViewById(R.id.id_container))
        //如果是Activity上增加引导层，不需要设置anchor
        //                .addHighLight(R.id.id_btn_important, R.layout.info_up,
        //                        new HighLight.OnPosCallback()
        //                        {
        //                            @Override
        //                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo)
        //                            {
        //                                marginInfo.leftMargin = rectF.right - rectF.width() / 2;
        //                                marginInfo.topMargin = rectF.bottom;
        //                            }
        //                        })//
        //                .addHighLight(R.id.id_btn_amazing, R.layout.info_down, new HighLight.OnPosCallback()
        //                {
        //                    /**
        //                     * @param rightMargin 高亮view在anchor中的右边距
        //                     * @param bottomMargin 高亮view在anchor中的下边距
        //                     * @param rectF 高亮view的l,t,r,b,w,h都有
        //                     * @param marginInfo 设置你的布局的位置，一般设置l,t或者r,b
        //                     */
        //                    @Override
        //                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo)
        //                    {
        //                        marginInfo.rightMargin = rightMargin + rectF.width() / 2;
        //                        marginInfo.bottomMargin = bottomMargin + rectF.height();
        //                    }
        //
        //                });
        //        .addHighLight(R.id.id_btn_important_right,R.layout.info_gravity_right_up, new HighLight.OnPosCallback(){
        //
        //
        //            @Override
        //            public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
        //                marginInfo.rightMargin = rightMargin;
        //                marginInfo.topMargin = rectF.top + rectF.height();
        //            }
        //        })
        //        .addHighLight(R.id.id_btn_whoami, R.layout.info_gravity_left_down, new HighLight.OnPosCallback() {
        //
        //
        //            @Override
        //            public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
        //                marginInfo.leftMargin = rectF.right - rectF.width()/2;
        //                marginInfo.bottomMargin = bottomMargin + rectF.height();
        //            }
        //        })
        //        .setClickCallback(new HighLight.OnClickCallback() {
        //            @Override
        //            public void onClick() {
        //                Toast.makeText(MainActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        //            }
        //        });

        //        mHightLight.show();
        //        mHightLight = new HighLight(MainActivity.this)//
        //                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
        //                .addHighLight(R.id.btn_rightLight,R.layout.info_left, new OnLeftPosCallback(10),new RectLightShape())
        //                .addHighLight(R.id.btn_light,R.layout.info_right,new OnRightPosCallback(),new CircleLightShape())
        //                .addHighLight(R.id.btn_bottomLight,R.layout.info_up,new OnTopPosCallback(46),new CircleLightShape())
        //                .addHighLight(R.id.id_btn_amazing,R.layout.info_up,new OnBottomPosCallback(46),new CircleLightShape());
        //        mHightLight.show();
    }


    fun remove(view: View?) {
        mHightLight!!.remove()
    }

    fun add(view: View) {
        mHightLight!!.show()
    }
}
