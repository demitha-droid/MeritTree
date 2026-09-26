package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.LocalAppStrings
import kotlin.math.cos
import kotlin.math.sin

/**
 * 10 Original Custom-Designed Bodhi Tree Top Bar Styles
 */
data class TopBarDesignOption(
    val id: Int,
    val nameEn: String,
    val nameSi: String,
    val taglineEn: String,
    val taglineSi: String
)

val TOP_BAR_DESIGN_OPTIONS = listOf(
    TopBarDesignOption(
        id = 1,
        nameEn = "1. Sacred Bodhi Heart-Leaf",
        nameSi = "1. පූජනීය බෝ පත්‍රය",
        taglineEn = "Iconic heart-shaped Bodhi leaf with golden drip-tip and branching tree inside.",
        taglineSi = "රන්වන් නාරටි සහිත හෘදයාකාර බෝ පත්‍රයේ නිමවූ පූජනීය බෝධි වෘක්ෂය."
    ),
    TopBarDesignOption(
        id = 2,
        nameEn = "2. Zen Enso Circle",
        nameSi = "2. සෙන් එන්සෝ වෘත්තය",
        taglineEn = "Golden Zen brushstroke circle embracing an organic flourishing Bodhi tree.",
        taglineSi = "බෞද්ධ සෙන් රන්වන් වෘත්තය තුළ වැඩෙන මනරම් බෝධි අතුපතර."
    ),
    TopBarDesignOption(
        id = 3,
        nameEn = "3. Rooted Horizon (Standing Flush)",
        nameSi = "3. ක්ෂිතිජ බෝධිය (රේඛාව මත පිහිටි)",
        taglineEn = "Pure vector Bodhi tree with roots planted perfectly flush on the baseline divider.",
        taglineSi = "පහළ බෙදුම් රේඛාව මත ස්වභාවිකව මුල් විහිදා පිහිටි පිරිසිදු දෛශික බෝධිය."
    ),
    TopBarDesignOption(
        id = 4,
        nameEn = "4. Lotus Throne Pedestal",
        nameSi = "4. පද්මාසන පීඨය",
        taglineEn = "Sacred Bodhi tree rooted upon a carved 5-petal golden lotus throne (Padma Asana).",
        taglineSi = "ස්වර්ණමය පද්මාසනයක් මත රෝපණය වූ අනුරාධපුර සම්ප්‍රදායේ බෝධි රාජයාණන්."
    ),
    TopBarDesignOption(
        id = 5,
        nameEn = "5. Royal Jade Crest",
        nameSi = "5. මරකත රාජකීය ලාංඡනය",
        taglineEn = "Deep jade cabochon medallion with 24k gold filigree branches and emerald leaves.",
        taglineSi = "මරකත මැණික් පැහැති පසුබිමක රන්වන් සූක්ෂ්ම කැටයමින් යුතු ලාංඡනය."
    ),
    TopBarDesignOption(
        id = 6,
        nameEn = "6. Minimalist Line-Art",
        nameSi = "6. සෙන් සරල රේඛා කලාව",
        taglineEn = "Nordic-Zen architectural vector line art with delicate heart-leaf buds.",
        taglineSi = "ඉතා පැහැදිලි, සන්සුන්, සැහැල්ලු නවීන රේඛීය බෝධි මෝස්තරය."
    ),
    TopBarDesignOption(
        id = 7,
        nameEn = "7. Solar Prabha Mandala",
        nameSi = "7. ප්‍රභා මණ්ඩල බෝධිය",
        taglineEn = "Sacred Bodhi canopy surrounded by a radiant golden sunburst halo (Chakkavatti Prabha).",
        taglineSi = "ගස වටා විහිදෙන උදෑසන රන්වන් ප්‍රභා මණ්ඩලය සහිත ආධ්‍යාත්මික නිර්මාණය."
    ),
    TopBarDesignOption(
        id = 8,
        nameEn = "8. Modern Sprout Capsule",
        nameSi = "8. නවීන මෘදු කැප්සියුලය",
        taglineEn = "Contemporary tech-spiritual frosted pill with dual-tone sprout & live leaf counter.",
        taglineSi = "විනිවිද පෙනෙන නවීන කැප්සියුල රාමුවක් සහිත අලංකාර බෝධි අංකුරය."
    ),
    TopBarDesignOption(
        id = 9,
        nameEn = "9. Anuradhapura Torana Arch",
        nameSi = "9. අනුරාධපුර තොරණ ආරුක්කුව",
        taglineEn = "Classic Sri Lankan Buddhist temple arch portal framing the sacred tree.",
        taglineSi = "ඓතිහාසික සිංහල විහාර තොරණ ආරුක්කුවෙන් සරසන ලද ශ්‍රී මහා බෝධිය."
    ),
    TopBarDesignOption(
        id = 10,
        nameEn = "10. Twin Bodhi Leaves of Dharma",
        nameSi = "10. ද්විත්ව බෝපත් යුගලය",
        taglineEn = "Two interlocking golden Bodhi heart-leaves symbolizing Wisdom (Prajñā) and Compassion (Karunā).",
        taglineSi = "ප්‍රඥාව හා කරුණාව සංකේතවත් කරන සුසංයෝගී රන් බෝපත් යුගලය."
    )
)

