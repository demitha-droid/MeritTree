package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BodhiDatabase
import com.example.data.MeritCategory
import com.example.data.MeritEntity
import com.example.data.MeritRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.sound.MindfulSoundHelper
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class BodhiTab {
    TREE,
    JOURNAL,
    SETTINGS
}

class BodhiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MeritRepository
    private val backupManager: com.example.data.backup.MeritBackupManager
    private val prefs = application.getSharedPreferences("bodhi_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        AppThemeMode.fromString(prefs.getString("theme_mode", AppThemeMode.GREEN.name))
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(
        AppLanguage.fromString(prefs.getString("app_language", AppLanguage.ENGLISH.name))
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(prefs.getBoolean("haptic_enabled", true))
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    private val _isBackupLoading = MutableStateFlow(false)
    val isBackupLoading: StateFlow<Boolean> = _isBackupLoading.asStateFlow()

    init {
        val database = BodhiDatabase.getDatabase(application)
        repository = MeritRepository(database.meritDao())
        backupManager = com.example.data.backup.MeritBackupManager(application, repository)
    }

    val allMerits: StateFlow<List<MeritEntity>> = repository.allMerits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeritsDescending: StateFlow<List<MeritEntity>> = repository.allMeritsDescending
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMerit = MutableStateFlow<MeritEntity?>(null)
    val selectedMerit: StateFlow<MeritEntity?> = _selectedMerit.asStateFlow()

    private val _newlySproutedId = MutableStateFlow<Long?>(null)
    val newlySproutedId: StateFlow<Long?> = _newlySproutedId.asStateFlow()

    private val _revealedLeafIds = MutableStateFlow<Set<Long>>(emptySet())
    val revealedLeafIds: StateFlow<Set<Long>> = _revealedLeafIds.asStateFlow()

    private val _filterCategory = MutableStateFlow<MeritCategory?>(null)
    val filterCategory: StateFlow<MeritCategory?> = _filterCategory.asStateFlow()

    private val _activeTab = MutableStateFlow(BodhiTab.TREE)
    val activeTab: StateFlow<BodhiTab> = _activeTab.asStateFlow()

    val filteredMerits: StateFlow<List<MeritEntity>> = allMerits

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
        triggerHaptic()
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.name).apply()
        triggerHaptic()
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun setHapticEnabled(enabled: Boolean) {
        _isHapticEnabled.value = enabled
        prefs.edit().putBoolean("haptic_enabled", enabled).apply()
    }

    fun triggerBellChime() {
        if (_isSoundEnabled.value) {
            MindfulSoundHelper.playSingingBowlChime()
        }
    }

    fun triggerHaptic(strong: Boolean = false) {
        if (_isHapticEnabled.value) {
            MindfulSoundHelper.performMindfulHaptic(getApplication(), strong)
        }
    }

    private var leafRevertJob: Job? = null

    fun selectMerit(merit: MeritEntity?) {
        _selectedMerit.value = merit
        if (merit != null) {
            triggerHaptic()
            hideLeaf()
        }
    }

    fun setFilterCategory(category: MeritCategory?) {
        _filterCategory.value = category
    }

    fun setActiveTab(tab: BodhiTab) {
        _activeTab.value = tab
    }

    fun clearNewlySprouted() {
        _newlySproutedId.value = null
    }

    fun revealLeaf(meritId: Long, autoRevertMs: Long = 2000L) {
        leafRevertJob?.cancel()
        // Keep only 1 leaf revealed at a time so leaves NEVER overlap or block each other
        _revealedLeafIds.value = setOf(meritId)
        leafRevertJob = viewModelScope.launch {
            delay(autoRevertMs)
            _revealedLeafIds.value = _revealedLeafIds.value - meritId
        }
    }

    fun hideLeaf(meritId: Long? = null) {
        leafRevertJob?.cancel()
        if (meritId != null) {
            _revealedLeafIds.value = _revealedLeafIds.value - meritId
        } else {
            _revealedLeafIds.value = emptySet()
        }
    }

    /**
     * Creates a destination temporary File and Content Uri via FileProvider for capturing photos or videos.
     */
    fun createMediaCaptureFile(isVideo: Boolean): Pair<Uri, File>? {
        return try {
            val context = getApplication<Application>()
            val mediaDir = File(context.filesDir, "merit_photos").apply { if (!exists()) mkdirs() }
            val ext = if (isVideo) "mp4" else "jpg"
            val prefix = if (isVideo) "merit_video_" else "merit_photo_"
            val file = File(mediaDir, "${prefix}${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$ext")
            if (!file.exists()) {
                file.createNewFile()
            }
            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Pair(contentUri, file)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Saves a captured Bitmap directly to internal filesDir as a JPEG file.
     */
    fun saveBitmapLocally(bitmap: android.graphics.Bitmap): String? {
        return try {
            val context = getApplication<Application>()
            val mediaDir = File(context.filesDir, "merit_photos").apply { if (!exists()) mkdirs() }
            val file = File(mediaDir, "merit_photo_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Copies and downsamples an external image or video Uri into internal app filesDir so it remains permanently accessible
     * with low memory footprint and instant rendering.
     */
    fun saveImageLocally(sourceUri: Uri): String? = saveMediaLocally(sourceUri)

    fun saveMediaLocally(sourceUri: Uri): String? {
        return try {
            val context = getApplication<Application>()
            val mediaDir = File(context.filesDir, "merit_photos").apply { if (!exists()) mkdirs() }
            val mimeType = context.contentResolver.getType(sourceUri)?.lowercase()
            val uriString = sourceUri.toString().lowercase()
            val isVideo = mimeType?.startsWith("video/") == true ||
                    uriString.endsWith(".mp4") ||
                    uriString.endsWith(".mov") ||
                    uriString.endsWith(".mkv") ||
                    uriString.endsWith(".3gp") ||
                    uriString.endsWith(".webm")

            if (isVideo) {
                val fileName = "merit_video_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.mp4"
                val destFile = File(mediaDir, fileName)
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                // Pre-generate companion thumbnail file immediately so journal feeds render it instantaneously
                try {
                    val retriever = android.media.MediaMetadataRetriever()
                    retriever.setDataSource(destFile.absolutePath)
                    val frame = retriever.getFrameAtTime(500_000, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        ?: retriever.frameAtTime
                    retriever.release()
                    if (frame != null) {
                        val thumbFile = File("${destFile.absolutePath}.thumb.jpg")
                        FileOutputStream(thumbFile).use { out ->
                            frame.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
                        }
                    }
                } catch (_: Exception) {}

                return destFile.absolutePath
            }

            val fileName = "merit_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val destFile = File(mediaDir, fileName)

            val boundsOptions = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                android.graphics.BitmapFactory.decodeStream(input, null, boundsOptions)
            }

            var inSampleSize = 1
            val maxDim = 1600
            while (boundsOptions.outWidth / (inSampleSize * 2) >= maxDim ||
                boundsOptions.outHeight / (inSampleSize * 2) >= maxDim
            ) {
                inSampleSize *= 2
            }

            val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }

            val bitmap = context.contentResolver.openInputStream(sourceUri)?.use { input ->
                android.graphics.BitmapFactory.decodeStream(input, null, decodeOptions)
            }

            if (bitmap != null) {
                FileOutputStream(destFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
                }
                bitmap.recycle()
                destFile.absolutePath
            } else {
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                destFile.absolutePath
            }
        } catch (_: Exception) {
            null
        }
    }

    fun addMerit(
        title: String,
        category: MeritCategory,
        description: String,
        dedication: String,
        imageUri: String?,
        targetSlotIndex: Int? = null
    ) {
        viewModelScope.launch {
            val currentList = allMerits.value
            val usedSlots = currentList.map { it.branchIndex }.toSet()
            val maxSlots = com.example.ui.components.BodhiTreeGeometry.LEAF_SLOTS.size

            val branchIndex = if (targetSlotIndex != null && targetSlotIndex in 0 until maxSlots && targetSlotIndex !in usedSlots) {
                targetSlotIndex
            } else {
                (0 until maxSlots).firstOrNull { it !in usedSlots } ?: (currentList.size % maxSlots)
            }

            val newMerit = MeritEntity(
                title = title.trim(),
                category = category.name,
                description = description.trim(),
                dedication = dedication.trim(),
                imageUri = imageUri,
                timestamp = System.currentTimeMillis(),
                branchIndex = branchIndex,
                leafOffsetRatio = 0.5f,
                leafAngleOffset = 0f
            )

            val newId = repository.insert(newMerit)
            _newlySproutedId.value = newId

            triggerBellChime()
            triggerHaptic(strong = true)
        }
    }

    fun updateMerit(
        merit: MeritEntity,
        newTitle: String,
        newCategory: MeritCategory,
        newDescription: String,
        newDedication: String,
        newImageUri: String?
    ) {
        viewModelScope.launch {
            val updated = merit.copy(
                title = newTitle.trim(),
                category = newCategory.name,
                description = newDescription.trim(),
                dedication = newDedication.trim(),
                imageUri = newImageUri
            )
            repository.update(updated)
            if (_selectedMerit.value?.id == merit.id) {
                _selectedMerit.value = updated
            }
            triggerBellChime()
            triggerHaptic(strong = true)
        }
    }

    fun deleteMerit(merit: MeritEntity) {
        viewModelScope.launch {
            if (_selectedMerit.value?.id == merit.id) {
                _selectedMerit.value = null
            }
            _revealedLeafIds.value = _revealedLeafIds.value - merit.id
            repository.delete(merit)
            triggerHaptic()
        }
    }

    fun exportBackup(uri: Uri, onComplete: (Result<com.example.data.backup.BackupSummary>) -> Unit) {
        viewModelScope.launch {
            _isBackupLoading.value = true
            val context = getApplication<Application>()
            val result = try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    backupManager.createBackup(os)
                } ?: Result.failure(Exception("Cannot open destination file"))
            } catch (e: Exception) {
                Result.failure(e)
            }
            _isBackupLoading.value = false
            if (result.isSuccess) {
                triggerBellChime()
                triggerHaptic(strong = true)
            }
            onComplete(result)
        }
    }

    fun inspectBackup(uri: Uri, onComplete: (Result<com.example.data.backup.BackupSummary>) -> Unit) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val result = try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    backupManager.inspectBackup(inputStream)
                } ?: Result.failure(Exception("Cannot open backup file"))
            } catch (e: Exception) {
                Result.failure(e)
            }
            onComplete(result)
        }
    }

    fun restoreBackup(uri: Uri, replaceAll: Boolean, onComplete: (Result<com.example.data.backup.BackupSummary>) -> Unit) {
        viewModelScope.launch {
            _isBackupLoading.value = true
            val context = getApplication<Application>()
            val result = try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    backupManager.restoreBackup(inputStream, replaceAll)
                } ?: Result.failure(Exception("Cannot read backup file"))
            } catch (e: Exception) {
                Result.failure(e)
            }
            _isBackupLoading.value = false
            if (result.isSuccess) {
                triggerBellChime()
                triggerHaptic(strong = true)
            }
            onComplete(result)
        }
    }

    fun shareBackup(onReady: (Uri) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isBackupLoading.value = true
            val tempFileResult = backupManager.createBackupTempFile()
            _isBackupLoading.value = false
            tempFileResult.fold(
                onSuccess = { file ->
                    val context = getApplication<Application>()
                    val contentUri = androidx.core.content.FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    onReady(contentUri)
                },
                onFailure = { err ->
                    onError(err.localizedMessage ?: "Failed to generate backup")
                }
            )
        }
    }
}
