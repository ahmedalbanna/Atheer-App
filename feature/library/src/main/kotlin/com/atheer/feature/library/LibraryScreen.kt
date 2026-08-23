package com.atheer.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.atheer.core.designsystem.AccentCyan
import com.atheer.core.designsystem.ActiveGreen
import com.atheer.core.designsystem.DarkCard
import com.atheer.core.designsystem.GreyText
import com.atheer.core.designsystem.Red60
import com.atheer.core.model.Video

@Composable
fun LibraryScreen(
    onVideoClick: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val downloadedVideos by viewModel.downloadedVideos.collectAsStateWithLifecycle()
    val recentVideos by viewModel.recentVideos.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "المكتبة والملفات الشخصية",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        ProfileCard()

        WatchTimeChart()

        RecentHistory(recentVideos = recentVideos.takeLast(3).reversed(), onVideoClick = onVideoClick)

        DownloadsSection(
            downloadedVideos = downloadedVideos,
            onVideoClick = onVideoClick,
        )
    }
}

@Composable
private fun ProfileCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop",
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(50)),
        )
        Column {
            Text("مطور أندرويد أثير بلس", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("ahmedalbanna966@gmail.com", color = GreyText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun WatchTimeChart() {
    Text("وقت المشاهدة الأسبوعي (ساعات)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    Card(colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = DarkCard)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom,
            ) {
                val days = listOf(
                    "سبت" to 0.25f, "أحد" to 0.45f, "إثن" to 0.80f, "ثلاث" to 0.55f,
                    "أرب" to 0.95f, "خم" to 0.70f, "جمع" to 0.30f,
                )
                days.forEach { (day, fraction) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.height(80.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(62.dp * fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Brush.verticalGradient(listOf(AccentCyan, Red60))),
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(day, color = GreyText, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentHistory(
    recentVideos: List<Video>,
    onVideoClick: (String) -> Unit,
) {
    Text("سجل المشاهدة الأخير", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

    if (recentVideos.isEmpty()) {
        Text("لا يوجد سجل مشاهدة بعد", color = Color.Gray, fontSize = 12.sp)
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recentVideos, key = { it.id }) { video ->
                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { onVideoClick(video.id) },
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = DarkCard),
                ) {
                    Column {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp),
                        )
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text(
                                text = video.title,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = video.channelName,
                                color = GreyText,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadsSection(
    downloadedVideos: List<Video>,
    onVideoClick: (String) -> Unit,
) {
    Text(
        "تنزيلات بدون اتصال الإنترنت (${downloadedVideos.size})",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )

    if (downloadedVideos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard),
            contentAlignment = Alignment.Center,
        ) {
            Text("لا توجد فيديوهات منزلة حالياً.", color = Color.Gray, fontSize = 11.sp)
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            downloadedVideos.forEach { video ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCard)
                        .clickable { onVideoClick(video.id) }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 120.dp, height = 68.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            video.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(video.channelName, color = GreyText, fontSize = 11.sp)
                        Spacer(modifier = Modifier.size(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = ActiveGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.size(4.dp))
                            Text("جاهز للمشاهدة بدون إنترنت", color = ActiveGreen, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
