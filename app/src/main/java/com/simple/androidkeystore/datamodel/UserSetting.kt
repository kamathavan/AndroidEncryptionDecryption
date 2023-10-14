package com.simple.androidkeystore.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class UserSetting(
    val username: String? = "",
    val password: String? = ""
)
