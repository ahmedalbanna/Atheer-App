package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VideoRepository(private val videoDao: VideoDao) {

    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val shorts: Flow<List<VideoEntity>> = videoDao.getShorts()
    val subscribedVideos: Flow<List<VideoEntity>> = videoDao.getSubscribedVideos()
    val downloadedVideos: Flow<List<VideoEntity>> = videoDao.getDownloadedVideos()

    fun observeVideoById(id: String): Flow<VideoEntity?> = videoDao.observeVideoById(id)

    fun observeComments(videoId: String): Flow<List<CommentEntity>> = videoDao.getCommentsForVideo(videoId)

    suspend fun insertVideo(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun toggleLike(id: String, isLiked: Boolean) {
        val offset = if (isLiked) 1 else -1
        videoDao.toggleLike(id, isLiked, offset)
    }

    suspend fun toggleSubscription(id: String, isSubscribed: Boolean) {
        val video = videoDao.getVideoById(id)
        if (video != null) {
            videoDao.updateSubscriptionByChannel(video.channelName, isSubscribed)
        } else {
            videoDao.toggleSubscription(id, isSubscribed)
        }
    }

    suspend fun toggleDownload(id: String, isDownloaded: Boolean) {
        videoDao.updateDownloadStatus(id, isDownloaded)
    }

    suspend fun incrementViews(id: String) {
        videoDao.incrementViews(id)
    }

    suspend fun addComment(videoId: String, author: String, text: String) {
        val comment = CommentEntity(videoId = videoId, author = author, text = text)
        videoDao.insertComment(comment)
    }

    suspend fun seedDatabaseIfEmpty() {
        val currentVideos = videoDao.getAllVideos().first()
        if (currentVideos.isEmpty()) {
            val defaultVideos = listOf(
                VideoEntity(
                    id = "vid_bunny",
                    title = "مغامرة الأرنب الكبير - رسوم متحركة مبهجة",
                    description = "استمتع بمشاهدة أحدث مغامرات الأرنب الودود في هذا الفيلم الكرتوني عالي الجودة والمليء باللقطات المضحكة والمؤثرة المناسبة لكافة أفراد العائلة.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=800&auto=format&fit=crop",
                    channelName = "قناة كرتون بلس",
                    channelAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&auto=format&fit=crop",
                    views = 125000,
                    likes = 4500,
                    duration = "9:56",
                    category = "ترفيه"
                ),
                VideoEntity(
                    id = "vid_sintel",
                    title = "رحلة البحث الكبرى (سينتيل) - خيال ملحمي وثائقي",
                    description = "فيلم خيالي قصير ملحمي يروي قصة فتاة شابة تبحث عن تنينها الصغير الضائع، يبرز جمالية الفن البصري وقوة الترابط العاطفي والتحمل من أجل الحب والصداقة.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800&auto=format&fit=crop",
                    channelName = "عالم السينما المستقلة",
                    channelAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&auto=format&fit=crop",
                    views = 89000,
                    likes = 3100,
                    duration = "5:20",
                    category = "ترفيه",
                    isSubscribed = true
                ),
                VideoEntity(
                    id = "vid_kotlin",
                    title = "دورة البرمجة بلغة كوتلن Kotlin للمبتدئين بلغة بسيطة",
                    description = "في هذا الفيديو التعليمي الشامل، نتعلم أساسيات لغة كوتلن Kotlin وبناء واجهات المستخدم الجميلة باستخدام أحدث تقنيات أندرويد وجيت باك كومبوز (Jetpack Compose).",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&auto=format&fit=crop",
                    channelName = "تعلم برمج بذكاء",
                    channelAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&auto=format&fit=crop",
                    views = 42000,
                    likes = 2900,
                    duration = "15:45",
                    category = "تعليمي"
                ),
                VideoEntity(
                    id = "vid_steel",
                    title = "تكنولوجيا المستقبل وأعاجيب الروبوتات الطبية",
                    description = "وثائقي يغوص في عالم التكنولوجيا الحيوية وصناعة الأطراف الذكية الطبية لإنقاذ حياة ملايين البشر وتعزيز جودة المعيشة في المستقبل القريب.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop",
                    channelName = "معرفة بلا حدود - وثائقيات",
                    channelAvatar = "https://images.unsplash.com/photo-1628157582853-a796fa650a6a?w=100&auto=format&fit=crop",
                    views = 204000,
                    likes = 9500,
                    duration = "12:14",
                    category = "وثائقي",
                    isSubscribed = true
                ),
                VideoEntity(
                    id = "vid_live1",
                    title = "بث مباشر: مؤتمر مطوري جوجل السنوي والجديد في الذكاء الاصطناعي",
                    description = "تابعوا معنا باللغة العربية البث المباشر الكامل والمترجم فوريًا لتغطية الإعلان الرسمي عن أحدث نماذج جيميناي (Gemini) وميزات أندرويد 17 الثورية.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&auto=format&fit=crop",
                    channelName = "شبكة أخبار التقنية",
                    channelAvatar = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=100&auto=format&fit=crop",
                    views = 3300,
                    likes = 850,
                    duration = "مباشر",
                    category = "وثائقي",
                    liveStatus = "LIVE",
                    liveViewerCount = 7800
                ),
                VideoEntity(
                    id = "short_fun1",
                    title = "عندما تبدأ في تجربة كود برمجي جديد لأول مرة! 💻🤣",
                    description = "مقطع فكاهي قصير يوضح حالة المبرمجين عند تشغيل الأكواد لأول مرة وحجم الأخطاء غير المتوقعة.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop",
                    channelName = "يوميات مبرمج مرح",
                    channelAvatar = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&auto=format&fit=crop",
                    views = 77000,
                    likes = 5400,
                    duration = "0:15",
                    category = "ألعاب",
                    isShort = true
                ),
                VideoEntity(
                    id = "short_nature",
                    title = "راحة نفسية خيالية! دقيقة استرخاء تام وسط الغابة 🌲⛰️",
                    description = "لوّن يومك بمشاهد الطبيعة الخلابة وهدوء الريف السويسري. مقطع مصور بدقة فائقة لتصفية الذهن.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1447752875215-b2761acb3c5d?w=800&auto=format&fit=crop",
                    channelName = "واحة الاسترخاء",
                    channelAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&auto=format&fit=crop",
                    views = 150000,
                    likes = 12000,
                    duration = "0:14",
                    category = "وثائقي",
                    isShort = true,
                    isSubscribed = true
                ),
                VideoEntity(
                    id = "short_car",
                    title = "انطلاق أسرع سيارة دفع رباعي على الطرق الوعرة! 🔥🚗",
                    description = "تحدي قوة محبين وعشاق سيارات الدفع الرباعي والمغامرة في الأودية الوعرة والمجاري المائية.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=800&auto=format&fit=crop",
                    channelName = "تيربو العرب",
                    channelAvatar = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=100&auto=format&fit=crop",
                    views = 310000,
                    likes = 24000,
                    duration = "0:11",
                    category = "ترفيه",
                    isShort = true
                )
            )
            videoDao.insertVideos(defaultVideos)
            
            // Seed initial comments
            videoDao.insertComment(CommentEntity(videoId = "vid_bunny", author = "أحمد خالد", text = "هذا الفيلم ممتع للغاية وتنزيله ممتاز لمشاهدته بلا إنترنت شكراً لكم!"))
            videoDao.insertComment(CommentEntity(videoId = "vid_bunny", author = "سارة علي", text = "جودة البث ممتازة والأرنب لطيف جداً 🐇❤️"))
            videoDao.insertComment(CommentEntity(videoId = "vid_kotlin", author = "يزن محمد", text = "أفضل شرح للغة كوتلن باللغة العربية، مبسط ورائع 👍"))
            videoDao.insertComment(CommentEntity(videoId = "vid_steel", author = "أماني حماد", text = "التكنولوجيا الطبية تتطور بسرعة خيالية. سبحان الله العلم لا يتوقف."))
            videoDao.insertComment(CommentEntity(videoId = "vid_live1", author = "ماجد الشريف", text = "مستعد لمشاهدة ميزات أندرويد الجديدة! تحديثات شيقة جداً."))
        }
    }
}
