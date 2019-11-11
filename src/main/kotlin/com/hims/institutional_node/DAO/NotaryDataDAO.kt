package com.hims.institutional_node

import com.hims.institutional_node.Model.NotaryData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface NotaryDataDAO : CrudRepository<NotaryData, Long> {
}