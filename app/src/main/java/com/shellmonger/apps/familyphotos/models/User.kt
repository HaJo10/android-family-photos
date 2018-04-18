package com.shellmonger.apps.familyphotos.models

import java.util.*

/**
 * The TokenType is a list of allowable tokens available
 */
enum class TokenType {
    ID_TOKEN,
    ACCESS_TOKEN,
    REFRESH_TOKEN
}

/**
 * The user type is for storing information about the currently authenticated user
 */
class User(val id: String = UUID.randomUUID().toString(), var username: String = "") {
    /**
     * The list of attributes that are known about this user
     */
    val userAttributes: MutableMap<String, String> = HashMap()

    /**
     * The list of tokens that are available
     */
    val tokens: MutableMap<TokenType, String> = HashMap()
}
