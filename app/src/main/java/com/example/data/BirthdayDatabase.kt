package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MemoryEntity::class,
        ReminderEntity::class,
        SavedCardEntity::class,
        AuditLogEntity::class,
        AdminMediaEntity::class,
        AppSettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BirthdayDatabase : RoomDatabase() {
    abstract fun birthdayDao(): BirthdayDao

    companion object {
        @Volatile
        private var INSTANCE: BirthdayDatabase? = null

        fun getInstance(context: Context): BirthdayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirthdayDatabase::class.java,
                    "birthday_wish_db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).birthdayDao()
                            // Seed default AppSettings
                            dao.saveSettings(
                                AppSettingsEntity(
                                    id = 1,
                                    bestFriendName = "Asmita Yadav",
                                    birthdayDateMillis = 1790186700000L, // Asoj 7, 2083 BS (September 23, 2026, 00:00:00 NPT)
                                    greetingHeadline = "Happy Birthday, Dearest Asmita!",
                                    specialQuote = "To the world you may just be one person, but to me you are the best friend ever!",
                                    isDarkMode = false,
                                    isHighContrast = false,
                                    largeFontEnabled = false,
                                    cloudSyncEnabled = true,
                                    adminUsername = "admin",
                                    adminPasswordHash = EncryptionHelper.hashString("Admin@2026"),
                                    adminPinHash = EncryptionHelper.hashPin("2026")
                                )
                            )

                            // Seed pre-loaded memories for Asmita Yadav
                            dao.insertMemory(
                                MemoryEntity(
                                    title = "First Coffee & Endless Laughs",
                                    dateStr = "August 14",
                                    description = "The day we sat at the cafe for four hours talking about our biggest dreams, laughing until our stomachs hurt. Unbreakable bond formed!",
                                    tags = "#bestfriends #coffee #laughter",
                                    platformSource = "Instagram",
                                    isFavorite = true
                                )
                            )
                            dao.insertMemory(
                                MemoryEntity(
                                    title = "The Spontaneous Sunset Roadtrip",
                                    dateStr = "October 22",
                                    description = "Blasting our favorite playlist in the car, singing loudly at the top of our lungs while chasing the golden hour sunset!",
                                    tags = "#roadtrip #sunset #vibes",
                                    platformSource = "Google Photos",
                                    isFavorite = true
                                )
                            )
                            dao.insertMemory(
                                MemoryEntity(
                                    title = "College Project All-Nighter",
                                    dateStr = "March 18",
                                    description = "Staying up with cold pizza and snacks, turning a stressful deadline into one of the most memorable nights ever.",
                                    tags = "#college #memories #bff",
                                    platformSource = "Facebook",
                                    mediaType = "PHOTO",
                                    isFavorite = false
                                )
                            )
                            dao.insertMemory(
                                MemoryEntity(
                                    title = "Surprise Birthday Video Montage 🎥",
                                    dateStr = "Special Birthday Edition",
                                    description = "A heartwarming video compilation created especially for Asmita with sweetest birthday wishes and candid laughter!",
                                    tags = "#video #birthday #surprise #bff",
                                    platformSource = "Admin Upload",
                                    mediaType = "VIDEO",
                                    isFavorite = true
                                )
                            )

                            // Seed scheduled reminders
                            val now = System.currentTimeMillis()
                            dao.insertReminder(
                                ReminderEntity(
                                    title = "Midnight Birthday Wish (12:00 AM)",
                                    targetTimeMillis = now + 86400000L,
                                    category = "Midnight Wish",
                                    notes = "Be the very first to call and wish Asmita a joyful birthday!"
                                )
                            )
                            dao.insertReminder(
                                ReminderEntity(
                                    title = "Order Customized Flower Bouquet & Cake",
                                    targetTimeMillis = now + (2 * 86400000L),
                                    category = "Gift Surprise",
                                    notes = "Order Asmita's favorite red velvet cake and fresh lilies."
                                )
                            )
                            dao.insertReminder(
                                ReminderEntity(
                                    title = "Host Secret Surprise Birthday Gathering",
                                    targetTimeMillis = now + (5 * 86400000L),
                                    category = "Celebration Party",
                                    notes = "Gather close friends, prepare photo cards and party poppers!"
                                )
                            )

                            // Seed admin media showcase
                            dao.insertAdminMedia(
                                AdminMediaEntity(
                                    mediaType = "PHOTO",
                                    title = "Best Friend Golden Memories",
                                    caption = "Exclusive curated birthday album snapshot celebrating Asmita!",
                                    mediaUri = "",
                                    dateStr = "Special Birthday Edition",
                                    isSharedToMemories = true
                                )
                            )
                            dao.insertAdminMedia(
                                AdminMediaEntity(
                                    mediaType = "VIDEO",
                                    title = "Surprise Birthday Video Montage",
                                    caption = "Compilation video of heartfelt wishes from everyone who loves Asmita.",
                                    mediaUri = "",
                                    dateStr = "Surprise Video Highlight",
                                    isSharedToMemories = true
                                )
                            )

                            // Seed initial audit log
                            dao.insertAuditLog(
                                AuditLogEntity(
                                    action = "DATABASE_INITIALIZED",
                                    actorRole = "SYSTEM",
                                    severity = "INFO",
                                    details = "Encrypted local SQLite database initialized with default friendship profile for Asmita Yadav."
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
