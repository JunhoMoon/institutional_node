package com.hims.institutional_node

import com.hims.Central_Server.message.ParsingJSON
import com.hims.institutional_node.Model.Communication_Key
import com.hims.institutional_node.Model.Message
import com.hims.institutional_node.Model.NodeInfo
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.lang.Exception
import java.net.InetAddress
import java.util.*


@SpringBootApplication
class InstitutionalNodeApplication

fun main(args: Array<String>) {
    runApplication<InstitutionalNodeApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)

//기관 사용자 노드 로그인
        if (NodeInfoObject.node_kn == null){
            var nodeInfo = NodeInfo(null, null, null, null, null)

            var login = false
            println("1 - Login, 2 - Edit PW, 3 - EXIT")
            print("Enter Function NO : ")
            var funtionNo: String = readLine()!!

            when (funtionNo) {
                "1" -> {
                    // RestTemplate 에 MessageConverter 세팅
                    val converters = ArrayList<HttpMessageConverter<*>>()
                    converters.add(FormHttpMessageConverter())
                    converters.add(StringHttpMessageConverter())
                    converters.add(MappingJackson2HttpMessageConverter())

                    val restTemplate = RestTemplate()
                    restTemplate.messageConverters = converters

                    // parameter 세팅
                    val map = LinkedMultiValueMap<String, String>()

                    print("Enter Node UID : ")
                    nodeInfo.node_uid = readLine()!!

                    map.add("node_uid", nodeInfo.node_uid)
                    map.add("node_ap_type", "institutional")

                    // REST API 호출 - user_kn 확인
                    try {
                        val user_kn = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/getNodeInfoByUID", map, String::class.java)
                        if (user_kn != null) {
                            nodeInfo.node_kn = user_kn
                            println("Hello $user_kn")
                            print("Enter Node PW : ")
                            nodeInfo.node_pw = readLine()!!

                            map.clear()
                            map.add("target", "center")
                            // REST API 호출 - 중앙 서버 공개키 획득
                            try {
                                var key = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/getPublicKey", map, Communication_Key::class.java)

                                if (key != null) {
                                    nodeInfo.node_pw = EncryptionSHA.encryptSha(nodeInfo.node_pw!!)

                                    var value_sha = EncryptionSHA.encryptSha(nodeInfo.node_pw!!)
                                    var aes_key = EncryptionAES.init()
                                    var node_pw = EncryptionAES.encryptAES(nodeInfo.node_pw!!, aes_key)

                                    var message: Message = Message("center", nodeInfo.node_kn, node_pw, key.key_no, EncryptionRSA.encrypt(aes_key, key.key), value_sha)

                                    try {
                                        var responseValue = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/institutionLogin", message, String::class.java)

                                        if (responseValue.equals("Receiver mismatch") || responseValue.equals("Validation key mismatch") || responseValue.equals("Password mismatch")) {
                                            println(responseValue)
                                        } else {
                                            println("Login success")
                                            println("Your access point address set in the server is '$responseValue'.")
                                            val local = InetAddress.getLocalHost().hostAddress
                                            println("And, your ip address is '$local'.")
                                            println("Enter '1' if the address is correct on the server, otherwise enter the new address.")
                                            print("Enter AP : ")
                                            nodeInfo.node_ap = readLine()!!
                                            if (nodeInfo.node_ap.equals("1")) {
                                                NodeInfoObject.node_kn = nodeInfo.node_kn!!
                                                login = true
                                            } else {
                                                key = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/getPublicKey", map, Communication_Key::class.java)
                                                if (key != null) {
                                                    aes_key = EncryptionAES.init()
                                                    var jsonValue = ParsingJSON.modelToJson(nodeInfo)

                                                    var value = EncryptionAES.encryptAES(jsonValue, aes_key)
                                                    var sha_key = EncryptionSHA.encryptSha(jsonValue)

                                                    message = Message("center", nodeInfo.node_kn, value, key.key_no, EncryptionRSA.encrypt(aes_key, key.key), sha_key)
                                                    var checkChange = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/changeNodeAP", message, Boolean::class.java)
                                                    if (checkChange!!) {
                                                        login = true
                                                    }
                                                }
                                            }

                                            key = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/getPublicKey", map, Communication_Key::class.java)
                                            if (key != null) {
                                                aes_key = EncryptionAES.init()
                                                var jsonValue = ParsingJSON.modelToJson(nodeInfo)

                                                var value = EncryptionAES.encryptAES(jsonValue, aes_key)
                                                var sha_key = EncryptionSHA.encryptSha(jsonValue)

                                                message = Message("center", nodeInfo.node_kn, value, key.key_no, EncryptionRSA.encrypt(aes_key, key.key), sha_key)
                                                NodeInfoObject.cert_key = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/refreshCertKeyforInst", message, String::class.java)!!
                                                if (NodeInfoObject.cert_key == null) {
                                                    login = false
                                                }
                                            } else {
                                                login = false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("Connect Error : $e")
                                    }
                                } else {
                                    println("Connect Error")
                                }
                            } catch (e: Exception) {
                                println("Connect Error : $e")
                            }

                        } else {
                            println("Node does not exist")
                        }
                    } catch (e: Exception) {
                        println("Connect Error : $e")
                    }
                }
                "2" -> {

                }
                else -> {
                    System.exit(0)
                }
            }
            println(login)
            if (!login) {
                System.exit(0)
            }
        }
    }
}
