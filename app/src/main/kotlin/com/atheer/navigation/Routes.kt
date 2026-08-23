package com.atheer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

// ---------------------------------------------------------------------------
// Type-safe navigation routes (Navigation Compose 2.9 + kotlinx.serialization)
// ---------------------------------------------------------------------------

@Serializable
data object HomeRoute

@Serializable
data object ShortsRoute

@Serializable
data object CreateRoute

@Serializable
data object SubscriptionsRoute

@Serializable
data object LibraryRoute

@Serializable
data object SearchRoute

@Serializable
data class VideoDetailRoute(val videoId: String)

// ---------------------------------------------------------------------------
// Bottom navigation destinations
// ---------------------------------------------------------------------------

enum class TopLevelDestination(
    val route: Any,
    val label: String,
    val icon: ImageVector,
) {
    HOME(HomeRoute, "الرئيسية", Icons.Default.Home) {
        override fun matches(destination: NavDestination): Boolean =
            destination.hasRoute(HomeRoute::class) || destination.hasRoute(SearchRoute::class)
    },
    SHORTS(ShortsRoute, "Shorts", Icons.Default.PlayArrow) {
        override fun matches(destination: NavDestination): Boolean =
            destination.hasRoute(ShortsRoute::class)
    },
    CREATE(CreateRoute, "إنشاء", Icons.Default.AddCircle) {
        override fun matches(destination: NavDestination): Boolean =
            destination.hasRoute(CreateRoute::class)
    },
    SUBSCRIPTIONS(SubscriptionsRoute, "الاشتراكات", Icons.AutoMirrored.Filled.List) {
        override fun matches(destination: NavDestination): Boolean =
            destination.hasRoute(SubscriptionsRoute::class)
    },
    LIBRARY(LibraryRoute, "المكتبة", Icons.Default.Person) {
        override fun matches(destination: NavDestination): Boolean =
            destination.hasRoute(LibraryRoute::class)
    };

    abstract fun matches(destination: NavDestination): Boolean
}
