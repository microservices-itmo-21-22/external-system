package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import javax.persistence.*


@Entity
@Getter
@Setter
@Table(name = "project")
class ProjectEntity(
    var name: String,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST], mappedBy = "project")
    var accounts: MutableSet<AccountEntity> = mutableSetOf()
): BaseEntity() {

    fun addAccount(account: AccountEntity) {
        accounts.add(account)
    }

    override fun toString(): String = "Project(id=$id, name=$name)"
}