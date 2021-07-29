package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import javax.persistence.*


@Entity
@Getter
@Setter
@Table(name = "project")
class ProjectEntity: BaseEntity() {
    private var name: String? = null

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST], mappedBy = "project")
    private var accounts: List<AccountEntity>? = null


    override fun toString(): String = "Project(id=$id, name=$name)"
}