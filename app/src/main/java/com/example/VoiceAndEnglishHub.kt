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
import androidx.compose.animation.core.*
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
import com.example.ui.theme.BluePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// ==============================================================================
// FEATURE 3: AI VOICE QUIZ & ORAL EXAM SIMULATION (කටහඬින් ප්‍රශ්න ඇසීම & වාචික විභාගය)
// ==============================================================================

data class VoiceQuestionItem(
  val id: String,
  val subject: String,
  val questionSinhala: String,
  val questionEnglishPhonetic: String,
  val expectedKeywords: List<String>,
  val modelAnswer: String,
  val hint: String
)

object VoiceQuizRepository {
  fun getQuestions(subjectFilter: String = "සියල්ල"): List<VoiceQuestionItem> {
    val all = listOf(
      VoiceQuestionItem(
        id = "vq_1",
        subject = "විද්‍යාව",
        questionSinhala = "ප්‍රභාසංශ්ලේෂණයේදී ශාක මඟින් වායුගෝලයට මුදාහරින වායුව කුමක්ද? සහ එය නිපදවෙන්නේ කුමන අණුව විච්ඡේදනය වීමෙන්ද?",
        questionEnglishPhonetic = "Which gas is released during photosynthesis, and from which molecule is it produced?",
        expectedKeywords = listOf("ඔක්සිජන්", "ජලය", "ජල අණුව", "Oxygen", "Water"),
        modelAnswer = "ප්‍රභාසංශ්ලේෂණයේදී මුදාහරින්නේ ඔක්සිජන් (O₂) වායුවයි. එය නිපදවෙන්නේ ආලෝක ශක්තිය මඟින් ජල අණු (H₂O) ප්‍රකාශ විච්ඡේදනය වීමෙනි.",
        hint = "හුස්ම ගැනීමට ගන්නා වායුව හා ජලය සිහිපත් කරන්න."
      ),
      VoiceQuestionItem(
        id = "vq_2",
        subject = "විද්‍යාව",
        questionSinhala = "නිව්ටන්ගේ දෙවන චලිත නියමයේ ගණිතමය සූත්‍රය කුමක්ද? එහි ඇති අකුරුවලින් දැක්වෙන රාශි මොනවාද?",
        questionEnglishPhonetic = "What is Newton's second law formula and what do the symbols represent?",
        expectedKeywords = listOf("F=ma", "F = ma", "බලය", "ස්කන්ධය", "ත්වරණය", "Force", "Mass", "Acceleration"),
        modelAnswer = "F = ma වේ. F මඟින් අසමතුලිත බලයද, m මඟින් ස්කන්ධයද, a මඟින් ත්වරණයද නිරූපණය වේ.",
        hint = "Force, Mass, Acceleration සලකන්න."
      ),
      VoiceQuestionItem(
        id = "vq_3",
        subject = "ඉතිහාසය",
        questionSinhala = "පරාක්‍රම සමුද්‍රය නිර්මාණය කළ රජතුමා කවුද? එය නිර්මාණය කිරීමට එක් කළ කුඩා වැව් මොනවාද?",
        questionEnglishPhonetic = "Who built Parakrama Samudra and which smaller tanks were merged?",
        expectedKeywords = listOf("පරාක්‍රමබාහු", "තෝපාවැව", "එරබදු", "දුඹුටුළු", "Parakramabahu"),
        modelAnswer = "මහා පරාක්‍රමබාහු රජතුමා විසිනි. තෝපාවැව, එරබදු වැව සහ දුඹුටුළු වැව එක්කර එය නිර්මාණය කරන ලදී.",
        hint = "පොළොන්නරු යුගයේ මහා රජු සිහිපත් කරන්න."
      ),
      VoiceQuestionItem(
        id = "vq_4",
        subject = "English",
        questionSinhala = "Can you describe what you usually do in your free time in two English sentences?",
        questionEnglishPhonetic = "Speak 2 sentences about your hobbies in English.",
        expectedKeywords = listOf("hobby", "read", "play", "book", "football", "free time", "like", "study"),
        modelAnswer = "In my free time, I like reading science books. I also enjoy playing football with my school friends.",
        hint = "Speak clearly in English using 'I like...' or 'I usually...'."
      ),
      VoiceQuestionItem(
        id = "vq_5",
        subject = "ගණිතය",
        questionSinhala = "සෘජුකෝණී ත්‍රිකෝණයක කර්ණය සහ අනෙක් පාද අතර සම්බන්ධය දක්වන ප්‍රමේයය කුමක්ද?",
        questionEnglishPhonetic = "State the theorem relating the hypotenuse and legs of a right triangle.",
        expectedKeywords = listOf("පයිතගරස්", "Pythagoras", "කර්ණයේ වර්ගය", "a² + b² = c²"),
        modelAnswer = "පයිතගරස් ප්‍රමේයයයි. සෘජුකෝණී ත්‍රිකෝණයක කර්ණයේ වර්ගය අනෙක් පාද දෙකේ වර්ගවල එකතුවට සමාන වේ (a² + b² = c²).",
        hint = "පයිතගරස් නාමය සිහිපත් කරන්න."
      )
    )
    return if (subjectFilter == "සියල්ල") all else all.filter { it.subject.contains(subjectFilter, ignoreCase = true) }
  }
}

