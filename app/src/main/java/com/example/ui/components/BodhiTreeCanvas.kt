package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import com.example.data.getFirstMediaUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Exact canonical Bodhi leaf shape matching the sacred artwork.
 * Used to clip the post's image inside the leaf with zero distortion.
 */
val BodhiLeafShape: Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(0.5f * w, 1.0f * h)
    cubicTo(
        0f * w, (37f / 42f) * h,
        (2f / 36f) * w, (22f / 42f) * h,
        (9f / 36f) * w, (15f / 42f) * h
    )
    cubicTo(
        (14f / 36f) * w, (10f / 42f) * h,
        (17f / 36f) * w, (4f / 42f) * h,
        0.5f * w, 0f
    )
    cubicTo(
        (19f / 36f) * w, (4f / 42f) * h,
        (22f / 36f) * w, (10f / 42f) * h,
        (27f / 36f) * w, (15f / 42f) * h
    )
    cubicTo(
        (34f / 36f) * w, (22f / 42f) * h,
        1.0f * w, (37f / 42f) * h,
        0.5f * w, 1.0f * h
    )
    close()
}

/**
 * Pre-allocated drawing cache holding all geometry paths, gradients, and stipple points.
 * Generated only when canvas width or height changes.
 * Zero allocations during draw frames!
 */
private class BodhiTreeRenderCache(
    val width: Float,
    val height: Float,
    val treePaths: TreePaths,
    val canonicalLeafPath: Path,
    val canonicalVeinsPath: Path,
    val haloCenter: Offset,
    val haloRadius: Float,
    val haloBrush: Brush,
    val shadowCenter: Offset,
    val shadowTopLeft: Offset,
    val shadowSize: Size,
    val shadowBrush: Brush,
    val rootStipples: List<Offset>,
    val trunkGradient: Brush,
    val dormantLeafBrush: Brush,
    val awakenedLeafBrush: Brush
)

/**
 * Instance representing a live sacred explosion animation on the tree.
 */
private class ActiveLeafExplosion(
    val id: Long,
    val center: Offset,
    val categoryColor: Color,
    val animatable: Animatable<Float, AnimationVector1D>
)

