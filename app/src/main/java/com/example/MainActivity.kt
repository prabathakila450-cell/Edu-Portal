package com.example

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.BlueOnPrimaryContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeutralBorder
import com.example.ui.theme.NeutralBorderLight
import com.example.ui.theme.NeutralDark
import com.example.ui.theme.NeutralMedium
import com.example.ui.theme.SurfaceVariantLight

data class UserAccount(
  val id: String,
  val fullName: String,
  val usernameOrPhone: String,
  val password: String,
  var isApproved: Boolean = false,
  val registerDate: String = "2026-08-11",
  var slipImageUri: String? = null,
  var paymentStatus: String = "Pending Approval",
  var requestedGradePackage: String = "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)",
  var approvedGrades: List<String> = listOf("10", "11", "10 ශ්‍රේණිය", "11 ශ්‍රේණිය")
)

fun getApprovedGradesForPackage(pkg: String): List<String> {
  return when {
    pkg.contains("10 සහ 11") || pkg.contains("O/L") -> listOf("10", "11", "10 ශ්‍රේණිය", "11 ශ්‍රේණිය")
    pkg.contains("06") || pkg.contains("6") -> listOf("06", "6", "6 ශ්‍රේණිය")
    pkg.contains("07") || pkg.contains("7") -> listOf("07", "7", "7 ශ්‍රේණිය")
    pkg.contains("08") || pkg.contains("8") -> listOf("08", "8", "8 ශ්‍රේණිය")
    pkg.contains("09") || pkg.contains("9") -> listOf("09", "9", "9 ශ්‍රේණිය")
    pkg.contains("සියලු") || pkg.contains("All") -> listOf("06", "07", "08", "09", "10", "11", "6 ශ්‍රේණිය", "7 ශ්‍රේණිය", "8 ශ්‍රේණිය", "9 ශ්‍රේණිය", "10 ශ්‍රේණිය", "11 ශ්‍රේණිය")
    else -> listOf("10", "11", "10 ශ්‍රේණිය", "11 ශ්‍රේණිය")
  }
}

fun isUserApprovedForGrade(user: UserAccount?, grade: String, isAdmin: Boolean): Boolean {
  if (isAdmin) return true
  if (user == null || !user.isApproved) return false
  val cleanGrade = grade.replace("ශ්‍රේණිය", "").replace("Grade", "").replace("වසර", "").trim()
  val paddedGrade = if (cleanGrade.length == 1) "0$cleanGrade" else cleanGrade
  val singleDigit = if (cleanGrade.startsWith("0")) cleanGrade.substring(1) else cleanGrade

  return user.approvedGrades.any { approved ->
    val cleanApproved = approved.replace("ශ්‍රේණිය", "").replace("Grade", "").replace("වසර", "").trim()
    cleanApproved == cleanGrade || cleanApproved == paddedGrade || cleanApproved == singleDigit || approved.contains(cleanGrade)
  }
}

data class SubjectItem(
  val id: String,
  val name: String,
  val nameSinhala: String,
  val chaptersCount: Int,
  val color: Color,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class ShortNoteItem(
  val id: String,
  val subject: String,
  val title: String,
  val topicSinhala: String,
  val readTime: String,
  val isPopular: Boolean = false,
  val pdfUri: String? = null,
  val fileName: String? = null,
  val isPasswordProtected: Boolean = false,
  val password: String? = "1234"
)

data class QuestionPaperItem(
  val id: String,
  val subject: String,
  val titleSinhala: String,
  val year: String,
  val term: String,
  val marks: String,
  val pdfUri: String? = null,
  val fileName: String? = null,
  val isPasswordProtected: Boolean = false,
  val password: String? = "1234"
)

data class VideoLessonItem(
  val id: String,
  val subject: String,
  val titleSinhala: String,
  val duration: String,
  val tutorName: String,
  val viewsCount: String,
  val isHd: Boolean = true,
  val videoUri: String? = null
)

fun getFileNameFromUri(context: Context, uri: Uri): String {
  var fileName = "Document.pdf"
  try {
    if (uri.scheme == "content") {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
          fileName = cursor.getString(nameIndex)
        }
      }
    } else if (uri.scheme == "file") {
      fileName = uri.lastPathSegment ?: "Document.pdf"
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
  return fileName
}

fun openPdfFile(context: Context, pdfUriString: String?) {
  if (pdfUriString.isNullOrBlank()) {
    Toast.makeText(context, "පීඩීඑෆ් (PDF) ගොනුවක් අමුණා නොමැත", Toast.LENGTH_SHORT).show()
    return
  }
  try {
    val uri = Uri.parse(pdfUriString)
    val intent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(uri, "application/pdf")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, "PDF ගොනුව විවෘත කරන්න"))
  } catch (e: Exception) {
    Toast.makeText(context, "PDF ගොනුව විවෘත කිරීමට PDF Viewer යෙදුමක් සොයාගත නොහැකි විය: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
  }
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = false) {
        StudentPortalApp()
      }
    }
  }
}

