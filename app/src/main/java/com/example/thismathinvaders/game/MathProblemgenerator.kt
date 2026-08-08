package com.example.thismathinvaders.game

import com.example.thismathinvaders.game.data.GameSettings
import kotlin.random.Random

enum class MathOperation { ADD, SUBTRACT }

class MathProblemGenerator(private val random: Random = Random.Default) {

    fun getRandomOperation(settings: GameSettings): MathOperation {
        val activeOps = mutableListOf<MathOperation>()
        if (settings.allowAddition) activeOps.add(MathOperation.ADD)
        if (settings.allowSubtraction) activeOps.add(MathOperation.SUBTRACT)

        if (activeOps.isEmpty()) {
            return MathOperation.ADD
        }
        return activeOps[random.nextInt(activeOps.size)]
    }

    fun generateProblem(settings: GameSettings): Pair<String, Int> {
        val op = getRandomOperation(settings)
        // Keep a minimum gap of 5 between min and max so there's always enough
        // room to generate a valid problem (especially for subtraction, which
        // needs at least two distinct values within the range).
        val minVal = settings.minNumberRange.coerceAtLeast(0)
        val maxVal = settings.maxNumberRange.coerceAtLeast(minVal + 5)

        return when (op) {
            MathOperation.ADD -> {
                val a = random.nextInt(minVal, maxVal + 1)
                val b = random.nextInt(minVal, maxVal + 1)
                Pair("$a + $b", a + b)
            }
            MathOperation.SUBTRACT -> {
                // a must be strictly greater than minVal so there's room for a b < a
                val a = random.nextInt(minVal + 1, maxVal + 1)
                val b = random.nextInt(minVal, a)
                Pair("$a - $b", a - b)
            }
        }
    }

    fun generateEquationForTarget(target: Int, settings: GameSettings): Pair<String, Int> {
        val op = getRandomOperation(settings)
        val minVal = settings.minNumberRange.coerceAtLeast(0).coerceAtLeast(1)

        return when (op) {
            MathOperation.ADD -> {
                if (target <= minVal) {
                    Pair("$target + 0", target)
                } else {
                    val a = random.nextInt(minVal, target)
                    val b = target - a
                    Pair("$a + $b", target)
                }
            }
            MathOperation.SUBTRACT -> {
                val extra = random.nextInt(1, 10)
                Pair("${target + extra} - $extra", target)
            }
        }
    }
}