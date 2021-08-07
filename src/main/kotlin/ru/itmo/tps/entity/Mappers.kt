package ru.itmo.tps.entity

import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.dto.management.Project
import ru.itmo.tps.entity.management.AccountEntity
import ru.itmo.tps.entity.management.AccountLimitsEntity
import ru.itmo.tps.entity.management.ProjectEntity

fun Account.toEntity(): AccountEntity = AccountEntity(
    id = this.id,
    name = this.name,
    answerMethod = this.answerMethod,
    clientSecret = this.clientSecret,
    callbackUrl = this.callbackUrl,
    project = ProjectEntity(this.projectId),
    accountLimits = AccountLimitsEntity(this.accountLimitsId)
)

fun AccountEntity.toDto(): Account = Account(
    id = this.id!!,
    name = this.name!!,
    answerMethod = this.answerMethod!!,
    callbackUrl = this.callbackUrl ?: "",
    clientSecret = this.clientSecret!!,
    projectId = this.project?.id!!,
    accountLimitsId = this.accountLimits?.id!!
)

fun ProjectEntity.toDto(): Project = Project(
    id = this.id!!,
    name = this.name!!,
    accounts = this.accounts.map { it.toDto() }.toMutableSet()
)

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = this.id,
    name = this.name,
    accounts = this.accounts.map { it.toEntity() }.toMutableSet()
)

fun AccountLimitsEntity.toDto(): AccountLimits = AccountLimits(
    id = this.id!!,
    acceptTransactions = this.acceptTransactions!!,
    enableResponseTimeVariation = this.enableResponseTimeVariation!!,
    responseTimeLowerBound = this.responseTimeLowerBound!!,
    responseTimeUpperBound = this.responseTimeUpperBound!!,
    enableFailures = this.enableFailures!!,
    failureProbability = this.failureProbability!!,
    enableRateLimits = this.enableRateLimits!!,
    requestsPerSecond = this.requestsPerSecond!!,
    requestsPerMinute = this.requestsPerMinute!!,
    requestsPerHour = this.requestsPerHour!!,
    requestsPerDay = this.requestsPerDay!!,
    enableServerErrors = this.enableServerErrors!!,
    serverErrorProbability = this.serverErrorProbability!!
)

fun AccountLimits.toEntity(): AccountLimitsEntity = AccountLimitsEntity(
    id = this.id,
    acceptTransactions = this.acceptTransactions,
    enableResponseTimeVariation = this.enableResponseTimeVariation,
    responseTimeLowerBound = this.responseTimeLowerBound,
    responseTimeUpperBound = this.responseTimeUpperBound,
    enableFailures = this.enableFailures,
    failureProbability = this.failureProbability,
    enableRateLimits = this.enableRateLimits,
    requestsPerSecond = this.requestsPerSecond,
    requestsPerMinute = this.requestsPerMinute,
    requestsPerHour = this.requestsPerHour,
    requestsPerDay = this.requestsPerDay,
    enableServerErrors = this.enableServerErrors,
    serverErrorProbability = this.serverErrorProbability
)



