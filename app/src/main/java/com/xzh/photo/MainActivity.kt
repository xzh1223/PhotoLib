package com.xzh.photo

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_to.setOnClickListener {
            val intent = Intent(this, SelectPhotoStyleActivity::class.java)
            intent.putExtra(Constans.MAX_NUM, 9)
            intent.putExtra(Constans.PROVIDER_NAME, "com.xzh.photo.fileprovider")
            intent.putExtra(Constans.FILE_PATH, Constans.PUBLIC_DIRECTORY)
            startActivityForResult(intent,1)
        }
        btn_view_image.setOnClickListener {
            val intent = Intent(this, ViewImageLargeActivity::class.java)
            intent.putExtra(Constans.IMAGE_INT_RESOURCE, R.mipmap.icon_load_error)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    var content = ""
                    var imagePathList = data!!.getStringArrayListExtra("data")
                    for (i in imagePathList.indices) {
                        Log.e("-->", imagePathList[i])
                        content+=imagePathList[i]
                    }
                    tv_content.text = content
                }
            }
        }
    }
}
