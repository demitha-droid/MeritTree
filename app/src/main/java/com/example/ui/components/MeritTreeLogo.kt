package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.i18n.LocalAppStrings

/**
 * Visual Merit Tree Logo Emblem.
 * Renders the sacred Merit Tree with luminous radiant aura.
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
        // Outer aura circle with subtle glowing gradient
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x40FFD54F),
                            Color(0x184CAF50),
                            Color.Transparent
                        )
                    )
                )
        )

        // Inner jewel base
        Surface(
            shape = CircleShape,
            color = Color(0xFF1B4D3E),
            border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
            shadowElevation = 2.dp,
            modifier = Modifier.size(size * 0.90f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.08f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_merit_tree_logo),
                    contentDescription = "Merit Tree Logo",
                    modifier = Modifier.size(size * 0.76f)
                )
            }
        }
    }
}

/**
 * Top App Bar Logo Component.
 * Dynamically displays "Merit Tree" in English or "පුණ්ය වෘක්ෂය" in Sinhala.
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
        MeritTreeEmblem(size = 38.dp, animatedGlow = false)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
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


