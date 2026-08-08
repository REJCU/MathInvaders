package com.example.thismathinvaders.game.data

fun isColliding(x1: Float, y1: Float, r1: Float, x2: Float, y2: Float, r2: Float): Boolean {
    val dx = x1 - x2
    val dy = y1 - y2
    val distanceSq = dx * dx + dy * dy
    val collisionThreshold = r1 + r2
    return distanceSq <= collisionThreshold * collisionThreshold
}
