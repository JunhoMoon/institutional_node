package com.hims.institutional_node.Model

import java.io.Serializable
import java.sql.Timestamp
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "HealthNotary")
internal data class HealthNotary(
        @EmbeddedId
        var healthNotaryPK: HealthNotaryPK,
        @Column(name = "notary_data_no", nullable = false)
        var notary_data_no: Long?,
        @Column(name = "reg_date", nullable = false)
        var reg_date: Timestamp?
): Serializable