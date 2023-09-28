package org.dhis2.commons.extensions

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.dhis2.commons.video.VideoActivity

fun Context.playVideo(videoUrl: String) {
    val intent = Intent(this, VideoActivity::class.java)
    intent.putExtra(VideoActivity.KEY_VIDEO_URL_VALUE, videoUrl)
    ContextCompat.startActivity(this, intent, null)
}

fun Context.playAudio(audioUrl: String) {
    //Todo: Implements this feature
}
