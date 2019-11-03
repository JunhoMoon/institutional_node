package com.hims.institutional_node.Model

import javax.persistence.*

@Entity
@Table(name = "MessageStack")
internal data class MessageStack(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "key_seq")
        @SequenceGenerator(sequenceName = "MessageStack_seq", allocationSize = 1, name = "key_seq")
        @Column(name = "Message_Stack_No", nullable = false)
        var message_stack_no: Long?,
        @Column(name = "target", nullable = false, length = 200)
        var node_kn: String,
        @Column(name = "message", nullable = false)
        @Lob
        var message: String
)