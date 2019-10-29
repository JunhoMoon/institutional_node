package com.hims.institutional_node

import com.hims.institutional_node.Model.Member
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.annotation.Resource
import javax.servlet.http.HttpSession

@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/", method = [RequestMethod.GET, RequestMethod.POST])
class InstitutionalNodeUIController {
    @Autowired
    internal lateinit var memberDAO: MemberDAO
    @Autowired
    internal lateinit var memberPage: MemberPage

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
        println(memberdetail.toString())
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
    private fun healthRecordPage(@RequestParam("page", required = false, defaultValue = "1") page: Int,
                               @RequestParam("key_word", required = false, defaultValue = "") key_word: String,
                               session: HttpSession): ModelAndView {
        var mv = ModelAndView()

        var member: Member? = null
        if (session.getAttribute("member") !=null){
            member = session.getAttribute("member") as Member
        }
        if (member != null && member.h_datarecord == 1) {
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

    //구현중
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