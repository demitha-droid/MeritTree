package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import kotlin.math.hypot
import kotlin.math.sin

data class LeafTouchHitBox(
    val slotId: Int,
    val merit: MeritEntity?,
    val center: Offset,
    val radius: Float
)

@Composable
fun BodhiTreeCanvas(
    merits: List<MeritEntity>,
    newlySproutedId: Long?,
    onLeafClick: (MeritEntity) -> Unit,
    onEmptyLeafClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Subtle ambient wind sway (smooth & lightweight)
    val infiniteTransition = rememberInfiniteTransition(label = "bodhi_wind")
    val windSway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wind_sway"
    )

    // Gentle sun halo pulse
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_glow"
    )

    var leafHitBoxes by remember { mutableStateOf<List<LeafTouchHitBox>>(emptyList()) }

    BoxWithConstraints(
        modifier = modifier
            .testTag("bodhi_tree_canvas_container")
            .fillMaxSize()
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Precompute and cache tree paths on dimension change (zero allocations during draw!)
        val treePaths = remember(width, height) {
            BodhiTreeGeometry.buildTreePaths(width, height)
        }

        // Precompute canonical leaf shapes (cached)
        val canonicalLeafPath = remember { BodhiTreeGeometry.createBodhiLeafShape(scale = 1.0f) }
        val canonicalVeinsPath = remember { BodhiTreeGeometry.createBodhiVeinsPath(scale = 1.0f) }

        // Map merits by their slot index
        val meritsBySlot = remember(merits) {
            merits.associateBy { it.branchIndex }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(merits) {
                    detectTapGestures { tapOffset ->
                        val hit = leafHitBoxes.firstOrNull { hitBox ->
                            val dist = hypot(tapOffset.x - hitBox.center.x, tapOffset.y - hitBox.center.y)
                            dist <= hitBox.radius * 1.5f
                        }
                        if (hit != null) {
                            if (hit.merit != null) {
                                onLeafClick(hit.merit)
                            } else {
                                onEmptyLeafClick(hit.slotId)
                            }
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Serene Backdrop with Gentle Golden Halo
            drawCircularCanopyHalo(canvasW, canvasH, auraGlow)

            // 2. Soft Ground Contact Shadow & Delicate Soil Stipples (Zero harsh blocks!)
            drawRootContactShadow(canvasW, canvasH)

            // 3. Render Circular Bodhi Tree Trunk, Root Tendrils & Branches
            drawBodhiTreeWood(treePaths, canvasW, canvasH)

            // 4. Fill the entire tree with leaves!
            // Dormant leaves: plain leaf shape without inner design
            // Awakened leaves: full inner skeleton veins, glowing aura & dewdrop jewel in middle
            val slots = BodhiTreeGeometry.LEAF_SLOTS
            val hitBoxes = mutableListOf<LeafTouchHitBox>()

            slots.forEach { slot ->
                val meritForSlot = meritsBySlot[slot.id]
                val hitBox = if (meritForSlot != null) {
                    // Awakened leaf: inner design appears inside the leaf!
                    renderAwakenedBodhiLeaf(
                        merit = meritForSlot,
                        slot = slot,
                        canvasW = canvasW,
                        canvasH = canvasH,
                        windSway = windSway,
                        canonicalLeafPath = canonicalLeafPath,
                        canonicalVeinsPath = canonicalVeinsPath,
                        isNewlySprouted = meritForSlot.id == newlySproutedId
                    )
                } else {
                    // Plain dormant leaf: fills the tree canopy, without inner design
                    renderPlainBodhiLeaf(
                        slot = slot,
                        canvasW = canvasW,
                        canvasH = canvasH,
                        windSway = windSway,
                        canonicalLeafPath = canonicalLeafPath
                    )
                }
                hitBoxes.add(hitBox)
            }

            leafHitBoxes = hitBoxes
        }
    }
}

/**
 * Draws the soft circular golden halo behind the tree canopy matching the mandala style.
 */
private fun DrawScope.drawCircularCanopyHalo(width: Float, height: Float, auraGlow: Float) {
    val center = Offset(width * 0.50f, height * 0.44f)
    val radius = (width * 0.45f) * auraGlow

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x20FFE082), // Soft golden sunlight
                Color(0x12FFD54F),
                Color(0x0681C784), // Gentle leaf breath
                Color.Transparent
            ),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius
    )
}

/**
 * Draws natural, soft grounding shadows and fine botanical etching stipples directly under the roots.
 */
