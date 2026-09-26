package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.i18n.LocalAppStrings

/**
 * Transparent cut-out of the sacred Bodhi Tree from the original emblem.
 * The background is completely removed and the roots sit at the exact bottom edge,
 * allowing it to stand perfectly flush with the line below.
 */
@Composable
fun MeritTreeCutoutEmblem(
    height: Dp = 54.dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.ic_merit_tree_cutout),
        contentDescription = "Merit Tree",
        contentScale = ContentScale.FillHeight,
        alignment = Alignment.BottomStart,
        modifier = modifier
            .height(height)
            .testTag("merit_tree_top_cutout")
    )
}

/**
 * Full Top App Bar with the Bodhi Tree standing flush with the horizontal line below.
 */
@Composable
fun MeritTreeTopBar(
    activeLeavesCount: Int,
    onBellClick: () -> Unit,
    onWaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val treeTitle = if (strings.isSinhala) "පුණ්ය වෘක්ෂය" else "Merit Tree"

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Left side: Tree cutout standing flush + Title & Count
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Bodhi Tree with transparent background, standing flush on the bottom line
                    MeritTreeCutoutEmblem(
                        height = 56.dp,
                        modifier = Modifier.padding(bottom = 0.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = treeTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = if (strings.isSinhala) 0.sp else 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("merit_tree_logo_title")
                        )
                        Text(
                            text = if (strings.isSinhala) "$activeLeavesCount කුසල් පත්‍ර" else "$activeLeavesCount merit leaves",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Right side: Mindful Bell Chime & Water Dedication
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mindful Bell Chime
                    IconButton(
                        onClick = onBellClick,
                        modifier = Modifier.testTag("mindful_bell_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = strings.mindfulBell,
                            tint = Color(0xFFD97706)
                        )
                    }

                    // Water Offering / Dedicate
                    IconButton(
                        onClick = onWaterClick,
                        modifier = Modifier.testTag("water_dedication_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = strings.shareMerit,
                            tint = Color(0xFF0288D1)
                        )
                    }
                }
            }

            // The line below: tree roots sit directly flush on top of this line
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f),
                thickness = 1.dp,
                modifier = Modifier.testTag("merit_tree_top_divider_line")
            )
        }
    }
}

/**
 * Top App Bar Logo Component for inline or custom bar usage.
 */
@Composable
fun MeritTreeTopBarLogo(
    activeLeavesCount: Int,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val treeTitle = if (strings.isSinhala) "පුණ්ය වෘක්ෂය" else "Merit Tree"

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.testTag("merit_tree_app_logo")
    ) {
        MeritTreeCutoutEmblem(height = 50.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            Text(
                text = treeTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (strings.isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("merit_tree_logo_title")
            )
        }
    }
}

/**
 * Legacy Emblem wrapper for About and other sections.
 */
@Composable
fun MeritTreeEmblem(
    size: Dp = 40.dp,
    animatedGlow: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF133946),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD54F)),
            shadowElevation = 2.dp,
            modifier = Modifier.size(size * 0.90f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_merit_tree_art),
                    contentDescription = "Merit Tree Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
