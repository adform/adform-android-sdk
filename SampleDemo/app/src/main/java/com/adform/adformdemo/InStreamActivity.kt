package com.adform.adformdemo

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.adform.sdk.pub.ContentPlayback
import com.adform.sdk.pub.VideoPlayer
import com.adform.sdk.pub.VideoPlayer.ExternalBuilder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

class InStreamActivity : AppCompatActivity() {

    lateinit var adVideoPlayer: VideoPlayer
    lateinit var videoView: StyledPlayerView
    lateinit var externalPlayer: ExoPlayer

    var initialPlay = true

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_stream)

        externalPlayer = ExoPlayer.Builder(this).build()
        videoView = findViewById(R.id.video_view)
        with(videoView) {
            player = externalPlayer
            showController()
        }

        setupAdVideoPlayer()
        setupExternalPlayer()
    }

    private fun setupAdVideoPlayer() {
        val preRollMasterTag = intent.extras!!.getInt(BUNDLE_KEY_MASTER_TAG, -1)
        val contentLayout = findViewById<FrameLayout>(R.id.content)
        adVideoPlayer = ExternalBuilder()
            .setContext(this)
            .setContainer(contentLayout)
            .setPreRollMasterTag(preRollMasterTag)
            .setPreloadAds(true)
            .setContentPlayback(object : ContentPlayback {
                override fun playContent() {
                    externalPlayer.play()
                }

                override fun pauseContent() {
                    externalPlayer.pause()
                }

                override fun setFullscreen(isFullscreen: Boolean) {
                    if (isFullscreen) {
                        videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    } else {
                        videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                }

                override fun isFullscreen(): Boolean {
                    return false
                }

                override fun getContentDuration(): Long {
                    return externalPlayer.duration
                }

                override fun getCurrentTimePosition(): Long {
                    return externalPlayer.currentPosition
                }
            })
            .build()
    }

    private fun setupExternalPlayer() {
        externalPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    adVideoPlayer.onVideoPlaybackCompleted()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying && initialPlay) {
                    adVideoPlayer.onPlayStart()
                    initialPlay = false
                }
            }
        })
        val mediaItem =
            MediaItem.fromUri(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
        externalPlayer.setMediaItem(mediaItem)
    }

    public override fun onResume() {
        super.onResume()
        adVideoPlayer.onResume()
    }

    public override fun onPause() {
        super.onPause()
        adVideoPlayer.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        adVideoPlayer.onDestroy()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adVideoPlayer.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        adVideoPlayer.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        const val BUNDLE_KEY_MASTER_TAG = "BUNDLE_KEY_MASTER_TAG"
    }
}
