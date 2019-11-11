package com.hims.institutional_node.Model

import java.sql.Timestamp
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "HealthNotary")
internal data class HealthNotary(
        @Id
        @Column(name = "issuer_health_no", nullable = false)
        var issuer_health_no: Long?,
        @Id
        @Column(name = "notary_kn", nullable = false)
        var notary_kn: String?,
        @Column(name = "notary_data_no", nullable = false)
        var notary_data_no: Long?,
        @Column(name = "reg_date", nullable = false)
        var reg_date: Timestamp?
)