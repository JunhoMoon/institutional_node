package com.hims.institutional_node

import java.math.BigInteger
import java.security.MessageDigest

object EncryptionSHA{
    fun encryptSha(value:String):String{
        val digest = MessageDigest.getInstance("SHA-512")
        digest.reset()
        digest.update(value.toByteArray(Charsets.UTF_8))
        return String.format("%0128x", BigInteger(1, digest.digest()))
    }
}