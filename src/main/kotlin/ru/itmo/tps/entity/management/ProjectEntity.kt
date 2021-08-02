package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import java.util.*
import javax.persistence.*


@Entity
@Getter
@Setter
@Table(name = "project")
class ProjectEntity(
    id: UUID?,

    var name: String?,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST], mappedBy = "project")
    var accounts: MutableSet<AccountEntity> = mutableSetOf()
): BaseEntity(id) {

    constructor(id: UUID?) : this(id, null, mutableSetOf())

    fun addAccount(account: AccountEntity) {
        accounts.add(account)
    }

    override fun toString(): String = "Project(id=$id, name=$name)"
}