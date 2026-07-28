package com.example.thismathinvaders.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.ui.geometry.CornerRadius
import androidx.core.content.ContextCompat
import com.example.thismathinvaders.R
import kotlin.random.Random


data class Meteor(
    var x: Float,
    var y: Float,
    val equation: String,
    val answer: Int,
    val radius: Float = 70f,
    var speed: Float,
)

val padding = 80

class GameView(
    context: Context,
    private val difficulty: String = "default"
) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null

    private val meteors = mutableListOf<Meteor>()
    private var framesSinceSpawn = 0
    private val spawnEveryFrames = 90 // 1.5s = 60fps

    private val meteorsprite: Drawable? = ContextCompat.getDrawable(context, R.drawable.shooting_star_svgrepo)

    private var score = 0
    private var lives = 3

    private val speedMultiplier = when (difficulty) {
        "easy" -> 0.6f
        "hard" -> 1.6f
        else -> 1f
    }

    private val problemDiff = when (difficulty) {
        "easy" -> 5
        "hard" -> 20
        else -> 10
    }


    private val shipSprite: Drawable? = ContextCompat.getDrawable(context, R.drawable.spaceship_svgrepo)
    private var shipX = -1f
    private var shipY = -1f
    private val shipRadius = 70f

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    private val equationPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val hudPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        isAntiAlias = true
    }

    private val gameOverText = Paint().apply {
        color = Color.RED
        textSize = 128f
        isAntiAlias = true
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

    val padding = 80

    private fun spawnMeteor() {
        if (width == 0) return
        val a = Random.nextInt(1, problemDiff)
        val b = Random.nextInt(1, problemDiff)
        val x = Random.nextInt(padding, (width - 80).coerceAtLeast(padding + 1)).toFloat()
        meteors.add(
            Meteor(
                x = x,
                y = -80f,
                equation = "$a + $b",
                answer = a + b,
                speed = (3f + Random.nextFloat() * 2f) * speedMultiplier
            )
        )
    }



    fun update() {
        if (shipX < 0 && width > 0) {
            shipX = width / 2f
            shipY = height - 200f
        }

        framesSinceSpawn++
        if (framesSinceSpawn >= spawnEveryFrames) {
            spawnMeteor()
            framesSinceSpawn = 0
        }

        val iterator = meteors.iterator()
        while (iterator.hasNext()) {
            val meteor = iterator.next()
            meteor.y += meteor.speed

            // TODO - ship meteor collision
            if (shipX >= 0) {
                val dx = meteor.x - shipX
                val dy = meteor.y - shipY
                val distanceSquared = dx * dx + dy * dy
                val collisonThreshold = meteor.radius + shipRadius

                if (distanceSquared <= collisonThreshold * collisonThreshold) {
                    iterator.remove()
                    lives -= 1
                    continue
                }
            }

            if (meteor.y - meteor.radius > height) {
                iterator.remove() // fell off the bottom
            }
        }
    }


    fun render(canvas: Canvas) {
        canvas.drawColor(Color.DKGRAY)

        for (meteor in meteors) {
            meteorsprite?.let { drawable ->
                val size = (meteor.radius * 2).toInt()
                drawable.setBounds(
                    (meteor.x - meteor.radius).toInt(),
                    (meteor.y - meteor.radius).toInt(),
                    (meteor.x - meteor.radius).toInt() + size,
                    (meteor.y - meteor.radius).toInt() + size
                )
                drawable.draw(canvas)
            }
            canvas.drawText(meteor.equation, meteor.x, meteor.y, equationPaint)
        }

        if ( lives == 0 ) {
            // TODO - either retry screen or boot back to menu
           canvas.drawText("Game over ", 180f, 180f, gameOverText )
        }

        if (shipX >= 0) {
            shipSprite?.let { drawable ->
                val size = (shipRadius * 2).toInt()
                drawable.setBounds(
                    (shipX - shipRadius).toInt(),
                    (shipY - shipRadius).toInt(),
                    (shipX - shipRadius).toInt() + size,
                    (shipY - shipRadius).toInt() + size
                )
                drawable.draw(canvas)
            }
        }
        canvas.drawText("Score: $score", 24f, 60f, hudPaint)
        canvas.drawText("Lives: $lives", 24f, 120f, hudPaint)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                shipX = event.x.coerceIn(shipRadius, (width - shipRadius).coerceAtLeast(shipRadius))
            }
        }
        return true
    }
}


