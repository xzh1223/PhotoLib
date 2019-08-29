package com.xzh.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_dialog_select_photo_style.*


class SelectPhotoStyleActivity : PhotoActivity() {

    private var maxNum: Int = 9
    private var providerName: String? = null
    private var filePath: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_select_photo_style)

        getDataByIntent()
        setEvent()
    }

    /**
     * 事件点击
     */
    @SuppressLint("ShowToast")
    private fun setEvent() {
        tv_select_by_album.setOnClickListener {
            if (maxNum == 0) {
                maxNum = 9
            }
            if (providerName.isNullOrEmpty()) {
                Log.e("SelectPhotoStyle->", "请输入文件提供器的名称")
                return@setOnClickListener
            }
            toMultiplePhoto(this, maxNum, providerName!!)
        }
        tv_select_by_camera.setOnClickListener {
            if (providerName.isNullOrEmpty()) {
                Log.e("SelectPhotoStyle->", "请输入文件提供器的名称")
                return@setOnClickListener
            }
            if (filePath.isNullOrEmpty()) {
                Log.e("SelectPhotoStyle->", "请输入文件存储位置")
                return@setOnClickListener
            }
            toCamera(
                this,
                providerName!!,
                filePath!!
            )
        }
        rl_view.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }

    /**
     * 从intent中获取数据
     */
    private fun getDataByIntent() {
        maxNum = intent.getIntExtra(Constans.MAX_NUM, 9)
        providerName = intent.getStringExtra(Constans.PROVIDER_NAME)
        filePath = intent.getStringExtra(Constans.FILE_PATH)
    }


    /**
     * 图片处理
     * @param imagePathList 图片路径List
     *
     */
    override fun handle(imagePathList: List<String>) {
        val intent = intent
        val list = imagePathList as ArrayList<String>
        intent.putExtra("data", list)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * 返回键
     */
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

}