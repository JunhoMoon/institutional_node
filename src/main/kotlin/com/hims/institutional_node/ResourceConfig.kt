package com.hims.institutional_node

//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Configuration
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
//
//
//@Configuration
//class WebMvcConfig : WebMvcConfigurer {
//
//    @Value("\${resources.location}")
//    private val resourcesLocation: String? = null
//    @Value("\${resources.uri_path:}")
//    private val resourcesUriPath: String? = null
//
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        println(resourcesUriPath)
//        registry!!.addResourceHandler(resourcesUriPath!! + "/**")
//                .addResourceLocations("file://" + resourcesLocation!!)
//
//        super.addResourceHandlers(registry)
//    }
//
//}