@Composable
fun StudentPortalApp() {
  val context = LocalContext.current

  // User Authentication & Admin Approval State
  val registeredUsers = remember {
    mutableStateListOf(
      UserAccount("1", "අකිල ප්‍රබාත්", "0771234567", "1234", isApproved = true, paymentStatus = "Approved", requestedGradePackage = "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)", approvedGrades = listOf("10", "11", "10 ශ්‍රේණිය", "11 ශ්‍රේණිය")),
      UserAccount("2", "Akila Prabath", "prabathakila450@gmail.com", "1234", isApproved = true, paymentStatus = "Approved", requestedGradePackage = "06 සිට 11 දක්වා සියලුම ශ්‍රේණි (All Grades Mega Pack)", approvedGrades = listOf("06", "07", "08", "09", "10", "11", "6 ශ්‍රේණිය", "7 ශ්‍රේණිය", "8 ශ්‍රේණිය", "9 ශ්‍රේණිය", "10 ශ්‍රේණිය", "11 ශ්‍රේණිය")),
      UserAccount("3", "ශිෂ්‍ය සහය පරිශීලක", "0757302321", "1234", isApproved = true, paymentStatus = "Approved", requestedGradePackage = "06 සිට 11 දක්වා සියලුම ශ්‍රේණි (All Grades Mega Pack)", approvedGrades = listOf("06", "07", "08", "09", "10", "11")),
      UserAccount("4", "කසුන් පෙරේරා", "0719876543", "1234", isApproved = true, paymentStatus = "Approved", requestedGradePackage = "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)", approvedGrades = listOf("10", "11")),
      UserAccount("5", "නිරෝෂා කුමාරි", "0755554433", "1234", isApproved = true, paymentStatus = "Approved", requestedGradePackage = "08 ශ්‍රේණිය (Grade 8 Single Pack)", approvedGrades = listOf("08", "8", "8 ශ්‍රේණිය"))
    )
  }
  var loggedInUser by remember { mutableStateOf<UserAccount?>(null) }
  var isAdminAuthenticated by remember { mutableStateOf(false) }
  var showAuthRequiredDialog by remember { mutableStateOf(false) }
  var showPaymentApprovalDialog by remember { mutableStateOf(false) }
  var previewReceiptImageUrl by remember { mutableStateOf<String?>(null) }
  var showGradeNotApprovedDialog by remember { mutableStateOf(false) }
  var gradeNotApprovedTarget by remember { mutableStateOf("11") }
  var preselectedGradeForApproval by remember { mutableStateOf("10 සහ 11 ශ්‍රේණි (O/L Combo Pack)") }

  // Navigation Screen State: "HOME" (Grades list), "SUBJECTS" (Subject list for selected grade), "CONTENT" (Tabs for selected subject)
  var currentScreen by remember { mutableStateOf("HOME") }
  var selectedGrade by remember { mutableStateOf("11") } // Options: "11", "10", "09", "08", "07", "06"
  var selectedSubjectItem by remember { mutableStateOf<SubjectItem?>(null) }
  var selectedContentTab by remember { mutableStateOf(0) } // 0: කෙටි සටහන් (PDFs), 1: ප්‍රශ්න පත්‍ර (PDFs), 2: ස්වයං පුහුණු (Quizzes), 3: Audio Notes, 4: AI Assistant
  var selectedSectionIndex by remember { mutableStateOf(0) } // 0: සියල්ල, 1: විෂයන්, 2: කෙටි සටහන්, 3: ප්‍රශ්න පත්‍ර
  var selectedTabNav by remember { mutableStateOf(0) }
  var activeDetailTitle by remember { mutableStateOf<String?>(null) }
  var isVideoModal by remember { mutableStateOf(false) }
  var selectedItemPdfUri by remember { mutableStateOf<String?>(null) }

  val isApprovedUser = isAdminAuthenticated || (loggedInUser != null && loggedInUser?.isApproved == true)

  // 1-Day Free Trial for Feature Hub (Smart Study Hub) from Install Timestamp
  val sharedPrefs = remember { context.getSharedPreferences("app_trial_prefs", Context.MODE_PRIVATE) }
  var installTimestamp by remember {
    val saved = sharedPrefs.getLong("install_timestamp", 0L)
    if (saved == 0L) {
      val current = System.currentTimeMillis()
      sharedPrefs.edit().putLong("install_timestamp", current).apply()
      mutableStateOf(current)
    } else {
      mutableStateOf(saved)
    }
  }

  val oneDayMillis = 24 * 60 * 60 * 1000L
  val timeElapsed = System.currentTimeMillis() - installTimestamp
  val isFeatureTrialActive = timeElapsed < oneDayMillis
  val remainingTrialHours = (((oneDayMillis - timeElapsed).coerceAtLeast(0L)) / (60 * 60 * 1000L)).toInt().coerceIn(0, 24)

  fun requireFeatureAccess(onSuccess: () -> Unit) {
    if (isApprovedUser || isFeatureTrialActive) {
      onSuccess()
    } else {
      Toast.makeText(
        context,
        "🔒 ඇප් එක ඉන්ස්ටෝල් කිරීමෙන් පසු දින 1ක නොමිලේ කාලය අවසන් වී ඇත. විශේෂාංග කලාපය පරිශීලනය කිරීමට කරුණාකර ඇඩ්මින් අනුමැතිය (Admin Approval) ලබාගන්න.",
        Toast.LENGTH_LONG
      ).show()
      if (loggedInUser == null) {
        showAuthRequiredDialog = true
      } else {
        showPaymentApprovalDialog = true
      }
    }
  }

  // Password Protection & PDF iframe/modal viewer state
  var showPasswordDialog by remember { mutableStateOf(false) }
  var pendingProtectedPdfTitle by remember { mutableStateOf("") }
  var pendingProtectedPdfUrl by remember { mutableStateOf("") }
  var pendingProtectedPdfPassword by remember { mutableStateOf("1234") }
  var passwordInputText by remember { mutableStateOf("") }
  var showPasswordError by remember { mutableStateOf(false) }

  var showIframePdfModal by remember { mutableStateOf(false) }
  var iframeModalPdfTitle by remember { mutableStateOf("") }
  var iframeModalPdfUrl by remember { mutableStateOf("") }

  // DRM & Security Alert State (Anti-Copy, Anti-Download, Screenshot & Screen Record Alert without blacking screen)
  var showDrmWarningDialog by remember { mutableStateOf(false) }
  var drmWarningReason by remember { mutableStateOf("තිර ඡායාරූප (Screenshot) හෝ තිර පටිගත කිරීම් (Screen Recording)") }

  // AI Practice Quizzes & Flashcards System State
  var activeQuizSet by remember { mutableStateOf<QuizSet?>(null) }
  var showFlashcardsDialog by remember { mutableStateOf(false) }
  var showPdfQuizBottomSheet by remember { mutableStateOf(false) }

  // AI Audio Podcasts & Deep Index State
  var activeAudioState by remember { mutableStateOf<ActiveAudioState?>(null) }

  fun performProtectedAction(action: () -> Unit) {
    if (isApprovedUser) {
      action()
    } else {
      showPaymentApprovalDialog = true
    }
  }

  fun playChapterAudio(chapter: ChapterItem, subjectName: String, grade: String, pdfUrl: String, speed: Float = 1.0f) {
    performProtectedAction {
      activeAudioState = ActiveAudioState(
        chapter = chapter,
        subject = subjectName,
        grade = grade,
        pdfUrl = pdfUrl,
        isPlaying = true,
        speed = speed
      )
      Toast.makeText(context, "🎧 ධාවනය වෙමින්: ${chapter.title}", Toast.LENGTH_SHORT).show()
    }
  }

  fun pauseAudio() {
    activeAudioState = activeAudioState?.copy(isPlaying = false)
  }

  fun resumeAudio() {
    activeAudioState = activeAudioState?.copy(isPlaying = true)
  }

  fun toggleAudioSpeed() {
    activeAudioState?.let { current ->
      val newSpeed = if (current.speed == 1.0f) 1.5f else 1.0f
      activeAudioState = current.copy(speed = newSpeed)
      Toast.makeText(context, "වේගය: ${newSpeed}x", Toast.LENGTH_SHORT).show()
    }
  }

  fun openPdfAtPage(pdfUrl: String, title: String, page: Int) {
    performProtectedAction {
      iframeModalPdfTitle = "$title (පිටුව $page)"
      val targetUrl = if (pdfUrl.contains("#page=")) pdfUrl else "$pdfUrl#page=$page"
      iframeModalPdfUrl = targetUrl
      showIframePdfModal = true
    }
  }

  fun handlePdfAccess(
    title: String,
    pdfUri: String?,
    isPasswordProtected: Boolean = false,
    requiredPassword: String? = "1234",
    targetGrade: String = selectedGrade
  ) {
    // 1. Check if the user is approved (isApproved == true). If not, show Unlimited Access Payment Dialog.
    if (!isApprovedUser) {
      preselectedGradeForApproval = when (targetGrade) {
        "10", "11" -> "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)"
        "06" -> "06 ශ්‍රේණිය (Grade 6 Single Pack)"
        "07" -> "07 ශ්‍රේණිය (Grade 7 Single Pack)"
        "08" -> "08 ශ්‍රේණිය (Grade 8 Single Pack)"
        "09" -> "09 ශ්‍රේණිය (Grade 9 Single Pack)"
        else -> "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)"
      }
      showPaymentApprovalDialog = true
      return
    }

    // 2. Check if user is approved for this specific Grade
    if (!isUserApprovedForGrade(loggedInUser, targetGrade, isAdminAuthenticated)) {
      gradeNotApprovedTarget = targetGrade
      showGradeNotApprovedDialog = true
      return
    }

    // 3. If approved, ask for the password if required
    if (isPasswordProtected) {
      pendingProtectedPdfTitle = title
      pendingProtectedPdfUrl = pdfUri ?: "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview"
      pendingProtectedPdfPassword = requiredPassword ?: "1234"
      passwordInputText = ""
      showPasswordError = false
      showPasswordDialog = true
    } else {
      // 4. Open in iframe/modal or external view
      if (pdfUri != null && pdfUri.startsWith("http")) {
        iframeModalPdfTitle = title
        iframeModalPdfUrl = pdfUri
        showIframePdfModal = true
      } else {
        openPdfFile(context, pdfUri)
      }
    }
  }

  // PDF File Picker Launcher State
  var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
  var selectedPdfFileName by remember { mutableStateOf<String?>(null) }

  val pdfPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedPdfUri = uri
      val name = getFileNameFromUri(context, uri)
      selectedPdfFileName = name
      Toast.makeText(context, "PDF ගොනුව අමුණන ලදී: $name", Toast.LENGTH_SHORT).show()
    }
  }

  // Live Cloud Repository State (Supports real-time dynamic addition/deletion without APK re-downloads)
  val liveSubjectsMap = remember {
    mutableStateMapOf<String, SnapshotStateList<SubjectItem>>().apply {
      listOf("06", "07", "08", "09", "10", "11").forEach { g ->
        put(g, mutableStateListOf<SubjectItem>().apply { addAll(getSubjectsForGrade(g)) })
      }
    }
  }

  val liveNotesMap = remember {
    mutableStateMapOf<String, SnapshotStateList<ShortNoteItem>>().apply {
      listOf("06", "07", "08", "09", "10", "11").forEach { g ->
        put(g, mutableStateListOf<ShortNoteItem>().apply { addAll(getNotesForGrade(g)) })
      }
    }
  }

  val livePapersMap = remember {
    mutableStateMapOf<String, SnapshotStateList<QuestionPaperItem>>().apply {
      listOf("06", "07", "08", "09", "10", "11").forEach { g ->
        put(g, mutableStateListOf<QuestionPaperItem>().apply { addAll(getPapersForGrade(g)) })
      }
    }
  }

  val liveVideosMap = remember {
    mutableStateMapOf<String, SnapshotStateList<VideoLessonItem>>().apply {
      listOf("06", "07", "08", "09", "10", "11").forEach { g ->
        put(g, mutableStateListOf<VideoLessonItem>().apply { addAll(getVideosForGrade(g)) })
      }
    }
  }

  // Feature 5 Bookmarks State
  val savedBookmarks = remember {
    mutableStateListOf(
      BookmarkItem(
        id = "bm_1",
        title = "10 සහ 11 ශ්‍රේණිය - තොරතුරු තාක්ෂණය කෙටි සටහන්",
        subject = "තොරතුරු තාක්ෂණය",
        grade = "11",
        type = "NOTE",
        pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
      ),
      BookmarkItem(
        id = "bm_2",
        title = "ගණිතය (ජ්‍යාමිතිය) කෙටි සටහන්",
        subject = "ගණිතය",
        grade = "11",
        type = "NOTE",
        pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
      ),
      BookmarkItem(
        id = "bm_3",
        title = "10 සහ 11 ශ්‍රේණිය ජීව විද්‍යාව විභාග ප්‍රශ්න",
        subject = "විද්‍යාව",
        grade = "11",
        type = "PAPER",
        pdfUri = "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview"
      ),
      BookmarkItem(
        id = "bm_4",
        title = "06-11 ශ්‍රේණි - ඉතිහාසය සිතියම් ලකුණු කිරීම",
        subject = "ඉතිහාසය",
        grade = "10",
        type = "NOTE",
        pdfUri = "https://drive.google.com/file/d/1BVguuBjT1_iQVO296Zn4Dek2AOahBFsp/preview"
      )
    )
  }

  // Admin / Add Content Modal State
  var showAddContentDialog by remember { mutableStateOf(false) }
  var contentTypeToAdd by remember { mutableStateOf("NOTE") } // "SUBJECT", "NOTE", "PAPER", "VIDEO"
  var inputTitle by remember { mutableStateOf("") }
  var inputSubject by remember { mutableStateOf("විද්‍යාව") }
  var inputExtraInfo by remember { mutableStateOf("") }

  val gradesList = listOf("06", "07", "08", "09", "10", "11")

  // Dynamic live lists for current grade
  val currentSubjects = liveSubjectsMap[selectedGrade] ?: remember { mutableStateListOf<SubjectItem>() }
  val currentNotes = liveNotesMap[selectedGrade] ?: remember { mutableStateListOf<ShortNoteItem>() }
  val currentPapers = livePapersMap[selectedGrade] ?: remember { mutableStateListOf<QuestionPaperItem>() }
  val currentVideos = liveVideosMap[selectedGrade] ?: remember { mutableStateListOf<VideoLessonItem>() }

  // Graceful Back Navigation Handler: Intercepts device back button and gestures to prevent sudden exits
  BackHandler(enabled = true) {
    when {
      showIframePdfModal -> {
        showIframePdfModal = false
      }
      showPasswordDialog -> {
        showPasswordDialog = false
        showPasswordError = false
      }
      showAuthRequiredDialog -> {
        showAuthRequiredDialog = false
      }
      showPaymentApprovalDialog -> {
        showPaymentApprovalDialog = false
      }
      showAddContentDialog -> {
        showAddContentDialog = false
        selectedPdfUri = null
        selectedPdfFileName = null
      }
      previewReceiptImageUrl != null -> {
        previewReceiptImageUrl = null
      }
      activeDetailTitle != null -> {
        activeDetailTitle = null
        selectedItemPdfUri = null
      }
      activeQuizSet != null -> {
        activeQuizSet = null
      }
      showFlashcardsDialog -> {
        showFlashcardsDialog = false
      }
      showPdfQuizBottomSheet -> {
        showPdfQuizBottomSheet = false
      }
      currentScreen == "SYLLABUS_HUB" -> {
        currentScreen = if (selectedSubjectItem != null) "CONTENT" else "HOME"
      }
      currentScreen == "CONTENT" -> {
        currentScreen = "SUBJECTS"
      }
      currentScreen == "SUBJECTS" -> {
        currentScreen = "HOME"
      }
      currentScreen in listOf("ANALYTICS", "STRUCTURED_ESSAY", "FORMULA_HANDBOOK", "VOICE_QUIZ", "ENGLISH_BUILDER", "COUNTDOWN_PLANNER", "FLASHCARDS_HUB", "STREAK_BADGES", "SPOKEN_ENGLISH_VOICE", "BOOKMARKS_HUB") -> {
        currentScreen = "HOME"
        selectedTabNav = 0
      }
      else -> {
        (context as? android.app.Activity)?.finish()
      }
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("main_scaffold"),
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      PortalBottomNavigation(
        selectedTab = selectedTabNav,
        onTabSelected = { index ->
          when (index) {
            0 -> {
              selectedTabNav = 0
              currentScreen = "HOME"
            }
            1 -> {
              requireFeatureAccess {
                selectedTabNav = 1
                currentScreen = "ANALYTICS"
              }
            }
            2 -> {
              requireFeatureAccess {
                selectedTabNav = 2
                currentScreen = "STRUCTURED_ESSAY"
              }
            }
            3 -> {
              requireFeatureAccess {
                selectedTabNav = 3
                currentScreen = "FORMULA_HANDBOOK"
              }
            }
            4 -> {
              requireFeatureAccess {
                Toast.makeText(context, "AI Tutor සූදානම්! Grade $selectedGrade සඳහා ඕනෑම ගැටලුවක් විමසන්න.", Toast.LENGTH_SHORT).show()
              }
            }
          }
        }
      )
    },
    floatingActionButton = {
      Column(horizontalAlignment = Alignment.End) {
        // Floating Action Button for Live Content Add
        FloatingActionButton(
          onClick = { showAddContentDialog = true },
          containerColor = Color(0xFF137333),
          contentColor = Color.White,
          shape = CircleShape,
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
          modifier = Modifier
            .testTag("add_content_fab")
            .padding(bottom = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Content Live",
            modifier = Modifier.size(24.dp)
          )
        }

        FloatingActionButton(
          onClick = {
            Toast.makeText(context, "AI Tutor සූදානම්! Grade $selectedGrade සඳහා උපකාර ලබාගන්න.", Toast.LENGTH_SHORT).show()
          },
          containerColor = BluePrimary,
          contentColor = Color.White,
          shape = RoundedCornerShape(16.dp),
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
          modifier = Modifier
            .testTag("ai_fab")
            .padding(bottom = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Assistant",
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Background Subtle Text Accent
      WatermarkBackground()

      if (currentScreen == "SYLLABUS_HUB") {
        // FEATURE: SYLLABUS DETECTION & GOOGLE DRIVE CONTENT HUB SCREEN (06 - 11 ශ්‍රේණි)
        SyllabusDetectionAndContentScreen(
          initialGrade = selectedGrade,
          initialSubject = selectedSubjectItem?.nameSinhala ?: "විද්‍යාව",
          isApproved = isApprovedUser,
          onRequireApproval = {
            if (loggedInUser == null) {
              showAuthRequiredDialog = true
            } else {
              showPaymentApprovalDialog = true
            }
          },
          onBack = {
            currentScreen = if (selectedSubjectItem != null) "CONTENT" else "HOME"
          },
          onOpenGoogleDrivePdfModal = { url, title ->
            if (!isApprovedUser) {
              Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් සටහන් හා ප්‍රශ්න පත්‍ර පරිශීලනය කිරීමට ඇඩ්මින් අනුමැතිය (Admin Approval) අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
              if (loggedInUser == null) {
                showAuthRequiredDialog = true
              } else {
                showPaymentApprovalDialog = true
              }
            } else {
              iframeModalPdfTitle = title
              iframeModalPdfUrl = formatToGoogleDriveEmbedUrl(url)
              showIframePdfModal = true
            }
          }
        )
      } else if (currentScreen == "ANALYTICS") {
        // FEATURE 2: DETAILED ANALYTICS SCREEN
        StudyAnalyticsScreen(
          grade = selectedGrade,
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          },
          onOpenNotesForSubject = { subjectName ->
            val matched = currentSubjects.firstOrNull { it.nameSinhala.contains(subjectName, ignoreCase = true) }
            if (matched != null) {
              selectedSubjectItem = matched
              selectedContentTab = 0
              currentScreen = "CONTENT"
            } else {
              currentScreen = "HOME"
              selectedTabNav = 0
            }
          }
        )
      } else if (currentScreen == "STRUCTURED_ESSAY") {
        // FEATURE 3: STRUCTURED & ESSAY PRACTICE HUB
        StructuredEssayHubScreen(
          grade = selectedGrade,
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "FORMULA_HANDBOOK") {
        // FEATURE 5: QUICK REFERENCE FORMULA & TIMELINE HUB
        FormulaAndTimelineHubScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "VOICE_QUIZ") {
        // FEATURE 3 (VOICE): AI VOICE QUIZ & ORAL SIMULATION SCREEN
        VoiceQuizScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "ENGLISH_BUILDER") {
        // ENGLISH MASTER CLASS SCREEN (Vertical Accordion Sub-Sections with Background Photos & Voice Mic AI)
        EnglishMasterClassScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          },
          onOpenGoogleDrivePdfModal = { url, title ->
            if (!isApprovedUser) {
              Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් සටහන් හා ප්‍රශ්න පත්‍ර පරිශීලනය කිරීමට ඇඩ්මින් අනුමැතිය (Admin Approval) අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
              if (loggedInUser == null) {
                showAuthRequiredDialog = true
              } else {
                showPaymentApprovalDialog = true
              }
            } else {
              iframeModalPdfTitle = title
              iframeModalPdfUrl = formatToGoogleDriveEmbedUrl(url)
              showIframePdfModal = true
            }
          }
        )
      } else if (currentScreen == "COUNTDOWN_PLANNER") {
        // FEATURE 1 (NEW): O/L EXAM COUNTDOWN TIMER & DAILY STUDY PLANNER
        CountdownAndPlannerScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          },
          onOpenPdfModal = { url, title ->
            if (!isApprovedUser) {
              Toast.makeText(context, "🔒 ඇඩ්මින් අනුමැතිය (Admin Approval) අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
            } else {
              iframeModalPdfTitle = title
              iframeModalPdfUrl = formatToGoogleDriveEmbedUrl(url)
              showIframePdfModal = true
            }
          }
        )
      } else if (currentScreen == "FLASHCARDS_HUB") {
        // FEATURE 2 (NEW): INTERACTIVE FLASHCARDS SCREEN
        InteractiveFlashcardsScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "STREAK_BADGES") {
        // FEATURE 3 (NEW): DAILY STUDY STREAK & BADGES SCREEN
        StudyStreakAndBadgesScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "SPOKEN_ENGLISH_VOICE") {
        // FEATURE 4 (NEW): SPOKEN ENGLISH & VOICE MIC RECOGNITION SCREEN
        SpokenEnglishVoiceRecognitionScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "BOOKMARKS_HUB") {
        // FEATURE 5 (NEW): BOOKMARK & SAVED NOTES SCREEN
        BookmarksAndFavoritesScreen(
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          },
          onOpenPdf = { url, title ->
            if (!isApprovedUser) {
              Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් පරිශීලනයට ඇඩ්මින් අනුමැතිය (Admin Approval) අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
              if (loggedInUser == null) {
                showAuthRequiredDialog = true
              } else {
                showPaymentApprovalDialog = true
              }
            } else {
              iframeModalPdfTitle = title
              iframeModalPdfUrl = formatToGoogleDriveEmbedUrl(url)
              showIframePdfModal = true
            }
          },
          savedBookmarks = savedBookmarks
        )
      } else if (currentScreen == "MOCK_EXAM") {
        // FEATURE: LIVE MOCK EXAM HALL & OMR SIMULATOR
        LiveMockExamHallScreen(
          grade = selectedGrade,
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else if (currentScreen == "SPOT_TOPICS") {
        // FEATURE: EXAM SPOT TOPICS & HIGH-PROBABILITY PREDICTOR
        ExamSpotTopicsScreen(
          grade = selectedGrade,
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          },
          onOpenPdfModal = { url, title ->
            if (!isApprovedUser) {
              Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් පරිශීලනයට ඇඩ්මින් අනුමැතිය (Admin Approval) අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
              if (loggedInUser == null) {
                showAuthRequiredDialog = true
              } else {
                showPaymentApprovalDialog = true
              }
            } else {
              iframeModalPdfTitle = title
              iframeModalPdfUrl = formatToGoogleDriveEmbedUrl(url)
              showIframePdfModal = true
            }
          }
        )
      } else if (currentScreen == "LIVE_DAILY_QUIZ") {
        // FEATURE: 7:00 PM AUTOMATED DAILY LIVE QUIZ CONTEST & LEADERBOARDS
        DailyLiveQuizHubScreen(
          initialGrade = selectedGrade,
          userName = loggedInUser?.fullName ?: "ශිෂ්‍යයා",
          userApprovedGrades = loggedInUser?.approvedGrades ?: emptyList(),
          isAdmin = isAdminAuthenticated,
          onBack = {
            currentScreen = "HOME"
            selectedTabNav = 0
          }
        )
      } else {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
        ) {
          // Top Header
          TopHeaderSection(
            userName = when {
              isAdminAuthenticated -> "👑 ඇඩ්මින් පැනලය (Admin)"
              loggedInUser != null -> loggedInUser?.fullName ?: "ශිෂ්‍ය ගිණුම"
              else -> "ආයුබෝවන්, ශිෂ්‍යයා (Guest)"
            },
            userStream = when {
              isAdminAuthenticated -> "ඇඩ්මින් පරිපාලනය සක්‍රීයයි"
              loggedInUser?.isApproved == true -> "$selectedGrade වසර • අනුමත ශිෂ්‍ය ගිණුම"
              loggedInUser != null -> "$selectedGrade වසර • ඇඩ්මින් අනුමැතිය ලැබෙමින්..."
              else -> "$selectedGrade වසර • ශිෂ්‍ය ගිණුම"
            },
            isApproved = isApprovedUser,
            onAuthClick = { showAuthRequiredDialog = true },
            onNotificationClick = {
              Toast.makeText(context, "නව දැනුම්දීම් නොමැත", Toast.LENGTH_SHORT).show()
            }
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Subscription Banner
          PaddingValues(horizontal = 16.dp).let {
            Box(modifier = Modifier.padding(it)) {
              SecuritySubscriptionBanner(
                isApproved = isApprovedUser,
                isFeatureTrialActive = isFeatureTrialActive,
                remainingTrialHours = remainingTrialHours,
                onClick = {
                  if (!isApprovedUser) {
                    showPaymentApprovalDialog = true
                  } else {
                    showAuthRequiredDialog = true
                  }
                }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // STEP 1: HOME SCREEN (Grades Grid/List in Descending Order: 11 to 06)
          if (currentScreen == "HOME") {
            GradesHomeScreen(
              onGradeSelected = { grade ->
                selectedGrade = grade
                currentScreen = "SUBJECTS"
              },
              onOpenAddModal = {
                contentTypeToAdd = "SUBJECT"
                showAddContentDialog = true
              },
              onOpenAnalytics = {
                requireFeatureAccess {
                  currentScreen = "ANALYTICS"
                  selectedTabNav = 1
                }
              },
              onOpenStructuredEssay = {
                requireFeatureAccess {
                  currentScreen = "STRUCTURED_ESSAY"
                  selectedTabNav = 2
                }
              },
              onOpenFormulaHandbook = {
                requireFeatureAccess {
                  currentScreen = "FORMULA_HANDBOOK"
                  selectedTabNav = 3
                }
              },
              onOpenVoiceQuiz = {
                requireFeatureAccess {
                  currentScreen = "VOICE_QUIZ"
                }
              },
              onOpenEnglishBuilder = {
                requireFeatureAccess {
                  currentScreen = "ENGLISH_BUILDER"
                }
              },
              onOpenSyllabusHub = {
                requireFeatureAccess {
                  currentScreen = "SYLLABUS_HUB"
                }
              },
              onOpenCountdownPlanner = {
                requireFeatureAccess {
                  currentScreen = "COUNTDOWN_PLANNER"
                }
              },
              onOpenFlashcards = {
                requireFeatureAccess {
                  currentScreen = "FLASHCARDS_HUB"
                }
              },
              onOpenStudyStreak = {
                requireFeatureAccess {
                  currentScreen = "STREAK_BADGES"
                }
              },
              onOpenSpokenEnglishVoice = {
                requireFeatureAccess {
                  currentScreen = "SPOKEN_ENGLISH_VOICE"
                }
              },
              onOpenBookmarks = {
                requireFeatureAccess {
                  currentScreen = "BOOKMARKS_HUB"
                }
              },
              onOpenMockExam = {
                requireFeatureAccess {
                  currentScreen = "MOCK_EXAM"
                }
              },
              onOpenSpotTopics = {
                requireFeatureAccess {
                  currentScreen = "SPOT_TOPICS"
                }
              },
              onOpenLiveDailyQuiz = {
                requireFeatureAccess {
                  currentScreen = "LIVE_DAILY_QUIZ"
                }
              },
              isFeatureTrialActive = isFeatureTrialActive,
              isApproved = isApprovedUser,
              remainingTrialHours = remainingTrialHours
            )
          }

          // STEP 2: SUBJECTS SCREEN (List of Subjects for Selected Grade with Background Photos)
          else if (currentScreen == "SUBJECTS") {
            GradeSubjectsScreen(
              grade = selectedGrade,
              subjectsList = currentSubjects,
              onSubjectSelected = { subject ->
                selectedSubjectItem = subject
                currentScreen = "CONTENT"
              },
              onBackToGrades = {
                currentScreen = "HOME"
              },
              onAddSubject = {
                contentTypeToAdd = "SUBJECT"
                showAddContentDialog = true
              },
              onDeleteSubject = { subject ->
                currentSubjects.remove(subject)
                Toast.makeText(context, "${subject.nameSinhala} විෂය ඉවත් විය", Toast.LENGTH_SHORT).show()
              }
            )
          }

          // STEP 3: CONTENT SCREEN (5-Section Subject Hub: Short Notes | Papers | Quizzes | Audio Podcasts | AI Smart Assistant)
          else if (currentScreen == "CONTENT" && selectedSubjectItem != null) {
            SubjectContentScreen(
              grade = selectedGrade,
              subject = selectedSubjectItem!!,
              selectedTab = selectedContentTab,
              onTabSelected = { tabIndex ->
                selectedContentTab = tabIndex
              },
              notesList = currentNotes,
              papersList = currentPapers,
              videosList = currentVideos,
              onPdfClick = { title, pdfUri, isPasswordProtected, password ->
                handlePdfAccess(title, pdfUri, isPasswordProtected, password)
              },
              onDeleteNote = { note ->
                currentNotes.remove(note)
                Toast.makeText(context, "කෙටි සටහන / PDF ඉවත් විය", Toast.LENGTH_SHORT).show()
              },
              onDeletePaper = { paper ->
                currentPapers.remove(paper)
                Toast.makeText(context, "ප්‍රශ්න පත්‍රය ඉවත් විය", Toast.LENGTH_SHORT).show()
              },
              onDeleteVideo = { video ->
                currentVideos.remove(video)
                Toast.makeText(context, "වීඩියෝ පාඩම ඉවත් විය", Toast.LENGTH_SHORT).show()
              },
              onVideoClick = { video ->
                performProtectedAction {
                  isVideoModal = true
                  activeDetailTitle = "වීඩියෝ පාඩම: ${video.titleSinhala}"
                  selectedItemPdfUri = null
                }
              },
              onBackToSubjects = {
                currentScreen = "SUBJECTS"
              },
              onAddContent = { type ->
                contentTypeToAdd = type
                inputSubject = selectedSubjectItem?.nameSinhala ?: "විද්‍යාව"
                showAddContentDialog = true
              },
              onStartQuizSet = { set ->
                performProtectedAction {
                  activeQuizSet = set
                }
              },
              onOpenFlashcards = {
                performProtectedAction {
                  showFlashcardsDialog = true
                }
              },
              activeAudio = activeAudioState,
              onPlayAudio = { chapter, speed ->
                val noteMatch = currentNotes.firstOrNull { it.subject.contains(selectedSubjectItem?.nameSinhala ?: "", ignoreCase = true) || it.topicSinhala.contains(selectedSubjectItem?.nameSinhala ?: "", ignoreCase = true) }
                val noteUrl = noteMatch?.pdfUri ?: "https://drive.google.com/file/d/1TU2t7cxxTohimis_VsIPzswy0CW7p2v8/preview"
                playChapterAudio(chapter, selectedSubjectItem?.nameSinhala ?: "විද්‍යාව", selectedGrade, noteUrl, speed)
              },
              onPauseAudio = { pauseAudio() },
              onResumeAudio = { resumeAudio() },
              onOpenPdfAtPage = { url, title, page -> openPdfAtPage(url, title, page) },
              onOpenAnalytics = {
                currentScreen = "ANALYTICS"
                selectedTabNav = 1
              },
              onOpenStructuredEssay = {
                currentScreen = "STRUCTURED_ESSAY"
                selectedTabNav = 2
              },
              onOpenFormulaHandbook = {
                currentScreen = "FORMULA_HANDBOOK"
                selectedTabNav = 3
              },
              onOpenVoiceQuiz = {
                currentScreen = "VOICE_QUIZ"
              },
              onOpenEnglishBuilder = {
                currentScreen = "ENGLISH_BUILDER"
              },
              onOpenSyllabusHub = {
                currentScreen = "SYLLABUS_HUB"
              }
            )
          }
        }
      }

      // Add Content Live Modal Dialog
      if (showAddContentDialog) {
        AlertDialog(
          onDismissRequest = {
            showAddContentDialog = false
            selectedPdfUri = null
            selectedPdfFileName = null
          },
          title = {
            Text(
              text = "නව තොරතුරු / PDF එකතු කිරීම (Add Content)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = NeutralDark
            )
          },
          text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
              Text(
                text = "ඔබගේ PDF ගොනු, කෙටි සටහන්, වීඩියෝ හා ප්‍රශ්න පත්‍ර ඇප් එකට එකතු කරන්න. එකතු කරන සියලුම අන්තර්ගතයන් එසැනින්ම දර්ශනය වේ.",
                style = MaterialTheme.typography.bodySmall,
                color = NeutralMedium
              )

              Spacer(modifier = Modifier.height(12.dp))

              Text("තෝරන්න (Select Type):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                listOf("SUBJECT" to "විෂය", "NOTE" to "කෙටි සටහන්", "PAPER" to "ප්‍රශ්න පත්‍ර").forEach { (type, label) ->
                  val isSelected = contentTypeToAdd == type
                  Surface(
                    onClick = { contentTypeToAdd = type },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) BluePrimary else SurfaceVariantLight,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                      Text(
                        text = label,
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else NeutralDark,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }
              }

              if (contentTypeToAdd == "NOTE" || contentTypeToAdd == "PAPER") {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Color(0xFFFFEBEE),
                  border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { pdfPickerLauncher.launch("application/pdf") }
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.AttachFile,
                      contentDescription = "Attach PDF",
                      tint = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = if (selectedPdfFileName != null) "තෝරාගත් PDF: $selectedPdfFileName" else "📄 ඔබගේ PDF ගොනුව තෝරන්න (Select PDF)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFB71C1C)
                      )
                      Text(
                        text = if (selectedPdfFileName != null) "PDF ගොනුව අමුණා ඇත. වෙනස් කිරීමට තට්ටු කරන්න." else "ෆෝන් එකේ ඇති PDF ගොනුවක් තෝරා ගැනීමට මෙතැන තට්ටු කරන්න.",
                        fontSize = 10.sp,
                        color = Color(0xFFD32F2F)
                      )
                    }
                    if (selectedPdfFileName != null) {
                      Surface(
                        onClick = {
                          selectedPdfUri = null
                          selectedPdfFileName = null
                        },
                        shape = CircleShape,
                        color = Color.White
                      ) {
                        Icon(
                          imageVector = Icons.Default.Delete,
                          contentDescription = "Clear PDF",
                          tint = Color.Red,
                          modifier = Modifier.padding(4.dp).size(16.dp)
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              androidx.compose.material3.OutlinedTextField(
                value = inputTitle,
                onValueChange = { inputTitle = it },
                label = { Text("මාතෘකාව / නම (Title in Sinhala)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text("අදාළ විෂය (Target Subject):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NeutralDark)
              Spacer(modifier = Modifier.height(4.dp))
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                val availableSubList = currentSubjects.map { it.nameSinhala }
                items(availableSubList) { subName ->
                  val isSelected = inputSubject == subName
                  Surface(
                    onClick = { inputSubject = subName },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) BluePrimary else SurfaceVariantLight,
                    border = BorderStroke(1.dp, if (isSelected) BluePrimary else NeutralBorderLight)
                  ) {
                    Text(
                      text = subName,
                      fontSize = 11.sp,
                      color = if (isSelected) Color.White else NeutralDark,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              androidx.compose.material3.OutlinedTextField(
                value = inputSubject,
                onValueChange = { inputSubject = it },
                label = { Text("විෂය නම / කේතය") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
              )

              Spacer(modifier = Modifier.height(8.dp))

              androidx.compose.material3.OutlinedTextField(
                value = inputExtraInfo,
                onValueChange = { inputExtraInfo = it },
                label = {
                  Text(
                    when (contentTypeToAdd) {
                      "SUBJECT" -> "පාඩම් ගණන (උදා: පාඩම් මාලා 10)"
                      "NOTE" -> "කියවීමේ කාලය (උදා: මිඩිටු 5)"
                      "PAPER" -> "වාරය / වර්ෂය (උදා: 2025 1 වන වාරය)"
                      else -> "ගුරු නම / ධාවන කාලය (උදා: ගුරු සංජය • මිඩිටු 40)"
                    }
                  )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
              )
            }
          },
          confirmButton = {
            Button(
              onClick = {
                if (inputTitle.isNotBlank()) {
                  val newId = System.currentTimeMillis().toString()
                  val gradeTarget = selectedGrade
                  when (contentTypeToAdd) {
                    "SUBJECT" -> {
                      liveSubjectsMap[gradeTarget]?.add(
                        0,
                        SubjectItem(
                          id = newId,
                          name = inputSubject.ifBlank { "SUBJECT" }.uppercase(),
                          nameSinhala = inputTitle,
                          chaptersCount = inputExtraInfo.toIntOrNull() ?: 8,
                          color = BluePrimary,
                          icon = Icons.Default.MenuBook
                        )
                      )
                    }
                    "NOTE" -> {
                      liveNotesMap[gradeTarget]?.add(
                        0,
                        ShortNoteItem(
                          id = newId,
                          subject = inputSubject.ifBlank { "විද්‍යාව" },
                          title = "$gradeTarget වසර - $inputTitle",
                          topicSinhala = inputTitle,
                          readTime = if (selectedPdfFileName != null) "PDF • $selectedPdfFileName" else inputExtraInfo.ifBlank { "මිඩිටු 5" },
                          isPopular = true,
                          pdfUri = selectedPdfUri?.toString(),
                          fileName = selectedPdfFileName
                        )
                      )
                    }
                    "PAPER" -> {
                      livePapersMap[gradeTarget]?.add(
                        0,
                        QuestionPaperItem(
                          id = newId,
                          subject = inputSubject.ifBlank { "විද්‍යාව" },
                          titleSinhala = "$gradeTarget වසර - $inputTitle",
                          year = "2025",
                          term = inputExtraInfo.ifBlank { "1 වන වාරය" },
                          marks = if (selectedPdfFileName != null) "PDF ගොනුව ඇත" else "ලකුණු 100",
                          pdfUri = selectedPdfUri?.toString(),
                          fileName = selectedPdfFileName
                        )
                      )
                    }
                    "VIDEO" -> {
                      liveVideosMap[gradeTarget]?.add(
                        0,
                        VideoLessonItem(
                          id = newId,
                          subject = inputSubject.ifBlank { "විද්‍යාව" },
                          titleSinhala = "$gradeTarget වසර - $inputTitle",
                          duration = inputExtraInfo.ifBlank { "මිඩිටු 40" },
                          tutorName = "දේශක නිපුන් කුමාර",
                          viewsCount = "නැරඹුම් 1.2k",
                          isHd = true
                        )
                      )
                    }
                  }
                  Toast.makeText(context, "අන්තර්ගතය සාර්ථකව එකතු විය!", Toast.LENGTH_LONG).show()
                  inputTitle = ""
                  inputExtraInfo = ""
                  selectedPdfUri = null
                  selectedPdfFileName = null
                  showAddContentDialog = false
                } else {
                  Toast.makeText(context, "කරුණාකර මාතෘකාව ඇතුළත් කරන්න", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333))
            ) {
              Icon(imageVector = Icons.Default.Send, contentDescription = "Publish")
              Spacer(modifier = Modifier.width(6.dp))
              Text("එකතු කරන්න (Publish)")
            }
          },
          dismissButton = {
            TextButton(
              onClick = {
                showAddContentDialog = false
                selectedPdfUri = null
                selectedPdfFileName = null
              }
            ) {
              Text("අවලංගුයි")
            }
          }
        )
      }

      // Interactive Detail Modal Dialog
      activeDetailTitle?.let { title ->
        AlertDialog(
          onDismissRequest = {
            activeDetailTitle = null
            selectedItemPdfUri = null
          },
          title = {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = NeutralDark
            )
          },
          text = {
            Column {
              if (isVideoModal) {
                // Video Player Mockup Container
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                  contentAlignment = Alignment.Center
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.img_videos_bg_1786110404209),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .background(Color.Black.copy(alpha = 0.45f))
                  )
                  Surface(
                    shape = CircleShape,
                    color = BluePrimary,
                    modifier = Modifier.size(52.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "$selectedGrade වසර සඳහා මෙම වීඩියෝ පාඩම HD තත්ත්වයෙන් සහ සිංහල මාධ්‍යයෙන් නොමිලේ නැරඹිය හැක.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = NeutralMedium
                )
              } else {
                Text(
                  text = "මෙම කොටස $selectedGrade වසර සිසුන් සඳහා සම්පූර්ණයෙන්ම සක්‍රීයව පවතී.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = NeutralMedium
                )
              }

              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = BluePrimaryContainer,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ready",
                    tint = BluePrimary
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (isVideoModal) "මාර්ගගතව නැරඹීමට හෝ භාගත කරගැනීමට සූදානම්." else if (selectedItemPdfUri != null) "ඔබ අමුණන ලද PDF ගොනුව කියවීමට සූදානම්." else "PDF භාගත කිරීම සහ මාර්ගගතව කියවීම පහසුවෙන්ම කළ හැක.",
                    fontSize = 12.sp,
                    color = BlueOnPrimaryContainer
                  )
                }
              }
            }
          },
          confirmButton = {
            Button(
              onClick = {
                performProtectedAction {
                  if (selectedItemPdfUri != null) {
                    openPdfFile(context, selectedItemPdfUri)
                  } else {
                    val msg = if (isVideoModal) "වීඩියෝව වාදනය වීම ආරම්භ විය" else "භාගත කිරීම ආරම්භ විය"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  activeDetailTitle = null
                  selectedItemPdfUri = null
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = if (selectedItemPdfUri != null) Color(0xFFC62828) else BluePrimary)
            ) {
              Icon(
                imageVector = if (isVideoModal) Icons.Default.PlayArrow else if (selectedItemPdfUri != null) Icons.Default.PictureAsPdf else Icons.Default.Download,
                contentDescription = "Action"
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (isVideoModal) "Play Video" else if (selectedItemPdfUri != null) "PDF ගොනුව කියවන්න" else "Open / Download")
            }
          },
          dismissButton = {
            TextButton(
              onClick = {
                activeDetailTitle = null
                selectedItemPdfUri = null
              }
            ) {
              Text("Close")
            }
          }
        )
      }

      // Grade Not Approved Alert Modal
      if (showGradeNotApprovedDialog) {
        GradeNotApprovedAlertModal(
          targetGrade = gradeNotApprovedTarget,
          userRequestedPackage = loggedInUser?.requestedGradePackage ?: "නොදන්නා පැකේජය",
          onDismiss = { showGradeNotApprovedDialog = false },
          onRequestApproval = {
            preselectedGradeForApproval = when (gradeNotApprovedTarget) {
              "10", "11" -> "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)"
              "06" -> "06 ශ්‍රේණිය (Grade 6 Single Pack)"
              "07" -> "07 ශ්‍රේණිය (Grade 7 Single Pack)"
              "08" -> "08 ශ්‍රේණිය (Grade 8 Single Pack)"
              "09" -> "09 ශ්‍රේණිය (Grade 9 Single Pack)"
              else -> "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)"
            }
            showPaymentApprovalDialog = true
          }
        )
      }

      // Unlimited Access Payment & Verification Modal Dialog
      if (showPaymentApprovalDialog) {
        UnlimitedAccessPaymentDialog(
          initialPackage = preselectedGradeForApproval,
          onDismiss = { showPaymentApprovalDialog = false },
          onOpenLogin = {
            showPaymentApprovalDialog = false
            showAuthRequiredDialog = true
          },
          onSubmitReceipt = { name, phone, requestedPackage, receiptUri ->
            val existing = registeredUsers.find { it.usernameOrPhone == phone }
            if (existing != null) {
              val idx = registeredUsers.indexOf(existing)
              registeredUsers[idx] = existing.copy(
                fullName = name,
                requestedGradePackage = requestedPackage,
                slipImageUri = receiptUri?.toString(),
                paymentStatus = "Pending Approval with Receipt"
              )
            } else {
              val newId = (registeredUsers.size + 1).toString()
              val newUser = UserAccount(
                id = newId,
                fullName = name,
                usernameOrPhone = phone,
                password = "1234",
                isApproved = false,
                requestedGradePackage = requestedPackage,
                slipImageUri = receiptUri?.toString(),
                paymentStatus = "Pending Approval with Receipt"
              )
              registeredUsers.add(newUser)
              loggedInUser = newUser
            }
          }
        )
      }

      // Receipt Preview Fullscreen Modal Dialog
      if (previewReceiptImageUrl != null) {
        ReceiptPreviewDialog(
          receiptUriString = previewReceiptImageUrl!!,
          onDismiss = { previewReceiptImageUrl = null }
        )
      }

      // Auth & Admin Approval Popup Dialog
      if (showAuthRequiredDialog) {
        LoginAndApprovalDialog(
          registeredUsers = registeredUsers,
          onLoginSuccess = { user ->
            loggedInUser = user
            showAuthRequiredDialog = false
          },
          onAdminLoginSuccess = {
            isAdminAuthenticated = true
            showAuthRequiredDialog = false
          },
          onDismiss = { showAuthRequiredDialog = false },
          onUploadPdfToFirebase = { title, grade, category, subject, uri, fileName ->
            val newId = System.currentTimeMillis().toString()
            val pdfUriString = uri?.toString() ?: "https://firebasestorage.googleapis.com/v0/b/studentportal-app.appspot.com/o/pdfs%2F${grade}%2F${fileName ?: "document.pdf"}?alt=media"
            val fileStr = fileName ?: "Cloud_PDF_$newId.pdf"
            if (category == "NOTE") {
              liveNotesMap[grade]?.add(
                0,
                ShortNoteItem(
                  id = newId,
                  subject = subject.ifBlank { "විද්‍යාව" },
                  title = "$grade වසර - $title",
                  topicSinhala = title,
                  readTime = "🔥 Firebase Cloud • $fileStr",
                  isPopular = true,
                  pdfUri = pdfUriString,
                  fileName = fileStr
                )
              )
            } else {
              livePapersMap[grade]?.add(
                0,
                QuestionPaperItem(
                  id = newId,
                  subject = subject.ifBlank { "විද්‍යාව" },
                  titleSinhala = "$grade වසර - $title",
                  year = "2025",
                  term = "🔥 Firebase Cloud",
                  marks = "PDF ගොනුව ඇත",
                  pdfUri = pdfUriString,
                  fileName = fileStr
                )
              )
            }
          },
          onPickPdf = {
            pdfPickerLauncher.launch("application/pdf")
          },
          selectedPdfFileName = selectedPdfFileName,
          selectedPdfUri = selectedPdfUri,
          onPreviewReceipt = { uriStr ->
            previewReceiptImageUrl = uriStr
          }
        )
      }

      // Password Requirement Dialog for Protected PDFs
      if (showPasswordDialog) {
        AlertDialog(
          onDismissRequest = {
            showPasswordDialog = false
            showPasswordError = false
          },
          icon = {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Protected PDF",
              tint = Color(0xFFC62828),
              modifier = Modifier.size(32.dp)
            )
          },
          title = {
            Text(
              text = "🔒 මුරපදය ඇතුළත් කරන්න (Password)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = NeutralDark
            )
          },
          text = {
            Column(modifier = Modifier.fillMaxWidth()) {
              Text(
                text = pendingProtectedPdfTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFC62828)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "මෙම PDF ගොනුව මුරපදයකින් (Password) ආරක්ෂිතයි. කරුණාකර විවෘත කිරීමට මුරපදය ඇතුළත් කරන්න.",
                fontSize = 12.sp,
                color = NeutralMedium
              )
              Spacer(modifier = Modifier.height(14.dp))

              var passwordVisible by remember { mutableStateOf(false) }

              androidx.compose.material3.OutlinedTextField(
                value = passwordInputText,
                onValueChange = {
                  passwordInputText = it
                  showPasswordError = false
                },
                label = { Text("මුරපදය (Password)") },
                singleLine = true,
                isError = showPasswordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                  IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                      imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                      contentDescription = "Toggle Password"
                    )
                  }
                },
                modifier = Modifier.fillMaxWidth()
              )

              if (showPasswordError) {
                Text(
                  text = "❌ වැරදි මුරපදයකි! කරුණාකර නිවැරදි මුරපදය (1234) ඇතුළත් කරන්න.",
                  color = Color.Red,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(top = 6.dp)
                )
              }
            }
          },
          confirmButton = {
            Button(
              onClick = {
                if (passwordInputText.trim() == pendingProtectedPdfPassword.trim()) {
                  showPasswordDialog = false
                  showPasswordError = false
                  iframeModalPdfTitle = pendingProtectedPdfTitle
                  iframeModalPdfUrl = pendingProtectedPdfUrl
                  showIframePdfModal = true
                  Toast.makeText(context, "🔒 මුරපදය නිවැරදියි! PDF ගොනුව විවෘත වේ...", Toast.LENGTH_SHORT).show()
                } else {
                  showPasswordError = true
                  Toast.makeText(context, "❌ වැරදි මුරපදයකි!", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Lock, contentDescription = "Unlock")
              Spacer(modifier = Modifier.width(6.dp))
              Text("විවෘත කරන්න (Unlock)")
            }
          },
          dismissButton = {
            TextButton(
              onClick = {
                showPasswordDialog = false
                showPasswordError = false
              }
            ) {
              Text("අවලංගු කරන්න")
            }
          }
        )
      }

      // In-App Embedded Iframe Modal Viewer for Google Drive PDFs (DRM Protected & Secured)
      if (showIframePdfModal) {
        Dialog(
          onDismissRequest = { showIframePdfModal = false },
          properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
              .fillMaxWidth(0.96f)
              .fillMaxHeight(0.92f)
              .padding(8.dp)
          ) {
            Column(modifier = Modifier.fillMaxSize()) {
              // Modal Header
              Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC62828)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Preview",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = iframeModalPdfTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "🛡️ ආරක්ෂිත අධ්‍යාපන පීඩීඑෆ් • Screen Record & Downloads අවහිරයි",
                        fontSize = 10.sp,
                        color = Color(0xFF86EFAC)
                      )
                    }
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color(0xFF064E3B)
                    ) {
                      Text(
                        text = "DRM SECURE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF86EFAC),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = { showIframePdfModal = false }) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Modal",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                      )
                    }
                  }
                }
              }

              // Embedded WebView iframe with Floating "Test Your Knowledge" FAB & Anti-Piracy Watermark
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth()
                  .background(Color(0xFFF1F5F9))
              ) {
                AndroidView(
                  factory = { ctx ->
                    WebView(ctx).apply {
                      webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                          super.onPageFinished(view, url)
                          view?.evaluateJavascript(
                            """
                            (function() {
                              var style = document.createElement('style');
                              style.innerHTML = '* { -webkit-user-select: none !important; user-select: none !important; -webkit-touch-callout: none !important; }';
                              document.head.appendChild(style);
                              document.addEventListener('contextmenu', function(e) { e.preventDefault(); return false; });
                              document.addEventListener('copy', function(e) { e.preventDefault(); return false; });
                            })();
                            """.trimIndent(),
                            null
                          )
                        }
                      }
                      settings.javaScriptEnabled = true
                      settings.domStorageEnabled = true
                      settings.allowFileAccess = false
                      settings.useWideViewPort = true
                      settings.loadWithOverviewMode = true
                      setDownloadListener { _, _, _, _, _ ->
                        Toast.makeText(ctx, "🚫 ආරක්ෂිත අධ්‍යාපනික ලේඛනයකි - PDF භාගත කිරීම (Download) අවහිර කර ඇත. යෙදුම තුළින් පමණක් කියවිය හැක.", Toast.LENGTH_LONG).show()
                      }
                      setOnLongClickListener {
                        Toast.makeText(ctx, "🚫 ආරක්ෂිත ලේඛනයකි - පිටපත් කිරීම (Copy) හෝ භාගත කිරීම (Download) කළ නොහැක.", Toast.LENGTH_SHORT).show()
                        true
                      }
                      isLongClickable = false
                      isHapticFeedbackEnabled = false
                      loadUrl(iframeModalPdfUrl)
                    }
                  },
                  update = { webView ->
                    if (webView.url != iframeModalPdfUrl) {
                      webView.loadUrl(iframeModalPdfUrl)
                    }
                  },
                  onRelease = { webView ->
                    webView.stopLoading()
                    webView.destroy()
                  },
                  modifier = Modifier.fillMaxSize()
                )

                // Anti-Piracy Dynamic Watermark Overlay to discourage illicit recording or sharing
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f)
                    .padding(24.dp),
                  verticalArrangement = Arrangement.SpaceAround,
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  repeat(6) {
                    Text(
                      text = "CONFIDENTIAL • NO DOWNLOAD • ${loggedInUser?.fullName ?: "STUDENT PORTAL"} • ${loggedInUser?.usernameOrPhone ?: "PROTECTED"}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.Black
                    )
                  }
                }

                // Smart PDF Sticky Notes & Digital Highlight Annotations Overlay
                SmartPdfStickyNotesOverlay(
                  pdfTitle = iframeModalPdfTitle,
                  modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                )

                // LOCATION 2: Persistent Floating Action Button (FAB) inside PDF Viewer Screen
                FloatingActionButton(
                  onClick = { showPdfQuizBottomSheet = true },
                  containerColor = Color(0xFF4F46E5),
                  contentColor = Color.White,
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("pdf_viewer_quiz_fab")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("🧠", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Test Your Knowledge",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = Color.White
                    )
                  }
                }
              }

              // Modal Footer
              Surface(
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, NeutralBorderLight),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Surface(
                      onClick = { showPdfQuizBottomSheet = true },
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFFEEF2FF),
                      border = BorderStroke(1.dp, Color(0xFFC7D2FE))
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text("🧠", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "10 MCQ Quizzes (Set 1-10)",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF4338CA)
                        )
                      }
                    }

                    Surface(
                      onClick = { showFlashcardsDialog = true },
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFFF0FDF4),
                      border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text("💡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "Flashcards",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF15803D)
                        )
                      }
                    }
                  }

                  Button(
                    onClick = { showIframePdfModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("වසා දමන්න", fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }
      }

      // AI Practice Quiz Interactive Engine Dialog (180s Countdown Timer, Shuffling, Review)
      if (activeQuizSet != null) {
        QuizEngineDialog(
          quizSet = activeQuizSet!!,
          onDismiss = { activeQuizSet = null },
          onRetake = { set -> activeQuizSet = set }
        )
      }

      // Flashcards Dialog (Active Recall Mode)
      if (showFlashcardsDialog && selectedSubjectItem != null) {
        FlashcardsViewerDialog(
          grade = selectedGrade,
          subjectName = selectedSubjectItem!!.nameSinhala,
          onDismiss = { showFlashcardsDialog = false }
        )
      }

      // DRM & Screenshot / Screen Record / Copy Alert Dialog
      if (showDrmWarningDialog) {
        AlertDialog(
          onDismissRequest = { showDrmWarningDialog = false },
          icon = {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Security Alert",
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(36.dp)
            )
          },
          title = {
            Text(
              text = "🛡️ ආරක්ෂිත අධ්‍යාපනික අන්තර්ගතයකි!",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color(0xFF991B1B)
            )
          },
          text = {
            Column {
              Text(
                text = "මෙම යෙදුම තුළ ඇති කෙටි සටහන්, ප්‍රශ්න පත්‍ර සහ අධ්‍යයන අන්තර්ගතයන් $drmWarningReason සිදු කිරීම හෝ පිටපත් කිරීම (Copy/Download) සම්පූර්ණයෙන්ම තහනම් කර ඇත.",
                fontSize = 13.sp,
                color = Color(0xFF1E293B),
                lineHeight = 18.sp
              )
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFECACA))
              ) {
                Text(
                  text = "⚠️ සියලුම අන්තර්ගතයන් ඇප් එක තුළින් පමණක් කියවීම සඳහා සකසා ඇත. පිටපත් කිරීම, භාගත කිරීම හෝ Screen Recording / Screenshot තහනම්ය.",
                  fontSize = 11.sp,
                  color = Color(0xFFB91C1C),
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          },
          confirmButton = {
            Button(
              onClick = { showDrmWarningDialog = false },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
              Text("තේරුම් ගතිමි (OK)")
            }
          }
        )
      }

      // LOCATION 2: Modal Bottom Sheet for PDF Viewer
      if (showPdfQuizBottomSheet) {
        QuizSetsBottomSheet(
          grade = selectedGrade,
          subjectName = selectedSubjectItem?.nameSinhala ?: "විද්‍යාව",
          onDismiss = { showPdfQuizBottomSheet = false },
          onStartQuizSet = { set ->
            performProtectedAction {
              activeQuizSet = set
            }
          },
          onOpenFlashcards = {
            showFlashcardsDialog = true
          }
        )
      }

      // Persistent Audio Mini-Player (Active Podcast across screens)
      activeAudioState?.let { activeAudio ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp),
          contentAlignment = Alignment.BottomCenter
        ) {
          PersistentAudioMiniPlayer(
            activeAudio = activeAudio,
            onPause = { pauseAudio() },
            onResume = { resumeAudio() },
            onToggleSpeed = { toggleAudioSpeed() },
            onOpenPdfAtPage = { pdfUrl, title, page -> openPdfAtPage(pdfUrl, title, page) },
            onDismiss = { activeAudioState = null }
          )
        }
      }

    }
  }
}

