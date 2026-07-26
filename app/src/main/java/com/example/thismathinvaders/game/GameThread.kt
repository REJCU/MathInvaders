package com.example.thismathinvaders.game

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {

    @Volatile
    var running = false
    private val targetFps = 60
    private val targetFrameTimeMs = 1000L / targetFps

    override fun run() {
        while (running) {
            val startTime = System.currentTimeMillis()
            var canvas: Canvas? = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    canvas?.let { gameView.render(it) }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }

            val elapsed = System.currentTimeMillis() - startTime
            val sleepTime = targetFrameTimeMs - elapsed
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime)
                } catch (e: InterruptedException) {
                }
            }
        }
    }
}

