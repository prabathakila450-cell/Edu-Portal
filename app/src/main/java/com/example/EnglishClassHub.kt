package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishMasterClassScreen(
  onBack: () -> Unit,
  onOpenGoogleDrivePdfModal: ((url: String, title: String) -> Unit)? = null
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  // State to track which sections are expanded (Only show notes when clicked)
  var expandedSection by remember { mutableStateOf<Int?>(null) }

  // Data sources from repository
  val vocabList = remember { EnglishBuilderRepository.getDailyVocab() }
  val templates = remember { EnglishBuilderRepository.getWritingTemplates() }
  val grammarLessons = remember { EnglishBuilderRepository.getGrammarLessons() }
  val clozeTests = remember { EnglishBuilderRepository.getClozeTests() }
  val readingPassages = remember { EnglishBuilderRepository.getReadingPassages() }

  val defaultEnglishDriveUrl = "https://drive.google.com/file/d/155eu00n0_0IdrKc0wiWDqcwkqLa08ndI/preview"
  val defaultEnglishNoteTitle = "06-11 ශ්‍රේණි ඉංග්‍රීසි පූර්ණ කෙටි සටහන් (Google Drive PDF)"

  var internalShowPdfModal by remember { mutableStateOf(false) }
  var internalPdfUrl by remember { mutableStateOf("") }
  var internalPdfTitle by remember { mutableStateOf("") }

  fun openDrivePdf(url: String = defaultEnglishDriveUrl, title: String = defaultEnglishNoteTitle) {
    if (onOpenGoogleDrivePdfModal != null) {
      onOpenGoogleDrivePdfModal(url, title)
    } else {
      internalPdfUrl = url
      internalPdfTitle = title
      internalShowPdfModal = true
    }
  }

  // TTS helper
  var tts by remember { mutableStateOf<TextToSpeech?>(null) }
  var isTtsReady by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    var engine: TextToSpeech? = null
    engine = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        engine?.language = Locale.ENGLISH
        isTtsReady = true
      }
    }
    tts = engine
    onDispose {
      engine?.stop()
      engine?.shutdown()
    }
  }

  fun speakEnglish(text: String, speechRate: Float = 1.0f) {
    tts?.setSpeechRate(speechRate)
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "english_class_tts")
  }

  // Voice Speech Recognition State
  val practiceSentences = remember {
    listOf(
      SpokenSentencePractice(
        "sp_1",
        "Good morning, my name is Kasun and I study in Grade 11.",
        "/ɡʊd ˈmɔːnɪŋ maɪ neɪm ɪz kəˈsuːn/",
        "සුබ උදෑසනක්, මගේ නම කසුන් සහ මම 11 ශ්‍රේණියේ ඉගෙනුම ලබමි.",
        "Beginner"
      ),
      SpokenSentencePractice(
        "sp_2",
        "Science and mathematics are my favorite subjects for the O/L examination.",
        "/ˈsaɪəns ænd ˌmæθəˈmætɪks ɑː maɪ ˈfeɪvərɪt ˈsʌbdʒɪkts/",
        "විද්‍යාව හා ගණිතය සාමාන්‍ය පෙළ විභාගය සඳහා මගේ ප්‍රියතම විෂයන් වේ.",
        "Intermediate"
      ),
      SpokenSentencePractice(
        "sp_3",
        "Protecting the natural environment is the responsibility of every citizen.",
        "/prəˈtɛktɪŋ ðə ˈnætʃrəl ɪnˈvaɪrənmənt/",
        "ස්වභාවික පරිසරය ආරක්ෂා කිරීම සෑම පුරවැසියෙකුගේම වගකීමකි.",
        "Advanced"
      ),
      SpokenSentencePractice(
        "sp_4",
        "Consistent practice and hard work lead to great success in life.",
        "/kənˈsɪstənt ˈpræktɪs ænd hɑːd wɜːk/",
        "නිරන්තර පුහුණුව සහ වෙහෙස මහන්සි වී වැඩ කිරීම ජීවිතයේ උසස් ජයග්‍රහණවලට මඟ පාදයි.",
        "Intermediate"
      ),
      SpokenSentencePractice(
        "sp_5",
        "Could you please explain this grammar lesson one more time?",
        "/kʊd juː pliːz ɪkˈspleɪn ðɪs ˈɡræmər ˈlɛsn/",
        "කරුණාකර මෙම ව්‍යාකරණ පාඩම තවත් එක් වරක් පැහැදිලි කළ හැකිද?",
        "Beginner"
      )
    )
  }

  var selectedPracticeIndex by remember { mutableStateOf(0) }
  val currentPracticeItem = practiceSentences[selectedPracticeIndex]
  var recognizedSpokenText by remember { mutableStateOf("") }
  var matchAccuracyPercentage by remember { mutableStateOf<Int?>(null) }
  var isListening by remember { mutableStateOf(false) }
  var speechStatusMessage by remember { mutableStateOf("මයික්‍රෆෝන් බොත්තම ඔබා ඉංග්‍රීසි වාක්‍යය ශබ්ද නඟා කියවන්න.") }

  fun calculateAccuracy(spoken: String, expected: String): Int {
    val cleanSpoken = spoken.lowercase().replace(Regex("[^a-z0-9 ]"), "").trim().split("\\s+".toRegex())
    val cleanExpected = expected.lowercase().replace(Regex("[^a-z0-9 ]"), "").trim().split("\\s+".toRegex())
    if (cleanExpected.isEmpty() || cleanSpoken.isEmpty()) return 0
    var matches = 0
    cleanExpected.forEach { word ->
      if (cleanSpoken.contains(word)) matches++
    }
    return ((matches.toFloat() / cleanExpected.size) * 100).toInt().coerceIn(0, 100)
  }

  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isListening = false
    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
      val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      if (!matches.isNullOrEmpty()) {
        val spoken = matches[0]
        recognizedSpokenText = spoken
        val accuracy = calculateAccuracy(spoken, currentPracticeItem.englishText)
        matchAccuracyPercentage = accuracy
        speechStatusMessage = when {
          accuracy >= 85 -> "🌟 විශිෂ්ටයි! ඉතාමත් පැහැදිලි නිවැරදි උච්චාරණයක්! (+50 XP)"
          accuracy >= 50 -> "👍 හොඳයි! නැවත උත්සාහ කර 100% ට ළඟා වන්න."
          else -> "💡 නැවත සවන්දී පැහැදිලිව නැවත කියවන්න."
        }
      }
    } else {
      speechStatusMessage = "කටහඬ හඳුනාගැනීම අවලංගු විය. නැවත උත්සාහ කරන්න."
    }
  }

  val micPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the English sentence now...")
      }
      try {
        isListening = true
        speechStatusMessage = "🎙️ සවන්දෙමින් පවතී... වාක්‍යය ශබ්ද නඟා කියවන්න."
        speechLauncher.launch(intent)
      } catch (e: Exception) {
        isListening = false
        Toast.makeText(context, "Speech recognition is not available on this device", Toast.LENGTH_SHORT).show()
      }
    } else {
      Toast.makeText(context, "Microphone permission is required for voice practice", Toast.LENGTH_SHORT).show()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Top Hero Header with Background Image and Gradient
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = Color(0xFF0F172A),
      shadowElevation = 6.dp
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        Image(
          painter = painterResource(id = R.drawable.img_english_header_1787064736646),
          contentDescription = "English Class Header",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .matchParentSize()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        )

        Box(
          modifier = Modifier
            .matchParentSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF0F172A).copy(alpha = 0.88f),
                  Color(0xFF1E1B4B).copy(alpha = 0.93f),
                  Color(0xFF0F172A).copy(alpha = 0.97f)
                )
              )
            )
        )

        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              onClick = {
                tts?.stop()
                onBack()
              },
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.18f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                  .padding(8.dp)
                  .size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "🇬🇧 ඉංග්‍රීසි පන්තිය",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFF59E0B)
                ) {
                  Text(
                    text = "06-11 ALL GRADES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "අනු කොටස් මත Touch කර සටහන් හා පුහුණු මෙවලම් විවෘත කරන්න",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1)
              )
            }
          }
        }
      }
    }

    // Vertically Stacked Sub-Sections (පහලට පහලට පෙන්වීම)
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
    ) {

      // =========================================================================
      // SUB-SECTION 1: 📑 06-11 ඉංග්‍රීසි පූර්ණ කෙටි සටහන් (PDF HUB)
      // Background Image: img_papers_bg_1786107349533
      // =========================================================================
      item {
        val isExp = expandedSection == 1
        EnglishSectionCard(
          sectionNumber = "1",
          badgeText = "06 - 11 ALL SYLLABUS",
          badgeColor = Color(0xFFF59E0B),
          title = "📑 1. 06-11 ශ්‍රේණි ඉංග්‍රීසි පූර්ණ කෙටි සටහන් (PDF)",
          description = "Tenses, Grammar Formulas, Active/Passive Voice, Prepositions, Letter Formats, Graphs & Essay Frameworks සාරාංශගත කෙටි සටහන් පොත.",
          backgroundImageRes = R.drawable.img_papers_bg_1786107349533,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 1 }
        ) {
          Column(modifier = Modifier.padding(top = 12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { openDrivePdf(defaultEnglishDriveUrl, defaultEnglishNoteTitle) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                modifier = Modifier.weight(1.3f),
                contentPadding = PaddingValues(vertical = 10.dp)
              ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color(0xFF78350F), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("PDF කියවන්න 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
              }

              OutlinedButton(
                onClick = {
                  try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(defaultEnglishDriveUrl))
                    context.startActivity(browserIntent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "Browser විවෘත කළ නොහැක", Toast.LENGTH_SHORT).show()
                  }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.weight(0.9f),
                contentPadding = PaddingValues(vertical = 10.dp)
              ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = "Browser", tint = Color(0xFF1E293B), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Browser", fontSize = 11.sp, color = Color(0xFF1E293B))
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown card
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("🎯 10 & 11 ශ්‍රේණි (O/L Focus & A Pass Strategies):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))
                Text("• All 12 English Tenses & Active vs Passive Voice (be + V3)\n• Direct and Indirect Speech Rules\n• Conditional Clauses (If Types 0, 1, 2, 3)\n• Formal/Informal Letters, Notices, Articles & Graph Descriptions", fontSize = 11.sp, color = Color(0xFF475569), lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("📘 06 - 09 ශ්‍රේණි (Foundations & Intermediate):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))
                Text("• 8 Parts of Speech & Subject-Verb Agreement Rules\n• Prepositions of Time (at, on, in) & Place (under, between)\n• Relative Pronouns (who, which, that, whose)\n• Picture Descriptions & Dialogues", fontSize = 11.sp, color = Color(0xFF475569), lineHeight = 16.sp)
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 2: 📖 ඉංග්‍රීසි ව්‍යාකරණ (ENGLISH GRAMMAR MASTERY)
      // Background Image: img_grammar_card_bg_1787064790400
      // =========================================================================
      item {
        val isExp = expandedSection == 2
        EnglishSectionCard(
          sectionNumber = "2",
          badgeText = "TENSES & RULES",
          badgeColor = Color(0xFF8B5CF6),
          title = "📖 2. ඉංග්‍රීසි ව්‍යාකරණ විශේෂාංගය (Grammar Mastery)",
          description = "Tenses 12, Active/Passive Voice, Reported Speech සහ Prepositions සූත්‍ර සහ සරල සිංහල පැහැදිලි කිරීම්.",
          backgroundImageRes = R.drawable.img_grammar_card_bg_1787064790400,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 2 }
        ) {
          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            grammarLessons.forEach { lesson ->
              var isLessonExp by remember { mutableStateOf(false) }

              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(lesson.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF581C87))
                      Text(lesson.sinhalaSummary, fontSize = 11.sp, color = Color(0xFF7E22CE))
                    }
                    IconButton(onClick = { isLessonExp = !isLessonExp }) {
                      Icon(if (isLessonExp) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "Expand", tint = Color(0xFF7E22CE))
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "📐 ${lesson.formula}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      color = Color(0xFF6B21A8),
                      modifier = Modifier.padding(8.dp)
                    )
                  }

                  if (isLessonExp) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("නිදසුන් වාක්‍ය (Examples):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    lesson.examples.forEach { (eng, sin) ->
                      Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text("• $eng", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                          Text("  ($sin)", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        IconButton(onClick = { speakEnglish(eng) }, modifier = Modifier.size(26.dp)) {
                          Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color(0xFF7E22CE), modifier = Modifier.size(15.dp))
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color(0xFFFFFBEB),
                      border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text(
                        text = "💡 Pro-Tip: ${lesson.proTip}",
                        fontSize = 10.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(8.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 3: ✍️ රචනා, ලිපි හා නිවේදන (WRITING & COMPOSITION STUDIO)
      // Background Image: img_writing_card_bg_1787064772136
      // =========================================================================
      item {
        val isExp = expandedSection == 3
        EnglishSectionCard(
          sectionNumber = "3",
          badgeText = "ESSAYS & LETTERS",
          badgeColor = Color(0xFF0284C7),
          title = "✍️ 3. රචනා, ලිපි හා නිවේදන (Writing & Composition)",
          description = "Formal/Informal Letters, Notices, Articles, Essay Frameworks සහ Graph Descriptions නිවැරදි ආකෘති.",
          backgroundImageRes = R.drawable.img_writing_card_bg_1787064772136,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 3 }
        ) {
          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            templates.forEach { tpl ->
              var isTplExp by remember { mutableStateOf(false) }

              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(tpl.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                      Text(tpl.description, fontSize = 11.sp, color = Color(0xFF0284C7))
                    }
                    IconButton(onClick = { isTplExp = !isTplExp }) {
                      Icon(if (isTplExp) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "Expand", tint = Color(0xFF0284C7))
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Text("📌 Structure Steps:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                  tpl.structureSteps.forEach { step ->
                    Text("• $step", fontSize = 10.sp, color = Color(0xFF334155))
                  }

                  if (isTplExp) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color.White,
                      border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text("📄 Sample Format:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                          IconButton(
                            onClick = {
                              clipboardManager.setText(AnnotatedString(tpl.modelFormat))
                              Toast.makeText(context, "පිටපත් කරගන්නා ලදී (Copied)", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                          ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF0284C7), modifier = Modifier.size(15.dp))
                          }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tpl.modelFormat, fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 16.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 4: 📚 දිනපතා වචන මාලාව (DAILY VOCABULARY & IDIOMS)
      // Background Image: img_vocab_card_bg_1787064760289
      // =========================================================================
      item {
        val isExp = expandedSection == 4
        EnglishSectionCard(
          sectionNumber = "4",
          badgeText = "VOCABULARY BUILDER",
          badgeColor = Color(0xFF10B981),
          title = "📚 4. දිනපතා වචන මාලාව හා උච්චාරණය (Daily Vocabulary)",
          description = "නව ඉංග්‍රීසි වචන, නිවැරදි උච්චාරණය, සිංහල තේරුම සහ ප්‍රායෝගික වාක්‍ය භාවිතය.",
          backgroundImageRes = R.drawable.img_vocab_card_bg_1787064760289,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 4 }
        ) {
          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            vocabList.forEach { item ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(item.word, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(item.pronunciation, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF15803D))
                    }
                    IconButton(
                      onClick = { speakEnglish("${item.word}. ${item.exampleSentence}") },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                    }
                  }
                  Text("තේරුම: ${item.sinhalaMeaning}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("📝 Ex: \"${item.exampleSentence}\"", fontSize = 11.sp, color = Color(0xFF334155))
                  Text("(${item.sinhalaSentenceMeaning})", fontSize = 10.sp, color = Color(0xFF64748B))
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 5: 🔍 කියවීම & CLOZE TESTS (READING & PRACTICE)
      // Background Image: img_subjects_bg_1786107319789
      // =========================================================================
      item {
        val isExp = expandedSection == 5
        EnglishSectionCard(
          sectionNumber = "5",
          badgeText = "READING & CLOZE",
          badgeColor = Color(0xFF3B82F6),
          title = "🔍 5. කියවීම & Cloze Tests (Reading & Practice)",
          description = "විභාග ආකෘතියේ Cloze Tests සහ Reading Comprehension කතා කියවා තේරුම් ගැනීමේ ප්‍රශ්නාවලි.",
          backgroundImageRes = R.drawable.img_subjects_bg_1786107319789,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 5 }
        ) {
          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            clozeTests.forEach { cloze ->
              var showAnswers by remember { mutableStateOf(false) }

              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(cloze.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                  Text(cloze.instructions, fontSize = 10.sp, color = Color(0xFF64748B))
                  Spacer(modifier = Modifier.height(6.dp))

                  Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEFF6FF), modifier = Modifier.fillMaxWidth()) {
                    Text(
                      text = "වචන පෙට්ටිය: ${cloze.wordBank.joinToString(" | ")}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1D4ED8),
                      modifier = Modifier.padding(8.dp)
                    )
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Text(cloze.passageWithBlanks, fontSize = 11.sp, color = Color(0xFF1E293B), lineHeight = 16.sp)

                  if (showAnswers) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5), modifier = Modifier.fillMaxWidth()) {
                      Column(modifier = Modifier.padding(8.dp)) {
                        Text("✅ නිවැරදි පිළිතුරු: ${cloze.correctAnswers.joinToString(", ")}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        Text("පැහැදිලි කිරීම: ${cloze.explanation}", fontSize = 10.sp, color = Color(0xFF047857))
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Button(
                    onClick = { showAnswers = !showAnswers },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.align(Alignment.End)
                  ) {
                    Text(if (showAnswers) "සඟවන්න" else "පිළිතුරු පරීක්ෂා කරන්න", fontSize = 10.sp)
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 6: 🎧 සවන්දීම හා වේග ශ්‍රව්‍ය පුහුණුව (LISTENING LAB)
      // Background Image: img_listening_card_bg_1787064805369
      // =========================================================================
      item {
        val isExp = expandedSection == 6
        EnglishSectionCard(
          sectionNumber = "6",
          badgeText = "LISTENING LAB",
          badgeColor = Color(0xFFE11D48),
          title = "🎧 6. සවන්දීම හා ශ්‍රව්‍ය පුහුණුව (Listening & Speed Audio)",
          description = "සාමාන්‍ය වේගයෙන් හෝ මන්දගාමීව (0.7x Slow) අසා වාක්‍ය උච්චාරණය හා සවන්දීමේ හැකියාව දියුණු කරගන්න.",
          backgroundImageRes = R.drawable.img_listening_card_bg_1787064805369,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 6 }
        ) {
          val listeningSentences = listOf(
            Pair("Good morning teacher, could you please explain the homework again?", "සුබ උදෑසනක් ගුරුතුමනි, කරුණාකර ගෙදර වැඩ නැවත පැහැදිලි කළ හැකිද?"),
            Pair("Environmental pollution is one of the most critical challenges facing humanity today.", "පරිසර දූෂණය අද මානව වර්ගයා මුහුණ දෙන ප්‍රධානතම අභියෝගයකි."),
            Pair("Education empowers young students to think critically and solve complex problems.", "අධ්‍යාපනය සිසුන්ට තාර්කිකව සිතීමට හා ගැටලු විසඳීමට ශක්තිය ලබාදෙයි."),
            Pair("Practice makes perfect, so never give up on learning new languages.", "පුහුණුවෙන් පරිපූර්ණත්වය ළඟා වේ, එබැවින් නව භාෂා ඉගෙනීම කිසි විටෙකත් අත්නොහරින්න.")
          )

          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listeningSentences.forEach { (sentence, sinMeaning) ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(sentence, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9F1239))
                  Text("($sinMeaning)", fontSize = 10.sp, color = Color(0xFF64748B))
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = { speakEnglish(sentence, 1.0f) },
                      shape = RoundedCornerShape(6.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.VolumeUp, contentDescription = "Normal", modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("1.0x Normal", fontSize = 10.sp)
                    }
                    OutlinedButton(
                      onClick = { speakEnglish(sentence, 0.7f) },
                      shape = RoundedCornerShape(6.dp),
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF881337)),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.SlowMotionVideo, contentDescription = "Slow", modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("0.7x Slow", fontSize = 10.sp)
                    }
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 7: 🎙️ කථන ඉංග්‍රීසි & මයික්‍රෆෝන උච්චාරණ පුහුණුව (VOICE MIC AI)
      // Background Image: img_videos_bg_1786110404209
      // =========================================================================
      item {
        val isExp = expandedSection == 7
        EnglishSectionCard(
          sectionNumber = "7",
          badgeText = "VOICE MIC AI",
          badgeColor = Color(0xFF0284C7),
          title = "🎙️ 7. කථන ඉංග්‍රීසි & මයික්‍රෆෝන උච්චාරණ පුහුණුව (Voice Speech Recognition AI)",
          description = "මයික්‍රෆෝනය මඟින් කතා කරන ඉංග්‍රීසි වාක්‍ය සජීවීව හඳුනාගෙන උච්චාරණ නිරවද්‍යතාවය (Accuracy Score %) පරීක්ෂා කිරීම.",
          backgroundImageRes = R.drawable.img_videos_bg_1786110404209,
          isExpanded = isExp,
          onToggle = { expandedSection = if (isExp) null else 7 }
        ) {
          Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Sentence Selector Chips
            Text("පුහුණු වන වාක්‍යය තෝරන්න:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(practiceSentences.size) { idx ->
                FilterChip(
                  selected = selectedPracticeIndex == idx,
                  onClick = {
                    selectedPracticeIndex = idx
                    recognizedSpokenText = ""
                    matchAccuracyPercentage = null
                    speechStatusMessage = "මයික්‍රෆෝන් බොත්තම ඔබා ඉංග්‍රීසි වාක්‍යය ශබ්ද නඟා කියවන්න."
                  },
                  label = { Text("වාක්‍යය ${idx + 1} (${practiceSentences[idx].difficulty})", fontSize = 10.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF0284C7),
                    selectedLabelColor = Color.White
                  )
                )
              }
            }

            // Current Sentence Card
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE0F2FE)) {
                    Text(
                      text = currentPracticeItem.difficulty,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0284C7),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Row {
                    IconButton(onClick = { speakEnglish(currentPracticeItem.englishText, 1.0f) }, modifier = Modifier.size(28.dp)) {
                      Icon(Icons.Default.VolumeUp, contentDescription = "Normal", tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { speakEnglish(currentPracticeItem.englishText, 0.7f) }, modifier = Modifier.size(28.dp)) {
                      Icon(Icons.Default.SlowMotionVideo, contentDescription = "Slow", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(currentPracticeItem.englishText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(currentPracticeItem.phoneticGuide, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Text("තේරුම: ${currentPracticeItem.sinhalaMeaning}", fontSize = 11.sp, color = Color(0xFF334155))
              }
            }

            // Mic Record Action Card
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (matchAccuracyPercentage != null && matchAccuracyPercentage!! >= 80) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, if (matchAccuracyPercentage != null && matchAccuracyPercentage!! >= 80) Color(0xFF86EFAC) else Color(0xFFCBD5E1)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(speechStatusMessage, fontSize = 11.sp, color = Color(0xFF475569), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the sentence in English now...")
                      }
                      try {
                        isListening = true
                        speechStatusMessage = "🎙️ සවන්දෙමින් පවතී... දැන් කියවන්න."
                        speechLauncher.launch(intent)
                      } catch (e: Exception) {
                        isListening = false
                        Toast.makeText(context, "Speech recognition unavailable", Toast.LENGTH_SHORT).show()
                      }
                    } else {
                      micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                  },
                  shape = CircleShape,
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) Color(0xFFDC2626) else Color(0xFF0284C7)
                  ),
                  modifier = Modifier.size(56.dp)
                ) {
                  Icon(
                    if (isListening) Icons.Default.Hearing else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                  )
                }

                if (recognizedSpokenText.isNotBlank()) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Surface(shape = RoundedCornerShape(8.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                      Text("ඔබ පැවසූ දෙය: \"$recognizedSpokenText\"", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                      if (matchAccuracyPercentage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = "🎯 නිරවද්‍යතාවය: $matchAccuracyPercentage%",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = if (matchAccuracyPercentage!! >= 80) Color(0xFF16A34A) else Color(0xFFD97706)
                        )
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

  // Google Drive In-App PDF Dialog
  if (internalShowPdfModal && internalPdfUrl.isNotBlank()) {
    Dialog(
      onDismissRequest = { internalShowPdfModal = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F172A)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(internalPdfTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, modifier = Modifier.weight(1f))
              IconButton(onClick = { internalShowPdfModal = false }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
              }
            }
          }
          AndroidView(
            factory = { ctx ->
              WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = WebViewClient()
                loadUrl(internalPdfUrl)
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}

// Reusable Sub-Section Banner Card with Unique Background Photo & Expandable Accordion
@Composable
fun EnglishSectionCard(
  sectionNumber: String,
  badgeText: String,
  badgeColor: Color,
  title: String,
  description: String,
  backgroundImageRes: Int,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  expandedContent: @Composable () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.5f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header Banner with Unique Educational Background Photo
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggle() }
      ) {
        Image(
          painter = painterResource(id = backgroundImageRes),
          contentDescription = title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.matchParentSize()
        )

        // Dark gradient overlay for high contrast readability
        Box(
          modifier = Modifier
            .matchParentSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF0F172A).copy(alpha = 0.88f),
                  Color(0xFF1E293B).copy(alpha = 0.94f),
                  Color(0xFF0F172A).copy(alpha = 0.97f)
                )
              )
            )
        )

        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = badgeColor
            ) {
              Text(
                text = badgeText,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isExpanded) "හකුළන්න" else "සටහන් බලන්න",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                  contentDescription = "Toggle",
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = 21.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = description,
            fontSize = 11.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 15.sp
          )
        }
      }

      // Detailed Notes / Content (Only shown when clicked / expanded)
      AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          expandedContent()
        }
      }
    }
  }
}