/**
 * Main Top App Bar Composable supporting 10 original, clean custom-designed styles
 * with instant 60fps switching and full bilingual name support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeritTreeTopBar(
    activeLeavesCount: Int,
    currentStyle: Int = 1,
    onStyleChange: (Int) -> Unit = {},
    onBellClick: () -> Unit,
    onWaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val treeTitle = if (strings.isSinhala) "පුණ්ය වෘක්ෂය" else "Merit Tree"
    var showStylePicker by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
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
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left & Center: Top Bar Design Variant (1 of 10)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { showStylePicker = true },
                    contentAlignment = Alignment.CenterStart
                ) {
                    when (currentStyle) {
                        1 -> Design1BodhiHeartLeaf(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        2 -> Design2EnsoCircle(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        3 -> Design3RootedHorizonFlush(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        4 -> Design4LotusPedestal(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        5 -> Design5RoyalJadeCrest(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        6 -> Design6MinimalLineArt(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        7 -> Design7SolarPrabha(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        8 -> Design8ModernCapsule(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        9 -> Design9ToranaArch(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        10 -> Design10TwinDharmaLeaves(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                        else -> Design1BodhiHeartLeaf(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
                    }
                }

                // Right Actions: Mindful Bell + Water Offering
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mindful Bell Chime
                    IconButton(
                        onClick = onBellClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("mindful_bell_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = strings.mindfulBell,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Water Offering / Dedicate
                    IconButton(
                        onClick = onWaterClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("water_dedication_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = strings.shareMerit,
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bottom Boundary Line: Crisp divider with subtle gold or jade accents
            HorizontalDivider(
                color = when (currentStyle) {
                    3 -> Color(0x60D4AF37) // Golden line for Rooted Horizon
                    4 -> Color(0x60D4AF37) // Antique gold for Lotus Throne
                    7 -> Color(0x50FFD54F) // Dawn gold for Solar Prabha
                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                },
                thickness = if (currentStyle == 3 || currentStyle == 4) 1.5.dp else 1.dp,
                modifier = Modifier.testTag("merit_tree_top_divider_line")
            )
        }
    }

    // Modal Bottom Sheet to Preview & Select all 10 Custom Designs
    if (showStylePicker) {
        ModalBottomSheet(
            onDismissRequest = { showStylePicker = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = if (strings.isSinhala) "ඉහළ තීරුවේ මෝස්තරය තෝරන්න (1-10)" else "Choose Top Bar Design (1–10)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (strings.isSinhala) "අප විසින් නිර්මාණය කළ මෝස්තර 10න් කැමති එක ස්පර්ශ කරන්න" else "Tap any of our 10 handcrafted vector designs to preview live",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    items(TOP_BAR_DESIGN_OPTIONS) { opt ->
                        val isSelected = opt.id == currentStyle
                        OutlinedCard(
                            onClick = {
                                onStyleChange(opt.id)
                                showStylePicker = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.8.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("top_bar_design_option_${opt.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${opt.id}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (strings.isSinhala) opt.nameSi else opt.nameEn,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (strings.isSinhala) opt.taglineSi else opt.taglineEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================================================================================================
// 10 HANDCRAFTED CUSTOM VECTOR TREE EMBLEMS & TOP BAR LAYOUTS
// ================================================================================================

/**
 * Design 1: Sacred Bodhi Heart-Leaf with Tree Inside
 */
