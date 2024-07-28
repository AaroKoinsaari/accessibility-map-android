/*
 * Copyright (c) 2024 Aaro Koinsaari
 */
@file:Suppress("MatchingDeclarationName", "ForbiddenComment")

package com.aarokoinsaari.accessibilitymap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

enum class Screen(val route: String, val icon: ImageVector) {
    Map("map", Icons.Filled.Place), // TODO: Change to Map icon
    Places("places", Icons.AutoMirrored.Filled.List)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Map,
        Screen.Places
    )
    NavigationBar {
        val currentRoute = navController.currentRoute()
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                icon = { Icon(screen.icon, contentDescription = null) },
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

fun NavController.currentRoute(): String? {
    return currentBackStackEntry?.destination?.route
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBar_Preview() {
    BottomNavigationBar(rememberNavController())
}
