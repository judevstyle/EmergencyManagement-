package com.idon.emergencmanagement.view.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import com.idon.emergencmanagement.R
import com.tt.workfinders.BaseClass.BaseActivity

class AlermActivity : BaseActivity(){

    var mMediaPlayer: MediaPlayer? = null


    override fun getContentView(): Int {
        return R.layout.activity_alerm
    }


    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent?) {
        super.onViewReady(savedInstanceState, intent)

        playSound()


    }

    fun playSound() {
        val count = 100 * .01f

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.alerm)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.setVolume(count,count);

            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    // 2. Pause playback
    fun pauseSound() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) mMediaPlayer!!.pause()
    }

    // 3. {optional} Stops playback
    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // 4. Closes the MediaPlayer when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }


}