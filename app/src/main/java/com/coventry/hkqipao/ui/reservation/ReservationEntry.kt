package com.coventry.hkqipao.ui.reservation

data class ReservationEntry(
    val customerName: String,
    val emailAddress: String,
    val phoneNumber: String,
    val dateOfRental: String,
    val numberOfPeople: String,
    val remark: String
)