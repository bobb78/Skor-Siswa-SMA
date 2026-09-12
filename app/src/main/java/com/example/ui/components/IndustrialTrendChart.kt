package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.data.model.ViolationRecord
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.Sp1Orange
import com.example.ui.theme.Sp3Red
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrendPoint(
    val label: String,
    val points: Int,
    val dateMillis: Long
)

@Composable
fun IndustrialTrendChart(
    allRecords: List<ViolationRecord>,
    allStudents: List<Student>,
    modifier: Modifier = Modifier
) {
    var selectedClassFilter by remember { mutableStateOf("ALL") }
    var selectedTimeSpanDays by remember { mutableStateOf(30) }

    val classList = remember(allStudents) {
        listOf("ALL") + allStudents.map { it.className }.distinct().sorted()
    }

    val filteredRecords = remember(allRecords, selectedClassFilter, selectedTimeSpanDays) {
        val now = System.currentTimeMillis()
        val cutoff = now - (selectedTimeSpanDays.toLong() * 24 * 60 * 60 * 1000)
        allRecords.filter { record ->
            val timeMatch = record.timestamp >= cutoff
            val classMatch = if (selectedClassFilter == "ALL") true else record.studentClass == selectedClassFilter
            timeMatch && classMatch
        }.sortedBy { it.timestamp }
    }

    val trendData = remember(filteredRecords) {
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val grouped = filteredRecords.groupBy { dateFormat.format(Date(it.timestamp)) }
        if (grouped.isEmpty()) {
            listOf(
                TrendPoint("H-6", 0, 0L),
                TrendPoint("H-5", 5, 0L),
                TrendPoint("H-4", 10, 0L),
                TrendPoint("H-3", 15, 0L),
                TrendPoint("H-2", 12, 0L),
                TrendPoint("H-1", 20, 0L),
                TrendPoint("Hari Ini", 25, 0L)
            )
        } else {
            grouped.entries.map { entry ->
                TrendPoint(
                    label = entry.key,
                    points = entry.value.sumOf { it.points },
                    dateMillis = entry.value.first().timestamp
                )
            }.takeLast(8)
        }
    }

    val totalPoints = remember(filteredRecords) { filteredRecords.sumOf { it.points } }
    val maxSpike = remember(trendData) { trendData.maxOfOrNull { it.points } ?: 0 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("industrial_trend_chart_container"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueLight.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = PrimaryBlueLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Grafik Tren Pelanggaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Statistik akumulasi poin kedisiplinan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Timespan Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(7 to "7 Hari Terakhir", 30 to "30 Hari Terakhir", 90 to "1 Semester").forEach { (days, label) ->
                    val isSelected = selectedTimeSpanDays == days
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryBlueLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { selectedTimeSpanDays = days }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Class Filter Selector
            if (classList.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    classList.forEach { cName ->
                        val isSelected = selectedClassFilter == cName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PrimaryBlueLight else CardBorderLight,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedClassFilter = cName }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (cName == "ALL") "Semua Kelas" else "Kelas $cName",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryBlueLight else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3 Stat Highlights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Total Poin",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+$totalPoints",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (totalPoints > 50) Sp3Red else PrimaryBlueLight
                        )
                    }
                }

                // Card 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Jumlah Kasus",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${filteredRecords.size} Kasus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 3
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Puncak Harian",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$maxSpike Poin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                }
            }

            // Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val paddingBottom = 24f
                    val chartHeight = canvasHeight - paddingBottom
                    val maxVal = maxOf(trendData.maxOfOrNull { it.points } ?: 30, 40).toFloat()

                    // Grid lines
                    val hSteps = 3
                    for (i in 0..hSteps) {
                        val y = chartHeight * (i.toFloat() / hSteps)
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                    }

                    // Threshold Line at 50 pts (SP-1)
                    if (maxVal >= 50f) {
                        val sp1Y = chartHeight - (50f / maxVal * chartHeight)
                        drawLine(
                            color = Sp3Red.copy(alpha = 0.5f),
                            start = Offset(0f, sp1Y),
                            end = Offset(canvasWidth, sp1Y),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    // Curve path
                    if (trendData.size >= 2) {
                        val path = Path()
                        val areaPath = Path()

                        trendData.forEachIndexed { index, point ->
                            val x = canvasWidth * (index.toFloat() / (trendData.size - 1))
                            val y = chartHeight - (point.points.toFloat() / maxVal * chartHeight).coerceIn(0f, chartHeight)

                            if (index == 0) {
                                path.moveTo(x, y)
                                areaPath.moveTo(x, chartHeight)
                                areaPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                areaPath.lineTo(x, y)
                            }
                        }

                        areaPath.lineTo(canvasWidth, chartHeight)
                        areaPath.close()

                        // Gradient fill
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    PrimaryBlueLight.copy(alpha = 0.25f),
                                    PrimaryBlueLight.copy(alpha = 0.02f)
                                )
                            )
                        )

                        // Smooth line stroke
                        drawPath(
                            path = path,
                            color = PrimaryBlueLight,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw circular dots
                        trendData.forEachIndexed { index, point ->
                            val x = canvasWidth * (index.toFloat() / (trendData.size - 1))
                            val y = chartHeight - (point.points.toFloat() / maxVal * chartHeight).coerceIn(0f, chartHeight)
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = PrimaryBlueLight,
                                radius = 2.5.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }

            // X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trendData.forEach { pt ->
                    Text(
                        text = pt.label,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

