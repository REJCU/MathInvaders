package com.example.thismathinvaders.game


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(
    context: Context,
    private val difficulty: String = "default"
) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null

    private val speedMultiplier = when (difficulty) {
        "easy" -> 0.6f
        "hard" -> 1.6f
        else -> 1f
    }

    private var ballX = 200f
    private var ballY = 200f
    private var velocityX = 6f * speedMultiplier
    private var velocityY = 4f * speedMultiplier
    private val ballRadius = 40f
    private val paint = Paint().apply {
        color = Color.CYAN
        isAntiAlias = true
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread(holder, this).apply {
            running = true
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameThread?.running = false
        gameThread?.join() // wait for thread to actually stop
        gameThread = null
    }

    fun update() {
        ballX += velocityX
        ballY += velocityY

        // Bounce off edges
        if (ballX - ballRadius < 0 || ballX + ballRadius > width) velocityX = -velocityX
        if (ballY - ballRadius < 0 || ballY + ballRadius > height) velocityY = -velocityY
    }

    fun render(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.drawCircle(ballX, ballY, ballRadius, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                velocityX = if (event.x > ballX) 6f else -6f
                velocityY = if (event.y > ballY) 4f else -4f
            }
        }
        return true
    }
}

