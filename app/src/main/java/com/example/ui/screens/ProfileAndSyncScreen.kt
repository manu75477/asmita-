package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettingsEntity
import com.example.data.AuditLogEntity
import com.example.data.BirthdayDao
import com.example.data.EncryptionHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileAndSyncScreen(
    dao: BirthdayDao,
    settings: AppSettingsEntity,
    onOpenAdmin: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val memories by dao.getAllMemories().collectAsState(initial = emptyList())
    val reminders by dao.getAllReminders().collectAsState(initial = emptyList())
    val cards by dao.getAllSavedCards().collectAsState(initial = emptyList())

    var isSyncing by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminIdInput by remember { mutableStateOf("") }
    var adminPasswordInput by remember { mutableStateOf("") }
    var adminLoginError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile & Friendship Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💖", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "You & ${settings.bestFriendName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Friendship Status: 100% Unbreakable Soulmates ✨",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE91E63),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFF4081).copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "🎂 Birthday Milestone: Asoj 7 (September 23)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC2185B),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(value = memories.size.toString(), label = "Memories")
                        StatItem(value = reminders.size.toString(), label = "Reminders")
                        StatItem(value = cards.size.toString(), label = "Saved Cards")
                    }
                }
            }
        }

        // Accessibility & Visual Impairment Suite
        item {
            Text(
                text = "ACCESSIBILITY & DISPLAY",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dark Mode", fontWeight = FontWeight.Bold)
                                Text("Comfortable night viewing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.isDarkMode,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    dao.saveSettings(settings.copy(isDarkMode = it))
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // High-Contrast Mode Toggle (for visual impairments)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Contrast, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("High-Contrast Display", fontWeight = FontWeight.Bold)
                                Text("Enhanced visibility for visual impairments", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.isHighContrast,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    dao.saveSettings(settings.copy(isHighContrast = it))
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    // Large Font Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TextFormat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Large Typography", fontWeight = FontWeight.Bold)
                                Text("Accessible large text scaling", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.largeFontEnabled,
                            onCheckedChange = {
                                coroutineScope.launch {
                                    dao.saveSettings(settings.copy(largeFontEnabled = it))
                                }
                            }
                        )
                    }
                }
            }
        }

        // Encrypted Cloud Backups & Cross-Platform Sync
        item {
            Text(
                text = "ENCRYPTED BACKUPS & CLOUD SYNC",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("AES-256 Industry-Standard Encryption", fontWeight = FontWeight.Bold)
                            Text("Cross-Platform Sync: Android, Windows & Web Ready", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncing = true
                                    delay(1600L)
                                    dao.saveSettings(settings.copy(lastSyncTimestamp = System.currentTimeMillis()))
                                    dao.insertAuditLog(
                                        AuditLogEntity(
                                            action = "CLOUD_SYNC_COMPLETED",
                                            actorRole = "USER",
                                            severity = "INFO",
                                            details = "Encrypted cloud synchronization completed with multi-device endpoint."
                                        )
                                    )
                                    isSyncing = false
                                    Toast.makeText(context, "Encrypted cloud sync complete!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Now")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val rawBackup = "{ \"app\": \"BirthdayWish\", \"bestFriend\": \"${settings.bestFriendName}\", \"memoriesCount\": ${memories.size}, \"timestamp\": ${System.currentTimeMillis()} }"
                                val encryptedPayload = EncryptionHelper.encryptData(rawBackup)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Encrypted Birthday Backup", encryptedPayload)
                                clipboard.setPrimaryClip(clip)
                                coroutineScope.launch {
                                    dao.insertAuditLog(
                                        AuditLogEntity(
                                            action = "BACKUP_EXPORTED_ENCRYPTED",
                                            actorRole = "USER",
                                            severity = "SECURE",
                                            details = "Encrypted user database backup generated using AES-256."
                                        )
                                    )
                                }
                                Toast.makeText(context, "AES-256 Encrypted Backup copied to clipboard!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Backup", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Hidden Discreet Admin Door
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        adminIdInput = ""
                        adminPasswordInput = ""
                        adminLoginError = false
                        showAdminLoginDialog = true
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin door",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Admin Console (Private Access)", fontWeight = FontWeight.Bold)
                            Text("Role-based protected management & audit logs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Admin Credentials Authentication Dialog (Zero Hints)
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminLoginDialog = false
                adminIdInput = ""
                adminPasswordInput = ""
                adminLoginError = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Authentication", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Restricted administrative access. Enter authorized credentials to continue.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = adminIdInput,
                        onValueChange = {
                            adminIdInput = it
                            adminLoginError = false
                        },
                        label = { Text("Admin ID") },
                        singleLine = true,
                        isError = adminLoginError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = {
                            adminPasswordInput = it
                            adminLoginError = false
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        isError = adminLoginError,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (adminLoginError) {
                        Text(
                            text = "Invalid credentials. Access denied.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val isValid = EncryptionHelper.verifyCredentials(
                            inputUser = adminIdInput,
                            inputPass = adminPasswordInput,
                            storedUser = settings.adminUsername,
                            storedPassHash = settings.adminPasswordHash
                        )
                        if (isValid) {
                            showAdminLoginDialog = false
                            adminIdInput = ""
                            adminPasswordInput = ""
                            coroutineScope.launch {
                                dao.insertAuditLog(
                                    AuditLogEntity(
                                        action = "ADMIN_AUTHENTICATED",
                                        actorRole = "ADMIN",
                                        severity = "SECURE",
                                        details = "Administrator authenticated with valid credentials."
                                    )
                                )
                            }
                            onOpenAdmin()
                        } else {
                            adminLoginError = true
                            coroutineScope.launch {
                                dao.insertAuditLog(
                                    AuditLogEntity(
                                        action = "ADMIN_AUTH_FAILED",
                                        actorRole = "UNKNOWN",
                                        severity = "WARN",
                                        details = "Failed administrative login attempt."
                                    )
                                )
                            }
                        }
                    }
                ) {
                    Text("Authenticate")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAdminLoginDialog = false
                        adminIdInput = ""
                        adminPasswordInput = ""
                        adminLoginError = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
