package com.teessideUni.cfs_tracker.presentation.ui.home.components.respiratory_rate_graph

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teessideUni.cfs_tracker.data.model.RespiratoryRatePoint
import com.teessideUni.cfs_tracker.presentation.components.LoadingIndicator
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun RespiratoryRateDataGraph(
    data: List<List<RespiratoryRatePoint>>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val spacing = 100f
    val graphColor = Color.Red
    val upperValue = remember { 25 }
    val lowerValue = remember { 0 }
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
            isFakeBoldText = true
        }
    }

    // Animation values
    val lineAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1.0f else 0.3f,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (isLoading) {
            LoadingIndicator()
        }
        else {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Data points and lines
                if (data.isEmpty() || data.all { it.isEmpty() }) {
                    val text = "No data found."
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        size.width / 2,
                        size.height / 2,
                        textPaint
                    )
                    return@Canvas
                }

                val numDays = data.size
                val spacePerDay = (size.width - spacing) / numDays


                // Draw grid lines
                val gridLineColor = Color.LightGray
                val gridLineStrokeWidth = 1.dp.toPx()
                val numHorizontalLines = 5
                val numVerticalLines = numDays + 1

                // Draw horizontal grid lines
                val horizontalLineYStep = (size.height - 2 * spacing) / (numHorizontalLines - 1)
                (0 until numHorizontalLines).forEach { i ->
                    val y = size.height - spacing - i * horizontalLineYStep
                    drawLine(
                        color = gridLineColor,
                        start = Offset(spacing, y),
                        end = Offset(size.width + 100 - spacing, y), // Adjusted the end position
                        strokeWidth = gridLineStrokeWidth
                    )
                }

                // Draw vertical grid lines
                val verticalLineXStep =
                    (size.width - 2 * spacing) / numDays // Adjusted the calculation
                (0 until numVerticalLines).forEach { i ->
                    val x = spacing + i * verticalLineXStep
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, spacing - 100),
                        end = Offset(x, size.height - spacing),
                        strokeWidth = gridLineStrokeWidth
                    )
                }

                // Draw x-axis labels
                val xPosOffset = spacePerDay / 2
                data.forEachIndexed { index, dayDataList ->
                    if (dayDataList.isNotEmpty()) {
                        val dayData = dayDataList.first()
                        val xPos = spacing + index * spacePerDay
                        drawContext.canvas.nativeCanvas.drawText(
                            dayData.dayOfWeek,
                            xPos + xPosOffset,
                            size.height + 10f,
                            textPaint
                        )
                    }
                }

                // Draw y-axis labels
                val priceStep = (upperValue - lowerValue) / 5f
                val labelOffset = 50f
                (0..6).forEach { i ->
                    val yPos = size.height - spacing - i * (size.height - 2 * spacing) / 5
                    drawContext.canvas.nativeCanvas.drawText(
                        round(lowerValue + priceStep * i).toFloat().toString(),
                        spacing - labelOffset,
                        yPos - textPaint.descent(),
                        textPaint
                    )
                }

                // Draw data lines
                data.forEachIndexed { index, dayDataList ->
                    if (dayDataList.isNotEmpty()) {
                        val dayData = dayDataList.first()
                        val xPos = spacing + index * spacePerDay

                        if (dayData.maxRespiratoryRate == 0.0 && dayData.minRespiratoryRate == 0.0) {
                            val dotRadius = 4.dp.toPx()
                            val dotY = size.height - spacing - dotRadius
                            drawCircle(
                                color = graphColor,
                                center = Offset(xPos + xPosOffset, dotY),
                                radius = dotRadius
                            )

                            val maxLabel = dayData.maxRespiratoryRate.roundToInt().toFloat().toString()
                            val minLabel = dayData.minRespiratoryRate.roundToInt().toFloat().toString()
                            drawContext.canvas.nativeCanvas.drawText(
                                maxLabel,
                                xPos + xPosOffset,
                                (dotY + textPaint.descent() + 14.dp.toPx()).toFloat(),
                                textPaint
                            )
                            drawContext.canvas.nativeCanvas.drawText(
                                minLabel,
                                xPos + xPosOffset,
                                (dotY + textPaint.descent() + 14.dp.toPx()).toFloat(),
                                textPaint
                            )
                        } else {
                            val maxLabel = dayData.maxRespiratoryRate.roundToInt().toFloat().toString()
                            val minLabel = dayData.minRespiratoryRate.roundToInt().toFloat().toString()

                            if (maxLabel == minLabel) {
                                val dotY =
                                    size.height - spacing - (dayData.maxRespiratoryRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)
                                val dotRadius = 4.dp.toPx()
                                drawCircle(
                                    color = graphColor,
                                    center = Offset(xPos + xPosOffset, dotY.toFloat()),
                                    radius = dotRadius
                                )

                                drawContext.canvas.nativeCanvas.drawText(
                                    maxLabel,
                                    xPos + xPosOffset,
                                    (dotY - dotRadius - textPaint.descent()).toFloat(),
                                    textPaint
                                )
                            } else {
                                val yPosMax =
                                    size.height - spacing - (dayData.maxRespiratoryRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)
                                val yPosMin =
                                    size.height - spacing - (dayData.minRespiratoryRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)

                                val barWidth = spacePerDay * 0.6f
                                val barLeft = xPos + (spacePerDay - barWidth) / 2
                                drawRect(
                                    color = graphColor,
                                    topLeft = Offset(barLeft, yPosMax.toFloat()),
                                    size = Size(barWidth, (yPosMin - yPosMax).toFloat()),
                                    alpha = 0.5f
                                )

                                drawContext.canvas.nativeCanvas.drawText(
                                    maxLabel,
                                    xPos + xPosOffset,
                                    (yPosMax - 4.dp.toPx() - textPaint.descent()).toFloat(),
                                    textPaint
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    minLabel,
                                    xPos + xPosOffset,
                                    (yPosMin + textPaint.textSize - textPaint.descent() + 4.dp.toPx()).toFloat(),
                                    textPaint
                                )
                            }
                        }
                    }
                }

                // Connect points with animated lines
                val lineColor = Color.Blue
                val lineStrokeWidth = 2.dp.toPx()
                val linePath = Path()
                data.forEachIndexed { index, dayDataList ->
                    if (dayDataList.isNotEmpty()) {
                        val dayData = dayDataList.first()
                        val xPos = spacing + index * spacePerDay
                        val yPos =
                            size.height - spacing - (dayData.maxRespiratoryRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)
                        if (index == 0) {
                            linePath.moveTo(xPos + xPosOffset, yPos.toFloat())
                        } else {
                            linePath.lineTo(xPos + xPosOffset, yPos.toFloat())
                        }
                    }
                }
                drawPath(
                    path = linePath,
                    color = lineColor.copy(alpha = lineAlpha),
                    style = Stroke(width = lineStrokeWidth)
                )
            }
        }
    }
}