package ru.itmo.tps.config

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import org.springframework.stereotype.Component

@Component
class TpsMetricsCollector(serviceName: String) : CommonMetricsCollector(serviceName) {
    constructor(): this(SERVICE_NAME)

    companion object {
        const val SERVICE_NAME = "tps"
    }
}