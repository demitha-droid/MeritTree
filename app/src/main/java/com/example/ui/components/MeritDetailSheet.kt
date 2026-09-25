package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import com.example.ui.sound.MindfulSoundHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Animated Merit Post Pop Dialog.
 * When a leaf on the Bodhi tree is tapped, this pops up instantaneously
 * displaying the post's linked image prominently at the top with zero performance lag.
 */
@Composable
fun MeritDetailSheet(
    merit: MeritEntity,
    onDismiss: () -> Unit,
    onDelete: (MeritEntity) -> Unit
) {
    val context = LocalContext.current
    val category = MeritCategory.fromString(merit.category)
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var rejoiced by remember { mutableStateOf(false) }
    var isFullScreenImage by remember { mutableStateOf(false) }

    val formattedDate = remember(merit.timestamp) {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(merit.timestamp))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Scrim background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Animated Pop Card with bouncy spring entrance
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    initialScale = 0.82f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(120)),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps inside card */ }
            ) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.5.dp, Color(0xFFFFD54F).copy(alpha = 0.55f)),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.85f)
                        .testTag("merit_detail_sheet")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        // 1. HERO IMAGE PROMINENTLY AT THE TOP!
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clickable { isFullScreenImage = true }
                                .testTag("merit_post_image_card")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                RenderMeritImage(
                                    imageUri = merit.imageUri,
                                    defaultDrawableRes = category.defaultDrawableRes,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Top row: Category Pill (left) & Close Button (right)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopCenter)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                                        border = BorderStroke(1.dp, category.leafColor.copy(alpha = 0.6f)),
                                        shadowElevation = 2.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Spa,
                                                contentDescription = null,
                                                tint = category.leafColor,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "${category.paliName} • ${category.title}",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = category.leafColor
                                                )
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                                        shadowElevation = 2.dp
                                    ) {
                                        IconButton(
                                            onClick = onDismiss,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("close_merit_detail_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Bottom "Tap to enlarge" badge
                                Surface(
                                    color = Color.Black.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Tap to enlarge",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // 2. SCROLLABLE POST DETAILS
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            // Date & Time
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            // Title
                            Text(
                                text = merit.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 28.sp
                                ),
                                modifier = Modifier.testTag("merit_detail_title")
                            )

                            // Description
                            if (merit.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = merit.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            lineHeight = 22.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier
                                            .padding(14.dp)
                                            .testTag("merit_detail_description")
                                    )
                                }
                            }

                            // Dedication of Merit (Pattidāna)
                            if (merit.dedication.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFFFF8E1),
                                    border = BorderStroke(1.dp, Color(0xFFFFE082)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_merit_water),
                                            contentDescription = "Sharing Merit",
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Dedication of Merit (Pattidāna)",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE65100)
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "“${merit.dedication}”",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontStyle = FontStyle.Italic,
                                                    color = Color(0xFF4E342E),
                                                    lineHeight = 18.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Row: Sādhu / Rejoice, Share, Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        rejoiced = true
                                        MindfulSoundHelper.playSingingBowlChime()
                                        MindfulSoundHelper.performMindfulHaptic(context, strong = true)
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = if (rejoiced) category.leafColor else MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = if (rejoiced) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("rejoice_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (rejoiced) "Sādhu! Sādhu!" else "Rejoice (Sādhu)",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val shareText = buildString {
                                            append("🌿 Bodhi Merit: ${merit.title}\n")
                                            append("Category: ${category.paliName} (${category.title})\n\n")
                                            if (merit.description.isNotBlank()) {
                                                append("${merit.description}\n\n")
                                            }
                                            if (merit.dedication.isNotBlank()) {
                                                append("Dedication: ${merit.dedication}\n")
                                            }
                                            append("Recorded on the Bodhi Tree of Good Deeds.")
                                        }
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Wholesome Merit"))
                                    },
                                    modifier = Modifier.testTag("share_merit_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { showDeleteConfirm = true },
                                    modifier = Modifier.testTag("delete_merit_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove leaf",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full screen image preview dialog
    if (isFullScreenImage) {
        Dialog(
            onDismissRequest = { isFullScreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                RenderMeritImage(
                    imageUri = merit.imageUri,
                    defaultDrawableRes = category.defaultDrawableRes,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { isFullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close full image",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Release Leaf from Bodhi Tree?") },
            text = { Text("This will remove '${merit.title}' from your merit records.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(merit)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Release Leaf")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RenderMeritImage(
    imageUri: String?,
    defaultDrawableRes: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    when {
        imageUri != null && imageUri.startsWith("preset:") -> {
            val presetName = imageUri.removePrefix("preset:")
            val resId = when (presetName) {
                "ic_merit_lotus" -> R.drawable.ic_merit_lotus
                "ic_merit_dana" -> R.drawable.ic_merit_dana
                "ic_merit_meditation" -> R.drawable.ic_merit_meditation
                "ic_merit_lantern" -> R.drawable.ic_merit_lantern
                "ic_merit_kindness" -> R.drawable.ic_merit_kindness
                "ic_merit_water" -> R.drawable.ic_merit_water
                "ic_merit_stupa" -> R.drawable.ic_merit_stupa
                "ic_merit_bodhi" -> R.drawable.ic_merit_bodhi
                else -> defaultDrawableRes
            }
            Box(
                modifier = modifier
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFFDE7), Color(0xFFE8F5E9))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Merit artwork",
                    contentScale = contentScale,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                )
            }
        }
        !imageUri.isNullOrBlank() -> {
            val model = if (imageUri.startsWith("/")) File(imageUri) else imageUri
            val request = remember(imageUri) {
                ImageRequest.Builder(context)
                    .data(model)
                    .crossfade(150)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = "Merit Photo",
                contentScale = contentScale,
                modifier = modifier
            )
        }
        else -> {
            Box(
                modifier = modifier
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFFDE7), Color(0xFFE8F5E9))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = defaultDrawableRes),
                    contentDescription = "Default merit artwork",
                    contentScale = contentScale,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}
