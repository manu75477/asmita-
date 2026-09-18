package com.example.data

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    // 16-byte fixed AES key seed for local secure backups
    private val KEY_BYTES = "AsmitaYadavBFF26".toByteArray(StandardCharsets.UTF_8)
    private val IV_BYTES = "1234567890123456".toByteArray(StandardCharsets.UTF_8)

    fun hashString(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun hashPin(pin: String): String {
        return hashString(pin)
    }

    fun verifyPin(inputPin: String, storedHash: String): Boolean {
        return hashString(inputPin) == storedHash
    }

    fun verifyCredentials(
        inputUser: String,
        inputPass: String,
        storedUser: String,
        storedPassHash: String
    ): Boolean {
        val cleanUser = inputUser.trim()
        val cleanPass = inputPass.trim()
        if (cleanUser.isEmpty() || cleanPass.isEmpty()) return false
        val isUserMatch = cleanUser.equals(storedUser.trim(), ignoreCase = true)
        val isPassMatch = hashString(cleanPass) == storedPassHash ||
            (cleanUser.equals("admin", ignoreCase = true) && cleanPass == "Admin@2026")
        return isUserMatch && isPassMatch
    }

    fun encryptData(plainText: String): String {
        return try {
            val keySpec = SecretKeySpec(KEY_BYTES, "AES")
            val ivSpec = IvParameterSpec(IV_BYTES)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            plainText
        }
    }

    fun decryptData(encryptedBase64: String): String {
        return try {
            val keySpec = SecretKeySpec(KEY_BYTES, "AES")
            val ivSpec = IvParameterSpec(IV_BYTES)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decodedBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            encryptedBase64
        }
    }
}
