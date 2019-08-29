# PhotoLib
相机相册程序封装

提供两种方式进行调用：

方式1、直接跳转到弹窗选择从相册或者相机的页面；

  使用startActivityForResult()进行意图跳转，在回调程序中对携带的图片路径可进行上传等处理。
  
        // 意图跳转到选择相机相册程序
        btn_to.setOnClickListener {
  
            val intent = Intent(this, SelectPhotoStyleActivity::class.java)
            
            intent.putExtra(Constans.MAX_NUM, 9)
            
            intent.putExtra(Constans.PROVIDER_NAME, "com.xzh.photo.fileprovider")
            
            intent.putExtra(Constans.FILE_PATH, Constans.PUBLIC_DIRECTORY)
            
            startActivityForResult(intent,1)
            
        }
        
        // 回调处理返回的图片数组
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
        
 方式2、让activity继承PhotoActivity抽象类，实现抽象类中的方法， 调用toMultiplePhoto()方法去吊起从相册选择图片程序，调用toCamera()方法去吊起使用相机拍摄图片程序；具体使用可参考SelectPhotoActivity类中的方法。
 
 
