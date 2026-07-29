package com.example.thismathinvaders.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.thismathinvaders.R
import com.example.thismathinvaders.game.data.GameUiState

@Composable
fun GameCanvas(
    uiState: GameUiState,
    onSizeReady: (Float, Float) -> Unit,
    onShipMove: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val shipPainter = painterResource(id = R.drawable.spaceship_svgrepo)
    val meteorPainter = painterResource(id = R.drawable.shooting_star_svgrepo)

    val textMeasurer = rememberTextMeasurer()
    val equationTextStyle = remember {
        TextStyle(
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                onSizeReady(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onShipMove(change.position.x)
                }
            }
    ) {
        drawRect(color = Color.DarkGray)

        for (meteor in uiState.meteors) {
            val meteorDiameter = meteor.radius * 2f

            translate(
                left = meteor.x - meteor.radius,
                top = meteor.y - meteor.radius
            ) {
                with(meteorPainter) {
                    draw(size = Size(meteorDiameter, meteorDiameter))
                }
            }

            val textLayoutResult = textMeasurer.measure(
                text = meteor.equation,
                style = equationTextStyle
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = meteor.x - (textLayoutResult.size.width / 2f),
                    y = meteor.y - (textLayoutResult.size.height / 2f)
                )
            )
        }

        if (uiState.shipX >= 0) {
            val shipRadius = 70f
            val shipDiameter = shipRadius * 2f

            translate(
                left = uiState.shipX - shipRadius,
                top = uiState.shipY - shipRadius
            ) {
                with(shipPainter) {
                    draw(size = Size(shipDiameter, shipDiameter))
                }
            }
        }
    }
}
