package com.example.nav

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch


/**
 * Класс который сетапит и контролирует всю навигацию через bottomNavigationView
 * У Гугла есть свой стандартный setupWithNavController(), но в нем есть несколько
 * критичных косяков.
 * Они сами про это знают и говорят прямым текстом - пишите сами как вам надо:
 * https://developer.android.com/guide/navigation/backstack/multi-back-stacks
 * If the elements provided by NavigationUI don't satisfy your requirements,
 * you can use the underlying APIs for saving and restoring back stacks through
 * one of the other API surfaces provided by the Navigation component.
 *
 * Вся навигация у нас делает pop/restore до destination_home. В таком случае:
 * У всех фрагментов отрабатывают onSaveInstanceState / savedInstanceState
 *
 * Из коробки (от Гугла) полоностью работает вытеснение/восстановление актикивити/процесса
 * из памяти - сохраняюся все стеки и позиция выбранного таба

 */
class BottomNavigationController(
    private val tabs: List<Tab>,
    private val activity: AppCompatActivity,
    private val bottomNavigationView: BottomNavigationView,
    private val navController: NavController
) {

    var tabTransition: Transition? = null
    private var pauseSetOnItemSelectedListener = false
    private val tabsMapDestination: Map<Int, Tab> = tabs.associateBy {
        it.destinationId
    }

//    private val tabsMapAction: Map<Int, Tab> = tabs.associateBy {
//        it.actionId
//    }


    @SuppressLint("RestrictedApi")
    fun build() {

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(
                    "gnavnav",
                    "handleOnBackPressed navController.currentDestination=${navController.currentDestination}"
                )
                val curId = navController.currentDestination?.id
                if (tabs.takeLast(tabs.size - 1).any { it.destinationId == curId }) {
                    navigate(
                        actionId = tabs.first().actionId,
                        args = null,
                        navigatorExtras = null,
                        navOptions = null
                    )
//                    //bottomNavigationView.selectedItemId = tabs.first().destinationId
//                    val tabNomeDestination = navController.currentBackStack.value[1].destination
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(
//                            destinationId = tabNomeDestination.id,
//                            inclusive = false,
//                            saveState = true
//                        )
//                        .setRestoreState(true)
////                        .setEnterAnim(R.anim.slide_in_left)
////                        .setExitAnim(R.anim.slide_in_right)
////                        .setPopEnterAnim(R.anim.slide_out_right)
////                        .setPopExitAnim(R.anim.slide_out_left)
//                        .build()
//
//                    navController.navigate(
//                        resId = tabNomeDestination.id,
//                        args = null,
//                        navOptions = navOptions,
//                        navigatorExtras = null
//                    )
//
//                    navController.navigate(
//                        resId = tabs.first().actionId
//                    )
                } else {
                    this.isEnabled = false
                    activity.onBackPressed()
                    this.isEnabled = true
                }

            }
        }

        activity.onBackPressedDispatcher.addCallback(activity, onBackPressedCallback)

        /**
         * Добавляем табы в bottomNavigationView
         */
        tabs.forEach { tab ->
            bottomNavigationView.menu.add(
                Menu.NONE,
                tab.destinationId,
                tab.index,
                tab.name,
            ).apply {
                setIcon(tab.icon)
            }
        }


        /**
         * Навигируем на Action таба при клике в таббар.
         * pauseSetOnItemSelectedListener нужен для случаев когда это был не клик
         * а просто подсветка выбранного таба через bottomNavigationView.setSelectedItemId
         * menuItem.itemId - это Action таба в графе
         */
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            if (!pauseSetOnItemSelectedListener) {
                Log.d("gnavnav", "setOnItemSelectedListener")

                navigate(
                    actionId = tabs.firstOrNull { it.destinationId == menuItem.itemId }!!.actionId,
                    args = null,
                    navigatorExtras = null,
                    navOptions = null
                )
//                val tabCurrentDestination = navController.graph.findNode(bottomNavigationView.selectedItemId)
//                val tabNomeDestination = navController.currentBackStack.value[1].destination
//                val tabToGoDestination = navController.graph.findNode(menuItem.itemId)
//
//                Log.d("gnavnav", "tabCurrentDestination = $tabCurrentDestination tabNomeDestination = $tabNomeDestination tabNome=$tabToGoDestination")
//
//                if (tabCurrentDestination != tabToGoDestination) {
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(
//                            destinationId = tabNomeDestination.id,
//                            inclusive = false,
//                            saveState = true
//                        )
//                        .setRestoreState(true)
////                        .setEnterAnim(R.anim.slide_in_left)
////                        .setExitAnim(R.anim.slide_in_right)
////                        .setPopEnterAnim(R.anim.slide_out_right)
////                        .setPopExitAnim(R.anim.slide_out_left)
//                        .build()
//
//                    navController.navigate(
//                        resId = tabToGoDestination!!.id,
//                        args = null,
//                        navOptions = navOptions,
//                        navigatorExtras = null
//                    )
//                }

            }
            true
        }


        /**
         * При повтоном клике в таб сбрасываем бекстек.
         * menuItem.itemId это Action таба в графе, по нему достаем destionatioID этого экшена
         * и попаем до него
         */
        bottomNavigationView.setOnItemReselectedListener { menuItem ->
            if (!pauseSetOnItemSelectedListener) {
                Log.d("gnavnav", "setOnItemReselectedListener")
                val tabDestinationId = navController.graph.getAction(menuItem.itemId)?.destinationId
                if (tabDestinationId != null) {
                    navController.popBackStack(
                        destinationId = tabDestinationId,
                        inclusive = false
                    )
                }
            }
        }

        /**
         * Обсервим изменения бекстека чтобы найти в нем destination выбранного таба и подстветить
         * его в bottomNavigationView
         * Так как при выборе таба навигация попоается до таба navigation_home, он всегда присутвует в стеке
         * то есть если выбран таб Notification и в нем два фрагмента Product, то бекстек выгдядит как
         * mobile_navigation / navigation_home / navigation_notifications / navigation_product / navigation_product
         * смотрим бекстек asReversed и находим в нем destionation таба, и подсвесчиваем его через selectedItemId
         */
        activity.lifecycleScope.launch {
            navController.currentBackStack.collect { backStacks ->
                Log.d("gnavnav", "currentBackStack")
                backStacks.forEach {
                    Log.d("gnavnav", "navBackStackEntry=${it.destination.displayName}")
                }

                var currentSelectedTab: Tab? = null
                val backStackIterator = backStacks.reversed().iterator()
                while (backStackIterator.hasNext() && currentSelectedTab == null) {
                    val navBackStackEntry = backStackIterator.next()
                    currentSelectedTab = tabsMapDestination[navBackStackEntry.destination.id]
                }

                if (currentSelectedTab != null) {
                    // подсвечиваем выбранный таб
                    // ставим pauseSetOnItemSelectedListener чтобы в bottomNavigationView.onItemSelectedListener
                    // не дернулся navigate в этот таб (как при клике).
                    pauseSetOnItemSelectedListener = true
                    bottomNavigationView.selectedItemId = currentSelectedTab.destinationId
                    pauseSetOnItemSelectedListener = false
                }
            }
        }

        /**
         * Фикс - когда переходишь в первый таб через back надо восстановить его бекстек
         * Просто видим если destination == home навигируем в его Action и стек восстанавливается
         */
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d("gnavnav", "OnDestinationChangedListener destination=${destination.displayName}")
//            if (destination.id == tabs.first().destinationId) {
//                controller.navigate(tabs.first().actionId)
//            }
        }
    }


    fun popBackStack() {
        navController.popBackStack()
    }

    @SuppressLint("RestrictedApi")
    fun navigate(
        @IdRes actionId: Int,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        val tab = tabs.firstOrNull {
            it.actionId == actionId
        }

        var navOptionsRich: NavOptions? = navOptions
        if (tab != null) {
            val tabCurrentDestination =
                navController.graph.findNode(bottomNavigationView.selectedItemId)
            val tabNomeDestination = navController.currentBackStack.value[1].destination
            val tabToGoDestination = navController.graph.findNode(tab.destinationId)

            Log.d(
                "gnavnav",
                "tabCurrentDestination = $tabCurrentDestination tabNomeDestination = $tabNomeDestination tabNome=$tabToGoDestination"
            )



            if (tabCurrentDestination != tabToGoDestination) {
                val currentIndex =
                    tabs.indexOfFirst { it.destinationId == tabCurrentDestination?.id }
                val toGoIndex =
                    tabs.indexOfFirst { it.destinationId == tabToGoDestination?.id }
                tabTransition = Transition(
                    currentIndex = currentIndex,
                    toGoIndex = toGoIndex
                )
                navOptionsRich = NavOptions.Builder()
                    .setPopUpTo(
                        destinationId = tabNomeDestination.id,
                        inclusive = false,
                        saveState = true
                    )
                    .setRestoreState(true)
                    .apply {

                        when {
                            toGoIndex == 0 && currentIndex > 0 -> {
//                                this.setEnterAnim(R.anim.slide_in_left)
//                                this.setExitAnim(R.anim.slide_out_right)
//                                this.setPopEnterAnim(R.anim.slide_in_left)
//                                this.setPopExitAnim(R.anim.slide_out_left)
                            }

                            currentIndex < toGoIndex -> {
//                                this.setEnterAnim(R.anim.slide_in_right)
//                                this.setExitAnim(R.anim.slide_out_right)
//                                this.setPopEnterAnim(R.anim.slide_out_right)
//                                this.setPopExitAnim(R.anim.slide_out_right)
                            }

                            currentIndex > toGoIndex -> {
//                                this.setEnterAnim(R.anim.slide_in_left)
//                                this.setExitAnim(R.anim.slide_out_right)
//                                this.setPopEnterAnim(R.anim.slide_out_right)
//                                this.setPopExitAnim(R.anim.slide_out_left)
                            }
                        }
                    }

                    .build()


//                navController.navigate(
//                    resId = tabToGoDestination!!.id,
//                    args = null,
//                    navOptions = navOptions,
//                    navigatorExtras = null
//                )
            }
        }
        navController.navigate(
            resId = actionId,
            args = args,
            navOptions = navOptionsRich,
            navigatorExtras = navigatorExtras
        )
    }

    /**
     * Структура в которой хранятся табы.
     */
    data class Tab(
        val index: Int,
        @IdRes val actionId: Int, // Action таба из графа навигации. Этот action также будет назначен menuItem.itemId
        @IdRes val destinationId: Int, // Destination таба из графа навигации
        val name: String,
        @DrawableRes val icon: Int
    )

    data class Transition(
        val toGoIndex: Int,
        val currentIndex: Int
    )
}
