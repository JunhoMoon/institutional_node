package com.hims.institutional_node

import com.hims.institutional_node.Model.Health
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
internal interface HealthDAO : CrudRepository<Health, Long> {
}

@Repository
internal interface HealthPage : PagingAndSortingRepository<Health, String> {
}