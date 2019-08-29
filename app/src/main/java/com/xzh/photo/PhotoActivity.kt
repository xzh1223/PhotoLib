package com.xzh.photo

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Environment
import java.io.File
import android.support.v4.content.FileProvider
import android.os.Build
import android.provider.MediaStore
import android.content.Intent
import android.content.ContentUris
import android.provider.DocumentsContract
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import java.util.*

abstract class PhotoActivity : AppCompatActivity() {

    private var outputImage: File? = null
    private var imageUri: Uri? = null
    private var imagePathList = ArrayList<String>()
    private val mRequestCode = 0x01
    private val requestMultiplePhoto = 0x02
    private val requestTakePhoto = 0x03
    private var i: Int = 0
    private var mType: Int = 1
    private var maxNum: Int = 1
    private var providerName = ""
    private var filePath = Environment.getExternalStorageDirectory().toString() + "/ekang"
    private var context: Context? = null

    /**
     * 准备从相册中选择
     *
     * @param max_num 一次最多选择的数量
     * @param provider_name 文件提供器的名称，类似“com.ichuk.ekang.fileprovider”，名称需要和Manifest中设置的对应
     *
     */
    fun toMultiplePhoto(context: Context, max_num: Int, provider_name: String) {
        this.context = context
        maxNum = max_num
        mType = 2
        providerName = provider_name
        if (providerName.isEmpty()) {
            return
        }
        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            toMultiplePhotoByPermission()
        }
    }

    /**
     * 打开相机进行拍摄图片
     * @param file_path 拍照程序文件保存的路径
     *
     */
    fun toCamera(context: Context, provider_name: String, file_path: String) {
        this.context = context
        if (file_path.isEmpty()) {
            return
        }
        if (provider_name.isEmpty()) {
            return
        }
        this.providerName = provider_name
        filePath = file_path
        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            mType = 1
            checkPermission(Manifest.permission.CAMERA)
        } else {
            toCameraByPermission()
        }
    }

    /**
     * 使用matisse作为图片筛选的工具
     *
     */
    private fun toMultiplePhotoByPermission() {
        Matisse.from(this)
            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))//选择mime的类型
            .countable(true)//设置从1开始的数字
            .maxSelectable(maxNum)//选择图片的最大数量限制
            .capture(false)//启用相机
            .captureStrategy(CaptureStrategy(true, providerName))//自定义FileProvider
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//屏幕显示方向
            .thumbnailScale(0.85f) // 缩略图的比例
            .imageEngine(GlideLoadEngine())
            .theme(R.style.Matisse_Dracula) // 黑色背景
            .forResult(requestMultiplePhoto) // 设置作为标记的请求码
    }


    /**
     * 拍摄图片准备工作
     *
     * 1.创建拍摄的图片文件
     * 2.获取到图片对应的uri
     * 3.打开相机程序
     *
     */
    private fun toCameraByPermission() {
        createFile()
        getUri()
        openCamera()
    }

    /**
     * 打开相机程序
     *
     */
    private fun openCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, requestTakePhoto)
    }

    /**
     * 获取图片文件对应的uri
     *
     */
    private fun getUri() {
        imageUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                context!!,
                providerName, outputImage!!
            )
        } else {
            Uri.fromFile(outputImage)
        }
    }

    /**
     * 创建图片文件
     *
     */
    private fun createFile() {
        val file = File(filePath)
        if (!file.exists()){
            file.mkdirs()
        }
        outputImage = File(filePath, "image.jpg")
        try {
            if (outputImage!!.exists()) {
                outputImage!!.delete()
            }
            outputImage!!.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 审查权限
     *
     * @param permission 权限名
     */
    private fun checkPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(permission)
            ActivityCompat.requestPermissions(this, permissions, mRequestCode)
        } else {
            if (mType == 1) {
                toCameraByPermission()
            } else {
                toMultiplePhotoByPermission()
            }
        }
    }

    /**
     * 权限回调
     * @param requestCode 请求代码，在发送请求的时候自定义代码
     * @param permissions 权限列表
     * @param grantResults 请求权限结果集
     *
     */
    @SuppressLint("ShowToast")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mRequestCode == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mType == 1) {
                    toCameraByPermission()
                } else {
                    toMultiplePhotoByPermission()
                }
            } else {
                Toast.makeText(context, "必须具备权限才能使用此功能！", Toast.LENGTH_SHORT)
            }
        }
    }


    /**
     * 拍照/相册回调结果处理
     * @param requestCode
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestTakePhoto -> {
                imagePathList.clear()
                if (resultCode == RESULT_OK) {
                    try {

                        val imagePath = "$filePath/image.jpg"
                        imagePathList.add(imagePath)
                        handle(imagePathList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else{
                    handle(imagePathList)
                }
            }
            requestMultiplePhoto -> {
                imagePathList.clear()
                if (resultCode == RESULT_OK) {
                    val mSelected = Matisse.obtainResult(data)

                    i = 0
                    if (Build.VERSION.SDK_INT >= 19) {
                        for (i in mSelected.indices) {
                            handleImageOnKitKat(mSelected[i])
                        }
                    } else {
                        for (i in mSelected.indices) {
                            handleImageBeforeKitKat(mSelected[i])
                        }
                    }
                    handle(imagePathList)
                } else{
                    handle(imagePathList)
                }
            }
        }
    }

    /**
     * Android 4.4之后处理图片路径的方法
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun handleImageOnKitKat(selected: Uri) {
        var imagePath: String? = null
        if (DocumentsContract.isDocumentUri(this, selected)) {
            val docId = DocumentsContract.getDocumentId(selected)
            if ("com.android.providers.media.documents" == selected.authority) {
                val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == selected.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(selected.scheme, ignoreCase = true)) {
            imagePath = getImagePath(selected, null)
        } else if ("file".equals(selected.scheme, ignoreCase = true)) {
            imagePath = selected.path
        }
        if (imagePath.isNullOrEmpty()) {
            return
        }
        imagePathList.add(imagePath)
    }

    /**
     * 获取文件路径
     *
     * @param uri 文件uri
     * @param selection
     */
    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = contentResolver.query(
            uri!!, null, selection, null, null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    /**
     * Android 4.4之前处理图片路径的方法
     *
     * @param selected 文件uri
     */
    private fun handleImageBeforeKitKat(selected: Uri) {
        val imagePath = getImagePath(selected, null)
        if (imagePath != null && imagePath != "") {
            imagePathList.add(imagePath)
        }
    }

    /**
     * 回调处理
     */
    abstract fun handle(imagePathList: List<String>)

}
