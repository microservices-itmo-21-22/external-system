package ru.itmo.tps.entity

import ru.itmo.tps.dto.Transaction
import ru.itmo.tps.dto.management.Account
import ru.itmo.tps.dto.management.AccountLimits
import ru.itmo.tps.dto.management.Project
import ru.itmo.tps.entity.core.TransactionEntity
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
    accountLimits = this.accountLimits.toEntity(),
    transactionCost = this.transactionCost
)

fun AccountEntity.toDto(): Account = Account(
    id = this.id!!,
    name = this.name!!,
    answerMethod = this.answerMethod!!,
    callbackUrl = this.callbackUrl ?: "",
    clientSecret = this.clientSecret!!,
    projectId = this.project?.id!!,
    accountLimits = this.accountLimits!!.toDto(),
    transactionCost = this.transactionCost!!
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

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = this.id,
    submitTime = this.submitTime,
    completedTime = this.completedTime,
    status = this.status,
    account = AccountEntity(this.accountId),
    cost = this.cost
)

fun TransactionEntity.toDto(): Transaction = Transaction(
    id = this.id!!,
    submitTime = this.submitTime!!,
    completedTime = this.completedTime,
    status = this.status!!,
    accountId = this.account?.id!!,
    cost = this.cost
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
    requestsPerMinute = this.requestsPerMinute!!,
    parallelRequests = this.parallelRequests!!,
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
    requestsPerMinute = this.requestsPerMinute,
    parallelRequests = this.parallelRequests,
    enableServerErrors = this.enableServerErrors,
    serverErrorProbability = this.serverErrorProbability
)



