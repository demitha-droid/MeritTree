package com.example.ui.i18n

import androidx.compose.runtime.compositionLocalOf
import com.example.data.MeritCategory

class AppStrings(val language: AppLanguage) {

    val isSinhala = language == AppLanguage.SINHALA

    // App Bar & General
    val appName = if (isSinhala) "පුණ්ය වෘක්ෂය" else "Merit Tree"
    val treeName = if (isSinhala) "පුණ්ය වෘක්ෂය" else "Merit Tree"
    val appSubtitle = if (isSinhala) "බෝධි පින්කම් සටහන" else "Sacred Bodhi Journal"
    fun leavesInBloom(count: Int) = if (isSinhala) "පින් පත් $count ක් පිපී ඇත" else "$count sacred leaves in bloom"
    val mindfulBell = if (isSinhala) "ඝණ්ඨා නාදය" else "Mindful Bell"
    val shareMerit = if (isSinhala) "පින් බෙදාගන්න" else "Share Merit"

    // Navigation Tabs
    val navTree = if (isSinhala) "පුණ්ය වෘක්ෂය" else "Merit Tree"
    val navJournal = if (isSinhala) "පින් පොත" else "Journal"
    val navSettings = if (isSinhala) "සැකසුම්" else "Settings"

    // Actions
    val sproutLeaf = if (isSinhala) "පතක් දළු ලවන්න" else "Sprout Leaf"

    // Tree Guidance
    val guidanceBanner = if (isSinhala)
        "පින් පතක් ස්පර්ශ කර ඡායාරූපය බලන්න • හිස් පතක් ස්පර්ශ කර පිනක් එක්කරන්න"
    else
        "Touch awakened leaf for photo • Touch empty leaf to sprout"

    val emptyTreeNotice = if (isSinhala)
        "පින්බර බෝධි වෘක්ෂය ඔබේ කුසල ක්‍රියා බලාපොරොත්තුවෙන් සිටී.\nපතක් දළු ලවා අතු පතර පින් සුවඳින් පුරවන්න."
    else
        "The sacred Bodhi tree awaits your wholesome deeds.\nSprout a leaf to watch the canopy flourish."

    // Filter Chips
    val filterAll = if (isSinhala) "සියලු පින්" else "All Leaves"

    fun categoryTitle(category: MeritCategory): String {
        return if (isSinhala) {
            when (category) {
                MeritCategory.DANA -> "දන් දීම (ත්‍යාගශීලී බව)"
                MeritCategory.SILA -> "සිල් රැකීම (සදාචාරය)"
                MeritCategory.BHAVANA -> "භාවනා (සිත සන්සුන් කිරීම)"
                MeritCategory.KINDNESS -> "මෙත් වැඩීම (මෛත්‍රිය)"
                MeritCategory.TEMPLE -> "පූජා පැවැත්වීම (ගෞරවය)"
                MeritCategory.DEDICATION -> "පින් අනුමෝදන් කිරීම"
            }
        } else {
            "${category.paliName} (${category.title})"
        }
    }

    // Settings Screen
    val settingsTitle = if (isSinhala) "සැකසුම්" else "Settings"
    val languageSection = if (isSinhala) "භාෂාව (Language)" else "Language"
    val languageSubtitle = if (isSinhala) "යෙදුමේ භාෂාව තෝරන්න" else "Choose your preferred display language"
    val languageEnglish = "English"
    val languageSinhala = "සිංහල (Sinhala)"

    val themeSection = if (isSinhala) "යෙදුමේ තේමාව (App Theme)" else "App Theme"
    val themeSubtitle = if (isSinhala)
        "බෝධි වෘක්ෂය සඳහා ඔබ කැමති වර්ණ තේමාව තෝරන්න"
    else
        "Choose your preferred visual aesthetic for the sacred Bodhi tree."

    val themeGreenTitle = if (isSinhala) "හරිත (Green)" else "Green"
    val themeGreenDesc = if (isSinhala)
        "සන්සුන් වන අරණ සහ මරකත බෝධි වර්ණාවලිය"
    else
        "Sacred forest night and peaceful emerald Bodhi palette"

    val themeLightTitle = if (isSinhala) "දීප්තිමත් (Light)" else "Light"
    val themeLightDesc = if (isSinhala)
        "දීප්තිමත් හිරු රැස් සහ සුවපත් පත්ඉරු පැහැය"
    else
        "Calm ivory parchment and warm golden sunlight"

    val themeAmoledTitle = if (isSinhala) "ඇමොලෙඩ් (AMOLED)" else "AMOLED"
    val themeAmoledDesc = if (isSinhala)
        "ගැඹුරු කළු පැහැය සමග රන්වන් හා තඹ පැහැ බැබළීම"
    else
        "Pure pitch black (#000000) with luminous copper & golden aura"

