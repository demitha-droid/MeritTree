package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MeritCategory
import com.example.ui.BodhiTab
import com.example.ui.BodhiViewModel
import com.example.ui.components.AddMeritDialog
import com.example.ui.components.BodhiTreeCanvas
import com.example.ui.components.MeritDetailSheet
import com.example.ui.components.MeritJournalList
import com.example.ui.components.MeritTreeTopBar
import com.example.ui.components.SettingsScreen
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BodhiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val strings = remember(appLanguage) { AppStrings(appLanguage) }

            CompositionLocalProvider(LocalAppStrings provides strings) {
                MyApplicationTheme(themeMode = themeMode) {
                    BodhiMeritApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodhiMeritApp(viewModel: BodhiViewModel) {
    val strings = LocalAppStrings.current
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isHapticEnabled.collectAsStateWithLifecycle()
    val merits by viewModel.filteredMerits.collectAsStateWithLifecycle()
    val allMerits by viewModel.allMerits.collectAsStateWithLifecycle()
    val allMeritsDescending by viewModel.allMeritsDescending.collectAsStateWithLifecycle()
    val selectedMerit by viewModel.selectedMerit.collectAsStateWithLifecycle()
    val newlySproutedId by viewModel.newlySproutedId.collectAsStateWithLifecycle()
    val revealedLeafIds by viewModel.revealedLeafIds.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isBackupLoading by viewModel.isBackupLoading.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var targetSlotId by remember { mutableStateOf<Int?>(null) }
    var currentPickedMediaUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var editPickedMediaUris by remember { mutableStateOf<List<String>>(emptyList()) }

    val context = LocalContext.current
    var pendingCameraMediaFile by remember { mutableStateOf<File?>(null) }
    var isCameraForAdd by remember { mutableStateOf(true) }
    var pendingCameraAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Media Picker (Multiple Photos & Videos from Device Gallery)
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val savedPaths = uris.mapNotNull { uri ->
                viewModel.saveMediaLocally(uri) ?: uri.toString()
            }
            if (showAddDialog) {
                currentPickedMediaUris = (currentPickedMediaUris + savedPaths).distinct()
            } else {
                editPickedMediaUris = (editPickedMediaUris + savedPaths).distinct()
            }
        }
    }

    // Camera Permission Launcher (Handles dangerous CAMERA permission safely)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingCameraAction?.invoke()
        } else {
            Toast.makeText(
                context,
                if (strings.isSinhala) "කැමරා අවසරය අවශ්‍ය වේ" else "Camera permission is required to capture photos",
                Toast.LENGTH_LONG
            ).show()
        }
        pendingCameraAction = null
    }

    // Camera Capture: Photo Preview fallback if TakePicture intent has FileProvider issue on device
    val takePhotoPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val localPath = viewModel.saveBitmapLocally(bitmap)
            if (localPath != null) {
                if (isCameraForAdd) {
                    currentPickedMediaUris = (currentPickedMediaUris + localPath).distinct()
                } else {
                    editPickedMediaUris = (editPickedMediaUris + localPath).distinct()
                }
            }
        }
    }

    // Camera Capture: Full-resolution Photo
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraMediaFile != null) {
            val path = pendingCameraMediaFile!!.absolutePath
            if (isCameraForAdd) {
                currentPickedMediaUris = (currentPickedMediaUris + path).distinct()
            } else {
                editPickedMediaUris = (editPickedMediaUris + path).distinct()
            }
        }
    }

    // Camera Capture: Video
    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && pendingCameraMediaFile != null) {
            val path = pendingCameraMediaFile!!.absolutePath
            if (isCameraForAdd) {
                currentPickedMediaUris = (currentPickedMediaUris + path).distinct()
            } else {
                editPickedMediaUris = (editPickedMediaUris + path).distinct()
            }
        }
    }

    fun executeCameraPhoto(isAdd: Boolean) {
        isCameraForAdd = isAdd
        val result = viewModel.createMediaCaptureFile(isVideo = false)
        if (result != null) {
            val (contentUri, file) = result
            pendingCameraMediaFile = file
            try {
                takePhotoLauncher.launch(contentUri)
            } catch (_: Exception) {
                try {
                    takePhotoPreviewLauncher.launch(null)
                } catch (_: Exception) {
                    Toast.makeText(
                        context,
                        if (strings.isSinhala) "කැමරාව සොයාගත නොහැකි විය. ගැලරිය විවෘත වේ..." else "Camera unavailable. Opening gallery...",
                        Toast.LENGTH_SHORT
                    ).show()
                    mediaPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
        } else {
            try {
                takePhotoPreviewLauncher.launch(null)
            } catch (_: Exception) {
                mediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }

    fun launchCameraPhoto(isAdd: Boolean) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            executeCameraPhoto(isAdd)
        } else {
            pendingCameraAction = { executeCameraPhoto(isAdd) }
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun executeCameraVideo(isAdd: Boolean) {
        isCameraForAdd = isAdd
        val result = viewModel.createMediaCaptureFile(isVideo = true)
        if (result != null) {
            val (contentUri, file) = result
            pendingCameraMediaFile = file
            try {
                captureVideoLauncher.launch(contentUri)
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    if (strings.isSinhala) "වීඩියෝ කැමරාව සොයාගත නොහැකි විය. ගැලරිය විවෘත වේ..." else "Video camera unavailable. Opening gallery...",
                    Toast.LENGTH_SHORT
                ).show()
                mediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            }
        } else {
            mediaPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }
    }

    fun launchCameraVideo(isAdd: Boolean) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            executeCameraVideo(isAdd)
        } else {
            pendingCameraAction = { executeCameraVideo(isAdd) }
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Clear newly sprouted animation highlight after 4 seconds
    LaunchedEffect(newlySproutedId) {
        if (newlySproutedId != null) {
            kotlinx.coroutines.delay(4000)
            viewModel.clearNewlySprouted()
        }
    }

    // Back gesture handling:
    // When on other tabs like Journal or Settings, the back gesture returns to Home (Merit Tree) instead of quitting.
    // When on Home (Merit Tree), back gesture closes the app as normal.
    BackHandler(enabled = activeTab != BodhiTab.TREE && !showAddDialog && selectedMerit == null) {
        viewModel.setActiveTab(BodhiTab.TREE)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        topBar = {
            MeritTreeTopBar(
                activeLeavesCount = allMerits.size,
                onSettingsClick = { viewModel.setActiveTab(BodhiTab.SETTINGS) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("bodhi_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // 1. Bodhi Tree Tab
                NavigationBarItem(
                    selected = activeTab == BodhiTab.TREE,
                    onClick = { viewModel.setActiveTab(BodhiTab.TREE) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == BodhiTab.TREE) Icons.Filled.Park else Icons.Outlined.Park,
                            contentDescription = strings.navTree
                        )
                    },
                    label = { Text(strings.navTree) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_tree")
                )

                // 2. Merit Journal Tab
                NavigationBarItem(
                    selected = activeTab == BodhiTab.JOURNAL,
                    onClick = { viewModel.setActiveTab(BodhiTab.JOURNAL) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == BodhiTab.JOURNAL) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = strings.navJournal
                        )
                    },
                    label = { Text(strings.navJournal) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_journal")
                )

                // 3. Settings Tab
                NavigationBarItem(
                    selected = activeTab == BodhiTab.SETTINGS,
                    onClick = { viewModel.setActiveTab(BodhiTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == BodhiTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = strings.navSettings
                        )
                    },
                    label = { Text(strings.navSettings) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        },
        floatingActionButton = {
            if (activeTab != BodhiTab.SETTINGS) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.triggerHaptic()
                        targetSlotId = null
                        currentPickedMediaUris = emptyList()
                        showAddDialog = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    text = {
                        Text(
                            text = strings.sproutLeaf,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    modifier = Modifier.testTag("sprout_leaf_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Content Area based on Active Tab with buttery smooth Crossfade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                androidx.compose.animation.Crossfade(
                    targetState = activeTab,
                    animationSpec = androidx.compose.animation.core.tween(130),
                    label = "main_tab_crossfade"
                ) { currentTab ->
                    when (currentTab) {
                        BodhiTab.TREE -> {
                            BodhiTreeCanvas(
                                merits = allMerits,
                                newlySproutedId = newlySproutedId,
                                revealedMeritIds = revealedLeafIds,
                                onRevealLeaf = { clickedMerit ->
                                    viewModel.triggerHaptic(strong = true)
                                    viewModel.triggerBellChime()
                                    viewModel.revealLeaf(clickedMerit.id)
                                },
                                onLeafClick = { clickedMerit ->
                                    viewModel.triggerHaptic()
                                    viewModel.selectMerit(clickedMerit)
                                },
                                onEmptyLeafClick = { slotId ->
                                    viewModel.triggerHaptic()
                                    targetSlotId = slotId
                                    currentPickedMediaUris = emptyList()
                                    showAddDialog = true
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        BodhiTab.JOURNAL -> {
                            MeritJournalList(
                                merits = allMeritsDescending,
                                onMeritClick = { clickedMerit ->
                                    viewModel.triggerHaptic()
                                    viewModel.selectMerit(clickedMerit)
                                }
                            )
                        }
                        BodhiTab.SETTINGS -> {
                            SettingsScreen(
                                currentLanguage = appLanguage,
                                onLanguageSelected = { viewModel.setAppLanguage(it) },
                                currentTheme = themeMode,
                                onThemeSelected = { viewModel.setThemeMode(it) },
                                isSoundEnabled = isSoundEnabled,
                                onSoundToggle = { viewModel.setSoundEnabled(it) },
                                isHapticEnabled = isHapticEnabled,
                                onHapticToggle = { viewModel.setHapticEnabled(it) },
                                onPlayTestChime = { viewModel.triggerBellChime() },
                                allMerits = allMerits,
                                isBackupLoading = isBackupLoading,
                                onExportBackup = { uri, cb -> viewModel.exportBackup(uri, cb) },
                                onInspectBackup = { uri, cb -> viewModel.inspectBackup(uri, cb) },
                                onRestoreBackup = { uri, replaceAll, cb -> viewModel.restoreBackup(uri, replaceAll, cb) },
                                onShareBackup = { onReady, onError -> viewModel.shareBackup(onReady, onError) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Sheet for clicked leaf / post (Full Screen with Edit Mode)
    selectedMerit?.let { merit ->
        MeritDetailSheet(
            merit = merit,
            onDismiss = {
                editPickedMediaUris = emptyList()
                viewModel.selectMerit(null)
            },
            onDelete = {
                viewModel.deleteMerit(it)
            },
            onUpdate = { targetMerit, newTitle, newCategory, newDesc, newDedication, newImageUri, newTimestamp ->
                viewModel.updateMerit(
                    merit = targetMerit,
                    newTitle = newTitle,
                    newCategory = newCategory,
                    newDescription = newDesc,
                    newDedication = newDedication,
                    newImageUri = newImageUri,
                    newTimestamp = newTimestamp
                )
                editPickedMediaUris = emptyList()
            },
            onUploadMedia = {
                mediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            },
            onCapturePhoto = {
                launchCameraPhoto(isAdd = false)
            },
            onCaptureVideo = {
                launchCameraVideo(isAdd = false)
            },
            pickedMediaUris = editPickedMediaUris
        )
    }

    // Add Merit / Sprout Leaf Dialog
    if (showAddDialog) {
        // Cycle category so each sprouted leaf naturally introduces a different vibrant color
        val nextVariedCategory = remember(merits.size, targetSlotId, showAddDialog) {
            val entries = MeritCategory.entries
            val slotOffset = targetSlotId ?: 0
            val idx = (merits.size + slotOffset) % entries.size
            entries[idx]
        }

        AddMeritDialog(
            onDismiss = {
                currentPickedMediaUris = emptyList()
                showAddDialog = false
            },
            onUploadMedia = {
                mediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            },
            onCapturePhoto = {
                launchCameraPhoto(isAdd = true)
            },
            onCaptureVideo = {
                launchCameraVideo(isAdd = true)
            },
            pickedMediaUris = currentPickedMediaUris,
            initialCategory = nextVariedCategory,
            onAddMerit = { title, category, description, dedication, imageUri, timestamp ->
                viewModel.addMerit(
                    title = title,
                    category = category,
                    description = description,
                    dedication = dedication,
                    imageUri = imageUri,
                    customTimestamp = timestamp,
                    targetSlotIndex = targetSlotId
                )
                currentPickedMediaUris = emptyList()
                showAddDialog = false
            }
        )
    }
    }
}

@Composable
fun CategoryFilterBar(
    selectedCategory: MeritCategory?,
    onCategorySelected: (MeritCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All Leaves" Chip
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text(strings.filterAll) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("filter_all")
        )

        MeritCategory.entries.forEach { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(if (isSelected) null else cat) },
                label = { Text(strings.categoryTitle(cat)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = cat.leafColor.copy(alpha = 0.2f),
                    selectedLabelColor = cat.leafColor
                ),
                modifier = Modifier.testTag("filter_${cat.name.lowercase()}")
            )
        }
    }
}