@Composable
private fun Design1BodhiHeartLeaf(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(42.dp)) {
            val w = size.width
            val h = size.height

            // 1. Draw Sacred Bodhi Heart-Leaf Path with extended drip-tip
            val leafPath = Path().apply {
                moveTo(w * 0.5f, h * 0.98f) // Drip-tip at bottom
                // Left curve up
                cubicTo(w * 0.35f, h * 0.85f, w * 0.05f, h * 0.60f, w * 0.06f, h * 0.38f)
                cubicTo(w * 0.07f, h * 0.16f, w * 0.30f, h * 0.05f, w * 0.50f, h * 0.22f)
                // Right curve down
                cubicTo(w * 0.70f, h * 0.05f, w * 0.93f, h * 0.16f, w * 0.94f, h * 0.38f)
                cubicTo(w * 0.95f, h * 0.60f, w * 0.65f, h * 0.85f, w * 0.50f, h * 0.98f)
                close()
            }

            // Fill gradient: Deep Emerald into Forest Green
            drawPath(
                path = leafPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0F3D32), Color(0xFF1B5E20), Color(0xFF2E7D32)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Golden Leaf Rim
            drawPath(
                path = leafPath,
                color = Color(0xFFFFD54F),
                style = Stroke(width = 1.8f)
            )

            // 2. Intricate Golden Bodhi Tree Silhouette inside the leaf
            // Trunk
            drawLine(
                color = Color(0xFFFFD54F),
                start = Offset(w * 0.5f, h * 0.88f),
                end = Offset(w * 0.5f, h * 0.42f),
                strokeWidth = 2.4f,
                cap = StrokeCap.Round
            )
            // Left main branch
            val leftBranch = Path().apply {
                moveTo(w * 0.5f, h * 0.64f)
                quadraticBezierTo(w * 0.32f, h * 0.58f, w * 0.24f, h * 0.44f)
            }
            drawPath(leftBranch, Color(0xFFFFD54F), style = Stroke(width = 1.6f, cap = StrokeCap.Round))
            // Right main branch
            val rightBranch = Path().apply {
                moveTo(w * 0.5f, h * 0.64f)
                quadraticBezierTo(w * 0.68f, h * 0.58f, w * 0.76f, h * 0.44f)
            }
            drawPath(rightBranch, Color(0xFFFFD54F), style = Stroke(width = 1.6f, cap = StrokeCap.Round))
            // Upper center branches
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.46f), Offset(w * 0.38f, h * 0.32f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.46f), Offset(w * 0.62f, h * 0.32f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.42f), Offset(w * 0.50f, h * 0.28f), strokeWidth = 1.4f, cap = StrokeCap.Round)

            // Leaf bud accents in luminous gold and jade
            val budColor = Color(0xFFFFE082)
            drawCircle(budColor, radius = 2.4f, center = Offset(w * 0.24f, h * 0.44f))
            drawCircle(budColor, radius = 2.4f, center = Offset(w * 0.76f, h * 0.44f))
            drawCircle(budColor, radius = 2.4f, center = Offset(w * 0.38f, h * 0.32f))
            drawCircle(budColor, radius = 2.4f, center = Offset(w * 0.62f, h * 0.32f))
            drawCircle(budColor, radius = 2.6f, center = Offset(w * 0.50f, h * 0.28f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("merit_tree_logo_title")
            )
            Text(
                text = if (isSinhala) "$leavesCount කුසල් පත්‍ර වැඩෙයි" else "$leavesCount merit leaves",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Design 2: Zen Enso Circle with Curving Bodhi Branches
 */
@Composable
private fun Design2EnsoCircle(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(42.dp)) {
            val w = size.width
            val h = size.height

            // 1. Enso Circle with open gap and brushed taper
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFFD4AF37), Color(0xFFFFD54F), Color(0xFFC5A059), Color(0xFFB8860B)),
                    center = Offset(w * 0.5f, h * 0.5f)
                ),
                startAngle = 45f,
                sweepAngle = 295f,
                useCenter = false,
                style = Stroke(width = 2.8f, cap = StrokeCap.Round)
            )

            // 2. Graceful Bodhi Tree inside
            // Trunk with slight organic S-curve
            val trunkPath = Path().apply {
                moveTo(w * 0.5f, h * 0.82f)
                quadraticBezierTo(w * 0.48f, h * 0.65f, w * 0.50f, h * 0.46f)
            }
            drawPath(trunkPath, Color(0xFF6D4C41), style = Stroke(width = 2.4f, cap = StrokeCap.Round))

            // Sweeping branches
            val b1 = Path().apply {
                moveTo(w * 0.49f, h * 0.62f)
                quadraticBezierTo(w * 0.35f, h * 0.55f, w * 0.28f, h * 0.42f)
            }
            drawPath(b1, Color(0xFF8D6E63), style = Stroke(width = 1.8f, cap = StrokeCap.Round))

            val b2 = Path().apply {
                moveTo(w * 0.50f, h * 0.56f)
                quadraticBezierTo(w * 0.65f, h * 0.50f, w * 0.74f, h * 0.38f)
            }
            drawPath(b2, Color(0xFF8D6E63), style = Stroke(width = 1.8f, cap = StrokeCap.Round))

            val b3 = Path().apply {
                moveTo(w * 0.50f, h * 0.46f)
                quadraticBezierTo(w * 0.42f, h * 0.36f, w * 0.44f, h * 0.24f)
            }
            drawPath(b3, Color(0xFF8D6E63), style = Stroke(width = 1.6f, cap = StrokeCap.Round))

            val b4 = Path().apply {
                moveTo(w * 0.50f, h * 0.46f)
                quadraticBezierTo(w * 0.58f, h * 0.36f, w * 0.58f, h * 0.24f)
            }
            drawPath(b4, Color(0xFF8D6E63), style = Stroke(width = 1.6f, cap = StrokeCap.Round))

            // Cluster of Emerald & Golden leaves
            val gold = Color(0xFFFFD54F)
            val jade = Color(0xFF2E7D32)
            drawCircle(gold, radius = 2.8f, center = Offset(w * 0.28f, h * 0.42f))
            drawCircle(jade, radius = 2.6f, center = Offset(w * 0.32f, h * 0.36f))
            drawCircle(gold, radius = 3.0f, center = Offset(w * 0.74f, h * 0.38f))
            drawCircle(jade, radius = 2.6f, center = Offset(w * 0.70f, h * 0.32f))
            drawCircle(gold, radius = 3.0f, center = Offset(w * 0.44f, h * 0.24f))
            drawCircle(jade, radius = 3.0f, center = Offset(w * 0.58f, h * 0.24f))
            drawCircle(gold, radius = 2.5f, center = Offset(w * 0.51f, h * 0.20f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isSinhala) "සෙන් බෝධි අභයභූමිය" else "Zen Bodhi Sanctuary",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC5A059)
            )
        }
    }
}

