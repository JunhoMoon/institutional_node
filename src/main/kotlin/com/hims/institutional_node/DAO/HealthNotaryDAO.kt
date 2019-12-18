package com.hims.institutional_node

import com.hims.institutional_node.Model.HealthNotary
import com.hims.institutional_node.Model.HealthNotaryPK
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface HealthNotaryDAO : CrudRepository<HealthNotary, HealthNotaryPK> {
}