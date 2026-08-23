package com.atheer.core.database

import androidx.room.TypeConverter
import com.atheer.core.model.VideoCategory

class Converters {

    @TypeConverter
    fun fromCategory(category: VideoCategory): String = category.name

    @TypeConverter
    fun toCategory(name: String): VideoCategory =
        runCatching { VideoCategory.valueOf(name) }.getOrDefault(VideoCategory.ENTERTAINMENT)
}
