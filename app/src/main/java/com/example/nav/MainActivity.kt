package com.example.nav

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.nav.databinding.ActivityMainBinding
import com.example.nav.navigation.Nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Nav.ControllerProvider {

    @Inject lateinit var nav: Nav
    val tabs = mutableListOf<BottomNavigationController.Tab>(
        BottomNavigationController.Tab(
            index = 0,
            actionId = R.id.action_tab_home,
            destinationId = R.id.destination_home,
            name = "Home",
            icon = R.drawable.ic_home_black_24dp
        ),
        BottomNavigationController.Tab(
            index = 1,
            actionId = R.id.action_tab_dashboard,
            destinationId = R.id.destination_dashboard,
            name = "Dashboard",
            icon = R.drawable.ic_dashboard_black_24dp
        ),
        BottomNavigationController.Tab(
            index = 2,
            actionId = R.id.action_tab_notifications,
            destinationId = R.id.destination_notifications,
            name = "Notifications",
            icon = R.drawable.ic_notifications_black_24dp
        )
    )

    var bottomNavigationController: BottomNavigationController? = null


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("gnavlife", "MainActivity pre onCreate")
        super.onCreate(savedInstanceState)
        nav.controllerProvider = this
        Log.d("gnavlife", "MainActivity post onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationController = BottomNavigationController(
            tabs = tabs,
            activity = this,
            bottomNavigationView = binding.navView,
            navController = findNavController(R.id.nav_host_fragment_activity_main)
        ).apply {
            build()
        }
    }

    override fun provideBottomNavigationController(): BottomNavigationController? {
        return bottomNavigationController
    }

    override fun provideLifecycleScope(): LifecycleCoroutineScope = this.lifecycleScope

    override fun provideLifecycle(): Lifecycle = this.lifecycle
}





