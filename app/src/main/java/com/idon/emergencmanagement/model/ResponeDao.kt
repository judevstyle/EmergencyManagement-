package com.idon.emergencmanagement.model

data class ResponeDao(
    val status: Int,
    val msg: String

)


data class ResponeUserDao(
    val status: Int,
    val msg: String,
    val user: UserFull?=null

)