package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.CommentEntity
import com.example.data.VideoEntity
import com.example.ui.AppScreen
import com.example.ui.VideoViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtheerAppContent(viewModel: VideoViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()

    // Force RTL direction for consistent Arabic video engineering
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val rootScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            bottomBar = {
                AtheerBottomNavigation(
                    currentScreen = currentScreen,
                    onTabSelected = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .background(DarkBg)
            ) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(viewModel)
                    AppScreen.SHORTS -> ShortsScreen(viewModel)
                    AppScreen.CREATE -> CreateScreen(viewModel)
                    AppScreen.SUBSCRIPTIONS -> SubscriptionsScreen(viewModel)
                    AppScreen.LIBRARY -> LibraryScreen(viewModel)
                    AppScreen.SEARCH -> SearchScreen(viewModel)
                    AppScreen.VIDEO_DETAIL -> activeVideo?.let {
                        VideoDetailScreen(viewModel, it)
                    } ?: HomeScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun AtheerBottomNavigation(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = DarkBg,
        tonalElevation = 0.dp, // Flat clean design
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .drawBehind {
                drawLine(
                    color = Color(0xFF272727),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .testTag("atheer_bottom_nav")
    ) {
        val items = listOf(
            Triple(AppScreen.HOME, "الرئيسية", Icons.Default.Home),
            Triple(AppScreen.SHORTS, "Shorts", Icons.Default.PlayArrow),
            Triple(AppScreen.CREATE, "إنشاء", Icons.Default.AddCircle),
            Triple(AppScreen.SUBSCRIPTIONS, "الاشتراكات", Icons.Default.List),
            Triple(AppScreen.LIBRARY, "المكتبة", Icons.Default.AccountCircle)
        )

        items.forEach { (screen, label, icon) ->
            val isSelected = when (screen) {
                AppScreen.HOME -> currentScreen == AppScreen.HOME || currentScreen == AppScreen.SEARCH
                AppScreen.VIDEO_DETAIL -> false
                else -> currentScreen == screen
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(screen) },
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        if (screen == AppScreen.CREATE) {
                            Icon(
                                icon,
                                contentDescription = label,
                                tint = Red60,
                                modifier = Modifier.size(38.dp)
                            )
                        } else {
                            Icon(
                                icon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Red60,
                    selectedTextColor = Red60,
                    unselectedIconColor = GreyText,
                    unselectedTextColor = GreyText,
                    indicatorColor = Color(0x22FF2231)
                )
            )
        }
    }
}

@Composable
fun HomeScreen(viewModel: VideoViewModel) {
    val videos by viewModel.allVideos.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    
    val filteredVideos = remember(videos, selectedCategory) {
        if (selectedCategory == "الكل") {
            videos.filter { !it.isShort }
        } else {
            videos.filter { !it.isShort && it.category == selectedCategory }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // App bar top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Red60, AccentCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "أثير",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                    modifier = Modifier.testTag("search_icon")
                ) {
                    Icon(Icons.Default.Search, contentDescription = "بحث", tint = Color.White)
                }
                Spacer(modifier = Modifier.size(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkCard)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop",
                        contentDescription = "الملف الشخصي",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Horizontal Category Chips
        val categories = listOf("الكل", "ترفيه", "وثائقي", "تعليمي", "ألعاب")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(category) },
                    label = {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkCard,
                        selectedContainerColor = Color(0xFFF1F1F1),
                        labelColor = Color(0xFFF1F1F1),
                        selectedLabelColor = Color(0xFF0F0F0F)
                    ),
                    border = null,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Video Feed List
        if (filteredVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Empty",
                        tint = GreyText,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = "لا توجد فيديوهات متاحة حاليّاً في هذا القسم",
                        color = Color.LightGray,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredVideos) { video ->
                    VideoItemCard(
                        video = video,
                        onClick = {
                            viewModel.navigateTo(AppScreen.VIDEO_DETAIL, video)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VideoItemCard(
    video: VideoEntity,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_card_${video.id}")
    ) {
        Column {
            // Thumbnail container - Sleek rounded corner mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1F1F1F))
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Optional LIVE Badge or Duration Indicator
                if (video.liveStatus == "LIVE") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Red60)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                "مباشر",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = video.duration,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Title and Channel metadata below - Perfect alignment starting from 16.dp margin
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Channel Avatar circle
                AsyncImage(
                    model = video.channelAvatar,
                    contentDescription = video.channelName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                )

                // Metadata Column (Title, description, stats)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        lineHeight = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = buildString {
                            append(video.channelName)
                            append(" • ")
                            if (video.liveStatus == "LIVE") {
                                append(video.liveViewerCount)
                                append(" مشاهد حالياً")
                            } else {
                                append(formatViewsCount(video.views))
                                append(" مشاهدة")
                            }
                        },
                        color = GreyText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Interactive Fullscreen Shorts Vertical Loop Simulation
@Composable
fun ShortsScreen(viewModel: VideoViewModel) {
    val shortsList by viewModel.shorts.collectAsStateWithLifecycle()
    var currentIndex by remember { mutableStateOf(0) }
    var showCommentDrawer by remember { mutableStateOf(false) }

    if (shortsList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("لا توجد مقاطع قصيرة متوفرة", color = Color.White)
        }
    } else {
        val activeShort = shortsList[currentIndex]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Fullscreen Native Playback representation
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        setVideoURI(Uri.parse(activeShort.videoUrl))
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            start()
                        }
                    }
                },
                update = { videoView ->
                    videoView.setVideoURI(Uri.parse(activeShort.videoUrl))
                    videoView.start()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Bottom horizontal gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(250.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                        )
                    )
            )

            // Header for Shorts tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أثير Shorts",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Next / Prev custom Vertical gestures or button controls
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = {
                        if (currentIndex > 0) currentIndex--
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "السابق", tint = Color.White)
                }

                IconButton(
                    onClick = {
                        if (currentIndex < shortsList.size - 1) currentIndex++
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, "التالي", tint = Color.White)
                }
            }

            // Right overlay controls sidebar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Liked Action
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { viewModel.toggleLike(activeShort.id, activeShort.isLiked) },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Like",
                            tint = if (activeShort.isLiked) Red60 else Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = if (activeShort.isLiked) (activeShort.likes + 1).toString() else activeShort.likes.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Comment Action
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showCommentDrawer = true },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Comment",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "28",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share Action
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "مشاركة",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Rotating Disc or Channel Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Red60, CircleShape)
                ) {
                    AsyncImage(
                        model = activeShort.channelAvatar,
                        contentDescription = "Channel",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Bottom Metadata description overlaid on screen bottom-start
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 80.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = activeShort.channelAvatar,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = activeShort.channelName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Subscription Button
                    Button(
                        onClick = { viewModel.toggleSubscription(activeShort.id, activeShort.isSubscribed) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeShort.isSubscribed) Color.Gray else Red60
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (activeShort.isSubscribed) "مشترك" else "متابعة",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = activeShort.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = activeShort.description,
                    color = GreyText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Optional Interactive Comment Drawer overlay
            if (showCommentDrawer) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 0.dp),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التعليقات والمحادثة", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showCommentDrawer = false }) {
                                Icon(Icons.Default.Close, "إغلاق", tint = Color.White)
                            }
                        }

                        Divider(color = Color(0x11FFFFFF), modifier = Modifier.padding(vertical = 8.dp))

                        // Render some fast simple comments
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                ShortCommentItem("أحمد السعيد", "مقطع رائع جداً ومضحك للغاية! استمروا بقوة ❤️🚀")
                                ShortCommentItem("رهف منصور", "تجربة البث سريعة للغاية كأنها تيك توك!")
                                ShortCommentItem("عمرو عبد اللطيف", "اللقطة رهيبة وعجيبة 😂😂😂")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShortCommentItem(author: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(author.take(1), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Column {
            Text(author, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text, color = Color.White, fontSize = 12.sp)
        }
    }
}

