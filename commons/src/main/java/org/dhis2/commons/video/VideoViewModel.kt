package org.dhis2.commons.video

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class VideoViewModel : ViewModel(), Player.Listener {

    lateinit var exoPlayer: ExoPlayer

    @Deprecated("")
    fun setupPlayer(exoPlayer: ExoPlayer) {
        this.exoPlayer = exoPlayer
        exoPlayer.prepare()
        exoPlayer.addListener(this)
        exoPlayer.playWhenReady = true
    }

    @Deprecated("")
    fun setupMP4File(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.addMediaItem(mediaItem)
    }

    fun playVideo(seekTime: Long) {
        if (exoPlayer.playbackState == Player.STATE_IDLE) {
            setupPlayer(exoPlayer)
        }
        exoPlayer.seekTo(seekTime)
        exoPlayer.play()
    }

    fun pauseVideo() {
        exoPlayer.pause()
    }

    fun stopVideo() {
        exoPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
