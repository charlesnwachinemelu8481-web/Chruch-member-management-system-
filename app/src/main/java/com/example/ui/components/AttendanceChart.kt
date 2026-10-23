package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceRecord
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyWine
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextMuted

@Composable
fun AttendanceMiniChart(
    records: List<AttendanceRecord>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) {
        Box(
            modifier = modifier.height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No attendance records yet", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        return
    }

    // Display chronological order (oldest to newest)
    val displayList = records.take(6).reversed()
    val maxCount = (displayList.maxOfOrNull { it.totalCount } ?: 100).coerceAtLeast(100)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
        ) {
            val width = size.width
            val height = size.height
            val barCount = displayList.size
            val barWidth = (width / barCount) * 0.45f
            val spacing = (width - (barCount * barWidth)) / (barCount + 1)

            // Draw subtle horizontal guidelines
            val lineY = height * 0.5f
            drawLine(
                color = Color.LightGray.copy(alpha = 0.4f),
                start = Offset(0f, lineY),
                end = Offset(width, lineY),
                strokeWidth = 1.dp.toPx()
            )

            // Draw bars and curve
            val points = mutableListOf<Offset>()

            displayList.forEachIndexed { index, record ->
                val x = spacing + index * (barWidth + spacing)
                val barHeight = (record.totalCount.toFloat() / maxCount.toFloat()) * (height * 0.85f)
                val y = height - barHeight

                points.add(Offset(x + barWidth / 2, y))

                // Rounded gradient bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(GoldAccent, BurgundyWine)
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }

            // Draw smooth trend line connecting tops
            if (points.size >= 2) {
                val path = Path()
                path.moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val cX1 = (prev.x + curr.x) / 2
                    path.cubicTo(cX1, prev.y, cX1, curr.y, curr.x, curr.y)
                }

                drawPath(
                    path = path,
                    color = GoldAccent,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                points.forEach { pt ->
                    drawCircle(color = BurgundyDeep, radius = 3.dp.toPx(), center = pt)
                    drawCircle(color = GoldAccent, radius = 1.8.dp.toPx(), center = pt)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Dates under bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            displayList.forEach { record ->
                val label = try {
                    val parts = record.date.split("-")
                    "${parts[1]}/${parts[2]}"
                } catch (_: Exception) {
                    record.date
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun AttendanceTrendFullChart(
    records: List<AttendanceRecord>,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No recorded attendance history yet.", color = TextMuted)
            }
        }
        return
    }

    val displayList = records.take(7).reversed()
    val maxCount = (displayList.maxOfOrNull { it.totalCount } ?: 100).coerceAtLeast(100)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attendance Trend (Weekly Services)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Past ${displayList.size} recorded services breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldAccent.copy(alpha = 0.15f),
                    contentColor = BurgundyDeep
                ) {
                    val peak = displayList.maxOfOrNull { it.totalCount } ?: 0
                    Text(
                        text = "Peak: $peak",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val width = size.width
                val height = size.height
                val barCount = displayList.size
                val barWidth = (width / barCount) * 0.48f
                val spacing = (width - (barCount * barWidth)) / (barCount + 1)

                // Grid lines (0%, 50%, 100%)
                for (ratio in listOf(0.1f, 0.5f, 0.9f)) {
                    val y = height * ratio
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.35f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                displayList.forEachIndexed { index, record ->
                    val x = spacing + index * (barWidth + spacing)
                    val adultH = (record.adultCount.toFloat() / maxCount.toFloat()) * (height * 0.85f)
                    val childH = (record.childCount.toFloat() / maxCount.toFloat()) * (height * 0.85f)
                    val guestH = (record.guestCount.toFloat() / maxCount.toFloat()) * (height * 0.85f)

                    val yAdult = height - adultH
                    val yChild = yAdult - childH
                    val yGuest = yChild - guestH

                    // Adults portion (Burgundy)
                    drawRoundRect(
                        color = BurgundyWine,
                        topLeft = Offset(x, yAdult),
                        size = Size(barWidth, adultH),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // Children portion (Gold)
                    drawRoundRect(
                        color = GoldAccent,
                        topLeft = Offset(x, yChild),
                        size = Size(barWidth, childH),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // Guests portion (Navy)
                    if (guestH > 0) {
                        drawRoundRect(
                            color = NavyPrimary,
                            topLeft = Offset(x, yGuest),
                            size = Size(barWidth, guestH),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                displayList.forEach { record ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val dateParts = record.date.split("-")
                        val shortDate = if (dateParts.size >= 3) "${dateParts[1]}/${dateParts[2]}" else record.date
                        Text(
                            text = shortDate,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${record.totalCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = BurgundyWine,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = BurgundyWine, label = "Adults")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = GoldAccent, label = "Children")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = NavyPrimary, label = "Guests")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}
