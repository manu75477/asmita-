package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateBirthdayWish(
        friendName: String = "Asmita Yadav",
        tone: String = "Heartfelt & Emotional",
        memoryHighlight: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineWish(friendName, tone, memoryHighlight)
        }

        val prompt = "Write a beautiful, personalized, high-emotion birthday wish for my best friend named $friendName. " +
                "Tone: $tone. " +
                (if (memoryHighlight.isNotBlank()) "Include or reference this memory: $memoryHighlight. " else "") +
                "Keep it warm, memorable, sincere, and ready to write in a luxury birthday card. Do not add markdown headers."

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                return@withContext getOfflineWish(friendName, tone, memoryHighlight)
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                getOfflineWish(friendName, tone, memoryHighlight)
            }
        } catch (e: Exception) {
            getOfflineWish(friendName, tone, memoryHighlight)
        }
    }

    private fun getOfflineWish(name: String, tone: String, highlight: String): String {
        return when (tone) {
            "Heartfelt & Emotional" -> {
                "Dearest $name,\n\nHappy Birthday to the one who has seen me at my best and stood by me at my absolute worst. Having you as my best friend is one of the greatest gifts life has ever given me. Your kindness, your infectious smile, and your gentle soul illuminate every single room you walk into.\n\nMay this year shower you with boundless laughter, flourishing health, and all the dreams your pure heart desires. Here is to a lifetime of unbreakable friendship! Cheers to you, my favorite human!"
            }
            "Funny & Teasing" -> {
                "Happy Birthday, $name!\n\nAnother year older, slightly wiser, but still just as gloriously crazy as the day we met! Thank you for being the partner-in-crime who never judges my weirdness because you're right there doing it with me. Here's to eating way too much cake today, pretending we have our lives completely figured out, and remaining best friends forever—because you already know way too many of my secrets!"
            }
            "Nostalgic Bestie" -> {
                "Happy Birthday, $name!\n\nLooking back at every road trip, late-night cafe session, and every moment we burst out laughing until tears streamed down our faces—I am reminded of how lucky I am. " +
                        (if (highlight.isNotBlank()) "Especially remembering '$highlight' brings the warmest smile to my face. " else "") +
                        "Time flies, seasons change, but our bond remains timeless. Wishing you the most magical birthday filled with boundless joy!"
            }
            "Poetic & Elegant" -> {
                "To $name, on your special day:\n\nMay the stars light your path with grace, and may every morning greet you with tranquility and wonder. You bring golden warmth into this world simply by being yourself. Happy Birthday to an extraordinary best friend whose spirit shines brighter with every passing year."
            }
            "10 Reasons You're The Best" -> {
                "Happy Birthday $name! Top reasons why you are the greatest best friend:\n1. Your unmatched loyalty\n2. The way you make everyone laugh\n3. Our late-night chats\n4. Your huge heart\n5. Always believing in me\n6. The best travel companion\n7. You never judge my bad choices\n8. Sharing snacks without complaints\n9. Your beautiful optimism\n10. Simply being YOU! Have the best birthday!"
            }
            else -> {
                "Happy Birthday to my dearest best friend $name! Wishing you a day as brilliant, delightful, and genuinely wonderful as you are. Cheers to another fabulous trip around the sun!"
            }
        }
    }
}
