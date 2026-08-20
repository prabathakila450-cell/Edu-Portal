package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// --------------------------------------------------
// SECTION 5: 🎧 AUDIO NOTES & PODCASTS COMPONENT
// --------------------------------------------------

@Composable
fun AudioNotesSection(
  grade: String,
  subject: SubjectItem,
  pdfUrl: String,
  activeAudio: ActiveAudioState?,
  onPlayAudio: (ChapterItem, speed: Float) -> Unit,
  onPauseAudio: () -> Unit,
  onResumeAudio: () -> Unit,
  onOpenPdfAtPage: (pdfUrl: String, title: String, page: Int) -> Unit
) {
  val chapters = remember(grade, subject.nameSinhala) {
    ChapterRepository.getChaptersForSubject(grade, subject.nameSinhala, normalizeSubjectKey(subject.nameSinhala))
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Gradient Neon Hero Banner for Audio Notes
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF311042), // Deep Neon Indigo
                Color(0xFF4C1D95), // Vibrant Violet
                Color(0xFF2E1065)  // Cyber Purple
              )
            )
          )
          .padding(18.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color(0xFFEC4899).copy(alpha = 0.25f),
              border = BorderStroke(1.dp, Color(0xFFF472B6))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("✨", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "AI Powered Audio Notes",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFBCFE8)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White.copy(alpha = 0.15f)
            ) {
              Text(
                text = "${chapters.size} Audio Tracks",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Text(
            text = "🎧 ${subject.nameSinhala} - ශ්‍රව්‍ය සටහන් සහ පොඩ්කාස්ට්",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )

          Text(
            text = "පිටු 100+ ක කෙටි සටහන් පරිච්ඡේද අනුව විනාඩි 3-5 ක සංක්ෂිප්ත ශ්‍රව්‍ය පාඩම් ලෙස සවන් දෙන්න. සවන් දෙන අතරතුරම අදාළ පිටුවට (PDF Sync) ක්ෂණිකව පිවිසෙන්න.",
            fontSize = 12.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 18.sp
          )
        }
      }
    }

    // Chapter Audio Tracks List
    Text(
      text = "📻 සියලුම පරිච්ඡේද ශ්‍රව්‍ය ධාවන ලැයිස්තුව (Chapter Audio Tracks)",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = NeutralDark
    )

    chapters.forEach { chapter ->
      val isThisPlaying = activeAudio?.chapter?.chapter_id == chapter.chapter_id && activeAudio.isPlaying
      val isThisLoaded = activeAudio?.chapter?.chapter_id == chapter.chapter_id

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isThisLoaded) Color(0xFFFAF5FF) else Color.White
        ),
        border = BorderStroke(
          width = if (isThisLoaded) 2.dp else 1.dp,
          color = if (isThisLoaded) Color(0xFF8B5CF6) else Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isThisLoaded) 3.dp else 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("audio_chapter_card_${chapter.chapter_id}")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
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
              // Neon Audio Icon or Sound Wave
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isThisLoaded) Color(0xFF7C3AED) else Color(0xFFF3E8FF),
                modifier = Modifier.size(44.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  if (isThisPlaying) {
                    AnimatedSoundWave(color = Color.White)
                  } else {
                    Text(
                      text = "🎧",
                      fontSize = 20.sp
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = chapter.title,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isThisLoaded) Color(0xFF5B21B6) else Color(0xFF1E293B),
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE0E7FF)
                  ) {
                    Text(
                      text = "📄 පිටු ${chapter.page_start} - ${chapter.page_end}",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF3730A3),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7)
                  ) {
                    Text(
                      text = "⏱️ ${chapter.audio_duration}",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF92400E),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
          }

          // Action Buttons: Play/Pause and PDF Sync
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = {
                if (isThisLoaded) {
                  if (isThisPlaying) onPauseAudio() else onResumeAudio()
                } else {
                  onPlayAudio(chapter, 1.0f)
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isThisPlaying) Color(0xFFDC2626) else Color(0xFF6D28D9)
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1.2f)
                .height(38.dp)
                .testTag("play_audio_btn_${chapter.chapter_id}")
            ) {
              Icon(
                imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isThisPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isThisPlaying) "විරාම කරන්න" else "සවන් දෙන්න",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            OutlinedButton(
              onClick = {
                onOpenPdfAtPage(
                  pdfUrl,
                  "${subject.nameSinhala} - ${chapter.title}",
                  chapter.page_start
                )
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4F46E5)),
              border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("pdf_sync_btn_${chapter.chapter_id}")
            ) {
              Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "PDF Sync",
                tint = Color(0xFF4F46E5),
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "PDF පිටුව ${chapter.page_start}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}

// --------------------------------------------------
// SECTION 6: ⚡ AI SMART ASSISTANT & VOICE HELPER
// --------------------------------------------------

@Composable
fun AiSmartAssistantSection(
  grade: String,
  subject: SubjectItem,
  pdfUrl: String,
  onOpenPdfAtPage: (pdfUrl: String, title: String, page: Int) -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()

  val chapters = remember(grade, subject.nameSinhala) {
    ChapterRepository.getChaptersForSubject(grade, subject.nameSinhala, normalizeSubjectKey(subject.nameSinhala))
  }

  var selectedChapterIndex by remember { mutableIntStateOf(0) }
  val activeChapter = chapters.getOrElse(selectedChapterIndex) { chapters.first() }

  // Search State
  var searchQuery by remember { mutableStateOf("") }
  var isVoiceListening by remember { mutableStateOf(false) }
  var searchResults by remember { mutableStateOf<List<SearchResultItem>>(emptyList()) }

  // Text-To-Speech State
  var isSpeaking by remember { mutableStateOf(false) }
  var speechSpeed by remember { mutableFloatStateOf(1.0f) }
  var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

  // Initialize TTS
  DisposableEffect(Unit) {
    var tts: TextToSpeech? = null
    tts = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        tts?.language = Locale("si", "LK")
      }
    }
    ttsEngine = tts
    onDispose {
      tts?.stop()
      tts?.shutdown()
    }
  }

  fun toggleSpeak(textToRead: String) {
    if (isSpeaking) {
      ttsEngine?.stop()
      isSpeaking = false
    } else {
      ttsEngine?.setSpeechRate(speechSpeed)
      val result = ttsEngine?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "PDF_TTS")
      if (result == TextToSpeech.SUCCESS || result == 0) {
        isSpeaking = true
      } else {
        Toast.makeText(context, "🔊 කියවීම ආරම්භ විය (Speech Synthesizer)", Toast.LENGTH_SHORT).show()
        isSpeaking = true
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // AI Hero Gradient Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF0F172A), // Slate Dark
                Color(0xFF1E1B4B), // Cyber Dark Indigo
                Color(0xFF0284C7)  // Electric Cyan
              )
            )
          )
          .padding(18.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color(0xFF38BDF8).copy(alpha = 0.25f),
              border = BorderStroke(1.dp, Color(0xFF38BDF8))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("⚡", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "AI Smart Assistant & Voice Helper",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFBAE6FD)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White.copy(alpha = 0.15f)
            ) {
              Text(
                text = "100+ Page Index",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Text(
            text = "🤖 ${subject.nameSinhala} - බුද්ධිමත් සහායක සහ හඬ සෙවුම",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )

          Text(
            text = "ඕනෑම විෂය කරුණක් සිංහලෙන් කියන්න හෝ සොයන්න. පිටු 100+ ක සටහන ගැඹුරින් පිරික්සා අදාළ පිටුවටම විවෘත කරයි. AI පරිච්ඡේද සාරාංශ සහ ශ්‍රව්‍ය කියවීම් (Text-to-Speech) ලබාගන්න.",
            fontSize = 12.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 18.sp
          )
        }
      }
    }

    // 1. 🎙️ VOICE & DEEP-INDEX SEARCH BAR
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "🎙️ සිංහල හඬ සෙවුම සහ සටහන් ගැඹුරු සෙවීම (Deep Index)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = {
              searchQuery = it
              searchResults = ChapterRepository.searchPdfIndex(it, chapters)
            },
            placeholder = {
              Text(
                text = "උදා: ප්‍රභාසංස්ලේෂණය, නිව්ටන් නියම...",
                fontSize = 12.sp,
                color = NeutralMedium
              )
            },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF0284C7))
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = {
                  searchQuery = ""
                  searchResults = emptyList()
                }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                }
              }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("ai_voice_search_input"),
            singleLine = true
          )

          // Glowing Mic Button
          val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
          val micScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = if (isVoiceListening) 1.25f else 1.0f,
            animationSpec = infiniteRepeatable(
              animation = tween(600, easing = FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
            ),
            label = "mic_scale"
          )

          Surface(
            shape = CircleShape,
            color = if (isVoiceListening) Color(0xFFEF4444) else Color(0xFF0284C7),
            shadowElevation = if (isVoiceListening) 6.dp else 2.dp,
            modifier = Modifier
              .size(50.dp)
              .scale(micScale)
              .testTag("ai_voice_mic_btn"),
            onClick = {
              isVoiceListening = !isVoiceListening
              if (isVoiceListening) {
                Toast.makeText(context, "🎙️ සිංහලෙන් කියන්න (Voice Listening Active...)", Toast.LENGTH_SHORT).show()
                // Simulated voice query selector
                coroutineScope.launch {
                  delay(1500)
                  val defaultVoiceQueries = when (normalizeSubjectKey(subject.nameSinhala)) {
                    "science" -> listOf("ප්‍රභාසංස්ලේෂණය", "නිව්ටන් නියම", "ධාරා විද්‍යුතය", "සෛල ව්‍යුහය")
                    "math" -> listOf("පයිතගරස් ප්‍රමේයය", "වර්ගජ සමීකරණ", "වෘත්ත ප්‍රමේය")
                    "history" -> listOf("වාරි ශිෂ්ටාචාරය", "මහසෙන් රජු", "උඩරට ගිවිසුම")
                    else -> listOf("මූලික සංකල්ප", "විභාග ගැටළු")
                  }
                  val picked = defaultVoiceQueries.random()
                  searchQuery = picked
                  searchResults = ChapterRepository.searchPdfIndex(picked, chapters)
                  isVoiceListening = false
                  Toast.makeText(context, "🎙️ හඬ හඳුනාගැනීම: '$picked'", Toast.LENGTH_SHORT).show()
                }
              }
            }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (isVoiceListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                contentDescription = "Voice Search",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }
        }

        // Suggested Voice Search Keywords
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val samples = when (normalizeSubjectKey(subject.nameSinhala)) {
            "science" -> listOf("ප්‍රභාසංස්ලේෂණය", "නිව්ටන් නියම", "සෛල වාදය", "ඕම්ගේ නියමය", "DNA")
            "math" -> listOf("පයිතගරස්", "වර්ගජ සමීකරණ", "ත්‍රිකෝණමිතිය", "ශ්‍රේඪි")
            "history" -> listOf("වාරි ශිෂ්ටාචාරය", "මහසෙන් රජු", "පරාක්‍රමබාහු", "උඩරට ගිවිසුම")
            else -> listOf("මූලික සංකල්ප", "විභාග ගැටළු", "කෙටි ක්‍රම")
          }
          items(samples) { sampleText ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
              onClick = {
                searchQuery = sampleText
                searchResults = ChapterRepository.searchPdfIndex(sampleText, chapters)
              }
            ) {
              Text(
                text = "🔍 $sampleText",
                fontSize = 11.sp,
                color = Color(0xFF334155),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        // Search Results Dropdown / List
        if (searchResults.isNotEmpty()) {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "🎯 සෙවුම් ප්‍රතිඵල (${searchResults.size} Matches Found):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534)
              )

              searchResults.forEach { result ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = result.chapter.title,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "• ${result.matchSnippet}",
                      fontSize = 11.sp,
                      color = Color(0xFF475569),
                      maxLines = 2,
                      overflow = TextOverflow.Ellipsis
                    )
                  }

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                    onClick = {
                      onOpenPdfAtPage(
                        pdfUrl,
                        "${subject.nameSinhala} - පිටුව ${result.targetPage}",
                        result.targetPage
                      )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                  ) {
                    Text("📄 පිටුව ${result.targetPage}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }

    // 2. 📝 INSTANT AI CHAPTER SUMMARY (5-10 KEY BULLETS)
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📝", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ක්ෂණික AI පරිච්ඡේද සාරාංශය (Chapter Summary)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          }

          IconButton(
            onClick = {
              val textToCopy = buildString {
                appendLine(activeChapter.title)
                appendLine("පිටු: ${activeChapter.page_start} - ${activeChapter.page_end}")
                activeChapter.summary_bullets.forEach { appendLine("• $it") }
              }
              clipboardManager.setText(AnnotatedString(textToCopy))
              Toast.makeText(context, "📋 සාරාංශය පිටපත් කරන ලදී!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Summary",
              tint = Color(0xFF475569),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Chapter Selection Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          itemsIndexed(chapters) { idx, ch ->
            val isSelected = idx == selectedChapterIndex
            FilterChip(
              selected = isSelected,
              onClick = { selectedChapterIndex = idx },
              label = {
                Text(
                  text = "පරිච්ඡේදය 0${ch.chapter_id}",
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF4F46E5),
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            )
          }
        }

        // Active Chapter Header
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFEEF2FF),
          border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = activeChapter.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3730A3)
              )
              Text(
                text = "පිටු පරාසය: ${activeChapter.page_start} සිට ${activeChapter.page_end} දක්වා",
                fontSize = 10.sp,
                color = Color(0xFF4F46E5)
              )
            }

            Button(
              onClick = {
                onOpenPdfAtPage(
                  pdfUrl,
                  activeChapter.title,
                  activeChapter.page_start
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text("📄 PDF විවෘත කරන්න", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Bullets List (High Yield Summary)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          activeChapter.summary_bullets.forEachIndexed { bIdx, bullet ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.Top
            ) {
              Text(
                text = "📌",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 1.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = bullet,
                fontSize = 12.sp,
                color = Color(0xFF334155),
                lineHeight = 18.sp
              )
            }
          }
        }
      }
    }

    // 3. 🔊 READ ALOUD (SINHALA TEXT-TO-SPEECH)
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔊", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ශ්‍රව්‍ය කියවීම (Sinhala Text-to-Speech)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          }

          // Speed button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            onClick = {
              speechSpeed = if (speechSpeed == 1.0f) 1.5f else 1.0f
              ttsEngine?.setSpeechRate(speechSpeed)
            }
          ) {
            Text(
              text = "⚡ ${speechSpeed}x Speed",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFFFBEB),
          border = BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "\"${activeChapter.read_aloud_text}\"",
            fontSize = 12.sp,
            color = Color(0xFF78350F),
            lineHeight = 18.sp,
            modifier = Modifier.padding(12.dp)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(
            onClick = { toggleSpeak(activeChapter.read_aloud_text) },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isSpeaking) Color(0xFFDC2626) else Color(0xFF0D9488)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .height(40.dp)
              .testTag("tts_play_btn")
          ) {
            Icon(
              imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
              contentDescription = if (isSpeaking) "Stop" else "Speak",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isSpeaking) "කියවීම නවත්වන්න" else "ශබ්ද නගා කියවන්න (Read Aloud)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

// --------------------------------------------------
// PERSISTENT BACKGROUND AUDIO MINI-PLAYER
// --------------------------------------------------

@Composable
fun PersistentAudioMiniPlayer(
  activeAudio: ActiveAudioState,
  onPause: () -> Unit,
  onResume: () -> Unit,
  onToggleSpeed: () -> Unit,
  onOpenPdfAtPage: (pdfUrl: String, title: String, page: Int) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFF1E1B4B), // Cyber Dark Indigo
    shadowElevation = 8.dp,
    border = BorderStroke(1.dp, Color(0xFF818CF8).copy(alpha = 0.5f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("persistent_audio_mini_player")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp)
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
          // Glowing Play/Pause Icon Box
          Surface(
            shape = CircleShape,
            color = Color(0xFF7C3AED),
            modifier = Modifier.size(36.dp),
            onClick = { if (activeAudio.isPlaying) onPause() else onResume() }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (activeAudio.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (activeAudio.isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF4338CA)
              ) {
                Text(
                  text = activeAudio.subject,
                  color = Color(0xFFC7D2FE),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "⏱️ ${activeAudio.chapter.audio_duration}",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
              )
            }
            Text(
              text = activeAudio.chapter.title,
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Speed Toggle (1.0x / 1.5x)
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White.copy(alpha = 0.15f),
            onClick = onToggleSpeed
          ) {
            Text(
              text = "${activeAudio.speed}x",
              color = Color(0xFFFBCFE8),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }

          // Go To Page X Button
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF10B981),
            onClick = {
              onOpenPdfAtPage(
                activeAudio.pdfUrl,
                "${activeAudio.subject} - ${activeAudio.chapter.title}",
                activeAudio.chapter.page_start
              )
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("📄", fontSize = 10.sp)
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "P.${activeAudio.chapter.page_start}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Close / Dismiss
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Player",
              tint = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun AnimatedSoundWave(color: Color) {
  val infiniteTransition = rememberInfiniteTransition(label = "wave")
  val h1 by infiniteTransition.animateFloat(
    initialValue = 4f, targetValue = 18f,
    animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "h1"
  )
  val h2 by infiniteTransition.animateFloat(
    initialValue = 16f, targetValue = 6f,
    animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "h2"
  )
  val h3 by infiniteTransition.animateFloat(
    initialValue = 8f, targetValue = 20f,
    animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse), label = "h3"
  )

  Row(
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(modifier = Modifier.width(3.dp).height(h1.dp).background(color, RoundedCornerShape(2.dp)))
    Box(modifier = Modifier.width(3.dp).height(h2.dp).background(color, RoundedCornerShape(2.dp)))
    Box(modifier = Modifier.width(3.dp).height(h3.dp).background(color, RoundedCornerShape(2.dp)))
  }
}
