package com.atheer.feature.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.atheer.core.designsystem.ActiveGreen
import com.atheer.core.designsystem.DarkBg
import com.atheer.core.model.Video
import com.atheer.core.ui.component.VideoCard

@Composable
fun SubscriptionsScreen(
    onVideoClick: (String) -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val subscribedVideos by viewModel.subscribedVideos.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "الاشتراكات الحية",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
        )

        ChannelRow(videos = subscribedVideos)

        HorizontalDivider(
            color = Color(0x11FFFFFF),
            modifier = Modifier.padding(bottom = 12.dp),
        )

        if (subscribedVideos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "لم تشترك في قنوات حتى الآن أو لا توجد فيديوهات منشورة حالياً",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(subscribedVideos, key = { it.id }) { video ->
                    VideoCard(video = video, onClick = { onVideoClick(video.id) })
                }
            }
        }
    }
}

@Composable
private fun ChannelRow(videos: List<Video>) {
    val channels = videos.distinctBy { it.channelName }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(channels, key = { it.channelName }) { channel ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(56.dp)) {
                    AsyncImage(
                        model = channel.channelAvatar,
                        contentDescription = channel.channelName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkBg)
                            .padding(2.dp),
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(ActiveGreen)
                            .align(Alignment.BottomEnd),
                    )
                }
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = channel.channelName.take(8) + "..",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
