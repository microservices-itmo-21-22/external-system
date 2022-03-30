package ru.itmo.tps.service.core.ratelimits

import java.util.concurrent.atomic.AtomicInteger

class OngoingWindow(
    private val maxWinSize: Int
) {
    private val winSize = AtomicInteger()

    fun putIntoWindow(): WindowResponse {
        while (true) {
            val currentWinSize = winSize.get()
            if (currentWinSize >= maxWinSize) {
                return WindowResponse.Fail(currentWinSize)
            }

            if (winSize.compareAndSet(currentWinSize, currentWinSize + 1)) {
                break
            }
        }

        return WindowResponse.Success(winSize.get())
    }

    fun releaseWindow() = winSize.decrementAndGet()

    sealed class WindowResponse(val currentWinSize: Int) {
        class Success(currentWinSize: Int): WindowResponse(currentWinSize)
        class Fail(currentWinSize: Int) : WindowResponse(currentWinSize)
    }
}