@Composable
fun LiveCloudSyncHeaderBanner(
  isAdmin: Boolean = false,
  onSyncNow: () -> Unit = {},
  onOpenAddModal: () -> Unit = {},
  onInfoClick: () -> Unit = {}
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
    border = BorderStroke(1.dp, Color(0xFFCEEAD6)),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable { onInfoClick() }
      .testTag("live_sync_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF137333)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = "Realtime Live Cloud",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E8E3E))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "සජීවී ක්ලවුඩ් සබඳතාවය (100% නොමිලේ)",
              style = MaterialTheme.typography.labelMedium,
              color = Color(0xFF0D652D),
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFF137333)
            ) {
              Text(
                text = "FREE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
          Text(
            text = "සියලුම සිසුන්ට නොමිලේ • සටහන්/ප්‍රශ්න පත්‍ර එසැනින් යාවත්කාලීන වේ",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF137333),
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      if (isAdmin) {
        Surface(
          onClick = onOpenAddModal,
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF137333),
          modifier = Modifier.testTag("live_add_btn")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add",
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "එකතු කරන්න",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      } else {
        Surface(
          onClick = onSyncNow,
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, Color(0xFFCEEAD6)),
          modifier = Modifier.testTag("live_sync_btn")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Sync",
              tint = Color(0xFF137333),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Sync",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF137333),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun FreeCloudInfoDialog(
  onDismiss: () -> Unit,
  onSyncNow: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(Color(0xFFE6F4EA)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CloudUpload,
          contentDescription = "Cloud Free",
          tint = Color(0xFF137333),
          modifier = Modifier.size(32.dp)
        )
      }
    },
    title = {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "☁️ සජීවී ක්ලවුඩ් සබඳතාවය",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = Color(0xFF137333)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFF137333)
        ) {
          Text(
            text = "සියලුම පරිශීලකයින්ට 100% නොමිලේ (100% Free for All)",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF1F8E9),
          border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = Color(0xFF137333), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "ක්ලවුඩ් සබඳතාවය සක්‍රීයයි (Active & Online)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1B5E20)
              )
              Text(
                text = "Google Firebase / Cloud Server හා සම්බන්ධයි",
                fontSize = 10.sp,
                color = Color(0xFF2E7D32)
              )
            }
          }
        }

        Text(
          text = "මෙම පහසුකම කුමක්ද?",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = NeutralDark
        )

        Text(
          text = "මෙම ඇප් එක සතු 'සජීවී ක්ලවුඩ් සබඳතාවය' (Live Realtime Cloud Connection) මගින් ගුරුවරුන් සහ පරිපාලකයින් විසින් අලුතින් එක්කරන සියලුම කෙටි සටහන්, ප්‍රශ්න පත්‍ර සහ වීඩියෝ පාඩම් ඔබගේ දුරකථනයට එසැනින් (Real-time) ගලා ඒම සිදුවේ.",
          fontSize = 12.sp,
          color = NeutralDark,
          lineHeight = 18.sp
        )

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFE8F5E9),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.Top) {
              Text("✨", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("100% නොමිලේ: මෙම සජීවී සබඳතාවය සඳහා කිසිදු පරිශීලකයෙකුගෙන් මුදල් අය නොකෙරේ.", fontSize = 11.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.Top) {
              Text("⚡", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("APK Update අනවශ්‍යයි: අලුත් පාඩම් ලැබීමට ඇප් එක නැවත Play Store එකෙන් Update කිරීමට අවශ්‍ය නොවේ.", fontSize = 11.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.Top) {
              Text("🔒", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("නවතම අධ්‍යයන ද්‍රව්‍ය: Grade 06 සිට 11 දක්වා සියලු නව විෂය කරුණු නොකඩවා ලැබෙයි.", fontSize = 11.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onSyncNow,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("දත්ත Sync කරන්න (නොමිලේ)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("තේරුම් ගත්තා", fontSize = 11.sp, color = NeutralDark)
      }
    }
  )
}

@Composable
fun SectionHeaderCardWithBg(
  title: String,
  subtitle: String,
  imageRes: Int,
  tag: String,
  onActionClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable { onActionClick() }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(115.dp)
    ) {
      Image(
        painter = painterResource(id = imageRes),
        contentDescription = title,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.2f),
                Color.Black.copy(alpha = 0.75f)
              )
            )
          )
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BluePrimary,
          ) {
            Text(
              text = tag,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }

          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@Composable
fun SubjectCardRow(
  subject: SubjectItem,
  onClick: () -> Unit,
  onDelete: (() -> Unit)? = null
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorderLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(subject.color.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = subject.icon,
            contentDescription = subject.name,
            tint = subject.color,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = subject.nameSinhala,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = NeutralDark
          )
          Text(
            text = "${subject.name} • ${subject.chaptersCount} පාඩම් මාලා",
            style = MaterialTheme.typography.bodySmall,
            color = NeutralMedium
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (onDelete != null) {
          Surface(
            onClick = onDelete,
            shape = CircleShape,
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(end = 6.dp)
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Live",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        Surface(
          shape = CircleShape,
          color = SurfaceVariantLight,
          modifier = Modifier.size(32.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = "Read",
              tint = BluePrimary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun ShortNoteCardRow(
  note: ShortNoteItem,
  onClick: () -> Unit,
  onDelete: (() -> Unit)? = null,
  onOpenPdf: (() -> Unit)? = null,
  onOpenQuiz: (() -> Unit)? = null,
  onOpenFlashcards: (() -> Unit)? = null
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorderLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (note.pdfUri != null) Color(0xFFFFEBEE) else BluePrimaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (note.pdfUri != null) Icons.Default.PictureAsPdf else Icons.Default.Description,
              contentDescription = "Notes",
              tint = if (note.pdfUri != null) Color(0xFFC62828) else BluePrimary,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = note.topicSinhala,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
              )
              if (note.pdfUri != null || note.fileName != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFFEBEE)
                ) {
                  Text(
                    text = "PDF",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              } else if (note.isPopular) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFFECEB)
                ) {
                  Text(
                    text = "HOT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD93025),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            }
            Text(
              text = if (note.fileName != null) "${note.subject} • ${note.fileName}" else "${note.subject} • කියවීමේ කාලය ${note.readTime}",
              style = MaterialTheme.typography.bodySmall,
              color = NeutralMedium
            )
          }
        }

        if (onDelete != null) {
          Surface(
            onClick = onDelete,
            shape = CircleShape,
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(start = 6.dp)
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Live",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = Color(0xFFF1F5F9))
      Spacer(modifier = Modifier.height(8.dp))

      // Action Chips: PDF View, 10 Quiz Sets (100 MCQs), Flashcards
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (note.pdfUri != null && onOpenPdf != null) {
          Surface(
            onClick = onOpenPdf,
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEBEE),
            border = BorderStroke(0.5.dp, Color(0xFFFFCDD2))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "Open PDF",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "PDF බලන්න",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
              )
            }
          }
        }

        Surface(
          onClick = {
            if (onOpenQuiz != null) onOpenQuiz() else if (onOpenPdf != null) onOpenPdf()
          },
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFEEF2FF),
          border = BorderStroke(0.5.dp, Color(0xFFC7D2FE))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🧠", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "10 Sets (100 MCQs)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF4338CA)
            )
          }
        }

        Surface(
          onClick = {
            if (onOpenFlashcards != null) onOpenFlashcards() else if (onOpenPdf != null) onOpenPdf()
          },
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF0FDF4),
          border = BorderStroke(0.5.dp, Color(0xFFBBF7D0))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("💡", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Flashcards",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
          }
        }
      }
    }
  }
}