/**
 * Design 3: Rooted Horizon (Pure Vector Bodhi Tree Standing Flush with the Line Below)
 */
@Composable
private fun Design3RootedHorizonFlush(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(
            modifier = Modifier
                .width(64.dp)
                .height(54.dp)
                .padding(bottom = 0.dp)
        ) {
            val w = size.width
            val h = size.height

            // 1. Spreading Root Flares standing 100% flush on y = h (baseline)
            val rootsPath = Path().apply {
                moveTo(w * 0.26f, h) // Leftmost root tip flush on line
                quadraticBezierTo(w * 0.40f, h * 0.88f, w * 0.47f, h * 0.70f)
                lineTo(w * 0.53f, h * 0.70f)
                quadraticBezierTo(w * 0.60f, h * 0.88f, w * 0.74f, h) // Rightmost root tip flush on line
                lineTo(w * 0.26f, h) // Flat base along the divider line
                close()
            }
            drawPath(rootsPath, color = Color(0xFF5D4037))

            // 2. Majestic Trunk rising upward
            val trunkPath = Path().apply {
                moveTo(w * 0.45f, h * 0.70f)
                cubicTo(w * 0.46f, h * 0.55f, w * 0.47f, h * 0.45f, w * 0.48f, h * 0.36f)
                lineTo(w * 0.52f, h * 0.36f)
                cubicTo(w * 0.53f, h * 0.45f, w * 0.54f, h * 0.55f, w * 0.55f, h * 0.70f)
                close()
            }
            drawPath(trunkPath, color = Color(0xFF6D4C41))

            // 3. Multi-Tiered Bodhi Branches
            // Low left branch
            drawPath(
                Path().apply {
                    moveTo(w * 0.47f, h * 0.52f)
                    quadraticBezierTo(w * 0.30f, h * 0.46f, w * 0.16f, h * 0.38f)
                },
                color = Color(0xFF795548),
                style = Stroke(width = 2.4f, cap = StrokeCap.Round)
            )
            // Low right branch
            drawPath(
                Path().apply {
                    moveTo(w * 0.53f, h * 0.52f)
                    quadraticBezierTo(w * 0.70f, h * 0.46f, w * 0.84f, h * 0.38f)
                },
                color = Color(0xFF795548),
                style = Stroke(width = 2.4f, cap = StrokeCap.Round)
            )
            // Mid left branch
            drawPath(
                Path().apply {
                    moveTo(w * 0.48f, h * 0.42f)
                    quadraticBezierTo(w * 0.36f, h * 0.30f, w * 0.26f, h * 0.22f)
                },
                color = Color(0xFF8D6E63),
                style = Stroke(width = 2.0f, cap = StrokeCap.Round)
            )
            // Mid right branch
            drawPath(
                Path().apply {
                    moveTo(w * 0.52f, h * 0.42f)
                    quadraticBezierTo(w * 0.64f, h * 0.30f, w * 0.74f, h * 0.22f)
                },
                color = Color(0xFF8D6E63),
                style = Stroke(width = 2.0f, cap = StrokeCap.Round)
            )
            // Crown central branches
            drawLine(Color(0xFF8D6E63), Offset(w * 0.49f, h * 0.36f), Offset(w * 0.42f, h * 0.14f), strokeWidth = 1.8f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.51f, h * 0.36f), Offset(w * 0.58f, h * 0.14f), strokeWidth = 1.8f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.50f, h * 0.34f), Offset(w * 0.50f, h * 0.08f), strokeWidth = 1.8f, cap = StrokeCap.Round)

            // 4. Vibrant Bodhi Leaves (Gold & Emerald Canopy Clustered at branch tips)
            val gold = Color(0xFFFFD54F)
            val amber = Color(0xFFFFA000)
            val jade = Color(0xFF2E7D32)
            val lightGreen = Color(0xFF66BB6A)

            // Left cluster
            drawCircle(gold, radius = 3.8f, center = Offset(w * 0.16f, h * 0.38f))
            drawCircle(jade, radius = 3.2f, center = Offset(w * 0.22f, h * 0.34f))
            drawCircle(lightGreen, radius = 3.0f, center = Offset(w * 0.14f, h * 0.32f))
            drawCircle(amber, radius = 3.6f, center = Offset(w * 0.26f, h * 0.22f))
            drawCircle(jade, radius = 3.2f, center = Offset(w * 0.32f, h * 0.18f))

            // Right cluster
            drawCircle(gold, radius = 3.8f, center = Offset(w * 0.84f, h * 0.38f))
            drawCircle(jade, radius = 3.2f, center = Offset(w * 0.78f, h * 0.34f))
            drawCircle(lightGreen, radius = 3.0f, center = Offset(w * 0.86f, h * 0.32f))
            drawCircle(amber, radius = 3.6f, center = Offset(w * 0.74f, h * 0.22f))
            drawCircle(jade, radius = 3.2f, center = Offset(w * 0.68f, h * 0.18f))

            // Crown cluster
            drawCircle(gold, radius = 4.0f, center = Offset(w * 0.50f, h * 0.08f))
            drawCircle(lightGreen, radius = 3.4f, center = Offset(w * 0.44f, h * 0.07f))
            drawCircle(jade, radius = 3.4f, center = Offset(w * 0.56f, h * 0.07f))
            drawCircle(amber, radius = 3.6f, center = Offset(w * 0.42f, h * 0.14f))
            drawCircle(gold, radius = 3.6f, center = Offset(w * 0.58f, h * 0.14f))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isSinhala) "$leavesCount පින් පලදාව" else "$leavesCount merits planted",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Design 4: Lotus Throne Pedestal (Sacred Padma Asana Altar)
 */
