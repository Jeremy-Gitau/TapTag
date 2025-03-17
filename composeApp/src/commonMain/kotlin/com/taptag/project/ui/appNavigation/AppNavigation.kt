package com.taptag.project.ui.appNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.taptag.project.ui.screens.home.HomeScreen

@Composable
fun AppNavigation() {

    Navigator(HomeScreen()) { navigator ->
        SlideTransition(navigator)
    }

}