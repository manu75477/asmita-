package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AppSettingsEntity
import com.example.data.BirthdayDatabase
import com.example.ui.components.WebStyleTopNavigation
import com.example.ui.screens.AdminPageScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoriesTimelineScreen
import com.example.ui.screens.MessageGeneratorScreen
import com.example.ui.screens.PhotoFrameStudioScreen
import com.example.ui.screens.ProfileAndSyncScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.WebsiteViewScreen
import com.example.ui.theme.MyApplicationTheme

enum class NavTab(val title: String, val testTag: String) {
    HOME("Home", "nav_tab_home"),
    WEBSITE("Website", "nav_tab_website"),
    STUDIO("Studio", "nav_tab_studio"),
    MESSAGES("AI Wishes", "nav_tab_messages"),
    MEMORIES("Memories", "nav_tab_memories"),
    REMINDERS("Reminders", "nav_tab_reminders"),
    PROFILE("Profile", "nav_tab_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = BirthdayDatabase.getInstance(applicationContext)
        val dao = db.birthdayDao()

        // Sync target birthday to Asoj 7 (1790186700000L)
        lifecycleScope.launch(Dispatchers.IO) {
            val s = dao.getSettingsDirect()
            if (s != null && s.birthdayDateMillis != 1790186700000L) {
                dao.saveSettings(s.copy(birthdayDateMillis = 1790186700000L))
            }
        }

        setContent {
            val settingsState by dao.getSettings().collectAsState(initial = null)
            val currentSettings = settingsState ?: AppSettingsEntity(
                bestFriendName = "Asmita Yadav",
                greetingHeadline = "Happy Birthday, Dearest Asmita!"
            )

            MyApplicationTheme(
                darkTheme = currentSettings.isDarkMode,
                highContrast = currentSettings.isHighContrast
            ) {
                var currentTab by remember { mutableStateOf(NavTab.WEBSITE) }
                var isAdminOpen by remember { mutableStateOf(false) }
                var studioMessagePass by remember { mutableStateOf("") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (!isAdminOpen) {
                            WebStyleTopNavigation(
                                currentTab = currentTab,
                                onSelectTab = { selected -> currentTab = selected },
                                bestFriendName = currentSettings.bestFriendName,
                                onOpenAdmin = { isAdminOpen = true }
                            )
                        }
                    },
                    bottomBar = {
                        if (!isAdminOpen) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentTab == NavTab.HOME,
                                    onClick = { currentTab = NavTab.HOME },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text(NavTab.HOME.title) },
                                    modifier = Modifier.testTag(NavTab.HOME.testTag)
                                )
                                NavigationBarItem(
                                    selected = currentTab == NavTab.WEBSITE,
                                    onClick = { currentTab = NavTab.WEBSITE },
                                    icon = { Icon(Icons.Default.Language, contentDescription = "Website") },
                                    label = { Text(NavTab.WEBSITE.title) },
                                    modifier = Modifier.testTag(NavTab.WEBSITE.testTag)
                                )
                                NavigationBarItem(
                                    selected = currentTab == NavTab.STUDIO,
                                    onClick = { currentTab = NavTab.STUDIO },
                                    icon = { Icon(Icons.Default.Palette, contentDescription = "Studio") },
                                    label = { Text(NavTab.STUDIO.title) },
                                    modifier = Modifier.testTag(NavTab.STUDIO.testTag)
                                )
                                NavigationBarItem(
                                    selected = currentTab == NavTab.MESSAGES,
                                    onClick = { currentTab = NavTab.MESSAGES },
                                    icon = { Icon(Icons.Default.Psychology, contentDescription = "AI Wishes") },
                                    label = { Text(NavTab.MESSAGES.title) },
                                    modifier = Modifier.testTag(NavTab.MESSAGES.testTag)
                                )
                                NavigationBarItem(
                                    selected = currentTab == NavTab.MEMORIES,
                                    onClick = { currentTab = NavTab.MEMORIES },
                                    icon = { Icon(Icons.Default.Collections, contentDescription = "Memories") },
                                    label = { Text(NavTab.MEMORIES.title) },
                                    modifier = Modifier.testTag(NavTab.MEMORIES.testTag)
                                )
                                NavigationBarItem(
                                    selected = currentTab == NavTab.REMINDERS,
                                    onClick = { currentTab = NavTab.REMINDERS },
                                    icon = { Icon(Icons.Default.Alarm, contentDescription = "Reminders") },
                                    label = { Text(NavTab.REMINDERS.title) },
                                    modifier = Modifier.testTag(NavTab.REMINDERS.testTag)
                                )
                                NavigationBarItem(
                                    selected = currentTab == NavTab.PROFILE,
                                    onClick = { currentTab = NavTab.PROFILE },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text(NavTab.PROFILE.title) },
                                    modifier = Modifier.testTag(NavTab.PROFILE.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 680.dp)
                        ) {
                            Crossfade(targetState = if (isAdminOpen) "ADMIN" else currentTab.name, label = "ScreenTransition") { target ->
                            when (target) {
                                "ADMIN" -> {
                                    AdminPageScreen(
                                        dao = dao,
                                        settings = currentSettings,
                                        onBack = { isAdminOpen = false }
                                    )
                                }
                                NavTab.HOME.name -> {
                                    HomeScreen(
                                        settings = currentSettings,
                                        dao = dao,
                                        onNavigateToStudio = { currentTab = NavTab.STUDIO },
                                        onNavigateToMessages = { currentTab = NavTab.MESSAGES },
                                        onNavigateToMemories = { currentTab = NavTab.MEMORIES },
                                        onNavigateToReminders = { currentTab = NavTab.REMINDERS },
                                        onNavigateToProfile = { currentTab = NavTab.PROFILE },
                                        onNavigateToWebsite = { currentTab = NavTab.WEBSITE }
                                    )
                                }
                                NavTab.WEBSITE.name -> {
                                    WebsiteViewScreen(
                                        onNavigateBack = { currentTab = NavTab.HOME }
                                    )
                                }
                                NavTab.STUDIO.name -> {
                                    PhotoFrameStudioScreen(
                                        dao = dao,
                                        recipientName = currentSettings.bestFriendName,
                                        initialMessage = studioMessagePass
                                    )
                                }
                                NavTab.MESSAGES.name -> {
                                    MessageGeneratorScreen(
                                        dao = dao,
                                        recipientName = currentSettings.bestFriendName,
                                        onUseInStudio = { msg ->
                                            studioMessagePass = msg
                                            currentTab = NavTab.STUDIO
                                        }
                                    )
                                }
                                NavTab.MEMORIES.name -> {
                                    MemoriesTimelineScreen(
                                        dao = dao,
                                        friendName = currentSettings.bestFriendName
                                    )
                                }
                                NavTab.REMINDERS.name -> {
                                    RemindersScreen(
                                        dao = dao,
                                        friendName = currentSettings.bestFriendName
                                    )
                                }
                                NavTab.PROFILE.name -> {
                                    ProfileAndSyncScreen(
                                        dao = dao,
                                        settings = currentSettings,
                                        onOpenAdmin = { isAdminOpen = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
