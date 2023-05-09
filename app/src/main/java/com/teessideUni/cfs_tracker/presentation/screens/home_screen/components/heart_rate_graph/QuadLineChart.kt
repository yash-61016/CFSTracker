package com.metehanbolat.linechartcompose

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.min
import kotlin.math.round

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuadLineChart(
    data: List<Pair<Double, Date>> = emptyList(),
    modifier: Modifier = Modifier,
    isLoading: Boolean = false, // Add the isLoading parameter with a default value
) {
    val spacing = 100f
    val graphColor = Color.Red
    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
    val upperValue = remember { 120 } // Set upperValue to 120
    val lowerValue = remember { 0 } // Set lowerValue to 0
    val density = LocalDensity.current

    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
            isFakeBoldText = true // Make the text bold
        }
    }
    val sweepAngle by animateFloatAsState(
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier.background(MaterialTheme.colors.background).padding(16.dp).wrapContentSize()
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            if(isLoading){
                // Draw circular loading indicator
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = min(size.width, size.height) / 4f
                val startAngle = -90f
                val useCenter = false
                val color = Color.Blue
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = useCenter,
                    topLeft = center - Offset(radius, radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 8.dp.toPx())
                )
                return@Canvas
            }
            if (data.isEmpty()) {
                drawContext.canvas.nativeCanvas.drawText(
                    "No data found",
                    size.width / 2,
                    size.height / 2,
                    textPaint
                )
                return@Canvas
            }
            val spacePerHour = (size.width - spacing) / data.size
            // Draw x-axis label
            drawContext.canvas.nativeCanvas.drawText(
                "Weekly Timestamp",
                size.width / 2,
                size.height - spacing / 2,
                textPaint
            )

            // Draw y-axis label
            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(-90f, spacing / 2, size.height / 2)
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "Heart rate",
                    spacing / 2,
                    size.height + 900f / 2 + (textPaint.descent() - textPaint.ascent()) / 2, // Adjusted position
                    textPaint
                )
            }
            drawContext.canvas.nativeCanvas.restore()

            (data.indices step 2).forEach { i ->
                val timestamp = data[i].second
                val formattedTimestamp = SimpleDateFormat("EEE d/M HH:mm").format(timestamp)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        formattedTimestamp,
                        spacing + i * spacePerHour,
                        size.height - textPaint.descent() + 20f, // Adjusted position
                        textPaint
                    )
                }
            }

            val priceStep = (upperValue - lowerValue) / 5f
            (0..4).forEach { i ->
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        round(lowerValue + priceStep * i).toString(),
                        50f,
                        size.height - spacing - i * size.height / 5f - textPaint.descent(), // Adjusted position
                        textPaint
                    )
                }
            }

            var medX: Float
            var medY: Float
            val strokePath = Path().apply {
                val height = size.height
                data.indices.forEach { i ->
                    val nextInfo = data.getOrNull(i + 1) ?: data.last()
                    val firstRatio = (data[i].first - lowerValue) / (upperValue - lowerValue)
                    val secondRatio = (nextInfo.first - lowerValue) / (upperValue - lowerValue)

                    val x1 = spacing + i * spacePerHour
                    val y1 = height - spacing - (firstRatio * height).toFloat()
                    val x2 = spacing + (i + 1) * spacePerHour
                    val y2 = height - spacing - (secondRatio * height).toFloat()
                    if (i == 0) {
                        moveTo(x1, y1)
                    } else {
                        medX = (x1 + x2) / 2f
                        medY = (y1 + y2) / 2f
                        quadraticBezierTo(x1 = x1, y1 = y1, x2 = medX, y2 = medY)
                    }
                }
            }

            drawPath(
                path = strokePath,
                color = Color.Red,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            val fillPath = android.graphics.Path(strokePath.asAndroidPath()).asComposePath().apply {
                lineTo(size.width - spacePerHour, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        transparentGraphColor,
                        Color.Transparent
                    ),
                    endY = size.height - spacing
                )
            )
        }
    }
}

