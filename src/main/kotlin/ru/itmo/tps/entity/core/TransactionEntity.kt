package ru.itmo.tps.entity.core

import lombok.Getter
import lombok.Setter
import ru.itmo.tps.dto.TransactionStatus
import ru.itmo.tps.entity.BaseEntity
import ru.itmo.tps.entity.management.AccountEntity
import java.util.*
import javax.persistence.*

@Entity
@Getter
@Setter
@Table(name = "transaction")
class TransactionEntity(
    id: UUID?,

    var submitTime: Long?,

    var completedTime: Long?,

    @Enumerated(EnumType.STRING)
    var status: TransactionStatus?,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    var account: AccountEntity?
) : BaseEntity(id)