package com.atheer.core.data

import com.atheer.core.common.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Bootstraps demo content into an empty database exactly once per install.
 * Called from [com.atheer.AtheerApplication.onCreate].
 */
class DemoContentSeeder @Inject constructor(
    private val repository: OfflineFirstVideoRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
) {

    fun seedOnStartup() {
        externalScope.launch {
            runCatching { repository.seedIfEmpty() }
        }
    }
}
