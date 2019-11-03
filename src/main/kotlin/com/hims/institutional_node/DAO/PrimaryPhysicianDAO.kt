package com.hims.institutional_node

import com.hims.institutional_node.Model.Member
import com.hims.institutional_node.Model.NodeMapping
import com.hims.institutional_node.Model.PrimaryPhysician
import com.hims.institutional_node.Model.PrimaryPhysicianPK
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
internal interface PrimaryPhysicianDAO : CrudRepository<PrimaryPhysician, PrimaryPhysicianPK> {

    @Query(nativeQuery = true, value = "select * from PRIMARY_PHYSICIAN where node_kn like :node_kn")
    fun getAllbyNodeKN(@Param("node_kn") node_kn: String): MutableList<PrimaryPhysician>?
//
//    @Query(nativeQuery = true, value = "select count(*) from member where user_id like :user_id")
//    fun checkUserID(@Param("user_id") user_id: String): Int
//
//    @Query(nativeQuery = true, value = "update MEMBER set TYPE = :#{#member.type}, STATICIP = :#{#member.staticIp}, LEGACYID = :#{#member.legacyid}, DESCRIPTION = :#{#member.description}, H_DATARECORD = :#{#member.h_datarecord}, H_DATAVIEW = :#{#member.h_dataview}, A_PAYMENT = :#{#member.a_payment}, A_WITHDRAW = :#{#member.a_withdraw}, A_HISTORY = :#{#member.a_history}, PATIENTMAPPING = :#{#member.patientMapping}, H_SEARCH = :#{#member.h_search}, MANAGENODE = :#{#member.managenode} where USER_ID like :#{#member.user_id}")
//    @Modifying
//    @Transactional
//    fun manageMember(@Param("member") member: Member)
}
