package org.dhis2.commons.extensions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.dhis2.commons.R
import org.dhis2.commons.media.MediaActivity
import timber.log.Timber

fun Context.playMedia(mediaUrl: String) {
    if (mediaUrl.isEmpty()) {
        Toast.makeText(
            this,
            getString(R.string.error_message_invalid_file_path),
            Toast.LENGTH_LONG
        ).show()
        Timber.d("Invalid Media Path = [$mediaUrl]")
    } else {
        val intent = Intent(this, MediaActivity::class.java)
        intent.putExtra(MediaActivity.KEY_MEDIA_URL_VALUE, mediaUrl)
        ContextCompat.startActivity(this, intent, null)
    }
}

fun Context.checkConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    } else {
        @Suppress("DEPRECATION")
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        activeNetworkInfo?.isConnected == true
    }
}

