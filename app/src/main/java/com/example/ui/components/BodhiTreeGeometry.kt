package com.example.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * Represents a sacred leaf slot position on the circular Bodhi tree matching the uploaded artwork.
 * Leaves ONLY appear at these slots when a post/merit is recorded.
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

object BodhiTreeGeometry {

    // Pre-defined slots arranged in the circular mandala canopy matching the reference image.
    // Slots are ordered by priority so leaves sprout in an organic, balanced spread across all branches.
    val LEAF_SLOTS: List<BodhiLeafSlot> by lazy {
        val list = mutableListOf<BodhiLeafSlot>()
        var id = 0

        // Helper to add symmetrical pairs
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

        // 1. Crown Apex (Top center)
        addCenter(0.50f, 0.06f, 0.50f, 0.12f, 0f, 1.15f)
        addPair(0.44f, 0.08f, 0.46f, 0.14f, -18f, scale = 1.1f)
        addPair(0.38f, 0.11f, 0.42f, 0.17f, -30f, scale = 1.05f)

        // 2. Upper Shoulder Canopy (High ring)
        addPair(0.32f, 0.14f, 0.36f, 0.20f, -40f)
        addPair(0.26f, 0.18f, 0.31f, 0.24f, -50f)
        addPair(0.20f, 0.24f, 0.26f, 0.30f, -65f)
        addPair(0.15f, 0.31f, 0.22f, 0.36f, -80f)

        // 3. Middle Equatorial Outer Rim (Outer circular boundary)
        addPair(0.11f, 0.39f, 0.18f, 0.42f, -95f, scale = 1.05f)
        addPair(0.10f, 0.48f, 0.18f, 0.49f, -110f, scale = 1.05f)
        addPair(0.12f, 0.57f, 0.20f, 0.56f, -125f)
        addPair(0.16f, 0.66f, 0.24f, 0.63f, -140f)

        // 4. Lower Arching Rim (Lower boundary)
        addPair(0.22f, 0.74f, 0.29f, 0.69f, -155f)
        addPair(0.29f, 0.80f, 0.34f, 0.73f, -170f, scale = 1.05f)
        addPair(0.37f, 0.82f, 0.40f, 0.74f, 175f)
        addPair(0.44f, 0.80f, 0.45f, 0.73f, 160f)

        // 5. Inner Crown & Mid-Canopy Nodes (Dense interior heart)
        addCenter(0.50f, 0.18f, 0.50f, 0.25f, 0f, 0.95f)
        addPair(0.44f, 0.22f, 0.46f, 0.28f, -15f, scale = 0.95f)
        addPair(0.37f, 0.25f, 0.40f, 0.32f, -35f, scale = 0.95f)
        addPair(0.30f, 0.30f, 0.34f, 0.37f, -50f, scale = 0.95f)

        // 6. Mid Interior Tier
        addPair(0.23f, 0.39f, 0.28f, 0.43f, -70f, scale = 0.95f)
        addPair(0.22f, 0.49f, 0.29f, 0.50f, -90f, scale = 0.95f)
        addPair(0.26f, 0.58f, 0.33f, 0.57f, -115f, scale = 0.95f)
        addPair(0.34f, 0.64f, 0.39f, 0.62f, -135f, scale = 0.95f)

        // 7. Core Heart Nodes (Above the fork)
        addCenter(0.50f, 0.32f, 0.50f, 0.39f, 0f, 0.95f)
        addPair(0.43f, 0.36f, 0.45f, 0.42f, -20f, scale = 0.95f)
        addPair(0.36f, 0.42f, 0.40f, 0.47f, -45f, scale = 0.95f)
        addPair(0.42f, 0.50f, 0.45f, 0.55f, -30f, scale = 0.95f)

        // 8. Lowest hanging inner cluster
        addPair(0.36f, 0.72f, 0.40f, 0.68f, -160f, scale = 0.95f)
        addPair(0.42f, 0.68f, 0.45f, 0.65f, -170f, scale = 0.95f)

        // Extra outer symmetry fillers for lush full-canopy support
        addPair(0.18f, 0.15f, 0.24f, 0.21f, -45f, scale = 1.0f)
        addPair(0.08f, 0.43f, 0.15f, 0.45f, -100f, scale = 1.0f)
        addPair(0.13f, 0.62f, 0.20f, 0.60f, -130f, scale = 1.0f)
        addPair(0.25f, 0.76f, 0.31f, 0.71f, -150f, scale = 1.0f)

        list
    }

