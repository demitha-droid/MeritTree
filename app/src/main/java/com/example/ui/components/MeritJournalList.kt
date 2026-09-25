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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MeritCategory
import com.example.data.MeritEntity
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
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
 * Full post card following requested hierarchy:
 * 1. Date & Time
 * 2. Title
 * 3. Description font of the title
 * 4. Category label
 * 5. Big uploaded image
 */
@Composable
fun MeritJournalCard(
    merit: MeritEntity,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val category = MeritCategory.fromString(merit.category)
    val formattedDate = remember(merit.timestamp) {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(merit.timestamp))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("merit_card_${merit.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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

            // 3. DESCRIPTION FONT OF THE TITLE (Below title)
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

            Spacer(modifier = Modifier.height(12.dp))

            // 4. CATEGORY LABEL
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = category.leafColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, category.leafColor.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = category.leafColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${category.paliName} • ${strings.categoryTitle(category)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = category.leafColor
                        )
                    )
                }
            }

            // 5. UPLOADED IMAGE OR VIDEO (ONLY IF USER ATTACHED MEDIA!)
            if (!merit.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                val isVideo = remember(merit.imageUri) {
                    val lower = merit.imageUri.lowercase()
                    lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                            lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .testTag("merit_big_image_${merit.id}")
                ) {
                    if (isVideo) {
                        InAppVideoPlayer(
                            videoUriOrPath = merit.imageUri,
                            modifier = Modifier.fillMaxSize(),
                            autoPlay = false
                        )
                    } else {
                        RenderMeritImage(
                            imageUri = merit.imageUri,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
