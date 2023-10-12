package org.dhis2.commons.video

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class VideoViewModel : ViewModel(), Player.Listener {

    lateinit var exoPlayer: ExoPlayer

    fun setupPlayer(exoPlayer: ExoPlayer, videoUrl: String) {
        this.exoPlayer = initPlayer(player = exoPlayer)

        setupMediaFile(
            exoPlayer = this.exoPlayer,
            videoUrl = videoUrl
        )
    }

    private fun initPlayer(player: ExoPlayer): ExoPlayer {
        player.prepare()
        player.addListener(this)
        player.playWhenReady = true
        return player
    }

    private fun setupMediaFile(exoPlayer: ExoPlayer, videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.addMediaItem(mediaItem)
    }

    fun playVideo(seekTime: Long) {
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