    val soundSection = if (isSinhala) "ශබ්ද සහ කම්පන" else "Sound & Vibration"
    val singingBowlTitle = if (isSinhala) "ඝණ්ඨා නාදය" else "Singing Bowl Chime"
    val singingBowlDesc = if (isSinhala)
        "පින් පතක් පිපෙන විට 432Hz ශාන්ත සීනු නාදය"
    else
        "Play peaceful 432Hz bell when sprouting leaves"

    val hapticsTitle = if (isSinhala) "මෘදු කම්පන (Mindful Haptics)" else "Mindful Haptics"
    val hapticsDesc = if (isSinhala)
        "පින් පතක් ස්පර්ශ කිරීමේදී මෘදු කම්පනයක්"
    else
        "Gentle pulse upon touching leaves"

    val testBellButton = if (isSinhala) "පන්සලේ සීනු හඬ අසන්න" else "Ring Temple Bell Tone"

    val treeStatsSection = if (isSinhala) "බෝධි වෘක්ෂයේ විස්තර" else "Tree Statistics"
    val totalMeritsBloom = if (isSinhala) "මුළු පින් පත් සංඛ්‍යාව" else "Total Merits in Bloom"
    val leavesCountSuffix = if (isSinhala) "පත්" else "Leaves"
    val dedicateAllButton = if (isSinhala) "පින් අනුමෝදන් කිරීම (පැන් වැරීම)" else "Dedicate All Merits (Pattidāna)"
    val versionFooter = if (isSinhala) "බෝධි පින්කම් v1.0 • සියලු සත්වයෝ සුවපත් වෙත්වා" else "Bodhi Merit v1.0 • May all beings be peaceful"

    // Add Merit Dialog
    val addMeritTitle = if (isSinhala) "පින් පතක් දළු ලවන්න" else "Sprout a Sacred Leaf"
    val addMeritSubtitle = if (isSinhala)
        "ඔබ කළ යහපත් ක්‍රියාව හෝ කුසල් සිතුවිල්ල සටහන් කරන්න"
    else
        "Record your wholesome thought or compassionate action"

    val deedTitleLabel = if (isSinhala) "කළ පින්කමේ නම" else "Wholesome Deed Title"
    val deedTitlePlaceholder = if (isSinhala) "උදා: මල් පූජා කළා, කුරුල්ලන්ට කෑම දුන්නා" else "e.g. Fed neighborhood birds, offered flowers"
    val categoryLabel = if (isSinhala) "පින්කම් වර්ගය" else "Merit Category"
    val storyLabel = if (isSinhala) "පින්කම පිළිබඳ විස්තරය" else "Reflection / Story"
    val storyPlaceholder = if (isSinhala)
        "මෙමගින් ඔබේ සිතට හෝ අන් අයට සතුටක් ගෙන දුන්නේ කෙසේද?"
    else
        "How did it bring peace to your heart or others?"

    val dedicationLabel = if (isSinhala) "ප්‍රාර්ථනාව / අනුමෝදනාව" else "Dedication / Aspiration"
    val dedicationPlaceholder = if (isSinhala)
        "මේ පින් සියලු සත්වයන්ගේ සුවසෙත පිණිස වේවා..."
    else
        "May this merit bring peace and well-being to all..."

    val attachPhoto = if (isSinhala) "ඡායාරූපයක් එක් කරන්න" else "Attach Memory Photo"
    val changePhoto = if (isSinhala) "ඡායාරූපය වෙනස් කරන්න" else "Change Photo"
    val cancel = if (isSinhala) "අවලංගු කරන්න" else "Cancel"
    val sproutAction = if (isSinhala) "දළු ලවන්න" else "Sprout Leaf"

    // Detail Sheet
    val shareDedicate = if (isSinhala) "පින් අනුමෝදන් කරන්න" else "Share / Dedicate"
    val editPost = if (isSinhala) "පෝස්ටුව සංස්කරණය කරන්න" else "Edit Post"
    val saveChanges = if (isSinhala) "වෙනස්කම් සුරකින්න" else "Save Changes"
    val delete = if (isSinhala) "මකා දමන්න" else "Delete"
    val close = if (isSinhala) "වසන්න" else "Close"

    // Dedication Bottom Sheet
    val dedicationSheetTitle = if (isSinhala) "පැන් වඩා පින් අනුමෝදන් කිරීම" else "Pattidāna — Water Pouring Dedication"
    val performWaterDedication = if (isSinhala) "පැන් වඩමින් පින් දෙන්න" else "Perform Water Pouring Ceremony"
    val dedicationDone = if (isSinhala) "සාධු! සාධු! පින් අනුමෝදන් විය" else "Sadhu! Merits dedicated to all beings"

    // Journal
    val journalEmpty = if (isSinhala) "තවමත් පින්කම් සටහන් කර නොමැත" else "No merits recorded yet"
    val journalEmptySub = if (isSinhala) "පළමු පින් පත දළු ලවා ඔබේ පින් පොත අරඹන්න" else "Sprout your first leaf to begin your merit journal"

