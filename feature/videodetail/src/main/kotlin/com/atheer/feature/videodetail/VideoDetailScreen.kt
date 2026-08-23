package com.atheer.feature.videodetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.atheer.core.designsystem.DarkCard
import com.atheer.core.designsystem.GreyText
import com.atheer.core.designsystem.Red60
import com.atheer.core.model.Comment
import com.atheer.core.model.Video
import com.atheer.core.ui.component.ActionButtonItem
import com.atheer.core.ui.util.formatViewsCount

@Composable
fun VideoDetailScreen(
    onBack: () -> Unit,
    onVideoClick: (String) -> Unit,
    viewModel: VideoDetailViewModel = hiltViewModel(),
) {
    val video by viewModel.video.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val relatedVideos by viewModel.relatedVideos.collectAsStateWithLifecycle()

    val currentVideo = video ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        PlayerSection(videoUrl = currentVideo.videoUrl, onBack = onBack)

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = currentVideo.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "${formatViewsCount(currentVideo.views)} مشاهدة • ${currentVideo.category.label}",
                color = GreyText,
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.size(16.dp))

            ActionBar(
                video = currentVideo,
                onLike = viewModel::onLike,
                onSubscribe = viewModel::onSubscribe,
                onDownloadToggle = viewModel::onDownloadToggle,
            )

            Spacer(modifier = Modifier.size(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.size(12.dp))

            ChannelHeader(video = currentVideo)

            Spacer(modifier = Modifier.size(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard)
                    .padding(12.dp),
            ) {
                Text(
                    text = currentVideo.description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.size(16.dp))

            CommentsSection(
                comments = comments,
                onSubmit = viewModel::onCommentSubmit,
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text("موالية ومقترحة لك", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                relatedVideos.forEach { suggested ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVideoClick(suggested.id) },
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AsyncImage(
                            model = suggested.thumbnailUrl,
                            contentDescription = suggested.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 130.dp, height = 73.dp)
                                .clip(RoundedCornerShape(6.dp)),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                suggested.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(suggested.channelName, color = GreyText, fontSize = 11.sp)
                            Text(
                                formatViewsCount(suggested.views) + " مشاهدة",
                                color = Color.Gray,
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSection(videoUrl: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black),
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = true
                    player = exoPlayer
                }
            },
            update = { view -> view.player = exoPlayer },
            modifier = Modifier.fillMaxSize(),
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
        }
    }
}

@Composable
private fun ActionBar(
    video: Video,
    onLike: (Video) -> Unit,
    onSubscribe: (Video) -> Unit,
    onDownloadToggle: (Video, () -> Unit) -> Unit,
) {
    var downloadInProgress by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActionButtonItem(
            icon = Icons.Filled.Favorite,
            label = if (video.isLiked) "أعجبني" else "إعجاب",
            color = if (video.isLiked) Red60 else Color.White,
            onClick = { onLike(video) },
        )

        if (downloadInProgress) {
            CircularProgressIndicator(color = Red60, modifier = Modifier.size(24.dp))
        } else {
            ActionButtonItem(
                icon = if (video.isDownloaded) Icons.Default.Check else Icons.Default.AddCircle,
                label = if (video.isDownloaded) "منزَّل" else "تحميل",
                color = if (video.isDownloaded) Color(0xFF2BD97C) else Color.White,
                onClick = {
                    downloadInProgress = true
                    onDownloadToggle(video) { downloadInProgress = false }
                },
            )
        }

        ActionButtonItem(
            icon = Icons.Default.Share,
            label = "مشاركة",
            onClick = { },
        )

        TextButton(onClick = { onSubscribe(video) }) {
            Icon(
                if (video.isSubscribed) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = if (video.isSubscribed) Color.Gray else Red60,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = if (video.isSubscribed) "مشترك" else "إشتراك",
                color = if (video.isSubscribed) Color.Gray else Red60,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun ChannelHeader(video: Video) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = video.channelAvatar,
            contentDescription = video.channelName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
        )
        Column {
            Text(video.channelName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CommentsSection(
    comments: List<com.atheer.core.model.Comment>,
    onSubmit: (String) -> Unit,
) {
    var commentText by remember { mutableStateOf("") }

    Text(
        "التعليقات (${comments.size})",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.size(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("أكتب تعليقاً محترماً...", color = Color.Gray, fontSize = 12.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Red60,
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard,
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
        )

        IconButton(
            onClick = {
                onSubmit(commentText)
                commentText = ""
            },
            enabled = commentText.isNotBlank(),
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "إرسال",
                tint = if (commentText.isNotBlank()) Red60 else Color.Gray,
            )
        }
    }

    Spacer(modifier = Modifier.size(12.dp))

    if (comments.isEmpty()) {
        Text("لا توجد تعليقات بعد ، كن أول من يعلق!", color = Color.Gray, fontSize = 11.sp)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            comments.forEach { comment ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B4050)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(comment.author.take(1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(comment.author, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(comment.text, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalDivider() {
    androidx.compose.material3.HorizontalDivider(color = Color(0x11FFFFFF))
}
