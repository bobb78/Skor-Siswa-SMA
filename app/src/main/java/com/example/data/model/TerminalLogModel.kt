package com.example.data.model

enum class TerminalLogType {
    SYSTEM,
    CONFIRMATION,
    COMMAND_INPUT,
    COMMAND_OUTPUT,
    WARNING,
    ERROR
}

data class TerminalLogItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val text: String,
    val type: TerminalLogType = TerminalLogType.SYSTEM
)