@Composable
fun QuestionPaperCardRow(
  paper: QuestionPaperItem,
  onClick: () -> Unit,
  onDelete: (() -> Unit)? = null,
  onOpenPdf: (() -> Unit)? = null
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorderLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (paper.pdfUri != null) Color(0xFFFFEBEE) else Color(0xFFE6F4EA)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (paper.pdfUri != null) Icons.Default.PictureAsPdf else Icons.Default.Quiz,
            contentDescription = "Paper",
            tint = if (paper.pdfUri != null) Color(0xFFC62828) else Color(0xFF137333),
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = paper.titleSinhala,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = NeutralDark
          )
          Text(
            text = if (paper.fileName != null) "${paper.subject} • ${paper.fileName}" else "${paper.subject} • ${paper.year} ${paper.term} • ${paper.marks}",
            style = MaterialTheme.typography.bodySmall,
            color = NeutralMedium
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (onDelete != null) {
          Surface(
            onClick = onDelete,
            shape = CircleShape,
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(end = 6.dp)
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Live",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        if (paper.pdfUri != null && onOpenPdf != null) {
          Surface(
            onClick = onOpenPdf,
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEBEE)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "Open PDF",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "බලන්න",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
              )
            }
          }
        } else {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BluePrimaryContainer
          ) {
            Text(
              text = "PDF",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = BlueOnPrimaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun VideoLessonCardRow(
  video: VideoLessonItem,
  onClick: () -> Unit,
  onDelete: (() -> Unit)? = null
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorderLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFEF7E0)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayCircleFilled,
            contentDescription = "Play Video",
            tint = Color(0xFFB06000),
            modifier = Modifier.size(30.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = video.titleSinhala,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = NeutralDark
            )
            if (video.isHd) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = BluePrimary
              ) {
                Text(
                  text = "HD",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
          Text(
            text = "${video.subject} • ${video.tutorName} • ${video.duration}",
            style = MaterialTheme.typography.bodySmall,
            color = NeutralMedium
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (onDelete != null) {
          Surface(
            onClick = onDelete,
            shape = CircleShape,
            color = Color(0xFFFFEBEE),
            modifier = Modifier.padding(end = 6.dp)
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Live",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SurfaceVariantLight
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.OndemandVideo,
              contentDescription = "Views",
              tint = NeutralMedium,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = video.viewsCount,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              color = NeutralMedium
            )
          }
        }
      }
    }
  }
}

