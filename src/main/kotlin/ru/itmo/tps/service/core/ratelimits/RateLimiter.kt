package ru.itmo.tps.service.core.ratelimits

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

class RateLimiter(
    private val rateLimiterScope: CoroutineScope,
    private val rate: Int,
    private val timeUnit: TimeUnit = TimeUnit.MINUTES
) {
    private val logger = KotlinLogging.logger {}

    private val semaphore = Semaphore(rate)

    private val releaseJob = rateLimiterScope.launch {
        while(true) {
            val permitsToRelease = rate - semaphore.availablePermits
            repeat(permitsToRelease) {
                kotlin.runCatching {
                    semaphore.release()
                }.onFailure { th -> logger.error("Failed while releasing permits", th) }
            }
            logger.info("Released $permitsToRelease permits")
            delay(timeUnit.toMillis(1)) // todo improve?
        }
    }.invokeOnCompletion { th -> if (th != null) logger.error("Rate limiter release job completed", th) }


    fun tick() = semaphore.tryAcquire()

    fun cancelReleaseJob() {
        releaseJob.dispose() // todo ?
    }
}