private fun DrawScope.drawRootContactShadow(width: Float, height: Float) {
    val trunkBaseX = width * 0.50f
    val rootY = height * 0.88f

    // Soft oval contact shadow under the roots
    val shadowWidth = width * 0.52f
    val shadowHeight = height * 0.024f

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x2226140E),
                Color(0x1026140E),
                Color.Transparent
            ),
            center = Offset(trunkBaseX, rootY + (height * 0.008f)),
            radius = shadowWidth * 0.5f
        ),
        topLeft = Offset(trunkBaseX - (shadowWidth * 0.5f), rootY),
        size = androidx.compose.ui.geometry.Size(shadowWidth, shadowHeight)
    )

    // Fine organic ground stipples / earth dust specks matching reference image
    val stipples = listOf(
        Offset(trunkBaseX - (width * 0.22f), rootY + 2f),
        Offset(trunkBaseX - (width * 0.18f), rootY + 5f),
        Offset(trunkBaseX - (width * 0.14f), rootY + 8f),
        Offset(trunkBaseX - (width * 0.09f), rootY + 11f),
        Offset(trunkBaseX - (width * 0.04f), rootY + 12f),
        Offset(trunkBaseX, rootY + 13f),
        Offset(trunkBaseX + (width * 0.04f), rootY + 12f),
        Offset(trunkBaseX + (width * 0.09f), rootY + 11f),
        Offset(trunkBaseX + (width * 0.14f), rootY + 8f),
        Offset(trunkBaseX + (width * 0.18f), rootY + 5f),
        Offset(trunkBaseX + (width * 0.22f), rootY + 2f),
        Offset(trunkBaseX - (width * 0.25f), rootY + 4f),
        Offset(trunkBaseX - (width * 0.16f), rootY + 10f),
        Offset(trunkBaseX + (width * 0.16f), rootY + 10f),
        Offset(trunkBaseX + (width * 0.25f), rootY + 4f)
    )

    stipples.forEach { pos ->
        drawCircle(
            color = Color(0x384A251B),
            radius = 1.6f,
            center = pos
        )
    }
}

/**
 * Draws the carved mahogany trunk, splayed root tendrils, radiating boughs, and sub-twigs.
 */
