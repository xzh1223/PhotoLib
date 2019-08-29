package com.xzh.photo

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_view_image_large.*

/**
 * 大图显示
 */
class ViewImageLargeActivity: AppCompatActivity() {

    private var imageStringPath: String? = null
    private var imageIntResource: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image_large)
        getDataByIntent()
        checkImageToView()
        setEvent()
    }

    /**
     * 事件处理
     */
    private fun setEvent() {
        pv_image.setOnClickListener { finish() }
    }

    /**
     * 审核传递的图片资源是否有效
     */
    private fun checkImageToView() {
        if (imageIntResource == 0 && imageStringPath.isNullOrEmpty()) {
            return
        }
        if (imageIntResource != 0) {
            Glide.with(this)
                .load(imageIntResource)
                .apply(RequestOptions().placeholder(R.mipmap.icon_loading).error(R.mipmap.icon_load_error))
                .into(pv_image)
            return
        }
        if (imageStringPath!!.isNotEmpty()){
            Glide.with(this)
                .load(imageStringPath)
                .apply(RequestOptions().placeholder(R.mipmap.icon_loading).error(R.mipmap.icon_load_error))
                .into(pv_image)
            return
        }
    }

    /**
     * 从intent中得到传递的数据
     */
    private fun getDataByIntent() {
        imageStringPath = intent.getStringExtra(Constans.IMAGE_STRING_PATH)
        imageIntResource = intent.getIntExtra(Constans.IMAGE_INT_RESOURCE, 0)
    }


    /**
     * 全屏模式
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

}