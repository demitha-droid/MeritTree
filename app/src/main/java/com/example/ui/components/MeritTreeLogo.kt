package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.i18n.LocalAppStrings

// ================================================================================================
// BESPOKE TYPOGRAPHY FOR MERIT TREE LOGO (CINZEL)
// ================================================================================================

val CinzelFamily = FontFamily(Font(R.font.cinzel, FontWeight.Normal))

// ================================================================================================
// EXACT HOME SCREEN BODHI TREE LEAF VECTOR RENDERER
// ================================================================================================

enum class HomeScreenLeafStyle {
    SacredCopper, // The exact default awakened leaf from the home screen tree
    GoldenMerit,  // Radiant golden awakened leaf
    EmeraldJade,  // Fresh emerald awakened leaf
    SapphireBlue, // Sacred contemplation sapphire leaf
    RubyRose      // Compassion ruby leaf
}

private data class LeafPalette(
    val brush: Brush,
    val outlineColor: Color,
    val veinColor: Color,
    val jewelColor: Color,
    val auraColor: Color
)

@Composable
fun HomeScreenTreeLeafCanvas(
    modifier: Modifier = Modifier,
    style: HomeScreenLeafStyle = HomeScreenLeafStyle.SacredCopper,
    scaleFactor: Float = 1.0f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val baseW = 36f
        val baseH = 42f
        val scale = (minOf(w / (baseW * 1.15f), h / (baseH * 1.15f))) * scaleFactor

        val centerX = w * 0.5f
        val centerY = h * 0.5f + (7f * scale)

        translate(left = centerX, top = centerY) {
            val leafPath = BodhiTreeGeometry.createBodhiLeafShape(scale)
            val veinsPath = BodhiTreeGeometry.createBodhiVeinsPath(scale)

            val palette = when (style) {
                HomeScreenLeafStyle.SacredCopper -> LeafPalette(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE29E7D), Color(0xFFD48B69), Color(0xFFB86C4B)),
                        startY = -28f * scale,
                        endY = 14f * scale
                    ),
                    outlineColor = Color(0xFF4A1F13),
                    veinColor = Color(0xFF5A2518).copy(alpha = 0.92f),
                    jewelColor = Color(0xFFFFB300),
                    auraColor = Color(0x60D48B69)
                )
                HomeScreenLeafStyle.GoldenMerit -> LeafPalette(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFE082), Color(0xFFFFD54F), Color(0xFFFFA000)),
                        startY = -28f * scale,
                        endY = 14f * scale
                    ),
                    outlineColor = Color(0xFF7A4F00),
                    veinColor = Color(0xFF9E6500).copy(alpha = 0.90f),
                    jewelColor = Color(0xFFFFF9C4),
                    auraColor = Color(0x65FFD54F)
                )
                HomeScreenLeafStyle.EmeraldJade -> LeafPalette(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFA5D6A7), Color(0xFF4CAF50), Color(0xFF2E7D32)),
                        startY = -28f * scale,
                        endY = 14f * scale
                    ),
                    outlineColor = Color(0xFF1B5E20),
                    veinColor = Color(0xFF0F3D13).copy(alpha = 0.90f),
                    jewelColor = Color(0xFFFFD54F),
                    auraColor = Color(0x604CAF50)
                )
                HomeScreenLeafStyle.SapphireBlue -> LeafPalette(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF90CAF9), Color(0xFF2196F3), Color(0xFF1565C0)),
                        startY = -28f * scale,
                        endY = 14f * scale
                    ),
                    outlineColor = Color(0xFF0D47A1),
                    veinColor = Color(0xFF072B6B).copy(alpha = 0.90f),
                    jewelColor = Color(0xFFE3F2FD),
                    auraColor = Color(0x602196F3)
                )
                HomeScreenLeafStyle.RubyRose -> LeafPalette(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFEF9A9A), Color(0xFFE91E63), Color(0xFFAD1457)),
                        startY = -28f * scale,
                        endY = 14f * scale
                    ),
                    outlineColor = Color(0xFF880E4F),
                    veinColor = Color(0xFF560027).copy(alpha = 0.90f),
                    jewelColor = Color(0xFFFFF9C4),
                    auraColor = Color(0x60E91E63)
                )
            }

            // Soft category aura glow inside
            drawCircle(
                color = palette.auraColor,
                radius = 10f * scale,
                center = Offset(0f, -2f * scale)
            )

            // Leaf Body
            drawPath(path = leafPath, brush = palette.brush, style = Fill)

            // Crisp perimeter outline
            drawPath(
                path = leafPath,
                color = palette.outlineColor,
                style = Stroke(width = 1.35f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Skeleton Veins
            drawPath(
                path = veinsPath,
                color = palette.veinColor,
                style = Stroke(width = 1.0f * scale, cap = StrokeCap.Round)
            )

            // Sacred Central Dewdrop Jewel
            drawCircle(
                color = palette.jewelColor,
                radius = 2.5f * scale,
                center = Offset(0f, -2f * scale)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = 1.1f * scale,
                center = Offset(-0.5f * scale, -2.5f * scale)
            )
        }
    }
}

/**
 * 1.5x Magnified Dual Harmony Leaves Icon (User Selected Design)
 */
