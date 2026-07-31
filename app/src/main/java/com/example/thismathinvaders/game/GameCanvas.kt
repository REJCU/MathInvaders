package com.example.thismathinvaders.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.thismathinvaders.R
import com.example.thismathinvaders.game.data.GameUiState
import java.security.Provider

@Composable
fun GameCanvas(
    uiState: GameUiState, // state provider lambda to isolate recomposition
    onSizeReady: (Float, Float) -> Unit,
    onShipMove: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val shipPainter = painterResource(id = R.drawable.spaceship_svgrepo)
    val meteorPainter = painterResource(id = R.drawable.shooting_star_svgrepo)
    val projectilePainter = painterResource(id = R.drawable.rocket_ship_launch_missile_svgrepo)

    val textMeasurer = rememberTextMeasurer()
    val equationTextStyle = remember {
        TextStyle(
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }

    // cache text measure so never called while in loop
    val textLayoutCache = remember { mutableMapOf<String, TextLayoutResult>() }

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

            .drawWithCache {
                onDrawBehind {
                    drawRect(color = Color.DarkGray)

                    for (proj in uiState.projectiles) {
                        val projSize = proj.radius * 2.5f

                        translate(
                            left = proj.x - (projSize / 2f),
                            top = proj.y - (projSize / 2f)
                        ) {
                            with(projectilePainter) {
                                draw(size = Size(projSize, projSize))
                            }
                        }
                    }


                    for (meteor in uiState.meteors) {
                        val meteorDiameter = meteor.radius * 2f

                        // TODO - uncomment before final submission commit
                        // meteor hitbox
                        drawCircle(
                            color = Color.Yellow.copy(alpha = 0.6f),
                            radius = meteor.radius,
                            center = Offset(meteor.x, meteor.y),
                            style = Stroke(width = 3f)
                        )

                        translate(
                            left = meteor.x - meteor.radius,
                            top = meteor.y - meteor.radius
                        ) {
                            with(meteorPainter) {
                                draw(size = Size(meteorDiameter, meteorDiameter))
                            }
                        }

                        val textLayoutResult = textLayoutCache.getOrPut(meteor.equation) {
                            textMeasurer.measure(
                                text = meteor.equation,
                                style = equationTextStyle
                            )
                        }

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

                        // hitbox
                        drawCircle(
                            color = Color.Red.copy(0.5f),
                            radius = shipRadius,
                            center = Offset(uiState.shipX, uiState.shipY),
                            style = Stroke(width = 4f)
                        )
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
    ) {
    }
}