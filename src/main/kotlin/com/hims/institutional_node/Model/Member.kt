package com.hims.institutional_node.Model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "MEMBER")
internal data class Member(
        @Id
        @Column(name = "USER_ID", unique = true, nullable = false, length = 200)
        var user_id: String?,
        @Column(name = "USER_NAME", length = 200)
        var user_name: String?,
        @Column(name = "EMAIL", length = 300)
        var email: String?,
        @Column(name = "TYPE", length = 30)
        var type: String?,
        @Column(name = "STATICIP", length = 30)
        var staticIp: String?,
        @Column(name = "LEGACYID", length = 200)
        var legacyid: String?,
        @Column(name = "DESCRIPTION", length = 2000)
        var description: String?,
        @Column(name = "H_DATARECORD", length = 1)
        var h_datarecord: Int? = 0,
        @Column(name = "H_DATAVIEW", length = 1)
        var h_dataview: Int? = 0,
        @Column(name = "A_PAYMENT", length = 1)
        var a_payment: Int? = 0,
        @Column(name = "A_WITHDRAW", length = 1)
        var a_withdraw: Int? = 0,
        @Column(name = "A_HISTORY", length = 1)
        var a_history: Int? = 0,
        @Column(name = "PATIENTMAPPING", length = 1)
        var patientMapping: Int? = 0,
        @Column(name = "H_SEARCH", length = 1)
        var h_search: Int? = 0,
        @Column(name = "MANAGENODE", length = 1)
        var managenode: Int? = 0,
        @Column(name = "USER_PW", length = 3000)
        var user_pw: String?
)