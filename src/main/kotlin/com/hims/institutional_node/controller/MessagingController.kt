package com.hims.institutional_node.controller

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hims.Central_Server.message.ParsingJSON
import com.hims.institutional_node.*
import com.hims.institutional_node.Model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(value = "/Messaging")
internal class MessagingController {
    @Autowired
    internal lateinit var memberDAO: MemberDAO
    @Autowired
    internal lateinit var memberPage: MemberPage
    @Autowired
    internal lateinit var nodeMappingDAO: NodeMappingDAO
    @Autowired
    internal lateinit var nodeMappingPage: NodeMappingPage
    @Autowired
    internal lateinit var messageStackDAO: MessageStackDAO
    @Autowired
    internal lateinit var primaryPhysicianDAO: PrimaryPhysicianDAO
    @Autowired
    internal lateinit var healthDao:HealthDAO
    @Autowired
    internal lateinit var healthDetailDAO: HealthDetailDAO
    @Autowired
    internal lateinit var notaryDataDAO: NotaryDataDAO
    @Autowired
    internal lateinit var healthNotaryDAO: HealthNotaryDAO
    @Autowired
    internal lateinit var healthViewDAO: HealthViewDAO
//    @Autowired
//    internal lateinit var healthFileDAO: HealthFileDAO
    @Autowired
    internal lateinit var testPatientDAO: TestPatientDAO

