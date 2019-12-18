package com.hims.institutional_node.Model

import java.io.Serializable
import java.sql.Timestamp
import javax.persistence.*
import kotlin.jvm.Transient

@Embeddable
internal data class HealthNotaryPK(
        @Column(name = "issuer_health_no", nullable = false)
        var issuer_health_no: Long?,
        @Column(name = "notary_kn", nullable = false)
        var notary_kn: String?
): Serializable