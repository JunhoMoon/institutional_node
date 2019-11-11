package com.hims.institutional_node

import com.hims.institutional_node.Model.Member
import com.hims.institutional_node.Model.NodeMapping
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
internal interface NodeMappingDAO : CrudRepository<NodeMapping, String> {
    @Query(nativeQuery = true, value = "update NODE_MAPPING set ACCEPTED = :accepted where node_kn like :node_kn")
    @Modifying
    @Transactional
    fun updateByNodeKn(@Param("node_kn") node_kn: String, @Param("accepted") accepted:Timestamp)

    @Query(nativeQuery = true, value = "select node_kn from NODE_MAPPING where PATIENT_NO like :patient_no")
    fun findByPatientNo(@Param("patient_no") patient_no: String): String?
}

@Repository
internal interface NodeMappingPage : PagingAndSortingRepository<NodeMapping, String> {
    @Query(nativeQuery = true, value = "select * from Node_Mapping where LOWER(node_kn) like LOWER(concat(concat('%',:key_word),'%')) or LOWER(patient_no) like LOWER(concat(concat('%',:key_word),'%'))")
    fun getNodeMappingList(@Param("key_word") key_word: String, pageable: Pageable): Page<NodeMapping>
}