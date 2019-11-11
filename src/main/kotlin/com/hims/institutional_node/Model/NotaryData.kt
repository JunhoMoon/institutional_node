package com.hims.institutional_node.Model

import java.sql.Timestamp
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "NotaryData")
internal data class NotaryData(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "key_seq")
        @SequenceGenerator(sequenceName = "Notary_DATA_SEQ", allocationSize = 1, name = "key_seq")
        @Column(name = "notary_data_no", unique = true, nullable = false)
        var notary_data_no: Long?,
        @Column(name = "sha")
        var sha: String,
        @Column(name = "reg_date", nullable = false)
        var reg_date: Timestamp?
)