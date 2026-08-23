package com.atheer.core.ui.util

import java.util.Locale

/**
 * Arabic-friendly compact view counts: 1.2 مليون / 125 ألف.
 */
fun formatViewsCount(count: Long): String = when {
    count >= 1_000_000 -> String.format(Locale.US, "%.1f", count / 1_000_000.0)
        .removeSuffix(".0") + " مليون"

    count >= 1_000 -> "${count / 1_000} ألف"
    else -> count.toString()
}
