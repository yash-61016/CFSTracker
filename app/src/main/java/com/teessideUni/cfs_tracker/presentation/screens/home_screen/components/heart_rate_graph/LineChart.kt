package com.teessideUni.cfs_tracker.presentation.screens.home_screen.components.heart_rate_graph


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
import com.teessideUni.cfs_tracker.data.local.HeartRatePoint
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarChart(
    data: List<List<HeartRatePoint>>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onBarClick: ((HeartRatePoint) -> Unit)? = null,
) {
    val spacing = 100f
    val graphColor = Color.Red
    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
    val upperValue = remember { 120 }
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
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .padding(16.dp)
            .wrapContentSize()
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
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
            if (data.isEmpty() || data.all { it.isEmpty() }) {
                drawContext.canvas.nativeCanvas.drawText(
                    "No data found.",
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

            // Draw x-axis label
            val xPosOffset = spacePerDay / 2 // Adjust the offset as needed
            val xLabelCenterPos = size.width / 2 // Calculate the center position
            drawContext.canvas.nativeCanvas.drawText(
                "Day of Week",
                xLabelCenterPos,
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
                    size.height + 900f / 2 + (textPaint.descent() - textPaint.ascent()) / 2,
                    textPaint
                )
            }
            drawContext.canvas.nativeCanvas.restore()

            // Draw x-axis labels
            data.forEachIndexed { index, dayDataList ->
                if (dayDataList.isNotEmpty()) {
                    val dayData = dayDataList.first()
                    val xPos = spacing + index * spacePerDay
                    drawContext.canvas.nativeCanvas.drawText(
                        dayData.dayOfWeek,
                        xPos + xPosOffset,
                        size.height - textPaint.descent() + 20f,
                        textPaint
                    )
                }
            }

            // Draw y-axis labels
            val priceStep = (upperValue - lowerValue) / 5f
            (0..4).forEach { i ->
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        round(lowerValue + priceStep * i).toFloat().toString(),
                        50f,
                        size.height - spacing - i * size.height / 5f - textPaint.descent(),
                        textPaint
                    )
                }
            }

            // Draw data bars
            data.forEachIndexed { index, dayDataList ->
                if (dayDataList.isNotEmpty()) {
                    val dayData = dayDataList.first()
                    val xPos = spacing + index * spacePerDay
                    val yPosMax =
                        size.height - spacing - (dayData.maxHeartRate - lowerValue) / (upperValue - lowerValue) * size.height
                    val yPosMin =
                        size.height - spacing - (dayData.minHeartRate - lowerValue) / (upperValue - lowerValue) * size.height

                    val barWidth = spacePerDay * 0.6f // Adjust the bar width as needed
                    val barLeft = xPos + (spacePerDay - barWidth) / 2
                    val barTop = yPosMax
                    val barBottom = yPosMin

                    // Draw the bar
                    drawRect(
                        color = graphColor,
                        topLeft = Offset(barLeft, barTop.toFloat()),
                        size = Size(barWidth, (barBottom - barTop).toFloat()),
                        alpha = 0.5f // Set the transparency for the bar
                    )

                    // Draw the max and min labels
                    val maxLabel = dayData.maxHeartRate.roundToInt().toFloat().toString()
                    val minLabel = dayData.minHeartRate.roundToInt().toFloat().toString()

                    if (maxLabel == minLabel) {
                        // Draw a dot below the value if max and min are the same
                        drawContext.canvas.nativeCanvas.drawText(
                            maxLabel,
                            xPos + xPosOffset,
                            (barTop - 4.dp.toPx()).toFloat(),
                            textPaint
                        )
                        drawCircle(
                            color = graphColor,
                            center = Offset(xPos + xPosOffset, (barBottom + textPaint.descent() + 14.dp.toPx()).toFloat()),
                            radius = 4.dp.toPx(),
                        )
                    } else {
                        drawContext.canvas.nativeCanvas.drawText(
                            maxLabel,
                            xPos + xPosOffset,
                            (barTop - 4.dp.toPx()).toFloat(),
                            textPaint
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            minLabel,
                            xPos + xPosOffset,
                            (barBottom + textPaint.descent() + 14.dp.toPx()).toFloat(),
                            textPaint
                        )
                    }
                }
            }
        }
    }
}


