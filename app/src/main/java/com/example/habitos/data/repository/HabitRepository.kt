package com.example.habitos.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.habitos.data.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "habits_prefs")

class HabitRepository(private val context: Context) {

    private val habitsKey = stringPreferencesKey("habits_list")
    private val json = Json { ignoreUnknownKeys = true }

    val habits: Flow<List<Habit>> = context.dataStore.data.map { prefs ->
        val raw = prefs[habitsKey] ?: "[]"
        try { json.decodeFromString<List<Habit>>(raw) } catch (e: Exception) { emptyList() }
    }

    suspend fun saveHabit(habit: Habit) {
        context.dataStore.edit { prefs ->
            val list = getCurrentList(prefs).toMutableList()
            val idx = list.indexOfFirst { it.id == habit.id }
            if (idx >= 0) list[idx] = habit else list.add(habit)
            prefs[habitsKey] = json.encodeToString(list)
        }
    }

    suspend fun deleteHabit(habitId: String) {
        context.dataStore.edit { prefs ->
            val list = getCurrentList(prefs).filter { it.id != habitId }
            prefs[habitsKey] = json.encodeToString(list)
        }
    }

    suspend fun toggleCompletion(habitId: String, date: String) {
        context.dataStore.edit { prefs ->
            val list = getCurrentList(prefs).map { habit ->
                if (habit.id != habitId) return@map habit
                val dates = habit.completedDates.toMutableList()
                if (dates.contains(date)) dates.remove(date) else dates.add(date)
                habit.copy(completedDates = dates)
            }
            prefs[habitsKey] = json.encodeToString(list)
        }
    }

    private fun getCurrentList(prefs: Preferences): List<Habit> {
        val raw = prefs[habitsKey] ?: "[]"
        return try { json.decodeFromString<List<Habit>>(raw) } catch (e: Exception) { emptyList() }
    }
}
