package com.example.nav.features.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.detmir.recycli.adapters.RecyclerItem
import com.detmir.recycli.annotations.RecyclerItemState
import com.example.nav.navigation.Nav
import com.example.nav.network.Network
import com.example.nav.ui.header.HeaderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val nav: Nav,
    private val network: Network
): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Log.d("gnavlife", "init DashboardViewModel")
    }


    var i = 0
    val iFlow = MutableStateFlow<Int>(i)
    val recyclerState = MutableStateFlow<List<RecyclerItem>>(emptyList())
    fun plusI() {
        i++
        network.doPlus(i)
        handleState()
    }

    fun onCreateView() {
        handleState()
    }

    private fun handleState() {
        iFlow.value = i

        recyclerState.value = (0..20).map {
            HeaderItem(
                id = "$it",
                title = "dash $it"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("gnavlife", "onCleared DashboardViewModel")
    }
}