//@SuppressLint("SimpleDateFormat")
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun BarChart(
//    data: List<List<HeartRatePoint>>,
//    modifier: Modifier = Modifier,
//    isLoading: Boolean = false,
//    onBarClick: ((HeartRatePoint) -> Unit)? = null,
//) {
//    val spacing = 100f
//    val graphColor = Color.Red
//    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
//    val upperValue = remember { 120 }
//    val lowerValue = remember { 0 }
//    val density = LocalDensity.current
//
//    val textPaint = remember(density) {
//        Paint().apply {
//            color = android.graphics.Color.BLACK
//            textAlign = Paint.Align.CENTER
//            textSize = density.run { 12.sp.toPx() }
//            isFakeBoldText = true
//        }
//    }
//    val sweepAngle by animateFloatAsState(
//        targetValue = if (isLoading) 360f else 0f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1000),
//            repeatMode = RepeatMode.Restart
//        )
//    )
//
//    Box(
//        modifier = modifier
//            .background(MaterialTheme.colors.background)
//            .padding(16.dp)
//            .wrapContentSize()
//    ) {
//        Canvas(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            if (isLoading) {
//                // Draw circular loading indicator
//                val center = Offset(size.width / 2f, size.height / 2f)
//                val radius = min(size.width, size.height) / 4f
//                val startAngle = -90f
//                val useCenter = false
//                val color = Color.Blue
//                drawArc(
//                    color = color,
//                    startAngle = startAngle,
//                    sweepAngle = sweepAngle,
//                    useCenter = useCenter,
//                    topLeft = center - Offset(radius, radius),
//                    size = Size(radius * 2, radius * 2),
//                    style = Stroke(width = 8.dp.toPx())
//                )
//                return@Canvas
//            }
//            if (data.isEmpty() || data.all { it.isEmpty() }) {
//                drawContext.canvas.nativeCanvas.drawText(
//                    "No data found.",
//                    size.width / 2,
//                    size.height / 2,
//                    textPaint
//                )
//                return@Canvas
//            }
//            val numDays = data.size
//            val spacePerDay = (size.width - spacing) / numDays
//
//            // Draw x-axis label
//            val xPosOffset = spacePerDay / 2 // Adjust the offset as needed
//            val xLabelCenterPos = size.width / 2 // Calculate the center position
//            drawContext.canvas.nativeCanvas.drawText(
//                "Day of Week",
//                xLabelCenterPos,
//                size.height - spacing / 2,
//                textPaint
//            )
//
//            // Draw y-axis label
//            drawContext.canvas.nativeCanvas.save()
//            drawContext.canvas.nativeCanvas.rotate(-90f, spacing / 2, size.height / 2)
//            drawContext.canvas.nativeCanvas.apply {
//                drawText(
//                    "Heart rate",
//                    spacing / 2,
//                    size.height + 900f / 2 + (textPaint.descent() - textPaint.ascent()) / 2,
//                    textPaint
//                )
//            }
//            drawContext.canvas.nativeCanvas.restore()
//
//            // Draw x-axis labels
//            data.forEachIndexed { index, dayDataList ->
//                if (dayDataList.isNotEmpty()) {
//                    val dayData = dayDataList.first()
//                    val xPos = spacing + index * spacePerDay
//                    val yPosMax = size.height - spacing - (dayData.maxHeartRate - lowerValue) / (upperValue - lowerValue) * size.height
//                    val yPosMin = size.height - spacing - (dayData.minHeartRate - lowerValue) / (upperValue - lowerValue) * size.height
//
//                    // Draw x-axis label
//                    drawContext.canvas.nativeCanvas.drawText(
//                        dayData.dayOfWeek,
//                        xPos + xPosOffset,
//                        size.height - textPaint.descent() + 20f,
//                        textPaint
//                    )
//
//                    // Draw max and min points as dots
//                    drawCircle(
//                        color = Color.Green,
//                        radius = 4.dp.toPx(),
//                        center = Offset(xPos + xPosOffset, yPosMax.toFloat())
//                    )
//                    drawCircle(
//                        color = Color.Blue,
//                        radius = 4.dp.toPx(),
//                        center = Offset(xPos + xPosOffset, yPosMin.toFloat())
//                    )
//
//                    // Draw line connecting max and min points
//                    drawLine(
//                        color = Color.Black,
//                        start = Offset(xPos + xPosOffset, yPosMax.toFloat()),
//                        end = Offset(xPos + xPosOffset, yPosMin.toFloat()),
//                        strokeWidth = 2.dp.toPx()
//                    )
//                }
//            }
//
//            // Draw y-axis labels
//            val priceStep = (upperValue - lowerValue) / 5f
//            (0..4).forEach { i ->
//                drawContext.canvas.nativeCanvas.apply {
//                    drawText(
//                        round(lowerValue + priceStep * i).toString(),
//                        50f,
//                        size.height - spacing - i * size.height / 5f - textPaint.descent(),
//                        textPaint
//                    )
//                }
//            }
//
//            // Draw border
//            drawRect(
//                color = Color.Black,
//                topLeft = Offset(spacing, 0f),
//                size = Size(size.width - spacing, size.height - spacing),
//                style = Stroke(width = 2.dp.toPx())
//            )
//        }
//    }
//}
