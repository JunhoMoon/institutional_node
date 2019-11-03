package com.hims.institutional_node

import com.hims.institutional_node.Model.Member
import com.hims.institutional_node.Model.TestPatient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal interface TestPatientDAO : CrudRepository<TestPatient, String> {
}