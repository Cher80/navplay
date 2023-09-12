package com.example.nav.network

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Network @Inject constructor() {
    init {
        Log.d("gnavlife","Network init")
    }
    fun doPlus(i: Int) {
        Log.d("gnavlife","network i=$i")
    }
}