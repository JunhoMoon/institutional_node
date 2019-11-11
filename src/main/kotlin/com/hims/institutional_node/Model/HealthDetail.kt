package com.hims.institutional_node.Model

import javax.persistence.*

@Entity
@Table(name = "HealthDetail")
internal data class HealthDetail(
        @EmbeddedId
        var healthDetailPK: HealthDetailPK,
        @Column(name = "data_name", length = 3000)
        var data_name: String?,
        @Column(name = "data_type", length = 3000)
        var data_type: String?,
        @Column(name = "data_text_value", length = 3000)
        var data_text_value: String?,
        @Column(name = "data_num_value")
        var data_num_value: Double?
)