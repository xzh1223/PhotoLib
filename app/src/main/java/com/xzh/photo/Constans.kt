package com.xzh.photo

import android.os.Environment

class Constans {
    companion object {
        val MAX_NUM:String = "MAX_NUM"
        val PROVIDER_NAME:String = "PROVIDER_NAME"
        val FILE_PATH:String = "FILE_PATH"
        val PUBLIC_DIRECTORY:String = Environment.getExternalStorageDirectory().toString() + "/project_image"
        val PUBLIC_PROVIDER:String = "com.xzh.photo.fileprovider"
        val IMAGE_STRING_PATH:String = "IMAGE_STRING_PATH"
        val IMAGE_INT_RESOURCE:String = "IMAGE_INT_RESOURCE"
    }

}