package com.anafthdev.aquri.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anafthdev.aquri.ui.screens.auth.LoginScreen
import com.anafthdev.aquri.ui.screens.auth.RegisterScreen
import com.anafthdev.aquri.ui.screens.home.HomeScreen
import com.anafthdev.aquri.ui.screens.manage_bottle.ManageBottleScreen
import com.anafthdev.aquri.ui.screens.mission.AllMissionsScreen
import com.anafthdev.aquri.ui.screens.mission.BadgesScreen
import com.anafthdev.aquri.ui.screens.mission.LevelLadderScreen
import com.anafthdev.aquri.ui.screens.mission.MissionScreen
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingScreen1
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingScreen2
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingScreen3
import com.anafthdev.aquri.ui.screens.onboarding.OnboardingViewModel
import com.anafthdev.aquri.ui.screens.personal_information.PersonalInformationScreen
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
        composable<Destinations.Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Destinations.Register)
                },
                onAuthComplete = { destination ->
                    navController.navigateAfterAuth(destination)
                }
            )
        }
        composable<Destinations.Register> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAuthComplete = { destination ->
                    navController.navigateAfterAuth(destination)
                }
            )
        }
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
            HomeScreen(
                onManageBottle = {
                    navController.navigate(Destinations.ManageBottle)
                }
            )
        }
        composable<Destinations.ManageBottle> {
            ManageBottleScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Destinations.Statistic> {
            StatisticScreen()
        }
        composable<Destinations.Mission> {
            MissionScreen(
                onViewAllMissions = {
                    navController.navigate(Destinations.AllMissions)
                },
                onViewBadges = {
                    navController.navigate(Destinations.Badges)
                },
                onViewLevelLadder = {
                    navController.navigate(Destinations.LevelLadder)
                }
            )
        }
        composable<Destinations.AllMissions> {
            AllMissionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Destinations.Badges> {
            BadgesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Destinations.LevelLadder> {
            LevelLadderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Destinations.Profile> {
            ProfileScreen(
                onPersonalInformation = {
                    navController.navigate(Destinations.PersonalInformation)
                },
                onLoginRequired = {
                    navController.navigate(Destinations.Login)
                }
            )
        }
        composable<Destinations.PersonalInformation> {
            PersonalInformationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

private fun NavHostController.navigateAfterAuth(destination: Destinations) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
