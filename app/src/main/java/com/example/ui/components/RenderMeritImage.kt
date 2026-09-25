package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.LruCache
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * High-performance video thumbnail loader with 2-tier caching:
 * 1. Ultra-fast in-memory LruCache
 * 2. Instant companion disk thumbnail (.thumb.jpg)
 * 3. Fallback background extraction via MediaMetadataRetriever
 */
object VideoThumbnailHelper {
    val memoryCache = object : LruCache<String, Bitmap>(50) {}

    suspend fun getThumbnail(context: Context, uriOrPath: String): Bitmap? = withContext(Dispatchers.IO) {
        if (uriOrPath.isBlank()) return@withContext null

        // Tier 1: In-memory cache hit (0ms latency)
        memoryCache.get(uriOrPath)?.let { return@withContext it }

        // Tier 2: Pre-generated companion disk thumbnail (.thumb.jpg)
        val file = File(uriOrPath)
        val thumbFile = if (file.exists()) File("${uriOrPath}.thumb.jpg") else null
        if (thumbFile?.exists() == true) {
            try {
                val decoded = BitmapFactory.decodeFile(thumbFile.absolutePath)
                if (decoded != null) {
                    memoryCache.put(uriOrPath, decoded)
                    return@withContext decoded
                }
            } catch (_: Exception) {}
        }

        // Tier 3: Extract frame asynchronously on IO thread
        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever()
            if (uriOrPath.startsWith("/") || file.exists()) {
                retriever.setDataSource(uriOrPath)
            } else {
                retriever.setDataSource(context, Uri.parse(uriOrPath))
            }
            val frame = retriever.getFrameAtTime(500_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime
            if (frame != null) {
                memoryCache.put(uriOrPath, frame)
                // Cache to companion file if accessible
                if (thumbFile != null && !thumbFile.exists()) {
                    try {
                        FileOutputStream(thumbFile).use { out ->
                            frame.compress(Bitmap.CompressFormat.JPEG, 85, out)
                        }
                    } catch (_: Exception) {}
                }
                return@withContext frame
            }
        } catch (_: Exception) {
        } finally {
            try {
                retriever?.release()
            } catch (_: Exception) {}
        }
        null
    }

    suspend fun getVideoDuration(context: Context, uriOrPath: String): String? = withContext(Dispatchers.IO) {
        if (uriOrPath.isBlank()) return@withContext null
        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever()
            val file = File(uriOrPath)
            if (uriOrPath.startsWith("/") || file.exists()) {
                retriever.setDataSource(uriOrPath)
            } else {
                retriever.setDataSource(context, Uri.parse(uriOrPath))
            }
            val durStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durMs = durStr?.toLongOrNull() ?: 0L
            if (durMs > 0) {
                val totalSec = durMs / 1000
                val m = totalSec / 60
                val s = totalSec % 60
                return@withContext String.format(Locale.getDefault(), "%d:%02d", m, s)
            }
        } catch (_: Exception) {
        } finally {
            try {
                retriever?.release()
            } catch (_: Exception) {}
        }
        null
    }
}

/**
 * Dedicated, lag-free Video Thumbnail preview for feeds and lists.
 * Renders an instantaneous poster frame, central play button, and duration indicator.
 */
@Composable
fun VideoThumbnailView(
    videoUriOrPath: String,
    modifier: Modifier = Modifier,
    onPlayClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var thumbnailBitmap by remember(videoUriOrPath) {
        mutableStateOf(VideoThumbnailHelper.memoryCache.get(videoUriOrPath))
    }
    var durationString by remember(videoUriOrPath) { mutableStateOf<String?>(null) }

    LaunchedEffect(videoUriOrPath) {
        if (thumbnailBitmap == null) {
            val bmp = VideoThumbnailHelper.getThumbnail(context, videoUriOrPath)
            if (bmp != null) {
                thumbnailBitmap = bmp
            }
        }
        val dur = VideoThumbnailHelper.getVideoDuration(context, videoUriOrPath)
        if (dur != null) {
            durationString = dur
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFF140D0B))
            .then(
                if (onPlayClick != null) Modifier.clickable(onClick = onPlayClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // 1. Video Thumbnail Image or Placeholder
        val bmp = thumbnailBitmap
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF241510),
                                Color(0xFF180E0B),
                                Color(0xFF0F0806)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // 2. Cinematic Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.30f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.50f)
                        )
                    )
                )
        )

        // 3. Central Glassmorphic Play Button
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.62f),
            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.85f)),
            shadowElevation = 8.dp,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play Video",
                    tint = Color.White,
                    modifier = Modifier
                        .size(34.dp)
                        .padding(start = 2.dp)
                )
            }
        }

        // 4. Video Badge & Duration Pill (Top-Right)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.70f),
            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.25f)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = durationString ?: "VIDEO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Universal media renderer for merit posts and Bodhi leaves.
 * Smoothly resolves photos, camera captures, video thumbnails with play indicators, and motifs.
 */
@Composable
fun RenderMeritImage(
    imageUri: String?,
    defaultDrawableRes: Int = 0,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    showVideoBadge: Boolean = true
) {
    val context = LocalContext.current

    val isVideo = remember(imageUri) {
        if (imageUri.isNullOrBlank()) false
        else {
            val lower = imageUri.lowercase()
            lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                    lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
        }
    }

    if (isVideo && !imageUri.isNullOrBlank()) {
        VideoThumbnailView(
            videoUriOrPath = imageUri,
            modifier = modifier
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            if (imageUri.isNullOrBlank()) {
                if (defaultDrawableRes != 0) {
                    Image(
                        painter = painterResource(id = defaultDrawableRes),
                        contentDescription = null,
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (imageUri.startsWith("preset:")) {
                val presetRes = when (imageUri.removePrefix("preset:")) {
                    "ic_merit_dana" -> R.drawable.ic_merit_dana
                    "ic_merit_lotus" -> R.drawable.ic_merit_lotus
                    "ic_merit_meditation" -> R.drawable.ic_merit_meditation
                    "ic_merit_lantern" -> R.drawable.ic_merit_lantern
                    "ic_merit_kindness" -> R.drawable.ic_merit_kindness
                    "ic_merit_water" -> R.drawable.ic_merit_water
                    "ic_merit_stupa" -> R.drawable.ic_merit_stupa
                    "ic_merit_bodhi" -> R.drawable.ic_merit_bodhi
                    else -> defaultDrawableRes
                }
                Image(
                    painter = painterResource(id = presetRes),
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val imageModel = if (imageUri.startsWith("/")) {
                    File(imageUri)
                } else {
                    imageUri
                }
                val request = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .error(defaultDrawableRes)
                    .fallback(defaultDrawableRes)
                    .build()

                AsyncImage(
                    model = request,
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
