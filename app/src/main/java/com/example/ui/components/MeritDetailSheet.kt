package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.sound.MindfulSoundHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Full-Screen Post Screen with options to Edit the post.
 * Displays the complete merit post with prominent media, full reflection, and dedicated edit capabilities.
 * Includes direct media upload and camera (photo / video) support.
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MeritDetailSheet(
    merit: MeritEntity,
    onDismiss: () -> Unit,
    onDelete: (MeritEntity) -> Unit,
    onUpdate: (merit: MeritEntity, newTitle: String, newCategory: MeritCategory, newDescription: String, newDedication: String, newImageUri: String?) -> Unit,
    onUploadMedia: () -> Unit,
    onCapturePhoto: () -> Unit,
    onCaptureVideo: () -> Unit,
    pickedMediaUri: String? = null
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var rejoiced by remember { mutableStateOf(false) }
    var isFullScreenImage by remember { mutableStateOf(false) }
    var showCameraChooser by remember { mutableStateOf(false) }

    // Edit state fields
    var editTitle by remember(merit) { mutableStateOf(merit.title) }
    var editCategory by remember(merit) { mutableStateOf(MeritCategory.fromString(merit.category)) }
    var editDescription by remember(merit) { mutableStateOf(merit.description) }
    var editDedication by remember(merit) { mutableStateOf(merit.dedication) }
    var editImageUri by remember(merit) { mutableStateOf(merit.imageUri) }
    var titleError by remember { mutableStateOf(false) }

    // Keyboard & focus safety
    var isTitleFocused by remember { mutableStateOf(false) }
    var isDescFocused by remember { mutableStateOf(false) }
    var isDedicationFocused by remember { mutableStateOf(false) }
    val isAnyFieldFocused = isTitleFocused || isDescFocused || isDedicationFocused
    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(pickedMediaUri) {
        if (!pickedMediaUri.isNullOrBlank() && isEditing) {
            editImageUri = pickedMediaUri
        }
    }

    val currentCategory = if (isEditing) editCategory else MeritCategory.fromString(merit.category)
    val activeMediaUri = if (isEditing) editImageUri else merit.imageUri
    val isVideo = remember(activeMediaUri) {
        if (activeMediaUri.isNullOrBlank()) false
        else {
            val lower = activeMediaUri.lowercase()
            lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                    lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
        }
    }

    val formattedDate = remember(merit.timestamp) {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(merit.timestamp))
    }

    fun playOrOpenMedia() {
        if (isVideo && activeMediaUri != null) {
            try {
                val uri = if (activeMediaUri.startsWith("/")) {
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(activeMediaUri))
                } else {
                    Uri.parse(activeMediaUri)
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "video/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, strings.playVideo))
            } catch (_: Exception) {
                isFullScreenImage = true
            }
        } else {
            isFullScreenImage = true
        }
    }

    fun saveEdits() {
        if (editTitle.isBlank()) {
            titleError = true
            return
        }
        onUpdate(
            merit,
            editTitle,
            editCategory,
            editDescription,
            editDedication,
            editImageUri
        )
        isEditing = false
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    // BackHandler: If keyboard is up, dismiss keyboard only. If editing, exit edit mode. Otherwise dismiss post.
    BackHandler {
        if (isImeVisible || isAnyFieldFocused) {
            focusManager.clearFocus()
            keyboardController?.hide()
            isTitleFocused = false
            isDescFocused = false
            isDedicationFocused = false
        } else if (isEditing) {
            editTitle = merit.title
            editCategory = MeritCategory.fromString(merit.category)
            editDescription = merit.description
            editDedication = merit.dedication
            editImageUri = merit.imageUri
            isEditing = false
        } else {
            onDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) strings.editPost else currentCategory.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isImeVisible || isAnyFieldFocused) {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                            if (isEditing) {
                                isEditing = false
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("back_from_detail_button")
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isEditing) strings.cancel else strings.close
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        Button(
                            onClick = { saveEdits() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(38.dp)
                                .testTag("save_edits_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = strings.saveChanges, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Edit Post button in Top Bar
                        IconButton(
                            onClick = {
                                editTitle = merit.title
                                editCategory = MeritCategory.fromString(merit.category)
                                editDescription = merit.description
                                editDedication = merit.dedication
                                editImageUri = merit.imageUri
                                isEditing = true
                            },
                            modifier = Modifier.testTag("edit_merit_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.editPost,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = {
                                val shareText = buildString {
                                    append("🌿 Bodhi Merit: ${merit.title}\n")
                                    append("Category: ${currentCategory.paliName} (${currentCategory.title})\n\n")
                                    if (merit.description.isNotBlank()) {
                                        append("${merit.description}\n\n")
                                    }
                                    if (merit.dedication.isNotBlank()) {
                                        append("Dedication: ${merit.dedication}\n")
                                    }
                                    append("Recorded on the Bodhi Tree of Good Deeds.")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Wholesome Merit"))
                            },
                            modifier = Modifier.testTag("share_merit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = strings.shareMerit
                            )
                        }

                        // Delete Button
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.testTag("delete_merit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = strings.delete,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
            .testTag("merit_full_screen_post")
    ) { paddingValues ->
        if (isEditing) {
            // ==================== EDIT MODE ====================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = strings.editPost,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Category Selector Chips
                Text(
                    text = strings.categoryLabel,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MeritCategory.entries.forEach { cat ->
                        val isSelected = editCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { editCategory = cat },
                            label = { Text(strings.categoryTitle(cat)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cat.leafColor.copy(alpha = 0.2f),
                                selectedLabelColor = cat.leafColor
                            ),
                            modifier = Modifier.testTag("edit_category_chip_${cat.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title TextField
                OutlinedTextField(
                    value = editTitle,
                    onValueChange = {
                        editTitle = it
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
                        .testTag("edit_merit_title_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Media Action Buttons
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Upload Media Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onUploadMedia()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("edit_upload_media_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = strings.uploadMedia, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                    }

                    // Camera Media Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            showCameraChooser = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("edit_camera_media_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = strings.cameraMedia, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                    }
                }

                // Media Preview Card (ONLY shown if media is actually attached)
                if (!editImageUri.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    val isEditVideo = remember(editImageUri) {
                        val lower = editImageUri!!.lowercase()
                        lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".mkv") ||
                                lower.endsWith(".3gp") || lower.endsWith(".webm") || lower.contains("merit_video_")
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("edit_merit_image_preview")
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (isEditVideo) {
                                InAppVideoPlayer(
                                    videoUriOrPath = editImageUri!!,
                                    modifier = Modifier.fillMaxSize(),
                                    autoPlay = false
                                )
                            } else {
                                RenderMeritImage(
                                    imageUri = editImageUri,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Detach / Clear Media Button
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .clickable { editImageUri = null }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = strings.removeMedia,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Description / Story
                OutlinedTextField(
                    value = editDescription,
                    onValueChange = { editDescription = it },
                    label = { Text(strings.storyLabel) },
                    placeholder = { Text(strings.storyPlaceholder) },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isDescFocused = it.isFocused }
                        .testTag("edit_merit_description_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dedication
                OutlinedTextField(
                    value = editDedication,
                    onValueChange = { editDedication = it },
                    label = { Text(strings.dedicationLabel) },
                    placeholder = { Text(strings.dedicationPlaceholder) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isDedicationFocused = it.isFocused }
                        .testTag("edit_merit_dedication_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save / Cancel Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            editTitle = merit.title
                            editCategory = MeritCategory.fromString(merit.category)
                            editDescription = merit.description
                            editDedication = merit.dedication
                            editImageUri = merit.imageUri
                            isEditing = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(strings.cancel)
                    }

                    Button(
                        onClick = { saveEdits() },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("save_edits_button_bottom"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = strings.saveChanges, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        } else {
            // ==================== VIEW MODE (FULL SCREEN) ====================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Prominent Media (ONLY IF MEDIA EXISTS! No default image fallback)
                if (!merit.imageUri.isNullOrBlank()) {
                    if (isVideo) {
                        InAppVideoPlayer(
                            videoUriOrPath = merit.imageUri!!,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            autoPlay = false,
                            allowFullScreenToggle = true
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { isFullScreenImage = true }
                                .testTag("merit_post_image_card")
                        ) {
                            RenderMeritImage(
                                imageUri = merit.imageUri,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Bottom info badge
                            Surface(
                                color = Color.Black.copy(alpha = 0.60f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tap to view full photo",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Post Content Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = currentCategory.leafColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, currentCategory.leafColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = currentCategory.leafColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${currentCategory.paliName} • ${strings.categoryTitle(currentCategory)}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = currentCategory.leafColor
                                )
                            )
                        }
                    }
                    // Date & Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Title
                    Text(
                        text = merit.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 32.sp
                        ),
                        modifier = Modifier.testTag("merit_detail_title")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description / Reflection
                    if (merit.description.isNotBlank()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = merit.description,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 26.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier
                                    .padding(18.dp)
                                    .testTag("merit_detail_description")
                            )
                        }
                    }

                    // Dedication of Merit (Pattidāna)
                    if (merit.dedication.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF8E1),
                            border = BorderStroke(1.dp, Color(0xFFFFE082)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_merit_water),
                                    contentDescription = "Sharing Merit",
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = "Dedication of Merit (Pattidāna)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "“${merit.dedication}”",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontStyle = FontStyle.Italic,
                                            color = Color(0xFF4E342E),
                                            lineHeight = 22.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions Row: Rejoice & Edit Post
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = {
                                rejoiced = true
                                MindfulSoundHelper.playSingingBowlChime()
                                MindfulSoundHelper.performMindfulHaptic(context, strong = true)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (rejoiced) currentCategory.leafColor else MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (rejoiced) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("rejoice_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (rejoiced) "Sādhu! Sādhu!" else "Rejoice (Sādhu)",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Prominent Edit Post Button
                        Button(
                            onClick = {
                                editTitle = merit.title
                                editCategory = MeritCategory.fromString(merit.category)
                                editDescription = merit.description
                                editDedication = merit.dedication
                                editImageUri = merit.imageUri
                                isEditing = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("edit_post_button_main")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.editPost,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
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

    // Full screen image preview dialog
    if (isFullScreenImage) {
        Dialog(
            onDismissRequest = { isFullScreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                RenderMeritImage(
                    imageUri = if (isEditing) editImageUri else merit.imageUri,
                    defaultDrawableRes = currentCategory.defaultDrawableRes,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { isFullScreenImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = if (strings.isSinhala) "පින් පත මකා දමන්නද?" else "Remove Leaf from Bodhi Tree?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (strings.isSinhala)
                        "මෙම පින් සටහන සහ එහි ඡායාරූපය බෝධි වෘක්ෂයෙන් ස්ථිරවම ඉවත් කරනු ලැබේ."
                    else
                        "This merit post and its photograph will be permanently removed from your sacred tree."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(merit)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text(strings.delete, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
