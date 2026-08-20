package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.BluePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

// ==============================================================================
// FEATURE 1: O/L EXAM COUNTDOWN TIMER & DAILY STUDY PLANNER (විභාග දින ගණකය සහ දෛනික කාලසටහන)
// ==============================================================================

data class StudyTaskItem(
  val id: String,
  val title: String,
  val subject: String,
  val estimatedMinutes: Int,
  val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownAndPlannerScreen(
  onBack: () -> Unit,
  onOpenPdfModal: (url: String, title: String) -> Unit = { _, _ -> }
) {
  val context = LocalContext.current
  val tasks = remember {
    mutableStateListOf(
      StudyTaskItem("task_1", "විද්‍යාව 11 ශ්‍රේණිය: ජීව විද්‍යාව කෙටි සටහන් කියවීම", "විද්‍යාව", 30, true),
      StudyTaskItem("task_2", "ගණිතය: ජ්‍යාමිතිය ප්‍රමේය 5ක් පුහුණු වීම", "ගණිතය", 45, false),
      StudyTaskItem("task_3", "ඉතිහාසය: 06-11 සිතියම් 3ක් ලකුණු කිරීම", "ඉතිහාසය", 20, false),
      StudyTaskItem("task_4", "ඉංග්‍රීසි: Daily Vocabulary කාඩ්පත් 10ක් කියවීම", "English", 15, false),
      StudyTaskItem("task_5", "බුද්ධ ධර්මය: ප්‍රශ්න පත්‍රයේ කෙටි ප්‍රශ්න 20ක් ලිවීම", "බුද්ධ ධර්මය", 25, false)
    )
  }

  var newTaskText by remember { mutableStateOf("") }
  var newTaskSubject by remember { mutableStateOf("විද්‍යාව") }
  var newTaskMinutes by remember { mutableStateOf("30") }
  var showAddTaskDialog by remember { mutableStateOf(false) }

  // Countdown State
  var targetExamName by remember { mutableStateOf("2026 අ.පො.ස. සාමාන්‍ය පෙළ (O/L)") }
  // Target date for O/L (e.g. May 15, 2027)
  val examTargetCalendar = remember {
    Calendar.getInstance().apply {
      set(2027, Calendar.MAY, 15, 8, 30, 0)
    }
  }

  var timeRemainingMillis by remember {
    mutableStateOf(max(0L, examTargetCalendar.timeInMillis - System.currentTimeMillis()))
  }

  LaunchedEffect(Unit) {
    while (true) {
      timeRemainingMillis = max(0L, examTargetCalendar.timeInMillis - System.currentTimeMillis())
      delay(1000L)
    }
  }

  val totalSeconds = timeRemainingMillis / 1000
  val days = totalSeconds / (24 * 3600)
  val hours = (totalSeconds % (24 * 3600)) / 3600
  val minutes = (totalSeconds % 3600) / 60
  val seconds = totalSeconds % 60

  val completedTasksCount = tasks.count { it.isCompleted }
  val progressPercent = if (tasks.isNotEmpty()) completedTasksCount.toFloat() / tasks.size else 0f

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "⏱️ O/L Countdown & Daily Planner",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "විභාග දින ගණකය හා දෛනික ඉලක්ක සැලසුම",
              fontSize = 10.sp,
              color = Color(0xFFFED7AA)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFC2410C))
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddTaskDialog = true },
        containerColor = Color(0xFFC2410C),
        contentColor = Color.White,
        shape = CircleShape
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))
        .padding(paddingValues)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. HERO COUNTDOWN CARD
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color(0xFF9A3412), Color(0xFFEA580C), Color(0xFFF97316))
                )
              )
              .padding(18.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
              ) {
                Text(
                  text = "🎯 $targetExamName",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Timer Digits Grid
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                CountdownDigitBox(value = days.toString().padStart(2, '0'), label = "දින (Days)")
                CountdownDigitBox(value = hours.toString().padStart(2, '0'), label = "පැය (Hours)")
                CountdownDigitBox(value = minutes.toString().padStart(2, '0'), label = "මිනිත්තු (Mins)")
                CountdownDigitBox(value = seconds.toString().padStart(2, '0'), label = "තත්පර (Secs)")
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "✨ \"අද කරන පුහුණුව හෙට දවසේ විශිෂ්ට සාමාර්ථයකට මග පාදයි!\"",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFEDD5),
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      // 2. DAILY PROGRESS BAR CARD
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
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
                Text("📈", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "දෛනික ඉලක්ක ප්‍රගතිය (Today's Progress)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF0F172A)
                )
              }
              Text(
                text = "$completedTasksCount / ${tasks.size} සම්පූර්ණයි",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0284C7)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
              progress = { progressPercent },
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape),
              color = Color(0xFF10B981),
              trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = if (completedTasksCount == tasks.size && tasks.isNotEmpty())
                "🎉 නියමයි! අද දවසේ සියලු අධ්‍යයන ඉලක්ක සාර්ථකව අවසන්!"
              else
                "ඉතිරි ඉලක්ක ${tasks.size - completedTasksCount} සම්පූර්ණ කර ලකුණු එකතු කරගන්න.",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }
      }

      // 3. TASK LIST HEADER
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📋", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "අද දවසේ කාලසටහන (Daily Study Tasks)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF1E293B)
            )
          }
          TextButton(onClick = { showAddTaskDialog = true }) {
            Icon(Icons.Default.AddCircle, contentDescription = "Add", modifier = Modifier.size(16.dp), tint = Color(0xFFC2410C))
            Spacer(modifier = Modifier.width(4.dp))
            Text("නව ඉලක්කයක්", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
          }
        }
      }

      // 4. TASK ITEMS
      items(tasks) { task ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Color(0xFFF0FDF4) else Color.White
          ),
          border = BorderStroke(
            1.dp,
            if (task.isCompleted) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = task.isCompleted,
              onCheckedChange = { isChecked ->
                val idx = tasks.indexOf(task)
                if (idx != -1) {
                  tasks[idx] = task.copy(isCompleted = isChecked)
                  if (isChecked) {
                    Toast.makeText(context, "🌟 නියමයි! +20 XP ලකුණු හිමිවිය!", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = task.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (task.isCompleted) Color(0xFF15803D) else Color(0xFF1E293B),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = Color(0xFFEFF6FF)
                ) {
                  Text(
                    text = task.subject,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "⏱️ ${task.estimatedMinutes} mins",
                  fontSize = 10.sp,
                  color = Color(0xFF64748B)
                )
              }
            }

            IconButton(
              onClick = { tasks.remove(task) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }

  // Add Task Dialog
  if (showAddTaskDialog) {
    AlertDialog(
      onDismissRequest = { showAddTaskDialog = false },
      title = {
        Text("➕ නව අධ්‍යයන ඉලක්කයක් එක් කරන්න", fontSize = 14.sp, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            label = { Text("පාඩම / ඉලක්ක විස්තරය") },
            placeholder = { Text("උදා: විද්‍යාව රසායන විද්‍යාව සටහන් කියවීම") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = newTaskSubject,
            onValueChange = { newTaskSubject = it },
            label = { Text("විෂය (Subject)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = newTaskMinutes,
            onValueChange = { newTaskMinutes = it },
            label = { Text("ගතවන කාලය (මිනිත්තු)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newTaskText.isNotBlank()) {
              tasks.add(
                StudyTaskItem(
                  id = "task_${System.currentTimeMillis()}",
                  title = newTaskText,
                  subject = newTaskSubject.ifBlank { "අධ්‍යයනය" },
                  estimatedMinutes = newTaskMinutes.toIntOrNull() ?: 30,
                  isCompleted = false
                )
              )
              newTaskText = ""
              showAddTaskDialog = false
              Toast.makeText(context, "ඉලක්කය සාර්ථකව එක් කරන ලදී!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2410C))
        ) {
          Text("එක් කරන්න")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddTaskDialog = false }) {
          Text("අවලංගු කරන්න")
        }
      }
    )
  }
}

@Composable
fun CountdownDigitBox(value: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White,
      shadowElevation = 4.dp,
      modifier = Modifier.size(58.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = value,
          fontSize = 24.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color(0xFFC2410C),
          fontFamily = FontFamily.Monospace
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}

// ==============================================================================
// FEATURE 2: INTERACTIVE FLASHCARDS SYSTEM (ඩිජිටල් මතක කාඩ්පත්)
// ==============================================================================

data class StudyDeckFlashcard(
  val id: String,
  val subject: String,
  val grade: String,
  val questionFront: String,
  val answerBack: String,
  val explanationTip: String,
  var isMastered: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveFlashcardsScreen(
  onBack: () -> Unit
) {
  val allCards = remember {
    mutableStateListOf(
      // Science
      StudyDeckFlashcard(
        id = "fc_sci_1",
        subject = "විද්‍යාව",
        grade = "11",
        questionFront = "ප්‍රභාසංස්ලේෂණයට අවශ්‍ය සාධක 4 මොනවාද?",
        answerBack = "1. සූර්යාලෝකය\n2. ක්ලෝරෆිල් (පත්‍රහරිතය)\n3. කාබන් ඩයොක්සයිඩ් (CO₂)\n4. ජලය (H₂O)",
        explanationTip = "ප්‍රතිඵලය: ග්ලූකෝස් සහ ඔක්සිජන් පිටවීම"
      ),
      StudyDeckFlashcard(
        id = "fc_sci_2",
        subject = "විද්‍යාව",
        grade = "11",
        questionFront = "නිව්ටන්ගේ දෙවන චලිත නියමය කුමක්ද?",
        answerBack = "F = ma\n(බලය = ස්කන්ධය × ත්වරණය)",
        explanationTip = "බලයේ ඒකකය නිව්ටන් (N) හෙවත් kg m/s² වේ."
      ),
      StudyDeckFlashcard(
        id = "fc_sci_3",
        subject = "විද්‍යාව",
        grade = "10",
        questionFront = "මිනිස් සිරුරේ විශාලතම ග්‍රන්ථිය කුමක්ද?",
        answerBack = "අක්මාව (Liver)",
        explanationTip = "පිත නිපදවීම හා විෂහරණය සිදු කරයි."
      ),
      // Mathematics
      StudyDeckFlashcard(
        id = "fc_math_1",
        subject = "ගණිතය",
        grade = "11",
        questionFront = "වෘත්තයක වර්ගඵලය සෙවීමේ සූත්‍රය කුමක්ද?",
        answerBack = "A = πr²\n(π = 22/7 හෝ 3.1416)",
        explanationTip = "පරිධිය = 2πr"
      ),
      StudyDeckFlashcard(
        id = "fc_math_2",
        subject = "ගණිතය",
        grade = "10",
        questionFront = "පයිතගරස් ප්‍රමේය ප්‍රකාශ කරන්න.",
        answerBack = "ඍජුකෝණී ත්‍රිකෝණයක කර්ණයේ වර්ගය = අනෙක් පාද දෙකේ වර්ගවල එකතුව (a² + b² = c²)",
        explanationTip = "උදා: 3, 4, 5 හෝ 5, 12, 13"
      ),
      // History
      StudyDeckFlashcard(
        id = "fc_hist_1",
        subject = "ඉතිහාසය",
        grade = "11",
        questionFront = "1815 උඩරට ගිවිසුම අත්සන් කළේ කවුරුන්ද?",
        answerBack = "රොබට් බ්‍රවුන්රිග් ආණ්ඩුකාරවරයා සහ උඩරට ප්‍රධාන නිලමේවරුන්",
        explanationTip = "දිනය: 1815 මාර්තු 02"
      ),
      StudyDeckFlashcard(
        id = "fc_hist_2",
        subject = "ඉතිහාසය",
        grade = "10",
        questionFront = "මහා පරාක්‍රමබාහු රජුගේ අගනුවර කුමක්ද?",
        answerBack = "පොළොන්නරුව (පරාක්‍රම සමුද්‍රය නිර්මාණය කරන ලදී)",
        explanationTip = "\"අහසින් වැටෙන එක දිය බිඳක්වත්...\" ප්‍රසිද්ධ ප්‍රකාශය."
      ),
      // Buddhism
      StudyDeckFlashcard(
        id = "fc_bud_1",
        subject = "බුද්ධ ධර්මය",
        grade = "11",
        questionFront = "චතුරාර්ය සත්‍යය 4 මොනවාද?",
        answerBack = "1. දුක්ඛ සත්‍යය\n2. සමුදය සත්‍යය\n3. නිරෝධ සත්‍යය\n4. මාර්ග සත්‍යය (ආර්ය අෂ්ටාංගික මාර්ගය)",
        explanationTip = "දම්සක් පැවතුම් සූත්‍රයේදී දේශනා කරන ලදී."
      ),
      // English
      StudyDeckFlashcard(
        id = "fc_eng_1",
        subject = "English",
        grade = "11",
        questionFront = "Past Participle form of:\n1. Speak\n2. Write\n3. Begin",
        answerBack = "1. Spoken\n2. Written\n3. Begun",
        explanationTip = "Used in Present Perfect: has/have + V3"
      ),
      // ICT
      StudyDeckFlashcard(
        id = "fc_ict_1",
        subject = "ICT",
        grade = "11",
        questionFront = "HTML හි full form එක සහ Heading Tags මොනවාද?",
        answerBack = "HyperText Markup Language\nHeading tags: <h1> සිට <h6> දක්වා",
        explanationTip = "<h1> විශාලතම ප්‍රධාන මාතෘකාවයි."
      )
    )
  }

  val subjects = listOf("සියල්ල (All)", "විද්‍යාව", "ගණිතය", "ඉතිහාසය", "බුද්ධ ධර්මය", "English", "ICT")
  var selectedSubject by remember { mutableStateOf("සියල්ල (All)") }

  val filteredCards = remember(selectedSubject, allCards.size) {
    if (selectedSubject == "සියල්ල (All)") allCards else allCards.filter { it.subject == selectedSubject }
  }

  var currentIndex by remember { mutableStateOf(0) }
  var isFlipped by remember { mutableStateOf(false) }

  // Keep index in bound
  LaunchedEffect(filteredCards.size) {
    if (currentIndex >= filteredCards.size) {
      currentIndex = 0
    }
    isFlipped = false
  }

  val masteredCount = filteredCards.count { it.isMastered }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("💡 Digital Flashcards (මතක කාඩ්පත්)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("ඉක්මන් විභාග මතක් කිරීම් & සූත්‍ර", fontSize = 10.sp, color = Color(0xFFBBF7D0))
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF15803D))
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))
        .padding(paddingValues)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Subject Filter Chips
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(subjects) { subj ->
          FilterChip(
            selected = selectedSubject == subj,
            onClick = {
              selectedSubject = subj
              currentIndex = 0
              isFlipped = false
            },
            label = { Text(subj, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF15803D),
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Mastery Stats Bar
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "කාඩ්පත්: ${if (filteredCards.isEmpty()) 0 else currentIndex + 1} / ${filteredCards.size}",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF1E293B)
          )
          Text(
            text = "✅ ප්‍රගුණ කළ ප්‍රමාණය: $masteredCount",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF15803D)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (filteredCards.isEmpty()) {
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text("මෙම විෂය සඳහා කාඩ්පත් හමු නොවීය.", color = Color(0xFF64748B))
        }
      } else {
        val currentCard = filteredCards[currentIndex.coerceIn(0, filteredCards.size - 1)]

        // FLIP CARD (Interactive Animated Card)
        Card(
          onClick = { isFlipped = !isFlipped },
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) Color(0xFF1E293B) else Color.White
          ),
          border = BorderStroke(
            2.dp,
            if (isFlipped) Color(0xFF38BDF8) else Color(0xFF86EFAC)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 8.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Card Header Tag
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isFlipped) Color(0xFF334155) else Color(0xFFDCFCE7)
              ) {
                Text(
                  text = "${currentCard.subject} • ${currentCard.grade} ශ්‍රේණිය",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isFlipped) Color(0xFF38BDF8) else Color(0xFF15803D),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isFlipped) Color(0xFF0284C7) else Color(0xFFE2E8F0)
              ) {
                Text(
                  text = if (isFlipped) "💡 පිළිතුර (Answer)" else "❓ ප්‍රශ්නය (Question)",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isFlipped) Color.White else Color(0xFF475569),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            // Card Body (Question or Answer)
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 16.dp)
            ) {
              if (!isFlipped) {
                Text(
                  text = currentCard.questionFront,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F172A),
                  textAlign = TextAlign.Center,
                  lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                  text = "👆 පිළිතුර බැලීමට කාඩ්පත ස්පර්ශ කරන්න (Tap to Flip)",
                  fontSize = 11.sp,
                  color = Color(0xFF94A3B8),
                  textAlign = TextAlign.Center
                )
              } else {
                Text(
                  text = currentCard.answerBack,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF38BDF8),
                  textAlign = TextAlign.Center,
                  lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF334155),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "💡 Tip: ${currentCard.explanationTip}",
                    fontSize = 11.sp,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                  )
                }
              }
            }

            // Card Bottom Actions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.TouchApp, contentDescription = "Tap", tint = if (isFlipped) Color(0xFF94A3B8) else Color(0xFF15803D), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Flip Card",
                fontSize = 11.sp,
                color = if (isFlipped) Color(0xFF94A3B8) else Color(0xFF15803D),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation & Mastery Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Previous Button
          OutlinedButton(
            onClick = {
              if (currentIndex > 0) {
                currentIndex--
                isFlipped = false
              }
            },
            enabled = currentIndex > 0,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.NavigateBefore, contentDescription = "Prev")
            Spacer(modifier = Modifier.width(4.dp))
            Text("පෙර", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          // Mark Mastered Button
          Button(
            onClick = {
              currentCard.isMastered = !currentCard.isMastered
              if (currentIndex < filteredCards.size - 1) {
                currentIndex++
                isFlipped = false
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (currentCard.isMastered) Color(0xFF15803D) else Color(0xFF0284C7)
            ),
            modifier = Modifier.weight(1.5f)
          ) {
            Icon(
              if (currentCard.isMastered) Icons.Default.CheckCircle else Icons.Default.Done,
              contentDescription = "Mastered",
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              if (currentCard.isMastered) "ප්‍රගුණ කළා ✓" else "මතක තබාගත්තා",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Next Button
          Button(
            onClick = {
              if (currentIndex < filteredCards.size - 1) {
                currentIndex++
                isFlipped = false
              }
            },
            enabled = currentIndex < filteredCards.size - 1,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.weight(1f)
          ) {
            Text("ඊළඟ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.NavigateNext, contentDescription = "Next")
          }
        }
      }
    }
  }
}

// ==============================================================================
// FEATURE 3: DAILY STUDY STREAK & BADGES (දෛනික අධ්‍යයන පුරුද්ද, XP ලකුණු සහ පදක්කම්)
// ==============================================================================

data class QuestItem(
  val id: String,
  val title: String,
  val xpReward: Int,
  val isCompleted: Boolean
)

data class BadgeItem(
  val id: String,
  val titleSinhala: String,
  val iconEmoji: String,
  val description: String,
  val isUnlocked: Boolean,
  val unlockedDate: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyStreakAndBadgesScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  var currentStreakDays by remember { mutableStateOf(7) }
  var totalXp by remember { mutableStateOf(1450) }
  var studentLevel by remember { mutableStateOf(4) }

  val quests = remember {
    mutableStateListOf(
      QuestItem("q_1", "අද කෙටි සටහන් 1ක් සම්පූර්ණයෙන් කියවීම", 50, true),
      QuestItem("q_2", "Flashcards 5ක් ප්‍රගුණ කිරීම", 30, true),
      QuestItem("q_3", "ඉංග්‍රීසි කථන වාක්‍ය 2ක් සවන්දීම හා කීම", 40, true),
      QuestItem("q_4", "ප්‍රශ්න පත්‍රයක බහුවරණ ප්‍රශ්න 10ක් විසඳීම", 60, false),
      QuestItem("q_5", "මිතුරෙකු සමඟ අධ්‍යයන සටහනක් Share කිරීම", 20, false)
    )
  }

  val badges = listOf(
    BadgeItem("b_1", "🔥 දින 7ක නොනැවතුණු Streak", "🔥", "දින 7ක් අඛණ්ඩව ඇප් එක භාවිතා කර අධ්‍යයනය කිරීම.", true, "2026-08-18"),
    BadgeItem("b_2", "🧠 විද්‍යා විශාරද (Science Master)", "🔬", "විද්‍යාව කෙටි සටහන් 10ක් කියවා අවසන් කිරීම.", true, "2026-08-15"),
    BadgeItem("b_3", "📐 ගණිත සූත්‍ර ශූරයා (Math Wizard)", "📐", "ජ්‍යාමිතිය හා වීජ ගණිතය සූත්‍ර 20ක් ප්‍රගුණ කිරීම.", true, "2026-08-10"),
    BadgeItem("b_4", "🎙️ English Fluent Speaker", "🎙️", "ඉංග්‍රීසි කථන පුහුණුවෙන් 90%+ ලකුණු ලබාගැනීම.", true, "2026-08-17"),
    BadgeItem("b_5", "🏆 O/L All-Rounder (9A ඉලක්කය)", "🏆", "සියලු විෂයන්ගේ සටහන් හා ප්‍රශ්න පත්‍ර සාර්ථකව හැදෑරීම.", false, null),
    BadgeItem("b_6", "⚡ වේගවත් පිළිතුරු දෙන්නා (Speedster)", "⚡", "මිනිත්තු 15කින් ප්‍රශ්න 20කට නිවැරදිව පිළිතුරු දීම.", false, null)
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("🔥 Study Streaks & Badges", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("දෛනික පුරුද්ද, XP ලකුණු සහ පදක්කම්", fontSize = 10.sp, color = Color(0xFFFEF08A))
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEA580C))
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. STREAK HERO CARD
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color(0xFFEA580C), Color(0xFFC2410C), Color(0xFF9A3412))
                )
              )
              .padding(18.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("🔥", fontSize = 42.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "දින $currentStreakDays ක අඛණ්ඩ Streak එකක්!",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = "ඔබ දින 7ක් අඛණ්ඩව දිනපතා පාඩම් කරමින් සිටී!",
                fontSize = 11.sp,
                color = Color(0xFFFFEDD5)
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Weekday Dots
              val weekDays = listOf("සඳු", "අඟ", "බදා", "බ්‍රහ", "සිකු", "සෙන", "ඉරි")
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                weekDays.forEachIndexed { idx, day ->
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (idx <= 6) Color(0xFFFDE047) else Color.White.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Check, contentDescription = "Checked", tint = Color(0xFF9A3412), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(day, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      // 2. LEVEL & XP STATS
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
              ) {
                Text("⭐", fontSize = 24.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Level $studentLevel: O/L Scholar",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp,
                  color = Color(0xFF0F172A)
                )
                Text(
                  text = "$totalXp XP ලකුණු උපයා ඇත",
                  fontSize = 11.sp,
                  color = Color(0xFFD97706),
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFEF3C7),
              border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
              Text(
                text = "Next: Lvl 5 (1600 XP)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 3. DAILY QUESTS
      item {
        Text(
          text = "🎯 අද දවසේ Quests (XP ලකුණු ලබාගන්න)",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = Color(0xFF1E293B)
        )
      }

      items(quests) { quest ->
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (quest.isCompleted) Color(0xFFF0FDF4) else Color.White,
          border = BorderStroke(1.dp, if (quest.isCompleted) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = "Status",
                tint = if (quest.isCompleted) Color(0xFF16A34A) else Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = quest.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (quest.isCompleted) Color(0xFF15803D) else Color(0xFF1E293B)
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFEF9C3)
            ) {
              Text(
                text = "+${quest.xpReward} XP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF854D0E),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // 4. UNLOCKED BADGES GRID
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "🏆 ඔබ උපයාගත් පදක්කම් (Achievement Badges)",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = Color(0xFF1E293B)
        )
      }

      items(badges) { badge ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF1F5F9)
          ),
          border = BorderStroke(
            1.dp,
            if (badge.isUnlocked) Color(0xFFFDE68A) else Color(0xFFE2E8F0)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (badge.isUnlocked) Color(0xFFFEF3C7) else Color(0xFFE2E8F0)),
              contentAlignment = Alignment.Center
            ) {
              Text(badge.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = badge.titleSinhala,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (badge.isUnlocked) Color(0xFF0F172A) else Color(0xFF94A3B8)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = badge.description,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
              if (badge.unlockedDate != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "✓ හිමිවූ දිනය: ${badge.unlockedDate}",
                  fontSize = 9.sp,
                  color = Color(0xFF16A34A),
                  fontWeight = FontWeight.Bold
                )
              }
            }

            if (!badge.isUnlocked) {
              Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}

// ==============================================================================
// FEATURE 4: SPOKEN ENGLISH AI VOICE RECOGNITION (උච්චාරණ පුහුණු AI Voice Mic)
// ==============================================================================

data class SpokenSentencePractice(
  val id: String,
  val englishText: String,
  val phoneticGuide: String,
  val sinhalaMeaning: String,
  val difficulty: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpokenEnglishVoiceRecognitionScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

  // Practice Sentences
  val practiceList = listOf(
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

  var selectedIndex by remember { mutableStateOf(0) }
  val currentItem = practiceList[selectedIndex]

  var recognizedSpokenText by remember { mutableStateOf("") }
  var matchAccuracyPercentage by remember { mutableStateOf<Int?>(null) }
  var isListening by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf("මයික්‍රෆෝන් බොත්තම ඔබා ඉංග්‍රීසි වාක්‍යය ශබ්ද නඟා කියවන්න.") }

  // TTS Setup
  DisposableEffect(Unit) {
    var tts: TextToSpeech? = null
    tts = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        tts?.language = Locale.US
      }
    }
    textToSpeech = tts
    onDispose {
      tts?.stop()
      tts?.shutdown()
    }
  }

  fun speak(text: String, speed: Float = 1.0f) {
    textToSpeech?.setSpeechRate(speed)
    textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PRACTICE_TTS")
  }

  // Calculate similarity
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

  // Native Speech Recognizer Launcher
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isListening = false
    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
      val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      if (!matches.isNullOrEmpty()) {
        val spoken = matches[0]
        recognizedSpokenText = spoken
        val accuracy = calculateAccuracy(spoken, currentItem.englishText)
        matchAccuracyPercentage = accuracy
        statusMessage = when {
          accuracy >= 85 -> "🌟 විශිෂ්ටයි! ඉතාමත් පැහැදිලි නිවැරදි උච්චාරණයක්! (+50 XP)"
          accuracy >= 50 -> "👍 හොඳයි! නැවත උත්සාහ කර 100% ට ළඟා වන්න."
          else -> "💡 නැවත සවන්දී පැහැදිලිව නැවත කියවන්න."
        }
      }
    } else {
      statusMessage = "කටහඬ හඳුනාගැනීම අවලංගු විය. නැවත උත්සාහ කරන්න."
    }
  }

  // Permission Launcher
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
        statusMessage = "🎙️ සවන්දෙමින් පවතී... වාක්‍යය ශබ්ද නඟා කියවන්න."
        speechLauncher.launch(intent)
      } catch (e: Exception) {
        isListening = false
        Toast.makeText(context, "Speech recognition is not available on this device", Toast.LENGTH_SHORT).show()
      }
    } else {
      Toast.makeText(context, "Microphone permission is required for voice practice", Toast.LENGTH_SHORT).show()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("🎙️ English Voice & Speech Recognition", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("කටහඬින් උච්චාරණ නිරවද්‍යතාවය මැනීම", fontSize = 10.sp, color = Color(0xFFBAE6FD))
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0369A1))
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // SENTENCE SELECTOR CHIPS
      item {
        Text("වාක්‍යය තෝරන්න (Practice Sentences):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(practiceList.size) { idx ->
            FilterChip(
              selected = selectedIndex == idx,
              onClick = {
                selectedIndex = idx
                recognizedSpokenText = ""
                matchAccuracyPercentage = null
                statusMessage = "මයික්‍රෆෝන් බොත්තම ඔබා ඉංග්‍රීසි වාක්‍යය ශබ්ද නඟා කියවන්න."
              },
              label = { Text("වාක්‍යය ${idx + 1} (${practiceList[idx].difficulty})", fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF0369A1),
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // CURRENT SENTENCE CARD
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.5.dp, Color(0xFFBAE6FD)),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFE0F2FE)
              ) {
                Text(
                  text = "🎯 ${currentItem.difficulty}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0369A1),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Row {
                IconButton(onClick = { speak(currentItem.englishText, 1.0f) }) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Speak Normal", tint = Color(0xFF0284C7))
                }
                IconButton(onClick = { speak(currentItem.englishText, 0.7f) }) {
                  Icon(Icons.Default.SlowMotionVideo, contentDescription = "Speak Slow", tint = Color(0xFFD97706))
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = currentItem.englishText,
              fontSize = 17.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF0F172A),
              lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = currentItem.phoneticGuide,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "තේරුම: ${currentItem.sinhalaMeaning}",
              fontSize = 12.sp,
              color = Color(0xFF334155),
              lineHeight = 18.sp
            )
          }
        }
      }

      // MIC RECORD ACTION CARD
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (matchAccuracyPercentage != null && matchAccuracyPercentage!! >= 80) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
          ),
          border = BorderStroke(
            1.dp,
            if (matchAccuracyPercentage != null && matchAccuracyPercentage!! >= 80) Color(0xFF86EFAC) else Color(0xFFCBD5E1)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "🎙️ ඔබේ කටහඬ පරීක්ෂා කරන්න",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = statusMessage,
              fontSize = 11.sp,
              color = Color(0xFF475569),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Big Mic Button
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
                    statusMessage = "🎙️ සවන්දෙමින් පවතී... දැන් කියවන්න."
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
              modifier = Modifier.size(72.dp)
            ) {
              Icon(
                if (isListening) Icons.Default.Hearing else Icons.Default.Mic,
                contentDescription = "Mic",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
              )
            }

            if (recognizedSpokenText.isNotBlank()) {
              Spacer(modifier = Modifier.height(16.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text("ඔබ පැවසූ දෙය (You said):", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "\"$recognizedSpokenText\"",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                  )

                  if (matchAccuracyPercentage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text("නිරවද්‍යතාවය (Accuracy):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                      Text(
                        text = "$matchAccuracyPercentage%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (matchAccuracyPercentage!! >= 80) Color(0xFF16A34A) else if (matchAccuracyPercentage!! >= 50) Color(0xFFD97706) else Color(0xFFDC2626)
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                      progress = { (matchAccuracyPercentage!! / 100f).coerceIn(0f, 1f) },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                      color = if (matchAccuracyPercentage!! >= 80) Color(0xFF16A34A) else if (matchAccuracyPercentage!! >= 50) Color(0xFFD97706) else Color(0xFFDC2626),
                      trackColor = Color(0xFFE2E8F0)
                    )
                  }
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}

// ==============================================================================
// FEATURE 5: BOOKMARK & FAVORITES SCREEN (ප්‍රියතම සටහන් Bookmark කර ගැනීම)
// ==============================================================================

data class BookmarkItem(
  val id: String,
  val title: String,
  val subject: String,
  val grade: String,
  val type: String, // "NOTE", "PAPER", "FORMULA", "ESSAY"
  val pdfUri: String? = null,
  val savedDate: String = "2026-08-18"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksAndFavoritesScreen(
  onBack: () -> Unit,
  onOpenPdf: (pdfUri: String, title: String) -> Unit,
  savedBookmarks: SnapshotStateList<BookmarkItem>
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var filterType by remember { mutableStateOf("සියල්ල") }

  val filterTypes = listOf("සියල්ල", "කෙටි සටහන්", "ප්‍රශ්න පත්‍ර", "සූත්‍ර/වගු")

  val filteredList = remember(searchQuery, filterType, savedBookmarks.size) {
    savedBookmarks.filter { item ->
      val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) || item.subject.contains(searchQuery, ignoreCase = true)
      val matchesType = when (filterType) {
        "කෙටි සටහන්" -> item.type == "NOTE"
        "ප්‍රශ්න පත්‍ර" -> item.type == "PAPER"
        "සූත්‍ර/වගු" -> item.type == "FORMULA"
        else -> true
      }
      matchesSearch && matchesType
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("🔖 මගේ Bookmark & ප්‍රියතම සටහන්", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("සුරැකි අධ්‍යයන අන්තර්ගතයන් (${savedBookmarks.size})", fontSize = 10.sp, color = Color(0xFFFDE68A))
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4338CA))
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // SEARCH BAR
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("සටහන් නම හෝ විෂය සොයන්න...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF4338CA)) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
              }
            }
          },
          shape = RoundedCornerShape(14.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // TYPE FILTER CHIPS
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(filterTypes) { type ->
            FilterChip(
              selected = filterType == type,
              onClick = { filterType = type },
              label = { Text(type, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF4338CA),
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      if (filteredList.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("📂", fontSize = 42.sp)
              Spacer(modifier = Modifier.height(10.dp))
              Text("තවමත් සටහන් Bookmark කර නොමැත.", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF64748B))
              Text("කෙටි සටහන් හෝ ප්‍රශ්න පත්‍රවල ඇති 🔖 ලකුණ ඔබා මෙහි සුරකින්න.", fontSize = 11.sp, color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
            }
          }
        }
      } else {
        items(filteredList) { item ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                      when (item.type) {
                        "NOTE" -> Color(0xFFEFF6FF)
                        "PAPER" -> Color(0xFFFEF2F2)
                        else -> Color(0xFFF0FDF4)
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (item.type) {
                      "NOTE" -> Icons.Default.MenuBook
                      "PAPER" -> Icons.Default.Description
                      else -> Icons.Default.Calculate
                    },
                    contentDescription = item.type,
                    tint = when (item.type) {
                      "NOTE" -> Color(0xFF2563EB)
                      "PAPER" -> Color(0xFFDC2626)
                      else -> Color(0xFF16A34A)
                    },
                    modifier = Modifier.size(24.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = Color(0xFFF1F5F9)
                    ) {
                      Text(
                        text = "${item.subject} • ${item.grade} වසර",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "📅 ${item.savedDate}",
                      fontSize = 9.sp,
                      color = Color(0xFF94A3B8)
                    )
                  }
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.pdfUri != null) {
                  Button(
                    onClick = { onOpenPdf(item.pdfUri, item.title) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                  ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Open", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("බලන්න", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }

                IconButton(
                  onClick = {
                    savedBookmarks.remove(item)
                    Toast.makeText(context, "Bookmark ඉවත් කරන ලදී", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Icon(Icons.Default.BookmarkRemove, contentDescription = "Remove", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}
