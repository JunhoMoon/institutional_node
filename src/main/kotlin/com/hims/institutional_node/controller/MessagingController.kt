package com.hims.institutional_node.controller

import com.google.gson.JsonObject
import com.hims.institutional_node.*
import com.hims.institutional_node.Model.Communication_Key
import com.hims.institutional_node.Model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.ArrayList


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
}