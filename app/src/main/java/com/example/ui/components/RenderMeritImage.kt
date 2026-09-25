package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import java.io.File

/**
 * Universal, high-performance image renderer for merit posts and Bodhi leaves.
 * Smoothly resolves preset sacred motifs, internal storage file paths, and gallery Uris with caching.
 */
@Composable
fun RenderMeritImage(
    imageUri: String?,
    defaultDrawableRes: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    if (imageUri.isNullOrBlank()) {
        Image(
            painter = painterResource(id = defaultDrawableRes),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier
        )
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
            modifier = modifier
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
            modifier = modifier
        )
    }
}
