package com.example.nav.features

import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.nav.R
import com.example.nav.navigation.Nav

class NavHelper(
    private val nav: Nav,
    private val pop: Button,
    private val home: Button,
    private val dashboard: Button,
    private val notifications: Button,
    private val product: Button,
    private val company: Button,
    private val seller: Button,
    private val plus: Button,
    private val onPlus: () -> Unit
) {
    fun doBind() {

        pop.setOnClickListener {
            nav.popBackStack()
        }

        home.setOnClickListener {
            nav.gotoHome()
        }

        dashboard.setOnClickListener {
            nav.gotoDashboard()
        }

        notifications.setOnClickListener {
            nav.gotoNotifications()
        }


        product.setOnClickListener {
            nav.gotoProduct()
        }

        seller.setOnClickListener {
            nav.gotoSeller()
        }

        company.setOnClickListener {
            nav.gotoCompany()
        }

        plus.setOnClickListener {
            onPlus.invoke()
        }
    }
}