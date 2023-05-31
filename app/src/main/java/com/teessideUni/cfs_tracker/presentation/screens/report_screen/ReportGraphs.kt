package com.teessideUni.cfs_tracker.presentation.screens.report_screen

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.data.local.AverageHeartRateData
import com.teessideUni.cfs_tracker.data.local.AverageRespiratoryRateData
import com.teessideUni.cfs_tracker.presentation.components.LoadingIndicator
import kotlin.math.roundToInt

@Composable
fun MonthlyHeartRateComparisonGraph(
    data: List<AverageHeartRateData>,
    modifier: Modifier = Modifier,
    isLoading: Boolean
)
{
    val graphColor = MaterialTheme.colorScheme.primary
    val upperValue = remember { data.maxOfOrNull { it.averageHeartRate } ?: 100.0 }
    val lowerValue = remember { data.minOfOrNull { it.averageHeartRate } ?: 0.0 }
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
            isFakeBoldText = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize().padding(start = 10.dp)
    ) {
        if (isLoading) {
            Log.d("indicator " , "$isLoading")
            LoadingIndicator()
        }
        else if (data.isEmpty()) {
            Image(
                painter = painterResource(R.drawable.no_data_found),
                contentDescription = "No data found",
                modifier = Modifier
                    .padding(start = 40.dp, end = 20.dp)
            )
        }
        else {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Data points and lines
                if (data.isEmpty()) {
                    val text = "No data found."
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        size.width / 2,
                        size.height / 2,
                        textPaint
                    )
                    return@Canvas
                }

                val numMonths = data.size
                val spacing = 100f
                val spacePerMonth =
                    (size.width - 2 * spacing) / numMonths // Adjusted the calculation


                // Draw grid lines
                val gridLineColor = Color.LightGray
                val gridLineStrokeWidth = 1.dp.toPx()
                val numHorizontalLines = 5
                val numVerticalLines = numMonths + 1

                // Draw horizontal grid lines
                val horizontalLineYStep = (size.height - 2 * spacing) / (numHorizontalLines - 1)
                (0 until numHorizontalLines).forEach { i ->
                    val y = spacing + i * horizontalLineYStep // Adjusted the y position
                    drawLine(
                        color = gridLineColor,
                        start = Offset(spacing, y),
                        end = Offset(size.width - spacing, y), // Adjusted the end position
                        strokeWidth = gridLineStrokeWidth
                    )
                }

                // Draw vertical grid lines
                val verticalLineXStep =
                    (size.width - 2 * spacing) / numMonths // Adjusted the calculation
                (0 until numVerticalLines).forEach { i ->
                    val x = spacing + i * verticalLineXStep
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, spacing),
                        end = Offset(x, size.height - spacing),
                        strokeWidth = gridLineStrokeWidth
                    )
                }

                // Draw x-axis labels
                val xPosOffset = spacePerMonth / 2
                data.forEachIndexed { index, monthData ->
                    val xPos = spacing + index * spacePerMonth
                    drawContext.canvas.nativeCanvas.drawText(
                        monthData.month,
                        xPos + xPosOffset,
                        size.height - spacing / 2,
                        textPaint
                    )
                }

                // Draw y-axis labels
                val labelOffset = 50f
                (0..5).forEach { i ->
                    val yPos = size.height - spacing - i * (size.height - 2 * spacing) / 5
                    val yValue = lowerValue + i * (upperValue - lowerValue) / 5
                    drawContext.canvas.nativeCanvas.drawText(
                        yValue.roundToInt().toString(),
                        spacing - labelOffset,
                        yPos - textPaint.descent(),
                        textPaint
                    )
                }

                // Draw bars
                val barWidth = spacePerMonth * 0.6f
                data.forEachIndexed { index, monthData ->
                    if (!monthData.averageHeartRate.isNaN()) {
                        val xPos = spacing + index * spacePerMonth
                        val yPos =
                            size.height - spacing - (monthData.averageHeartRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)

                        val barLeft = xPos + (spacePerMonth - barWidth) / 2
                        drawRect(
                            color = graphColor,
                            topLeft = Offset(barLeft, yPos.toFloat()),
                            size = Size(
                                barWidth,
                                (size.height - spacing - yPos).toFloat()
                            ), // Adjusted the size
                            alpha = 0.9f
                        )

                        val labelYOffset = 14.dp.toPx()
                        drawContext.canvas.nativeCanvas.drawText(
                            String.format("%.2f bpm", monthData.averageHeartRate),
                            xPos + xPosOffset,
                            (yPos - labelYOffset).toFloat(),
                            textPaint
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MonthlyRespiratoryRateComparisonGraph(
    data: List<AverageRespiratoryRateData>,
    modifier: Modifier = Modifier,
    isLoading: Boolean
)
{
    val graphColor = MaterialTheme.colorScheme.primary
    val upperValue = remember { data.maxOfOrNull { it.averageRespiratoryRate } ?: 25.0 }
    val lowerValue = remember { data.minOfOrNull { it.averageRespiratoryRate } ?: 0.0 }
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
            isFakeBoldText = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize().padding(start = 10.dp)
    ) {
        if (isLoading) {
            Log.d("indicator " , "$isLoading")
            LoadingIndicator()
        }
        else if (data.isEmpty()) {
            Image(
                painter = painterResource(R.drawable.no_data_found),
                contentDescription = "No data found",
                modifier = Modifier
                    .padding(start = 40.dp, end = 20.dp)
            )
        }
        else {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Data points and lines
                if (data.isEmpty()) {
                    val text = "No data found."
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        size.width / 2,
                        size.height / 2,
                        textPaint
                    )
                    return@Canvas
                }

                val numMonths = data.size
                val spacing = 100f
                val spacePerMonth =
                    (size.width - 2 * spacing) / numMonths // Adjusted the calculation


                // Draw grid lines
                val gridLineColor = Color.LightGray
                val gridLineStrokeWidth = 1.dp.toPx()
                val numHorizontalLines = 5
                val numVerticalLines = numMonths + 1

                // Draw horizontal grid lines
                val horizontalLineYStep = (size.height - 2 * spacing) / (numHorizontalLines - 1)
                (0 until numHorizontalLines).forEach { i ->
                    val y = spacing + i * horizontalLineYStep // Adjusted the y position
                    drawLine(
                        color = gridLineColor,
                        start = Offset(spacing, y),
                        end = Offset(size.width - spacing, y), // Adjusted the end position
                        strokeWidth = gridLineStrokeWidth
                    )
                }

                // Draw vertical grid lines
                val verticalLineXStep =
                    (size.width - 2 * spacing) / numMonths // Adjusted the calculation
                (0 until numVerticalLines).forEach { i ->
                    val x = spacing + i * verticalLineXStep
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, spacing),
                        end = Offset(x, size.height - spacing),
                        strokeWidth = gridLineStrokeWidth
                    )
                }

                // Draw x-axis labels
                val xPosOffset = spacePerMonth / 2
                data.forEachIndexed { index, monthData ->
                    val xPos = spacing + index * spacePerMonth
                    drawContext.canvas.nativeCanvas.drawText(
                        monthData.month,
                        xPos + xPosOffset,
                        size.height - spacing / 2,
                        textPaint
                    )
                }

                // Draw y-axis labels
                val labelOffset = 50f
                (0..5).forEach { i ->
                    val yPos = size.height - spacing - i * (size.height - 2 * spacing) / 5
                    val yValue = lowerValue + i * (upperValue - lowerValue) / 5
                    drawContext.canvas.nativeCanvas.drawText(
                        yValue.roundToInt().toString(),
                        spacing - labelOffset,
                        yPos - textPaint.descent(),
                        textPaint
                    )
                }

                // Draw bars
                val barWidth = spacePerMonth * 0.6f
                data.forEachIndexed { index, monthData ->
                    if (!monthData.averageRespiratoryRate.isNaN()) {
                        val xPos = spacing + index * spacePerMonth
                        val yPos =
                            size.height - spacing - (monthData.averageRespiratoryRate - lowerValue) / (upperValue - lowerValue) * (size.height - 2 * spacing)

                        val barLeft = xPos + (spacePerMonth - barWidth) / 2
                        drawRect(
                            color = graphColor,
                            topLeft = Offset(barLeft, yPos.toFloat()),
                            size = Size(
                                barWidth,
                                (size.height - spacing - yPos).toFloat()
                            ), // Adjusted the size
                            alpha = 0.9f
                        )

                        val labelYOffset = 14.dp.toPx()
                        drawContext.canvas.nativeCanvas.drawText(
                            String.format("%.2f", monthData.averageRespiratoryRate),
                            xPos + xPosOffset,
                            (yPos - labelYOffset).toFloat(),
                            textPaint
                        )
                    }
                }
            }
        }
    }
}

