package com.hims.institutional_node.Model

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "PrimaryPhysician")
internal data class PrimaryPhysician(
        @EmbeddedId
        var pk: PrimaryPhysicianPK,
        @Column(name = "acceptor")
        var acceptor: String?,
        @Column(name = "reg_date")
        var reg_date: Timestamp?,
        @Column(name = "accepted")
        var accepted: Timestamp?
)