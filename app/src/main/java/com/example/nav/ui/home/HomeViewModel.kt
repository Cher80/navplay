package com.example.nav.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Log.d("gnavlife", "init HomeViewModel")
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
        Log.d("gnavlife", "onCleared HomeViewModel")
    }
}