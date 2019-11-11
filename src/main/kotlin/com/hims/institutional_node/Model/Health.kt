package com.hims.institutional_node.Model

import java.sql.Timestamp
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "Health")
internal data class Health(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "key_seq")
        @SequenceGenerator(sequenceName = "HEALTH_DATA_SEQ", allocationSize = 1, name = "key_seq")
        @Column(name = "issuer_health_no", unique = true, nullable = false)
        var issuer_health_no: Long?,
        @Column(name = "subject_health_no")
        var subject_health_no: Long?,
        @Column(name = "patient_no", nullable = false)
        var patient_no: String?,
        @Column(name = "physician_id", nullable = false)
        var physician_id: String?,
        @Column(name = "reg_date", nullable = false)
        var reg_date: Timestamp?
)