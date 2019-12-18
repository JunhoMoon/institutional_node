package com.hims.institutional_node.Model

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "HealthView")
internal data class HealthView(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "key_seq")
        @SequenceGenerator(sequenceName = "HEALTH_VIEW_SEQ", allocationSize = 1, name = "key_seq")
        @Column(name = "health_view_no")
        var health_view_no: Long?,
        @Column(name = "acceptor")
        var acceptor: String?,
        @Column(name = "patient_no")
        var patient_no: String,
        @Column(name = "reg_date")
        var reg_date: Timestamp?,
        @Column(name = "accepted")
        var accepted: Timestamp?,
        @Column(name = "message")
        @Lob
        var message: String?
)