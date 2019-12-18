//package com.hims.institutional_node
//
//import com.hims.institutional_node.Model.Health
//import com.hims.institutional_node.Model.HealthFile
//import org.springframework.data.jpa.repository.Query
//import org.springframework.data.repository.CrudRepository
//import org.springframework.data.repository.PagingAndSortingRepository
//import org.springframework.data.repository.query.Param
//import org.springframework.stereotype.Repository
//
//@Repository
//internal interface HealthFileDAO : CrudRepository<HealthFile, String> {
//    @Query(nativeQuery = true, value = "SELECT * FROM HealthFile where health_no = :health_no and health_detail_no = :health_detail_no")
//    fun getById(@Param("health_no") health_no: Long, @Param("health_detail_no") health_detail_no: Long): MutableList<HealthFile>
//}
//
//@Repository
//internal interface HealthFilePage : PagingAndSortingRepository<HealthFile, String> {
//}