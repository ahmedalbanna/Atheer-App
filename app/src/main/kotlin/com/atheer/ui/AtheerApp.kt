package com.atheer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.atheer.core.designsystem.DarkBg
import com.atheer.navigation.AtheerNavHost
import com.atheer.navigation.TopLevelDestination
import com.atheer.navigation.VideoDetailRoute

/**
 * Root scaffold: RTL Arabic shell + bottom navigation + type-safe NavHost.
 * System back (incl. predictive back) pops the NavHost back stack natively;
 * no custom history stack is maintained.
 */
@Composable
fun AtheerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val isDetailScreen = currentDestination?.hasRoute(VideoDetailRoute::class) == true

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = DarkBg,
            bottomBar = {
                if (!isDetailScreen) {
                    AtheerBottomNavigation(
                        currentDestination = currentDestination,
                        onTabSelected = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .background(DarkBg),
            ) {
                AtheerNavHost(navController = navController)
            }
        }
    }
}
