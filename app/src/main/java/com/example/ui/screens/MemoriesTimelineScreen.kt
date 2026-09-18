package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AdminMediaEntity
import com.example.data.AuditLogEntity
import com.example.data.BirthdayDao
import com.example.data.MemoryEntity
import com.example.ui.components.PhotoLightboxDialog
import com.example.ui.components.VideoPlayerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoriesTimelineScreen(
    dao: BirthdayDao,
    friendName: String = "Asmita Yadav"
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTagFilter by remember { mutableStateOf("All") }
    var isSyncingSocial by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Media Dialog States
    var activeVideoData by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    var activePhotoData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val adminMediaList by dao.getAllAdminMedia().collectAsState(initial = emptyList())

    val allMemories by if (searchQuery.isBlank()) {
        dao.getAllMemories().collectAsState(initial = emptyList())
    } else {
        dao.searchMemories(searchQuery).collectAsState(initial = emptyList())
    }

    val filteredMemories = remember(allMemories, selectedTagFilter) {
        if (selectedTagFilter == "All") allMemories
        else if (selectedTagFilter == "Favorites") allMemories.filter { it.isFavorite }
        else if (selectedTagFilter == "Videos") allMemories.filter { it.mediaType == "VIDEO" }
        else if (selectedTagFilter == "Photos") allMemories.filter { it.mediaType == "PHOTO" || it.photoUri.isNotBlank() }
        else allMemories.filter { it.tags.contains(selectedTagFilter, ignoreCase = true) }
    }

    val tags = listOf("All", "Favorites", "Videos", "Photos", "#bestfriends", "#roadtrip", "#coffee", "#college")

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "MEMORIES WITH ${friendName.uppercase()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Relive cherished milestones and sync memories from Instagram, Facebook & Photos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // AI-Driven Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("AI Search across memories, stories, tags...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Social Media Sync Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Social Media Sync",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Pull shared stories from Instagram, Google Photos & Facebook",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncingSocial = true
                                    delay(1800L) // simulated secure OAuth sync
                                    dao.insertMemory(
                                        MemoryEntity(
                                            title = "Instagram Throwback Reel",
                                            dateStr = "Synced Just Now",
                                            description = "Instagram Memory: The unforgettable weekend festival where we danced in the rain with Asmita!",
                                            tags = "#instagram #memories #bestfriends",
                                            platformSource = "Instagram",
                                            isFavorite = true
                                        )
                                    )
                                    dao.insertAuditLog(
                                        AuditLogEntity(
                                            action = "SOCIAL_SYNC_SUCCESS",
                                            actorRole = "USER",
                                            severity = "INFO",
                                            details = "Successfully synced Instagram shared memories with Asmita Yadav."
                                        )
                                    )
                                    isSyncingSocial = false
                                    Toast.makeText(context, "Synced 1 new shared memory from Instagram!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSyncingSocial
                        ) {
                            if (isSyncingSocial) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync")
                            }
                        }
                    }
                }
            }

            // Exclusive Birthday Media Showcase (Videos & Photos)
            if (adminMediaList.isNotEmpty()) {
                item {
                    Text(
                        text = "EXCLUSIVE BIRTHDAY MEDIA (VIDEOS & PHOTOS)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(adminMediaList) { media ->
                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clickable {
                                        if (media.mediaType == "VIDEO") {
                                            activeVideoData = Triple(media.title, media.caption, media.mediaUri)
                                        } else {
                                            activePhotoData = Triple(media.title, media.caption, media.mediaUri)
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (media.mediaType == "VIDEO") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFF673AB7), Color(0xFFE91E63))
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Video",
                                                tint = Color.White,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    } else {
                                        if (media.mediaUri.isNotBlank()) {
                                            AsyncImage(
                                                model = media.mediaUri,
                                                contentDescription = media.title,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(110.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(110.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFFFF4081).copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AddPhotoAlternate,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFF4081)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = media.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${media.mediaType} • Tap to view",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tag Filters
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tags) { tag ->
                        FilterChip(
                            selected = selectedTagFilter == tag,
                            onClick = { selectedTagFilter = tag },
                            label = { Text(tag) }
                        )
                    }
                }
            }

            // Timeline Items
            if (filteredMemories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No memories match your query. Add your first memory with $friendName!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredMemories) { memory ->
                    MemoryCard(
                        memory = memory,
                        onClickMedia = {
                            if (memory.mediaType == "VIDEO") {
                                activeVideoData = Triple(memory.title, memory.description, memory.photoUri)
                            } else {
                                activePhotoData = Triple(memory.title, memory.description, memory.photoUri)
                            }
                        },
                        onToggleFavorite = {
                            coroutineScope.launch {
                                dao.insertMemory(memory.copy(isFavorite = !memory.isFavorite))
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                dao.deleteMemory(memory)
                                Toast.makeText(context, "Memory removed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }

        // Floating Action Button to Add Memory
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Memory")
        }

        if (showAddDialog) {
            AddMemoryDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { title, date, desc, tagsStr, platform, uri, mediaType ->
                    coroutineScope.launch {
                        dao.insertMemory(
                            MemoryEntity(
                                title = title,
                                dateStr = date,
                                description = desc,
                                tags = tagsStr,
                                platformSource = platform,
                                photoUri = uri,
                                mediaType = mediaType
                            )
                        )
                        showAddDialog = false
                        Toast.makeText(context, "Memory added!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Media Dialogs
        activeVideoData?.let { (title, desc, uri) ->
            VideoPlayerDialog(
                title = title,
                caption = desc,
                dateStr = "Special Video Tribute",
                videoUri = uri,
                onDismiss = { activeVideoData = null }
            )
        }

        activePhotoData?.let { (title, desc, uri) ->
            PhotoLightboxDialog(
                title = title,
                caption = desc,
                dateStr = "Cherished Memory",
                photoUri = uri,
                onDismiss = { activePhotoData = null }
            )
        }
    }
}

@Composable
fun MemoryCard(
    memory: MemoryEntity,
    onClickMedia: () -> Unit = {},
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (memory.platformSource) {
                        "Instagram" -> Color(0xFFE1306C).copy(alpha = 0.15f)
                        "Google Photos" -> Color(0xFF4285F4).copy(alpha = 0.15f)
                        "Facebook" -> Color(0xFF1877F2).copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        text = "${memory.platformSource} • ${memory.dateStr}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (memory.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Memory",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = memory.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = memory.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (memory.mediaType == "VIDEO") {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1B1B2F), Color(0xFF162447), Color(0xFF673AB7))
                            )
                        )
                        .clickable { onClickMedia() },
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onClickMedia,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play Video Tribute", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else if (memory.photoUri.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = memory.photoUri,
                    contentDescription = memory.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClickMedia() },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = memory.tags,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, date: String, desc: String, tags: String, platform: String, uri: String, mediaType: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Today") }
    var desc by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("#bestfriend #memories") }
    var platform by remember { mutableStateOf("Instagram") }
    var selectedMediaType by remember { mutableStateOf("PHOTO") }
    var mediaUriString by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                mediaUriString = uri.toString()
            }
        }
    )

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                mediaUriString = uri.toString()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Shared Memory", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Media Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedMediaType == "PHOTO",
                        onClick = {
                            selectedMediaType = "PHOTO"
                            mediaUriString = ""
                        },
                        label = { Text("Photo Memory") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedMediaType == "VIDEO",
                        onClick = {
                            selectedMediaType = "VIDEO"
                            mediaUriString = ""
                        },
                        label = { Text("Video Memory") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Memory Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date / Time") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("What made this moment unforgettable?") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (e.g. #bff #party)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedMediaType == "PHOTO") {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (mediaUriString.isNotBlank()) "Photo Attached" else "Attach Photo")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (mediaUriString.isNotBlank()) "Video Attached" else "Attach Video")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && desc.isNotBlank()) {
                        onAdd(title, date, desc, tags, platform, mediaUriString, selectedMediaType)
                    }
                }
            ) {
                Text("Save Memory")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
