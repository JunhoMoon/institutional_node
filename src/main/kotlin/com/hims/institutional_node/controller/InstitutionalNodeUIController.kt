package com.hims.institutional_node

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.hims.Central_Server.message.ParsingJSON
import com.hims.institutional_node.Model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ModelAndView
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpSession

@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/", method = [RequestMethod.GET, RequestMethod.POST])
class InstitutionalNodeUIController {
    @Autowired
    internal lateinit var memberDAO: MemberDAO
    @Autowired
    internal lateinit var memberPage: MemberPage
    @Autowired
    internal lateinit var nodeMappingDAO: NodeMappingDAO
    @Autowired
    internal lateinit var nodeMappingPage: NodeMappingPage
    @Autowired
    internal lateinit var primaryPhysicianDAO: PrimaryPhysicianDAO
    @Autowired
    internal lateinit var messageStackDAO: MessageStackDAO
    @Autowired
    internal lateinit var healthDAO: HealthDAO
    @Autowired
    internal lateinit var healthDetailDAO: HealthDetailDAO
    @Autowired
    internal lateinit var testPatientDAO: TestPatientDAO

    @RequestMapping(value = "loginPage", method = [RequestMethod.GET])
    private fun loginPage(session: HttpSession): ModelAndView {
        var mv = ModelAndView()
        mv.viewName = "login"
        return mv
    }

    @RequestMapping(value = "loginProcess", method = [RequestMethod.POST])
    @ResponseBody
    private fun loginProcess(@ModelAttribute member: Member, session: HttpSession): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var checkmember = memberDAO.checkMember(member.user_id!!, member.user_pw!!)

        checkmember?.user_pw = null

