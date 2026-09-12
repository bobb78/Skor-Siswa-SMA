package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DisciplineLevel
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.SafeGreenBorder
import com.example.ui.theme.SeverePurple
import com.example.ui.theme.SeverePurpleBg
import com.example.ui.theme.Sp1Orange
import com.example.ui.theme.Sp1OrangeBg
import com.example.ui.theme.Sp1OrangeBorder
import com.example.ui.theme.Sp2Rose
import com.example.ui.theme.Sp2RoseBg
import com.example.ui.theme.Sp2RoseBorder
import com.example.ui.theme.Sp3Red
import com.example.ui.theme.Sp3RedBg
import com.example.ui.theme.Sp3RedBorder
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg

@Composable
fun ScoreBadge(
    score: Int,
    level: DisciplineLevel,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (bgColor, textColor, borderColor) = when (level) {
        DisciplineLevel.AMAN -> Triple(SafeGreenBg, SafeGreen, SafeGreenBorder)
        DisciplineLevel.PERINGATAN_RINGAN -> Triple(WarningAmberBg, WarningAmber, WarningAmber.copy(alpha = 0.5f))
        DisciplineLevel.SP_1 -> Triple(Sp1OrangeBg, Sp1Orange, Sp1OrangeBorder)
        DisciplineLevel.SP_2 -> Triple(Sp2RoseBg, Sp2Rose, Sp2RoseBorder)
        DisciplineLevel.SP_3 -> Triple(Sp3RedBg, Sp3Red, Sp3RedBorder)
        DisciplineLevel.SANGAT_BERAT -> Triple(SeverePurpleBg, SeverePurple, SeverePurple.copy(alpha = 0.6f))
    }

    val icon = when (level) {
        DisciplineLevel.AMAN -> Icons.Default.CheckCircle
        DisciplineLevel.PERINGATAN_RINGAN -> Icons.Default.PriorityHigh
        DisciplineLevel.SP_1 -> Icons.Default.Warning
        DisciplineLevel.SP_2 -> Icons.Default.Warning
        DisciplineLevel.SP_3, DisciplineLevel.SANGAT_BERAT -> Icons.Default.ErrorOutline
    }

    if (compact) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(99.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(99.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$score Poin",
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
                Text(
                    text = " • ${level.shortName}",
                    color = textColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$score",
                    color = textColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = " Poin",
                    color = textColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Text(
                    text = " | ${level.shortName}",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