    @PostMapping("getMessage")
    fun getMessage(@RequestParam("message_stack_no") message_stack_no: Long, @RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String): Message? {
        var message:Message? = null
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                map.clear()
                map.add("node_kn", NodeInfoObject.node_kn)
                map.add("cert_key", NodeInfoObject.cert_key)
                map.add("target", node_kn)

                var key = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/getPublicKey", map, Communication_Key::class.java)

                if (key!=null){
                    var messageStack = messageStackDAO.findById(message_stack_no)

                    var aes_key = EncryptionAES.init()
                    var jsonValue = messageStack.get().message

                    var value = EncryptionAES.encryptAES(jsonValue, aes_key)
                    var sha_key = EncryptionSHA.encryptSha(jsonValue)

                    message = Message(node_kn, NodeInfoObject.node_kn, value, key.key_no, EncryptionRSA.encrypt(aes_key, key.key), sha_key)
                    messageStackDAO.deleteById(message_stack_no)
                }
            }
        }catch (e:Exception){
            println(e.toString())
        }
        return message
    }

    @PostMapping("acceptNodeMapping")
    fun acceptNodeMapping(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String) {
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                var now = Timestamp(Date().time)
                nodeMappingDAO.updateByNodeKn(node_kn, now)
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

    @PostMapping("rejectNodeMapping")
    fun rejectNodeMapping(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String) {
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                nodeMappingDAO.deleteById(node_kn)
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

    @PostMapping("acceptPrimaryPhysician")
    fun acceptPrimaryPhysician(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("primaryPhysician_id") primaryPhysician_id: String) {
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                var now = Timestamp(Date().time)
                primaryPhysicianDAO.updateByID(node_kn, primaryPhysician_id, now)
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

    @PostMapping("rejectPrimaryPhysician")
    fun rejectNodeMapping(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("primaryPhysician_id") primaryPhysician_id: String) {
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                var primaryPhysicianPK = PrimaryPhysicianPK(node_kn, primaryPhysician_id)
                primaryPhysicianDAO.deleteById(primaryPhysicianPK)
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

    @PostMapping("acceptHealth")
    fun acceptHealth(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("issuer_health_no") issuer_health_no: Long, @RequestParam("subject_health_no") subject_health_no: Long, @RequestParam("healthNotarys") healthNotarys: String) {
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                var health =  healthDao.findById(issuer_health_no)
                var healthData = health.get()
                var check_node_kn = nodeMappingDAO.findByPatientNo(healthData.patient_no!!)
                if (node_kn == check_node_kn){
                    healthData.subject_health_no = subject_health_no
                    healthDao.save(healthData)


                    var jsonObj = JsonParser().parse(healthNotarys) as JsonArray
                    for (obj in jsonObj){
                        println(obj)
                        var notary = HealthNotary(HealthNotaryPK(issuer_health_no, null), null, null)
                        notary.healthNotaryPK.notary_kn = obj.asJsonObject.get("notary_kn").toString().replace("\"", "")
                        notary.notary_data_no = obj.asJsonObject.get("notary_data_no").toString().toLong()
                        notary.reg_date = Timestamp.valueOf(obj.asJsonObject.get("regDate").toString().replace("\"", ""))

                        println(notary)
                        healthNotaryDAO.save(notary)
                    }
                }
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

    @PostMapping("rejectHealth")
    fun rejectHealth(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("issuer_health_no") issuer_health_no: Long) {
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                var health =  healthDao.findById(issuer_health_no)
                var healthData = health.get()
                var check_node_kn = nodeMappingDAO.findByPatientNo(healthData.patient_no!!)
                if (node_kn == check_node_kn){
                    healthData.subject_health_no = 0
                    healthDao.save(healthData)
                }
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

    @PostMapping("addNotary")
    fun addNotary(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("sha") sha: String):NotaryData? {
        var notaryData: NotaryData? = null
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                notaryData = NotaryData(null, sha, Timestamp(Date().time))
                notaryData = notaryDataDAO.save(notaryData)
            }
        }catch (e:Exception){
            println(e.toString())
        }
        return notaryData
    }

    @PostMapping("addHealthView")
    fun addHealthView(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("health_view_no") health_view_no: Long, @RequestParam("healthView") healthView: String) {
        println("test11111")
        try {
            val converters = ArrayList<HttpMessageConverter<*>>()
            converters.add(FormHttpMessageConverter())
            converters.add(StringHttpMessageConverter())
            converters.add(MappingJackson2HttpMessageConverter())

            val restTemplate = RestTemplate()
            restTemplate.messageConverters = converters
            val map = LinkedMultiValueMap<String, String>()
            map.add("node_kn", node_kn)
            map.add("cert_key", cert_key)

            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)

            if (checkCert!!){
                var healthViewOpt = healthViewDAO.findById(health_view_no)
                var healthViewModel = healthViewOpt.get()

                var healthJson = JsonParser().parse(healthView) as JsonObject

                println(healthJson)

                var test = healthJson.get("key_no").asString
                println("test : $test")
                var test1 = healthJson.get("aes_key").asString
                println("test1 : $test1")
                var test2 = healthJson.get("value").asString
                println("test2 : $test2")
                map.clear()
                map.add("node_kn", NodeInfoObject.node_kn)
                map.add("cert_key", NodeInfoObject.cert_key)
                map.add("key_no", healthJson.get("key_no").toString())
                var privateKey = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/getPrivateKey", map, String::class.java)
                println("privateKey : $privateKey")
                var aes_key = EncryptionRSA.decrypt(healthJson.get("aes_key").asString, privateKey!!)
                println("aes_key : $aes_key")
                var message = EncryptionAES.decryptAES(healthJson.get("value").asString, aes_key)
                println("message : $message")

                healthViewModel.message = message
                healthViewModel.accepted = Timestamp(Date().time)
                healthViewDAO.save(healthViewModel)
                println(message)
            }
        }catch (e:Exception){
            println(e.toString())
        }
    }

//    @PostMapping("getHealthFilesList")
//    fun getHealthFilesList(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("health_no") health_no: Long, @RequestParam("health_detail_no") health_detail_no: Long):MutableList<HealthFile>? {
//        var healthFiles = mutableListOf<HealthFile>()
//        try {
//            val converters = ArrayList<HttpMessageConverter<*>>()
//            converters.add(FormHttpMessageConverter())
//            converters.add(StringHttpMessageConverter())
//            converters.add(MappingJackson2HttpMessageConverter())
//
//            val restTemplate = RestTemplate()
//            restTemplate.messageConverters = converters
//            val map = LinkedMultiValueMap<String, String>()
//            map.add("node_kn", node_kn)
//            map.add("cert_key", cert_key)
//
//            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)
//
//            if (checkCert!!){
//                healthFiles = healthFileDAO.getById(health_no, health_detail_no)
//            }
//        }catch (e:Exception){
//            println(e.toString())
//        }
//        return healthFiles
//    }
//
//    @GetMapping("getHealthFile")
//    fun getHealthFile(@RequestParam("node_kn") node_kn: String, @RequestParam("cert_key") cert_key: String, @RequestParam("file_name") file_name: String, request: HttpServletRequest): ResponseEntity<Resource>? {
//        var result:ResponseEntity<Resource>? = null
//        try {
//            val converters = ArrayList<HttpMessageConverter<*>>()
//            converters.add(FormHttpMessageConverter())
//            converters.add(StringHttpMessageConverter())
//            converters.add(MappingJackson2HttpMessageConverter())
//
//            val restTemplate = RestTemplate()
//            restTemplate.messageConverters = converters
//            val map = LinkedMultiValueMap<String, String>()
//            map.add("node_kn", node_kn)
//            map.add("cert_key", cert_key)
//
//            var checkCert = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/CheckCertKey", map, Boolean::class.java)
//
//            if (checkCert!!){
//                var healthFile = healthFileDAO.findById(file_name)
//                var health = healthDao.findById(healthFile.get().health_no!!)
//                var nodeMapping = nodeMappingDAO.findById(node_kn)
//                if (health.get().patient_no?.equals(nodeMapping.get().patient_no)!!){
//                    var path = Paths.get(healthFile.get().file_loc+healthFile.get().file_name).normalize()
//                    var resource = ByteArrayResource(Files.readAllBytes(path))
//
//                    result = ResponseEntity.ok()
//                            .contentType(MediaType.parseMediaType(request.servletContext.getMimeType(resource.file.absolutePath)))
//                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                            .body(resource)
//                }
//            }
//        }catch (e:Exception){
//            println(e.toString())
//        }
//        return result
//    }
}