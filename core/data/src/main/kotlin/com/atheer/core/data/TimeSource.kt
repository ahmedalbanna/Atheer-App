package com.atheer.core.data

import kotlinx.coroutines.flow.Flow

interface TimeSource {
    fun now(): Long
}