    /**
     * Builds and returns the Bodhi tree wood branches matching the reference image.
     */
    fun buildTreePaths(width: Float, height: Float): TreePaths {
        val trunkBaseX = width * 0.50f
        val trunkBaseY = height * 0.88f
        val mainForkY = height * 0.56f

        // 1. Trunk Path with Natural Flaring Base
        val trunkPath = Path().apply {
            // Left ground root flare
            moveTo(trunkBaseX - (width * 0.20f), trunkBaseY)
            cubicTo(
                trunkBaseX - (width * 0.12f), trunkBaseY - (height * 0.015f),
                trunkBaseX - (width * 0.07f), trunkBaseY - (height * 0.045f),
                trunkBaseX - (width * 0.045f), height * 0.78f
            )
            // Taper up to fork
            cubicTo(
                trunkBaseX - (width * 0.040f), height * 0.68f,
                trunkBaseX - (width * 0.035f), height * 0.60f,
                trunkBaseX - (width * 0.025f), mainForkY
            )
            // Fork center top
            lineTo(trunkBaseX + (width * 0.025f), mainForkY)
            // Right taper down
            cubicTo(
                trunkBaseX + (width * 0.035f), height * 0.60f,
                trunkBaseX + (width * 0.040f), height * 0.68f,
                trunkBaseX + (width * 0.045f), height * 0.78f
            )
            // Right ground root flare
            cubicTo(
                trunkBaseX + (width * 0.07f), trunkBaseY - (height * 0.045f),
                trunkBaseX + (width * 0.12f), trunkBaseY - (height * 0.015f),
                trunkBaseX + (width * 0.20f), trunkBaseY
            )
            // Sculpted base bottom edge matching natural gnarly root collar
            cubicTo(
                trunkBaseX + (width * 0.12f), trunkBaseY - (height * 0.005f),
                trunkBaseX + (width * 0.05f), trunkBaseY + (height * 0.005f),
                trunkBaseX, trunkBaseY + (height * 0.003f)
            )
            cubicTo(
                trunkBaseX - (width * 0.05f), trunkBaseY + (height * 0.005f),
                trunkBaseX - (width * 0.12f), trunkBaseY - (height * 0.005f),
                trunkBaseX - (width * 0.20f), trunkBaseY
            )
            close()
        }

        // 2. Individual Root Tendrils (Matching the reference artwork's roots)
        val rootTendrilsPath = Path().apply {
            // Far Left root tendril
            moveTo(trunkBaseX - (width * 0.08f), height * 0.83f)
            cubicTo(
                trunkBaseX - (width * 0.14f), height * 0.85f,
                trunkBaseX - (width * 0.19f), height * 0.87f,
                trunkBaseX - (width * 0.24f), trunkBaseY + (height * 0.008f)
            )

            // Mid Left root tendril
            moveTo(trunkBaseX - (width * 0.05f), height * 0.82f)
            cubicTo(
                trunkBaseX - (width * 0.09f), height * 0.84f,
                trunkBaseX - (width * 0.13f), height * 0.87f,
                trunkBaseX - (width * 0.16f), trunkBaseY + (height * 0.014f)
            )

            // Inner Left root tendril
            moveTo(trunkBaseX - (width * 0.02f), height * 0.83f)
            cubicTo(
                trunkBaseX - (width * 0.04f), height * 0.85f,
                trunkBaseX - (width * 0.06f), height * 0.88f,
                trunkBaseX - (width * 0.08f), trunkBaseY + (height * 0.018f)
            )

            // Center taproot tendril
            moveTo(trunkBaseX, height * 0.83f)
            cubicTo(
                trunkBaseX - (width * 0.01f), height * 0.86f,
                trunkBaseX + (width * 0.01f), height * 0.88f,
                trunkBaseX, trunkBaseY + (height * 0.020f)
            )

            // Inner Right root tendril
            moveTo(trunkBaseX + (width * 0.02f), height * 0.83f)
            cubicTo(
                trunkBaseX + (width * 0.04f), height * 0.85f,
                trunkBaseX + (width * 0.06f), height * 0.88f,
                trunkBaseX + (width * 0.08f), trunkBaseY + (height * 0.018f)
            )

            // Mid Right root tendril
            moveTo(trunkBaseX + (width * 0.05f), height * 0.82f)
            cubicTo(
                trunkBaseX + (width * 0.09f), height * 0.84f,
                trunkBaseX + (width * 0.13f), height * 0.87f,
                trunkBaseX + (width * 0.16f), trunkBaseY + (height * 0.014f)
            )

            // Far Right root tendril
            moveTo(trunkBaseX + (width * 0.08f), height * 0.83f)
            cubicTo(
                trunkBaseX + (width * 0.14f), height * 0.85f,
                trunkBaseX + (width * 0.19f), height * 0.87f,
                trunkBaseX + (width * 0.24f), trunkBaseY + (height * 0.008f)
            )
        }

        // 3. Main Heavy Boughs Path
        val heavyBoughsPath = Path().apply {
            // Central rising vertical stem
            moveTo(trunkBaseX, mainForkY)
            cubicTo(
                trunkBaseX, height * 0.44f,
                trunkBaseX, height * 0.28f,
                trunkBaseX, height * 0.12f
            )

            // Left Lower Major Arch
            moveTo(trunkBaseX - (width * 0.02f), mainForkY + (height * 0.02f))
            cubicTo(
                trunkBaseX - (width * 0.12f), height * 0.64f,
                width * 0.32f, height * 0.70f,
                width * 0.24f, height * 0.64f
            )

            // Right Lower Major Arch
            moveTo(trunkBaseX + (width * 0.02f), mainForkY + (height * 0.02f))
            cubicTo(
                trunkBaseX + (width * 0.12f), height * 0.64f,
                width * 0.68f, height * 0.70f,
                width * 0.76f, height * 0.64f
            )

            // Left Mid Equatorial Arch
            moveTo(trunkBaseX - (width * 0.02f), mainForkY - (height * 0.03f))
            cubicTo(
                width * 0.38f, height * 0.50f,
                width * 0.24f, height * 0.52f,
                width * 0.18f, height * 0.44f
            )

            // Right Mid Equatorial Arch
            moveTo(trunkBaseX + (width * 0.02f), mainForkY - (height * 0.03f))
            cubicTo(
                width * 0.62f, height * 0.50f,
                width * 0.76f, height * 0.52f,
                width * 0.82f, height * 0.44f
            )

            // Left Upper Shoulder Arch
            moveTo(trunkBaseX - (width * 0.015f), height * 0.42f)
            cubicTo(
                width * 0.42f, height * 0.34f,
                width * 0.32f, height * 0.26f,
                width * 0.24f, height * 0.22f
            )

            // Right Upper Shoulder Arch
            moveTo(trunkBaseX + (width * 0.015f), height * 0.42f)
            cubicTo(
                width * 0.58f, height * 0.34f,
                width * 0.68f, height * 0.26f,
                width * 0.76f, height * 0.22f
            )
        }

        // 4. Sub-branches and twigs leading to the leaf nodes
        val twigsPath = Path().apply {
            LEAF_SLOTS.forEach { slot ->
                val startX = slot.branchStartX * width
                val startY = slot.branchStartY * height
                val endX = slot.xRatio * width
                val endY = slot.yRatio * height

                moveTo(startX, startY)
                val midX = (startX + endX) * 0.5f + ((endY - startY) * 0.12f)
                val midY = (startY + endY) * 0.5f - ((endX - startX) * 0.12f)
                quadraticTo(midX, midY, endX, endY)
            }
        }

        return TreePaths(trunkPath, heavyBoughsPath, twigsPath, rootTendrilsPath)
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

data class TreePaths(
    val trunkPath: Path,
    val heavyBoughsPath: Path,
    val twigsPath: Path,
    val rootTendrilsPath: Path
)
