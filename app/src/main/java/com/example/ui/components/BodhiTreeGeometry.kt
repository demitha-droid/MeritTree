package com.example.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * Represents a sacred leaf slot position on the circular Bodhi tree matching the uploaded artwork.
 */
data class BodhiLeafSlot(
    val id: Int,
    val xRatio: Float,       // 0.0 .. 1.0 of canvas width
    val yRatio: Float,       // 0.0 .. 1.0 of canvas height
    val branchStartX: Float, // Connection point from parent branch
    val branchStartY: Float,
    val leafAngle: Float,    // Rotation angle in degrees
    val scaleFactor: Float = 1.0f
)

data class TreePaths(
    val trunkSilhouettePath: Path,
    val trunkFissuresPath: Path,
    val trunkHighlightsPath: Path,
    val gnarledRootsPath: Path,
    val rootHighlightsPath: Path,
    val majorBoughsShadowPath: Path,
    val majorBoughsBodyPath: Path,
    val majorBoughsHighlightPath: Path,
    val midBoughsBodyPath: Path,
    val midBoughsHighlightPath: Path,
    val fineTwigsPath: Path
)

object BodhiTreeGeometry {

    // Symmetrical leaf slots positioned exactly along the organic circular canopy
    // of the uploaded sacred Bodhi tree artwork.
    val LEAF_SLOTS: List<BodhiLeafSlot> by lazy {
        val list = mutableListOf<BodhiLeafSlot>()
        var id = 0

        fun addPair(
            lx: Float, ly: Float, bx: Float, by: Float, lAngle: Float,
            rx: Float = 1.0f - lx, ry: Float = ly,
            rbx: Float = 1.0f - bx, rby: Float = by,
            rAngle: Float = -lAngle,
            scale: Float = 1.0f
        ) {
            list.add(BodhiLeafSlot(id++, lx, ly, bx, by, lAngle, scale))
            list.add(BodhiLeafSlot(id++, rx, ry, rbx, rby, rAngle, scale))
        }

        fun addCenter(
            cx: Float, cy: Float, bx: Float, by: Float, angle: Float, scale: Float = 1.0f
        ) {
            list.add(BodhiLeafSlot(id++, cx, cy, bx, by, angle, scale))
        }

        // 1. Crown Pinnacle & Apex (Top center)
        addCenter(0.50f, 0.065f, 0.50f, 0.13f, 0f, 1.15f)
        addPair(0.44f, 0.085f, 0.465f, 0.145f, -16f, scale = 1.1f)
        addPair(0.38f, 0.115f, 0.42f, 0.175f, -28f, scale = 1.05f)

        // 2. High Outer Ring (Top-left & Top-right shoulder)
        addPair(0.32f, 0.15f, 0.36f, 0.21f, -42f)
        addPair(0.26f, 0.195f, 0.31f, 0.25f, -54f)
        addPair(0.20f, 0.255f, 0.26f, 0.31f, -68f)
        addPair(0.15f, 0.325f, 0.215f, 0.37f, -82f)

        // 3. Middle Equatorial Outer Rim (Outer circular boundary)
        addPair(0.11f, 0.405f, 0.175f, 0.435f, -95f, scale = 1.05f)
        addPair(0.095f, 0.495f, 0.17f, 0.505f, -108f, scale = 1.05f)
        addPair(0.115f, 0.585f, 0.195f, 0.575f, -124f)
        addPair(0.155f, 0.675f, 0.235f, 0.645f, -138f)

        // 4. Lower Arching Rim (Lower boundary)
        addPair(0.215f, 0.755f, 0.285f, 0.705f, -152f)
        addPair(0.285f, 0.815f, 0.335f, 0.745f, -168f, scale = 1.05f)
        addPair(0.365f, 0.835f, 0.395f, 0.755f, 176f)
        addPair(0.445f, 0.815f, 0.455f, 0.745f, 162f)

        // 5. Inner Crown (High inner canopy)
        addCenter(0.50f, 0.175f, 0.50f, 0.24f, 0f, 0.95f)
        addPair(0.435f, 0.215f, 0.46f, 0.275f, -18f, scale = 0.95f)
        addPair(0.37f, 0.245f, 0.405f, 0.315f, -36f, scale = 0.95f)
        addPair(0.305f, 0.295f, 0.345f, 0.365f, -52f, scale = 0.95f)

        // 6. Mid Interior Canopy
        addPair(0.235f, 0.385f, 0.285f, 0.425f, -72f, scale = 0.95f)
        addPair(0.225f, 0.485f, 0.285f, 0.495f, -92f, scale = 0.95f)
        addPair(0.265f, 0.575f, 0.325f, 0.565f, -116f, scale = 0.95f)
        addPair(0.345f, 0.635f, 0.385f, 0.615f, -136f, scale = 0.95f)

        // 7. Core Heart Nodes (Above trunk fork)
        addCenter(0.50f, 0.315f, 0.50f, 0.38f, 0f, 0.95f)
        addPair(0.43f, 0.355f, 0.455f, 0.415f, -22f, scale = 0.95f)
        addPair(0.36f, 0.415f, 0.405f, 0.465f, -48f, scale = 0.95f)
        addPair(0.42f, 0.495f, 0.455f, 0.545f, -32f, scale = 0.95f)

        // 8. Hanging Inner Lower Cluster
        addPair(0.365f, 0.715f, 0.405f, 0.675f, -162f, scale = 0.95f)
        addPair(0.425f, 0.675f, 0.455f, 0.645f, -172f, scale = 0.95f)

        // Symmetrical Canopy Dense Accents
        addPair(0.18f, 0.16f, 0.245f, 0.22f, -46f, scale = 1.0f)
        addPair(0.08f, 0.44f, 0.145f, 0.46f, -102f, scale = 1.0f)
        addPair(0.13f, 0.63f, 0.195f, 0.61f, -132f, scale = 1.0f)
        addPair(0.25f, 0.77f, 0.305f, 0.72f, -152f, scale = 1.0f)

        list
    }

