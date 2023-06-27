package com.coventry.hkqipao.ui.profile

data class ReservationRecordsEntry(
    val date: String,
    val customerName: String,
    val dateOfRental: String,
    val emailAddress: String,
    val numberOfPeople: String,
    val phoneNumber: String,
    val remark: String
)

data class ReservationData(
    val customerName: String = "",
    val dateOfRental: String = "",
    val emailAddress: String = "",
    val numberOfPeople: String = "",
    val phoneNumber: String = "",
    val remark: String = ""
)