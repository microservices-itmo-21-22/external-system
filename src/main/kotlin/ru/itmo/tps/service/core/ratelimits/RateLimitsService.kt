package ru.itmo.tps.service.core.ratelimits

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.exception.RateLimitExceededException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
@RequiredArgsConstructor
class RateLimitsService(private val rateLimitsWorkerDispatcher: CoroutineDispatcher) {
    private val rateLimiterMap = ConcurrentHashMap<UUID, RateLimiter>(256)
    private val ongoingWindowMap = ConcurrentHashMap<UUID, OngoingWindow>(256)

    fun evict(accountId: UUID) {
        rateLimiterMap.remove(accountId)?.cancelReleaseJob()
        ongoingWindowMap.remove(accountId)
    }

    fun acquire(account: Account) {
        if (!account.accountLimits.enableRateLimits) {
            return
        }

        val rateLimiter = getRateLimiterOrCreate(account)
        val ongoingWindow = getOngoingWindowOrCreate(account)

        if (ongoingWindow.putIntoWindow() is OngoingWindow.WindowResponse.Fail) {
            throw RateLimitExceededException("Too many parallel requests")
        }

        if (!rateLimiter.tick()) {
            ongoingWindow.releaseWindow()
            throw RateLimitExceededException("Too many requests")
        }
    }

    fun release(account: Account) {
        if (!account.accountLimits.enableRateLimits) {
            return
        }

        getOngoingWindowOrCreate(account).releaseWindow()
    }

    private fun getRateLimiterOrCreate(account: Account): RateLimiter =
        rateLimiterMap.getOrPut(account.id) {
            RateLimiter(CoroutineScope(rateLimitsWorkerDispatcher), account.accountLimits.requestsPerMinute.toInt())
        }

    private fun getOngoingWindowOrCreate(account: Account): OngoingWindow =
        ongoingWindowMap.getOrPut(account.id) { OngoingWindow(account.accountLimits.parallelRequests.toInt()) }
}