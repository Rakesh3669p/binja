package com.app.fuse.ui.login.model


data class LoginModel(
    val access_token: String?,
    val `data`: RegisterData?,
    val msg: String,
    val status: Boolean
)

data class RegisterData(
    val created_at: String,
    val email: String,
    var profile: String,
    var age: String="",
    val status: Int,
    val reset_code: Int,
    val id: Int,
    val is_online:Boolean,
    var socket_id:String,
    var designation:String = "",
    val is_verified: Boolean,
    val password: String,
    val updated_at: String,
    var username: String
)
