package com.stokia.aura.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stokia.aura.presentation.screens.auth.ForgotPasswordScreen
import com.stokia.aura.presentation.screens.auth.LoginScreen
import com.stokia.aura.presentation.screens.auth.RegisterScreen
import com.stokia.aura.presentation.screens.auth.SplashScreen

import com.stokia.aura.presentation.screens.auth.CreateProfileScreen

/**
 * Main navigation graph for the Aura application.
 * Defines all composable destinations and transition animations.
 */
@Composable
fun AuraNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Splash.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = spring()) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, spring())
        },
        exitTransition = {
            fadeOut(animationSpec = spring()) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, spring())
        },
        popEnterTransition = {
            fadeIn(animationSpec = spring()) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, spring())
        },
        popExitTransition = {
            fadeOut(animationSpec = spring()) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, spring())
        }
    ) {
        // --- Auth Flow ---
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }

        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        composable(Routes.CreateProfile.route) {
            CreateProfileScreen(navController)
        }

        // --- Main Flow ---
        composable(Routes.ChatList.route) {
            // TODO: ChatListScreen(navController)
        }

        composable(
            route = Routes.ChatRoom.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("recipientId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            val recipientId = backStackEntry.arguments?.getString("recipientId") ?: return@composable
            // TODO: ChatRoomScreen(navController, chatId, recipientId)
        }

        composable(
            route = Routes.UserProfile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // TODO: UserProfileScreen(navController, userId)
        }

        composable(Routes.Settings.route) {
            // TODO: SettingsScreen(navController)
        }

        composable(Routes.QrScanner.route) {
            // TODO: QrScannerScreen(navController)
        }

        composable(Routes.QrDisplay.route) {
            // TODO: QrDisplayScreen(navController)
        }
    }
}
