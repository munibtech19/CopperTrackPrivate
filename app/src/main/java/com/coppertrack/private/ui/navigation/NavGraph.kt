package com.coppertrack.private.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.coppertrack.private.ui.screens.contacts.ContactsScreen
import com.coppertrack.private.ui.screens.dashboard.DashboardScreen
import com.coppertrack.private.ui.screens.deals.DealsScreen
import com.coppertrack.private.ui.screens.expenses.ExpensesScreen
import com.coppertrack.private.ui.screens.reports.ReportsScreen
import com.coppertrack.private.ui.screens.settings.SettingsScreen
import com.coppertrack.private.ui.theme.CopperPrimary
import com.coppertrack.private.ui.theme.DarkSurface
import com.coppertrack.private.ui.theme.TextMuted
import com.coppertrack.private.ui.theme.TextPrimary

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
    data object Deals : Screen("deals", "Deals", Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz)
    data object Contacts : Screen("contacts", "Contacts", Icons.Filled.People, Icons.Outlined.People)
    data object Expenses : Screen("expenses", "Expenses", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    data object Reports : Screen("reports", "Reports", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    data object Settings : Screen("settings", "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Deals,
    Screen.Contacts,
    Screen.Expenses,
    Screen.Reports,
    Screen.Settings
)

@Composable
fun CopperTrackNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = com.coppertrack.private.ui.theme.DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextPrimary
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CopperPrimary,
                            selectedTextColor = CopperPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CopperPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Deals.route) { DealsScreen() }
            composable(Screen.Contacts.route) { ContactsScreen() }
            composable(Screen.Expenses.route) { ExpensesScreen() }
            composable(Screen.Reports.route) { ReportsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