    /**
     * Builds and returns all hand-carved Bodhi tree wood paths matching the uploaded image:
     * deep fluted root pedestal, twin rising trunks, organic arching boughs, chiseled bark
     * fissures, and satin wood highlights.
     */
    fun buildTreePaths(width: Float, height: Float): TreePaths {
        val centerX = width * 0.50f
        val groundY = height * 0.88f
        val trunkBaseY = height * 0.83f
        val lowerForkY = height * 0.64f
        val midForkY = height * 0.51f
        val upperForkY = height * 0.36f
        val crownForkY = height * 0.22f

        // 1. Trunk Silhouette Path (Fluted trunk body with flared base)
        val trunkSilhouette = Path().apply {
            // Left base flare to root collar
            moveTo(centerX - (width * 0.18f), groundY)
            cubicTo(
                centerX - (width * 0.10f), groundY - (height * 0.02f),
                centerX - (width * 0.055f), trunkBaseY,
                centerX - (width * 0.038f), lowerForkY
            )
            // Left mid-trunk to twin branch split
            cubicTo(
                centerX - (width * 0.032f), midForkY,
                centerX - (width * 0.024f), upperForkY,
                centerX - (width * 0.016f), crownForkY
            )
            // Apex center fork
            lineTo(centerX + (width * 0.016f), crownForkY)
            // Right mid-trunk back down
            cubicTo(
                centerX + (width * 0.024f), upperForkY,
                centerX + (width * 0.032f), midForkY,
                centerX + (width * 0.038f), lowerForkY
            )
            // Right base flare to root collar
            cubicTo(
                centerX + (width * 0.055f), trunkBaseY,
                centerX + (width * 0.10f), groundY - (height * 0.02f),
                centerX + (width * 0.18f), groundY
            )
            // Bottom root contact contour
            cubicTo(
                centerX + (width * 0.08f), groundY + (height * 0.006f),
                centerX - (width * 0.08f), groundY + (height * 0.006f),
                centerX - (width * 0.18f), groundY
            )
            close()
        }

        // 2. Trunk Fissures & Deep Bark Crevices (The sculpted carved wood relief lines)
        val trunkFissures = Path().apply {
            // Centerline division of twin trunks
            moveTo(centerX, crownForkY)
            cubicTo(
                centerX - (width * 0.003f), upperForkY,
                centerX + (width * 0.002f), midForkY,
                centerX, lowerForkY
            )
            cubicTo(
                centerX - (width * 0.002f), trunkBaseY,
                centerX, groundY - (height * 0.01f),
                centerX, groundY + (height * 0.012f)
            )

            // Left bark grooves flowing down into roots
            moveTo(centerX - (width * 0.012f), crownForkY + (height * 0.04f))
            cubicTo(
                centerX - (width * 0.018f), upperForkY,
                centerX - (width * 0.022f), midForkY,
                centerX - (width * 0.026f), lowerForkY
            )
            cubicTo(
                centerX - (width * 0.038f), trunkBaseY,
                centerX - (width * 0.075f), groundY - 2f,
                centerX - (width * 0.12f), groundY + (height * 0.012f)
            )

            // Right bark grooves flowing down into roots
            moveTo(centerX + (width * 0.012f), crownForkY + (height * 0.04f))
            cubicTo(
                centerX + (width * 0.018f), upperForkY,
                centerX + (width * 0.022f), midForkY,
                centerX + (width * 0.026f), lowerForkY
            )
            cubicTo(
                centerX + (width * 0.038f), trunkBaseY,
                centerX + (width * 0.075f), groundY - 2f,
                centerX + (width * 0.12f), groundY + (height * 0.012f)
            )

            // Outer left bark furrow
            moveTo(centerX - (width * 0.030f), lowerForkY + (height * 0.04f))
            cubicTo(
                centerX - (width * 0.045f), trunkBaseY,
                centerX - (width * 0.11f), groundY,
                centerX - (width * 0.17f), groundY + (height * 0.008f)
            )

            // Outer right bark furrow
            moveTo(centerX + (width * 0.030f), lowerForkY + (height * 0.04f))
            cubicTo(
                centerX + (width * 0.045f), trunkBaseY,
                centerX + (width * 0.11f), groundY,
                centerX + (width * 0.17f), groundY + (height * 0.008f)
            )
        }

        // 3. Trunk Satin Wood Highlights (Top specular ridges)
        val trunkHighlights = Path().apply {
            // Left twin trunk highlight ridge
            moveTo(centerX - (width * 0.007f), crownForkY + (height * 0.02f))
            cubicTo(
                centerX - (width * 0.010f), upperForkY,
                centerX - (width * 0.012f), midForkY,
                centerX - (width * 0.014f), lowerForkY
            )
            cubicTo(
                centerX - (width * 0.018f), trunkBaseY,
                centerX - (width * 0.035f), groundY - 4f,
                centerX - (width * 0.06f), groundY + 2f
            )

            // Right twin trunk highlight ridge
            moveTo(centerX + (width * 0.007f), crownForkY + (height * 0.02f))
            cubicTo(
                centerX + (width * 0.010f), upperForkY,
                centerX + (width * 0.012f), midForkY,
                centerX + (width * 0.014f), lowerForkY
            )
            cubicTo(
                centerX + (width * 0.018f), trunkBaseY,
                centerX + (width * 0.035f), groundY - 4f,
                centerX + (width * 0.06f), groundY + 2f
            )
        }

        // 4. Gnarled Splayed Root Pedestal
        val gnarledRoots = Path().apply {
            // Far Left outer root
            moveTo(centerX - (width * 0.06f), trunkBaseY + (height * 0.02f))
            cubicTo(
                centerX - (width * 0.12f), groundY - (height * 0.01f),
                centerX - (width * 0.18f), groundY,
                centerX - (width * 0.24f), groundY + (height * 0.012f)
            )

            // Mid Left root
            moveTo(centerX - (width * 0.035f), trunkBaseY + (height * 0.025f))
            cubicTo(
                centerX - (width * 0.08f), groundY - (height * 0.005f),
                centerX - (width * 0.12f), groundY + (height * 0.005f),
                centerX - (width * 0.15f), groundY + (height * 0.018f)
            )

            // Center Left rootlet
            moveTo(centerX - (width * 0.015f), groundY - (height * 0.015f))
            cubicTo(
                centerX - (width * 0.04f), groundY,
                centerX - (width * 0.06f), groundY + (height * 0.010f),
                centerX - (width * 0.07f), groundY + (height * 0.022f)
            )

            // Center Right rootlet
            moveTo(centerX + (width * 0.015f), groundY - (height * 0.015f))
            cubicTo(
                centerX + (width * 0.04f), groundY,
                centerX + (width * 0.06f), groundY + (height * 0.010f),
                centerX + (width * 0.07f), groundY + (height * 0.022f)
            )

            // Mid Right root
            moveTo(centerX + (width * 0.035f), trunkBaseY + (height * 0.025f))
            cubicTo(
                centerX + (width * 0.08f), groundY - (height * 0.005f),
                centerX + (width * 0.12f), groundY + (height * 0.005f),
                centerX + (width * 0.15f), groundY + (height * 0.018f)
            )

            // Far Right outer root
            moveTo(centerX + (width * 0.06f), trunkBaseY + (height * 0.02f))
            cubicTo(
                centerX + (width * 0.12f), groundY - (height * 0.01f),
                centerX + (width * 0.18f), groundY,
                centerX + (width * 0.24f), groundY + (height * 0.012f)
            )
        }

        // Root highlights
        val rootHighlights = Path().apply {
            moveTo(centerX - (width * 0.05f), trunkBaseY + (height * 0.025f))
            cubicTo(
                centerX - (width * 0.11f), groundY - (height * 0.008f),
                centerX - (width * 0.16f), groundY,
                centerX - (width * 0.21f), groundY + (height * 0.008f)
            )
            moveTo(centerX + (width * 0.05f), trunkBaseY + (height * 0.025f))
            cubicTo(
                centerX + (width * 0.11f), groundY - (height * 0.008f),
                centerX + (width * 0.16f), groundY,
                centerX + (width * 0.21f), groundY + (height * 0.008f)
            )
        }

        // 5. Major Heavy Arching Boughs (The 4 core radiating tiers of the artwork)
        val majorBoughsShadow = Path().apply {
            // Tier 1: Lower Hanging Cradle Boughs (Left & Right)
            moveTo(centerX - (width * 0.035f), lowerForkY)
            cubicTo(
                centerX - (width * 0.14f), lowerForkY + (height * 0.03f),
                width * 0.32f, height * 0.72f,
                width * 0.23f, height * 0.65f
            )
            moveTo(centerX + (width * 0.035f), lowerForkY)
            cubicTo(
                centerX + (width * 0.14f), lowerForkY + (height * 0.03f),
                width * 0.68f, height * 0.72f,
                width * 0.77f, height * 0.65f
            )

            // Tier 2: Mid Equatorial Boughs (Left & Right)
            moveTo(centerX - (width * 0.030f), midForkY)
            cubicTo(
                width * 0.36f, height * 0.50f,
                width * 0.24f, height * 0.53f,
                width * 0.16f, height * 0.44f
            )
            moveTo(centerX + (width * 0.030f), midForkY)
            cubicTo(
                width * 0.64f, height * 0.50f,
                width * 0.76f, height * 0.53f,
                width * 0.84f, height * 0.44f
            )

            // Tier 3: Upper Shoulder Boughs (Left & Right)
            moveTo(centerX - (width * 0.022f), upperForkY)
            cubicTo(
                width * 0.38f, height * 0.35f,
                width * 0.30f, height * 0.28f,
                width * 0.22f, height * 0.22f
            )
            moveTo(centerX + (width * 0.022f), upperForkY)
            cubicTo(
                width * 0.62f, height * 0.35f,
                width * 0.70f, height * 0.28f,
                width * 0.78f, height * 0.22f
            )

            // Tier 4: Crown Arches & Apex Split
            moveTo(centerX - (width * 0.015f), crownForkY)
            cubicTo(
                centerX - (width * 0.08f), height * 0.18f,
                width * 0.40f, height * 0.14f,
                width * 0.36f, height * 0.12f
            )
            moveTo(centerX + (width * 0.015f), crownForkY)
            cubicTo(
                centerX + (width * 0.08f), height * 0.18f,
                width * 0.60f, height * 0.14f,
                width * 0.64f, height * 0.12f
            )

            // Crown Pinnacle central stem
            moveTo(centerX, crownForkY)
            lineTo(centerX, height * 0.10f)
        }

        // Major Boughs Body Path (same curves with slightly tighter stroke)
        val majorBoughsBody = majorBoughsShadow

        // Major Boughs Highlights (warm top ridges)
        val majorBoughsHighlight = Path().apply {
            // Lower cradle boughs top highlights
            moveTo(centerX - (width * 0.035f), lowerForkY - 2f)
            cubicTo(
                centerX - (width * 0.14f), lowerForkY + (height * 0.025f),
                width * 0.32f, height * 0.71f,
                width * 0.23f, height * 0.645f
            )
            moveTo(centerX + (width * 0.035f), lowerForkY - 2f)
            cubicTo(
                centerX + (width * 0.14f), lowerForkY + (height * 0.025f),
                width * 0.68f, height * 0.71f,
                width * 0.77f, height * 0.645f
            )

            // Mid equatorial boughs top highlights
            moveTo(centerX - (width * 0.030f), midForkY - 2f)
            cubicTo(
                width * 0.36f, height * 0.495f,
                width * 0.24f, height * 0.525f,
                width * 0.16f, height * 0.435f
            )
            moveTo(centerX + (width * 0.030f), midForkY - 2f)
            cubicTo(
                width * 0.64f, height * 0.495f,
                width * 0.76f, height * 0.525f,
                width * 0.84f, height * 0.435f
            )

            // Upper shoulder boughs top highlights
            moveTo(centerX - (width * 0.022f), upperForkY - 2f)
            cubicTo(
                width * 0.38f, height * 0.345f,
                width * 0.30f, height * 0.275f,
                width * 0.22f, height * 0.215f
            )
            moveTo(centerX + (width * 0.022f), upperForkY - 2f)
            cubicTo(
                width * 0.62f, height * 0.345f,
                width * 0.70f, height * 0.275f,
                width * 0.78f, height * 0.215f
            )
        }

        // 6. Secondary Mid-Boughs (Branch forks branching off major limbs)
        val midBoughsBody = Path().apply {
            // Lower cradle secondary forks curling downward & upward
            moveTo(width * 0.32f, height * 0.71f)
            cubicTo(width * 0.27f, height * 0.74f, width * 0.22f, height * 0.76f, width * 0.18f, height * 0.73f)

            moveTo(width * 0.68f, height * 0.71f)
            cubicTo(width * 0.73f, height * 0.74f, width * 0.78f, height * 0.76f, width * 0.82f, height * 0.73f)

            moveTo(width * 0.26f, height * 0.67f)
            cubicTo(width * 0.23f, height * 0.60f, width * 0.20f, height * 0.56f, width * 0.16f, height * 0.52f)

            moveTo(width * 0.74f, height * 0.67f)
            cubicTo(width * 0.77f, height * 0.60f, width * 0.80f, height * 0.56f, width * 0.84f, height * 0.52f)

            // Mid equatorial secondary forks
            moveTo(width * 0.34f, height * 0.50f)
            cubicTo(width * 0.30f, height * 0.44f, width * 0.26f, height * 0.40f, width * 0.20f, height * 0.36f)

            moveTo(width * 0.66f, height * 0.50f)
            cubicTo(width * 0.70f, height * 0.44f, width * 0.74f, height * 0.40f, width * 0.80f, height * 0.36f)

            // Upper shoulder secondary forks
            moveTo(width * 0.36f, height * 0.34f)
            cubicTo(width * 0.32f, height * 0.28f, width * 0.28f, height * 0.24f, width * 0.23f, height * 0.18f)

            moveTo(width * 0.64f, height * 0.34f)
            cubicTo(width * 0.68f, height * 0.28f, width * 0.72f, height * 0.24f, width * 0.77f, height * 0.18f)

            // Crown secondary arches
            moveTo(width * 0.44f, height * 0.16f)
            cubicTo(width * 0.42f, height * 0.13f, width * 0.40f, height * 0.10f, width * 0.38f, height * 0.08f)

            moveTo(width * 0.56f, height * 0.16f)
            cubicTo(width * 0.58f, height * 0.13f, width * 0.60f, height * 0.10f, width * 0.62f, height * 0.08f)
        }

        val midBoughsHighlight = Path().apply {
            moveTo(width * 0.32f, height * 0.708f)
            cubicTo(width * 0.27f, height * 0.738f, width * 0.22f, height * 0.758f, width * 0.18f, height * 0.728f)
            moveTo(width * 0.68f, height * 0.708f)
            cubicTo(width * 0.73f, height * 0.738f, width * 0.78f, height * 0.758f, width * 0.82f, height * 0.728f)
        }

        // 7. Fine Petioles & Twigs leading smoothly into every Bodhi leaf slot
        val fineTwigs = Path().apply {
            LEAF_SLOTS.forEach { slot ->
                val startX = slot.branchStartX * width
                val startY = slot.branchStartY * height
                val endX = slot.xRatio * width
                val endY = slot.yRatio * height

                moveTo(startX, startY)
                val midX = (startX + endX) * 0.5f + ((endY - startY) * 0.14f)
                val midY = (startY + endY) * 0.5f - ((endX - startX) * 0.14f)
                quadraticTo(midX, midY, endX, endY)
            }
        }

        return TreePaths(
            trunkSilhouettePath = trunkSilhouette,
            trunkFissuresPath = trunkFissures,
            trunkHighlightsPath = trunkHighlights,
            gnarledRootsPath = gnarledRoots,
            rootHighlightsPath = rootHighlights,
            majorBoughsShadowPath = majorBoughsShadow,
            majorBoughsBodyPath = majorBoughsBody,
            majorBoughsHighlightPath = majorBoughsHighlight,
            midBoughsBodyPath = midBoughsBody,
            midBoughsHighlightPath = midBoughsHighlight,
            fineTwigsPath = fineTwigs
        )
    }

