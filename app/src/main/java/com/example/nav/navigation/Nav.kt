package com.example.nav.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.withStarted
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.example.nav.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Nav @Inject constructor() {

    lateinit var controllerProvider: ControllerProvider

    private var navJob: Job? = null

    fun popBackStack() {
        controllerProvider.provideLifecycleScope().launch {
            controllerProvider.provideLifecycle().withStarted {
                controllerProvider.provideNavController().popBackStack()
            }
        }
    }



    fun gotoHome() {
        navigate(R.id.action_tab_home)
    }

    fun gotoDashboard() {
        navigate(R.id.action_tab_dashboard)
    }

    fun gotoNotifications() {
        navigate(R.id.action_tab_notifications)
    }

    fun gotoProduct() {
        navigate(R.id.action_product)
    }

    fun gotoSeller() {
        navigate(R.id.action_seller)
    }

    fun gotoCompany() {
        navigate(R.id.action_company)
    }

    fun gotoCamera() {
        navigate(R.id.action_camera)
    }

    fun gotoCameraSber() {
        navigate(R.id.action_camera_sber)
    }

    private fun navigate(
        @IdRes actionId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        navJob?.cancel()
        navJob = controllerProvider.provideLifecycleScope().launch {
            controllerProvider.provideLifecycle().withStarted {
                controllerProvider.provideNavController().navigate(actionId, args, navOptions, navigatorExtras)
            }
        }
    }

    interface ControllerProvider {
        fun provideNavController(): NavController
        fun provideLifecycleScope(): LifecycleCoroutineScope
        fun provideLifecycle(): Lifecycle
    }
}