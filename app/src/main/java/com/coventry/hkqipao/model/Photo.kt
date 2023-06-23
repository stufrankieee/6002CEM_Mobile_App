package com.coventry.hkqipao.model

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val download_url: String
)