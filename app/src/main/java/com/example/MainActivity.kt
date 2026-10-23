package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.ChurchDrawerContent
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditMemberScreen
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DepartmentsScreen
import com.example.ui.screens.EventsCalendarScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemberProfileScreen
import com.example.ui.screens.MembersListScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.FaithConnectTheme
import com.example.ui.viewmodel.FaithConnectViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaithConnectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FaithConnectApp()
                }
            }
        }
    }
}

@Composable
fun FaithConnectApp(
    viewModel: FaithConnectViewModel = viewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val userSession by viewModel.userSession.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    // Check if current screen is top-level (where drawer is available)
    val isTopLevel = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.MembersList.route,
        Screen.Departments.route,
        Screen.Attendance.route,
        Screen.EventsCalendar.route,
        Screen.Reports.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isTopLevel && isLoggedIn,
        drawerContent = {
            ChurchDrawerContent(
                currentRoute = currentRoute,
                userSession = userSession,
                onNavigate = { route ->
                    coroutineScope.launch { drawerState.close() }
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onSwitchUser = {
                    val nextRole = if (userSession.role == "Pastor") "Church Admin" else "Pastor"
                    viewModel.login(nextRole)
                    coroutineScope.launch { drawerState.close() }
                },
                onLogout = {
                    viewModel.logout()
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Dashboard Screen
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 3. Members List Screen
            composable(Screen.MembersList.route) {
                MembersListScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 4. Add / Edit Member Screen
            composable(
                route = Screen.AddEditMember.route,
                arguments = listOf(
                    navArgument("memberId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("memberId")
                val memberId = idStr?.toLongOrNull()

                AddEditMemberScreen(
                    memberId = memberId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSaved = { savedId ->
                        navController.popBackStack()
                    }
                )
            }

            // 5. Member Profile Screen
            composable(
                route = Screen.MemberProfile.route,
                arguments = listOf(
                    navArgument("memberId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val memberId = backStackEntry.arguments?.getLong("memberId") ?: 0L

                MemberProfileScreen(
                    memberId = memberId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddEditMember.createRoute(id))
                    }
                )
            }

            // 6. Departments Screen
            composable(Screen.Departments.route) {
                DepartmentsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 7. Attendance Screen
            composable(Screen.Attendance.route) {
                AttendanceScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            // 8. Events Calendar Screen
            composable(Screen.EventsCalendar.route) {
                EventsCalendarScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            // 9. Reports Screen
            composable(Screen.Reports.route) {
                ReportsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }
        }
    }
}
