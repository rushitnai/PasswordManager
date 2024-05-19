package com.example.passwordmanager.ui.theme.utils

import android.util.Base64
import java.nio.charset.Charset
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES"

     const val STATIC_KEY = "RushStaticKey123"

    fun generateKey(): String {
        val keyGen = KeyGenerator.getInstance(ALGORITHM)
        keyGen.init(256)
        val secretKey = keyGen.generateKey()
        return Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
    }

    // Encrypt a string with the static key
    fun encrypt(input: String): String {
        try {
            // Create a secret key
            val secretKey = SecretKeySpec(STATIC_KEY.toByteArray(), ALGORITHM)

            // Initialize the cipher with the secret key for encryption
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            // Perform the encryption
            val encryptedBytes = cipher.doFinal(input.toByteArray())

            // Encode the encrypted bytes to a Base64-encoded string
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle encryption failure
            return ""
        }
    }

    // Decrypt a string with the static key
    fun decrypt(input: String): String {
        try {
            // Create a secret key
            val secretKey = SecretKeySpec(STATIC_KEY.toByteArray(), ALGORITHM)

            // Initialize the cipher with the secret key for decryption
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            // Decode the Base64-encoded input string to encrypted bytes
            val encryptedBytes = Base64.decode(input, Base64.DEFAULT)

            // Perform the decryption
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            // Convert the decrypted bytes to a string
            return String(decryptedBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle decryption failure
            return ""
        }
    }
}
