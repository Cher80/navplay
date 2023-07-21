package com.example.nav.ui.fcompany

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CompanyViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Log.d("gnavlife", "init CompanyViewModel")
    }

    var i = 0
    val iFlow = MutableStateFlow<Int>(i)
    fun plusI() {
        i++
        handleState()
    }

    fun onCreateView() {
        handleState()
    }

    fun handleState() {
        iFlow.value = i
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("gnavlife", "onCleared CompanyViewModel")
    }
}