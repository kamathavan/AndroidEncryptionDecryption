package com.simple.androidkeystore.securitylayer

import androidx.datastore.core.Serializer
import com.simple.androidkeystore.datamodel.UserSetting
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class UserSettingSerializer(
    private val encryptDecryptManager: EncryptDecryptManager
) : Serializer<UserSetting> {
    override val defaultValue: UserSetting
        get() = UserSetting()

    override suspend fun readFrom(input: InputStream): UserSetting {
        /*   val decryptedValues = encryptDecryptManager.decrypt(input)
           return try {
               Json.decodeFromString(
                   deserializer = null,
                   string = decryptedValues
               )
           } catch (e: SerializationException) {
               e.printStackTrace()
               defaultValue
           }*/
        return UserSetting()
    }

    override suspend fun writeTo(t: UserSetting, output: OutputStream) {
        /*  encryptDecryptManager.encrypt(
              bytes = Json.encodeToString(
                  serializer = UserSetting.serializer(),
                  value = t
              ).encodeToByteArray(),
              outputStream = output
          )*/
    }

}