    /**
     * Constructs a single canonical Bodhi leaf path matching the reference image.
     */
    fun createBodhiLeafShape(scale: Float): Path {
        return Path().apply {
            moveTo(0f, 14f * scale)
            cubicTo(
                -18f * scale, 9f * scale,
                -16f * scale, -6f * scale,
                -9f * scale, -13f * scale
            )
            cubicTo(
                -4f * scale, -18f * scale,
                -1f * scale, -24f * scale,
                0f * scale, -28f * scale
            )
            cubicTo(
                1f * scale, -24f * scale,
                4f * scale, -18f * scale,
                9f * scale, -13f * scale
            )
            cubicTo(
                16f * scale, -6f * scale,
                18f * scale, 9f * scale,
                0f, 14f * scale
            )
            close()
        }
    }

    /**
     * Constructs the skeleton vein lines inside the leaf matching the uploaded reference image.
     */
    fun createBodhiVeinsPath(scale: Float): Path {
        return Path().apply {
            moveTo(0f, 12f * scale)
            lineTo(0f, -25f * scale)

            val yPositions = listOf(7f, 2f, -3f, -8f, -13f)
            yPositions.forEachIndexed { i, y ->
                val span = (8f - i * 1.3f) * scale
                moveTo(0f, y * scale)
                lineTo(-span, (y - 3f) * scale)
                moveTo(0f, y * scale)
                lineTo(span, (y - 3f) * scale)
            }
        }
    }
}
