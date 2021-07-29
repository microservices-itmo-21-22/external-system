package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import java.util.*
import javax.persistence.*

@Entity
@Getter
@Setter
@Table(name = "account")
class AccountEntity: BaseEntity() {
    private var name: String? = null

    @Enumerated(EnumType.STRING)
    private var answerMethod: AnswerMethod? = null

    private var clientSecret: UUID? = null

    private var callbackUrl: String? = null

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private var project: ProjectEntity? = null

    override fun toString(): String = "Account(id=$id, name=$name, answerMethod=$answerMethod)"
}
