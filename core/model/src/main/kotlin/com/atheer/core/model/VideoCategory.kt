package com.atheer.core.model

enum class VideoCategory(val label: String) {
    ALL("الكل"),
    ENTERTAINMENT("ترفيه"),
    DOCUMENTARY("وثائقي"),
    EDUCATION("تعليمي"),
    GAMING("ألعاب");

    companion object {
        fun fromLabel(label: String): VideoCategory =
            entries.firstOrNull { it.label == label } ?: ENTERTAINMENT
    }
}
