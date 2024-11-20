package com.example.nav.base

import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.nav.MainActivity
import com.example.nav.R
import com.example.nav.navigation.Nav

open class BaseFragment: Fragment() {

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val activity = activity
        Log.d("gnavnav", "onCreateAnimation enter=${enter} ${this}")
        val bottomNavigationController = (activity as? Nav.ControllerProvider)?.provideBottomNavigationController()
        var anim: Animation? = null
        if (bottomNavigationController != null) {
             val tabTransition = bottomNavigationController.tabTransition
            if (tabTransition != null) {

                when {
                    enter && tabTransition.currentIndex < tabTransition.toGoIndex -> {
                        bottomNavigationController.tabTransition = null
                        anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
                    }
                    !enter && tabTransition.currentIndex < tabTransition.toGoIndex -> {
                        anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
                    }
                    enter && tabTransition.currentIndex > tabTransition.toGoIndex -> {
                        bottomNavigationController.tabTransition = null
                        anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
                    }
                    !enter && tabTransition.currentIndex > tabTransition.toGoIndex -> {
                        anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
                    }
                }
//                if (enter) {
//                    //flow is exit first, then enter, so we have to reset the flag on enter
//                    //in order that following animations will run as defined in the nav graph
//
//                    activity.tabWasSelected = false
//                    return AnimationUtils.loadAnimation(requireContext(), R.anim.nav_default_pop_enter_anim)
//                } else {
//                    return AnimationUtils.loadAnimation(requireContext(), R.anim.nav_default_pop_exit_anim)
//                }
            }
        }
        //no tab was selected, so run the defined animation
        if (anim != null) {
            return anim
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim)
        }

    }
}