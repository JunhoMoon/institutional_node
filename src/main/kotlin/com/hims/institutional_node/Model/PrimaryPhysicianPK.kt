package com.hims.institutional_node.Model

import java.io.Serializable
import javax.persistence.*

@Embeddable
internal data class PrimaryPhysicianPK(
        @Column(name = "node_kn", nullable = false)
        var node_kn: String?,
        @Column(name = "primaryPhysician", nullable = false)
        var primaryPhysician: String?
):Serializable