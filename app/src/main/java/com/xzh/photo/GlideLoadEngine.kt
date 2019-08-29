package com.xzh.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

/**
 * 重写glide加载器
 */
class GlideLoadEngine : ImageEngine {

    /**
     * Load thumbnail of a static image resource.
     *
     * @param context     Context
     * @param resize      Desired size of the origin image
     * @param placeholder Placeholder drawable when image is not loaded yet
     * @param imageView   ImageView widget
     * @param uri         Uri of the loaded image
     */
    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        Glide.with(context)
            .asBitmap() // some .jpeg files are actually gif
            .load(uri)
            .apply(
                RequestOptions()
                    .override(resize, resize)
                    .placeholder(placeholder)
                    .centerCrop()
            )
            .into(imageView)
    }

    override fun loadAnimatedGifThumbnail(
        context: Context,
        resize: Int,
        placeholder: Drawable,
        imageView: ImageView,
        uri: Uri
    ) {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .apply(
                RequestOptions()
                    .override(resize, resize)
                    .placeholder(placeholder)
                    .centerCrop()
            )
            .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Glide.with(context)
            .load(uri)
            .apply(
                RequestOptions()
                    .override(resizeX, resizeY)
                    .priority(Priority.HIGH)
                    .fitCenter()
            )
            .into(imageView)
    }

    override fun loadAnimatedGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Glide.with(context)
            .load(uri)
            .apply(
                RequestOptions()
                    .override(resizeX, resizeY)
                    .priority(Priority.HIGH)
            )
            .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }
}