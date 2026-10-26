package com.example.habitos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitos.ui.auth.LoginScreen
import com.example.habitos.ui.auth.RegisterScreen
import com.example.habitos.ui.habits.HabitDetailScreen
import com.example.habitos.ui.habits.HabitFormScreen
import com.example.habitos.ui.habits.HabitListScreen
import com.example.habitos.viewmodel.AuthViewModel
import com.example.habitos.viewmodel.HabitViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object HabitList : Screen("habit_list")
    object HabitForm : Screen("habit_form?habitId={habitId}") {
        fun createRoute(habitId: String? = null) =
            if (habitId != null) "habit_form?habitId=$habitId" else "habit_form"
    }
    object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: String) = "habit_detail/$habitId"
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    habitViewModel: HabitViewModel
) {
    val navController = rememberNavController()
    val start = if (authViewModel.isLoggedIn) Screen.HabitList.route else Screen.Login.route

    NavHost(navController = navController, startDestination = start) {

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.HabitList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.HabitList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.HabitList.route) {
            HabitListScreen(
                habitViewModel = habitViewModel,
                onNavigateToCreate = { navController.navigate(Screen.HabitForm.createRoute()) },
                onNavigateToDetail = { id -> navController.navigate(Screen.HabitDetail.createRoute(id)) },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.HabitList.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.HabitForm.route,
            arguments = listOf(navArgument("habitId") {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            })
        ) { back ->
            val habitId = back.arguments?.getString("habitId")
            HabitFormScreen(
                habitViewModel = habitViewModel,
                habitId = habitId,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { back ->
            val habitId = back.arguments?.getString("habitId") ?: return@composable
            HabitDetailScreen(
                habitViewModel = habitViewModel,
                habitId = habitId,
                onEdit = { navController.navigate(Screen.HabitForm.createRoute(habitId)) },
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.navigate(Screen.HabitList.route) {
                        popUpTo(Screen.HabitDetail.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
