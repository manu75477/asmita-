package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateStr: String,
    val description: String,
    val photoUri: String = "",
    val mediaType: String = "PHOTO", // "PHOTO" or "VIDEO"
    val tags: String = "#bestfriend",
    val platformSource: String = "Instagram", // Instagram, Facebook, Google Photos, Camera
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetTimeMillis: Long,
    val category: String, // Midnight Wish, Gift Surprise, Celebration Party, Video Call
    val notes: String = "",
    val isCompleted: Boolean = false,
    val isNotified: Boolean = false
)

@Entity(tableName = "saved_cards")
data class SavedCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientName: String,
    val message: String,
    val frameStyle: String, // Golden, Floral, Polaroid, Confetti, Neon, Hearts
    val themeColorHex: String,
    val sticker: String,
    val photoUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String,
    val actorRole: String, // ADMIN, USER, SYSTEM
    val severity: String,  // INFO, WARN, SECURE
    val details: String
)

@Entity(tableName = "admin_media")
data class AdminMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mediaType: String, // "PHOTO" or "VIDEO"
    val title: String,
    val caption: String = "",
    val mediaUri: String = "",
    val dateStr: String = "",
    val isSharedToMemories: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val bestFriendName: String = "Asmita Yadav",
    val birthdayDateMillis: Long = 1790186700000L, // Target birthday milestone: Asoj 7 (September 23, 2026, 00:00:00 NPT)
    val greetingHeadline: String = "Happy Birthday, Dearest Asmita!",
    val specialQuote: String = "True friends are never apart, maybe in distance but never in heart.",
    val isDarkMode: Boolean = false,
    val isHighContrast: Boolean = false,
    val largeFontEnabled: Boolean = false,
    val cloudSyncEnabled: Boolean = true,
    val adminUsername: String = "admin",
    val adminPasswordHash: String = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918",
    val adminPinHash: String = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918",
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
