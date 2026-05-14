package com.stokia.aura.presentation.navigation

/**
 * Sealed class defining all navigation routes in Aura.
 * Each route is a unique destination in the app's navigation graph.
 */
sealed class Routes(val route: String) {

    // --- Auth Flow ---
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object ForgotPassword : Routes("forgot_password")
    data object CreateProfile : Routes("create_profile")

    // --- Main Flow ---
    data object ChatList : Routes("chat_list")
    data object ChatRoom : Routes("chat_room/{chatId}/{recipientId}") {
        fun createRoute(chatId: String, recipientId: String): String =
            "chat_room/$chatId/$recipientId"
    }
    data object UserProfile : Routes("user_profile/{userId}") {
        fun createRoute(userId: String): String = "user_profile/$userId"
    }
    data object Settings : Routes("settings")
    data object QrScanner : Routes("qr_scanner")
    data object QrDisplay : Routes("qr_display")
}
