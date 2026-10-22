package com.example.habitos.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    val id: String,
    val title: String,
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val colorHex: String = "#5E35B1",
    val emoji: String = "🎯",
    val createdAt: Long = System.currentTimeMillis(),
    val completedDates: List<String> = emptyList()
)

@Serializable
enum class HabitFrequency(val label: String) {
    DAILY("Diário"),
    WEEKLY("Semanal")
}
