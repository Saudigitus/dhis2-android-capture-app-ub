package org.dhis2.commons.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.dhis2.commons.R
import org.dhis2.commons.media.MediaActivity

fun Context.playMedia(mediaUrl: String) {
    if (mediaUrl.isEmpty()) {
        Toast.makeText(
            this,
            getString(R.string.error_message_invalid_file_path),
            Toast.LENGTH_LONG
        ).show()
    } else {
        val intent = Intent(this, MediaActivity::class.java)
        intent.putExtra(MediaActivity.KEY_MEDIA_URL_VALUE, mediaUrl)
        ContextCompat.startActivity(this, intent, null)
    }
}
