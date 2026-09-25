package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import com.example.data.getMediaUris
import com.example.ui.i18n.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeritJournalList(
    merits: List<MeritEntity>,
    onMeritClick: (MeritEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    if (merits.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.journalEmpty,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.journalEmptySub,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        // Full screen from both sides (0 horizontal padding)
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 84.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(merits, key = { it.id }) { merit ->
                MeritJournalCard(
                    merit = merit,
                    onClick = { onMeritClick(merit) }
                )
            }
        }
    }
}

/**
 * Full post card stretching full screen from left to right edges.
 * Only has top and bottom borders (left and right borders removed per design request).
 * Renders video thumbnails seamlessly with zero lag.
 */
@Composable
fun MeritJournalCard(
    merit: MeritEntity,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val formattedDate = remember(merit.timestamp) {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(merit.timestamp))
    }

    val isVideo = remember(merit.imageUri) {
        val uri = merit.imageUri?.lowercase() ?: ""
        uri.endsWith(".mp4") || uri.endsWith(".mov") || uri.endsWith(".mkv") ||
                uri.endsWith(".3gp") || uri.endsWith(".webm") || uri.contains("merit_video_")
    }

    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)

    // Full screen card with ONLY top and bottom borders
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                // Top post border
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
                // Bottom post border
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .testTag("merit_card_${merit.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp)
        ) {
            // Text Header: Date, Title, Description, and Category (with comfortable 20.dp horizontal padding)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // 1. DATE & TIME (Top)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                        ),
                        modifier = Modifier.testTag("merit_date_${merit.id}")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. TITLE (Below date & time)
                Text(
                    text = merit.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("merit_title_${merit.id}")
                )

                // 3. DESCRIPTION (Below title)
                if (merit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = merit.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("merit_description_${merit.id}")
                    )
                }
            }

            // 4. UPLOADED MEDIA (Supports multiple images and videos)
            // Spans edge-to-edge full width from left to right edge
            val mediaUris = remember(merit.imageUri) { merit.getMediaUris() }
            if (mediaUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                if (mediaUris.size == 1) {
                    val singleUri = mediaUris.first()
                    val isSingleVideo = remember(singleUri) {
                        val lower = singleUri.lowercase()
                        lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                                lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .testTag("merit_big_image_${merit.id}")
                    ) {
                        if (isSingleVideo) {
                            VideoThumbnailView(
                                videoUriOrPath = singleUri,
                                modifier = Modifier.fillMaxSize(),
                                onPlayClick = onClick
                            )
                        } else {
                            RenderMeritImage(
                                imageUri = singleUri,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    // Multi-media carousel in feed
                    val pagerState = rememberPagerState(pageCount = { mediaUris.size })
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color.Black)
                            .testTag("merit_big_image_${merit.id}")
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val uri = mediaUris[page]
                            val isPageVideo = remember(uri) {
                                val lower = uri.lowercase()
                                lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                                        lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
                            }

                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isPageVideo) {
                                    VideoThumbnailView(
                                        videoUriOrPath = uri,
                                        modifier = Modifier.fillMaxSize(),
                                        onPlayClick = onClick
                                    )
                                } else {
                                    RenderMeritImage(
                                        imageUri = uri,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        // Counter pill overlay (Top-Right)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.65f),
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
                                    imageVector = Icons.Default.Collections,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${pagerState.currentPage + 1}/${mediaUris.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Dots indicator (Bottom Center)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(mediaUris.size) { iteration ->
                                val isSelected = pagerState.currentPage == iteration
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color.White else Color.White.copy(alpha = 0.45f)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
