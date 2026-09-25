package com.example.ui.components

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Universal, high-performance media renderer for merit posts and Bodhi leaves.
 * Smoothly resolves photos, camera captures, video thumbnails with play indicators, and legacy motifs.
 */
@Composable
fun RenderMeritImage(
    imageUri: String?,
    defaultDrawableRes: Int,
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

    var videoThumbnail by remember(imageUri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUri, isVideo) {
        if (isVideo && !imageUri.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val retriever = MediaMetadataRetriever()
                    if (imageUri.startsWith("/")) {
                        retriever.setDataSource(imageUri)
                    } else {
                        retriever.setDataSource(context, Uri.parse(imageUri))
                    }
                    val frame = retriever.getFrameAtTime(500_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        ?: retriever.frameAtTime
                    videoThumbnail = frame
                    retriever.release()
                } catch (_: Exception) {}
            }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (imageUri.isNullOrBlank()) {
            Image(
                painter = painterResource(id = defaultDrawableRes),
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else if (isVideo) {
            val thumb = videoThumbnail
            if (thumb != null) {
                Image(
                    bitmap = thumb.asImageBitmap(),
                    contentDescription = "Video Thumbnail",
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = defaultDrawableRes),
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (showVideoBadge) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
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
