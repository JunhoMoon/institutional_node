package com.hims.institutional_node.controller

import com.hims.institutional_node.AppConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = "/Authentication")
internal class AuthenticationController {
    @Autowired
    lateinit var appConfiguration:AppConfiguration
}