    // Backup & Restore (User Request)
    val backupRestoreSection = if (isSinhala) "උපස්ථ සහ ප්‍රතිසාධනය (Backup & Restore)" else "Backup & Restore"
    val backupRestoreSubtitle = if (isSinhala)
        "ඔබගේ සියලු පින්කම් සටහන් සහ ඡායාරූප සුරක්ෂිතව ගොනුවක් ලෙස උපස්ථ හෝ ප්‍රතිසාධනය කරන්න"
    else
        "Safely backup and restore all your merit posts with attached photographs included"

    val backupCardTitle = if (isSinhala) "සම්පූර්ණ උපස්ථයක් සාදන්න" else "Create Full Backup"
    val backupCardDesc = if (isSinhala)
        "සියලු පින් සටහන් සහ ඡායාරූප තනි සංයුක්ත ගොනුවක් ලෙස සුරකින්න හෝ වෙනත් තැනකට යවන්න"
    else
        "Package all posts and memory photos into a single portable backup file"

    val backupSaveButton = if (isSinhala) "උපස්ථ ගොනුව සුරකින්න" else "Save Backup (.bodhibackup)"
    val backupShareButton = if (isSinhala) "උපස්ථය බෙදාගන්න" else "Share Backup File"

    val restoreCardTitle = if (isSinhala) "උපස්ථයකින් ප්‍රතිසාධනය කරන්න" else "Restore from Backup"
    val restoreCardDesc = if (isSinhala)
        "කලින් සුරකින ලද උපස්ථ ගොනුවකින් (.bodhibackup හෝ .zip) පින්කම් සහ ඡායාරූප නැවත ලබාගන්න"
    else
        "Restore merits and attached photographs from a previously saved backup file"

    val restoreSelectButton = if (isSinhala) "උපස්ථ ගොනුව තෝරන්න" else "Select Backup File"

    val restoreDialogTitle = if (isSinhala) "පින්කම් ප්‍රතිසාධනය තහවුරු කිරීම" else "Restore Merits & Media"
    fun restoreFoundSummary(merits: Int, media: Int) = if (isSinhala)
        "උපස්ථයේ පින්කම් $merits ක් සහ ඡායාරූප $media ක් අඩංගුයි."
    else
        "Backup contains $merits merit posts and $media memory photos."

    val restoreModeQuestion = if (isSinhala) "ප්‍රතිසාධනය කළ යුතු ආකාරය තෝරන්න:" else "Choose restore mode:"
    val restoreModeMerge = if (isSinhala) "දැනට ඇති ඒවාට එකතු කරන්න (Merge)" else "Append to existing merits (Merge)"
    val restoreModeMergeDesc = if (isSinhala)
        "දැනට බෝධි වෘක්ෂයේ ඇති පින්කම් එලෙසම තබා නව පින්කම් එක් කරයි"
    else
        "Keeps current leaves intact and adds restored merits to free branches"

    val restoreModeReplace = if (isSinhala) "දැනට ඇති සියල්ල වෙනුවට ප්‍රතිස්ථාපනය (Replace All)" else "Replace all existing merits"
    val restoreModeReplaceDesc = if (isSinhala)
        "දැනට ඇති සියලු පින්කම් මකා දමා උපස්ථයේ ඇති තත්ත්වයටම පත් කරයි"
    else
        "Clears existing merits and restores the tree to exact backup state"

    val restoreConfirmAction = if (isSinhala) "දැන් ප්‍රතිසාධනය කරන්න" else "Restore Now"
    val backingUpMessage = if (isSinhala) "උපස්ථය සකසමින් පවතී..." else "Creating backup package..."
    val restoringMessage = if (isSinhala) "පින්කම් ප්‍රතිසාධනය වෙමින් පවතී..." else "Restoring posts and media..."

    fun backupSuccessMessage(merits: Int, media: Int) = if (isSinhala)
        "සාර්ථකයි! පින්කම් $merits ක් සහ ඡායාරූප $media ක් උපස්ථ කරන ලදී."
    else
        "Success! Backed up $merits posts and $media photos."

    fun restoreSuccessMessage(merits: Int, media: Int) = if (isSinhala)
        "සාර්ථකයි! පින්කම් $merits ක් සහ ඡායාරූප $media ක් ප්‍රතිසාධනය කරන ලදී."
    else
        "Success! Restored $merits posts and $media photos."

    val backupError = if (isSinhala) "උපස්ථය සෑදීම අසාර්ථක විය" else "Failed to create backup"
    val restoreError = if (isSinhala) "ප්‍රතිසාධනය අසාර්ථක විය" else "Failed to restore backup"
}

val LocalAppStrings = compositionLocalOf { AppStrings(AppLanguage.ENGLISH) }