@Composable
fun BodhiTreeCanvas(
    merits: List<MeritEntity>,
    newlySproutedId: Long?,
    revealedMeritIds: Set<Long> = emptySet(),
    onRevealLeaf: (MeritEntity) -> Unit = {},
    onLeafClick: (MeritEntity) -> Unit,
    onEmptyLeafClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val activeExplosions = remember { mutableStateListOf<ActiveLeafExplosion>() }

    BoxWithConstraints(
        modifier = modifier
            .testTag("bodhi_tree_canvas_container")
            .fillMaxSize()
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        if (width <= 0f || height <= 0f) {
            return@BoxWithConstraints
        }

        // 1. Build and cache all drawing artifacts on dimension change
        val renderCache = remember(width, height) {
            val trunkBaseX = width * 0.50f
            val rootY = height * 0.88f
            val shadowW = width * 0.54f
            val shadowH = height * 0.026f
            val haloC = Offset(width * 0.50f, height * 0.44f)
            val haloR = width * 0.45f

            val stipplesList = listOf(
                Offset(trunkBaseX - (width * 0.23f), rootY + 2f),
                Offset(trunkBaseX - (width * 0.19f), rootY + 5f),
                Offset(trunkBaseX - (width * 0.15f), rootY + 8f),
                Offset(trunkBaseX - (width * 0.10f), rootY + 11f),
                Offset(trunkBaseX - (width * 0.05f), rootY + 12f),
                Offset(trunkBaseX, rootY + 14f),
                Offset(trunkBaseX + (width * 0.05f), rootY + 12f),
                Offset(trunkBaseX + (width * 0.10f), rootY + 11f),
                Offset(trunkBaseX + (width * 0.15f), rootY + 8f),
                Offset(trunkBaseX + (width * 0.19f), rootY + 5f),
                Offset(trunkBaseX + (width * 0.23f), rootY + 2f),
                Offset(trunkBaseX - (width * 0.26f), rootY + 4f),
                Offset(trunkBaseX - (width * 0.17f), rootY + 10f),
                Offset(trunkBaseX + (width * 0.17f), rootY + 10f),
                Offset(trunkBaseX + (width * 0.26f), rootY + 4f)
            )

            BodhiTreeRenderCache(
                width = width,
                height = height,
                treePaths = BodhiTreeGeometry.buildTreePaths(width, height),
                canonicalLeafPath = BodhiTreeGeometry.createBodhiLeafShape(scale = 1.0f),
                canonicalVeinsPath = BodhiTreeGeometry.createBodhiVeinsPath(scale = 1.0f),
                haloCenter = haloC,
                haloRadius = haloR,
                haloBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x22FFE082),
                        Color(0x14FFD54F),
                        Color(0x0681C784),
                        Color.Transparent
                    ),
                    center = haloC,
                    radius = haloR
                ),
                shadowCenter = Offset(trunkBaseX, rootY + (height * 0.008f)),
                shadowTopLeft = Offset(trunkBaseX - (shadowW * 0.5f), rootY),
                shadowSize = Size(shadowW, shadowH),
                shadowBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2826140E),
                        Color(0x1226140E),
                        Color.Transparent
                    ),
                    center = Offset(trunkBaseX, rootY + (height * 0.008f)),
                    radius = shadowW * 0.5f
                ),
                rootStipples = stipplesList,
                trunkGradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1C0A06),
                        Color(0xFF33140C),
                        Color(0xFF4E2216),
                        Color(0xFF683122),
                        Color(0xFF481F14),
                        Color(0xFF260E08),
                        Color(0xFF1A0905)
                    ),
                    startX = trunkBaseX - (width * 0.18f),
                    endX = trunkBaseX + (width * 0.18f)
                ),
                dormantLeafBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x35E29E7D),
                        Color(0x28D48B69),
                        Color(0x20B86C4B)
                    ),
                    startY = -28f,
                    endY = 14f
                ),
                awakenedLeafBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE29E7D),
                        Color(0xFFD48B69),
                        Color(0xFFB86C4B)
                    ),
                    startY = -28f,
                    endY = 14f
                )
            )
        }

        // 2. Pre-index merits by branch slot (O(1) lookup per leaf slot)
        val meritsBySlot = remember(merits) {
            merits.associateBy { it.branchIndex }
        }

        // 3. Pre-render the entire static tree wood & all dormant leaves into a hardware bitmap
        val cachedStaticTreeBitmap = remember(width, height, renderCache) {
            try {
                if (width <= 0f || height <= 0f) return@remember null
                val bitmap = ImageBitmap(width.toInt(), height.toInt())
                val canvas = androidx.compose.ui.graphics.Canvas(bitmap)
                val drawScope = CanvasDrawScope()
                drawScope.draw(
                    density = density,
                    layoutDirection = LayoutDirection.Ltr,
                    canvas = canvas,
                    size = Size(width, height)
                ) {
                    // 1. Serene Golden Sun Halo
                    drawCircle(
                        brush = renderCache.haloBrush,
                        center = renderCache.haloCenter,
                        radius = renderCache.haloRadius
                    )

                    // 2. Soft Ground Contact Shadow & Botanical Soil Stipples
                    drawOval(
                        brush = renderCache.shadowBrush,
                        topLeft = renderCache.shadowTopLeft,
                        size = renderCache.shadowSize
                    )

                    val stippleColor = Color(0x384A251B)
                    val stipples = renderCache.rootStipples
                    for (i in stipples.indices) {
                        drawCircle(
                            color = stippleColor,
                            radius = 1.6f,
                            center = stipples[i]
                        )
                    }

                    // 3. Render Hand-Carved Sacred Bodhi Wood
                    drawCarvedBodhiTreeWood(renderCache)

                    // 4. Render All Dormant Bodhi Leaves onto the static background
                    val slots = BodhiTreeGeometry.LEAF_SLOTS
                    val leafBaseScale = (width / 390f) * 1.05f

                    for (i in slots.indices) {
                        val slot = slots[i]
                        val leafX = slot.xRatio * width
                        val leafY = slot.yRatio * height
                        val scale = leafBaseScale * slot.scaleFactor

                        translate(left = leafX, top = leafY) {
                            rotate(degrees = slot.leafAngle, pivot = Offset.Zero) {
                                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
                                    renderDormantLeaf(cache = renderCache)
                                }
                            }
                        }
                    }
                }
                bitmap
            } catch (_: Throwable) {
                null
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(meritsBySlot, revealedMeritIds, width, height) {
                    detectTapGestures { tapOffset ->
                        val slots = BodhiTreeGeometry.LEAF_SLOTS
                        val leafBaseScale = (width / 390f) * 1.05f

                        // 1. If a leaf is currently revealed, check if tapped first
                        val revealedMerit = merits.firstOrNull { it.id in revealedMeritIds }
                        if (revealedMerit != null) {
                            val slot = slots.getOrNull(revealedMerit.branchIndex)
                            if (slot != null) {
                                val cx = slot.xRatio * width
                                val cy = slot.yRatio * height
                                val hitRadius = 32f * leafBaseScale * slot.scaleFactor * 1.35f
                                if (hypot(tapOffset.x - cx, tapOffset.y - cy) <= hitRadius) {
                                    onLeafClick(revealedMerit)
                                    return@detectTapGestures
                                }
                            }
                        }

                        // 2. Nearest-neighbor leaf selection so overlapping hitboxes never prevent clicking any leaf
                        val candidates = slots.mapNotNull { slot ->
                            val cx = slot.xRatio * width
                            val cy = slot.yRatio * height
                            val hitRadius = 26f * leafBaseScale * slot.scaleFactor * 1.30f
                            val dist = hypot(tapOffset.x - cx, tapOffset.y - cy)
                            if (dist <= hitRadius) slot to dist else null
                        }
                        val hit = candidates.minByOrNull { it.second }?.first

                        if (hit != null) {
                            val merit = meritsBySlot[hit.id]
                            if (merit != null) {
                                if (merit.id in revealedMeritIds) {
                                    // When user taps that revealed pic, the post pops up!
                                    onLeafClick(merit)
                                } else {
                                    // When user taps an unrevealed leaf, explode and reveal image inside the leaf!
                                    val cx = hit.xRatio * width
                                    val cy = hit.yRatio * height
                                    val category = MeritCategory.fromString(merit.category)
                                    val explosion = ActiveLeafExplosion(
                                        id = System.nanoTime(),
                                        center = Offset(cx, cy),
                                        categoryColor = category.leafColor,
                                        animatable = Animatable(0f)
                                    )
                                    activeExplosions.add(explosion)
                                    coroutineScope.launch {
                                        explosion.animatable.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(450, easing = FastOutSlowInEasing)
                                        )
                                        activeExplosions.remove(explosion)
                                    }
                                    onRevealLeaf(merit)
                                }
                            } else {
                                onEmptyLeafClick(hit.id)
                            }
                        }
                    }
                }
        ) {
            // Draw pre-rendered static tree + dormant leaves in 1 instantaneous call, or fallback
            if (cachedStaticTreeBitmap != null) {
                drawImage(image = cachedStaticTreeBitmap)
            } else {
                drawCircle(
                    brush = renderCache.haloBrush,
                    center = renderCache.haloCenter,
                    radius = renderCache.haloRadius
                )
                drawOval(
                    brush = renderCache.shadowBrush,
                    topLeft = renderCache.shadowTopLeft,
                    size = renderCache.shadowSize
                )
                val stippleColor = Color(0x384A251B)
                val stipples = renderCache.rootStipples
                for (i in stipples.indices) {
                    drawCircle(color = stippleColor, radius = 1.6f, center = stipples[i])
                }
                drawCarvedBodhiTreeWood(renderCache)
                val slots = BodhiTreeGeometry.LEAF_SLOTS
                val leafBaseScale = (width / 390f) * 1.05f
                for (i in slots.indices) {
                    val slot = slots[i]
                    val leafX = slot.xRatio * width
                    val leafY = slot.yRatio * height
                    val scale = leafBaseScale * slot.scaleFactor
                    translate(left = leafX, top = leafY) {
                        rotate(degrees = slot.leafAngle, pivot = Offset.Zero) {
                            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
                                renderDormantLeaf(cache = renderCache)
                            }
                        }
                    }
                }
            }

            // Always render awakened leaves on Canvas so when 2-second revealed pic reverts, natural leaf is seamlessly visible
            val slots = BodhiTreeGeometry.LEAF_SLOTS
            val leafBaseScale = (width / 390f) * 1.05f

            for (merit in merits) {
                val slot = slots.getOrNull(merit.branchIndex) ?: continue
                val leafX = slot.xRatio * width
                val leafY = slot.yRatio * height
                val scale = leafBaseScale * slot.scaleFactor

                translate(left = leafX, top = leafY) {
                    rotate(degrees = slot.leafAngle, pivot = Offset.Zero) {
                        scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
                            renderAwakenedLeaf(
                                merit = merit,
                                cache = renderCache,
                                isNewlySprouted = merit.id == newlySproutedId
                            )
                        }
                    }
                }
            }
        }

        // 4. Render Revealed Leaves displaying the linked post's image inside the Bodhi leaf!
        // Sized compactly (48dp x 58dp) so it perfectly frames the leaf without overflowing nearby branches
        val slots = BodhiTreeGeometry.LEAF_SLOTS
        val leafWidth = 48.dp
        val leafHeight = 58.dp
        val leafWidthPx = with(density) { leafWidth.toPx() }
        val leafHeightPx = with(density) { leafHeight.toPx() }

        for (merit in merits) {
            if (merit.id !in revealedMeritIds) continue
            val slot = slots.getOrNull(merit.branchIndex) ?: continue
            val cx = slot.xRatio * width
            val cy = slot.yRatio * height
            val category = MeritCategory.fromString(merit.category)

            val leftPx = cx - (leafWidthPx * 0.5f)
            val topPx = cy - (leafHeightPx * 0.45f)

            RevealedBodhiLeafPic(
                merit = merit,
                category = category,
                onPicClick = { onLeafClick(merit) },
                modifier = Modifier
                    .offset { IntOffset(leftPx.roundToInt(), topPx.roundToInt()) }
                    .size(leafWidth, leafHeight)
            )
        }

        // 5. Sacred Explosion Particle & Shockwave Overlay (60/120 FPS hardware accelerated)
        if (activeExplosions.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (explosion in activeExplosions) {
                    val p = explosion.animatable.value
                    val c = explosion.center
                    val catColor = explosion.categoryColor

                    // A. Golden Shockwave Ring
                    val r1 = (10f + 56f * p) * density.density
                    val sw1 = (4f * (1f - p)).coerceAtLeast(0.5f) * density.density
                    drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = (1f - p).coerceIn(0f, 1f)),
                        radius = r1,
                        center = c,
                        style = Stroke(width = sw1)
                    )

                    // B. Inner Sacred White Shockwave Ring
                    val r2 = (6f + 38f * p) * density.density
                    val sw2 = (2.5f * (1f - p)).coerceAtLeast(0.5f) * density.density
                    drawCircle(
                        color = Color.White.copy(alpha = ((1f - p) * 0.9f).coerceIn(0f, 1f)),
                        radius = r2,
                        center = c,
                        style = Stroke(width = sw2)
                    )

                    // C. Central Radiance Burst Flash
                    if (p < 0.65f) {
                        val flashAlpha = ((0.65f - p) / 0.65f).coerceIn(0f, 1f)
                        drawCircle(
                            color = Color(0xFFFFF9C4).copy(alpha = flashAlpha * 0.85f),
                            radius = (24f * (1f - p)) * density.density,
                            center = c
                        )
                    }

                    // D. 12 Sacred Sparkle Particles radiating outward
                    val dist = (12f + 54f * (1f - (1f - p) * (1f - p))) * density.density
                    val pAlpha = (1f - p).coerceIn(0f, 1f)
                    for (i in 0 until 12) {
                        val rad = Math.toRadians((i * 30.0 + (i * 7.0)))
                        val px = c.x + (cos(rad) * dist).toFloat()
                        val py = c.y + (sin(rad) * dist).toFloat()
                        val pColor = when (i % 3) {
                            0 -> Color(0xFFFFD54F)
                            1 -> catColor
                            else -> Color.White
                        }
                        val pRadius = (3.6f * (1f - p * 0.6f)) * density.density
                        drawCircle(
                            color = pColor.copy(alpha = pAlpha),
                            radius = pRadius,
                            center = Offset(px, py)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renders the revealed post image beautifully inside the sacred Bodhi leaf shape.
 * Framed with a golden border, vein overlay, and serene breathing glow.
 * Tapping it pops up the full post dialog.
 */
@Composable
private fun RevealedBodhiLeafPic(
    merit: MeritEntity,
    category: MeritCategory,
    onPicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.2f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(merit.id) {
        // 1. Pop-in with bouncy spring
        launch {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.58f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            alphaAnim.animateTo(1f, animationSpec = tween(140))
        }

        // 2. Stay revealed so the user can view the picture and tap it
        delay(1750L)

        // 3. Smoothly dissolve back to natural leaf state at 2.0 seconds
        launch {
            scaleAnim.animateTo(0.65f, animationSpec = tween(250, easing = FastOutSlowInEasing))
        }
        launch {
            alphaAnim.animateTo(0f, animationSpec = tween(250))
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "revealed_leaf_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim.value * pulseGlow)
            .alpha(alphaAnim.value)
            .shadow(
                elevation = 8.dp,
                shape = BodhiLeafShape,
                ambientColor = Color(0xFFFFD54F),
                spotColor = category.leafColor
            )
            .clip(BodhiLeafShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFDE7), category.leafColor.copy(alpha = 0.35f))
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // "when i tap that pic post should pop up"
                onPicClick()
            }
            .testTag("revealed_leaf_pic_${merit.id}"),
        contentAlignment = Alignment.Center
    ) {
        // 1. If media was uploaded, display it inside the leaf; otherwise, render a pure sacred Bodhi leaf
        val firstPic = merit.getFirstMediaUri()
        if (!firstPic.isNullOrBlank()) {
            RenderMeritImage(
                imageUri = firstPic,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                category.leafColor.copy(alpha = 0.92f),
                                category.leafColor
                            )
                        )
                    )
            )
        }

        // 2. Translucent golden leaf skeleton veins overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val veinColor = Color(0x75FFD54F)
            drawLine(
                color = veinColor,
                start = Offset(0.5f * w, 0.96f * h),
                end = Offset(0.5f * w, 0.08f * h),
                strokeWidth = 1.5f
            )
            val veinY = listOf(0.35f, 0.48f, 0.62f, 0.76f)
            for ((idx, y) in veinY.withIndex()) {
                val span = (w * 0.30f) * (1f - idx * 0.12f)
                drawLine(
                    color = veinColor,
                    start = Offset(0.5f * w, y * h),
                    end = Offset(0.5f * w - span, (y - 0.08f) * h),
                    strokeWidth = 1.0f
                )
                drawLine(
                    color = veinColor,
                    start = Offset(0.5f * w, y * h),
                    end = Offset(0.5f * w + span, (y - 0.08f) * h),
                    strokeWidth = 1.0f
                )
            }
        }

        // 3. Golden Rim Border framing the sacred leaf shape
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFD54F),
                            category.leafColor,
                            Color(0xFFFFB300)
                        )
                    ),
                    shape = BodhiLeafShape
                )
        )

        // 4. Luminous apex dewdrop jewel
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 1.5.dp)
                .size(5.dp)
                .background(Color(0xFFFFD54F), CircleShape)
        )
    }
}

