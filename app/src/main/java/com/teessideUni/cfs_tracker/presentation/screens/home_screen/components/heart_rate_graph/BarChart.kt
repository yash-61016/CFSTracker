package com.teessideUni.cfs_tracker.presentation.screens.home_screen.components.heart_rate_graph


import android.graphics.Paint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teessideUni.cfs_tracker.data.local.HeartRatePoint
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun BarChart(
    data: List<List<HeartRatePoint>>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onBarClick: ((HeartRatePoint) -> Unit)? = null,
) {
    val spacing = 100f
    val graphColor = Color.Red
    val upperValue = remember { 100 }
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
    val sweepAngle by animateFloatAsState(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        )
    )
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
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                // Loading animation
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = min(size.width, size.height) / 4f
                val startAngle = 0f

                val intervals = 16
                val intervalLength = sweepAngle / intervals
                val dotSize = 4.dp.toPx()

                for (i in 0 until intervals) {
                    val rotationAngle = (System.currentTimeMillis() / 10L + i * intervalLength) % 360f

                    val start = startAngle + i * intervalLength + rotationAngle

                    val path = Path().apply {
                        arcTo(
                            rect = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius),
                            startAngleDegrees = start,
                            sweepAngleDegrees = intervalLength / 2,
                            forceMoveTo = i == 0
                        )
                        moveTo(
                            center.x + (radius - dotSize / 2) * cos(Math.toRadians((startAngle + (i + 1) * intervalLength / 2 + rotationAngle).toDouble())).toFloat(),
                            center.y + (radius - dotSize / 2) * sin(Math.toRadians((startAngle + (i + 1) * intervalLength / 2 + rotationAngle).toDouble())).toFloat()
                        )
                    }
                    drawPath(
                        path = path,
                        color = graphColor,
                        style = Stroke(width = 8.dp.toPx()),
                    )
                }

                val text = "Please wait..."
                drawContext.canvas.nativeCanvas.drawText(
                    text,
                    center.x,
                    center.y + textPaint.textSize / 2 - textPaint.descent(),
                    textPaint
                )

                return@Canvas
            }

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

            // Draw border
            drawRect(
                color = Color.Black,
                topLeft = Offset(spacing, 0f),
                size = Size(size.width - spacing, size.height - spacing),
                style = Stroke(width = 2.dp.toPx())
            )

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
            val verticalLineXStep = (size.width - 2 * spacing) / numDays // Adjusted the calculation
            (-1 until numVerticalLines).forEach { i ->
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

                    if (dayData.maxHeartRate == 0f.toDouble() && dayData.minHeartRate == 0f.toDouble()) {
                        val dotRadius = 4.dp.toPx()
                        val dotY = size.height - spacing - dotRadius
                        drawCircle(
                            color = graphColor,
                            center = Offset(xPos + xPosOffset, dotY),
                            radius = dotRadius
                        )

                        val maxLabel = dayData.maxHeartRate.roundToInt().toFloat().toString()
                        val minLabel = dayData.minHeartRate.roundToInt().toFloat().toString()
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
                        val maxLabel = dayData.maxHeartRate.roundToInt().toFloat().toString()
                        val minLabel = dayData.minHeartRate.roundToInt().toFloat().toString()

                        if (maxLabel == minLabel) {
                            val dotY = size.height - spacing - (dayData.maxHeartRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)
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
                            val yPosMax = size.height - spacing - (dayData.maxHeartRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)
                            val yPosMin = size.height - spacing - (dayData.minHeartRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)

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
                    val yPos = size.height - spacing - (dayData.maxHeartRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)
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