@Composable
fun DualHarmonyLeavesIcon(
    modifier: Modifier = Modifier,
    scaleFactor: Float = 1.0f
) {
    Box(
        modifier = modifier.size(width = (64 * scaleFactor).dp, height = (54 * scaleFactor).dp),
        contentAlignment = Alignment.Center
    ) {
        // Golden leaf tilted left (1.5x larger: 45dp)
        HomeScreenTreeLeafCanvas(
            style = HomeScreenLeafStyle.GoldenMerit,
            scaleFactor = 0.88f * scaleFactor,
            modifier = Modifier
                .size((45 * scaleFactor).dp)
                .offset(x = ((-9) * scaleFactor).dp, y = ((3) * scaleFactor).dp)
                .rotate(-18f)
        )
        // Classic Copper leaf tilted slightly right (1.5x larger: 51dp)
        HomeScreenTreeLeafCanvas(
            style = HomeScreenLeafStyle.SacredCopper,
            scaleFactor = 0.98f * scaleFactor,
            modifier = Modifier
                .size((51 * scaleFactor).dp)
                .offset(x = ((6) * scaleFactor).dp, y = ((-2) * scaleFactor).dp)
                .rotate(14f)
        )
    }
}

// ================================================================================================
// DESIGN 10: SACRED CROWNED 'M' (CINZEL) - DEFINITIVE LOGO
// ================================================================================================

/**
 * Sacred Crowned 'M' (Font: Cinzel)
 * A stately classical Roman 'M' in Cinzel where the dual leaves physically perch as a royal crest
 * right upon the apex of its primary column, with a golden vine spiraling down its column and a golden crown gem.
 */
@Composable
fun SacredCrownedMeritLogo(
    activeLeavesCount: Int,
    isSinhala: Boolean,
    modifier: Modifier = Modifier,
    scale: Float = 1.0f
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxHeight()
    ) {
        DualHarmonyLeavesIcon(scaleFactor = scale)
        Box(
            modifier = Modifier.weight(1f, fill = false),
            contentAlignment = Alignment.CenterStart
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val h = size.height

                // Golden vine spiraling down the left column of 'M'
                val spiral = Path().apply {
                    moveTo(-12f, h * 0.38f)
                    cubicTo(
                        -2f, h * 0.42f,
                        4f, h * 0.58f,
                        8f, h * 0.72f
                    )
                }
                drawPath(
                    path = spiral,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                    ),
                    style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                )

                // Golden crown gem
                drawCircle(color = Color(0xFFFFD54F), radius = 2.2f, center = Offset(10f, h * 0.32f))
            }

            Column(modifier = Modifier.padding(start = 8.dp)) {
                if (isSinhala) {
                    Text(
                        text = "පුණ්ය වෘක්ෂය",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFFC5A059),
                        modifier = Modifier.testTag("merit_tree_top_bar_title")
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.testTag("merit_tree_top_bar_title")
                    ) {
                        Text(
                            text = "M",
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color(0xFFC5A059)
                        )
                        Text(
                            text = "ERIT TREE",
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            letterSpacing = 1.4.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 2.dp, start = 1.dp)
                        )
                    }
                }
                Text(
                    text = if (isSinhala) "$activeLeavesCount කුසල් පත්‍ර වැඩෙයි" else "$activeLeavesCount merit leaves",
                    fontFamily = CinzelFamily,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                    color = Color(0xFFC5A059)
                )
            }
        }
    }
}

// ================================================================================================
// TOP APP BAR COMPONENT
// ================================================================================================

@Composable
fun MeritTreeTopBar(
    activeLeavesCount: Int,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    currentStyle: Int = 10,
    onStyleChange: (Int) -> Unit = {}
) {
    val strings = LocalAppStrings.current

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
                    .height(68.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left & Center: Sacred Crowned 'M' Logo (Design 10)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    SacredCrownedMeritLogo(
                        activeLeavesCount = activeLeavesCount,
                        isSinhala = strings.isSinhala
                    )
                }

                // Right Action: Settings Icon where water drop used to be
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("top_bar_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = strings.settingsTitle,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Bottom Boundary Line: Crisp divider with subtle gold tint
            HorizontalDivider(
                color = Color(0x50FFD54F),
                thickness = 1.dp,
                modifier = Modifier.testTag("merit_tree_top_divider_line")
            )
        }
    }
}

/**
 * Exact App Launcher Icon Tile rendered natively in Compose.
 * Features exclusively the two sacred Bodhi leaves (Golden Merit & Sacred Copper)
 * from the app, centered on the midnight emerald jade field with golden aura rings.
 */
@Composable
fun ExactDesign10AppIconTile(
    size: Dp = 100.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(size * 0.22f),
    showBorder: Boolean = true,
    scale: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = shape,
        color = Color(0xFF0A231C),
        border = if (showBorder) BorderStroke(1.8.dp, Color(0xFFFFD54F)) else null,
        shadowElevation = 8.dp,
        modifier = modifier.size(size)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Subtle enlightenment background halo
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = this.size.width
                val h = this.size.height

                // Radial dark emerald backdrop
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF144537), Color(0xFF0A231C), Color(0xFF051712)),
                        center = Offset(w * 0.5f, h * 0.48f),
                        radius = w * 0.72f
                    )
                )

                // Golden enlightenment aura rings
                drawCircle(
                    color = Color(0x20FFD54F),
                    radius = w * 0.40f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                drawCircle(
                    color = Color(0x35FFD54F),
                    radius = w * 0.32f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(width = 1.5f)
                )
            }

            // Foreground: The 2 Sacred Bodhi Leaves prominently centered
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val leafScale = (size.value / 64f) * 0.96f * scale
                DualHarmonyLeavesIcon(
                    scaleFactor = leafScale,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Emblem wrapper for About and other sections.
 */
@Composable
fun MeritTreeEmblem(
    size: Dp = 40.dp,
    animatedGlow: Boolean = false,
    modifier: Modifier = Modifier
) {
    ExactDesign10AppIconTile(
        size = size,
        modifier = modifier
    )
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
    SacredCrownedMeritLogo(
        activeLeavesCount = activeLeavesCount,
        isSinhala = strings.isSinhala,
        modifier = modifier.testTag("merit_tree_app_logo")
    )
}