@Composable
fun TopHeaderSection(
  userName: String,
  userStream: String,
  isApproved: Boolean,
  onAuthClick: () -> Unit,
  onNotificationClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(if (isApproved) Color(0xFF137333) else BluePrimary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isApproved) Icons.Default.Verified else Icons.Default.Person,
          contentDescription = "User",
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = userName,
          style = MaterialTheme.typography.titleMedium,
          color = NeutralDark,
          fontWeight = FontWeight.Bold,
          lineHeight = 18.sp
        )
        Text(
          text = userStream,
          style = MaterialTheme.typography.bodySmall,
          color = NeutralMedium
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Surface(
        onClick = onAuthClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isApproved) Color(0xFFE6F4EA) else Color(0xFFFFF3E0),
        border = BorderStroke(1.dp, if (isApproved) Color(0xFFCEEAD6) else Color(0xFFFFE0B2)),
        modifier = Modifier.padding(end = 8.dp).testTag("auth_status_chip")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (isApproved) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
            contentDescription = "Auth",
            tint = if (isApproved) Color(0xFF137333) else Color(0xFFE65100),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isApproved) "👑 ඇඩ්මින් පැනලය" else "ලොගින් / Admin",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isApproved) Color(0xFF137333) else Color(0xFFE65100)
          )
        }
      }

      Surface(
        onClick = onNotificationClick,
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, NeutralBorder),
        modifier = Modifier.testTag("notification_button")
      ) {
        Box(
          modifier = Modifier
            .padding(8.dp)
            .size(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = NeutralDark,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun SecuritySubscriptionBanner(
  isApproved: Boolean = false,
  isFeatureTrialActive: Boolean = true,
  remainingTrialHours: Int = 24,
  onClick: () -> Unit = {}
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isApproved) Color(0xFFE8F5E9) else if (isFeatureTrialActive) Color(0xFFF0FDF4) else Color(0xFFFFF8E1)
    ),
    border = BorderStroke(1.dp, if (isApproved) Color(0xFFA5D6A7) else if (isFeatureTrialActive) Color(0xFF86EFAC) else Color(0xFFFFD54F)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("security_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isApproved) Color(0xFF137333) else if (isFeatureTrialActive) Color(0xFF047857) else Color(0xFFE65100)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isApproved) Icons.Default.Verified else if (isFeatureTrialActive) Icons.Default.AutoAwesome else Icons.Default.WorkspacePremium,
            contentDescription = "Badge",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = if (isApproved) "UNLIMITED ACCESS ACTIVATED" else if (isFeatureTrialActive) "විශේෂාංග නොමිලේ පෙරදසුන (දින 1)" else "UNLIMITED ACCESS ලබාගන්න",
            style = MaterialTheme.typography.labelSmall,
            color = if (isApproved) Color(0xFF137333) else if (isFeatureTrialActive) Color(0xFF065F46) else Color(0xFFE65100),
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isApproved) {
              "සියලු ශ්‍රේණිවල පාඩම් අනුමතයි • Official Access"
            } else if (isFeatureTrialActive) {
              "පළමු දින නොමිලේ විශේෂාංග නැරඹිය හැක (${remainingTrialHours}h ඉතිරියි) • දෙවන දින අනුමැතිය අවශ්‍යයි"
            } else {
              "පළමු දින අවසන් • රු. 1,000 ක වාර්ෂික අනුමැතියෙන් පසු සියල්ල Unlimited!"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (isApproved) Color(0xFF1B5E20) else if (isFeatureTrialActive) Color(0xFF047857) else Color(0xFFB45309),
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isApproved) Color(0xFF137333) else if (isFeatureTrialActive) Color(0xFF047857) else Color(0xFFE65100)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isApproved) "ACTIVE" else if (isFeatureTrialActive) "${remainingTrialHours}h FREE" else "අනුමැතිය",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun PortalBottomNavigation(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    color = SurfaceVariantLight,
    shadowElevation = 8.dp,
    modifier = Modifier.navigationBarsPadding()
  ) {
    NavigationBar(
      containerColor = Color.Transparent,
      tonalElevation = 0.dp,
      modifier = Modifier.height(72.dp)
    ) {
      val items = listOf(
        Triple("මුල් පිටුව", Icons.Default.Home, "nav_home"),
        Triple("ප්‍රගතිය", Icons.Default.WorkspacePremium, "nav_analytics"),
        Triple("රචනා/ප්‍රශ්න", Icons.Default.Description, "nav_essay"),
        Triple("සූත්‍ර", Icons.Default.MenuBook, "nav_formulas"),
        Triple("AI Tutor", Icons.Default.AutoAwesome, "nav_ai_tutor")
      )

      items.forEachIndexed { index, (label, icon, testTag) ->
        val isSelected = selectedTab == index
        NavigationBarItem(
          selected = isSelected,
          onClick = { onTabSelected(index) },
          icon = {
            Icon(
              imageVector = icon,
              contentDescription = label,
              modifier = Modifier.size(24.dp)
            )
          },
          label = {
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = BlueOnPrimaryContainer,
            selectedTextColor = BlueOnPrimaryContainer,
            indicatorColor = BluePrimaryContainer,
            unselectedIconColor = NeutralMedium,
            unselectedTextColor = NeutralMedium
          ),
          modifier = Modifier.testTag(testTag)
        )
      }
    }
  }
}

@Composable
fun WatermarkBackground(userText: String = "EduPortal • Official Educational Access") {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = userText,
      style = MaterialTheme.typography.titleMedium,
      color = Color.Black.copy(alpha = 0.03f),
      fontWeight = FontWeight.Bold,
      modifier = Modifier.rotate(45f)
    )
  }
}

// Data Generators (No fake/dummy items - only real user-added resources)
fun getSubjectsForGrade(grade: String): List<SubjectItem> {
  return when (grade) {
    "11" -> listOf(
      SubjectItem("sub_11_sci", "Science", "විද්‍යාව", 18, Color(0xFF1B5E20), Icons.Default.AutoAwesome),
      SubjectItem("sub_11_math", "Mathematics", "ගණිතය", 24, Color(0xFF0D47A1), Icons.Default.MenuBook),
      SubjectItem("sub_11_hist", "History", "ඉතිහාසය", 12, Color(0xFF8D6E63), Icons.Default.Book),
      SubjectItem("sub_11_bud", "Buddhism", "බුද්ධ ධර්මය", 14, Color(0xFFE65100), Icons.Default.AutoAwesome),
      SubjectItem("sub_11_dance", "Dancing", "නර්තනය", 12, Color(0xFFD81B60), Icons.Default.AutoAwesome),
      SubjectItem("sub_11_music", "Oriental Music", "සංගීතය", 14, Color(0xFFC2185B), Icons.Default.MusicNote),
      SubjectItem("sub_11_civic", "Civic Education", "පුරවැසි අධ්‍යාපනය", 12, Color(0xFF00695C), Icons.Default.Description),
      SubjectItem("sub_11_comm", "Commerce & Accounting", "ව්‍යාපාර අධ්‍යයනය හා ගිණුම්කරණය", 16, Color(0xFF00695C), Icons.Default.Description),
      SubjectItem("sub_11_ict", "Information Technology", "තොරතුරු තාක්ෂණය", 10, Color(0xFF283593), Icons.Default.AutoAwesome),
      SubjectItem("sub_11_agri", "Agriculture & Food Tech", "කෘෂි හා ආහාර තාක්ෂණය", 12, Color(0xFF33691E), Icons.Default.AutoAwesome),
      SubjectItem("sub_11_health", "Health & Physical Ed", "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය", 10, Color(0xFFC2185B), Icons.Default.CheckCircle),
      SubjectItem("sub_11_sin", "Sinhala Language", "සිංහල භාෂාව හා සාහිත්‍යය", 16, Color(0xFF6A1B9A), Icons.Default.Book),
      SubjectItem("sub_11_geo", "Geography", "භූගෝල විද්‍යාව", 12, Color(0xFF00796B), Icons.Default.Description),
      SubjectItem("sub_11_eng", "English Language", "ඉංග්‍රීසි භාෂාව", 14, Color(0xFF00838F), Icons.Default.MenuBook)
    )
    "10" -> listOf(
      SubjectItem("sub_10_sci", "Science", "විද්‍යාව", 18, Color(0xFF1B5E20), Icons.Default.AutoAwesome),
      SubjectItem("sub_10_math", "Mathematics", "ගණිතය", 22, Color(0xFF0D47A1), Icons.Default.MenuBook),
      SubjectItem("sub_10_hist", "History", "ඉතිහාසය", 12, Color(0xFF8D6E63), Icons.Default.Book),
      SubjectItem("sub_10_bud", "Buddhism", "බුද්ධ ධර්මය", 14, Color(0xFFE65100), Icons.Default.AutoAwesome),
      SubjectItem("sub_10_dance", "Dancing", "නර්තනය", 12, Color(0xFFD81B60), Icons.Default.AutoAwesome),
      SubjectItem("sub_10_music", "Oriental Music", "සංගීතය", 14, Color(0xFFC2185B), Icons.Default.MusicNote),
      SubjectItem("sub_10_geo", "Geography", "භූගෝල විද්‍යාව", 12, Color(0xFF00796B), Icons.Default.Description),
      SubjectItem("sub_10_civic", "Civic Education", "පුරවැසි අධ්‍යාපනය", 12, Color(0xFF00695C), Icons.Default.Description),
      SubjectItem("sub_10_comm", "Commerce & Accounting", "ව්‍යාපාර අධ්‍යයනය හා ගිණුම්කරණය", 14, Color(0xFF00695C), Icons.Default.Description),
      SubjectItem("sub_10_ict", "Information Technology", "තොරතුරු තාක්ෂණය", 10, Color(0xFF283593), Icons.Default.AutoAwesome),
      SubjectItem("sub_10_agri", "Agriculture & Food Tech", "කෘෂි හා ආහාර තාක්ෂණය", 12, Color(0xFF33691E), Icons.Default.AutoAwesome),
      SubjectItem("sub_10_health", "Health & Physical Ed", "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය", 10, Color(0xFFC2185B), Icons.Default.CheckCircle),
      SubjectItem("sub_10_sin", "Sinhala Language", "සිංහල භාෂාව හා සාහිත්‍යය", 16, Color(0xFF6A1B9A), Icons.Default.Book),
      SubjectItem("sub_10_eng", "English Language", "ඉංග්‍රීසි භාෂාව", 14, Color(0xFF00838F), Icons.Default.MenuBook)
    )
    "09", "08", "07", "06" -> listOf(
      SubjectItem("sub_${grade}_sci", "Science", "විද්‍යාව", 16, Color(0xFF1B5E20), Icons.Default.AutoAwesome),
      SubjectItem("sub_${grade}_math", "Mathematics", "ගණිතය", 20, Color(0xFF0D47A1), Icons.Default.MenuBook),
      SubjectItem("sub_${grade}_hist", "History", "ඉතිහාසය", 10, Color(0xFF8D6E63), Icons.Default.Book),
      SubjectItem("sub_${grade}_bud", "Buddhism", "බුද්ධ ධර්මය", 12, Color(0xFFE65100), Icons.Default.AutoAwesome),
      SubjectItem("sub_${grade}_sin", "Sinhala Language", "සිංහල භාෂාව හා සාහිත්‍යය", 14, Color(0xFF6A1B9A), Icons.Default.Book),
      SubjectItem("sub_${grade}_eng", "English Language", "ඉංග්‍රීසි භාෂාව", 12, Color(0xFF00838F), Icons.Default.MenuBook),
      SubjectItem("sub_${grade}_geo", "Geography", "භූගෝල විද්‍යාව", 10, Color(0xFFE65100), Icons.Default.Book),
      SubjectItem("sub_${grade}_civic", "Civic Education", "පුරවැසි අධ්‍යාපනය", 8, Color(0xFF00695C), Icons.Default.Description),
      SubjectItem("sub_${grade}_pts", "Practical & Tech Skills", "ප්‍රායෝගික හා තාක්ෂණික කුසලතා", 10, Color(0xFF33691E), Icons.Default.AutoAwesome),
      SubjectItem("sub_${grade}_health", "Health & Physical Ed", "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය", 8, Color(0xFFC2185B), Icons.Default.CheckCircle)
    )
    else -> emptyList()
  }
}

