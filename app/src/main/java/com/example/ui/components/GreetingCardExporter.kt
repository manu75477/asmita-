package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object GreetingCardExporter {

    fun generateAndShareCard(
        context: Context,
        recipientName: String = "Asmita Yadav",
        headline: String = "Happy Birthday!",
        message: String,
        frameName: String = "Golden Sparkle",
        sticker: String = "🎂",
        senderName: String = "Your Best Friend"
    ) {
        try {
            val width = 1080
            val height = 1350
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Background gradient / solid
            val bgPaint = Paint().apply {
                isAntiAlias = true
                color = when (frameName) {
                    "Golden Sparkle" -> AndroidColor.parseColor("#1B1226")
                    "Floral Garland" -> AndroidColor.parseColor("#FFF0F5")
                    "Polaroid Memories" -> AndroidColor.parseColor("#FAF9F6")
                    "Confetti Fiesta" -> AndroidColor.parseColor("#161329")
                    "Neon Radiance" -> AndroidColor.parseColor("#090812")
                    else -> AndroidColor.parseColor("#FFF5F7")
                }
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // Outer Frame Border
            val borderPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 36f
                color = when (frameName) {
                    "Golden Sparkle" -> AndroidColor.parseColor("#FFD700")
                    "Floral Garland" -> AndroidColor.parseColor("#F48FB1")
                    "Polaroid Memories" -> AndroidColor.parseColor("#E0E0E0")
                    "Confetti Fiesta" -> AndroidColor.parseColor("#00E5FF")
                    "Neon Radiance" -> AndroidColor.parseColor("#D500F9")
                    else -> AndroidColor.parseColor("#FF4081")
                }
            }
            canvas.drawRoundRect(RectF(30f, 30f, width - 30f, height - 30f), 48f, 48f, borderPaint)

            // Inner Accent Border
            val innerBorderPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 6f
                color = when (frameName) {
                    "Golden Sparkle" -> AndroidColor.parseColor("#FFA000")
                    "Floral Garland" -> AndroidColor.parseColor("#E91E63")
                    "Polaroid Memories" -> AndroidColor.parseColor("#9E9E9E")
                    "Confetti Fiesta" -> AndroidColor.parseColor("#FFD740")
                    "Neon Radiance" -> AndroidColor.parseColor("#00E5FF")
                    else -> AndroidColor.parseColor("#F50057")
                }
            }
            canvas.drawRoundRect(RectF(60f, 60f, width - 60f, height - 60f), 36f, 36f, innerBorderPaint)

            // Top Decorative Sticker / Icon
            val stickerPaint = Paint().apply {
                isAntiAlias = true
                textSize = 110f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(sticker, width / 2f, 180f, stickerPaint)

            // Headline Paint
            val headlinePaint = Paint().apply {
                isAntiAlias = true
                textSize = 64f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
                color = when (frameName) {
                    "Floral Garland", "Polaroid Memories", "Love & Bestie" -> AndroidColor.parseColor("#3E2723")
                    else -> AndroidColor.parseColor("#FFFFFF")
                }
            }
            canvas.drawText(headline, width / 2f, 280f, headlinePaint)

            // Recipient Subtitle
            val recipientPaint = Paint().apply {
                isAntiAlias = true
                textSize = 50f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
                color = when (frameName) {
                    "Golden Sparkle" -> AndroidColor.parseColor("#FFD700")
                    "Floral Garland", "Polaroid Memories", "Love & Bestie" -> AndroidColor.parseColor("#C2185B")
                    "Confetti Fiesta" -> AndroidColor.parseColor("#FFD740")
                    "Neon Radiance" -> AndroidColor.parseColor("#00E5FF")
                    else -> AndroidColor.parseColor("#FF80AB")
                }
            }
            canvas.drawText("✨ For My Best Friend, $recipientName ✨", width / 2f, 360f, recipientPaint)

            // Decorative Divider Line
            val dividerPaint = Paint().apply {
                isAntiAlias = true
                strokeWidth = 4f
                color = recipientPaint.color
            }
            canvas.drawLine(width / 4f, 400f, (width * 3f) / 4f, 400f, dividerPaint)

            // Message Body Text (Wrapped lines)
            val bodyPaint = Paint().apply {
                isAntiAlias = true
                textSize = 38f
                color = when (frameName) {
                    "Floral Garland", "Polaroid Memories", "Love & Bestie" -> AndroidColor.parseColor("#212121")
                    else -> AndroidColor.parseColor("#F5F5F5")
                }
            }

            val margin = 120f
            val maxLineWidth = width - (margin * 2)
            val lines = mutableListOf<String>()

            // Simple line-wrapping
            val words = message.replace("\n", " \n ").split(" ")
            var currentLine = ""
            for (word in words) {
                if (word == "\n") {
                    lines.add(currentLine)
                    currentLine = ""
                    continue
                }
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (bodyPaint.measureText(testLine) < maxLineWidth) {
                    currentLine = testLine
                } else {
                    lines.add(currentLine)
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }

            var startY = 480f
            val lineHeight = 56f
            for (line in lines.take(13)) {
                canvas.drawText(line, margin, startY, bodyPaint)
                startY += lineHeight
            }

            // Bottom Signature
            val signPaint = Paint().apply {
                isAntiAlias = true
                textSize = 42f
                isFakeBoldText = true
                textAlign = Paint.Align.RIGHT
                color = recipientPaint.color
            }
            canvas.drawText("With Endless Love & Friendship,", width - margin, height - 160f, signPaint)
            canvas.drawText("💖 $senderName", width - margin, height - 100f, signPaint)

            // Save to Cache file
            val cachePath = File(context.cacheDir, "cards")
            cachePath.mkdirs()
            val cardFile = File(cachePath, "birthday_card_asmita_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(cardFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            // FileProvider uri
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cardFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Happy Birthday $recipientName! 🎉")
                putExtra(Intent.EXTRA_TEXT, "✨ Wishing $recipientName a very Happy Birthday! Here is your custom greeting card! ✨\n\n$message")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Greeting Card for $recipientName")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback: Share pure text if image generation or FileProvider isn't accessible
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Happy Birthday $recipientName! 🎉")
                putExtra(Intent.EXTRA_TEXT, "🎂 $headline\n✨ For $recipientName ✨\n\n$message\n\n💖 - $senderName")
            }
            val chooser = Intent.createChooser(shareIntent, "Share Birthday Wish")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }
}
