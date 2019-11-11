package com.hims.institutional_node

import com.hims.institutional_node.Model.HealthDetail
import com.hims.institutional_node.Model.HealthDetailPK
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
internal interface HealthDetailDAO : CrudRepository<HealthDetail, HealthDetailPK> {
}

@Repository
internal interface HealthDetailPage : PagingAndSortingRepository<HealthDetail, HealthDetailPK> {
}