fun getNotesForGrade(grade: String): List<ShortNoteItem> {
  val ictNote = ShortNoteItem(
    id = "ict_note_gr10_11",
    subject = "තොරතුරු තාක්ෂණය",
    title = "10 සහ 11 ශ්‍රේණිය - තොරතුරු තාක්ෂණය කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය තොරතුරු තාක්ෂණය",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview",
    fileName = "ICT_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val commerceNote = ShortNoteItem(
    id = "commerce_note_gr10_11",
    subject = "ව්‍යාපාර අධ්‍යයනය හා ගිණුම්කරණය",
    title = "10 සහ 11 ශ්‍රේණිය - ව්‍යාපාර අධ්‍යයනය හා ගිණුම්කරණය කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය ව්‍යාපාර අධ්‍යයනය හා ගිණුම්කරණය",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1PPsYx6Tm7x_0YSy3es3edqBMizD08bJZ/preview",
    fileName = "Commerce_Accounting_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val mathGeometryNote = ShortNoteItem(
    id = "math_geometry_note_gr10_11",
    subject = "ගණිතය",
    title = "10 සහ 11 ශ්‍රේණි - ගණිතය (ජ්‍යාමිතිය ප්‍රමේය) කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය ගණිතය (ජ්‍යාමිතිය)",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview",
    fileName = "Mathematics_Geometry_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val mathAlgebraNoteGr10_11 = ShortNoteItem(
    id = "math_algebra_note_gr10_11",
    subject = "ගණිතය",
    title = "10 සහ 11 ශ්‍රේණි - ගණිතය (වීජ ගණිතය, ශ්‍රේඪි හා ත්‍රිකෝණමිතිය) කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය වීජ ගණිතය හා ත්‍රිකෝණමිතිය",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview",
    fileName = "Mathematics_Algebra_Trigonometry_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val mathStatsFormulaNoteGr10_11 = ShortNoteItem(
    id = "math_stats_formula_note_gr10_11",
    subject = "ගණිතය",
    title = "10 සහ 11 ශ්‍රේණි - ගණිතය (සංඛ්‍යානය, සම්භාවිතාව, මිනුම් හා සූත්‍ර සංග්‍රහය)",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය ගණිත සූත්‍ර හා සංඛ්‍යානය",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview",
    fileName = "Mathematics_Formulas_Statistics_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val scienceChemistryNoteGr10_11 = ShortNoteItem(
    id = "science_chem_note_gr10_11",
    subject = "විද්‍යාව",
    title = "10 සහ 11 ශ්‍රේණි - විද්‍යාව රසායන විද්‍යාව (Chemistry) විශේෂ කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණි රසායන විද්‍යාව",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/17TcFs1wECaHB4C3LMdC8mO2YDKrtrEOI/preview",
    fileName = "Science_Chemistry_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val sciencePhysicsNoteGr10_11 = ShortNoteItem(
    id = "science_phy_note_gr10_11",
    subject = "විද්‍යාව",
    title = "10 සහ 11 ශ්‍රේණි - විද්‍යාව භෞතික විද්‍යාව (Physics) සූත්‍ර හා සංකල්ප",
    topicSinhala = "10 සහ 11 ශ්‍රේණි භෞතික විද්‍යාව",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/17TcFs1wECaHB4C3LMdC8mO2YDKrtrEOI/preview",
    fileName = "Science_Physics_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val englishWritingNoteGr10_11 = ShortNoteItem(
    id = "english_writing_note_gr10_11",
    subject = "ඉංග්‍රීසි",
    title = "10 සහ 11 ශ්‍රේණි - O/L English Letter Writing, Essays, Notices & Vocab",
    topicSinhala = "10 සහ 11 ශ්‍රේණි O/L English Writing Guide",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/155eu00n0_0IdrKc0wiWDqcwkqLa08ndI/preview",
    fileName = "English_Writing_Essay_Letter_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val sinhalaWritingNoteGr10_11 = ShortNoteItem(
    id = "sinhala_writing_note_gr10_11",
    subject = "සිංහල",
    title = "10 සහ 11 ශ්‍රේණි - සිංහල රචනා, නිර්මාණාත්මක ලිවීම් හා විචාර කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණි සිංහල රචනා හා විචාර",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/17jFpMgfgojJgdAT0K4Ja9dfDocoBAnsl/preview",
    fileName = "Sinhala_Essay_Writing_Guide_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val artNoteGr10_11 = ShortNoteItem(
    id = "art_note_gr10_11",
    subject = "චිත්‍ර කලාව",
    title = "10 සහ 11 ශ්‍රේණි - චිත්‍ර කලාව හා මූර්ති කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණි චිත්‍ර කලාව",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/12GBk7Eg8H558fgpOPGwFSqGskwUXyMfK/preview",
    fileName = "Art_Sculpture_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val dramaNoteGr10_11 = ShortNoteItem(
    id = "drama_note_gr10_11",
    subject = "නාට්‍ය හා රංග කලාව",
    title = "10 සහ 11 ශ්‍රේණි - නාට්‍ය හා රංග කලාව කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණි නාට්‍ය හා රංග කලාව",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/16z-qVWM6nwPErhsYoT5L0WOL3-jsEoDv/preview",
    fileName = "Drama_Theatre_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val danceNote = ShortNoteItem(
    id = "dance_note_gr10_11",
    subject = "නර්තනය",
    title = "10 සහ 11 ශ්‍රේණිය - නර්තනය කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය නර්තනය කෙටි සටහන්",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/12GBk7Eg8H558fgpOPGwFSqGskwUXyMfK/preview",
    fileName = "Dancing_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val musicNoteGr10_11_1 = ShortNoteItem(
    id = "music_note_gr10_11_1",
    subject = "සංගීතය",
    title = "10 ශ්‍රේණිය හා 11 ශ්‍රේණිය - පෙරදිග සංගීතය කෙටි සටහන් 1",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය පෙරදිග සංගීතය කෙටි සටහන් 1",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/16z-qVWM6nwPErhsYoT5L0WOL3-jsEoDv/preview",
    fileName = "Oriental_Music_Short_Notes_1_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val musicNoteGr10_11_2 = ShortNoteItem(
    id = "music_note_gr10_11_2",
    subject = "සංගීතය",
    title = "10 ශ්‍රේණිය හා 11 ශ්‍රේණිය - පෙරදිග සංගීතය කෙටි සටහන්",
    topicSinhala = "10 සහ 11 ශ්‍රේණිය පෙරදිග සංගීතය කෙටි සටහන් සංග්‍රහය",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1BYhGyyvqcVfQP7YYgWynzV_oZ0coLjbL/preview",
    fileName = "Oriental_Music_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val sinhalaGrammarNote = ShortNoteItem(
    id = "sinhala_grammar_note_all_grades",
    subject = "සිංහල",
    title = "06-11 ශ්‍රේණි - සිංහල ව්‍යාකරණ කෙටි සටහන්",
    topicSinhala = "06/07/08/09/10/11 ශ්‍රේණි සිංහල ව්‍යාකරණ කෙටි සටහන්",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1XB8up4GLB9mvcavvtVsbOaAZAO1027tc/preview",
    fileName = "Sinhala_Grammar_Short_Notes_Gr06_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val sinhalaLitNoteGr10_11 = ShortNoteItem(
    id = "sinhala_lit_note_gr10_11",
    subject = "සිංහල",
    title = "10/11 ශ්‍රේණි - සිංහල සාහිත්‍යය කෙටි සටහන්",
    topicSinhala = "10/11 ශ්‍රේණි සිංහල සාහිත්‍යය කෙටි සටහන්",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/17jFpMgfgojJgdAT0K4Ja9dfDocoBAnsl/preview",
    fileName = "Sinhala_Literature_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val historyMapsNoteAllGrades = ShortNoteItem(
    id = "history_maps_note_all_grades",
    subject = "ඉතිහාසය",
    title = "06-11 ශ්‍රේණි - ඉතිහාසය සිතියම් ලකුණු කිරීම",
    topicSinhala = "06/07/08/09/10/11 ශ්‍රේණි ඉතිහාසය සිතියම්",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/1BVguuBjT1_iQVO296Zn4Dek2AOahBFsp/preview",
    fileName = "History_Maps_Gr06_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val historyTablesNoteGr10_11 = ShortNoteItem(
    id = "history_tables_note_gr10_11",
    subject = "ඉතිහාසය",
    title = "10 හා 11 ශ්‍රේණි - ඉතිහාසය වගු කෙටි සටහන්",
    topicSinhala = "10 හා 11 ශ්‍රේණිය ඉතිහාසය වගු කෙටි සටහන්",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/13ctYgSQ0jefoMGg3cpJg2t74h0jIZyx7/preview",
    fileName = "History_Tables_Short_Notes_Gr10_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  val englishShortNoteAllGrades = ShortNoteItem(
    id = "english_short_note_all_grades",
    subject = "ඉංග්‍රීසි",
    title = "06-11 ශ්‍රේණි - ඉංග්‍රීසි ව්‍යාකරණ හා කෙටි සටහන්",
    topicSinhala = "06/07/08/09/10/11 ශ්‍රේණි ඉංග්‍රීසි කෙටි සටහන්",
    readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
    isPopular = true,
    pdfUri = "https://drive.google.com/file/d/155eu00n0_0IdrKc0wiWDqcwkqLa08ndI/preview",
    fileName = "English_Grammar_Short_Notes_Gr06_11.pdf",
    isPasswordProtected = false,
    password = null
  )

  if (grade == "11") {
    val scienceNoteGr11 = ShortNoteItem(
      id = "science_note_gr11_1",
      subject = "විද්‍යාව",
      title = "11 ශ්‍රේණිය - විද්‍යාව පූර්ණ කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය විද්‍යාව කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/17TcFs1wECaHB4C3LMdC8mO2YDKrtrEOI/preview",
      fileName = "Science_Short_Notes_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    val buddhismNote = ShortNoteItem(
      id = "buddhism_note_gr11_1",
      subject = "බුද්ධ ධර්මය",
      title = "11 ශ්‍රේණිය - බුද්ධ ධර්මය කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය බුද්ධ ධර්මය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1cG0Fj4Eg86hbvyx0OAnY1ffB-cvnwOLO/preview",
      fileName = "Buddhism_Short_Notes_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    val healthNote = ShortNoteItem(
      id = "health_note_gr11_1",
      subject = "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය",
      title = "11 ශ්‍රේණිය - සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/15x4Awk2VNyL2gpegVHblAtPb52lbiAUX/preview",
      fileName = "Health_Physical_Education_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    val historyNote = ShortNoteItem(
      id = "history_note_gr11_1",
      subject = "ඉතිහාසය",
      title = "11 ශ්‍රේණිය - ඉතිහාසය කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය ඉතිහාසය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview",
      fileName = "History_Short_Notes_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    val agriNoteGr11 = ShortNoteItem(
      id = "agri_note_gr11_1",
      subject = "කෘෂි හා ආහාර තාක්ෂණය",
      title = "11 ශ්‍රේණිය - කෘෂි හා ආහාර තාක්ෂණය කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය කෘෂි හා ආහාර තාක්ෂණය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1EjYUgrj-r_Uw9jMUlJPQh8a0Tl4oiBjb/preview",
      fileName = "Agri_Food_Tech_Short_Notes_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    val geoNoteGr11 = ShortNoteItem(
      id = "geo_note_gr11_1",
      subject = "භූගෝල විද්‍යාව",
      title = "11 ශ්‍රේණිය - භූගෝල විද්‍යාව කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය භූගෝල විද්‍යාව කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1dfKR5Cb8ZAE07E_3ddeYc1EDfXWOALHO/preview",
      fileName = "Geography_Short_Notes_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    val civicNoteGr11 = ShortNoteItem(
      id = "civic_note_gr11_1",
      subject = "පුරවැසි අධ්‍යාපනය",
      title = "11 ශ්‍රේණිය - පුරවැසි අධ්‍යාපනය කෙටි සටහන්",
      topicSinhala = "11 ශ්‍රේණිය පුරවැසි අධ්‍යාපනය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1kuL7jmciw_ZKLK4JYz8WV1lbSeeWOf08/preview",
      fileName = "Civic_Education_Short_Notes_Gr11.pdf",
      isPasswordProtected = false,
      password = null
    )
    return listOf(
      sinhalaGrammarNote, sinhalaLitNoteGr10_11, sinhalaWritingNoteGr10_11,
      englishShortNoteAllGrades, englishWritingNoteGr10_11,
      mathGeometryNote, mathAlgebraNoteGr10_11, mathStatsFormulaNoteGr10_11,
      scienceNoteGr11, scienceChemistryNoteGr10_11, sciencePhysicsNoteGr10_11,
      historyNote, historyMapsNoteAllGrades, historyTablesNoteGr10_11,
      buddhismNote, geoNoteGr11, civicNoteGr11, commerceNote, ictNote,
      agriNoteGr11, healthNote, danceNote, musicNoteGr10_11_1, musicNoteGr10_11_2,
      artNoteGr10_11, dramaNoteGr10_11
    )
  } else if (grade == "10") {
    val scienceNoteGr10 = ShortNoteItem(
      id = "science_note_gr10_1",
      subject = "විද්‍යාව",
      title = "10 ශ්‍රේණිය - විද්‍යාව පූර්ණ කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය විද්‍යාව කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1vx9uXTL_pKexaA5g0IHPa47h6eKdINZl/preview",
      fileName = "Science_Short_Notes_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    val buddhismNoteGr10 = ShortNoteItem(
      id = "buddhism_note_gr10_1",
      subject = "බුද්ධ ධර්මය",
      title = "10 ශ්‍රේණිය - බුද්ධ ධර්මය කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය බුද්ධ ධර්මය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/17O97RA-IbgZnpKt9cVynjoNZ7y5SCE-Q/preview",
      fileName = "Buddhism_Short_Notes_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    val civicNoteGr10 = ShortNoteItem(
      id = "civic_note_gr10_1",
      subject = "පුරවැසි අධ්‍යාපනය",
      title = "10 ශ්‍රේණිය - පුරවැසි අධ්‍යාපනය කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය පුරවැසි අධ්‍යාපනය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1-H0WHiCYob1T4kQ9Sol4n6SQSJZc9LEX/preview",
      fileName = "Civic_Education_Short_Notes_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    val geoNoteGr10 = ShortNoteItem(
      id = "geo_note_gr10_1",
      subject = "භූගෝල විද්‍යාව",
      title = "10 ශ්‍රේණිය - භූගෝල විද්‍යාව කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය භූගෝල විද්‍යාව කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1uKVJN3GsKephOV9In73EBW7R1bSSe47Z/preview",
      fileName = "Geography_Short_Notes_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    val agriNote = ShortNoteItem(
      id = "agri_note_gr10_1",
      subject = "කෘෂි හා ආහාර තාක්ෂණය",
      title = "10 ශ්‍රේණිය - කෘෂි හා ආහාර තාක්ෂණය කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය කෘෂි හා ආහාර තාක්ෂණය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1zSkjgp24A2wvjrKAlX_EDt3KMvRtk7xp/preview",
      fileName = "Agri_Food_Tech_Short_Notes_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    val healthNoteGr10 = ShortNoteItem(
      id = "health_note_gr10_1",
      subject = "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය",
      title = "10 ශ්‍රේණිය - සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1qEPI5g8KYCmX__5fzHSodtFoLUvj0BWi/preview",
      fileName = "Health_Physical_Education_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    val historyNoteGr10 = ShortNoteItem(
      id = "history_note_gr10_1",
      subject = "ඉතිහාසය",
      title = "10 ශ්‍රේණිය - ඉතිහාසය කෙටි සටහන්",
      topicSinhala = "10 ශ්‍රේණිය ඉතිහාසය කෙටි සටහන්",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1Ry6utaFim_tZl8OkTG5hoD6oB4RB8Uxl/preview",
      fileName = "History_Short_Notes_Gr10.pdf",
      isPasswordProtected = false,
      password = null
    )
    return listOf(
      sinhalaGrammarNote, sinhalaLitNoteGr10_11, sinhalaWritingNoteGr10_11,
      englishShortNoteAllGrades, englishWritingNoteGr10_11,
      mathGeometryNote, mathAlgebraNoteGr10_11, mathStatsFormulaNoteGr10_11,
      scienceNoteGr10, scienceChemistryNoteGr10_11, sciencePhysicsNoteGr10_11,
      historyNoteGr10, historyMapsNoteAllGrades, historyTablesNoteGr10_11,
      buddhismNoteGr10, geoNoteGr10, civicNoteGr10, commerceNote, ictNote,
      agriNote, healthNoteGr10, danceNote, musicNoteGr10_11_1, musicNoteGr10_11_2,
      artNoteGr10_11, dramaNoteGr10_11
    )
  } else {
    // Grades 06, 07, 08, 09 (Junior Secondary Complete Syllabus Notes)
    val sciNote = ShortNoteItem(
      id = "sci_note_gr${grade}_all",
      subject = "විද්‍යාව",
      title = "$grade ශ්‍රේණිය - විද්‍යාව පූර්ණ සිලබස් කෙටි සටහන් & ප්‍රස්ථාර",
      topicSinhala = "$grade ශ්‍රේණිය විද්‍යාව සම්පූර්ණ විෂය නිර්දේශය",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview",
      fileName = "Science_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val mathNote = ShortNoteItem(
      id = "math_note_gr${grade}_all",
      subject = "ගණිතය",
      title = "$grade ශ්‍රේණිය - ගණිතය සියලු සූත්‍ර, ජ්‍යාමිතිය & කෙටි ක්‍රම",
      topicSinhala = "$grade ශ්‍රේණිය ගණිතය මූලධර්ම හා සූත්‍ර",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview",
      fileName = "Math_Formulas_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val histNote = ShortNoteItem(
      id = "hist_note_gr${grade}_all",
      subject = "ඉතිහාසය",
      title = "$grade ශ්‍රේණිය - ඉතිහාසය යුග හා මූලාශ්‍ර කෙටි සටහන්",
      topicSinhala = "$grade ශ්‍රේණිය ඉතිහාසය පාඩම් මාලාව",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview",
      fileName = "History_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val buddhismNoteGr = ShortNoteItem(
      id = "bud_note_gr${grade}_all",
      subject = "බුද්ධ ධර්මය",
      title = "$grade ශ්‍රේණිය - බුද්ධ ධර්මය හා ශාසන ඉතිහාසය කෙටි සටහන්",
      topicSinhala = "$grade ශ්‍රේණිය බුද්ධ ධර්මය දහම් කරුණු",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1cG0Fj4Eg86hbvyx0OAnY1ffB-cvnwOLO/preview",
      fileName = "Buddhism_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val sinhalaWritingGr = ShortNoteItem(
      id = "sinhala_lit_gr${grade}_all",
      subject = "සිංහල",
      title = "$grade ශ්‍රේණිය - සිංහල සාහිත්‍යය, රචනා & පද්‍ය විචාර",
      topicSinhala = "$grade ශ්‍රේණිය සිංහල සාහිත්‍යය හා ලේඛනය",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/17jFpMgfgojJgdAT0K4Ja9dfDocoBAnsl/preview",
      fileName = "Sinhala_Literature_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val englishWritingGr = ShortNoteItem(
      id = "english_writing_gr${grade}_all",
      subject = "ඉංග්‍රීසි",
      title = "$grade ශ්‍රේණිය - English Tenses, Vocabulary & Essay Pack",
      topicSinhala = "$grade ශ්‍රේණිය ඉංග්‍රීසි Writing & Vocabulary",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/155eu00n0_0IdrKc0wiWDqcwkqLa08ndI/preview",
      fileName = "English_Writing_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val geoNoteGr = ShortNoteItem(
      id = "geo_note_gr${grade}_all",
      subject = "භූගෝල විද්‍යාව",
      title = "$grade ශ්‍රේණිය - භූගෝල විද්‍යාව & සිතියම් සටහන්",
      topicSinhala = "$grade ශ්‍රේණිය භූගෝල විද්‍යාව",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1dfKR5Cb8ZAE07E_3ddeYc1EDfXWOALHO/preview",
      fileName = "Geography_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val civicNoteGr = ShortNoteItem(
      id = "civic_note_gr${grade}_all",
      subject = "පුරවැසි අධ්‍යාපනය",
      title = "$grade ශ්‍රේණිය - පුරවැසි අධ්‍යාපනය & ප්‍රජාතන්ත්‍රවාදය",
      topicSinhala = "$grade ශ්‍රේණිය පුරවැසි අධ්‍යාපනය",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1kuL7jmciw_ZKLK4JYz8WV1lbSeeWOf08/preview",
      fileName = "Civic_Education_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val healthNoteGr = ShortNoteItem(
      id = "health_note_gr${grade}_all",
      subject = "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය",
      title = "$grade ශ්‍රේණිය - සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය කෙටි සටහන්",
      topicSinhala = "$grade ශ්‍රේණිය සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/15x4Awk2VNyL2gpegVHblAtPb52lbiAUX/preview",
      fileName = "Health_Physical_Ed_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val ictNoteGr = ShortNoteItem(
      id = "ict_note_gr${grade}_all",
      subject = "තොරතුරු තාක්ෂණය",
      title = "$grade ශ්‍රේණිය - තොරතුරු තාක්ෂණය (ICT) කෙටි සටහන්",
      topicSinhala = "$grade ශ්‍රේණිය තොරතුරු තාක්ෂණය",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1kG3qI3N281y9OqV36X0x_uL78n0kP2M_/preview",
      fileName = "ICT_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )
    val ptsNoteGr = ShortNoteItem(
      id = "pts_note_gr${grade}_all",
      subject = "ප්‍රායෝගික හා තාක්ෂණික කුසලතා",
      title = "$grade ශ්‍රේණිය - ප්‍රායෝගික හා තාක්ෂණික කුසලතා (PTS) කෙටි සටහන්",
      topicSinhala = "$grade ශ්‍රේණිය තාක්ෂණික කුසලතා (PTS)",
      readTime = "🔒 ආරක්ෂිත PDF • Google Drive",
      isPopular = true,
      pdfUri = "https://drive.google.com/file/d/1EjYUgrj-r_Uw9jMUlJPQh8a0Tl4oiBjb/preview",
      fileName = "PTS_Short_Notes_Grade_$grade.pdf",
      isPasswordProtected = false,
      password = null
    )

    return listOf(
      sciNote, mathNote, histNote, sinhalaGrammarNote, sinhalaWritingGr,
      englishShortNoteAllGrades, englishWritingGr, buddhismNoteGr,
      geoNoteGr, civicNoteGr, healthNoteGr, ictNoteGr, ptsNoteGr,
      historyMapsNoteAllGrades
    )
  }
}

fun getPapersForGrade(grade: String): List<QuestionPaperItem> {
  if (grade == "10" || grade == "11") {
    val biologyBioPaper = QuestionPaperItem(
      id = "bio_paper_1",
      subject = "විද්‍යාව",
      titleSinhala = "10 සහ 11 ශ්‍රේණිය ජීව විද්‍යාව විභාග ප්‍රශ්න",
      year = "2025/2026",
      term = "වාර විභාග",
      marks = "🔒 මුරපද ආරක්ෂිතයි (1234)",
      pdfUri = "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview",
      fileName = "Biology_Exam_Questions_Gr10_11.pdf",
      isPasswordProtected = true,
      password = "1234"
    )
    return listOf(biologyBioPaper)
  }
  return emptyList()
}

fun getVideosForGrade(grade: String): List<VideoLessonItem> {
  return emptyList()
}

@Composable
fun GradeNotApprovedAlertModal(
  targetGrade: String,
  userRequestedPackage: String,
  onDismiss: () -> Unit,
  onRequestApproval: () -> Unit
) {
  val context = LocalContext.current
  val whatsappUrl = "https://wa.me/94757302321?text=Hello,%20I%20would%20like%20to%20request%20Admin%20Approval%20for%20Grade%20$targetGrade%20PDF%20Access."

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(CircleShape)
          .background(Color(0xFFFEE2E2)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Grade Restricted",
          tint = Color(0xFFDC2626),
          modifier = Modifier.size(28.dp)
        )
      }
    },
    title = {
      Text(
        text = "🔒 $targetGrade ශ්‍රේණියේ PDF සීමා කර ඇත",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF1E293B),
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "මෙම PDF අන්තර්ගතය අයත් වන්නේ $targetGrade ශ්‍රේණියටයි. ඔබගේ ගිණුමට දැනට අනුමැතිය ලැබී ඇත්තේ '$userRequestedPackage' සඳහා පමණි.",
          fontSize = 12.sp,
          color = Color(0xFF475569),
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFEFF6FF),
          border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Shield, contentDescription = "Features Hub Open", tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "විශේෂාංග කලාපය ඔබට විවෘතයි!",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D4ED8)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "විශේෂාංග කලාපයේ ඇති සියලුම මෙවලම් ඕනෑම වසරක අනුමැතියක් සහිතව පරිශීලනය කළ හැක. $targetGrade ශ්‍රේණියේ කෙටි සටහන් හා ප්‍රශ්න පත්‍ර (PDF) විවෘත කරගැනීමට ඇඩ්මින් අනුමැතිය ලබාගන්න.",
              fontSize = 10.sp,
              color = Color(0xFF1E40AF),
              lineHeight = 14.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onDismiss()
          onRequestApproval()
        },
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.WorkspacePremium, contentDescription = "Upgrade", modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("අනුමැතිය ලබාගන්න (Request)", fontSize = 12.sp)
      }
    },
    dismissButton = {
      Row {
        TextButton(
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
              context.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(context, "0757302321 අංකයට WhatsApp කරන්න", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("WhatsApp", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onDismiss) {
          Text("වසන්න", fontSize = 11.sp)
        }
      }
    }
  )
}

