package org.dhis2.commons.extensions

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.dhis2.commons.media.MediaActivity

fun Context.playMedia(mediaUrl: String) {
    val intent = Intent(this, MediaActivity::class.java)
    intent.putExtra(MediaActivity.KEY_MEDIA_URL_VALUE, mediaUrl)
    ContextCompat.startActivity(this, intent, null)
}
