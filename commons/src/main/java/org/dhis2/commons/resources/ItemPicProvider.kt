package org.dhis2.commons.resources

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.util.Locale
import org.dhis2.commons.bindings.clipWithAllRoundedCorners
import org.dhis2.commons.bindings.dp


fun ImageView.setItemPic(
    imagePath: String?,
    defaultImageRes: Int,
    defaultColorRes: Int,
    defaultValue: String?,
    isSingleEvent: Boolean = false,
    textView: TextView?
) {
    val colorPrimary = ColorUtils.getPrimaryColor(
        context,
        ColorUtils.ColorType.PRIMARY
    )
    when {
        imagePath?.isNotEmpty() == true -> {
            visibility = View.VISIBLE
            textView?.visibility = View.GONE
            Glide.with(context).load(File(imagePath))
                .transform(CircleCrop())
                .placeholder(defaultImageRes)
                .error(defaultImageRes)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .skipMemoryCache(true)
                .into(this)
        }

        defaultValue != null && !isSingleEvent -> {
            visibility = View.GONE
            textView?.visibility = View.VISIBLE
            textView?.setBackgroundColor(colorPrimary)
            textView?.clipWithAllRoundedCorners(20.dp)
            setImageDrawable(null)
            textView?.text = defaultValue.first().toString().toUpperCase(Locale.getDefault())
            textView?.setTextColor(ColorUtils.getAlphaContrastColor(colorPrimary))
            textView?.setBackgroundColor(colorPrimary)
        }
        else -> {
            visibility = View.VISIBLE
            textView?.visibility = View.GONE
            setBackgroundColor(colorPrimary)
            clipWithAllRoundedCorners(6.dp)
            ContextCompat.getDrawable(context, defaultImageRes)?.let {
                Glide.with(context).load(
                    ColorUtils.tintDrawableReosurce(it, colorPrimary)
                ).transform(RoundedCorners(6.dp))
                    .placeholder(defaultImageRes)
                    .error(defaultImageRes)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .skipMemoryCache(true)
                    .into(this)
            }
        }
    }
}
