package com.example.habitos.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitos.data.model.Habit
import com.example.habitos.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitViewModel: HabitViewModel,
    habitId: String,
    onEdit: () -> Unit,
    onBack: () -> Unit,
    onDeleted: () -> Unit
) {
    val habits by habitViewModel.habits.collectAsState()
    val habit = habits.find { it.id == habitId }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(habit) {
        if (habit == null && habits.isNotEmpty()) onDeleted()
    }

    if (habit == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hábito não encontrado")
        }
        return
    }

    val habitColor = parseHexColor(habit.colorHex)
    val today = habitViewModel.getTodayDate()
    val isCompletedToday = habit.completedDates.contains(today)
    val streak = habitViewModel.calculateStreak(habit.completedDates)
    val completionRate = habitViewModel.getCompletionRate(habit.completedDates, habit.createdAt)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir hábito") },
            text = { Text("Tem certeza que deseja excluir \"${habit.title}\"? Essa ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        habitViewModel.deleteHabit(habitId)
                        showDeleteDialog = false
                        onDeleted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = habitColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Colored header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(habitColor)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(habit.emoji, fontSize = 56.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        habit.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    if (habit.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            habit.description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            habit.frequency.label,
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Stats row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Sequência",
                        value = "$streak",
                        icon = if (streak > 0) "🔥" else "💤",
                        color = habitColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Total",
                        value = "${habit.completedDates.size}",
                        icon = "✅",
                        color = habitColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Taxa",
                        value = "$completionRate%",
                        icon = "📊",
                        color = habitColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Today action
                if (isCompletedToday) {
                    Button(
                        onClick = { habitViewModel.toggleCompletion(habitId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = habitColor)
                    ) {
                        Text("✅  Concluído hoje!", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = { habitViewModel.toggleCompletion(habitId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = habitColor),
                        border = androidx.compose.foundation.BorderStroke(2.dp, habitColor)
                    ) {
                        Text("Marcar como concluído hoje", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }

                // Last 14 days
                SectionTitle("Últimos 14 dias")
                Last14DaysRow(habit = habit, today = today, habitColor = habitColor)
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(
                label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Last14DaysRow(habit: Habit, today: String, habitColor: Color) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayLabels = listOf("D", "S", "T", "Q", "Q", "S", "S")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        (13 downTo 0).forEach { daysAgo ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val date = sdf.format(cal.time)
            val isCompleted = habit.completedDates.contains(date)
            val isToday = date == today
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    dayLabels[dayOfWeek % 7],
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> habitColor
                                isToday -> habitColor.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .then(
                            if (isToday && !isCompleted)
                                Modifier.border(1.5.dp, habitColor, CircleShape)
                            else Modifier
                        )
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
    )
}
