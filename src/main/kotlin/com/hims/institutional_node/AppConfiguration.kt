package com.hims.institutional_node

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppConfiguration{
    @Value("\${spring.datasource.url}")
    lateinit var dbUrl:String
    @Value("\${spring.datasource.username}")
    lateinit var dbName:String
    @Value("\${spring.datasource.password}")
    lateinit var dbPW:String
}