package com.anafthdev.aquri.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.anafthdev.aquri.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Destinations {

    @Serializable
    data object Home : Destinations()

    @Serializable
    data object Statistic : Destinations()

    @Serializable
    data object Mission : Destinations()

    @Serializable
    data object Profile : Destinations()

    @Serializable
    data object Onboarding1 : Destinations()

    @Serializable
    data object Onboarding2 : Destinations()

    @Serializable
    data object Onboarding3 : Destinations()
}

enum class NavigationItem(
    val route: Destinations,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    Home(
        route = Destinations.Home,
        title = R.string.home,
        icon = Icons.Default.Home
    ),
    Statistic(
        route = Destinations.Statistic,
        title = R.string.statistic,
        icon = Icons.Default.BarChart
    ),
    Mission(
        route = Destinations.Mission,
        title = R.string.mission,
        icon = Icons.Default.Assignment
    ),
    Profile(
        route = Destinations.Profile,
        title = R.string.profile,
        icon = Icons.Default.Person
    )
}
