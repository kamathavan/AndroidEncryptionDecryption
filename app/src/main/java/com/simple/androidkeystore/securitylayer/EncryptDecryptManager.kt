package com.simple.androidkeystore.securitylayer

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class EncryptDecryptManager {

    companion object {
        private val ALGORITHM_KEY = KeyProperties.KEY_ALGORITHM_AES
        private val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private val TRANSFORMATION = "$ALGORITHM_KEY/$BLOCK_MODE/$PADDING"
    }

    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptChiper: Cipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun getDecryptCipherForIV(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM_KEY).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream): String {
        val encryptedBytes = encryptChiper.doFinal(
            bytes
        )
        outputStream.use {
            it.write(encryptChiper.iv.size)
            it.write(encryptChiper.iv)

            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes.decodeToString()
    }

    fun decrypt(inputStream: InputStream): String {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipherForIV(iv).doFinal(encryptedBytes).decodeToString()
        }
    }
}