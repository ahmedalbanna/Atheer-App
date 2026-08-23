package com.atheer.core.data

import javax.inject.Inject

class SystemTimeSource @Inject constructor() : TimeSource {
    override fun now(): Long = System.currentTimeMillis()
}
