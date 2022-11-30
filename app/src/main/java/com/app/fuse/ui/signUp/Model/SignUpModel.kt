package com.app.fuse.ui.signUp.Model


data class SignUpModel(
    val access_token: String,
    val `data`: RegisterData,
    val msg: String,
    val status: Boolean
)

data class RegisterData(
    val created_at: String,
    val email: String,
    val id: Int,
    val is_verified: Boolean,
    val password: String,
    val status: Int,
    val updated_at: String,
    val username: String
)