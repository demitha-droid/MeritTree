package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import kotlin.math.hypot

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

@Composable
fun BodhiTreeCanvas(
    merits: List<MeritEntity>,
    newlySproutedId: Long?,
    onLeafClick: (MeritEntity) -> Unit,
    onEmptyLeafClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

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

        // 2. Pre-render the entire static tree wood & all dormant leaves into a hardware bitmap
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
                .pointerInput(meritsBySlot, width, height) {
                    detectTapGestures { tapOffset ->
                        val slots = BodhiTreeGeometry.LEAF_SLOTS
                        val hit = slots.firstOrNull { slot ->
                            val cx = slot.xRatio * width
                            val cy = slot.yRatio * height
                            val hitRadius = 32f * (width / 390f) * 1.05f * slot.scaleFactor * 1.45f
                            hypot(tapOffset.x - cx, tapOffset.y - cy) <= hitRadius
                        }
                        if (hit != null) {
                            val merit = meritsBySlot[hit.id]
                            if (merit != null) {
                                onLeafClick(merit)
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

            // Only render active/awakened leaves on top
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
 * Renders an awakened Bodhi leaf with the full exquisite design inside:
 * glowing category essence, intricate skeleton veins, and central sacred dewdrop jewel.
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

    // 1. Rich Luminous Copper Body
    drawPath(
        path = cache.canonicalLeafPath,
        brush = cache.awakenedLeafBrush,
        style = Fill
    )

    // 2. Category Essence Glow appearing inside the leaf
    drawCircle(
        color = category.leafColor.copy(alpha = 0.35f),
        radius = 9f,
        center = Offset.Zero
    )

    // 3. Crisp Dark Copper Leaf Perimeter Outline
    drawPath(
        path = cache.canonicalLeafPath,
        color = Color(0xFF4A1F13),
        style = Stroke(width = 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 4. INNER DESIGN: Intricate Skeleton Veins
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
