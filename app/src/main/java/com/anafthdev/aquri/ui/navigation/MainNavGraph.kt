package com.anafthdev.aquri.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anafthdev.aquri.ui.screens.home.HomeScreen
import com.anafthdev.aquri.ui.screens.mission.MissionScreen
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingScreen1
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingScreen2
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingScreen3
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingViewModel
import com.anafthdev.aquri.ui.screens.profile.ProfileScreen
import com.anafthdev.aquri.ui.screens.statistic.StatisticScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: Destinations,
    modifier: Modifier = Modifier
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Destinations.Onboarding1> {
            OnboardingScreen1(navController, onboardingViewModel)
        }
        composable<Destinations.Onboarding2> {
            OnboardingScreen2(navController, onboardingViewModel)
        }
        composable<Destinations.Onboarding3> {
            OnboardingScreen3(navController, onboardingViewModel)
        }
        composable<Destinations.Home> {
            HomeScreen()
        }
        composable<Destinations.Statistic> {
            StatisticScreen()
        }
        composable<Destinations.Mission> {
            MissionScreen()
        }
        composable<Destinations.Profile> {
            ProfileScreen()
        }
    }
}
