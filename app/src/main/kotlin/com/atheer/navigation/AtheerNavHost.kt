package com.atheer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.atheer.feature.create.CreateScreen
import com.atheer.feature.home.HomeScreen
import com.atheer.feature.library.LibraryScreen
import com.atheer.feature.search.SearchScreen
import com.atheer.feature.shorts.ShortsScreen
import com.atheer.feature.subscriptions.SubscriptionsScreen
import com.atheer.feature.videodetail.VideoDetailScreen

@Composable
fun AtheerNavHost(
    navController: NavHostController,
    startDestination: Any = HomeRoute,
) {
    val onOpenVideo: (String) -> Unit = { videoId ->
        navController.navigate(VideoDetailRoute(videoId))
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable<HomeRoute> {
            HomeScreen(
                onVideoClick = onOpenVideo,
                onSearchClick = { navController.navigate(SearchRoute) },
            )
        }
        composable<ShortsRoute> {
            ShortsScreen()
        }
        composable<CreateRoute> {
            CreateScreen(
                onPublished = {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                },
            )
        }
        composable<SubscriptionsRoute> {
            SubscriptionsScreen(onVideoClick = onOpenVideo)
        }
        composable<LibraryRoute> {
            LibraryScreen(onVideoClick = onOpenVideo)
        }
        composable<SearchRoute> {
            SearchScreen(
                onBack = navController::popBackStack,
                onVideoClick = onOpenVideo,
            )
        }
        composable<VideoDetailRoute> {
            VideoDetailScreen(
                onBack = navController::popBackStack,
                onVideoClick = onOpenVideo,
            )
        }
    }
}