private fun DrawScope.drawBodhiTreeWood(paths: TreePaths, width: Float, height: Float) {
    val trunkBaseX = width * 0.50f

    // 1. Trunk Body Fill (Rich deep mahogany gradient)
    drawPath(
        path = paths.trunkPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF26140E),
                Color(0xFF4A251B),
                Color(0xFF6E3827),
                Color(0xFF422117),
                Color(0xFF23120C)
            ),
            startX = trunkBaseX - (width * 0.20f),
            endX = trunkBaseX + (width * 0.20f)
        )
    )

    // 2. Trunk Outline Stroke for crisp definition
    drawPath(
        path = paths.trunkPath,
        color = Color(0xFF26140E),
        style = Stroke(width = 1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 3. Splayed Root Tendrils
    drawPath(
        path = paths.rootTendrilsPath,
        color = Color(0xFF381B13),
        style = Stroke(
            width = 4.2f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
    drawPath(
        path = paths.rootTendrilsPath,
        color = Color(0xFF6E3827),
        style = Stroke(
            width = 1.8f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 4. Trunk Vertical Bark Grain Lines flowing into roots
    val grainOffsets = listOf(-0.035f, -0.020f, -0.008f, 0f, 0.008f, 0.020f, 0.035f)
    grainOffsets.forEach { offsetRatio ->
        val startX = trunkBaseX + (width * offsetRatio)
        val endX = trunkBaseX + (width * offsetRatio * 2.2f)
        drawLine(
            color = Color(0xFF1E0E08).copy(alpha = 0.55f),
            start = Offset(startX, height * 0.58f),
            end = Offset(endX, height * 0.88f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
    }

    // 5. Heavy Arching Boughs
    drawPath(
        path = paths.heavyBoughsPath,
        color = Color(0xFF381B13),
        style = Stroke(
            width = 12f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
    drawPath(
        path = paths.heavyBoughsPath,
        color = Color(0xFF5D2E21),
        style = Stroke(
            width = 5.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 6. Fine Branch Twigs reaching to leaf slots
    drawPath(
        path = paths.twigsPath,
        color = Color(0xFF432017),
        style = Stroke(
            width = 3.2f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
    drawPath(
        path = paths.twigsPath,
        color = Color(0xFF663426),
        style = Stroke(
            width = 1.4f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Renders a plain dormant Bodhi leaf (fills the tree, without inner design in the middle).
 * "fill the tree with the leafs, but dont add the leaf design in the middle"
 */
private fun DrawScope.renderPlainBodhiLeaf(
    slot: BodhiLeafSlot,
    canvasW: Float,
    canvasH: Float,
    windSway: Float,
    canonicalLeafPath: Path
): LeafTouchHitBox {
    val leafX = slot.xRatio * canvasW
    val leafY = slot.yRatio * canvasH
    val leafCenter = Offset(leafX, leafY)

    val swayOffset = sin((slot.id * 1.7f) + (windSway * 1.4f)) * 3.5f
    val currentAngle = slot.leafAngle + swayOffset

    val leafBaseScale = (canvasW / 390f) * 1.05f * slot.scaleFactor
    val hitRadius = 30f * leafBaseScale

    translate(left = leafCenter.x, top = leafCenter.y) {
        rotate(degrees = currentAngle, pivot = Offset.Zero) {
            scale(scaleX = leafBaseScale, scaleY = leafBaseScale, pivot = Offset.Zero) {
                // 1. Soft translucent copper silhouette fill (without inner design)
                drawPath(
                    path = canonicalLeafPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x35E29E7D),
                            Color(0x28D48B69),
                            Color(0x20B86C4B)
                        ),
                        startY = -28f,
                        endY = 14f
                    ),
                    style = Fill
                )

                // 2. Delicate copper leaf outline
                drawPath(
                    path = canonicalLeafPath,
                    color = Color(0x754A1F13),
                    style = Stroke(width = 1.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // NOTICE: No inner skeleton veins and no center jewel drawn here!
            }
        }
    }

    return LeafTouchHitBox(
        slotId = slot.id,
        merit = null,
        center = leafCenter,
        radius = hitRadius
    )
}

/**
 * Renders an awakened Bodhi leaf with the full exquisite design inside:
 * glowing category essence, intricate skeleton veins, and central sacred dewdrop jewel!
 * "when i add a merit that design appears inside the leaf"
 */
private fun DrawScope.renderAwakenedBodhiLeaf(
    merit: MeritEntity,
    slot: BodhiLeafSlot,
    canvasW: Float,
    canvasH: Float,
    windSway: Float,
    canonicalLeafPath: Path,
    canonicalVeinsPath: Path,
    isNewlySprouted: Boolean
): LeafTouchHitBox {
    val category = MeritCategory.fromString(merit.category)

    val leafX = slot.xRatio * canvasW
    val leafY = slot.yRatio * canvasH
    val leafCenter = Offset(leafX, leafY)

    val swayOffset = sin((slot.id * 1.7f) + (windSway * 1.4f)) * 3.5f
    val currentAngle = slot.leafAngle + swayOffset

    val leafBaseScale = (canvasW / 390f) * 1.05f * slot.scaleFactor
    val hitRadius = 32f * leafBaseScale

    if (isNewlySprouted) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD54F).copy(alpha = 0.85f),
                    Color(0xFFFFB74D).copy(alpha = 0.40f),
                    Color.Transparent
                ),
                center = leafCenter,
                radius = hitRadius * 2.4f
            ),
            radius = hitRadius * 2.4f,
            center = leafCenter
        )
    }

    translate(left = leafCenter.x, top = leafCenter.y) {
        rotate(degrees = currentAngle, pivot = Offset.Zero) {
            scale(scaleX = leafBaseScale, scaleY = leafBaseScale, pivot = Offset.Zero) {

                // 1. Rich Luminous Copper / Rose-Gold Bodhi Leaf Body
                drawPath(
                    path = canonicalLeafPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE29E7D),
                            Color(0xFFD48B69),
                            Color(0xFFB86C4B)
                        ),
                        startY = -28f,
                        endY = 14f
                    ),
                    style = Fill
                )

                // 2. Category Essence Glow appearing inside the leaf
                drawCircle(
                    color = category.leafColor.copy(alpha = 0.35f),
                    radius = 9f,
                    center = Offset(0f, 0f)
                )

                // 3. Crisp Dark Copper Leaf Perimeter Outline
                drawPath(
                    path = canonicalLeafPath,
                    color = Color(0xFF4A1F13),
                    style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // 4. THE DESIGN IN THE MIDDLE: Intricate Skeleton Veins
                drawPath(
                    path = canonicalVeinsPath,
                    color = Color(0xFF5A2518).copy(alpha = 0.88f),
                    style = Stroke(width = 0.95f, cap = StrokeCap.Round)
                )

                // 5. THE DESIGN IN THE MIDDLE: Central Sacred Dewdrop Jewel
                drawCircle(
                    color = category.accentColor,
                    radius = 2.4f,
                    center = Offset(0f, -2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.95f),
                    radius = 1.1f,
                    center = Offset(-0.4f, -2.4f)
                )
            }
        }
    }

    return LeafTouchHitBox(
        slotId = slot.id,
        merit = merit,
        center = leafCenter,
        radius = hitRadius
    )
}