/**
 * Draws the hand-carved mahogany trunk, roots, and 4 tiers of boughs.
 * Uses cached paths and pre-allocated brushes for ultra-fast, zero-jank 60/120 FPS rendering.
 */
private fun DrawScope.drawCarvedBodhiTreeWood(cache: BodhiTreeRenderCache) {
    val paths = cache.treePaths

    // 1. Trunk Base Fill (Mahogany gradient)
    drawPath(path = paths.trunkSilhouettePath, brush = cache.trunkGradient)

    // Woodcut outline
    drawPath(
        path = paths.trunkSilhouettePath,
        color = Color(0xFF140603),
        style = Stroke(width = 1.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. Trunk Fissures & Bark Furrows
    drawPath(
        path = paths.trunkFissuresPath,
        color = Color(0xFF100402),
        style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.trunkFissuresPath,
        color = Color(0xFF240D08),
        style = Stroke(width = 1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 3. Trunk Satin Highlights
    drawPath(
        path = paths.trunkHighlightsPath,
        color = Color(0xFF8F4632).copy(alpha = 0.85f),
        style = Stroke(width = 2.2f, cap = StrokeCap.Round)
    )
    drawPath(
        path = paths.trunkHighlightsPath,
        color = Color(0xFFB8664D).copy(alpha = 0.60f),
        style = Stroke(width = 1.0f, cap = StrokeCap.Round)
    )

    // 4. Gnarled Roots
    drawPath(
        path = paths.gnarledRootsPath,
        color = Color(0xFF160704),
        style = Stroke(width = 6.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.gnarledRootsPath,
        color = Color(0xFF4A2016),
        style = Stroke(width = 3.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.rootHighlightsPath,
        color = Color(0xFF944A35),
        style = Stroke(width = 1.6f, cap = StrokeCap.Round)
    )

    // 5. Major Arching Boughs (4 Tiers)
    drawPath(
        path = paths.majorBoughsShadowPath,
        color = Color(0xFF140603),
        style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.majorBoughsBodyPath,
        color = Color(0xFF4C2317),
        style = Stroke(width = 8.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.majorBoughsHighlightPath,
        color = Color(0xFF9E4E37),
        style = Stroke(width = 2.8f, cap = StrokeCap.Round)
    )
    drawPath(
        path = paths.majorBoughsHighlightPath,
        color = Color(0xFFC47157).copy(alpha = 0.70f),
        style = Stroke(width = 1.2f, cap = StrokeCap.Round)
    )

    // 6. Secondary Mid-Boughs
    drawPath(
        path = paths.midBoughsBodyPath,
        color = Color(0xFF220C07),
        style = Stroke(width = 5.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.midBoughsBodyPath,
        color = Color(0xFF5A2A1E),
        style = Stroke(width = 2.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.midBoughsHighlightPath,
        color = Color(0xFF9E4E37),
        style = Stroke(width = 1.4f, cap = StrokeCap.Round)
    )

    // 7. Fine Twigs
    drawPath(
        path = paths.fineTwigsPath,
        color = Color(0xFF2B110A),
        style = Stroke(width = 2.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = paths.fineTwigsPath,
        color = Color(0xFF6B3324),
        style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

/**
 * Renders a plain dormant Bodhi leaf (soft silhouette without inner design in the middle).
 */
private fun DrawScope.renderDormantLeaf(cache: BodhiTreeRenderCache) {
    drawPath(
        path = cache.canonicalLeafPath,
        brush = cache.dormantLeafBrush,
        style = Fill
    )
    drawPath(
        path = cache.canonicalLeafPath,
        color = Color(0x754A1F13),
        style = Stroke(width = 1.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

/**
 * Renders an awakened Bodhi leaf with the classic warm copper design
 * and subtle sacred category color essence radiating softly inside.
 */
private fun DrawScope.renderAwakenedLeaf(
    merit: MeritEntity,
    cache: BodhiTreeRenderCache,
    isNewlySprouted: Boolean
) {
    val category = MeritCategory.fromString(merit.category)

    if (isNewlySprouted) {
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = 0.55f),
            radius = 38f,
            center = Offset.Zero
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = 28f,
            center = Offset.Zero
        )
    }

    // 1. Classic Sacred Copper Leaf Body (The original beloved design!)
    drawPath(
        path = cache.canonicalLeafPath,
        brush = cache.awakenedLeafBrush,
        style = Fill
    )

    // 2. Subtle Category Color Essence radiating softly inside the leaf
    drawCircle(
        color = category.leafColor.copy(alpha = 0.38f),
        radius = 9.5f,
        center = Offset(0f, -2f)
    )
    drawCircle(
        color = category.accentColor.copy(alpha = 0.22f),
        radius = 5.5f,
        center = Offset(0f, -2f)
    )

    // 3. Crisp Dark Copper Leaf Perimeter Outline
    drawPath(
        path = cache.canonicalLeafPath,
        color = Color(0xFF4A1F13),
        style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 4. INNER DESIGN: Intricate Dark Copper Skeleton Veins
    drawPath(
        path = cache.canonicalVeinsPath,
        color = Color(0xFF5A2518).copy(alpha = 0.88f),
        style = Stroke(width = 0.95f, cap = StrokeCap.Round)
    )

    // 5. INNER DESIGN: Central Sacred Dewdrop Jewel
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
