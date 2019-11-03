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

@Repository
internal interface NodeMappingDAO : CrudRepository<NodeMapping, String> {
//    @Query(nativeQuery = true, value = "select * from member where user_id like :user_id and user_pw like :user_pw")
//    fun checkMember(@Param("user_id") user_id: String, @Param("user_pw") user_pw: String): Member?
//
//    @Query(nativeQuery = true, value = "select count(*) from member where user_id like :user_id")
//    fun checkUserID(@Param("user_id") user_id: String): Int
//
//    @Query(nativeQuery = true, value = "update MEMBER set TYPE = :#{#member.type}, STATICIP = :#{#member.staticIp}, LEGACYID = :#{#member.legacyid}, DESCRIPTION = :#{#member.description}, H_DATARECORD = :#{#member.h_datarecord}, H_DATAVIEW = :#{#member.h_dataview}, A_PAYMENT = :#{#member.a_payment}, A_WITHDRAW = :#{#member.a_withdraw}, A_HISTORY = :#{#member.a_history}, PATIENTMAPPING = :#{#member.patientMapping}, H_SEARCH = :#{#member.h_search}, MANAGENODE = :#{#member.managenode} where USER_ID like :#{#member.user_id}")
//    @Modifying
//    @Transactional
//    fun manageMember(@Param("member") member: Member)
}

@Repository
internal interface NodeMappingPage : PagingAndSortingRepository<NodeMapping, String> {
    @Query(nativeQuery = true, value = "select * from Node_Mapping where LOWER(node_kn) like LOWER(concat(concat('%',:key_word),'%')) or LOWER(patient_no) like LOWER(concat(concat('%',:key_word),'%'))")
    fun getNodeMappingList(@Param("key_word") key_word: String, pageable: Pageable): Page<NodeMapping>
}