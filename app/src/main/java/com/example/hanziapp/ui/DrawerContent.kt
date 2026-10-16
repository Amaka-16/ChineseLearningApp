package com.example.hanziapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val items = listOf(
    NavItem("dashboard",    "Dashboard",     Icons.Default.Home),
    NavItem("library",      "Library",       Icons.AutoMirrored.Filled.List),
    NavItem("practice",     "Practice",      Icons.Default.Edit),
    NavItem("random",       "Random Test",   Icons.Default.Refresh),
    NavItem("sentences",    "Sentences",     Icons.Default.Menu),
    NavItem("conversation", "Conversation",  Icons.Default.Call),
    NavItem("settings",     "Settings",      Icons.Default.Settings)
)

@Composable
fun DrawerContent(nav: NavController, onNavigate: () -> Unit) {
    val current = nav.currentBackStackEntryAsState().value?.destination?.route
    ModalDrawerSheet {
        Spacer(Modifier.height(24.dp))
        Text(
            "Hanzi Learner",
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = current == item.route,
                onClick = {
                    nav.navigate(item.route) {
                        popUpTo("dashboard") { inclusive = item.route == "dashboard" }
                        launchSingleTop = true
                    }
                    onNavigate()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
    }
}