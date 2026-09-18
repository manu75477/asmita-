package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BirthdayDao
import com.example.data.SavedCardEntity
import com.example.ui.components.CustomPhotoFrame
import com.example.ui.components.FrameStyle
import com.example.ui.components.GreetingCardExporter
import kotlinx.coroutines.launch

@Composable
fun PhotoFrameStudioScreen(
    dao: BirthdayDao,
    recipientName: String = "Asmita Yadav",
    initialMessage: String = ""
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedFrame by remember { mutableStateOf(FrameStyle.GOLDEN) }
    var selectedSticker by remember { mutableStateOf("🎂") }
    var cardHeadline by remember { mutableStateOf("Happy Birthday, Asmita!") }
    var cardMessage by remember {
        mutableStateOf(
            if (initialMessage.isNotBlank()) initialMessage
            else "To the most wonderful, loyal, and joyous best friend in the world! Thank you for lighting up every moment with your smile. Wishing you an extraordinary year ahead!"
        )
    }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Modern Android Photo Picker (zero broad storage permission!)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedPhotoUri = uri
            }
        }
    )

    val stickers = listOf("🎂", "👑", "💖", "🎁", "✨", "🥂", "🎉", "🌸", "🌟")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "PHOTO FRAME STUDIO",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Design a luxury custom greeting card for $recipientName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Card & Frame Live Interactive Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                CustomPhotoFrame(
                    frameStyle = selectedFrame,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top Sticker & Recipient
                        Text(text = selectedSticker, fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cardHeadline,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "✨ For $recipientName ✨",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Photo or Default Hero Image
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedPhotoUri != null) {
                                AsyncImage(
                                    model = selectedPhotoUri,
                                    contentDescription = "Selected Photo of $recipientName",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.hero_birthday_banner),
                                    contentDescription = "Birthday Celebratory Art",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Heartfelt Message Snippet
                        Text(
                            text = cardMessage,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Frame Selector
        item {
            Text(
                text = "CHOOSE FRAME STYLE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FrameStyle.values()) { frame ->
                    FilterChip(
                        selected = selectedFrame == frame,
                        onClick = { selectedFrame = frame },
                        label = { Text(frame.displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }

        // Sticker Badges Selector
        item {
            Text(
                text = "CELEBRATORY STICKER",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(stickers) { sticker ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedSticker == sticker) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (selectedSticker == sticker) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable { selectedSticker = sticker },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = sticker, fontSize = 22.sp)
                    }
                }
            }
        }

        // Headline & Message Editing
        item {
            OutlinedTextField(
                value = cardHeadline,
                onValueChange = { cardHeadline = it },
                label = { Text("Card Headline") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            OutlinedTextField(
                value = cardMessage,
                onValueChange = { cardMessage = it },
                label = { Text("Heartfelt Birthday Message for $recipientName") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Export & Save Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        GreetingCardExporter.generateAndShareCard(
                            context = context,
                            recipientName = recipientName,
                            headline = cardHeadline,
                            message = cardMessage,
                            frameName = selectedFrame.displayName,
                            sticker = selectedSticker
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export & Share", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            dao.insertCard(
                                SavedCardEntity(
                                    recipientName = recipientName,
                                    message = cardMessage,
                                    frameStyle = selectedFrame.displayName,
                                    themeColorHex = "#673AB7",
                                    sticker = selectedSticker,
                                    photoUri = selectedPhotoUri?.toString() ?: ""
                                )
                            )
                            Toast.makeText(context, "Card saved to collection!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
