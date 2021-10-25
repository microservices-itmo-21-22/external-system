package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Getter
@Setter
@Table(name = "account_limits")
class AccountLimitsEntity(
    @Id
    var id: UUID?,

    var acceptTransactions: Boolean?,

    var enableResponseTimeVariation: Boolean?,

    var responseTimeLowerBound: Long?,

    var responseTimeUpperBound: Long?,

    var enableFailures: Boolean?,

    var failureProbability: Double?,

    var enableRateLimits: Boolean?,

    var requestsPerMinute: Long?,

    var parallelRequests: Long?,

    var enableServerErrors: Boolean?,

    var serverErrorProbability: Double?
) {
    constructor(id: UUID?) : this(
        id,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}