package com.example.thismathinvaders.game.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.thismathinvaders.R


class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val fireSoundId = soundPool.load(context, R.raw.fire, 1)
    private val correctSoundId = soundPool.load(context, R.raw.correct_hit, 1)
    private val incorrectSoundId = soundPool.load(context, R.raw.incorrect_hit, 1)
    private val gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)

    private val musicPlayer: MediaPlayer? = try {
        MediaPlayer.create(context, R.raw.background_music)?.apply {
            isLooping = true
        }
    } catch (e: Exception) {
        null
    }


    // could be annoying, depending on sound
    fun playFire() {
        soundPool.play(fireSoundId, 1f, 1f, 1, 0, 1f)
    }

    // TODO - not working, changed .wav to short, long files but no success
    fun playCorrectHit(volume: Float = 1f) {
        soundPool.play(correctSoundId, volume, volume, 1, 5, 1f)
    }

    fun playIncorrectHit(volume: Float = 1f) {
        soundPool.play(incorrectSoundId, volume, volume, 1, 5, 1f)
    }

    fun playGameOver(volume: Float = 1f) {
        soundPool.play(gameOverSoundId, volume, volume, 1, 10, 1f)
    }

    fun startMusic(volume: Float = 1f) {
        musicPlayer?.let {
            it.setVolume(volume, volume)
            if (!it.isPlaying) it.start()
        }
    }

    fun pauseMusic() {
        musicPlayer?.let { if (it.isPlaying) it.pause() }
    }

    fun setMusicVolume(volume: Float) {
        musicPlayer?.setVolume(volume, volume)
    }


    fun release() {
        soundPool.release()
    }
}