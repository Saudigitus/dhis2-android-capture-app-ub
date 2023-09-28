package org.dhis2.commons.video

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
import javax.inject.Inject

class FullScreenActivity : ComponentActivity() {

//    private lateinit var viewModel: VideoViewModel

    @Inject
    lateinit var videoViewModelFactory: VideoViewModelFactory
    private lateinit var videoViewModel: VideoViewModel
//    private val viewModel: VideoViewModel by viewModels { videoViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra(KEY_VIDEO_URL_VALUE) ?: KEY_VIDEO_URL_ERROR

//        viewModel = getViewModel()
        val exoPlayer = ExoPlayer.Builder(this).build()
        val factory = VideoViewModelFactory()
        videoViewModel = ViewModelProvider(this, factory)[VideoViewModel::class.java]
        videoViewModel.setupPlayer(exoPlayer)
        videoViewModel.setupMP4File(videoUrl)

        if (savedInstanceState != null) {
            val seekTime = savedInstanceState.getLong(SEEK_TIME)
            videoScreen(seekTime)
        } else {
            val seekTime = 0L
            videoScreen(seekTime)
        }
    }

    private fun videoScreen(seekTime: Long) {
        setContent {
            ComposeVideo(seekTime)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentPosition = videoViewModel.exoPlayer.currentPosition
        outState.putLong(SEEK_TIME, currentPosition)
        Timber.d("Saved SeekTime: $currentPosition")
    }

    @Composable
    fun ComposeVideo(seekTime: Long) {
        val context = LocalContext.current
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

        DisposableEffect(
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                factory = {
                    PlayerView(context).apply {
                        player = videoViewModel.exoPlayer
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
                        videoViewModel.playVideo(seekTime)
                        Timber.d("ON_CREATE")
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        videoViewModel.pauseVideo()
                        Timber.d("ON_PAUSE")
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        videoViewModel.playVideo(seekTime)
                        Timber.d("ON_RESUME")
                    }

                    Lifecycle.Event.ON_STOP -> {
                        videoViewModel.stopVideo()
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
                videoViewModel.exoPlayer.release()
                Timber.d("ExoPlayer released!")
            }
        }
    }

    companion object {
        private const val SEEK_TIME: String = "SeekTime"
        const val KEY_VIDEO_URL_VALUE: String = "video_url"
        private const val KEY_VIDEO_URL_ERROR: String = "video_url_error"
    }
}
