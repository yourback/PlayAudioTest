package com.sp.playaudiotest

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initMediaPlayer()


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            var progressLast = 0

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    progressLast = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("测试", "松手：$progressLast")
                // 播放器操作
                mediaPlayer.seekTo(progressLast)
                // 进度条操作
                initCountDownTimer(mediaPlayer.currentPosition, duration = mediaPlayer.duration)
                start.performClick()
            }
        })

        start.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start() // 播放
                countDownTimer?.start()
            }
        }

        pause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause() // 暂停
                initCountDownTimer(mediaPlayer.currentPosition, duration = mediaPlayer.duration)
            }
        }

        stop.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.reset()
                initMediaPlayer()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        destroyCountDownTimer()
    }


    private fun initMediaPlayer() {
        val assetsManager = assets
        val fd = assetsManager.openFd("music.mp3")
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()

        initCountDownTimer(duration = mediaPlayer.duration)
    }


    private fun destroyCountDownTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }


    private fun initCountDownTimer(current: Int = 0, duration: Int) {
        tv_duration.text = (duration / 1000).toString()
        tv_current.text = (current / 1000).toString()

        seekBar.progress = current

        seekBar.max = duration

        destroyCountDownTimer()

        countDownTimer = object : CountDownTimer(duration.toLong() - current, 1) {
            override fun onFinish() {
                stop.performClick()
            }

            override fun onTick(millisUntilFinished: Long) {
                seekBar.progress = (duration.toLong() - millisUntilFinished).toInt()
                tv_current.text = (seekBar.progress / 1000).toString()
            }
        }
    }
}