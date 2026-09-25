package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.MeritCategory
import com.example.ui.i18n.LocalAppStrings

data class PresetArtwork(
    val id: String,
    val name: String,
    val resId: Int
)

/**
 * Full-Screen "Add New Merit Post" Page.
 * - Zero keyboard lag (no layout thrashing, adjustResize window, and stable state reads).
 * - Keyboard back-press safety: Pressing back while the keyboard is open only dismisses the keyboard,
 *   preserving all entered text and picked photos without dismissing the post page!
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun AddMeritDialog(
    onDismiss: () -> Unit,
    onPickPhoto: () -> Unit,
    pickedPhotoUri: String?,
    onAddMerit: (title: String, category: MeritCategory, description: String, dedication: String, imageUri: String?) -> Unit
) {
    val strings = LocalAppStrings.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MeritCategory.DANA) }
    var description by remember { mutableStateOf("") }
    var dedication by remember {
        mutableStateOf(
            if (strings.isSinhala) "සියලු සත්වයෝ සුවපත් වෙත්වා, නිදුක් වෙත්වා, නිරෝගී වෙත්වා."
            else "May all beings be well, happy, and peaceful."
        )
    }
    var selectedImageUri by remember { mutableStateOf<String?>("preset:ic_merit_dana") }
    var titleError by remember { mutableStateOf(false) }

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

    LaunchedEffect(pickedPhotoUri) {
        if (!pickedPhotoUri.isNullOrBlank()) {
            selectedImageUri = pickedPhotoUri
        }
    }

    // Preset sacred artworks
    val presets = remember {
        listOf(
            PresetArtwork("preset:ic_merit_dana", "Alms / Dana", R.drawable.ic_merit_dana),
            PresetArtwork("preset:ic_merit_lotus", "Lotus Offering", R.drawable.ic_merit_lotus),
            PresetArtwork("preset:ic_merit_meditation", "Meditation", R.drawable.ic_merit_meditation),
            PresetArtwork("preset:ic_merit_lantern", "Wisdom Lamp", R.drawable.ic_merit_lantern),
            PresetArtwork("preset:ic_merit_kindness", "Loving Care", R.drawable.ic_merit_kindness),
            PresetArtwork("preset:ic_merit_water", "Merit Water", R.drawable.ic_merit_water),
            PresetArtwork("preset:ic_merit_stupa", "Sacred Stupa", R.drawable.ic_merit_stupa),
            PresetArtwork("preset:ic_merit_bodhi", "Bodhi Leaf", R.drawable.ic_merit_bodhi)
        )
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
            selectedImageUri
        )
        onDismiss()
    }

    Dialog(
        onDismissRequest = {
            if (isImeVisible || isAnyFieldFocused) {
                focusManager.clearFocus()
                keyboardController?.hide()
            } else {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false // BackHandler manages back presses with 100% precision
        )
    ) {
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
                    modifier = Modifier.padding(bottom = 14.dp)
                )

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
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = cat
                                if (selectedImageUri?.startsWith("preset:") == true) {
                                    selectedImageUri = when (cat) {
                                        MeritCategory.DANA -> "preset:ic_merit_dana"
                                        MeritCategory.SILA -> "preset:ic_merit_lotus"
                                        MeritCategory.BHAVANA -> "preset:ic_merit_meditation"
                                        MeritCategory.KINDNESS -> "preset:ic_merit_kindness"
                                        MeritCategory.TEMPLE -> "preset:ic_merit_stupa"
                                        MeritCategory.DEDICATION -> "preset:ic_merit_water"
                                    }
                                }
                            },
                            label = {
                                Text(strings.categoryTitle(cat))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cat.leafColor.copy(alpha = 0.2f),
                                selectedLabelColor = cat.leafColor
                            ),
                            modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                // Selected Image Preview
                Text(
                    text = if (strings.isSinhala) "පින්කමේ ඡායාරූපය" else "Image of the Post",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = if (strings.isSinhala)
                        "ඔබේ දුරකථනයෙන් ඡායාරූපයක් තෝරන්න හෝ බෞද්ධ චිත්‍රයක් තෝරන්න."
                    else
                        "Choose a photograph from your device or select a sacred Buddhist motif.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("add_merit_image_preview")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        RenderMeritImage(
                            imageUri = selectedImageUri,
                            defaultDrawableRes = selectedCategory.defaultDrawableRes,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Button over preview to choose from device photo gallery
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clickable {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    onPickPhoto()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedImageUri != null && !selectedImageUri!!.startsWith("preset:"))
                                        strings.changePhoto else strings.attachPhoto,
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preset Artworks Horizontal Selector
                Text(
                    text = if (strings.isSinhala) "හෝ පූජනීය චිත්‍රයක් තෝරන්න:" else "Or pick sacred artwork:",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(presets, key = { it.id }) { preset ->
                        val isChosen = selectedImageUri == preset.id
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isChosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isChosen) 2.dp else 1.dp,
                                    color = if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedImageUri = preset.id }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = preset.resId),
                                contentDescription = preset.name,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isChosen) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
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
    }
}
