package com.panuphong.smssender.model

data class PostData(
    val bank: String,
    val bankcode: String,
    val phonenumber: String,
    val sms: String,
    val smsdate: String,
    val smsid: String
)