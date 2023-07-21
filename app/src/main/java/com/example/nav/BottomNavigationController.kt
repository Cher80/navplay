package com.example.nav

import android.util.Log
import android.view.Menu
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
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

 */
class BottomNavigationController(
    private val tabs: List<Tab>,
    private val activity: AppCompatActivity,
    private val bottomNavigationView: BottomNavigationView,
    private val navController: NavController
) {

    private var pauseSetOnItemSelectedListener = false
    private val tabsMap: Map<Int, Tab> = tabs.associateBy {
        it.destinationId
    }


    fun build() {
        /**
         * Добавляем табы в bottomNavigationView
         */
        tabs.forEach { tab ->
            bottomNavigationView.menu.add(
                Menu.NONE,
                tab.actionId,
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
                navController.navigate(
                    resId = menuItem.itemId
                )
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
                    currentSelectedTab = tabsMap[navBackStackEntry.destination.id]
                }

                if (currentSelectedTab != null) {
                    // подсвечиваем выбранный таб
                    // ставим pauseSetOnItemSelectedListener чтобы в bottomNavigationView.onItemSelectedListener
                    // не дернулся navigate в этот таб (как при клике).
                    pauseSetOnItemSelectedListener = true
                    bottomNavigationView.selectedItemId = currentSelectedTab.actionId
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
            if (destination.id == tabs.first().destinationId) {
                controller.navigate(tabs.first().actionId)
            }
        }
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
}
