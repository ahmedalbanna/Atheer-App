package com.atheer.feature.shorts

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.atheer.core.designsystem.DarkCard
import com.atheer.core.designsystem.GreyText
import com.atheer.core.designsystem.Red60
import com.atheer.core.model.Video
import com.atheer.core.ui.util.formatViewsCount

@Composable
fun ShortsScreen(
    viewModel: ShortsViewModel = hiltViewModel(),
) {
    val shorts by viewModel.shorts.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    var showComments by remember { mutableStateOf(false) }

    if (shorts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد مقاطع قصيرة متوفرة", color = Color.White)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { shorts.size })
    val currentShort = shorts[pagerState.currentPage]

    LaunchedEffect(currentShort.id) {
        viewModel.onPageChanged(currentShort)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ShortsPlayerPage(video = shorts[page], isActive = page == pagerState.currentPage)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))),
                ),
        )

        Text(
            text = "أثير Shorts",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
        )

        SideActions(
            video = currentShort,
            commentCount = comments.count { it.videoId == currentShort.id },
            onLike = viewModel::onLike,
            onShowComments = { showComments = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 40.dp),
        )

        ShortMetadata(
            video = currentShort,
            onSubscribe = viewModel::onSubscribe,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 80.dp, bottom = 40.dp),
        )

        if (showComments) {
            CommentsDrawer(
                comments = comments.filter { it.videoId == currentShort.id }.map { it.author to it.text },
                onDismiss = { showComments = false },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/**
 * One pager page with its own ExoPlayer instance. Only the active page plays;
 * players are released when the page leaves composition (fixes the old bug of
 * restarting the stream on every recomposition).
 */
@Composable
private fun ShortsPlayerPage(video: Video, isActive: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(video.id) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(MediaItem.fromUri(video.videoUrl))
            prepare()
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) exoPlayer.play() else exoPlayer.pause()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (isActive) exoPlayer.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                player = exoPlayer
            }
        },
        update = { view -> view.player = exoPlayer },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun SideActions(
    video: Video,
    commentCount: Int,
    onLike: (Video) -> Unit,
    onShowComments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { onLike(video) },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Like",
                    tint = if (video.isLiked) Red60 else Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                // Room is the single source of truth - no optimistic double count.
                text = video.likes.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = onShowComments,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Comment",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = commentCount.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        AsyncImage(
            model = video.channelAvatar,
            contentDescription = "Channel",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(2.dp),
        )
    }
}

@Composable
private fun ShortMetadata(
    video: Video,
    onSubscribe: (Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = video.channelAvatar,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
            )
            Text(
                text = video.channelName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )

            Button(
                onClick = { onSubscribe(video) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (video.isSubscribed) Color.Gray else Red60,
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(28.dp),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = if (video.isSubscribed) "مشترك" else "متابعة",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Text(
            text = video.title,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = "${formatViewsCount(video.views)} مشاهدة",
            color = GreyText,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CommentsDrawer(
    comments: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "التعليقات والمحادثة",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "إغلاق", tint = Color.White)
                }
            }

            LazyColumn {
                items(comments) { (author, text) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(author.take(1), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(author, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(text, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