@Composable
private fun Design4LotusPedestal(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(
            modifier = Modifier
                .width(52.dp)
                .height(52.dp)
                .padding(bottom = 0.dp)
        ) {
            val w = size.width
            val h = size.height

            // 1. Carved Lotus Petal Throne at the base (y = h * 0.82 to h)
            val lotusBase = Path().apply {
                moveTo(w * 0.10f, h)
                lineTo(w * 0.90f, h)
                lineTo(w * 0.85f, h * 0.90f)
                lineTo(w * 0.15f, h * 0.90f)
                close()
            }
            drawPath(lotusBase, color = Color(0xFFD4AF37))

            // 3 Lotus Petals above the base
            // Center petal
            val centerPetal = Path().apply {
                moveTo(w * 0.38f, h * 0.90f)
                quadraticBezierTo(w * 0.50f, h * 0.72f, w * 0.62f, h * 0.90f)
                close()
            }
            drawPath(centerPetal, color = Color(0xFFFFD54F))

            // Left petal
            val leftPetal = Path().apply {
                moveTo(w * 0.18f, h * 0.90f)
                quadraticBezierTo(w * 0.28f, h * 0.76f, w * 0.40f, h * 0.90f)
                close()
            }
            drawPath(leftPetal, color = Color(0xFFE5C158))

            // Right petal
            val rightPetal = Path().apply {
                moveTo(w * 0.60f, h * 0.90f)
                quadraticBezierTo(w * 0.72f, h * 0.76f, w * 0.82f, h * 0.90f)
                close()
            }
            drawPath(rightPetal, color = Color(0xFFE5C158))

            // 2. Bodhi Tree emerging from the Lotus Throne
            drawLine(Color(0xFF6D4C41), Offset(w * 0.5f, h * 0.76f), Offset(w * 0.5f, h * 0.38f), strokeWidth = 2.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.5f, h * 0.54f), Offset(w * 0.30f, h * 0.36f), strokeWidth = 2.0f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.5f, h * 0.54f), Offset(w * 0.70f, h * 0.36f), strokeWidth = 2.0f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.5f, h * 0.42f), Offset(w * 0.38f, h * 0.22f), strokeWidth = 1.8f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.5f, h * 0.42f), Offset(w * 0.62f, h * 0.22f), strokeWidth = 1.8f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(w * 0.5f, h * 0.38f), Offset(w * 0.50f, h * 0.16f), strokeWidth = 1.8f, cap = StrokeCap.Round)

            // Emerald & Golden leaves
            val gold = Color(0xFFFFD54F)
            val jade = Color(0xFF2E7D32)
            drawCircle(gold, radius = 3.4f, center = Offset(w * 0.30f, h * 0.36f))
            drawCircle(jade, radius = 3.4f, center = Offset(w * 0.70f, h * 0.36f))
            drawCircle(gold, radius = 3.6f, center = Offset(w * 0.38f, h * 0.22f))
            drawCircle(jade, radius = 3.6f, center = Offset(w * 0.62f, h * 0.22f))
            drawCircle(gold, radius = 4.0f, center = Offset(w * 0.50f, h * 0.16f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isSinhala) "පද්මාසන බෝධි පූජාව" else "Sacred Lotus Shrine",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD4AF37)
            )
        }
    }
}

/**
 * Design 5: Royal Jade Crest (Jeweled Medallion)
 */
