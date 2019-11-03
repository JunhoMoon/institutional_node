package com.hims.institutional_node.Model

import java.sql.Timestamp

internal data class NodeMappingMessage(
        var node_kn:String, var patient_no:String, var reg_date:Timestamp, var node_name:String
)