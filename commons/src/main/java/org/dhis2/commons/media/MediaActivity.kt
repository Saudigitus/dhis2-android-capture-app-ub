package org.dhis2.commons.media

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import timber.log.Timber

class MediaActivity : ComponentActivity() {

    private lateinit var mediaViewModel: MediaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exoPlayer = ExoPlayer.Builder(this).build()
        val mediaUrl = intent.getStringExtra(KEY_MEDIA_URL_VALUE) ?: KEY_MEDIA_URL_ERROR

        val factory = MediaViewModelFactory()
        mediaViewModel = ViewModelProvider(this, factory)[MediaViewModel::class.java]
        mediaViewModel.setupPlayer(
            exoPlayer = exoPlayer,
            mediaUrl = mediaUrl
        )

        if (savedInstanceState != null) {
            val seekTime = savedInstanceState.getLong(SEEK_TIME)
            mediaScreen(seekTime)
        } else {
            val seekTime = 0L
            mediaScreen(seekTime)
        }
    }

    private fun mediaScreen(seekTime: Long) {
        setContent {
            ComposeMedia(seekTime)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentPosition = mediaViewModel.exoPlayer.currentPosition
        outState.putLong(SEEK_TIME, currentPosition)
        Timber.d("Saved SeekTime: $currentPosition")
    }

    @Composable
    fun ComposeMedia(seekTime: Long) {
        val context = LocalContext.current
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

        DisposableEffect(
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                factory = {
                    PlayerView(context).apply {
                        player = mediaViewModel.exoPlayer
                        useController = true
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                }
            )
        ) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        mediaViewModel.playMedia(seekTime)
                        Timber.d("ON_CREATE")
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        mediaViewModel.pauseMedia()
                        Timber.d("ON_PAUSE")
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        mediaViewModel.playMedia(seekTime)
                        Timber.d("ON_RESUME")
                    }

                    Lifecycle.Event.ON_STOP -> {
                        mediaViewModel.stopMedia()
                        Timber.d("ON_STOP")
                    }

                    else -> {
                        Timber.d("Else event: $event.name")
                    }
                }
            }

            val lifecycle = lifecycleOwner.value.lifecycle
            lifecycle.addObserver(observer)

            onDispose {
                lifecycle.removeObserver(observer)
                mediaViewModel.exoPlayer.release()
                Timber.d("ExoPlayer released!")
            }
        }
    }

    companion object {
        const val KEY_MEDIA_URL_VALUE: String = "media_url"
        private const val SEEK_TIME: String = "SeekTime"
        private const val KEY_MEDIA_URL_ERROR: String = "media_url_error"
    }
}
