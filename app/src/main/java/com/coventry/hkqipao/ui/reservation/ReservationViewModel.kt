package com.coventry.hkqipao.ui.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is reservation Fragment"
    }
    val text: LiveData<String> = _text
}