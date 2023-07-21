package com.example.nav.ui

import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.nav.R

class NavHelper(
    private val fragment: Fragment,
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
            NavHostFragment.findNavController(fragment).popBackStack()
        }

        home.setOnClickListener {
            NavHostFragment.findNavController(fragment).navigate(R.id.action_tab_home)
        }

        dashboard.setOnClickListener {
            NavHostFragment.findNavController(fragment).navigate(R.id.action_tab_dashboard)
        }

        notifications.setOnClickListener {
            NavHostFragment.findNavController(fragment).navigate(R.id.action_tab_notifications)
        }


        product.setOnClickListener {
            NavHostFragment.findNavController(fragment).navigate(R.id.action_product)
        }

        seller.setOnClickListener {
            NavHostFragment.findNavController(fragment).navigate(R.id.action_seller)
        }

        company.setOnClickListener {
            NavHostFragment.findNavController(fragment).navigate(R.id.action_company)
        }

        plus.setOnClickListener {
            onPlus.invoke()
        }
    }
}