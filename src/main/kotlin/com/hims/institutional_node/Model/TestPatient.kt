package com.hims.institutional_node.Model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TestPatient")
internal data class TestPatient(
        @Id
        @Column(name = "patient_no", unique = true, nullable = false, length = 200)
        var patient_no: String?,
        @Column(name = "patient_name", length = 200)
        var patient_name: String?,
        @Column(name = "birthday", length = 300)
        var birthday: String?
)