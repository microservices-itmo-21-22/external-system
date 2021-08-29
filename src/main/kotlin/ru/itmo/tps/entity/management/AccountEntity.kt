package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import ru.itmo.tps.entity.BaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Getter
@Setter
@Table(name = "account")
class AccountEntity(
    id: UUID?,

    var name: String?,

    @Enumerated(EnumType.STRING)
    var answerMethod: AnswerMethod?,

    var clientSecret: UUID?,

    var callbackUrl: String?,

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    var project: ProjectEntity?,

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "account_limits_id")
    var accountLimits: AccountLimitsEntity?
) : BaseEntity(id) {
    constructor(id: UUID?) : this(id, null, null, null, null, null, null)

    override fun toString(): String = "Account(id=$id, name=$name, answerMethod=$answerMethod)"
}
