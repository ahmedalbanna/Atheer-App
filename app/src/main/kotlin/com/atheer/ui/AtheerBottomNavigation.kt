package com.atheer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import com.atheer.core.designsystem.DarkBg
import com.atheer.core.designsystem.GreyText
import com.atheer.core.designsystem.Red60
import com.atheer.navigation.TopLevelDestination

@Composable
fun AtheerBottomNavigation(
    currentDestination: NavDestination?,
    onTabSelected: (TopLevelDestination) -> Unit,
) {
    NavigationBar(
        containerColor = DarkBg,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .drawBehind {
                drawLine(
                    color = Color(0xFF272727),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .testTag("atheer_bottom_nav"),
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination?.let(destination::matches) == true

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(destination) },
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label,
                            modifier = if (destination == TopLevelDestination.CREATE) {
                                Modifier.size(38.dp)
                            } else {
                                Modifier.size(24.dp)
                            },
                        )
                    }
                },
                label = {
                    Text(
                        text = destination.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Red60,
                    selectedTextColor = Red60,
                    unselectedIconColor = GreyText,
                    unselectedTextColor = GreyText,
                    indicatorColor = Color(0x22FF2231),
                ),
            )
        }
    }
}
