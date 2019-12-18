package com.hims.institutional_node

import com.hims.institutional_node.Model.HealthView
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface HealthViewDAO : CrudRepository<HealthView, Long> {
}

@Repository
internal interface HealthViewPage : PagingAndSortingRepository<HealthView, String> {
    @Query(nativeQuery = true, value = "select * from health_view where LOWER(acceptor) like LOWER(concat(concat('%',:key_word),'%')) or LOWER(patient_no) like LOWER(concat(concat('%',:key_word),'%'))")
    fun getHealthViewList(@Param("key_word") key_word: String, pageable: Pageable): Page<HealthView>
}