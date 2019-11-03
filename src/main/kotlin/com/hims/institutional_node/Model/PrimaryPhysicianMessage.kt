package com.hims.institutional_node.Model

import java.sql.Timestamp

internal data class PrimaryPhysicianMessage(
        var node_kn: String?,
        var primaryPhysician_id: String?,
        var reg_date: Timestamp?,
        var primaryPhysician_name:String?
)