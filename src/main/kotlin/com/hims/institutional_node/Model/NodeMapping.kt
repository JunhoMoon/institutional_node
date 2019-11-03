package com.hims.institutional_node.Model

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "NodeMapping")
internal data class NodeMapping(
        @Id
        @Column(name = "node_kn", unique = true, nullable = false, length = 300)
        var node_kn: String?,
        @Column(name = "patient_no", unique = true, nullable = false, length = 200)
        var patient_no: String?,
        @Column(name = "acceptor", nullable = false)
        var acceptor: String?,
        @Column(name = "Description", length = 3000)
        var description: String?,
        @Column(name = "reg_date", nullable = false)
        var reg_date: Timestamp?,
        @Column(name = "accepted")
        var accepted: Timestamp?
)