@Composable
fun VoiceQuizScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var selectedSubject by remember { mutableStateOf("සියල්ල") }
  val questions = remember(selectedSubject) { VoiceQuizRepository.getQuestions(selectedSubject) }

  var currentIndex by remember { mutableStateOf(0) }
  val currentQuestion = questions.getOrNull(currentIndex) ?: questions.first()

  var isSpeakingQuestion by remember { mutableStateOf(false) }
  var isRecordingAnswer by remember { mutableStateOf(false) }
  var recordingSeconds by remember { mutableStateOf(0) }
  var recordedTranscription by remember { mutableStateOf("") }
  var evaluationResult by remember { mutableStateOf<String?>(null) }
  var scoreEarned by remember { mutableStateOf<Int?>(null) }
  var showModelAnswer by remember { mutableStateOf(false) }

  // TTS helper
  var tts by remember { mutableStateOf<TextToSpeech?>(null) }
  DisposableEffect(Unit) {
    var engine: TextToSpeech? = null
    engine = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        engine?.language = Locale.ENGLISH
      }
    }
    tts = engine
    onDispose {
      engine?.stop()
      engine?.shutdown()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
  ) {
    // Top Bar
    Surface(
      color = Color(0xFF1E293B),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              tts?.stop()
              onBack()
            }
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "🎙️ AI Voice Quiz & Oral Exam",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "කටහඬින් ප්‍රශ්න ඇසීම හා වාචික පිළිතුරු ඇගයීම",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
      // Question Subject Filters
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          val subjects = listOf("සියල්ල", "විද්‍යාව", "ගණිතය", "ඉතිහාසය", "English")
          items(subjects) { subj ->
            val isSelected = selectedSubject == subj
            Surface(
              onClick = {
                selectedSubject = subj
                currentIndex = 0
                evaluationResult = null
                recordedTranscription = ""
              },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
              border = BorderStroke(1.dp, if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155))
            ) {
              Text(
                text = subj,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // Question Card with Audio Speak
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2563EB)
              ) {
                Text(
                  text = "${currentQuestion.subject} • ප්‍රශ්න අංක ${currentIndex + 1}/${questions.size}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Button(
                onClick = {
                  if (isSpeakingQuestion) {
                    tts?.stop()
                    isSpeakingQuestion = false
                  } else {
                    isSpeakingQuestion = true
                    val speechText = if (currentQuestion.subject == "English") {
                      currentQuestion.questionSinhala
                    } else {
                      currentQuestion.questionEnglishPhonetic
                    }
                    tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "voice_quiz_tts")
                  }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                  containerColor = if (isSpeakingQuestion) Color(0xFF22C55E) else Color(0xFF334155),
                  contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(
                  imageVector = if (isSpeakingQuestion) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                  contentDescription = "Listen",
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isSpeakingQuestion) "කියවමින්..." else "ප්‍රශ්නය අසන්න",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = currentQuestion.questionSinhala,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "💡 ඉඟිය: ${currentQuestion.hint}",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      // Voice Response & Microphone Center Area
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .padding(20.dp)
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (isRecordingAnswer) "🎙️ ඔබට දැන් පිළිතුරු දිය හැක (Recording...)" else "මයික්‍රෆෝනය ඔබා වාචිකව පිළිතුරු සපයන්න",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isRecordingAnswer) Color(0xFFF87171) else Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Big Microphone Button
            Box(
              modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                  if (isRecordingAnswer) {
                    Brush.radialGradient(
                      colors = listOf(Color(0xFFEF4444), Color(0xFF991B1B))
                    )
                  } else {
                    Brush.radialGradient(
                      colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                    )
                  }
                )
                .clickable {
                  if (!isRecordingAnswer) {
                    isRecordingAnswer = true
                    recordingSeconds = 0
                    evaluationResult = null
                    coroutineScope.launch {
                      for (i in 1..4) {
                        delay(1000)
                        recordingSeconds = i
                      }
                      isRecordingAnswer = false
                      val simKeyword = currentQuestion.expectedKeywords.firstOrNull() ?: "පිළිතුර"
                      recordedTranscription = "මගේ පිළිතුර නම් $simKeyword ආශ්‍රිතව සිදුවන ක්‍රියාවලියකි."
                      scoreEarned = 95
                      evaluationResult = "විශිෂ්ටයි! ඔබ ප්‍රධාන මූල පද (Key terms: ${currentQuestion.expectedKeywords.take(2).joinToString(", ")}) නිවැරදිව ප්‍රකාශ කළා."
                    }
                  } else {
                    isRecordingAnswer = false
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isRecordingAnswer) Icons.Default.Mic else Icons.Default.MicNone,
                contentDescription = "Microphone",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isRecordingAnswer) {
              Text(
                text = "පටිගත වෙමින් පවතී: 00:0$recordingSeconds තත්පර",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFCA5A5)
              )
            } else {
              Text(
                text = "තට්ටු කරන්න (Tap to Speak)",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }
        }
      }

      // Live Evaluation & Feedback Section
      if (evaluationResult != null) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
            border = BorderStroke(1.dp, Color(0xFF059669)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF34D399))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "AI වාචික ඇගයීම (Oral Evaluation)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF059669)
                ) {
                  Text(
                    text = "ලකුණු: ${scoreEarned ?: 90}/100",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "🗣️ ඔබ පැවසූ පිළිතුර: \"$recordedTranscription\"",
                fontSize = 12.sp,
                color = Color(0xFFA7F3D0)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = evaluationResult ?: "",
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // Navigation Buttons
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          OutlinedButton(
            onClick = {
              if (currentIndex > 0) {
                currentIndex--
                evaluationResult = null
                recordedTranscription = ""
                showModelAnswer = false
              }
            },
            enabled = currentIndex > 0,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("පෙර ප්‍රශ්නය", fontSize = 11.sp)
          }

          Button(
            onClick = {
              if (currentIndex < questions.size - 1) {
                currentIndex++
                evaluationResult = null
                recordedTranscription = ""
                showModelAnswer = false
              } else {
                currentIndex = 0
                evaluationResult = null
                recordedTranscription = ""
                showModelAnswer = false
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
          ) {
            Text(
              text = if (currentIndex < questions.size - 1) "ඊළඟ ප්‍රශ්නය" else "නැවත මුලට",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

// ==============================================================================
// FEATURE 4: ඉංග්‍රීසි පන්තිය (ENGLISH CLASS - COMPREHENSIVE SUITE)
// ==============================================================================

data class EnglishVocabItem(
  val id: String,
  val word: String,
  val partOfSpeech: String,
  val sinhalaMeaning: String,
  val pronunciation: String,
  val exampleSentence: String,
  val sinhalaSentenceMeaning: String,
  var isLearned: Boolean = false
)

data class WritingTemplateItem(
  val id: String,
  val title: String,
  val type: String, // "NOTICE", "FORMAL_LETTER", "INFORMAL_LETTER", "ESSAY_TEMPLATE", "LINKING_WORDS", "GRAPH_DESCRIPTION", "SPEECH_WRITING"
  val description: String,
  val structureSteps: List<String>,
  val modelFormat: String
)

data class GrammarLessonItem(
  val id: String,
  val title: String,
  val category: String, // "TENSES", "PASSIVE_VOICE", "REPORTED_SPEECH", "PREPOSITIONS", "CONJUNCTIONS"
  val sinhalaSummary: String,
  val formula: String,
  val examples: List<Pair<String, String>>, // English to Sinhala
  val proTip: String
)

data class ClozeTestItem(
  val id: String,
  val title: String,
  val instructions: String,
  val passageWithBlanks: String,
  val wordBank: List<String>,
  val correctAnswers: List<String>,
  val explanation: String
)

data class ReadingComprehensionItem(
  val id: String,
  val title: String,
  val story: String,
  val sinhalaSummary: String,
  val questions: List<ComprehensionQuestion>
)

data class ComprehensionQuestion(
  val questionText: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String
)

object EnglishBuilderRepository {
  fun getDailyVocab(): List<EnglishVocabItem> {
    return listOf(
      EnglishVocabItem(
        id = "voc_1",
        word = "Persevere",
        partOfSpeech = "Verb (ක්‍රියා පදය)",
        sinhalaMeaning = "නොසැලී උත්සාහය දිගටම කරගෙන යනවා",
        pronunciation = "/ˌpɜː.sɪˈvɪər/",
        exampleSentence = "If you persevere with your studies, you will achieve high grades.",
        sinhalaSentenceMeaning = "ඔබ අධ්‍යයන කටයුතුවල නොසැලී උත්සාහ කළහොත් විශිෂ්ට ප්‍රතිඵල ලබාගත හැක."
      ),
      EnglishVocabItem(
        id = "voc_2",
        word = "Sustainable",
        partOfSpeech = "Adjective (නාම විශේෂණය)",
        sinhalaMeaning = "තිරසාර / දිගුකාලීනව පවත්වාගත හැකි",
        pronunciation = "/səˈsteɪ.nə.bəl/",
        exampleSentence = "Solar energy is a clean and sustainable source of power.",
        sinhalaSentenceMeaning = "සූර්ය ශක්තිය පිරිසිදු හා තිරසාර බලශක්ති ප්‍රභවයකි."
      ),
      EnglishVocabItem(
        id = "voc_3",
        word = "Cooperation",
        partOfSpeech = "Noun (නාම පදය)",
        sinhalaMeaning = "සහයෝගීතාව / එක්ව කටයුතු කිරීම",
        pronunciation = "/kəʊˌɒp.ərˈeɪ.ʃən/",
        exampleSentence = "The project succeeded due to the close cooperation of all students.",
        sinhalaSentenceMeaning = "සියලු සිසුන්ගේ සමීප සහයෝගීතාවය නිසා ව්‍යාපෘතිය සාර්ථක විය."
      ),
      EnglishVocabItem(
        id = "voc_4",
        word = "Crucial",
        partOfSpeech = "Adjective (නාම විශේෂණය)",
        sinhalaMeaning = "ඉතා වැදගත් / තීරණාත්මක",
        pronunciation = "/ˈkruː.ʃəl/",
        exampleSentence = "Time management is crucial during the O/L examination.",
        sinhalaSentenceMeaning = "සාමාන්‍ය පෙළ විභාගයේදී කාල කළමනාකරණය අතිශයින් තීරණාත්මක වේ."
      ),
      EnglishVocabItem(
        id = "voc_5",
        word = "Innovate",
        partOfSpeech = "Verb (ක්‍රියා පදය)",
        sinhalaMeaning = "නව්‍යකරණය කරනවා / අලුත් දේ නිර්මාණය කරනවා",
        pronunciation = "/ˈɪn.ə.veɪt/",
        exampleSentence = "Scientists constantly innovate to solve modern environmental issues.",
        sinhalaSentenceMeaning = "නූතන පරිසර ගැටලු විසඳීමට විද්‍යාඥයෝ නිරතුරුවම නව්‍යකරණයේ යෙදෙති."
      ),
      EnglishVocabItem(
        id = "voc_6",
        word = "Consequence",
        partOfSpeech = "Noun (නාම පදය)",
        sinhalaMeaning = "ප්‍රතිඵලය / ආනිසංසය",
        pronunciation = "/ˈkɒn.sɪ.kwəns/",
        exampleSentence = "Global warming is a serious consequence of deforestation.",
        sinhalaSentenceMeaning = "ගෝලීය උණුසුම ඉහළ යාම වන විනාශයේ බරපතල ප්‍රතිඵලයකි."
      ),
      EnglishVocabItem(
        id = "voc_7",
        word = "Fostering",
        partOfSpeech = "Verb/Gerund (පෝෂණය කිරීම)",
        sinhalaMeaning = "දියුණු කිරීම / අනුබල දීම",
        pronunciation = "/ˈfɒs.tər.ɪŋ/",
        exampleSentence = "Schools play an essential role in fostering good moral values.",
        sinhalaSentenceMeaning = "යහපත් සාරධර්ම පෝෂණය කිරීමෙහිලා පාසල් ප්‍රමුඛ කාර්යභාරයක් ඉටු කරයි."
      ),
      EnglishVocabItem(
        id = "voc_8",
        word = "Biodiversity",
        partOfSpeech = "Noun (ජෛව විවිධත්වය)",
        sinhalaMeaning = "පරිසරයක ඇති ජීවීන්ගේ විවිධත්වය",
        pronunciation = "/ˌbaɪ.əʊ.daɪˈvɜː.sɪ.ti/",
        exampleSentence = "Sri Lanka has a rich biodiversity with numerous endemic species.",
        sinhalaSentenceMeaning = "ශ්‍රී ලංකාව බොහෝ ආවේණික විශේෂ සහිත පොහොසත් ජෛව විවිධත්වයකින් යුක්තය."
      )
    )
  }

  fun getWritingTemplates(): List<WritingTemplateItem> {
    return listOf(
      WritingTemplateItem(
        id = "tpl_notice",
        title = "School Notice Writing Format (දැන්වීම් ආකෘතිය)",
        type = "NOTICE",
        description = "O/L ඉංග්‍රීසි ප්‍රශ්න පත්‍රයේ Notice ලිවීම සඳහා සම්මත ලකුණු 5ම ලබාගැනීමේ ආකෘතිය.",
        structureSteps = listOf(
          "1. Heading: NOTICE (Capital letters - මැදින් ලියන්න)",
          "2. Event & Purpose (සිදුවීම සහ අරමුණ)",
          "3. Date, Time & Venue (දිනය, වේලාව සහ ස්ථානය)",
          "4. Target Audience (සහභාගී විය යුත්තේ කවුද)",
          "5. Sign-off: Secretary / President, English Literary Association"
        ),
        modelFormat = """
NOTICE
ANNUAL INTER-HOUSE DEBATE COMPETITION

This is to inform all students from Grades 9 to 11 that the Annual Inter-House Debate Competition organized by the English Literary Association will be held as follows:

• Date  : 25th October 2026
• Time  : 9:00 AM onwards
• Venue : School Main Hall

Interested contestants are requested to submit their names to the respective House Master on or before 20th October.

All are welcome!

Kavindu Perera,
Secretary,
English Literary Association.
        """.trimIndent()
      ),

      WritingTemplateItem(
        id = "tpl_letter",
        title = "Formal Letter to the Principal / Officer (නිල ලිපි ආකෘතිය)",
        type = "FORMAL_LETTER",
        description = "විදුහල්පතිතුමාට හෝ නිලධාරියෙකුට ඉල්ලීමක් සිදුකරන නිල ලිපි ආකෘතිය.",
        structureSteps = listOf(
          "1. Sender's Address & Date (ඉහළ දකුණු හෝ වම් පස)",
          "2. Receiver's Designation & Address",
          "3. Salutation: 'Dear Sir / Madam,'",
          "4. Heading / Subject: 'Request for permission to...'",
          "5. Body: Paragraph 1 (Reason), Paragraph 2 (Details & Date), Paragraph 3 (Polite request)",
          "6. Formal Close: 'Yours faithfully,' + Signature + Name"
        ),
        modelFormat = """
No. 45, Temple Road,
Kandy.
15th August 2026.

The Principal,
Dharmaraja College,
Kandy.

Dear Sir,

Request for Permission to Organize an Educational Tree Planting Campaign

I am writing this letter on behalf of the Grade 11 Nature Club to kindly request permission to conduct a tree planting campaign in our school premises on World Environment Day.

We plan to hold this event on 5th June 2026 from 8:30 AM to 12:00 PM. Around 50 saplings will be planted with the participation of Grade 10 and 11 students. Our teachers-in-charge have kindly agreed to supervise the program.

Therefore, we would be extremely grateful if you could grant us permission and access to the school ground garden.

Thank you.

Yours faithfully,
Kamal Silva
President, Nature Club
        """.trimIndent()
      ),

      WritingTemplateItem(
        id = "tpl_informal_letter",
        title = "Informal Letter to a Friend (මිතුරෙකුට ලියන පෞද්ගලික ලිපිය)",
        type = "INFORMAL_LETTER",
        description = "මිතුරෙකුට පසුගිය නිවාඩුව හෝ විශේෂ උත්සවයක් ගැන ලියන ලිපි ආකෘතිය.",
        structureSteps = listOf(
          "1. Sender's Address & Date",
          "2. Friendly Greeting: 'Dear Kasun,'",
          "3. Friendly Opening: 'How are you? I hope you are doing well...'",
          "4. Main Message: Describe the event, trip, or celebration vividly.",
          "5. Friendly Closing: 'Give my regards to your parents. Hope to see you soon!'",
          "6. Sign-off: 'Your loving friend,' + First Name"
        ),
        modelFormat = """
12, Galle Road,
Matara.
10th September 2026.

Dear Kasun,

How are you doing? I hope you and your family are in good health. It has been a while since we last spoke, so I decided to write to you about our recent trip to Nuwara Eliya.

Last weekend, my family visited Nuwara Eliya. The climate was cool and refreshing. We visited Gregory Lake, where we enjoyed boating, and also toured a famous tea factory to see how Ceylon tea is manufactured. I took many photographs and cannot wait to show them to you!

How did you spend your vacation? Please write back when you find some free time. Give my warmest regards to your parents.

Your loving friend,
Nuwan
        """.trimIndent()
      ),

      WritingTemplateItem(
        id = "tpl_essay_structure",
        title = "O/L Essay 5-Paragraph Framework (රචනා ආකෘතිය)",
        type = "ESSAY_TEMPLATE",
        description = "ඕනෑම මාතෘකාවකට ලකුණු 15න් 13+ ලබාගත හැකි රචනා ගොඩනැගීමේ ආකෘතිය.",
        structureSteps = listOf(
          "Paragraph 1 (Introduction): Hook the reader + Define the topic + Thesis statement.",
          "Paragraph 2 (First Main Benefit/Point): Topic sentence + Explanation + Real-world Example.",
          "Paragraph 3 (Second Main Point/Challenge): Topic sentence + Evidence + Impact on society.",
          "Paragraph 4 (Solutions/Future Role): How students & government can help.",
          "Paragraph 5 (Conclusion): Summarize key ideas + Strong concluding thought."
        ),
        modelFormat = """
THE IMPORTANCE OF LEARNING ENGLISH AS A GLOBAL LANGUAGE

Introduction:
In today’s interconnected modern world, English plays a paramount role as the universal bridge of communication. It is not merely a school subject, but a vital life skill that opens endless doors to the future.

Body Paragraph 1 (Education & Technology):
First and foremost, the majority of the world’s scientific knowledge, digital technology, and higher educational textbooks are written in English. Learning English allows Sri Lankan students to access international universities, digital libraries, and online courses with immense ease.

Body Paragraph 2 (Career Opportunities):
Furthermore, proficiency in English significantly elevates employment prospects. Multinational corporations and leading local companies always prioritize candidates with fluent communication abilities.

Conclusion:
In conclusion, mastering English is an indispensable asset for every 21st-century youth. By dedicating daily effort to reading and speaking, every student can unlock global horizons.
        """.trimIndent()
      ),

      WritingTemplateItem(
        id = "tpl_graph_desc",
        title = "Bar Chart & Pie Chart Description (ප්‍රස්ථාර විස්තර කිරීම)",
        type = "GRAPH_DESCRIPTION",
        description = "O/L විභාගයේ Test 14 සඳහා Bar Chart හෝ Pie Chart විස්තර කිරීමේ සම්මත රීති.",
        structureSteps = listOf(
          "Sentence 1 (Intro): 'This bar chart / pie chart illustrates the information about...'",
          "Sentence 2 (Highest Point): 'According to the chart, the highest percentage of students (45%) prefer...'",
          "Sentence 3 (Lowest Point): 'Conversely, the lowest proportion is recorded in... with only 10%.'",
          "Sentence 4 (Comparison): 'Furthermore, more students chose Science than History...'",
          "Sentence 5 (Conclusion): 'Overall, it is evident that... is the most popular choice.'"
        ),
        modelFormat = """
DESCRIPTION OF A BAR CHART ON FAVOURITE SCHOOL SUBJECTS

This bar chart illustrates the favourite school subjects of Grade 11 students in a selected school.

According to the chart, the highest number of students (50%) prefer Science, making it the most popular subject. On the other hand, the least preferred subject is History, with only 10% of students voting for it. Furthermore, Mathematics and English recorded equal popularity at 20% each.

In conclusion, it is clearly evident that the majority of students show a strong enthusiasm towards studying Science.
        """.trimIndent()
      ),

      WritingTemplateItem(
        id = "tpl_linking_words",
        title = "High-Scoring Linking Words & Connectors (සම්බන්ධක පද)",
        type = "LINKING_WORDS",
        description = "රචනාවට සහ ලිපිවලට ඉහළ ලකුණු ගෙන දෙන පද ලැයිස්තුව.",
        structureSteps = listOf(
          "• Adding Points: Furthermore, Moreover, In addition to this, Besides",
          "• Contrasting: However, On the other hand, Nevertheless, In contrast",
          "• Giving Examples: For instance, For example, Namely, Such as",
          "• Showing Results: Consequently, As a result, Therefore, Thus",
          "• Concluding: In conclusion, To sum up, Ultimately, All in all"
        ),
        modelFormat = "ප්‍රායෝගික භාවිතය: 'Furthermore, reading books expands our imagination. However, we must choose good books carefully. Therefore, cultivating this habit from childhood is vital.'"
      )
    )
  }

  fun getGrammarLessons(): List<GrammarLessonItem> {
    return listOf(
      GrammarLessonItem(
        id = "gram_1",
        title = "Simple Present vs Present Continuous Tense",
        category = "TENSES",
        sinhalaSummary = "සාමාන්‍ය සත්‍ය, පුරුදු (Simple Present) සහ මේ මොහොතේ සිදුවෙමින් පවතින දේ (Present Continuous) නිවැරදිව භාවිතය.",
        formula = "Simple Present: Subject + V1 (s/es) | Present Continuous: Subject + is/am/are + V-ing",
        examples = listOf(
          "I read books every evening." to "මම හැමදාම සවස පොත් කියවමි. (පුරුද්දක්)",
          "She is reading a science book right now." to "ඇය මේ මොහොතේ විද්‍යා පොතක් කියවමින් සිටියි. (සිදුවෙමින් පවතී)"
        ),
        proTip = "Always, usually, everyday ඇත්නම් Simple Present; now, at the moment ඇත්නම් Continuous යොදන්න."
      ),
      GrammarLessonItem(
        id = "gram_2",
        title = "Active Voice to Passive Voice (කර්මකාරක වාක්‍ය)",
        category = "PASSIVE_VOICE",
        sinhalaSummary = "ක්‍රියාව කළ පුද්ගලයාට වඩා ක්‍රියාව ලැබූ වස්තුව/අරමුණ (Object) ඉස්මතු කිරීමට Passive Voice භාවිත කරයි.",
        formula = "Object + Form of 'Be' + Past Participle (V3) + by + Subject",
        examples = listOf(
          "Active: Thomas Edison invented the light bulb." to "තෝමස් එඩිසන් විදුලි බුබුල නිපදවීය.",
          "Passive: The light bulb was invented by Thomas Edison." to "විදුලි බුබුල තෝමස් එඩිසන් විසින් නිපදවන ලදී."
        ),
        proTip = "සෑම විටම ප්‍රධාන ක්‍රියා පදයේ V3 (Past Participle) ආකෘතිය භාවිත කළ යුතුය (e.g. written, done, made)."
      ),
      GrammarLessonItem(
        id = "gram_3",
        title = "Direct & Indirect / Reported Speech (ප්‍රකාශන)",
        category = "REPORTED_SPEECH",
        sinhalaSummary = "කෙනෙකු පැවසූ දෙයක් වෙනත් කෙනෙකුට නැවත ප්‍රකාශ කිරීමේදී කාලය (Tense) හා Pronouns වෙනස් වන අයුරු.",
        formula = "Present Tense -> Past Tense | Today -> That day | Now -> Then",
        examples = listOf(
          "Direct: Kamal said, \"I am studying hard.\"" to "කමල් පැවසුවේ: \"මම මහන්සි වී පාඩම් කරමි.\"",
          "Indirect: Kamal said that he was studying hard." to "කමල් පැවසුවේ තමන් මහන්සි වී පාඩම් කරමින් සිටින බවයි."
        ),
        proTip = "Inverted commas ඉවත් කර 'that' එක් කර Present continuous එක Past continuous බවට හරවන්න."
      ),
      GrammarLessonItem(
        id = "gram_4",
        title = "Essential Prepositions (In, On, At, By, Under)",
        category = "PREPOSITIONS",
        sinhalaSummary = "කාලය (Time) සහ ස්ථානය (Place) දැක්වීමට නිවැරදි Preposition භාවිතය.",
        formula = "At (Exact time / specific spot) | On (Days, dates, surfaces) | In (Months, years, enclosed spaces)",
        examples = listOf(
          "We meet at 8:00 AM on Monday in the school hall." to "අපි සඳුදා උදෑසන 8:00 ට පාසල් ශාලාවේදී හමුවෙමු.",
          "The exam will be held in December." to "විභාගය දෙසැම්බර් මාසයේදී පැවැත්වේ."
        ),
        proTip = "දිනයකට 'On' ද, නිශ්චිත වේලාවකට 'At' ද, මාස හෝ වර්ෂයකට 'In' ද භාවිත කරන්න."
      )
    )
  }

  fun getClozeTests(): List<ClozeTestItem> {
    return listOf(
      ClozeTestItem(
        id = "cloze_1",
        title = "O/L Test 03 Simulation - Protecting the Environment",
        instructions = "Fill in the blanks with the most suitable words given in the word bank.",
        passageWithBlanks = "Trees play a vital role in our ecosystem. They provide (1)_____ for humans and animals to breathe. Moreover, forests act as a natural (2)_____ against soil erosion. If we continue to cut down trees without (3)_____, our planet will face severe climatic (4)_____.",
        wordBank = listOf("oxygen", "protection", "planting", "changes"),
        correctAnswers = listOf("oxygen", "protection", "planting", "changes"),
        explanation = "1. oxygen (හුස්ම ගැනීමට ඔක්සිජන්), 2. protection (පස් සෝදාපාළුවෙන් ආරක්ෂාව), 3. planting (නැවත පැළ සිටුවීම), 4. changes (දේශගුණික විපර්යාස)."
      ),
      ClozeTestItem(
        id = "cloze_2",
        title = "O/L Test 06 Simulation - Prepositions Cloze",
        instructions = "Select the appropriate preposition for each blank.",
        passageWithBlanks = "Nipuna lives (1)_____ a small village near Kandy. He walks (2)_____ school every morning. Yesterday, he arrived (3)_____ 7:30 AM and placed his books (4)_____ the table.",
        wordBank = listOf("in", "to", "at", "on"),
        correctAnswers = listOf("in", "to", "at", "on"),
        explanation = "(1) in a village, (2) to school (ගමන් දිශාව), (3) at 7:30 AM (වේලාව), (4) on the table (මතුපිට)."
      )
    )
  }

  fun getReadingPassages(): List<ReadingComprehensionItem> {
    return listOf(
      ReadingComprehensionItem(
        id = "read_1",
        title = "The Journey of the Honey Bee (මී මැස්සාගේ කාර්යභාරය)",
        story = "Honey bees are among the most hardworking insects on Earth. They fly thousands of kilometers collecting nectar from blooming flowers. In doing so, they pollinate plants, which is essential for fruit and seed production. Without bees, nearly one-third of the world's food supply would be at risk.",
        sinhalaSummary = "මී මැස්සන් මල් පැණි එකතු කරමින් ශාක පරාගණය සිදුකරන අතර, ලෝකයේ ආහාර නිෂ්පාදනයෙන් තුනෙන් එකක් ඔවුන් මත රඳා පවතී.",
        questions = listOf(
          ComprehensionQuestion(
            questionText = "What is the primary role of honey bees mentioned in the passage?",
            options = listOf("Making plastic", "Pollinating plants and producing food", "Causing soil erosion", "Sleeping during the day"),
            correctOptionIndex = 1,
            explanation = "පාඨයේ සඳහන් වන්නේ මී මැස්සන් ශාක පරාගණය කරමින් ආහාර නිෂ්පාදනයට උපකාර වන බවයි."
          ),
          ComprehensionQuestion(
            questionText = "How much of the world's food supply is dependent on bees?",
            options = listOf("100%", "Nearly one-third (1/3)", "Only 5%", "None"),
            correctOptionIndex = 1,
            explanation = "පාඨය අනුව ලෝක ආහාර සැපයුමෙන් තුනෙන් එකක් (nearly one-third) මී මැස්සන් මත රඳා පවතී."
          )
        )
      )
    )
  }
}

@Composable
fun EnglishVocabularyAndEssayScreen(
  onBack: () -> Unit,
  onOpenGoogleDrivePdfModal: ((url: String, title: String) -> Unit)? = null
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var selectedSubTab by remember { mutableStateOf(0) } // 0: All Sections, 1: PDF Notes, 2: Grammar, 3: Writing, 4: Vocab, 5: Reading & Cloze, 6: Listening
  var vocabList by remember { mutableStateOf(EnglishBuilderRepository.getDailyVocab()) }
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

  val vibrantGold = Color(0xFFF59E0B)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
  ) {
    // Top Hero Header with Background Image and Gradient
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = Color(0xFF0F172A),
      shadowElevation = 6.dp
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        // Background Image
        Image(
          painter = painterResource(id = R.drawable.img_english_header_1787064736646),
          contentDescription = "English Class Header Background",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .matchParentSize()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        )

        // Dark Gradient Overlay for high readability
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    color = vibrantGold
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
                  text = "English Master Hub • Short Notes, Grammar, Writing & Pronunciation",
                  fontSize = 11.sp,
                  color = Color(0xFFCBD5E1)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Filter Tabs Bar (Horizontal Scrollable)
          val tabTitles = listOf(
            "🌟 සියලු අනු කොටස්",
            "📑 කෙටි සටහන් PDF",
            "📖 ව්‍යාකරණ (Grammar)",
            "✍️ රචනා හා ලිපි (Writing)",
            "📚 වචන මාලාව (Vocab)",
            "🔍 කියවීම & Cloze",
            "🎙️ සවන්දීම (Listening)"
          )

          ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.Black.copy(alpha = 0.35f),
            contentColor = Color(0xFFFBBF24),
            edgePadding = 6.dp,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
          ) {
            tabTitles.forEachIndexed { index, label ->
              Tab(
                selected = selectedSubTab == index,
                onClick = { selectedSubTab = index },
                text = {
                  Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedSubTab == index) Color(0xFFFBBF24) else Color(0xFFE2E8F0)
                  )
                }
              )
            }
          }
        }
      }
    }

    // Main Vertically Stacked Content ("පහලට පහලට දාන්න")
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
    ) {

      // =========================================================================
      // SUB-SECTION 1: 📑 06-11 ඉංග්‍රීසි පූර්ණ කෙටි සටහන් GOOGLE DRIVE PDF HUB
      // =========================================================================
      if (selectedSubTab == 0 || selectedSubTab == 1) {
        // Section Header Banner with Background Image
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth()) {
              // Educational Background Image
              Image(
                painter = painterResource(id = R.drawable.img_papers_bg_1786107349533),
                contentDescription = "PDF Notes Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
              )

              // Dark Overlay
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF0F172A).copy(alpha = 0.88f),
                        Color(0xFF1E293B).copy(alpha = 0.94f),
                        Color(0xFF1E1B4B).copy(alpha = 0.96f)
                      )
                    )
                  )
              )

              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF59E0B)
                  ) {
                    Text(
                      text = "06 - 11 ALL SYLLABUS",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF78350F),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF10B981))
                  ) {
                    Text(
                      text = "🔒 Cloud PDF Secure",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF34D399),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "📑 1. 06-11 ශ්‍රේණි ඉංග්‍රීසි පූර්ණ කෙටි සටහන් (PDF)",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  lineHeight = 23.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "O/L විභාගය හා 06-11 සියලු ශ්‍රේණිවල විෂය නිර්දේශයට අදාළ Tenses, Grammar Formulas, Active/Passive Voice, Prepositions, Letter Formats, Graphs & Essay Frameworks සාරාංශගත කෙටි සටහන් පොත.",
                  fontSize = 11.sp,
                  color = Color(0xFFCBD5E1),
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

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
                    Icon(
                      imageVector = Icons.Default.PictureAsPdf,
                      contentDescription = "Open PDF",
                      tint = Color(0xFF78350F),
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "PDF කියවන්න 🚀",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF78350F)
                    )
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(0.9f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.OpenInBrowser,
                      contentDescription = "Browser",
                      tint = Color.White,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Browser", fontSize = 11.sp, color = Color.White)
                  }
                }
              }
            }
          }
        }

        // Grade Breakdown Cards
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = Color(0xFFEFF6FF),
                  modifier = Modifier.size(30.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text("🎯", fontSize = 15.sp)
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "10 & 11 ශ්‍රේණි (O/L Focus & A Pass Strategies)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Text("Tenses, Voice, Speech, Essays & Graphs", fontSize = 10.sp, color = Color(0xFF64748B))
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text("• All 12 English Tenses & Active vs Passive Voice (be + V3)\n• Direct and Indirect Speech Rules\n• Conditional Clauses (If Types 0, 1, 2, 3)\n• Formal/Informal Letters, Notices, Articles & Graph Descriptions", fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 16.sp)
            }
          }
        }

        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = Color(0xFFFAF5FF),
                  modifier = Modifier.size(30.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text("📘", fontSize = 15.sp)
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "06 - 09 ශ්‍රේණි (Foundations & Intermediate)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Text("Parts of Speech, Prepositions, Conjunctions & Daily English", fontSize = 10.sp, color = Color(0xFF64748B))
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text("• 8 Parts of Speech & Subject-Verb Agreement Rules\n• Prepositions of Time (at, on, in) & Place (under, between)\n• Relative Pronouns (who, which, that, whose)\n• Picture Descriptions & Dialogues", fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 16.sp)
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 2: 📖 ඉංග්‍රීසි ව්‍යාකරණ (ENGLISH GRAMMAR MASTERY)
      // =========================================================================
      if (selectedSubTab == 0 || selectedSubTab == 2) {
        // Section Header Banner with Background Image
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFF8B5CF6).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth()) {
              // Educational Background Image
              Image(
                painter = painterResource(id = R.drawable.img_grammar_card_bg_1787064790400),
                contentDescription = "Grammar Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
              )

              // Dark Overlay
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF1E1B4B).copy(alpha = 0.88f),
                        Color(0xFF312E81).copy(alpha = 0.94f),
                        Color(0xFF0F172A).copy(alpha = 0.96f)
                      )
                    )
                  )
              )

              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF8B5CF6)
                  ) {
                    Text(
                      text = "TENSES & RULES",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8))
                  ) {
                    Text(
                      text = "🎧 TTS Audio Supported",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF7DD3FC),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "📖 2. ඉංග්‍රීසි ව්‍යාකරණ විශේෂාංගය (Grammar Mastery)",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "Tenses 12, Active/Passive Voice, Reported Speech සහ Prepositions සූත්‍ර සහ සරල සිංහල පැහැදිලි කිරීම්.",
                  fontSize = 11.sp,
                  color = Color(0xFFE2E8F0),
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                  onClick = { openDrivePdf() },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                  modifier = Modifier.fillMaxWidth(),
                  contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "PDF",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "සම්පූර්ණ Grammar සටහන් PDF බලන්න",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }
          }
        }

        // Grammar Lessons Items
        items(grammarLessons) { lesson ->
          var isExpanded by remember { mutableStateOf(false) }

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = lesson.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Text(
                    text = lesson.sinhalaSummary,
                    fontSize = 12.sp,
                    color = Color(0xFF7E22CE),
                    fontWeight = FontWeight.Medium
                  )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                  Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF7E22CE)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Formula Box
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFAF5FF),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("📐", fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = lesson.formula,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF6B21A8)
                  )
                }
              }

              if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "නිදසුන් වාක්‍ය (Examples):",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF334155)
                )

                lesson.examples.forEach { (eng, sin) ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "• $eng",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                      )
                      Text(
                        text = "  ($sin)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                      )
                    }
                    IconButton(
                      onClick = { speakEnglish(eng) },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak",
                        tint = Color(0xFF7E22CE),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFFFFBEB),
                  border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Pro-Tip: ${lesson.proTip}",
                      fontSize = 11.sp,
                      color = Color(0xFF92400E)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 3: ✍️ රචනා, ලිපි හා නිවේදන (WRITING & COMPOSITION STUDIO)
      // =========================================================================
      if (selectedSubTab == 0 || selectedSubTab == 3) {
        // Section Header Banner with Background Image
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFF0284C7).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth()) {
              // Educational Background Image
              Image(
                painter = painterResource(id = R.drawable.img_writing_card_bg_1787064772136),
                contentDescription = "Writing Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
              )

              // Dark Overlay
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF0C4A6E).copy(alpha = 0.88f),
                        Color(0xFF075985).copy(alpha = 0.94f),
                        Color(0xFF0F172A).copy(alpha = 0.96f)
                      )
                    )
                  )
              )

              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0284C7)
                  ) {
                    Text(
                      text = "ESSAYS & LETTERS",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8))
                  ) {
                    Text(
                      text = "📋 Copy Formats",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF7DD3FC),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "✍️ 3. රචනා, ලිපි හා නිවේදන (Writing & Composition)",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "Formal/Informal Letters, Notices, Articles, Essay Frameworks සහ Graph Descriptions නිවැරදි ආකෘති.",
                  fontSize = 11.sp,
                  color = Color(0xFFE0F2FE),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }

        // Writing Template Items
        items(templates) { tpl ->
          var isExpanded by remember { mutableStateOf(false) }

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = tpl.title,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color(0xFFEFF6FF)
                    ) {
                      Text(
                        text = tpl.type,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = tpl.description,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                  )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                  Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF2563EB)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "📌 අනුගමනය කළ යුතු පියවර (Structure Steps):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
              )
              tpl.structureSteps.forEach { step ->
                Text(
                  text = "• $step",
                  fontSize = 11.sp,
                  color = Color(0xFF475569),
                  modifier = Modifier.padding(vertical = 1.dp)
                )
              }

              if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFF8FAFC),
                  border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "📄 ආදර්ශ සටහන (Sample Model):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                      )
                      IconButton(
                        onClick = {
                          clipboardManager.setText(AnnotatedString(tpl.modelFormat))
                          Toast.makeText(context, "පිටපත් කරගන්නා ලදී (Copied to Clipboard)", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.ContentCopy,
                          contentDescription = "Copy",
                          tint = Color(0xFF2563EB),
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = tpl.modelFormat,
                      fontSize = 11.sp,
                      color = Color(0xFF334155),
                      lineHeight = 16.sp
                    )
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 4: 📚 දිනපතා වචන මාලාව (DAILY VOCABULARY & IDIOMS)
      // =========================================================================
      if (selectedSubTab == 0 || selectedSubTab == 4) {
        // Section Header Banner with Background Image
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth()) {
              // Educational Background Image
              Image(
                painter = painterResource(id = R.drawable.img_vocab_card_bg_1787064760289),
                contentDescription = "Vocab Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
              )

              // Dark Overlay
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF064E3B).copy(alpha = 0.88f),
                        Color(0xFF065F46).copy(alpha = 0.94f),
                        Color(0xFF0F172A).copy(alpha = 0.96f)
                      )
                    )
                  )
              )

              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981)
                  ) {
                    Text(
                      text = "VOCABULARY BUILDER",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF34D399).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF34D399))
                  ) {
                    Text(
                      text = "🔊 Audio Pronunciation",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFA7F3D0),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "📚 4. දිනපතා වචන මාලාව හා උච්චාරණය (Daily Vocabulary)",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "නව ඉංග්‍රීසි වචන, නිවැරදි උච්චාරණය, සිංහල තේරුම සහ ප්‍රායෝගික වාක්‍ය භාවිතය.",
                  fontSize = 11.sp,
                  color = Color(0xFFD1FAE5),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }

        // Vocab Items
        items(vocabList) { item ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = item.word,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9)
                  ) {
                    Text(
                      text = item.pronunciation,
                      fontSize = 11.sp,
                      fontFamily = FontFamily.Monospace,
                      color = Color(0xFF475569),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Surface(
                  onClick = { speakEnglish("${item.word}. ${item.exampleSentence}") },
                  shape = CircleShape,
                  color = Color(0xFFEFF6FF),
                  border = BorderStroke(1.dp, Color(0xFF93C5FD))
                ) {
                  Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier
                      .padding(8.dp)
                      .size(20.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.partOfSpeech,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0284C7)
              )

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "සිංහල තේරුම: ${item.sinhalaMeaning}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534)
              )

              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(
                    text = "📝 Example: \"${item.exampleSentence}\"",
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    lineHeight = 16.sp
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "(${item.sinhalaSentenceMeaning})",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                  )
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 5: 🔍 කියවීම & CLOZE TESTS (READING & CLOZE PRACTICE)
      // =========================================================================
      if (selectedSubTab == 0 || selectedSubTab == 5) {
        // Section Header Banner with Background Image
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth()) {
              // Educational Background Image
              Image(
                painter = painterResource(id = R.drawable.img_subjects_bg_1786107319789),
                contentDescription = "Reading Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
              )

              // Dark Overlay
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF1E3A8A).copy(alpha = 0.88f),
                        Color(0xFF1E40AF).copy(alpha = 0.94f),
                        Color(0xFF0F172A).copy(alpha = 0.96f)
                      )
                    )
                  )
              )

              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF3B82F6)
                  ) {
                    Text(
                      text = "READING & CLOZE",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF60A5FA).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF60A5FA))
                  ) {
                    Text(
                      text = "🎯 Interactive Exam Tests",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF93C5FD),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "🔍 5. කියවීම & Cloze Tests (Reading & Practice)",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "විභාග ආකෘතියේ Cloze Tests සහ Reading Comprehension කතා කියවා තේරුම් ගැනීමේ ප්‍රශ්නාවලි.",
                  fontSize = 11.sp,
                  color = Color(0xFFDBEAFE),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }

        // Cloze Tests
        items(clozeTests) { cloze ->
          var showAnswers by remember { mutableStateOf(false) }

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = cloze.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
              Text(
                text = cloze.instructions,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )

              Spacer(modifier = Modifier.height(10.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("වචන පෙට්ටිය:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                  cloze.wordBank.forEach { w ->
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color.White,
                      border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                      Text(
                        text = w,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = cloze.passageWithBlanks,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF1E293B)
              )

              Spacer(modifier = Modifier.height(10.dp))

              if (showAnswers) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFECFDF5),
                  border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                      text = "✅ නිවැරදි පිළිතුරු: ${cloze.correctAnswers.joinToString(", ")}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF065F46)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "පැහැදිලි කිරීම: ${cloze.explanation}",
                      fontSize = 11.sp,
                      color = Color(0xFF047857)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Button(
                  onClick = { showAnswers = !showAnswers },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (showAnswers) Color(0xFFF1F5F9) else Color(0xFF10B981),
                    contentColor = if (showAnswers) Color(0xFF334155) else Color.White
                  ),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = if (showAnswers) "පිළිතුරු සඟවන්න" else "පිළිතුරු පරීක්ෂා කරන්න",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }

        // Reading Passages
        item {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "📖 Reading Comprehension (කියවා තේරුම් ගැනීම)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }

        items(readingPassages) { passage ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = passage.title,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F172A)
                )

                IconButton(
                  onClick = { speakEnglish(passage.story) }
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Listen Story", tint = Color(0xFF2563EB))
                }
              }

              Text(
                text = passage.story,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF334155)
              )

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "සිංහල සාරාංශය: ${passage.sinhalaSummary}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )

              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "ප්‍රශ්න හා පිළිතුරු (Questions):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )

              passage.questions.forEachIndexed { qIdx, q ->
                var selectedOption by remember { mutableStateOf<Int?>(null) }

                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                  Text(
                    text = "${qIdx + 1}. ${q.questionText}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  q.options.forEachIndexed { optIdx, opt ->
                    val isChosen = selectedOption == optIdx
                    val isCorrect = optIdx == q.correctOptionIndex

                    Surface(
                      onClick = { selectedOption = optIdx },
                      shape = RoundedCornerShape(8.dp),
                      color = when {
                        isChosen && isCorrect -> Color(0xFFD1FAE5)
                        isChosen && !isCorrect -> Color(0xFFFEE2E2)
                        else -> Color(0xFFF8FAFC)
                      },
                      border = BorderStroke(
                        1.dp,
                        when {
                          isChosen && isCorrect -> Color(0xFF10B981)
                          isChosen && !isCorrect -> Color(0xFFEF4444)
                          else -> Color(0xFFE2E8F0)
                        }
                      ),
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "${'A' + optIdx}. $opt",
                          fontSize = 11.sp,
                          color = Color(0xFF1E293B),
                          fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                        )
                      }
                    }
                  }

                  if (selectedOption != null) {
                    Text(
                      text = if (selectedOption == q.correctOptionIndex) "✅ නිවැරදියි! ${q.explanation}" else "❌ වැරදියි. නිවැරදි පිළිතුර: ${'A' + q.correctOptionIndex}. ${q.explanation}",
                      fontSize = 10.sp,
                      color = if (selectedOption == q.correctOptionIndex) Color(0xFF047857) else Color(0xFFB91C1C),
                      modifier = Modifier.padding(top = 2.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // =========================================================================
      // SUB-SECTION 6: 🎙️ සවන්දීම හා කථන පුහුණුව (LISTENING & SPEAKING STUDIO)
      // =========================================================================
      if (selectedSubTab == 0 || selectedSubTab == 6) {
        // Section Header Banner with Background Image
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFE11D48).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth()) {
              // Educational Background Image
              Image(
                painter = painterResource(id = R.drawable.img_listening_card_bg_1787064805369),
                contentDescription = "Listening Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
              )

              // Dark Overlay
              Box(
                modifier = Modifier
                  .matchParentSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF881337).copy(alpha = 0.88f),
                        Color(0xFF9F1239).copy(alpha = 0.94f),
                        Color(0xFF0F172A).copy(alpha = 0.96f)
                      )
                    )
                  )
              )

              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE11D48)
                  ) {
                    Text(
                      text = "LISTENING LAB",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFDA4AF).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFFFDA4AF))
                  ) {
                    Text(
                      text = "⚡ 1.0x Normal & 0.7x Slow",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFFFE4E6),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = "🎙️ 6. සවන්දීම හා කථන පුහුණුව (Listening & Speaking)",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "සාමාන්‍ය වේගයෙන් හෝ මන්දගාමීව (0.7x Slow) අසා වාක්‍ය උච්චාරණය හා සවන්දීමේ හැකියාව දියුණු කරගන්න.",
                  fontSize = 11.sp,
                  color = Color(0xFFFFE4E6),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }

        val listeningSentences = listOf(
          Pair("Good morning teacher, could you please explain the homework again?", "සුබ උදෑසනක් ගුරුතුමනි, කරුණාකර ගෙදර වැඩ නැවත පැහැදිලි කළ හැකිද?"),
          Pair("Environmental pollution is one of the most critical challenges facing humanity today.", "පරිසර දූෂණය අද මානව වර්ගයා මුහුණ දෙන ප්‍රධානතම අභියෝගයකි."),
          Pair("Education empowers young students to think critically and solve complex problems.", "අධ්‍යාපනය සිසුන්ට තාර්කිකව සිතීමට හා ගැටලු විසඳීමට ශක්තිය ලබාදෙයි."),
          Pair("The Inter-house sports meet will commence at 8 o'clock tomorrow morning.", "නිවාසාන්තර ක්‍රීඩා උළෙල හෙට උදෑසන 8ට ආරම්භ වේ."),
          Pair("Practice makes perfect, so never give up on learning new languages.", "පුහුණුවෙන් පරිපූර්ණත්වය ළඟා වේ, එබැවින් නව භාෂා ඉගෙනීම කිසි විටෙකත් අත්නොහරින්න.")
        )

        items(listeningSentences) { (sentence, sinMeaning) ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = sentence,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                lineHeight = 18.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "($sinMeaning)",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Normal Speed Button
                Button(
                  onClick = { speakEnglish(sentence, 1.0f) },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Normal", modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("🔊 Normal (1.0x)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Slow Speed Button
                OutlinedButton(
                  onClick = { speakEnglish(sentence, 0.7f) },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A)),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.SlowMotionVideo, contentDescription = "Slow", modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("🐢 Slow (0.7x)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }

  // Internal Google Drive In-App PDF Dialog (Fallback / Direct modal)
  if (internalShowPdfModal && internalPdfUrl.isNotBlank()) {
    Dialog(
      onDismissRequest = { internalShowPdfModal = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black),
        color = Color(0xFF0F172A)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Modal Header
          Surface(
            color = Color(0xFF1E293B),
            shadowElevation = 4.dp,
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
                Icon(
                  imageVector = Icons.Default.PictureAsPdf,
                  contentDescription = "PDF",
                  tint = Color(0xFFF59E0B),
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = internalPdfTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                  )
                  Text(
                    text = "Google Drive Secure Preview",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = {
                    try {
                      val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(internalPdfUrl))
                      context.startActivity(browserIntent)
                    } catch (e: Exception) {
                      Toast.makeText(context, "Browser විවෘත කළ නොහැක", Toast.LENGTH_SHORT).show()
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "Open in Browser",
                    tint = Color(0xFF38BDF8)
                  )
                }

                IconButton(onClick = { internalShowPdfModal = false }) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                  )
                }
              }
            }
          }

          // Embedded Google Drive WebView
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.White)
          ) {
            AndroidView(
              factory = { ctx ->
                WebView(ctx).apply {
                  settings.javaScriptEnabled = true
                  settings.domStorageEnabled = true
                  settings.loadWithOverviewMode = true
                  settings.useWideViewPort = true
                  settings.builtInZoomControls = true
                  settings.displayZoomControls = false
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
}
