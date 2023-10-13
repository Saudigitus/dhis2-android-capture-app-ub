package org.dhis2.commons.media

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MediaViewModel : ViewModel(), Player.Listener {

    lateinit var exoPlayer: ExoPlayer

    fun setupPlayer(exoPlayer: ExoPlayer, mediaUrl: String) {
        this.exoPlayer = initPlayer(player = exoPlayer)

        setupMediaFile(
            exoPlayer = this.exoPlayer,
            mediaUrl = mediaUrl
        )
    }

    private fun initPlayer(player: ExoPlayer): ExoPlayer {
        player.prepare()
        player.addListener(this)
        player.playWhenReady = true
        return player
    }

    private fun setupMediaFile(exoPlayer: ExoPlayer, mediaUrl: String) {
        val mediaItem = MediaItem.fromUri(mediaUrl)
        exoPlayer.addMediaItem(mediaItem)
    }

    fun playMedia(seekTime: Long) {
        exoPlayer.seekTo(seekTime)
        exoPlayer.play()
    }

    fun pauseMedia() {
        exoPlayer.pause()
    }

    fun stopMedia() {
        exoPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
