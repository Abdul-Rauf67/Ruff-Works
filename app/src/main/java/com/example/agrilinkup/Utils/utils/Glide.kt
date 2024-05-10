package com.example.agrilinkup.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object Glide {
    fun loadImage(context: Context, imageUrl: String, imageView: ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)
    }

    fun loadImageWithListener(
        context: Context,
        imageUrl: String,
        imageView: ImageView,
        codeInListenerCallback: () -> Unit
    ) {
        Glide.with(context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    codeInListenerCallback.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    codeInListenerCallback.invoke()
                    return false
                }
            })
            .into(imageView)
    }

    fun loadImageWithPlaceholder(
        context: Context,
        imageUrl: String,
        imageView: ImageView,
        placeholder: Int
    ) {
        Glide.with(context)
            .load(imageUrl)
            .placeholder(placeholder)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            .into(imageView)
    }
}