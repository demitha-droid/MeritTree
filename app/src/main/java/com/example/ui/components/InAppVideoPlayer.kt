package com.example.ui.components

import android.net.Uri
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale

/**
 * Universal In-App Video Player.
 * Plays videos completely within the application (no external app or picker needed).
 * Features custom Material 3 controls, timeline scrub bar, play/pause, replay,
 * and seamless in-app fullscreen mode.
 */
@Composable
fun InAppVideoPlayer(
    videoUriOrPath: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    allowFullScreenToggle: Boolean = true
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }
    var isControlsVisible by remember { mutableStateOf(true) }
    var isUserDraggingSlider by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableIntStateOf(0) }
    var isFullScreen by remember { mutableStateOf(false) }

    var videoViewInstance by remember { mutableStateOf<VideoView?>(null) }

    // Auto-hide controls after 3 seconds of playing
    LaunchedEffect(isPlaying, isControlsVisible, isUserDraggingSlider) {
        if (isPlaying && isControlsVisible && !isUserDraggingSlider) {
            delay(3200)
            isControlsVisible = false
        }
    }

    // Periodic progress ticker
    LaunchedEffect(isPlaying, isPrepared) {
        while (isPlaying && isPrepared) {
            videoViewInstance?.let { vv ->
                try {
                    if (vv.isPlaying) {
                        currentPosition = vv.currentPosition
                        duration = vv.duration.coerceAtLeast(0)
                    }
                } catch (_: Exception) {}
            }
            delay(250)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                videoViewInstance?.stopPlayback()
            } catch (_: Exception) {}
        }
    }

    fun togglePlayPause() {
        val vv = videoViewInstance ?: return
        if (isCompleted) {
            vv.seekTo(0)
            vv.start()
            isPlaying = true
            isCompleted = false
        } else if (vv.isPlaying) {
            vv.pause()
            isPlaying = false
        } else {
            vv.start()
            isPlaying = true
        }
        isControlsVisible = true
    }

    fun formatTime(ms: Int): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isControlsVisible = !isControlsVisible
            }
            .testTag("in_app_video_player")
    ) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    if (videoUriOrPath.startsWith("/")) {
                        setVideoPath(videoUriOrPath)
                    } else {
                        setVideoURI(Uri.parse(videoUriOrPath))
                    }

                    setOnPreparedListener { mp ->
                        isPrepared = true
                        duration = mp.duration.coerceAtLeast(0)
                        if (autoPlay) {
                            mp.start()
                            isPlaying = true
                        }
                    }

                    setOnCompletionListener {
                        isPlaying = false
                        isCompleted = true
                        isControlsVisible = true
                        currentPosition = duration
                    }

                    setOnErrorListener { _, _, _ ->
                        true
                    }

                    videoViewInstance = this
                }
            },
            update = { vv ->
                videoViewInstance = vv
            },
            modifier = Modifier.fillMaxSize()
        )

        // Loading spinner while preparing
        if (!isPrepared) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = isControlsVisible || !isPlaying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // Center Large Play/Pause/Replay Button
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .clickable { togglePlayPause() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                isCompleted -> Icons.Default.Replay
                                isPlaying -> Icons.Default.Pause
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Bottom Timeline and Controls Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Scrub slider
                    val activePos = if (isUserDraggingSlider) dragPosition else currentPosition
                    val safeDuration = duration.coerceAtLeast(1)

                    Slider(
                        value = activePos.toFloat().coerceIn(0f, safeDuration.toFloat()),
                        onValueChange = {
                            isUserDraggingSlider = true
                            dragPosition = it.toInt()
                        },
                        onValueChangeFinished = {
                            videoViewInstance?.seekTo(dragPosition)
                            currentPosition = dragPosition
                            isUserDraggingSlider = false
                            if (isCompleted) isCompleted = false
                        },
                        valueRange = 0f..safeDuration.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { togglePlayPause() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${formatTime(activePos)} / ${formatTime(duration)}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                            )
                        }

                        if (allowFullScreenToggle) {
                            IconButton(
                                onClick = { isFullScreen = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // In-App Full-Screen Player Dialog
    if (isFullScreen) {
        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
        ) {
            BackHandler { isFullScreen = false }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                InAppVideoPlayer(
                    videoUriOrPath = videoUriOrPath,
                    modifier = Modifier.fillMaxSize(),
                    autoPlay = true,
                    allowFullScreenToggle = false
                )

                // Top Exit Fullscreen Bar
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(16.dp)
                        .clickable { isFullScreen = false }
                ) {
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Exit Fullscreen",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}
