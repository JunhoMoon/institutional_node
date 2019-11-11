package com.hims.institutional_node.Model

import java.io.Serializable
import javax.persistence.*

@Embeddable
internal data class HealthDetailPK(
        @Column(name = "health_no", nullable = false)
        var health_no: Long?,
        @Column(name = "health_detail_no", nullable = false)
        var health_detail_no: Long?
): Serializable