@Composable
private fun Design5RoyalJadeCrest(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(42.dp)) {
            val w = size.width
            val h = size.height

            // 1. Jeweled Rounded Hexagon / Squircle Background in Deep Jade
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF0F3D32), Color(0xFF09261F)),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.65f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                style = Fill
            )

            // Double Gold Filigree Rim
            drawRoundRect(
                color = Color(0xFFFFD54F),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                style = Stroke(width = 1.6f)
            )
            drawRoundRect(
                color = Color(0x70FFD54F),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(9.dp.toPx()),
                size = Size(w - 6.dp.toPx(), h - 6.dp.toPx()),
                topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                style = Stroke(width = 1.0f)
            )

            // 2. 24K Gold Filigree Bodhi Tree Motif in center
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.78f), Offset(w * 0.5f, h * 0.42f), strokeWidth = 2.2f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.60f), Offset(w * 0.32f, h * 0.48f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.60f), Offset(w * 0.68f, h * 0.48f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.48f), Offset(w * 0.36f, h * 0.32f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.48f), Offset(w * 0.64f, h * 0.32f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.42f), Offset(w * 0.50f, h * 0.24f), strokeWidth = 1.6f, cap = StrokeCap.Round)

            // Polished leaf jewels
            val leafGold = Color(0xFFFFF176)
            drawCircle(leafGold, radius = 2.4f, center = Offset(w * 0.32f, h * 0.48f))
            drawCircle(leafGold, radius = 2.4f, center = Offset(w * 0.68f, h * 0.48f))
            drawCircle(leafGold, radius = 2.5f, center = Offset(w * 0.36f, h * 0.32f))
            drawCircle(leafGold, radius = 2.5f, center = Offset(w * 0.64f, h * 0.32f))
            drawCircle(leafGold, radius = 2.8f, center = Offset(w * 0.50f, h * 0.24f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Active status dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32))
                )
            }
            Text(
                text = if (isSinhala) "$leavesCount කුසල් පත්‍ර" else "$leavesCount merit leaves",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Design 6: Minimalist Hairline Bodhi (Zen Line Art)
 */
@Composable
private fun Design6MinimalLineArt(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(38.dp)) {
            val w = size.width
            val h = size.height

            // Pure architectural clean line art
            val lineColor = Color(0xFF2E7D32)
            val goldColor = Color(0xFFD4AF37)

            // Flowing trunk line
            val trunk = Path().apply {
                moveTo(w * 0.5f, h * 0.88f)
                cubicTo(w * 0.46f, h * 0.68f, w * 0.54f, h * 0.50f, w * 0.50f, h * 0.32f)
            }
            drawPath(trunk, lineColor, style = Stroke(width = 2.0f, cap = StrokeCap.Round))

            // Branches
            drawLine(lineColor, Offset(w * 0.48f, h * 0.60f), Offset(w * 0.22f, h * 0.44f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(lineColor, Offset(w * 0.52f, h * 0.56f), Offset(w * 0.78f, h * 0.40f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(lineColor, Offset(w * 0.49f, h * 0.44f), Offset(w * 0.30f, h * 0.26f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(lineColor, Offset(w * 0.51f, h * 0.40f), Offset(w * 0.70f, h * 0.24f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(lineColor, Offset(w * 0.50f, h * 0.32f), Offset(w * 0.50f, h * 0.16f), strokeWidth = 1.4f, cap = StrokeCap.Round)

            // Stylized outlined heart-leaves at tips
            drawCircle(goldColor, radius = 2.6f, center = Offset(w * 0.22f, h * 0.44f), style = Stroke(width = 1.4f))
            drawCircle(goldColor, radius = 2.6f, center = Offset(w * 0.78f, h * 0.40f), style = Stroke(width = 1.4f))
            drawCircle(goldColor, radius = 2.6f, center = Offset(w * 0.30f, h * 0.26f), style = Stroke(width = 1.4f))
            drawCircle(goldColor, radius = 2.6f, center = Offset(w * 0.70f, h * 0.24f), style = Stroke(width = 1.4f))
            drawCircle(goldColor, radius = 3.0f, center = Offset(w * 0.50f, h * 0.16f), style = Stroke(width = 1.4f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = if (isSinhala) 0.sp else 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isSinhala) "සෙන් සරලතාව" else "Mindful Simplicity",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Design 7: Solar Prabha Mandala (Radiant Sunburst Halo)
 */
@Composable
private fun Design7SolarPrabha(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(44.dp)) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f
            val cy = h * 0.46f

            // 1. Radiant Sunburst Rays (12 beams of enlightenment)
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30.0))
                val rInner = w * 0.24f
                val rOuter = w * 0.44f
                val start = Offset((cx + rInner * cos(angle)).toFloat(), (cy + rInner * sin(angle)).toFloat())
                val end = Offset((cx + rOuter * cos(angle)).toFloat(), (cy + rOuter * sin(angle)).toFloat())
                drawLine(
                    color = Color(0x40FFD54F),
                    start = start,
                    end = end,
                    strokeWidth = 1.6f,
                    cap = StrokeCap.Round
                )
            }

            // Central Warm Halo Circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x60FFD54F), Color(0x20FFCA28), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = w * 0.45f
                ),
                radius = w * 0.44f,
                center = Offset(cx, cy)
            )

            // 2. Foreground Bodhi Tree Silhouette
            drawLine(Color(0xFF5D4037), Offset(cx, h * 0.84f), Offset(cx, cy), strokeWidth = 2.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFF795548), Offset(cx, h * 0.62f), Offset(w * 0.28f, h * 0.48f), strokeWidth = 1.8f, cap = StrokeCap.Round)
            drawLine(Color(0xFF795548), Offset(cx, h * 0.62f), Offset(w * 0.72f, h * 0.48f), strokeWidth = 1.8f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(cx, h * 0.50f), Offset(w * 0.36f, h * 0.34f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(cx, h * 0.50f), Offset(w * 0.64f, h * 0.34f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(cx, cy), Offset(cx, h * 0.22f), strokeWidth = 1.6f, cap = StrokeCap.Round)

            // Luminous golden leaves
            val goldLeaf = Color(0xFFFFD54F)
            drawCircle(goldLeaf, radius = 3.0f, center = Offset(w * 0.28f, h * 0.48f))
            drawCircle(goldLeaf, radius = 3.0f, center = Offset(w * 0.72f, h * 0.48f))
            drawCircle(goldLeaf, radius = 3.2f, center = Offset(w * 0.36f, h * 0.34f))
            drawCircle(goldLeaf, radius = 3.2f, center = Offset(w * 0.64f, h * 0.34f))
            drawCircle(goldLeaf, radius = 3.6f, center = Offset(cx, h * 0.22f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isSinhala) "ප්‍රභාෂ්වර අරුණෝදය" else "Radiant Enlightenment",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD97706)
            )
        }
    }
}

/**
 * Design 8: Modern Sprout Capsule (Contemporary Frosted Pill)
 */
@Composable
private fun Design8ModernCapsule(title: String, leavesCount: Int, isSinhala: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f),
        border = BorderStroke(1.dp, Color(0x35FFD54F)),
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Canvas(modifier = Modifier.size(28.dp)) {
                val w = size.width
                val h = size.height

                // Dual-tone modern Bodhi sprout
                // Left leaf in Jade
                val leftLeaf = Path().apply {
                    moveTo(w * 0.5f, h * 0.85f)
                    cubicTo(w * 0.20f, h * 0.65f, w * 0.15f, h * 0.35f, w * 0.50f, h * 0.15f)
                    cubicTo(w * 0.40f, h * 0.40f, w * 0.40f, h * 0.65f, w * 0.50f, h * 0.85f)
                    close()
                }
                drawPath(leftLeaf, color = Color(0xFF2E7D32))

                // Right leaf in Gold
                val rightLeaf = Path().apply {
                    moveTo(w * 0.5f, h * 0.85f)
                    cubicTo(w * 0.80f, h * 0.65f, w * 0.85f, h * 0.35f, w * 0.50f, h * 0.15f)
                    cubicTo(w * 0.60f, h * 0.40f, w * 0.60f, h * 0.65f, w * 0.50f, h * 0.85f)
                    close()
                }
                drawPath(rightLeaf, color = Color(0xFFFFD54F))

                // Central stem
                drawLine(Color(0xFF8D6E63), Offset(w * 0.5f, h * 0.90f), Offset(w * 0.5f, h * 0.30f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$leavesCount",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Design 9: Anuradhapura Torana Arch (Cultural Heritage Gate)
 */
@Composable
private fun Design9ToranaArch(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(width = 38.dp, height = 44.dp)) {
            val w = size.width
            val h = size.height

            // 1. Anuradhapura Torana Arch Portal outline
            val arch = Path().apply {
                moveTo(w * 0.12f, h * 0.92f)
                lineTo(w * 0.12f, h * 0.42f)
                cubicTo(w * 0.12f, h * 0.12f, w * 0.88f, h * 0.12f, w * 0.88f, h * 0.42f)
                lineTo(w * 0.88f, h * 0.92f)
                close()
            }

            // Arch background
            drawPath(
                path = arch,
                brush = Brush.verticalGradient(listOf(Color(0xFF0F3832), Color(0xFF132F2B)))
            )
            // Golden Torana Border
            drawPath(
                path = arch,
                color = Color(0xFFD4AF37),
                style = Stroke(width = 1.5f)
            )

            // 2. Bodhi Tree Inside the Portal
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.85f), Offset(w * 0.5f, h * 0.44f), strokeWidth = 2.0f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.62f), Offset(w * 0.30f, h * 0.50f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.62f), Offset(w * 0.70f, h * 0.50f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.50f), Offset(w * 0.36f, h * 0.34f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.50f), Offset(w * 0.64f, h * 0.34f), strokeWidth = 1.4f, cap = StrokeCap.Round)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.44f), Offset(w * 0.50f, h * 0.26f), strokeWidth = 1.4f, cap = StrokeCap.Round)

            // Golden Leaf nodes
            val gold = Color(0xFFFFD54F)
            drawCircle(gold, radius = 2.2f, center = Offset(w * 0.30f, h * 0.50f))
            drawCircle(gold, radius = 2.2f, center = Offset(w * 0.70f, h * 0.50f))
            drawCircle(gold, radius = 2.4f, center = Offset(w * 0.36f, h * 0.34f))
            drawCircle(gold, radius = 2.4f, center = Offset(w * 0.64f, h * 0.34f))
            drawCircle(gold, radius = 2.6f, center = Offset(w * 0.50f, h * 0.26f))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isSinhala) "අනුරාධපුර තොරණ කලාව" else "Heritage Temple Gate",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD4AF37)
            )
        }
    }
}

