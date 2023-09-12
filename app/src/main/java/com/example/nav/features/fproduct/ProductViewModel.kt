package com.example.nav.features.fproduct

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nav.navigation.Nav
import com.example.nav.network.Network
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    val nav: Nav,
    private val network: Network
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Log.d("gnavlife", "init ProductViewModel")
    }

    var i = 0
    val iFlow = MutableStateFlow<Int>(i)
    fun plusI() {
        i++
        network.doPlus(i)
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
        Log.d("gnavlife", "onCleared ProductViewModel")
    }
}