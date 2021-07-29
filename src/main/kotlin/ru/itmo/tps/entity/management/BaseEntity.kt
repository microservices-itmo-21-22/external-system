package ru.itmo.tps.entity.management

import lombok.Getter
import lombok.Setter
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@Getter
@Setter
@MappedSuperclass
open class BaseEntity {
    @Id
    @GeneratedValue
    protected var id: UUID? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BaseEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

    override fun toString(): String = "BaseEntity(id=$id)"
}