@Composable
fun UnlimitedAccessPaymentDialog(
  initialPackage: String = "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)",
  onDismiss: () -> Unit,
  onOpenLogin: () -> Unit,
  onSubmitReceipt: (name: String, phone: String, requestedPackage: String, receiptUri: Uri?) -> Unit
) {
  val context = LocalContext.current
  var studentName by remember { mutableStateOf("") }
  var studentPhone by remember { mutableStateOf("") }
  var selectedSlipUri by remember { mutableStateOf<Uri?>(null) }
  var selectedSlipName by remember { mutableStateOf<String?>(null) }
  var selectedPackage by remember { mutableStateOf(initialPackage) }

  val gradePackages = listOf(
    "10 සහ 11 ශ්‍රේණි (O/L Combo Pack)" to "⭐ 10 සහ 11 ශ්‍රේණි දෙකම එකවර (O/L Combo)",
    "06 ශ්‍රේණිය (Grade 6 Single Pack)" to "06 ශ්‍රේණිය පමණක් (Grade 6)",
    "07 ශ්‍රේණිය (Grade 7 Single Pack)" to "07 ශ්‍රේණිය පමණක් (Grade 7)",
    "08 ශ්‍රේණිය (Grade 8 Single Pack)" to "08 ශ්‍රේණිය පමණක් (Grade 8)",
    "09 ශ්‍රේණිය (Grade 9 Single Pack)" to "09 ශ්‍රේණිය පමණක් (Grade 9)",
    "06 සිට 11 දක්වා සියලුම ශ්‍රේණි (All Grades Mega Pack)" to "👑 06 සිට 11 දක්වා සියලුම ශ්‍රේණි (Mega Pack)"
  )

  val slipPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedSlipUri = uri
      selectedSlipName = getFileNameFromUri(context, uri)
      Toast.makeText(context, "රිසිට්පත තෝරාගන්නා ලදී: $selectedSlipName", Toast.LENGTH_SHORT).show()
    }
  }

  val whatsappUrl = "https://wa.me/94757302321?text=Hello,%20I%20have%20paid%20for%20$selectedPackage%20Unlimited%20Access.%20Here%20is%20my%20payment%20receipt."

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, NeutralBorderLight),
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Header with Gold Badge and Close Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFFF8E1),
              border = BorderStroke(1.dp, Color(0xFFFFD54F))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.WorkspacePremium,
                  contentDescription = "Premium",
                  tint = Color(0xFFE65100),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "වාර්ෂික සාමාජිකත්වය",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE65100)
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = NeutralMedium)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 1. Title
          Text(
            text = "Unlimited Access අනුමැතිය ලබාගැනීම",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            fontSize = 17.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // 2. Grade Package Selection UI
          Text(
            text = "🎓 ඔබ අනුමැතිය ඉල්ලා සිටින ශ්‍රේණිය තෝරන්න:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = NeutralDark,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            gradePackages.forEach { (pkgKey, pkgLabel) ->
              val isSelected = selectedPackage == pkgKey
              Surface(
                onClick = { selectedPackage = pkgKey },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                border = BorderStroke(1.5.dp, if (isSelected) BluePrimary else NeutralBorderLight),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(18.dp)
                      .clip(CircleShape)
                      .background(if (isSelected) BluePrimary else Color.Transparent)
                      .border(2.dp, if (isSelected) BluePrimary else NeutralMedium, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isSelected) {
                      Box(
                        modifier = Modifier
                          .size(8.dp)
                          .clip(CircleShape)
                          .background(Color.White)
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = pkgLabel,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) BluePrimary else NeutralDark
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 3. Message Text
          Text(
            text = "අනුමැතිය සඳහා එක් වරක් පමණක් අය කෙරෙන, රු. 1,000 ක වාර්ෂික ගෙවීම සිදු කර ඔබ තෝරාගත් ශ්‍රේණිවල කෙටි සටහන් හා ප්‍රශ්න පත්‍ර (PDF) UNLIMITED පරිශීලනය කරන්න.\n\nපහත සඳහන් බැංකු ගිණුමට මුදල් තැන්පත් කර, ලබාගන්නා රිසිට්පත (Bank Slip / Screenshot) පහත Upload Button එක හරහා හෝ WhatsApp හරහා අප වෙත යොමු කරන්න.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = Color(0xFF334155),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(12.dp))

          // 4. Bank Details Card (Styled nicely inside the modal)
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF0F7FF),
            border = BorderStroke(1.5.dp, Color(0xFF90CAF9)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(14.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AccountBalance,
                  contentDescription = "Bank",
                  tint = BluePrimary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "බැංකු ගිණුම් විස්තර (Bank Details)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = BluePrimary
                )
              }

              HorizontalDivider(color = Color(0xFFBBDEFB), modifier = Modifier.padding(bottom = 8.dp))

              Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("බැංකුව (Bank):", fontSize = 12.sp, color = NeutralMedium, fontWeight = FontWeight.Medium)
                Text("ලංකා බැංකුව (Bank of Ceylon)", fontSize = 12.sp, color = NeutralDark, fontWeight = FontWeight.Bold)
              }

              Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ශාඛාව (Branch):", fontSize = 12.sp, color = NeutralMedium, fontWeight = FontWeight.Medium)
                Text("අඹන්පොල (Ambanpola)", fontSize = 12.sp, color = NeutralDark, fontWeight = FontWeight.Bold)
              }

              Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("නම (Account Name):", fontSize = 12.sp, color = NeutralMedium, fontWeight = FontWeight.Medium)
                Text("D.H.M A P DISANAYAKA", fontSize = 12.sp, color = NeutralDark, fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(6.dp))

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFF64B5F6)),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text("ගිණුම් අංකය (Account Number):", fontSize = 10.sp, color = NeutralMedium)
                    Text("90313771", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)
                  }

                  TextButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("Account Number", "90313771")
                      clipboard.setPrimaryClip(clip)
                      Toast.makeText(context, "ගිණුම් අංකය Copy විය: 90313771", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = BluePrimary)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 5. Two Payment Verification Options
          Text(
            text = "තහවුරු කිරීමේ ක්‍රම (Payment Verification Options):",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = NeutralDark,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Option A: Direct Slip Upload Button Card
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = BluePrimary,
                  modifier = Modifier.size(20.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text("A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ක්‍රමය 1: රිසිට්පත Upload කර අනුමැතිය ලබාගන්න",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = NeutralDark
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              androidx.compose.material3.OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("ඔබගේ සම්පූර්ණ නම (Your Name)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(8.dp))

              androidx.compose.material3.OutlinedTextField(
                value = studentPhone,
                onValueChange = { studentPhone = it },
                label = { Text("දුරකථන අංකය (Phone Number)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(10.dp))

              if (selectedSlipUri != null) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFE8F5E9),
                  border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                  modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = selectedSlipName ?: "රිසිට්පත තෝරා ඇත",
                        fontSize = 11.sp,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                    TextButton(onClick = { slipPickerLauncher.launch("image/*") }) {
                      Text("Change", fontSize = 11.sp)
                    }
                  }
                }
              }

              Button(
                onClick = {
                  if (selectedSlipUri == null) {
                    slipPickerLauncher.launch("image/*")
                  } else {
                    if (studentName.isBlank() || studentPhone.isBlank()) {
                      Toast.makeText(context, "කරුණාකර ඔබගේ නම සහ දුරකථන අංකය ඇතුළත් කරන්න", Toast.LENGTH_SHORT).show()
                    } else {
                      onSubmitReceipt(studentName, studentPhone, selectedPackage, selectedSlipUri)
                      Toast.makeText(context, "රිසිට්පත සාර්ථකව යොමු කරන ලදී! ඇඩ්මින් අනුමැතියෙන් පසු ඔබට Unlimited Access හිමිවේ.", Toast.LENGTH_LONG).show()
                      onDismiss()
                    }
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedSlipUri != null) Color(0xFF137333) else BluePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
              ) {
                Icon(
                  imageVector = if (selectedSlipUri != null) Icons.Default.CloudUpload else Icons.Default.UploadFile,
                  contentDescription = "Upload"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (selectedSlipUri != null) "රිසිට්පත සහ අනුමැති ඉල්ලීම යොමු කරන්න" else "Upload Payment Receipt / රිසිට්පත Upload කරන්න",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Option B: WhatsApp Direct Button Card
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF0FDF4),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = Color(0xFF25D366),
                  modifier = Modifier.size(20.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text("B", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ක්‍රමය 2: WhatsApp හරහා රිසිට්පත යොමු කරන්න",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color(0xFF14532D)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "WhatsApp විවෘත කිරීමට නොහැකි විය: 0757302321 අංකයට යොමු කරන්න", Toast.LENGTH_LONG).show()
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
              ) {
                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("WhatsApp හරහා රිසිට්පත යවන්න", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // 6. Contact Support Note & Footnote
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F5F9),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/94757302321"))
                    context.startActivity(intent)
                  } catch (e: Exception) {}
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Phone, contentDescription = "Support", tint = BluePrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "වැඩිදුර සහය සඳහා වට්සැප් හරහා සම්බන්ධ වෙන්න : 0757302321",
                fontSize = 11.sp,
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Medium
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          TextButton(
            onClick = {
              onDismiss()
              onOpenLogin()
            }
          ) {
            Text("දැනටමත් අනුමත ගිණුමක් තිබේද? ලොගින් වන්න / Admin ප්‍රවේශය", fontSize = 11.sp, color = BluePrimary)
          }
        }
      }
    }
  }
}

@Composable
fun ReceiptPreviewDialog(
  receiptUriString: String,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, NeutralBorderLight),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ReceiptLong, contentDescription = "Receipt", tint = BluePrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text("ගෙවීම් රිසිට්පත (Bank Deposit Slip)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(360.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
          ) {
            AsyncImage(
              model = receiptUriString,
              contentDescription = "Payment Receipt",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Fit
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Close Preview")
          }
        }
      }
    }
  }
}

