package com.hims.institutional_node

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object EncryptionAES{
    internal fun init():String {
        var generator = KeyGenerator.getInstance("AES")
        var random = SecureRandom.getInstance("SHA1PRNG")
        generator.init(256, random);
        var secureKey = generator.generateKey()
        return Base64.getEncoder().encodeToString(secureKey.encoded)
    }

    internal fun encryptAES(plainText: String, key:String): String {
        var decodedkey = Base64.getDecoder().decode(key)
        var secretKey = SecretKeySpec(decodedkey, 0, decodedkey.size, "AES")

        var cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        var encryptedData = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedData)
    }

    internal fun decryptAES(value:String, key:String):String{
        var decodedkey = Base64.getDecoder().decode(key)
        var secretKey = SecretKeySpec(decodedkey, 0, decodedkey.size, "AES")

        var cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        var encryptedData = cipher.doFinal(Base64.getDecoder().decode(value))

        return String(encryptedData)
    }
}