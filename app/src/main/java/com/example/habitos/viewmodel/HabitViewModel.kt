package com.example.habitos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitos.data.model.Habit
import com.example.habitos.data.model.HabitFrequency
import com.example.habitos.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HabitRepository(application.applicationContext)

    val habits: StateFlow<List<Habit>> = repository.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedHabit = MutableStateFlow<Habit?>(null)
    val selectedHabit: StateFlow<Habit?> = _selectedHabit.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDate(): String = dateFormat.format(Date())

    fun getDateLabel(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return dateFormat.format(cal.time)
    }

    fun selectHabit(habitId: String) {
        _selectedHabit.value = habits.value.find { it.id == habitId }
    }

    fun refreshSelectedHabit(habitId: String) {
        _selectedHabit.value = habits.value.find { it.id == habitId }
    }

    fun clearSelectedHabit() {
        _selectedHabit.value = null
    }

    fun createHabit(
        title: String,
        description: String,
        frequency: HabitFrequency,
        colorHex: String,
        emoji: String
    ) {
        viewModelScope.launch {
            repository.saveHabit(
                Habit(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    description = description.trim(),
                    frequency = frequency,
                    colorHex = colorHex,
                    emoji = emoji
                )
            )
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.saveHabit(habit)
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    fun toggleCompletion(habitId: String) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, getTodayDate())
        }
    }

    fun calculateStreak(completedDates: List<String>): Int {
        if (completedDates.isEmpty()) return 0
        val sorted = completedDates.sortedDescending()
        val today = getTodayDate()
        var streak = 0
        val cal = Calendar.getInstance()

        for (i in sorted.indices) {
            val expected = dateFormat.format(cal.time)
            if (sorted[i] == expected || (i == 0 && sorted[0] == today)) {
                if (sorted[i] == dateFormat.format(cal.time)) {
                    streak++
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                } else break
            } else break
        }
        return streak
    }

    fun getCompletionRate(completedDates: List<String>, createdAt: Long): Int {
        if (completedDates.isEmpty()) return 0
        val daysSinceCreation = ((System.currentTimeMillis() - createdAt) / 86_400_000L).toInt()
            .coerceAtLeast(1)
        return ((completedDates.size.toFloat() / daysSinceCreation) * 100).toInt().coerceIn(0, 100)
    }
}
