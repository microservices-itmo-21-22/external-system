package ru.itmo.tps.config

import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.*
import javax.annotation.PreDestroy

class ExecutorsFactory {
    companion object {
        val logger = KotlinLogging.logger {}

        @JvmStatic
        fun threadPoolExecutor(
            name: String,
            threadNumber: Int = Runtime.getRuntime().availableProcessors(),
            queueSize: Int = 5000
        ) = ThreadPoolExecutor(
            threadNumber,
            threadNumber,
            0L,
            TimeUnit.NANOSECONDS,
            LinkedBlockingQueue(queueSize),
            DefaultThreadFactory(name),
            LoggingRejectedExecutionHandler()
        )

        @JvmStatic
        fun scheduledPoolExecutor(
            name: String,
            threadNumber: Int = Runtime.getRuntime().availableProcessors()
        ) = ScheduledThreadPoolExecutor(
            threadNumber,
            DefaultThreadFactory(name),
            LoggingRejectedExecutionHandler()
        )
    }

    internal class DefaultThreadFactory(
        internal val prefix: String
    ) : ThreadFactory {
        private val threadFactory = Executors.defaultThreadFactory()

        override fun newThread(r: Runnable): Thread {
            val thread = threadFactory.newThread(r)
            thread.name = "$prefix-${thread.name}"
            return thread
        }
    }

    internal class LoggingRejectedExecutionHandler : RejectedExecutionHandler {
        override fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor?) {
            if (executor?.threadFactory !is DefaultThreadFactory) return

            val threadFactory = executor.threadFactory as DefaultThreadFactory

            logger.error {
                "Executor task rejected. Executor: ${threadFactory.prefix}, queueSize: ${executor.queue.size}"
            }
        }
    }

    class ExecutorBean(
        private val executor: ExecutorService,
        private val waitForTaskToComplete: Boolean = true,
        private val awaitDuration: Duration = Duration.ofSeconds(30)
    ) : ExecutorService by executor {

        @PreDestroy
        fun destroy() {
            if (waitForTaskToComplete) {
                executor.shutdown()
                executor.awaitTermination(awaitDuration.seconds, TimeUnit.SECONDS)
            }
            executor.shutdownNow()
        }
    }
}