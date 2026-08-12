package com.example.thismathinvaders

import com.example.thismathinvaders.game.MathProblemGenerator
import com.example.thismathinvaders.game.data.GameSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MathProblemGeneratorTest {

    private val generator = MathProblemGenerator()
    private val iterations = 500

    @Test
    fun additionOnlySettingNeverProducesSubtractionProblem() {
        val settings = GameSettings(allowAddition = true, allowSubtraction = false)

        repeat(iterations) {
            val (equation, _) = generator.generateProblem(settings)
            assertTrue(
                "Expected only '+' problems but got: $equation",
                equation.contains("+")
            )
            assertFalse(
                "Expected no '-' problems but got: $equation",
                equation.contains("-")
            )
        }
    }

    @Test
    fun subtractionOnlySettingNeverProducesAdditionProblem() {
        val settings = GameSettings(allowAddition = false, allowSubtraction = true)

        repeat(iterations) {
            val (equation, _) = generator.generateProblem(settings)
            assertTrue(
                "Expected only '-' problems but got: $equation",
                equation.contains("-")
            )
            assertFalse(
                "Expected no '+' problems but got: $equation",
                equation.contains("+")
            )
        }
    }

    @Test
    fun bothOperationsEnabledEventuallyProducesBothAdditionAndSubtraction() {
        val settings = GameSettings(allowAddition = true, allowSubtraction = true)

        var sawAddition = false
        var sawSubtraction = false

        repeat(iterations) {
            val (equation, _) = generator.generateProblem(settings)
            if (equation.contains("+")) sawAddition = true
            if (equation.contains("-")) sawSubtraction = true
        }

        assertTrue("Expected to see at least one addition problem", sawAddition)
        assertTrue("Expected to see at least one subtraction problem", sawSubtraction)
    }

    @Test
    fun neitherOperationEnabledFallsBackToAdditionInsteadOfCrashing() {
        val settings = GameSettings(allowAddition = false, allowSubtraction = false)

        repeat(iterations) {
            val (equation, _) = generator.generateProblem(settings)
            assertTrue(
                "Expected fallback to '+' but got: $equation",
                equation.contains("+")
            )
        }
    }

    @Test
    fun additionProblemsAlwaysSumToTheReportedAnswer() {
        val settings = GameSettings(allowAddition = true, allowSubtraction = false)

        repeat(iterations) {
            val (equation, answer) = generator.generateProblem(settings)
            val (a, b) = equation.split("+").map { it.trim().toInt() }
            assertEquals(a + b, answer)
        }
    }

    @Test
    fun subtractionProblemsAlwaysSubtractToTheReportedAnswerAndAreNonNegative() {
        val settings = GameSettings(allowAddition = false, allowSubtraction = true)

        repeat(iterations) {
            val (equation, answer) = generator.generateProblem(settings)
            val (a, b) = equation.split("-").map { it.trim().toInt() }
            assertEquals(a - b, answer)
            assertTrue("Subtraction answer should never be negative: $equation", answer >= 0)
        }
    }

    @Test
    fun generatedOperandsRespectMinAndMaxRange() {
        val settings = GameSettings(
            allowAddition = true,
            allowSubtraction = true,
            minNumberRange = 10,
            maxNumberRange = 20
        )

        repeat(iterations) {
            val (equation, _) = generator.generateProblem(settings)
            val separator = if (equation.contains("+")) "+" else "-"
            val operands = equation.split(separator).map { it.trim().toInt() }

            operands.forEach { operand ->
                assertTrue(
                    "Operand $operand out of configured range in '$equation'",
                    operand in settings.minNumberRange..settings.maxNumberRange
                )
            }
        }
    }

    @Test
    fun generateEquationForTargetAlwaysProducesAnEquationEqualToTheTarget() {
        val settings = GameSettings(allowAddition = true, allowSubtraction = true)
        val targets = listOf(0, 1, 5, 20, 100)

        for (target in targets) {
            repeat(50) {
                val (equation, answer) = generator.generateEquationForTarget(target, settings)
                assertEquals(
                    "Equation '$equation' should evaluate to target $target",
                    target,
                    answer
                )
            }
        }
    }
}