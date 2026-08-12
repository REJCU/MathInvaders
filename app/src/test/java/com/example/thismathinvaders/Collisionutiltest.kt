package com.example.thismathinvaders

import com.example.thismathinvaders.game.data.isColliding
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CollisionUtilsTest {

    @Test
    fun identicalCentersWithPositiveRadiiAlwaysCollide() {
        assertTrue(isColliding(0f, 0f, 10f, 0f, 0f, 10f))
    }

    @Test
    fun circlesFarApartDoNotCollide() {
        assertFalse(isColliding(0f, 0f, 10f, 1000f, 1000f, 10f))
    }

    @Test
    fun circlesExactlyTouchingAtTheRadiusBoundaryCountAsColliding() {
        assertTrue(isColliding(0f, 0f, 10f, 20f, 0f, 10f))
    }

    @Test
    fun circlesJustOutsideTheRadiusBoundaryDoNotCollide() {
        assertFalse(isColliding(0f, 0f, 10f, 20.1f, 0f, 10f))
    }

    @Test
    fun collisionIsSymmetricRegardlessOfArgumentOrder() {
        val a = isColliding(5f, 5f, 8f, 12f, 9f, 6f)
        val b = isColliding(12f, 9f, 6f, 5f, 5f, 8f)
        assertTrue(a == b)
    }

    @Test
    fun diagonalOverlapIsDetectedCorrectly() {
        assertTrue(isColliding(0f, 0f, 3f, 3f, 4f, 3f))
    }
}