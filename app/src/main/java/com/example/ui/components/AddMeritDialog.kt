package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.MeritCategory
import com.example.ui.i18n.LocalAppStrings

/**
 * Full-Screen "Add New Merit Post" Page.
 * - Zero keyboard lag (no layout thrashing, adjustResize window, and stable state reads).
 * - Keyboard back-press safety: Pressing back while the keyboard is open only dismisses the keyboard,
 *   preserving all entered text and picked photos without dismissing the post page!
 * - Two dedicated media actions: Upload images/videos from gallery, or capture images/videos with camera.
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AddMeritDialog(
    onDismiss: () -> Unit,
    onUploadMedia: () -> Unit,
    onCapturePhoto: () -> Unit,
    onCaptureVideo: () -> Unit,
    pickedMediaUri: String? = null,
    pickedMediaUris: List<String> = emptyList(),
    initialCategory: MeritCategory = MeritCategory.DANA,
    onAddMerit: (title: String, category: MeritCategory, description: String, dedication: String, imageUri: String?) -> Unit
) {
    val strings = LocalAppStrings.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
    var description by remember { mutableStateOf("") }
    var dedication by remember {
        mutableStateOf(
            if (strings.isSinhala) "සියලු සත්වයෝ සුවපත් වෙත්වා, නිදුක් වෙත්වා, නිරෝගී වෙත්වා."
            else "May all beings be well, happy, and peaceful."
        )
    }
    var mediaList by remember { mutableStateOf<List<String>>(emptyList()) }
    var titleError by remember { mutableStateOf(false) }
    var showCameraChooser by remember { mutableStateOf(false) }

    // Focus tracking for instant responsive keyboard handling
    var isTitleFocused by remember { mutableStateOf(false) }
    var isDescFocused by remember { mutableStateOf(false) }
    var isDedicationFocused by remember { mutableStateOf(false) }
    val isAnyFieldFocused = isTitleFocused || isDescFocused || isDedicationFocused
    val isImeVisible = WindowInsets.isImeVisible

    // BackHandler: When the keyboard is active or a text field is focused,
    // intercept the back press to dismiss ONLY the keyboard and retain the entire post!
    BackHandler {
        if (isImeVisible || isAnyFieldFocused) {
            focusManager.clearFocus()
            keyboardController?.hide()
            isTitleFocused = false
            isDescFocused = false
            isDedicationFocused = false
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(pickedMediaUri, pickedMediaUris) {
        val newItems = (pickedMediaUris + listOfNotNull(pickedMediaUri)).filter { it.isNotBlank() }
        if (newItems.isNotEmpty()) {
            mediaList = (mediaList + newItems).distinct()
        }
    }

    fun submitPost() {
        if (title.isBlank()) {
            titleError = true
            return
        }
        onAddMerit(
            title,
            selectedCategory,
            description,
            dedication,
            if (mediaList.isEmpty()) null else mediaList.joinToString("|")
        )
        onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.addMeritTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isImeVisible || isAnyFieldFocused) {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_add_merit_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = strings.cancel)
                    }
                },
                actions = {
                    Button(
                        onClick = { submitPost() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(38.dp)
                            .testTag("top_sprout_leaf_button")
                    ) {
                        Text(
                            text = strings.sproutAction,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
            .testTag("add_merit_sheet")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = strings.addMeritSubtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Title Field (Instant focus, zero lag!)
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("${strings.deedTitleLabel} *") },
                placeholder = { Text(strings.deedTitlePlaceholder) },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text(if (strings.isSinhala) "කරුණාකර පින්කමේ නම ඇතුළත් කරන්න" else "Please enter a title for your merit") }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isTitleFocused = it.isFocused }
                    .testTag("merit_title_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Media Section Header
            Text(
                text = if (strings.isSinhala) "ඡායාරූප හෝ වීඩියෝ" else "Photos & Videos",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = strings.mediaSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Two Dedicated Action Buttons: Upload Media & Camera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Upload Images / Videos
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onUploadMedia()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("upload_media_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.uploadMedia,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }

                // Button 2: Camera (Photo / Video)
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        showCameraChooser = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("camera_media_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.cameraMedia,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }
            }

            // Only display media preview if media has actually been uploaded or captured
            if (mediaList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${mediaList.size} ${if (mediaList.size == 1) "item attached" else "items attached"}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    TextButton(onClick = { mediaList = emptyList() }) {
                        Text(strings.removeMedia, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(mediaList) { index, uri ->
                        val isVideo = remember(uri) {
                            val lower = uri.lowercase()
                            lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                                    lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .width(160.dp)
                                .height(180.dp)
                                .testTag("add_merit_media_item_$index")
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isVideo) {
                                    VideoThumbnailView(
                                        videoUriOrPath = uri,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    RenderMeritImage(
                                        imageUri = uri,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Delete (X) button for this item
                                Surface(
                                    color = Color.Black.copy(alpha = 0.70f),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(28.dp)
                                        .clickable {
                                            mediaList = mediaList.filterIndexed { i, _ -> i != index }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Index badge (#1, #2, ...)
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // "+ Add More" card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .width(110.dp)
                                .height(180.dp)
                                .clickable { onUploadMedia() }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add More",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (strings.isSinhala) "තව එකතු කරන්න" else "Add More",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Story / Reflection / Note
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(strings.storyLabel) },
                placeholder = { Text(strings.storyPlaceholder) },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isDescFocused = it.isFocused }
                    .testTag("merit_description_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dedication of Merit (Pattidāna)
            OutlinedTextField(
                value = dedication,
                onValueChange = { dedication = it },
                label = { Text(strings.dedicationLabel) },
                placeholder = { Text(strings.dedicationPlaceholder) },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isDedicationFocused = it.isFocused }
                    .testTag("merit_dedication_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = { submitPost() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sprout_leaf_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${strings.sproutAction} 🌿",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

    // Camera Mode Chooser (Photo vs Video)
    if (showCameraChooser) {
        AlertDialog(
            onDismissRequest = { showCameraChooser = false },
            title = {
                Text(
                    text = strings.chooseCameraMode,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            showCameraChooser = false
                            onCapturePhoto()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strings.takePhoto, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            showCameraChooser = false
                            onCaptureVideo()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strings.recordVideo, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCameraChooser = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