/**
 * Design 10: Twin Bodhi Leaves of Dharma (Wisdom & Compassion)
 */
@Composable
private fun Design10TwinDharmaLeaves(title: String, leavesCount: Int, isSinhala: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.size(42.dp)) {
            val w = size.width
            val h = size.height

            // 1. Left Bodhi Leaf (Compassion - Karunā) in Deep Jade
            val leftLeaf = Path().apply {
                moveTo(w * 0.46f, h * 0.88f)
                cubicTo(w * 0.12f, h * 0.74f, w * 0.08f, h * 0.36f, w * 0.42f, h * 0.18f)
                cubicTo(w * 0.36f, h * 0.46f, w * 0.40f, h * 0.70f, w * 0.46f, h * 0.88f)
                close()
            }
            drawPath(
                leftLeaf,
                brush = Brush.linearGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)))
            )
            drawPath(leftLeaf, Color(0xFFFFD54F), style = Stroke(width = 1.4f))

            // 2. Right Bodhi Leaf (Wisdom - Prajñā) in Luminous Gold
            val rightLeaf = Path().apply {
                moveTo(w * 0.54f, h * 0.88f)
                cubicTo(w * 0.88f, h * 0.74f, w * 0.92f, h * 0.36f, w * 0.58f, h * 0.18f)
                cubicTo(w * 0.64f, h * 0.46f, w * 0.60f, h * 0.70f, w * 0.54f, h * 0.88f)
                close()
            }
            drawPath(
                rightLeaf,
                brush = Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA000)))
            )
            drawPath(rightLeaf, Color(0xFFFFF176), style = Stroke(width = 1.4f))

            // Central Sacred Flame Tip / Sprout
            val centerSprout = Path().apply {
                moveTo(w * 0.50f, h * 0.88f)
                quadraticBezierTo(w * 0.44f, h * 0.40f, w * 0.50f, h * 0.10f)
                quadraticBezierTo(w * 0.56f, h * 0.40f, w * 0.50f, h * 0.88f)
                close()
            }
            drawPath(centerSprout, Color(0xFFFFD54F))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (isSinhala) 0.sp else 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = if (isSinhala) "$leavesCount පින් පත්‍ර වැඩෙයි" else "$leavesCount leaves thriving",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.testTag("merit_tree_app_logo")
    ) {
        Design1BodhiHeartLeaf(title = treeTitle, leavesCount = activeLeavesCount, isSinhala = strings.isSinhala)
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
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer jewel emblem
        drawRoundRect(
            color = Color(0xFF133946),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = Color(0xFFFFD54F),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Sacred Bodhi tree in pure vector inside
        val trunk = Path().apply {
            moveTo(w * 0.5f, h * 0.82f)
            lineTo(w * 0.5f, h * 0.40f)
        }
        drawPath(trunk, Color(0xFFFFD54F), style = Stroke(width = 2.2f, cap = StrokeCap.Round))
        drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.60f), Offset(w * 0.28f, h * 0.46f), strokeWidth = 1.8f, cap = StrokeCap.Round)
        drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.60f), Offset(w * 0.72f, h * 0.46f), strokeWidth = 1.8f, cap = StrokeCap.Round)
        drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.48f), Offset(w * 0.36f, h * 0.28f), strokeWidth = 1.6f, cap = StrokeCap.Round)
        drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.48f), Offset(w * 0.64f, h * 0.28f), strokeWidth = 1.6f, cap = StrokeCap.Round)
        drawLine(Color(0xFFFFD54F), Offset(w * 0.5f, h * 0.40f), Offset(w * 0.50f, h * 0.20f), strokeWidth = 1.6f, cap = StrokeCap.Round)

        drawCircle(Color(0xFF81C784), radius = 2.6f, center = Offset(w * 0.28f, h * 0.46f))
        drawCircle(Color(0xFF81C784), radius = 2.6f, center = Offset(w * 0.72f, h * 0.46f))
        drawCircle(Color(0xFFFFD54F), radius = 2.8f, center = Offset(w * 0.36f, h * 0.28f))
        drawCircle(Color(0xFFFFD54F), radius = 2.8f, center = Offset(w * 0.64f, h * 0.28f))
        drawCircle(Color(0xFFFFD54F), radius = 3.2f, center = Offset(w * 0.50f, h * 0.20f))
    }
}