// Creator Studio and Upload Portal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(viewModel: VideoViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isShort by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("ترفيه") }
    var videoUrl by remember { mutableStateOf("") }
    var thumbnailUrl by remember { mutableStateOf("") }
    var uploadSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "بوابة المبدعين - نشر فيديو جديد",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Segment Selector for Long Video vs Short
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard)
                .padding(4.dp)
        ) {
            Button(
                onClick = { isShort = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isShort) Red60 else Color.Transparent
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("فيديو طويل (يوتيوب)", color = Color.White, fontSize = 13.sp)
            }
            Button(
                onClick = { isShort = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isShort) Red60 else Color.Transparent
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("مقطع قصير (Shorts)", color = Color.White, fontSize = 13.sp)
            }
        }

        // Title and Description inputs
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("عنوان الفيديو", color = GreyText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Red60,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upload_title_field")
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("وصف محتوى الفيديو", color = GreyText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Red60,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard
            ),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Dropdown List selection for category
        Text("اختر التصنيف المتاح", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val categorios = listOf("ترفيه", "وثائقي", "تعليمي", "ألعاب")
            categorios.forEach { cat ->
                val active = cat == category
                Button(
                    onClick = { category = cat },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (active) AccentCyan else DarkCard
                    ),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(cat, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Custom video settings (URLs) with standard defaults shown to guide user
        OutlinedTextField(
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("رابط البث للفيديو (رابط MP4 خارجي متاح)", color = GreyText) },
            placeholder = { Text("اختياري - سيتم ملء رابط تلقائي ممتز", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Red60,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = thumbnailUrl,
            onValueChange = { thumbnailUrl = it },
            label = { Text("رابط الصورة المصغرة (Thumbnail URL)", color = GreyText) },
            placeholder = { Text("اكتب رابط صورة Unsplash للغلاف", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Red60,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Live streaming option toggle helper
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFECEF).copy(alpha = 0.05f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Red60)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text("خيار البث المباشر (Direct Live Feed)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("انقاذ تسجيل الكاميرا المباشر على خوادم أثير بلس", color = GreyText, fontSize = 10.sp)
                }
            }
            Switch(
                checked = false,
                onCheckedChange = {
                    Toast.makeText(context, "سيتم تخصيص كاميرا البث والبلوتوث في التحديث القادم!", Toast.LENGTH_SHORT).show()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Red60,
                    checkedTrackColor = Red60.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = DarkBg
                )
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        // Large high-impact Publish button
        Button(
            onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, "الرجاء كتابة عنوان مناسب أولاً لرفع الفيديو", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.uploadVideo(
                        title = title,
                        description = description,
                        category = category,
                        isShort = isShort,
                        videoUrl = videoUrl,
                        thumbnailUrl = thumbnailUrl
                    )
                    uploadSuccess = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Red60),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("publish_video_button"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("نشر ومشاركة الفيديو الآن 🎬", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // Animated Upload Success Dialog confirmation
        AnimatedVisibility(
            visible = uploadSuccess,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2D24)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                border = BorderStroke(1.dp, ActiveGreen)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, "نجاح", tint = ActiveGreen, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        "تهانينا! تم رفع ونشر مقطع الفيديو الخاص بك بنجاح على أثير.",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Button(
                        onClick = {
                            uploadSuccess = false
                            title = ""
                            description = ""
                            videoUrl = ""
                            thumbnailUrl = ""
                            viewModel.navigateTo(AppScreen.HOME)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActiveGreen)
                    ) {
                        Text("العودة إلى الصفحة الرئيسية للfeed", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Subscriptions Tab Screen layout
@Composable
fun SubscriptionsScreen(viewModel: VideoViewModel) {
    val subscribedVideos by viewModel.subscribedVideos.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "الاشتراكات الحية",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Channels avatars row with active indicators
        val mockChannels = listOf(
            Pair("عالم السينما المستقلة", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&auto=format&fit=crop"),
            Pair("معرفة بلا حدود - وثائقيات", "https://images.unsplash.com/photo-1628157582853-a796fa650a6a?w=100&auto=format&fit=crop"),
            Pair("واحة الاسترخاء", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&auto=format&fit=crop"),
            Pair("تيربو العرب", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=100&auto=format&fit=crop")
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mockChannels) { (channel, avatar) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(56.dp)) {
                        AsyncImage(
                            model = avatar,
                            contentDescription = channel,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(1.5.dp, ActiveGreen, CircleShape)
                        )
                        // Active green live online dot indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(ActiveGreen)
                                .align(Alignment.BottomEnd)
                                .border(2.dp, DarkBg, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = channel.take(8) + "..",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Divider(color = Color(0x11FFFFFF), modifier = Modifier.padding(bottom = 12.dp))

        // Videos from subscribed channel feed only
        if (subscribedVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "لم تشترك في قنوات حتى الآن أو لا توجد فيديوهات منشورة حالياً",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subscribedVideos) { video ->
                    VideoItemCard(
                        video = video,
                        onClick = {
                            viewModel.navigateTo(AppScreen.VIDEO_DETAIL, video)
                        }
                    )
                }
            }
        }
    }
}

// Search Window interface
@Composable
fun SearchScreen(viewModel: VideoViewModel) {
    val videos by viewModel.allVideos.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val filteredResults = remember(videos, searchQuery) {
        if (searchQuery.isBlank()) {
            videos.filter { !it.isShort }
        } else {
            videos.filter {
                !it.isShort && (it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true) ||
                        it.channelName.contains(searchQuery, ignoreCase = true))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { viewModel.handleBackPress() },
                modifier = Modifier.testTag("search_back_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("ابحث عن مقاطع كوتلن، أغاني، بث مباشر...", color = Color.Gray, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Red60,
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "بحث", tint = GreyText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("search_query_input"),
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Search Results
        if (filteredResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد مخرجات بحث تطابق الاستعلام الخاص بك", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredResults) { video ->
                    VideoItemCard(
                        video = video,
                        onClick = {
                            viewModel.navigateTo(AppScreen.VIDEO_DETAIL, video)
                        }
                    )
                }
            }
        }
    }
}

// Media library stats & Local downloads & history
@Composable
fun LibraryScreen(viewModel: VideoViewModel) {
    val downloadedVideos by viewModel.downloadedVideos.collectAsStateWithLifecycle()
    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "المكتبة والملفات الشخصية",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Profile details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCard)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop",
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column {
                Text("مطور أندرويد أثير بلس", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("ahmedalbanna966@gmail.com", color = GreyText, fontSize = 11.sp)
                Spacer(modifier = Modifier.size(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("قناتي الخاصة", fontSize = 11.sp, color = Red60) }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("لوحة التحكم", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Custom 3D Watchtime bar Chart analytics
        Text("وقت المشاهدة الأسبوعي (ساعات)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Draws custom charts elegantly using Row & Spacers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val days = listOf(Pair("سبت", 25), Pair("أحد", 45), Pair("إثن", 80), Pair("ثلاث", 55), Pair("أرب", 95), Pair("خم", 70), Pair("جمع", 30))
                    days.forEach { (day, percentage) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(percentage / 100f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(AccentCyan, Red60)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(day, color = GreyText, fontSize = 10.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "مجموع المشاهدات هذا الأسبوع: 14.5 ساعة (زيادة +20% عن الأسبوع الماضي)",
                    color = ActiveGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // History Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("سجل المشاهدة الأخير", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("عرض الكل", color = Red60, fontSize = 12.sp, modifier = Modifier.clickable { })
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allVideos.take(3)) { video ->
                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { viewModel.navigateTo(AppScreen.VIDEO_DETAIL, video) },
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp)
                        )
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text(
                                text = video.title,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = video.channelName,
                                color = GreyText,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Offline Downloads Section
        Text("تنزيلات بدون اتصال الإنترنت (${downloadedVideos.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        if (downloadedVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Downloads", tint = Color.Gray, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("لا توجد فيديوهات منزلة حالياً. قم بتحميل الفيديوهات من صفحة العرض.", color = Color.Gray, fontSize = 11.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                downloadedVideos.forEach { video ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .clickable { viewModel.navigateTo(AppScreen.VIDEO_DETAIL, video) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(120.dp)
                                .height(68.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(video.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(video.channelName, color = GreyText, fontSize = 11.sp)
                            Spacer(modifier = Modifier.size(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Downloaded", tint = ActiveGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("جاهز للمشاهدة بدون إنترنت", color = ActiveGreen, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Full video viewer detail player screen with interactive elements
@Composable
fun VideoDetailScreen(viewModel: VideoViewModel, initialVideo: VideoEntity) {
    // Collect active updates for this video
    val activeVideoFlow by viewModel.activeVideo.collectAsStateWithLifecycle()
    val video = activeVideoFlow ?: initialVideo
    val comments by viewModel.activeComments.collectAsStateWithLifecycle()
    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()

    var commentaryText by remember { mutableStateOf("") }
    var downloadingProgress by remember { mutableStateOf<Float?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val relatedVideos = remember(allVideos, video) {
        allVideos.filter { it.id != video.id && !it.isShort }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Master edge-to-edge Native VideoView Player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9.0f)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        val controller = MediaController(context)
                        controller.setAnchorView(this)
                        setMediaController(controller)
                        setVideoURI(Uri.parse(video.videoUrl))
                        setOnPreparedListener { mp ->
                            // Autoplay selected video
                            start()
                        }
                    }
                },
                update = { videoView ->
                    videoView.setVideoURI(Uri.parse(video.videoUrl))
                    videoView.start()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Back chevron floating button
            IconButton(
                onClick = { viewModel.handleBackPress() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("video_detail_back")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        // Title and description Metadata block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.size(8.dp))

            // Expandable metadata statistics description
            Text(
                text = buildString {
                    append(formatViewsCount(video.views))
                    append(" مشاهدة • قبل يومين • ")
                    append(video.category)
                },
                color = GreyText,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.size(16.dp))

            // Premium custom interactive Action bar (Likes, Desc, Sub, Downloads)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like Button
                ActionButtonItem(
                    icon = Icons.Filled.Favorite,
                    label = if (video.isLiked) "أعجبني" else "إعجاب",
                    color = if (video.isLiked) Red60 else Color.White,
                    onClick = { viewModel.toggleLike(video.id, video.isLiked) },
                    tag = "like_button"
                )

                // Download Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (downloadingProgress != null) {
                        CircularProgressIndicator(
                            progress = downloadingProgress!!,
                            color = Red60,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("يرجى الانتظار..", color = GreyText, fontSize = 11.sp)
                    } else {
                        ActionButtonItem(
                            icon = if (video.isDownloaded) Icons.Default.Check else Icons.Default.AddCircle,
                            label = if (video.isDownloaded) "منزَّل" else "تحميل",
                            color = if (video.isDownloaded) ActiveGreen else Color.White,
                            onClick = {
                                if (!video.isDownloaded) {
                                    scope.launch {
                                        downloadingProgress = 0.1f
                                        delay(500)
                                        downloadingProgress = 0.4f
                                        delay(600)
                                        downloadingProgress = 0.8f
                                        delay(400)
                                        downloadingProgress = null
                                        viewModel.toggleDownload(video.id, video.isDownloaded)
                                        Toast.makeText(context, "تم حفظ وتنزيل الفيديو لمشاهدته أوفلاين في المعرض!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    viewModel.toggleDownload(video.id, video.isDownloaded)
                                }
                            }
                        )
                    }
                }

                // Share Button
                ActionButtonItem(
                    icon = Icons.Default.Share,
                    label = "مشاركة",
                    onClick = {
                        Toast.makeText(context, "تم نسخ رابط المشاهدة المباشر!", Toast.LENGTH_SHORT).show()
                    }
                )

                // Subscribed status directly
                TextButton(
                    onClick = { viewModel.toggleSubscription(video.id, video.isSubscribed) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (video.isSubscribed) Color.Gray else Red60
                    )
                ) {
                    Icon(
                        if (video.isSubscribed) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Notification",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        if (video.isSubscribed) "مشترك" else "إشتراك",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))
            Divider(color = Color(0x11FFFFFF))
            Spacer(modifier = Modifier.size(12.dp))

            // Channel Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = video.channelAvatar,
                    contentDescription = video.channelName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(video.channelName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("134 ألف مشترك", color = GreyText, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.size(12.dp))
            // Expandable Description wrapper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCard)
                    .padding(12.dp)
            ) {
                Text(
                    text = video.description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            Divider(color = Color(0x11FFFFFF))
            Spacer(modifier = Modifier.size(16.dp))

            // Comments Box section
            Text("التعليقات والمجتمع (${comments.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(12.dp))

            // Input field to publish custom comments
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("أ", color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = commentaryText,
                    onValueChange = { commentaryText = it },
                    placeholder = { Text("أكتب تعليقاً محترماً...", color = Color.Gray, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Red60,
                        unfocusedBorderColor = Color(0x11FFFFFF),
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("comment_input_box"),
                    shape = RoundedCornerShape(24.dp)
                )

                IconButton(
                    onClick = {
                        if (commentaryText.isNotBlank()) {
                            viewModel.addComment(video.id, "أنا (المستخدم)", commentaryText)
                            commentaryText = ""
                            Toast.makeText(context, "تم نشر تعليقك الحقيقي بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("submit_comment_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "إرسال", tint = Red60)
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            // List of comments in chronological order
            if (comments.isEmpty()) {
                Text("لا توجد تعليقات بعد ، كن أول من يعلق!", color = Color.Gray, fontSize = 11.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    comments.forEach { comment ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B4050)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(comment.author.take(1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(comment.author, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("الآن", color = Color.Gray, fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.size(2.dp))
                                Text(comment.text, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))
            Divider(color = Color(0x11FFFFFF))
            Spacer(modifier = Modifier.size(20.dp))

            // Related/Suggested videos grid bottom section
            Text("موالية ومقترحة لك", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                relatedVideos.forEach { suggestedVideo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateTo(AppScreen.VIDEO_DETAIL, suggestedVideo)
                            },
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = suggestedVideo.thumbnailUrl,
                            contentDescription = suggestedVideo.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(130.dp)
                                .height(73.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                suggestedVideo.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(suggestedVideo.channelName, color = GreyText, fontSize = 11.sp)
                            Text(formatViewsCount(suggestedVideo.views) + " مشاهدة", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color = Color.White,
    onClick: () -> Unit,
    tag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(6.dp)
            .then(if (tag.isNotEmpty()) Modifier.testTag(tag) else Modifier)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.size(4.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

fun formatViewsCount(count: Int): String {
    return when {
         count >= 1000000 -> "${(count / 1000000.0).toString().take(3)} مليون"
         count >= 1000 -> "${count / 1000} ألف"
         else -> count.toString()
    }
}
