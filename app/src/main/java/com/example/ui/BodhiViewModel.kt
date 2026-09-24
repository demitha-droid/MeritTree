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

    init {
        val database = BodhiDatabase.getDatabase(application)
        repository = MeritRepository(database.meritDao())
    }

    val allMerits: StateFlow<List<MeritEntity>> = repository.allMerits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeritsDescending: StateFlow<List<MeritEntity>> = repository.allMeritsDescending
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMerit = MutableStateFlow<MeritEntity?>(null)
    val selectedMerit: StateFlow<MeritEntity?> = _selectedMerit.asStateFlow()

    private val _newlySproutedId = MutableStateFlow<Long?>(null)
    val newlySproutedId: StateFlow<Long?> = _newlySproutedId.asStateFlow()

    private val _filterCategory = MutableStateFlow<MeritCategory?>(null)
    val filterCategory: StateFlow<MeritCategory?> = _filterCategory.asStateFlow()

    private val _activeTab = MutableStateFlow(BodhiTab.TREE)
    val activeTab: StateFlow<BodhiTab> = _activeTab.asStateFlow()

    val filteredMerits: StateFlow<List<MeritEntity>> = combine(allMerits, _filterCategory) { merits, category ->
        if (category == null) merits else merits.filter { it.category == category.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun selectMerit(merit: MeritEntity?) {
        _selectedMerit.value = merit
        if (merit != null) {
            triggerHaptic()
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

    /**
     * Copies an external image Uri into internal app filesDir so it remains permanently accessible.
     */
    fun saveImageLocally(sourceUri: Uri): String? {
        return try {
            val context = getApplication<Application>()
            val imagesDir = File(context.filesDir, "merit_photos").apply { if (!exists()) mkdirs() }
            val fileName = "merit_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val destFile = File(imagesDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
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

    fun deleteMerit(merit: MeritEntity) {
        viewModelScope.launch {
            if (_selectedMerit.value?.id == merit.id) {
                _selectedMerit.value = null
            }
            repository.delete(merit)
            triggerHaptic()
        }
    }
}
