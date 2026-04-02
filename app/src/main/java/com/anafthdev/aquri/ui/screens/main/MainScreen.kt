package com.anafthdev.aquri.ui.screens.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anafthdev.aquri.ui.navigation.Destinations
import com.anafthdev.aquri.ui.navigation.MainNavGraph
import com.anafthdev.aquri.ui.navigation.NavigationItem

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val startDestination by viewModel.startDestination.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { destination ->
        NavigationItem.entries.any { item -> destination.hasRoute(item.route::class) }
    } == true

    if (startDestination == null) return

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    NavigationItem.entries.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

                        NavigationBarItem(
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = item.title),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        MainNavGraph(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(16.dp)
        )
    }
}
