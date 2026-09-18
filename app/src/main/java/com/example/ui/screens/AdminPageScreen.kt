package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.components.PhotoLightboxDialog
import com.example.data.AdminMediaEntity
import com.example.data.AppSettingsEntity
import com.example.data.AuditLogEntity
import com.example.data.BirthdayDao
import com.example.data.EncryptionHelper
import com.example.data.MemoryEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPageScreen(
    dao: BirthdayDao,
    settings: AppSettingsEntity,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auditLogs by dao.getAuditLogs().collectAsState(initial = emptyList())
    val adminMediaList by dao.getAllAdminMedia().collectAsState(initial = emptyList())

    // Settings state
    var editFriendName by remember { mutableStateOf(settings.bestFriendName) }
    var editHeadline by remember { mutableStateOf(settings.greetingHeadline) }
    var editQuote by remember { mutableStateOf(settings.specialQuote) }
    var editBirthdayDateMillis by remember { mutableLongStateOf(settings.birthdayDateMillis) }
    var editAdminUsername by remember { mutableStateOf(settings.adminUsername) }
    var newAdminPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Media viewer dialog state
    var activeVideoDialog by remember { mutableStateOf<AdminMediaEntity?>(null) }
    var activePhotoDialog by remember { mutableStateOf<AdminMediaEntity?>(null) }

    // Media upload state
    var pendingMediaUri by remember { mutableStateOf<Uri?>(null) }
    var pendingMediaType by remember { mutableStateOf("PHOTO") }
    var showUploadModal by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                pendingMediaUri = uri
                pendingMediaType = "PHOTO"
                showUploadModal = true
            }
        }
    )

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                pendingMediaUri = uri
                pendingMediaType = "VIDEO"
                showUploadModal = true
            }
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation & Admin Brand
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Admin")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADMIN CONTROL PANEL",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Role: Root Administrator • Content & System Management",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Security Status Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Role-Based Security: Active", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Admin identity secured with SHA-256 hashing. All administrative actions and media uploads are audit logged.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Admin Media Studio (Photo & Video Uploads)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEDIA MANAGEMENT (${adminMediaList.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Upload Photos & Videos for ${settings.bestFriendName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Upload exclusive celebration photos and video greetings directly to the app. Uploads can be shared immediately to the Memories Timeline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Photo", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Video", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Uploaded Media Items
        if (adminMediaList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No media uploaded yet. Use the buttons above to upload exclusive photos and videos!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(adminMediaList) { media ->
                AdminMediaCard(
                    media = media,
                    onPlayVideo = { activeVideoDialog = media },
                    onClickPhoto = { activePhotoDialog = media },
                    onDelete = {
                        coroutineScope.launch {
                            dao.deleteAdminMedia(media)
                            dao.insertAuditLog(
                                AuditLogEntity(
                                    action = "ADMIN_MEDIA_DELETED",
                                    actorRole = "ADMIN",
                                    severity = "INFO",
                                    details = "Deleted ${media.mediaType}: ${media.title}"
                                )
                            )
                            Toast.makeText(context, "Media item removed", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        // Global Customization & Credentials Suite
        item {
            Text(
                text = "PLATFORM CONFIGURATION & CREDENTIALS",
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Global Content Settings",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = editFriendName,
                        onValueChange = { editFriendName = it },
                        label = { Text("Best Friend Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editHeadline,
                        onValueChange = { editHeadline = it },
                        label = { Text("App Greeting Headline") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editQuote,
                        onValueChange = { editQuote = it },
                        label = { Text("Featured Friendship Quote") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Target Birthday Milestone",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Current Target: Asoj 7, 2083 BS (September 23, 2026, 00:00:00 NPT)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        editBirthdayDateMillis = 1790186700000L // Asoj 7, 2083 BS (Sep 23, 2026)
                                        Toast.makeText(context, "Set to Asoj 7 (September 23)", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Set Asoj 7 (Sep 23)", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Admin Credentials Management",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = editAdminUsername,
                        onValueChange = { editAdminUsername = it },
                        label = { Text("Admin ID / Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newAdminPassword,
                        onValueChange = { newAdminPassword = it },
                        label = { Text("New Password (Optional)") },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val updatedPasswordHash = if (newAdminPassword.isNotBlank()) {
                                    EncryptionHelper.hashString(newAdminPassword)
                                } else {
                                    settings.adminPasswordHash
                                }

                                val updated = settings.copy(
                                    bestFriendName = editFriendName,
                                    greetingHeadline = editHeadline,
                                    specialQuote = editQuote,
                                    birthdayDateMillis = editBirthdayDateMillis,
                                    adminUsername = editAdminUsername.trim(),
                                    adminPasswordHash = updatedPasswordHash
                                )
                                dao.saveSettings(updated)
                                dao.insertAuditLog(
                                    AuditLogEntity(
                                        action = "ADMIN_SETTINGS_UPDATED",
                                        actorRole = "ADMIN",
                                        severity = "SECURE",
                                        details = "Administrator updated global configuration and credentials."
                                    )
                                )
                                newAdminPassword = ""
                                Toast.makeText(context, "Configuration updated successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Configuration Across Platform", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Security Audit Logs Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SECURITY AUDIT LOGS (${auditLogs.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = {
                        val report = auditLogs.joinToString("\n") {
                            "[${dateFormat.format(Date(it.timestamp))}] [${it.severity}] [${it.actorRole}] ${it.action}: ${it.details}"
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Audit Log Report", report)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Audit logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export Logs", fontSize = 12.sp)
                }
            }
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (log.severity) {
                                "SECURE" -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                                "WARN" -> Color(0xFFE65100).copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        ) {
                            Text(
                                text = "${log.severity} • ${log.actorRole}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (log.severity) {
                                    "SECURE" -> Color(0xFF2E7D32)
                                    "WARN" -> Color(0xFFE65100)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = log.action,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = log.details,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    // Media Upload Detail Confirmation Dialog
    if (showUploadModal && pendingMediaUri != null) {
        var uploadTitle by remember {
            mutableStateOf(if (pendingMediaType == "VIDEO") "Special Birthday Video" else "Exclusive Birthday Photo")
        }
        var uploadCaption by remember { mutableStateOf("") }
        var syncToMemories by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = {
                showUploadModal = false
                pendingMediaUri = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (pendingMediaType == "VIDEO") Icons.Default.Videocam else Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Media Upload (${pendingMediaType})", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (pendingMediaType == "PHOTO") {
                        AsyncImage(
                            model = pendingMediaUri,
                            contentDescription = "Selected Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF1E1E2C), Color(0xFF2D1436)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Video Ready to Publish", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uploadTitle,
                        onValueChange = { uploadTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uploadCaption,
                        onValueChange = { uploadCaption = it },
                        label = { Text("Caption / Message") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = syncToMemories,
                            onCheckedChange = { syncToMemories = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Publish to Memories Timeline for ${settings.bestFriendName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dateString = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                        val entity = AdminMediaEntity(
                            mediaType = pendingMediaType,
                            title = uploadTitle.ifBlank { "Birthday $pendingMediaType" },
                            caption = uploadCaption,
                            mediaUri = pendingMediaUri.toString(),
                            dateStr = dateString,
                            isSharedToMemories = syncToMemories
                        )

                        coroutineScope.launch {
                            dao.insertAdminMedia(entity)
                            if (syncToMemories) {
                                dao.insertMemory(
                                    MemoryEntity(
                                        title = entity.title,
                                        dateStr = dateString,
                                        description = if (uploadCaption.isNotBlank()) uploadCaption else "Special birthday ${pendingMediaType.lowercase()} uploaded by admin!",
                                        tags = if (pendingMediaType == "VIDEO") "#video #exclusive #birthday" else "#photo #special #bff",
                                        platformSource = "Admin Upload",
                                        photoUri = pendingMediaUri.toString(),
                                        mediaType = pendingMediaType,
                                        isFavorite = true
                                    )
                                )
                            }
                            dao.insertAuditLog(
                                AuditLogEntity(
                                    action = "ADMIN_MEDIA_UPLOADED",
                                    actorRole = "ADMIN",
                                    severity = "INFO",
                                    details = "Uploaded $pendingMediaType: ${entity.title}"
                                )
                            )
                            showUploadModal = false
                            pendingMediaUri = null
                            Toast.makeText(context, "$pendingMediaType uploaded successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Publish Media")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showUploadModal = false
                        pendingMediaUri = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Active Media Lightboxes
    activeVideoDialog?.let { video ->
        VideoPlayerDialog(
            title = video.title,
            caption = video.caption,
            dateStr = video.dateStr,
            videoUri = video.mediaUri,
            onDismiss = { activeVideoDialog = null }
        )
    }

    activePhotoDialog?.let { photo ->
        PhotoLightboxDialog(
            title = photo.title,
            caption = photo.caption,
            dateStr = photo.dateStr,
            photoUri = photo.mediaUri,
            onDismiss = { activePhotoDialog = null }
        )
    }
}

@Composable
fun AdminMediaCard(
    media: AdminMediaEntity,
    onPlayVideo: () -> Unit,
    onClickPhoto: () -> Unit = {},
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (media.mediaType == "VIDEO") Color(0xFF673AB7).copy(alpha = 0.15f) else Color(0xFFE91E63).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (media.mediaType == "VIDEO") Icons.Default.Videocam else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (media.mediaType == "VIDEO") Color(0xFF673AB7) else Color(0xFFE91E63),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${media.mediaType} • ${media.dateStr}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (media.mediaType == "VIDEO") Color(0xFF673AB7) else Color(0xFFE91E63)
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Media", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = media.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (media.caption.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = media.caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (media.mediaType == "PHOTO" && media.mediaUri.isNotBlank()) {
                AsyncImage(
                    model = media.mediaUri,
                    contentDescription = media.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClickPhoto() },
                    contentScale = ContentScale.Crop
                )
            } else if (media.mediaType == "VIDEO") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1B1B2F), Color(0xFF162447), Color(0xFF1F4068))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onPlayVideo,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play / Preview Video", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
