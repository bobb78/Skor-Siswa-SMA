package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TerminalLogItem
import com.example.data.model.TerminalLogType
import com.example.ui.theme.BlinkingUnderscoreCursor
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalDarkBg
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalGreenBright
import com.example.ui.theme.TerminalGreenDim
import com.example.ui.theme.crtScanlines

/**
 * Scrollable Terminal Console with Green-on-Black Monospace Output,
 * Live System Confirmation Stream, Blinking Cursor Animation,
 * and Text-based Interactive Command Input System.
 */
@Composable
fun TerminalConsole(
    logs: List<TerminalLogItem>,
    onExecuteCommand: (String) -> Unit,
    onClearLogs: () -> Unit,
    userRoleLabel: String = "GURU_BK",
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    var commandText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to latest terminal entry
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    val quickCommands = listOf("help", "list", "stats", "records", "ping", "whoami", "clear")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            .background(TerminalDarkBg)
            .border(
                width = 1.dp,
                color = TerminalGreenDim.copy(alpha = 0.4f),
                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
            )
            .testTag("terminal_console")
    ) {
        // --- Terminal Titlebar & Toggle ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalBlack)
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = TerminalGreenBright,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "EDUTERM // SYS:CONSOLE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = TerminalGreenBright,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(TerminalGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AUTH:$userRoleLabel",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = TerminalGreen
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isExpanded) {
                    Text(
                        text = "[CLR]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerminalGreenDim,
                        modifier = Modifier
                            .clickable { onClearLogs() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("btn_terminal_clear")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Minimize" else "Expand",
                    tint = TerminalGreenDim,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // --- Scrollable Terminal Output Area (Monospace Green-on-Black) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(TerminalDarkBg)
                        .crtScanlines(lineSpacing = 3.dp, scanlineColor = Color(0x22000000))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(logs, key = { it.id }) { item ->
                            val color = when (item.type) {
                                TerminalLogType.CONFIRMATION -> TerminalGreenBright
                                TerminalLogType.COMMAND_INPUT -> Color(0xFF7DD3FC)
                                TerminalLogType.COMMAND_OUTPUT -> TerminalGreen
                                TerminalLogType.WARNING -> Color(0xFFFBBF24)
                                TerminalLogType.ERROR -> Color(0xFFF87171)
                                TerminalLogType.SYSTEM -> TerminalGreenDim
                            }
                            Text(
                                text = item.text,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = color,
                                lineHeight = 15.sp,
                                fontWeight = if (item.type == TerminalLogType.CONFIRMATION || item.type == TerminalLogType.COMMAND_INPUT) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        }
                    }
                }

                // --- Quick Suggestion Command Chips ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBlack)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK>",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TerminalGreenDim,
                        fontWeight = FontWeight.Bold
                    )
                    quickCommands.forEach { cmd ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, TerminalGreenDim.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .background(Color(0xFF0D1812))
                                .clickable {
                                    commandText = cmd
                                    onExecuteCommand(cmd)
                                    commandText = ""
                                }
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = cmd,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TerminalGreenBright
                            )
                        }
                    }
                }

                // --- Text-based Command Input System with Blinking Underscore Cursor ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBlack)
                        .border(
                            width = 1.dp,
                            color = TerminalGreen.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Command Prompt Indicator
                    Text(
                        text = "SYS:${userRoleLabel}> ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TerminalGreenBright
                    )

                    // Text Field with Blinking Cursor
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = commandText,
                            onValueChange = { commandText = it },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerminalGreenBright
                            ),
                            cursorBrush = SolidColor(TerminalGreenBright),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (commandText.isNotBlank()) {
                                        val cmd = commandText
                                        commandText = ""
                                        onExecuteCommand(cmd)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("terminal_command_input")
                        )

                        // If empty, show prompt placeholder with animated blinking cursor
                        if (commandText.isEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "type command (e.g. list Budi)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TerminalGreenDim.copy(alpha = 0.5f)
                                )
                                BlinkingUnderscoreCursor(
                                    color = TerminalGreenBright,
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            // Show blinking underscore trailing the user text
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                BlinkingUnderscoreCursor(
                                    color = TerminalGreenBright,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (commandText.isNotEmpty()) {
                        IconButton(
                            onClick = { commandText = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Hapus",
                                tint = TerminalGreenDim,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Execute Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TerminalGreen.copy(alpha = 0.2f))
                            .border(1.dp, TerminalGreen, RoundedCornerShape(4.dp))
                            .clickable {
                                if (commandText.isNotBlank()) {
                                    val cmd = commandText
                                    commandText = ""
                                    onExecuteCommand(cmd)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("terminal_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "RUN",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TerminalGreenBright
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Eksekusi",
                                tint = TerminalGreenBright,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