@Composable
fun LoginAndApprovalDialog(
  registeredUsers: SnapshotStateList<UserAccount>,
  onLoginSuccess: (UserAccount) -> Unit,
  onAdminLoginSuccess: () -> Unit,
  onDismiss: () -> Unit,
  onUploadPdfToFirebase: (title: String, grade: String, category: String, subject: String, uri: Uri?, fileName: String?) -> Unit = { _, _, _, _, _, _ -> },
  onPickPdf: () -> Unit = {},
  selectedPdfFileName: String? = null,
  selectedPdfUri: Uri? = null,
  initialTab: Int = 0,
  onPreviewReceipt: (String) -> Unit = {}
) {
  var selectedTab by remember { mutableStateOf(initialTab) } // 0: Login, 1: Register, 2: Admin

  // Login Form State
  var loginPhone by remember { mutableStateOf("0771234567") }
  var loginPassword by remember { mutableStateOf("1234") }
  var showLoginPassword by remember { mutableStateOf(false) }

  // Register Form State
  var regName by remember { mutableStateOf("") }
  var regPhone by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("1234") }
  var autoApproveNewUser by remember { mutableStateOf(true) }
  var showRegSuccessDialog by remember { mutableStateOf(false) }

  // Admin Form State
  var adminPasscode by remember { mutableStateOf("1234") }
  var isAdminUnlocked by remember { mutableStateOf(true) }

  // Admin Firebase PDF Upload State
  var adminPdfTitle by remember { mutableStateOf("") }
  var adminPdfGrade by remember { mutableStateOf("11") }
  var adminPdfCategory by remember { mutableStateOf("NOTE") } // "NOTE" or "PAPER"
  var adminPdfSubject by remember { mutableStateOf("විද්‍යාව") }

  val context = LocalContext.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, NeutralBorderLight),
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Dialog Close Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(BluePrimaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Lock",
                  tint = BluePrimary,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "🔒 ඇඩ්මින් අනුමැතිය (Admin Approval)",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = NeutralDark
                )
                Text(
                  text = "PDF නැරඹීමට ලියාපදිංචි වී අනුමැතිය ලබාගන්න",
                  fontSize = 10.sp,
                  color = NeutralMedium
                )
              }
            }

            IconButton(onClick = onDismiss) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = NeutralMedium
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

        // Tabs Header
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF1F5F9),
          modifier = Modifier.fillMaxWidth()
        ) {
          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF1F5F9),
            contentColor = BluePrimary,
            indicator = {}
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = { Text("ඇතුළුවීම", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = { Text("ලියාපදිංචිය", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
              selected = selectedTab == 2,
              onClick = { selectedTab = 2 },
              text = { Text("ඇඩ්මින්", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
          0 -> {
            // LOGIN FORM
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFEFF6FF),
              border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
              modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = "Quick Login", tint = BluePrimary, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("⚡ ක්ෂණික ප්‍රවේශය (1-Tap Fast Login)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BluePrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      val user = registeredUsers.firstOrNull { it.usernameOrPhone == "0771234567" || it.usernameOrPhone == "prabathakila450@gmail.com" } 
                        ?: registeredUsers.first()
                      onLoginSuccess(user.copy(isApproved = true))
                      Toast.makeText(context, "සාදරයෙන් පිළිගනිමු ${user.fullName}!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                  ) {
                    Text("🎓 ශිෂ්‍ය ලොගින්", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = {
                      onAdminLoginSuccess()
                      Toast.makeText(context, "👑 ඇඩ්මින් පාලනය සක්‍රීය විය!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                  ) {
                    Text("👑 ඇඩ්මින් ලොගින්", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            Text(
              text = "හෝ ඔබගේ විස්තර මඟින් ඇතුළු වන්න (Sign In)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = NeutralDark,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.material3.OutlinedTextField(
              value = loginPhone,
              onValueChange = { loginPhone = it },
              label = { Text("දුරකථන අංකය හෝ Email") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.material3.OutlinedTextField(
              value = loginPassword,
              onValueChange = { loginPassword = it },
              label = { Text("මුරපදය (Password)") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
              trailingIcon = {
                IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                  Icon(
                    imageVector = if (showLoginPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle password"
                  )
                }
              },
              visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                val cleanInput = loginPhone.trim()
                val cleanPass = loginPassword.trim()

                val foundUser = registeredUsers.find {
                  it.usernameOrPhone.trim().equals(cleanInput, ignoreCase = true) ||
                  it.fullName.trim().contains(cleanInput, ignoreCase = true)
                }

                if (foundUser != null) {
                  val approvedUser = foundUser.copy(isApproved = true)
                  val idx = registeredUsers.indexOf(foundUser)
                  if (idx >= 0) {
                    registeredUsers[idx] = approvedUser
                  }
                  onLoginSuccess(approvedUser)
                  Toast.makeText(context, "සාදරයෙන් පිළිගනිමු ${approvedUser.fullName}!", Toast.LENGTH_SHORT).show()
                } else if (cleanInput.isNotBlank()) {
                  // If it's a new phone number / user logging in with any details, auto-create & approve
                  val newAcc = UserAccount(
                    id = (registeredUsers.size + 1).toString(),
                    fullName = if (cleanInput.contains("@")) cleanInput.substringBefore("@") else "ශිෂ්‍ය ගිණුම ($cleanInput)",
                    usernameOrPhone = cleanInput,
                    password = cleanPass.ifBlank { "1234" },
                    isApproved = true,
                    paymentStatus = "Approved"
                  )
                  registeredUsers.add(newAcc)
                  onLoginSuccess(newAcc)
                  Toast.makeText(context, "සාදරයෙන් පිළිගනිමු ${newAcc.fullName}!", Toast.LENGTH_SHORT).show()
                } else {
                  Toast.makeText(context, "කරුණාකර දුරකථන අංකය හෝ විස්තර ඇතුළත් කරන්න", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
              Icon(Icons.Default.ExitToApp, contentDescription = "Login")
              Spacer(modifier = Modifier.width(8.dp))
              Text("ඇප් එකට ඇතුළු වන්න", fontWeight = FontWeight.Bold)
            }
          }
          1 -> {
            // REGISTER FORM
            Text(
              text = "නව ශිෂ්‍ය ලියාපදිංචිය (New Registration)",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = NeutralDark,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "ලියාපදිංචි වූ වහාම ඔබට සම්පූර්ණ අන්තර්ගතය නොමිලේ පරිශීලනය කළ හැක.",
              fontSize = 11.sp,
              color = NeutralMedium,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.OutlinedTextField(
              value = regName,
              onValueChange = { regName = it },
              label = { Text("සම්පූර්ණ නම (Full Name)") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.material3.OutlinedTextField(
              value = regPhone,
              onValueChange = { regPhone = it },
              label = { Text("දුරකථන අංකය (Phone Number)") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.material3.OutlinedTextField(
              value = regPassword,
              onValueChange = { regPassword = it },
              label = { Text("නව මුරපදය (Password)") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
              visualTransformation = PasswordVisualTransformation(),
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                if (regName.isBlank() || regPhone.isBlank()) {
                  Toast.makeText(context, "කරුණාකර සියලු විස්තර ඇතුළත් කරන්න", Toast.LENGTH_SHORT).show()
                } else {
                  val newAcc = UserAccount(
                    id = System.currentTimeMillis().toString(),
                    fullName = regName.trim(),
                    usernameOrPhone = regPhone.trim(),
                    password = regPassword.trim().ifBlank { "1234" },
                    isApproved = true,
                    paymentStatus = "Approved"
                  )
                  registeredUsers.add(newAcc)
                  onLoginSuccess(newAcc)
                  Toast.makeText(context, "ලියාපදිංචිය සාර්ථකයි! සාදරයෙන් පිළිගනිමු ${newAcc.fullName}", Toast.LENGTH_LONG).show()
                  regName = ""
                  regPhone = ""
                  regPassword = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
              Icon(Icons.Default.HowToReg, contentDescription = "Register")
              Spacer(modifier = Modifier.width(8.dp))
              Text("ලියාපදිංචි වී ඇතුළු වන්න", fontWeight = FontWeight.Bold)
            }
          }
          2 -> {
            // ADMIN APPROVAL PANEL
            if (!isAdminUnlocked) {
              Text(
                text = "ඇඩ්මින් ප්‍රවේශය (Admin PIN Lock)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeutralDark,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "පරිශීලකයින් අනුමත කිරීමට ඇඩ්මින් PIN අංකය ඇතුළත් කරන්න. (Default PIN: 1234)",
                fontSize = 11.sp,
                color = NeutralMedium
              )
              Spacer(modifier = Modifier.height(12.dp))

              androidx.compose.material3.OutlinedTextField(
                value = adminPasscode,
                onValueChange = { adminPasscode = it },
                label = { Text("ඇඩ්මින් PIN / Passcode") },
                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(16.dp))

              Button(
                onClick = {
                  if (adminPasscode.trim() == "1234" || adminPasscode.trim() == "admin") {
                    isAdminUnlocked = true
                    Toast.makeText(context, "ඇඩ්මින් පැනලය විවෘත විය!", Toast.LENGTH_SHORT).show()
                  } else {
                    Toast.makeText(context, "වැරදි ඇඩ්මින් PIN අංකයකි!", Toast.LENGTH_SHORT).show()
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB06000)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
              ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Unlock Admin")
                Spacer(modifier = Modifier.width(8.dp))
                Text("ඇඩ්මින් පැනලය විවෘත කරන්න", fontWeight = FontWeight.Bold)
              }
            } else {
              Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "👑 ඇඩ්මින් පරිශීලක අනුමැතිය",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeutralDark
                  )
                  Button(
                    onClick = { onAdminLoginSuccess() },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text("ඇප් එකට යන්න", fontSize = 11.sp)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val pendingUsers = registeredUsers.filter { !it.isApproved }
                val approvedUsers = registeredUsers.filter { it.isApproved }

                Text(
                  text = "අනුමැතිය බලපොරොත්තුවෙන් සිටින සිසුන් (${pendingUsers.size})",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (pendingUsers.isEmpty()) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "අනුමැතිය සඳහා කිසිදු ශිෂ්‍ය ගිණුමක් නැත.",
                      fontSize = 11.sp,
                      color = NeutralMedium,
                      modifier = Modifier.padding(12.dp)
                    )
                  }
                } else {
                  for (user in pendingUsers) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Color(0xFFFFF8E1),
                      border = BorderStroke(1.dp, Color(0xFFFFE082)),
                      modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                      Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                              Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                              Spacer(modifier = Modifier.width(6.dp))
                              Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (user.slipImageUri != null) Color(0xFFE8EAF6) else Color(0xFFFFECB3)
                              ) {
                                Text(
                                  text = if (user.slipImageUri != null) "🧾 රිසිට්පත ඇත" else "බැංකු රිසිට්පතක් නැත",
                                  fontSize = 9.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = if (user.slipImageUri != null) Color(0xFF283593) else Color(0xFFE65100),
                                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                              }
                            }
                            Text("දුරකථනය: ${user.usernameOrPhone}", fontSize = 11.sp, color = NeutralMedium)
                            
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                              shape = RoundedCornerShape(6.dp),
                              color = Color(0xFFE0F2FE),
                              border = BorderStroke(1.dp, Color(0xFF7DD3FC))
                            ) {
                              Text(
                                text = "🎓 ඉල්ලූ පැකේජය: ${user.requestedGradePackage}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                              )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("තත්ත්වය: ${user.paymentStatus}", fontSize = 10.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                          }
                          Row {
                            Button(
                              onClick = {
                                val idx = registeredUsers.indexOf(user)
                                if (idx != -1) {
                                  val gradesToAssign = getApprovedGradesForPackage(user.requestedGradePackage)
                                  registeredUsers[idx] = user.copy(
                                    isApproved = true,
                                    approvedGrades = gradesToAssign,
                                    paymentStatus = "Approved"
                                  )
                                }
                                Toast.makeText(context, "${user.fullName} (${user.requestedGradePackage}) අනුමත කරන ලදී!", Toast.LENGTH_SHORT).show()
                              },
                              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333)),
                              shape = RoundedCornerShape(8.dp),
                              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                              Text("Approve", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                              onClick = {
                                registeredUsers.remove(user)
                                Toast.makeText(context, "${user.fullName} ඉවත් කරන ලදී", Toast.LENGTH_SHORT).show()
                              }
                            ) {
                              Icon(Icons.Default.Delete, contentDescription = "Reject", tint = Color.Red, modifier = Modifier.size(18.dp))
                            }
                          }
                        }

                        if (user.slipImageUri != null) {
                          Spacer(modifier = Modifier.height(8.dp))
                          OutlinedButton(
                            onClick = { onPreviewReceipt(user.slipImageUri!!) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BluePrimary),
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                          ) {
                            Icon(Icons.Default.Visibility, contentDescription = "View", tint = BluePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🧾 රිසිට්පත පරීක්ෂා කරන්න (View Bank Slip)", fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                          }
                        }
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                  text = "අනුමත වූ ශිෂ්‍ය ගිණුම් (${approvedUsers.size})",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF137333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "ශ්‍රේණි චිප්ස් මත තට්ටු කර එක් එක් ශිෂ්‍යයාට අදාල ශ්‍රේණි පහසුවෙන්ම Add / Remove කළ හැක:",
                  fontSize = 10.sp,
                  color = NeutralMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                for (user in approvedUsers) {
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE6F4EA),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF137333), modifier = Modifier.size(18.dp))
                          Spacer(modifier = Modifier.width(8.dp))
                          Column {
                            Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(user.usernameOrPhone, fontSize = 10.sp, color = NeutralMedium)
                          }
                        }
                        IconButton(
                          onClick = {
                            val idx = registeredUsers.indexOf(user)
                            if (idx != -1) registeredUsers[idx] = user.copy(isApproved = false)
                            Toast.makeText(context, "${user.fullName} අනුමැතිය අවලංගු කරන ලදී", Toast.LENGTH_SHORT).show()
                          }
                        ) {
                          Icon(Icons.Default.Cancel, contentDescription = "Revoke", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                      }

                      Spacer(modifier = Modifier.height(6.dp))

                      // Grade Selector Chips for Admin
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        listOf("06", "07", "08", "09", "10", "11").forEach { gradeNum ->
                          val isGradeApproved = user.approvedGrades.contains(gradeNum)
                          Surface(
                            onClick = {
                              val idx = registeredUsers.indexOf(user)
                              if (idx != -1) {
                                val currentList = user.approvedGrades.toMutableList()
                                if (isGradeApproved) {
                                  currentList.remove(gradeNum)
                                } else {
                                  currentList.add(gradeNum)
                                }
                                registeredUsers[idx] = user.copy(approvedGrades = currentList)
                              }
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isGradeApproved) Color(0xFF1B5E20) else Color(0xFFE0E0E0),
                            border = BorderStroke(1.dp, if (isGradeApproved) Color(0xFF2E7D32) else Color(0xFFBDBDBD))
                          ) {
                            Text(
                              text = "$gradeNum වසර",
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (isGradeApproved) Color.White else Color(0xFF616161),
                              modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                            )
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(4.dp))

                      // Quick actions for O/L 10+11 combo and All Grades
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                      ) {
                        TextButton(
                          onClick = {
                            val idx = registeredUsers.indexOf(user)
                            if (idx != -1) {
                              registeredUsers[idx] = user.copy(approvedGrades = listOf("10", "11"))
                              Toast.makeText(context, "${user.fullName} හට 10 සහ 11 ශ්‍රේණි (O/L) ලබාදුනි", Toast.LENGTH_SHORT).show()
                            }
                          },
                          contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                          Text("⭐ 10+11 O/L", fontSize = 9.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        TextButton(
                          onClick = {
                            val idx = registeredUsers.indexOf(user)
                            if (idx != -1) {
                              registeredUsers[idx] = user.copy(approvedGrades = listOf("06", "07", "08", "09", "10", "11"))
                              Toast.makeText(context, "${user.fullName} හට සියලු ශ්‍රේණි ලබාදුනි", Toast.LENGTH_SHORT).show()
                            }
                          },
                          contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                          Text("👑 සියල්ල (06-11)", fontSize = 9.sp, color = Color(0xFF137333), fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = NeutralBorderLight)
                Spacer(modifier = Modifier.height(16.dp))

                // FIREBASE STORAGE & FIRESTORE PDF UPLOAD PANEL
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = Color(0xFFFFF1F0),
                  border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.CloudUpload, contentDescription = "Firebase Upload", tint = Color(0xFFC62828), modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "🔥 Firebase Storage & Firestore Upload",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFC62828)
                      )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Admin ලෙස PDF මාතෘකාව, ශ්‍රේණිය තෝරා Firebase Cloud Storage & Cloud Firestore වෙත Upload කරන්න.",
                      fontSize = 11.sp,
                      color = NeutralMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PDF Title Input
                    androidx.compose.material3.OutlinedTextField(
                      value = adminPdfTitle,
                      onValueChange = { adminPdfTitle = it },
                      label = { Text("PDF මාතෘකාව / නම (Title in Sinhala)") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grade Picker
                    Text("ශ්‍රේණිය (Grade) තෝරන්න:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      listOf("06", "07", "08", "09", "10", "11").forEach { g ->
                        FilterChip(
                          selected = adminPdfGrade == g,
                          onClick = { adminPdfGrade = g },
                          label = { Text("$g වසර", fontSize = 11.sp) }
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Picker
                    Text("අංශය / වර්ගය (Type):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      FilterChip(
                        selected = adminPdfCategory == "NOTE",
                        onClick = { adminPdfCategory = "NOTE" },
                        label = { Text("කෙටි සටහන් PDF", fontSize = 11.sp) }
                      )
                      FilterChip(
                        selected = adminPdfCategory == "PAPER",
                        onClick = { adminPdfCategory = "PAPER" },
                        label = { Text("ප්‍රශ්න පත්‍ර PDF", fontSize = 11.sp) }
                      )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subject Input
                    androidx.compose.material3.OutlinedTextField(
                      value = adminPdfSubject,
                      onValueChange = { adminPdfSubject = it },
                      label = { Text("විෂය (Subject Name)") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Attach PDF Button
                    Button(
                      onClick = { onPickPdf() },
                      colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BluePrimary),
                      border = BorderStroke(1.dp, BluePrimary),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Icon(Icons.Default.AttachFile, contentDescription = "Attach PDF", modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (selectedPdfFileName != null) "📎 $selectedPdfFileName" else "PDF ගොනුවක් තෝරන්න (Select PDF File)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Upload to Firebase Button
                    Button(
                      onClick = {
                        if (adminPdfTitle.isNotBlank()) {
                          onUploadPdfToFirebase(
                            adminPdfTitle,
                            adminPdfGrade,
                            adminPdfCategory,
                            adminPdfSubject,
                            selectedPdfUri,
                            selectedPdfFileName
                          )
                          Toast.makeText(
                            context,
                            "🔥 Firebase Cloud Storage (gs://studentportal-db.appspot.com/pdfs/${adminPdfGrade}) හා Cloud Firestore වෙත PDF සාර්ථකව උඩුගත (Upload) විය!",
                            Toast.LENGTH_LONG
                          ).show()
                          adminPdfTitle = ""
                        } else {
                          Toast.makeText(context, "කරුණාකර PDF මාතෘකාව ඇතුළත් කරන්න", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                      Icon(Icons.Default.CloudUpload, contentDescription = "Firebase Upload")
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Firebase Storage & Firestore Upload", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  }

  if (showRegSuccessDialog) {
    AlertDialog(
      onDismissRequest = { showRegSuccessDialog = false },
      title = {
        Text("ලියාපදිංචිය සාර්ථකයි!")
      },
      text = {
        Text("ඔබගේ ගිණුම සාර්ථකව සාදන ලදී. ඇඩ්මින්වරයා (Admin Approval) විසින් අනුමත කළ පසු ඔබට ඇප් එකට මුරපදය යොදා ප්‍රවේශ විය හැක.")
      },
      confirmButton = {
        Button(
          onClick = {
            showRegSuccessDialog = false
            selectedTab = 0
          }
        ) {
          Text("ලොගින් පිටුවට යන්න")
        }
      }
    )
  }
}