        if (checkmember != null) {
            session.setAttribute("member", checkmember)
            when {
                checkmember.managenode == 1 -> result["result"] = "./manageNodePage"
                checkmember.h_dataview == 1 -> result["result"] = "./healthViewPage"
                checkmember.h_datarecord == 1 -> result["result"] = "./healthRecordPage"
                checkmember.a_payment == 1 -> result["result"] = "./assetPaymentPage"
                checkmember.a_withdraw == 1 -> result["result"] = "./assetWithdrawPage"
                checkmember.a_history == 1 -> result["result"] = "./assetHistoryPage"
                checkmember.patientMapping == 1 -> result["result"] = "./patientMappingPage"
                checkmember.h_search == 1 -> result["result"] = "./healthSearchPage"
                else -> {
                    session.removeAttribute("member")
                    result["result"] = "Fail"
                    result["error"] = "This user is not authorized.\n" +
                            "Please contact system administrator."
                }
            }
        } else {
            result["result"] = "Fail"
            result["error"] = "User does not exist"
        }
        return result
    }

    @RequestMapping(value = "joinPage", method = [RequestMethod.GET])
    private fun joinPage(session: HttpSession): ModelAndView {
        var mv = ModelAndView()
        mv.viewName = "join"
        return mv
    }

    @RequestMapping(value = "checkUserID", method = [RequestMethod.POST])
    @ResponseBody
    private fun checkUserID(@RequestParam("user_id") user_id: String): Map<String, Int> {
        val result: MutableMap<String, Int> = mutableMapOf()

        result["cnt"] = memberDAO.checkUserID(user_id)

        return result
    }

    @RequestMapping(value = "joinProcess", method = [RequestMethod.POST])
    @ResponseBody
    private fun joinProcess(@ModelAttribute member: Member): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        try {
            member.h_search = 0
            member.h_dataview = 0
            member.h_datarecord = 0
            member.a_withdraw = 0
            member.a_payment = 0
            member.a_history = 0
            member.patientMapping = 0
            member.managenode = 0
            memberDAO.save(member)
            result["result"] = "true"
        } catch (e: Exception) {
            result["result"] = e.toString()
        }
        return result
    }

    @RequestMapping(value = "manageNodePage", method = [RequestMethod.POST])
    private fun manageNodePage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                               @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                               session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.managenode == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberPage = memberPage.getMemberList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "user_id")))
            var memberList = memberPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", memberPage.totalPages)
            mv.addObject("memberList", memberList)

            mv.viewName = "manageNode"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "manageNodeDetailPage", method = [RequestMethod.POST])
    private fun manageNodeDetailPage(@RequestParam("user_id") user_id: String,
                               session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.managenode == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberdetail = memberDAO.findById(user_id)

            var getMember = memberdetail.get()
            getMember.user_pw = null

            mv.addObject("memberdetail", getMember)

            mv.viewName = "manageNodeDetail"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "manageMemberProcess", method = [RequestMethod.POST])
    @ResponseBody
    private fun manageMemberProcess(@ModelAttribute memberdetail: Member, session: HttpSession): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.managenode == 1) {
            try {
                memberDAO.manageMember(memberdetail)
                result["result"] = "true"
            } catch (e: Exception) {
                result["result"] = e.toString()
                println(e.toString())
            }
        } else {
            session.removeAttribute("member")
            result["result"] = "false"
        }
        return result
    }

    //구현중
    @RequestMapping(value = "healthRecordPage", method = [RequestMethod.POST])
    private fun healthRecordPage(session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.h_datarecord == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            mv.viewName = "recordHealthData"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "addHealthData", method = [RequestMethod.POST])
    @ResponseBody
    private fun addHealthData(@RequestParam("patient_no") patient_no: String, @RequestParam("details") details: String, session: HttpSession): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.patientMapping == 1) {
            try {
                var health: Health = Health(null, null, patient_no, member.user_id, Timestamp(Date().time))

                health = healthDAO.save(health)

                var healthDetails:List<HealthDetail>?

                val groupListType = object : TypeToken<List<HealthDetail>>(){}.type
                healthDetails = ParsingJSON.gson.fromJson(details, groupListType)

                for(detail in healthDetails!!){
                    detail.healthDetailPK.health_no = health.issuer_health_no
                }

                healthDetailDAO.saveAll(healthDetails)

                val converters = ArrayList<HttpMessageConverter<*>>()
                converters.add(FormHttpMessageConverter())
                converters.add(StringHttpMessageConverter())
                converters.add(MappingJackson2HttpMessageConverter())

                val restTemplate = RestTemplate()
                restTemplate.messageConverters = converters
                val map = LinkedMultiValueMap<String, String>()
                map.add("node_kn",NodeInfoObject.node_kn)
                map.add("cert_key",NodeInfoObject.cert_key)
                map.add("last_health_no", health.issuer_health_no.toString())
                restTemplate.postForObject("http://220.149.87.125:10000/Authentication/UpdateLastHealthNo", map, Boolean::class.java)

                var node_kn = nodeMappingDAO.findByPatientNo(patient_no)
                if (node_kn != null){
                    map.clear()
                    map.add("node_kn",node_kn)
                    val checkKN = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/checkKN", map, Boolean::class.java)

                    if (!checkKN!!){
                        var healthMessage = ParsingJSON.modelToJson(health)
                        var healthDetailMessage = ParsingJSON.modelToJson(healthDetails)

                        var jsonObj = JsonObject()

                        jsonObj.add("health", JsonParser().parse(healthMessage))
                        jsonObj.add("healthDetail", JsonParser().parse(healthDetailMessage))


                        var message:String = jsonObj.toString()
                        message.replace("\"", "")

                        var messageStack = MessageStack(null, node_kn, ParsingJSON.modelToJson(jsonObj))
                        messageStack = messageStackDAO.save(messageStack)

                        map.clear()
                        map.add("sender", NodeInfoObject.node_kn!!)
                        map.add("cert_key", NodeInfoObject.cert_key!!)
                        map.add("message_type", "recordHealthData")
                        map.add("target", node_kn)
                        map.add("stack_no", messageStack.message_stack_no.toString())

                        var checkSend = restTemplate.postForObject("http://220.149.87.125:10000/MessageQueue/sendToPersonal", map, Boolean::class.java)

                        if (checkSend!!){
                            result["result"] = "true"
                        }else{
                            result["result"] = "Failed to send message to partner node"
                        }
                    }else{
                        result["result"] = "Node Kn does not exist."
                    }
                }else{
                    result["result"] = "true"
                }
            } catch (e: Exception) {
                result["result"] = e.toString()
            }
        } else {
            session.removeAttribute("member")
            result["result"] = "false"
        }
        return result
    }

    //구현중
    @RequestMapping(value = "healthViewPage", method = [RequestMethod.POST])
    private fun healthViewPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                                 @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                                 session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.h_dataview == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberPage = memberPage.getMemberList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "user_id")))
            var memberList = memberPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", memberPage.totalPages)
            mv.addObject("memberList", memberList)

            mv.viewName = "manageNode"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    //환자 맵핑
    @RequestMapping(value = "patientMappingPage", method = [RequestMethod.POST])
    private fun patientMappingPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                               @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                               session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.patientMapping == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var nodeMappingPage = nodeMappingPage.getNodeMappingList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "reg_date")))
            var nodeMappingList = nodeMappingPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", nodeMappingPage.totalPages)
            mv.addObject("nodeMappingList", nodeMappingList)

            mv.viewName = "patientMapping"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "addPatientMappingPage", method = [RequestMethod.GET])
    private fun addPatientMappingPage(session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.patientMapping == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            mv.viewName = "addPatientMapping"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "getPatientInfo", method = [RequestMethod.POST])
    @ResponseBody
    private fun getPatientInfo(@RequestParam("patient_no") patient_no: String, session: HttpSession): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.patientMapping == 1) {
            try {
                var patient_data =  testPatientDAO.findById(patient_no)
                var patient = patient_data.get()
                result["result"] = "true"
                result["patient_name"] = patient.patient_name!!
                result["birthday"] = patient.birthday!!
            } catch (e: Exception) {
                result["result"] = e.toString()
            }
        } else {
            session.removeAttribute("member")
            result["result"] = "false"
        }
        return result
    }

    @RequestMapping(value = "addPatientMappingProcess", method = [RequestMethod.POST])
    @ResponseBody
    private fun addPatientMappingProcess(@ModelAttribute nodeMapping: NodeMapping, session: HttpSession): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.patientMapping == 1) {
            try {
                val converters = ArrayList<HttpMessageConverter<*>>()
                converters.add(FormHttpMessageConverter())
                converters.add(StringHttpMessageConverter())
                converters.add(MappingJackson2HttpMessageConverter())

                val restTemplate = RestTemplate()
                restTemplate.messageConverters = converters
                val map = LinkedMultiValueMap<String, String>()
                map.add("node_kn", nodeMapping.node_kn)
                val checkKN = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/checkKN", map, Boolean::class.java)

                if (!checkKN!!){
                    nodeMapping.acceptor = member.user_id
                    nodeMapping.reg_date = Timestamp(Date().time)
                    var nodeMapping = nodeMappingDAO.save(nodeMapping)
                    var nodeMappingMessage = NodeMappingMessage(NodeInfoObject.node_kn!!, nodeMapping.patient_no!!, nodeMapping.reg_date!!, NodeInfoObject.node_name)
                    var messageStack = MessageStack(null, nodeMapping.node_kn!!, ParsingJSON.modelToJson(nodeMappingMessage))
                    messageStack = messageStackDAO.save(messageStack)

                    map.clear()
                    map.add("sender", NodeInfoObject.node_kn!!)
                    map.add("cert_key", NodeInfoObject.cert_key!!)
                    map.add("message_type", "node_mapping")
                    map.add("target", nodeMapping.node_kn!!)
                    map.add("stack_no", messageStack.message_stack_no.toString())

                    var checkSend = restTemplate.postForObject("http://220.149.87.125:10000/MessageQueue/sendToPersonal", map, Boolean::class.java)

                    if (checkSend!!){
                        result["result"] = "true"
                    }else{
                        result["result"] = "Failed to send message to partner node"
                    }
                }else{
                    result["result"] = "Node Kn does not exist."
                }
            } catch (e: Exception) {
                result["result"] = e.toString()
            }
        } else {
            session.removeAttribute("member")
            result["result"] = "false"
        }
        return result
    }

    @RequestMapping(value = "patientMappingDetailPage", method = [RequestMethod.POST])
    private fun patientMappingDetailPage(@RequestParam("node_kn") node_kn: String,
                                     session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.managenode == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var nodeMapping = nodeMappingDAO.findById(node_kn)

            mv.addObject("nodeMapping", nodeMapping.get())

            var primaryPhysicians = primaryPhysicianDAO.getAllbyNodeKN(node_kn)

            mv.addObject("primaryPhysicians", primaryPhysicians)

            mv.viewName = "patientMappingDetail"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "addPrimaryPhysician", method = [RequestMethod.POST])
    @ResponseBody
    private fun addPrimaryPhysician(@RequestParam("node_kn") node_kn: String,
                                    @RequestParam("primaryPhysician_id") primaryPhysician_id: String,
                                         session: HttpSession): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.patientMapping == 1) {
            try {
                val converters = ArrayList<HttpMessageConverter<*>>()
                converters.add(FormHttpMessageConverter())
                converters.add(StringHttpMessageConverter())
                converters.add(MappingJackson2HttpMessageConverter())

                val restTemplate = RestTemplate()
                restTemplate.messageConverters = converters
                val map = LinkedMultiValueMap<String, String>()
                map.add("node_kn", node_kn)
                val checkKN = restTemplate.postForObject("http://220.149.87.125:10000/Authentication/checkKN", map, Boolean::class.java)

                if (!checkKN!!){
                    var primaryPhysician = PrimaryPhysician(PrimaryPhysicianPK(node_kn, primaryPhysician_id), member.user_id, Timestamp(Date().time), null)
                    primaryPhysician = primaryPhysicianDAO.save(primaryPhysician)
                    var physician = memberDAO.findById(primaryPhysician_id)
                    var primaryPhysicianMessage = PrimaryPhysicianMessage(NodeInfoObject.node_kn!!, primaryPhysician.pk.primaryPhysician!!, primaryPhysician.reg_date!!, physician.get().user_name)
                    var messageStack = MessageStack(null, node_kn, ParsingJSON.modelToJson(primaryPhysicianMessage))
                    messageStack = messageStackDAO.save(messageStack)

                    map.clear()
                    map.add("sender", NodeInfoObject.node_kn!!)
                    map.add("cert_key", NodeInfoObject.cert_key!!)
                    map.add("message_type", "addPrimaryPhysician")
                    map.add("target", node_kn)
                    map.add("stack_no", messageStack.message_stack_no.toString())

                    var checkSend = restTemplate.postForObject("http://220.149.87.125:10000/MessageQueue/sendToPersonal", map, Boolean::class.java)

                    if (checkSend!!){
                        result["result"] = "true"
                    }else{
                        result["result"] = "Failed to send message to partner node"
                    }
                }else{
                    result["result"] = "Node Kn does not exist."
                }
            } catch (e: Exception) {
                result["result"] = e.toString()
            }
        } else {
            session.removeAttribute("member")
            result["result"] = "false"
        }
        println(result.toString())
        return result
    }

    //구현중
    @RequestMapping(value = "healthSearchPage", method = [RequestMethod.POST])
    private fun healthSearchPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                                   @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                                   session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.h_search == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberPage = memberPage.getMemberList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "user_id")))
            var memberList = memberPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", memberPage.totalPages)
            mv.addObject("memberList", memberList)

            mv.viewName = "manageNode"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    //구현중
    @RequestMapping(value = "assetPaymentPage", method = [RequestMethod.POST])
    private fun assetPaymentPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                                 @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                                 session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.a_payment == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberPage = memberPage.getMemberList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "user_id")))
            var memberList = memberPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", memberPage.totalPages)
            mv.addObject("memberList", memberList)

            mv.viewName = "manageNode"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    //구현중
    @RequestMapping(value = "assetWithdrawPage", method = [RequestMethod.POST])
    private fun assetWithdrawPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                                 @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                                 session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.a_withdraw == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberPage = memberPage.getMemberList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "user_id")))
            var memberList = memberPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", memberPage.totalPages)
            mv.addObject("memberList", memberList)

            mv.viewName = "manageNode"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    //구현중
    @RequestMapping(value = "assetHistoryPage", method = [RequestMethod.POST])
    private fun assetHistoryPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                                  @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                                  session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.a_history == 1) {
            mv.addObject("member", member)
            mv.addObject("node_kn", NodeInfoObject.node_kn)

            var memberPage = memberPage.getMemberList(key_word, PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "user_id")))
            var memberList = memberPage.content

            mv.addObject("nowPage", page)
            mv.addObject("key_word", key_word)
            mv.addObject("totalPages", memberPage.totalPages)
            mv.addObject("memberList", memberList)

            mv.viewName = "manageNode"
        } else {
            session.removeAttribute("member")
            mv.viewName = "redirect:/loginPage"
        }
        return mv
    }

    @RequestMapping(value = "logout", method = [RequestMethod.GET])
    private fun logout(session: HttpSession): ModelAndView {
        var mv = ModelAndView()
        if (session.getAttribute("member") !=null){
            session.removeAttribute("member")
        }
        mv.viewName = "redirect:/loginPage"
        return mv
    }
}