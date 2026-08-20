package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ==============================================================================
// FEATURE 2: DETAILED ANALYTICS & WEAK AREA DIAGNOSTIC (ප්‍රගති විශ්ලේෂණය)
// ==============================================================================

data class SubjectPerformance(
  val subjectName: String,
  val grade: String,
  val testsTaken: Int,
  val averageScore: Int, // Percentage 0-100
  val accuracyRate: Float,
  val weakTopics: List<String>,
  val strongTopics: List<String>,
  val recommendation: String,
  val color: Color
)

object AnalyticsRepository {
  fun getSubjectPerformances(grade: String): List<SubjectPerformance> {
    return listOf(
      SubjectPerformance(
        subjectName = "විද්‍යාව",
        grade = grade,
        testsTaken = 14,
        averageScore = 78,
        accuracyRate = 0.78f,
        weakTopics = listOf("ප්‍රකාශ සංශ්ලේෂණයේ ආලෝක ප්‍රතික්‍රියාව", "න්‍යෂ්ටික විකිරණ හා අර්ධ ආයු කාලය", "ද්‍රාව්‍යතාව හා සංශුද්ධතාව"),
        strongTopics = listOf("නිව්ටන්ගේ නියම", "ඕම්ගේ නියමය", "සෛල ව්‍යුහය"),
        recommendation = "ජෛව රසායන හා ශක්ති පරිවර්තන පාඩම්වල කෙටි සටහන් නැවත කියවා Flashcards පුහුණු වන්න.",
        color = Color(0xFF1B5E20)
      ),
      SubjectPerformance(
        subjectName = "ගණිතය",
        grade = grade,
        testsTaken = 18,
        averageScore = 84,
        accuracyRate = 0.84f,
        weakTopics = listOf("ත්‍රිකෝණමිතිය උන්නතාංශ හා අවනතාංශ", "වර්ගජ සමීකරණ සූත්‍රය භාවිතය"),
        strongTopics = listOf("පයිතගරස් ප්‍රමේයය", "වර්ගඵල හා පරිමාව", "සමාන්තර ශ්‍රේඪි"),
        recommendation = "ත්‍රිකෝණමිතික අනුපාත සූත්‍ර පත්‍රිකාව නිතර බලා ගැටලු පියවරෙන් පියවර විසඳන්න.",
        color = Color(0xFF0D47A1)
      ),
      SubjectPerformance(
        subjectName = "ඉතිහාසය",
        grade = grade,
        testsTaken = 10,
        averageScore = 91,
        accuracyRate = 0.91f,
        weakTopics = listOf("යටත්විජිත යුගයේ ව්‍යවස්ථා ප්‍රතිසංස්කරණ (1833-1948)"),
        strongTopics = listOf("අනුරාධපුර වාරි ශිෂ්ටාචාරය", "මහසෙන් & පරාක්‍රමබාහු රජවරු", "සීගිරි නිර්මාණ"),
        recommendation = "ඉතිහාස කාලරේඛාව (Historical Timeline) භාවිතයෙන් වර්ෂ සහ ගිවිසුම් මතක තබාගන්න.",
        color = Color(0xFF6A1B9A)
      ),
      SubjectPerformance(
        subjectName = "තොරතුරු තාක්ෂණය (ICT)",
        grade = grade,
        testsTaken = 8,
        averageScore = 88,
        accuracyRate = 0.88f,
        weakTopics = listOf("බූලීය වීජ ගණිතයේ Karnaugh Maps සරල කිරීම්"),
        strongTopics = listOf("Logic Gates", "Binary සංඛ්‍යා පරිවර්තන", "CPU ව්‍යුහය"),
        recommendation = "Logic Gates සත්‍යතා වගු හා සූත්‍ර චාට් එක භාවිතයෙන් අභ්‍යාස කරන්න.",
        color = Color(0xFF00695C)
      ),
      SubjectPerformance(
        subjectName = "බුද්ධ ධර්මය",
        grade = grade,
        testsTaken = 6,
        averageScore = 95,
        accuracyRate = 0.95f,
        weakTopics = listOf("පටිච්චසමුප්පාද ධර්මයේ අංග 12 අනුපිළිවෙළ"),
        strongTopics = listOf("චතුරාර්ය සත්‍යය", "ආර්ය අෂ්ටාංගික මාර්ගය", "ශාසන ඉතිහාසය"),
        recommendation = "විශිෂ්ට සාමාර්ථයක් (A) සඳහා සූත්‍ර විග්‍රහ කෙටි සටහන් කියවන්න.",
        color = Color(0xFFB06000)
      )
    )
  }
}

@Composable
fun StudyAnalyticsScreen(
  grade: String,
  onBack: () -> Unit,
  onOpenNotesForSubject: (String) -> Unit = {}
) {
  val performances = remember(grade) { AnalyticsRepository.getSubjectPerformances(grade) }
  val totalTests = performances.sumOf { it.testsTaken }
  val overallAvg = if (performances.isNotEmpty()) performances.map { it.averageScore }.average().toInt() else 0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // Header Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            onClick = onBack,
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color(0xFF1E293B),
              modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "ප්‍රගති විශ්ලේෂණය (Analytics)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "$grade ශ්‍රේණිය • Real-time Diagnostic Report",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFEFF6FF),
          border = BorderStroke(1.dp, Color(0xFFBFDBFE))
        ) {
          Text(
            text = "Grade $grade",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D4ED8),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Overall Score KPI Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "සමස්ත අධ්‍යයන කාර්යක්ෂමතාව (Overall Mastery)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "$overallAvg%",
                  fontSize = 36.sp,
                  fontWeight = FontWeight.Black,
                  color = if (overallAvg >= 75) Color(0xFF16A34A) else Color(0xFF2563EB)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (overallAvg >= 75) "විශිෂ්ට සාමාර්ථය (A)" else "ඉතා හොඳයි (B)",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (overallAvg >= 75) Color(0xFF16A34A) else Color(0xFF2563EB),
                  modifier = Modifier.padding(bottom = 6.dp)
                )
              }
              Text(
                text = "සමස්ත MCQs සහ Quizzes $totalTests කින් ලබාගත් ප්‍රතිඵලය",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }

            // Circular indicator look
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0FDF4)),
              contentAlignment = Alignment.Center
            ) {
              Text("🏆", fontSize = 28.sp)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          LinearProgressIndicator(
            progress = { overallAvg / 100f },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (overallAvg >= 75) Color(0xFF16A34A) else Color(0xFF2563EB),
            trackColor = Color(0xFFE2E8F0)
          )

          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            StatMiniBox(title = "සම්පූර්ණ Quizzes", value = "$totalTests Sets", icon = "📝")
            StatMiniBox(title = "සාමාන්‍ය වේගය", value = "18 තත්/MCQ", icon = "⚡")
            StatMiniBox(title = "පාඩම් Streak", value = "7 දින", icon = "🔥")
          }
        }
      }
    }

    // Weak Topic Diagnostic Section (අවධානය යොමු කළ යුතු දුර්වල මාතෘකා)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⚠️", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "වැඩි අවධානයක් අවශ්‍ය මාතෘකා (Weak Topics)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF92400E)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "ඔබ පසුගිය Quizzes වලදී වැඩිපුරම වරදවා ගත් සංකල්ප පහත දැක්වේ. විභාගයට පෙර මේවා පිළිබඳ කෙටි සටහන් නැවත කියවන්න:",
            fontSize = 11.sp,
            color = Color(0xFFB45309),
            lineHeight = 15.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          performances.flatMap { p -> p.weakTopics.map { p.subjectName to it } }.take(4).forEach { (subj, topic) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFD97706))
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "[$subj] $topic",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF78350F)
              )
            }
          }
        }
      }
    }

    // Subject Breakdown Cards
    item {
      Text(
        text = "විෂය අනුව සාමාර්ථතා විශ්ලේෂණය (Subject Breakdown)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F172A),
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    items(performances) { perf ->
      SubjectPerformanceCard(performance = perf, onReviewNotes = { onOpenNotesForSubject(perf.subjectName) })
    }
  }
}

@Composable
fun StatMiniBox(title: String, value: String, icon: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(icon, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(2.dp))
    Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
  }
}

@Composable
fun SubjectPerformanceCard(
  performance: SubjectPerformance,
  onReviewNotes: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(performance.color)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = performance.subjectName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
        }
        Text(
          text = "${performance.averageScore}%",
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = performance.color
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      LinearProgressIndicator(
        progress = { performance.accuracyRate },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = performance.color,
        trackColor = Color(0xFFF1F5F9)
      )

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "💡 උපදෙස: ${performance.recommendation}",
        fontSize = 11.sp,
        color = Color(0xFF475569),
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          onClick = onReviewNotes,
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF8FAFC),
          border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "කෙටි සටහන් බලන්න",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF334155), modifier = Modifier.size(12.dp))
          }
        }
      }
    }
  }
}

// ==============================================================================
// FEATURE 3: STRUCTURED & ESSAY PRACTICE HUB (ව්‍යුහගත හා රචනා ප්‍රශ්න & MARKING SCHEMES)
// ==============================================================================

data class StructuredSubQuestion(
  val subIndex: String, // e.g. "(i)", "(ii)"
  val questionText: String,
  val marksAllocated: Int,
  val modelAnswer: String,
  val keyPoints: List<String>
)

data class StructuredEssayItem(
  val id: String,
  val grade: String,
  val subject: String,
  val topicSinhala: String,
  val type: String, // "STRUCTURED" (ව්‍යුහගත) or "ESSAY" (රචනා)
  val totalMarks: Int,
  val mainScenario: String,
  val subQuestions: List<StructuredSubQuestion>
)

object StructuredEssayRepository {
  fun getQuestions(grade: String, subjectFilter: String = "සියල්ල"): List<StructuredEssayItem> {
    val all = listOf(
      // Science Structured 1
      StructuredEssayItem(
        id = "sci_str_1",
        grade = grade,
        subject = "විද්‍යාව",
        topicSinhala = "ශාකවල ප්‍රභාසංශ්ලේෂණය සහ පත්‍ර ව්‍යුහය",
        type = "STRUCTURED",
        totalMarks = 15,
        mainScenario = "හරිත ශාක ආලෝක ශක්තිය රසායනික ශක්තිය බවට පත් කරන ප්‍රභාසංශ්ලේෂණ ක්‍රියාවලිය පිළිබඳ පහත ප්‍රශ්නවලට පිළිතුරු සපයන්න.",
        subQuestions = listOf(
          StructuredSubQuestion(
            subIndex = "(i)",
            questionText = "ප්‍රභාසංශ්ලේෂණයේ සමස්ත තුලිත රසායනික සමීකරණය ලියන්න.",
            marksAllocated = 3,
            modelAnswer = "6CO₂ + 6H₂O ──(හිරු එළිය/හරිතප්‍රද)──> C₆H₁₂O₆ + 6O₂",
            keyPoints = listOf("ප්‍රතික්‍රියක CO₂ හා H₂O නිවැරදිව ලිවීම (1 ලකුණ)", "ඵල C₆H₁₂O₆ හා O₂ නිවැරදිව ලිවීම (1 ලකුණ)", "තුලනය කිරීම (1 ලකුණ)")
          ),
          StructuredSubQuestion(
            subIndex = "(ii)",
            questionText = "ප්‍රභාසංශ්ලේෂණය සඳහා ආලෝකය අත්‍යවශ්‍ය බව පෙන්වන පරීක්ෂණයේදී පත්‍රය මධ්‍යසාරයේ බහා උණු කරන්නේ ඇයි?",
            marksAllocated = 3,
            modelAnswer = "පත්‍රයේ ඇති හරිතප්‍රද (Chlorophyll) ඉවත් කර පත්‍රය වර්ණහීන කර ගැනීම සඳහායි. එමඟින් අයඩින් පරීක්ෂාවේදී නිල්-කළු පැහැය පැහැදිලිව නිරීක්ෂණය කළ හැක.",
            keyPoints = listOf("හරිතප්‍රද ඉවත් කිරීමට / විරංජනය කිරීමට (2 ලකුණු)", "අයඩින් නිරීක්ෂණය පැහැදිලිව ලබාගැනීමට (1 ලකුණ)")
          ),
          StructuredSubQuestion(
            subIndex = "(iii)",
            questionText = "ප්‍රභාසංශ්ලේෂණ සීඝ්‍රතාව කෙරෙහි බලපාන ප්‍රධාන බාහිර සාධක 3ක් නම් කරන්න.",
            marksAllocated = 3,
            modelAnswer = "1. ආලෝක තීව්‍රතාව 2. කාබන්ඩයොක්සයිඩ් (CO₂) සාන්ද්‍රණය 3. උෂ්ණත්වය (ප්‍රශස්ථ 25°C - 35°C).",
            keyPoints = listOf("සාධක 3 නිවැරදිව නම් කිරීම (ලකුණු 3)")
          )
        )
      ),

      // Science Essay 1
      StructuredEssayItem(
        id = "sci_ess_1",
        grade = grade,
        subject = "විද්‍යාව",
        topicSinhala = "නිව්ටන්ගේ චලිත නියම හා ගම්‍යතා මූලධර්ම",
        type = "ESSAY",
        totalMarks = 20,
        mainScenario = "ස්කන්ධය 1200 kg වන මෝටර් රථයක් 20 ms⁻¹ ඒකාකාර ප්‍රවේගයෙන් ගමන් කර තත්පර 5ක් තුළ ඒකාකාරව මන්දනය වී නතර වේ.",
        subQuestions = listOf(
          StructuredSubQuestion(
            subIndex = "(A)",
            questionText = "මෝටර් රථයේ මන්දනය ගණනය කරන්න.",
            marksAllocated = 5,
            modelAnswer = "v = u + at සමීකරණය භාවිතයෙන්:\n0 = 20 + a(5)\n-5a = 20 => a = -4 ms⁻²\nඑබැවින් මන්දනය = 4 ms⁻² වේ.",
            keyPoints = listOf("චලිත සමීකරණය ලිවීම (2 ලකුණු)", "අගයන් ආදේශය (2 ලකුණු)", "ඒකක සහිත නිවැරදි පිළිතුර (1 ලකුණ)")
          ),
          StructuredSubQuestion(
            subIndex = "(B)",
            questionText = "රථය නැවැත්වීමට තිරිංග මඟින් යෙදූ ප්‍රතිරෝධී බලය F = ma සූත්‍රයෙන් ගණනය කරන්න.",
            marksAllocated = 5,
            modelAnswer = "F = ma\nF = 1200 kg × 4 ms⁻²\nF = 4800 N (නියුටන්)",
            keyPoints = listOf("F = ma සූත්‍රය (2 ලකුණු)", "ආදේශය (2 ලකුණු)", "ඒකකය (N) සහිත පිළිතුර (1 ලකුණ)")
          )
        )
      ),

      // Math Structured 1
      StructuredEssayItem(
        id = "math_str_1",
        grade = grade,
        subject = "ගණිතය",
        topicSinhala = "වර්ගජ සමීකරණ හා වර්ගපූර්ණය",
        type = "STRUCTURED",
        totalMarks = 10,
        mainScenario = "x² + 6x - 7 = 0 වර්ගජ සමීකරණය සැලකිල්ලට ගන්න.",
        subQuestions = listOf(
          StructuredSubQuestion(
            subIndex = "(i)",
            questionText = "සාධක සෙවීමේ ක්‍රමයෙන් x හි අගයන් සොයන්න.",
            marksAllocated = 5,
            modelAnswer = "x² + 7x - x - 7 = 0\nx(x + 7) - 1(x + 7) = 0\n(x + 7)(x - 1) = 0\nx = -7 හෝ x = 1",
            keyPoints = listOf("මැද පදය කැඩීම (2 ලකුණු)", "සාධක වෙන් කිරීම (2 ලකුණු)", "මූලයන් දෙක ලබාගැනීම (1 ලකුණ)")
          ),
          StructuredSubQuestion(
            subIndex = "(ii)",
            questionText = "වර්ගපූර්ණ ක්‍රමයෙන් (x + 3)² = 16 ආකාරයට සකසා විසඳුම තහවුරු කරන්න.",
            marksAllocated = 5,
            modelAnswer = "x² + 6x = 7\nx² + 6x + (3)² = 7 + 9\n(x + 3)² = 16\nx + 3 = ±4\nx = -3 + 4 = 1 හෝ x = -3 - 4 = -7",
            keyPoints = listOf("දෙපසටම 3² (9) එකතු කිරීම (2 ලකුණු)", "පූර්ණ වර්ගය ලිවීම (2 ලකුණු)", "විසඳුම තහවුරු කිරීම (1 ලකුණ)")
          )
        )
      ),

      // History Essay 1
      StructuredEssayItem(
        id = "hist_ess_1",
        grade = grade,
        subject = "ඉතිහාසය",
        topicSinhala = "පරාක්‍රමබාහු රජුගේ පාලන සමය හා වාරි ශිෂ්ටාචාරය",
        type = "ESSAY",
        totalMarks = 15,
        mainScenario = "'අහසින් වැටෙන එකදු දිය බිඳක්වත් මිනිසාගේ ප්‍රයෝජනයට නොගෙන මුහුදට ගලා යාමට ඉඩ නොදිය යුතුය' යන සංකල්පය පිළිබඳ රචනාවක් ලියන්න.",
        subQuestions = listOf(
          StructuredSubQuestion(
            subIndex = "(i)",
            questionText = "පරාක්‍රමබාහු රජු විසින් සිදුකළ ප්‍රධාන වාරි ප්‍රතිසංස්කරණ 3ක් පැහැදිලි කරන්න.",
            marksAllocated = 8,
            modelAnswer = "1. පරාක්‍රම සමුද්‍රය නිර්මාණය (තෝපාවැව, එරබදු වැව, දුඹුටුළු වැව එක්කර).\n2. අංගමැඩිල්ල අමුණ හා ඇළ මාර්ග පද්ධතිය.\n3. පැරණි වැව් දහස් ගණනක් ප්‍රතිසංස්කරණය කර රට සහලින් ස්වයංපෝෂිත කිරීම.",
            keyPoints = listOf("පරාක්‍රම සමුද්‍රය විස්තරය (3 ලකුණු)", "ඇළ මාර්ග හා අමුණු (3 ලකුණු)", "ආර්ථික ප්‍රතිලාභය (2 ලකුණු)")
          ),
          StructuredSubQuestion(
            subIndex = "(ii)",
            questionText = "පොළොන්නරු යුගයේ ආගමික හා සංස්කෘතික පුනරුදය කෙටියෙන් විග්‍රහ කරන්න.",
            marksAllocated = 7,
            modelAnswer = "ගල් විහාරය (උත්තරාරාමය) නිර්මාණය, ශාසන ශෝධනය සිදුකර සංඝ සාමග්‍රිය ඇතිකිරීම, සහ තිවංක පිළිම ගෙයි බිතුසිතුවම් නිර්මාණය කිරීම.",
            keyPoints = listOf("ගල් විහාරය හා ප්‍රතිමා (3 ලකුණු)", "ශාසන ශෝධනය (2 ලකුණු)", "කලා නිර්මාණ (2 ලකුණු)")
          )
        )
      )
    )

    return if (subjectFilter == "සියල්ල") all else all.filter { it.subject.contains(subjectFilter, ignoreCase = true) }
  }
}

@Composable
fun StructuredEssayHubScreen(
  grade: String,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: ව්‍යුහගත ප්‍රශ්න, 1: රචනා ප්‍රශ්න, 2: Marking Schemes (ලකුණු ක්‍රමය)
  var selectedSubjectFilter by remember { mutableStateOf("සියල්ල") }
  val questions = remember(grade, selectedSubjectFilter) { StructuredEssayRepository.getQuestions(grade, selectedSubjectFilter) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Top Bar
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              onClick = onBack,
              shape = CircleShape,
              color = Color(0xFFF1F5F9)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1E293B),
                modifier = Modifier.padding(8.dp).size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "ව්‍යුහගත හා රචනා පුහුණුව (Structured & Essay)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "$grade ශ්‍රේණිය • Marking Schemes & Model Answers",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFFF1F5F9),
          contentColor = BluePrimary,
          modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
          listOf("ව්‍යුහගත (Part A)", "රචනා (Part B)", "ලකුණු පටිපාටි (Marking)").forEachIndexed { index, label ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Subject Filter Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val subjects = listOf("සියල්ල", "විද්‍යාව", "ගණිතය", "ඉතිහාසය", "ICT")
          items(subjects) { s ->
            val isSelected = selectedSubjectFilter == s
            Surface(
              onClick = { selectedSubjectFilter = s },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) BluePrimary else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (isSelected) BluePrimary else Color(0xFFCBD5E1))
            ) {
              Text(
                text = s,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFF334155),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      }
    }

    // Question List
    val filteredList = when (selectedTab) {
      0 -> questions.filter { it.type == "STRUCTURED" }
      1 -> questions.filter { it.type == "ESSAY" }
      else -> questions // Marking Schemes (All)
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
    ) {
      if (filteredList.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "තෝරාගත් විෂය සඳහා ප්‍රශ්න සූදානම් වෙමින් පවතී...",
              color = Color(0xFF64748B),
              fontSize = 13.sp
            )
          }
        }
      } else {
        items(filteredList) { q ->
          StructuredQuestionCard(item = q, showMarkingMode = selectedTab == 2)
        }
      }
    }
  }
}

@Composable
fun StructuredQuestionCard(
  item: StructuredEssayItem,
  showMarkingMode: Boolean
) {
  var isExpanded by remember { mutableStateOf(showMarkingMode) }

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
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (item.type == "STRUCTURED") Color(0xFFEEF2FF) else Color(0xFFFAF5FF)
        ) {
          Text(
            text = "${item.subject} • ${if (item.type == "STRUCTURED") "ව්‍යුහගත ප්‍රශ්නය" else "රචනා ප්‍රශ්නය"}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.type == "STRUCTURED") Color(0xFF4338CA) else Color(0xFF7E22CE),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }

        Text(
          text = "ලකුණු ${item.totalMarks}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF16A34A)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = item.topicSinhala,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F172A)
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = item.mainScenario,
        fontSize = 12.sp,
        color = Color(0xFF475569),
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = Color(0xFFF1F5F9))
      Spacer(modifier = Modifier.height(10.dp))

      // Sub questions
      item.subQuestions.forEach { sub ->
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${sub.subIndex} ${sub.questionText}",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1E293B),
              modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "(${sub.marksAllocated} ලකුණු)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF64748B)
            )
          }

          if (isExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF0FDF4),
              border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "✅ සම්මත ආදර්ශ පිළිතුර (Model Answer):",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF166534)
                )
                Text(
                  text = sub.modelAnswer,
                  fontSize = 11.sp,
                  color = Color(0xFF14532D),
                  lineHeight = 15.sp,
                  modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                  text = "📌 ලකුණු දීමේ පටිපාටිය (Marking Rubric):",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF15803D),
                  modifier = Modifier.padding(top = 4.dp)
                )
                sub.keyPoints.forEach { pt ->
                  Text(
                    text = "• $pt",
                    fontSize = 10.sp,
                    color = Color(0xFF166534)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Button(
          onClick = { isExpanded = !isExpanded },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isExpanded) Color(0xFFF1F5F9) else Color(0xFF16A34A),
            contentColor = if (isExpanded) Color(0xFF334155) else Color.White
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (isExpanded) "පිළිතුරු සඟවන්න" else "ආදර්ශ පිළිතුරු & ලකුණු ක්‍රමය බලන්න",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// ==============================================================================
// FEATURE 5: QUICK REFERENCE FORMULA HANDBOOK & HISTORICAL TIMELINE HUB
// (සූත්‍ර, ඒකක, නියමයන් & ඉතිහාස කාලරේඛාව)
// ==============================================================================

data class FormulaItem(
  val id: String,
  val category: String, // "MATH", "SCIENCE", "ICT", "COMMERCE", "TIMELINE"
  val nameSinhala: String,
  val formula: String,
  val explanation: String,
  val unitsOrEra: String,
  var isBookmarked: Boolean = false
)

object FormulaRepository {
  fun getFormulas(): List<FormulaItem> {
    return listOf(
      // Math Formulas
      FormulaItem(
        id = "f_math_1",
        category = "MATH",
        nameSinhala = "පයිතගරස් ප්‍රමේයය (Pythagoras Theorem)",
        formula = "a² + b² = c²",
        explanation = "ඍජුකෝණී ත්‍රිකෝණයක කර්ණයේ වර්ගය අනෙක් පාද දෙකේ වර්ගවල එකතුවට සමාන වේ.",
        unitsOrEra = "ගණිතය • ජ්‍යාමිතිය"
      ),
      FormulaItem(
        id = "f_math_2",
        category = "MATH",
        nameSinhala = "වර්ගජ සමීකරණ සූත්‍රය (Quadratic Formula)",
        formula = "x = [-b ± √(b² - 4ac)] / 2a",
        explanation = "ax² + bx + c = 0 ආකාරයේ වර්ගජ සමීකරණ විසඳීමට භාවිතා කරයි.",
        unitsOrEra = "ගණිතය • වීජ ගණිතය"
      ),
      FormulaItem(
        id = "f_math_3",
        category = "MATH",
        nameSinhala = "ත්‍රිකෝණමිතිය (Trigonometry - Sin, Cos, Tan)",
        formula = "Sinθ = O/H,  Cosθ = A/H,  Tanθ = O/A",
        explanation = "O = සම්මුඛ පාදය, A = බද්ධ පාදය, H = කර්ණය (කෝණ හා පාද අතර සම්බන්ධය).",
        unitsOrEra = "ගණිතය • ත්‍රිකෝණමිතිය"
      ),
      FormulaItem(
        id = "f_math_4",
        category = "MATH",
        nameSinhala = "සමාන්තර ශ්‍රේඪියක n වන පදය හා එකතුව",
        formula = "Tn = a + (n - 1)d   සහ   Sn = n/2 [2a + (n - 1)d]",
        explanation = "a = මුල් පදය, d = පොදු අන්තරය, n = පද ගණන වේ.",
        unitsOrEra = "ගණිතය • ශ්‍රේඪි"
      ),
      FormulaItem(
        id = "f_math_5",
        category = "MATH",
        nameSinhala = "සිලින්ඩරයක පරිමාව හා වක්‍ර පෘෂ්ඨ වර්ගඵලය",
        formula = "V = πr²h   සහ   A = 2πrh",
        explanation = "r = අරය, h = උස වේ. සම්පූර්ණ පෘෂ්ඨ වර්ගඵලය = 2πrh + 2πr²",
        unitsOrEra = "ගණිතය • පරිමිතිය හා වර්ගඵලය"
      ),

      // Science Formulas & Laws
      FormulaItem(
        id = "f_sci_1",
        category = "SCIENCE",
        nameSinhala = "නිව්ටන්ගේ දෙවන නියමය (Newton's 2nd Law)",
        formula = "F = ma",
        explanation = "බලය = ස්කන්ධය × ත්වරණය. (අසමතුලිත බලය ගම්‍යතා වෙනස්වීමේ සීඝ්‍රතාවයට සමානුපාතික වේ).",
        unitsOrEra = "ඒකකය: නියුටන් (N) හෙවත් kg ms⁻²"
      ),
      FormulaItem(
        id = "f_sci_2",
        category = "SCIENCE",
        nameSinhala = "ඕම්ගේ නියමය (Ohm's Law)",
        formula = "V = IR",
        explanation = "විභව අන්තරය (V) = ධාරාව (I) × ප්‍රතිරෝධය (R). (නියත උෂ්ණත්වයේදී).",
        unitsOrEra = "ඒකක: Volt (V), Ampere (A), Ohm (Ω)"
      ),
      FormulaItem(
        id = "f_sci_3",
        category = "SCIENCE",
        nameSinhala = "චලිත සමීකරණ 4 (Equations of Motion)",
        formula = "v = u + at,  s = ut + ½at²,  v² = u² + 2as,  s = [(u + v)/2]t",
        explanation = "u = ආරම්භක ප්‍රවේගය, v = අවසාන ප්‍රවේගය, a = ඒකාකාර ත්වරණය, t = කාලය, s = විස්ථාපනය.",
        unitsOrEra = "විද්‍යාව • යාන්ත්‍ර විද්‍යාව"
      ),
      FormulaItem(
        id = "f_sci_4",
        category = "SCIENCE",
        nameSinhala = "තරංග සමීකරණය (Wave Equation)",
        formula = "v = fλ",
        explanation = "තරංග ප්‍රවේගය (v) = සංඛ්‍යාතය (f) × තරංග ආයාමය (λ).",
        unitsOrEra = "ඒකක: ms⁻¹, Hertz (Hz), Meter (m)"
      ),
      FormulaItem(
        id = "f_sci_5",
        category = "SCIENCE",
        nameSinhala = "කාර්යය, ශක්තිය හා ජවය (Work & Power)",
        formula = "W = F × s   සහ   P = W / t",
        explanation = "කාර්යය = බලය × විස්ථාපනය,  ජවය = කාර්යය / කාලය.",
        unitsOrEra = "ඒකක: Joule (J), Watt (W)"
      ),

      // History Timeline
      FormulaItem(
        id = "f_hist_1",
        category = "TIMELINE",
        nameSinhala = "අනුරාධපුර යුගය (ක්‍රි.පූ. 437 - ක්‍රි.ව. 1017)",
        formula = "පණ්ඩුකාභය රජු -> දේවානම්පියතිස්ස -> දුටුගැමුණු -> වළගම්බා -> මහසෙන්",
        explanation = "බුදුදහම ස්ථාපිත වීම, මහා සෑයවල්, මින්නේරිය වැව හා මහා වාරි ශිෂ්ටාචාරය ගොඩනැගීම.",
        unitsOrEra = "ඓතිහාසික යුගය: අනුරාධපුර රාජධානිය"
      ),
      FormulaItem(
        id = "f_hist_2",
        category = "TIMELINE",
        nameSinhala = "පොළොන්නරු යුගය (ක්‍රි.ව. 1055 - 1215)",
        formula = "1 වන විජයබාහු -> 1 වන පරාක්‍රමබාහු -> නිශ්ශංකමල්ල",
        explanation = "චෝල ආක්‍රමණ පරාජය කර රට එක්සේසත් කිරීම, පරාක්‍රම සමුද්‍රය, ගල් විහාරය නිර්මාණය.",
        unitsOrEra = "ඓතිහාසික යුගය: පොළොන්නරු රාජධානිය"
      ),
      FormulaItem(
        id = "f_hist_3",
        category = "TIMELINE",
        nameSinhala = "කෝට්ටේ යුගය සහ බටහිර ආක්‍රමණ (ක්‍රි.ව. 1412 - 1597)",
        formula = "6 වන පරාක්‍රමබාහු -> 1505 පෘතුගීසි ලංකාගමනය -> ධර්මපාල කුමරු",
        explanation = "මුළු ලංකාවම එක්සේසත් කළ අවසන් සිංහල රජු (6 වන පරාක්‍රමබාහු) හා සන්දේශ කාව්‍ය සාහිත්‍යය.",
        unitsOrEra = "ඓතිහාසික යුගය: කෝට්ටේ රාජධානිය"
      ),
      FormulaItem(
        id = "f_hist_4",
        category = "TIMELINE",
        nameSinhala = "මහනුවර යුගය සහ නිදහස් අරගල (ක්‍රි.ව. 1592 - 1815)",
        formula = "1 වන විමලධර්මසූරිය -> 2 වන රාජසිංහ -> ශ්‍රී වික්‍රම රාජසිංහ -> 1815 ගිවිසුම",
        explanation = "දළදා මාලිගය, දන්තුරේ සටන, රන්දෙනිවෙල සටන සහ 1815 මාර්තු 2 උඩරට ගිවිසුමෙන් බ්‍රිතාන්‍යයට යටත් වීම.",
        unitsOrEra = "ඓතිහාසික යුගය: මහනුවර රාජධානිය"
      ),

      // ICT & Commerce
      FormulaItem(
        id = "f_ict_1",
        category = "ICT",
        nameSinhala = "Logic Gates (තර්ක ද්වාර) - AND, OR, NOT, NAND, NOR",
        formula = "AND: A.B,  OR: A+B,  NOT: Ā,  NAND: (A.B)',  NOR: (A+B)'",
        explanation = "බූලීය වීජ ගණිතය මත පදනම්ව 0 සහ 1 පරිපථ ක්‍රියාකාරීත්වය තීරණය කරයි.",
        unitsOrEra = "ICT • පරිගණක දෘඩාංග"
      ),
      FormulaItem(
        id = "f_comm_1",
        category = "COMMERCE",
        nameSinhala = "ගිණුම්කරණ මූලික සමීකරණය (Accounting Equation)",
        formula = "වත්කම් (Assets) = හිමිකම (Equity) + වගකීම් (Liabilities)",
        explanation = "ද්විත්ව සටහන් මූලධර්මයේ පදනම වන අතර සෑම ගනුදෙනුවකින්ම සමීකරණය තුලිතව පවතී.",
        unitsOrEra = "ව්‍යාපාර හා ගිණුම්කරණ අධ්‍යයනය"
      )
    )
  }
}

@Composable
fun FormulaAndTimelineHubScreen(
  onBack: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf("ALL") }
  var searchQuery by remember { mutableStateOf("") }
  var formulaList by remember { mutableStateOf(FormulaRepository.getFormulas()) }

  val filtered = formulaList.filter { item ->
    (selectedCategory == "ALL" || item.category == selectedCategory || (selectedCategory == "SAVED" && item.isBookmarked)) &&
        (searchQuery.isEmpty() || item.nameSinhala.contains(searchQuery, ignoreCase = true) || item.formula.contains(searchQuery, ignoreCase = true) || item.explanation.contains(searchQuery, ignoreCase = true))
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Top Bar
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              onClick = onBack,
              shape = CircleShape,
              color = Color(0xFFF1F5F9)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1E293B),
                modifier = Modifier.padding(8.dp).size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "සූත්‍ර හා ඉතිහාස කාලරේඛාව (Handbook)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
              )
              Text(
                text = "ගණිතය • විද්‍යාව • ඉතිහාසය • ICT Quick Reference",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("සූත්‍රයක්, නියමයක් හෝ වර්ෂයක් සොයන්න...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B), modifier = Modifier.size(20.dp)) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF8FAFC),
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedBorderColor = BluePrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Tabs
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val categories = listOf(
            "ALL" to "සියල්ල",
            "MATH" to "📐 ගණිත සූත්‍ර",
            "SCIENCE" to "⚛️ විද්‍යා නියම & ඒකක",
            "TIMELINE" to "🏛️ ඉතිහාස කාලරේඛාව",
            "ICT" to "💻 ICT & Commerce",
            "SAVED" to "⭐ සුරකින ලද"
          )

          items(categories) { (code, label) ->
            val isSelected = selectedCategory == code
            Surface(
              onClick = { selectedCategory = code },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) BluePrimary else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (isSelected) BluePrimary else Color(0xFFCBD5E1))
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFF334155),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // Formulas and Timeline List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
    ) {
      if (filtered.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (selectedCategory == "SAVED") "තවමත් කිසිදු සූත්‍රයක් Save කර නොමැත. ⭐ තට්ටු කර Save කරගන්න." else "ගැළපෙන සූත්‍රයක් හමු නොවීය.",
              color = Color(0xFF64748B),
              fontSize = 12.sp,
              textAlign = TextAlign.Center
            )
          }
        }
      } else {
        items(filtered) { formula ->
          FormulaCard(
            item = formula,
            onToggleBookmark = {
              formulaList = formulaList.map {
                if (it.id == formula.id) it.copy(isBookmarked = !it.isBookmarked) else it
              }
            }
          )
        }
      }
    }
  }
}

@Composable
fun FormulaCard(
  item: FormulaItem,
  onToggleBookmark: () -> Unit
) {
  val cardBg = when (item.category) {
    "MATH" -> Color(0xFFEFF6FF)
    "SCIENCE" -> Color(0xFFF0FDF4)
    "TIMELINE" -> Color(0xFFFAF5FF)
    else -> Color(0xFFFFFBEB)
  }

  val accentColor = when (item.category) {
    "MATH" -> Color(0xFF1D4ED8)
    "SCIENCE" -> Color(0xFF15803D)
    "TIMELINE" -> Color(0xFF7E22CE)
    else -> Color(0xFFB45309)
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = cardBg
        ) {
          Text(
            text = item.unitsOrEra,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        IconButton(
          onClick = onToggleBookmark,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Bookmark",
            tint = if (item.isBookmarked) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = item.nameSinhala,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F172A)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Highlighted Formula Box
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = cardBg,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = item.formula,
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = accentColor,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = item.explanation,
        fontSize = 11.sp,
        color = Color(0xFF475569),
        lineHeight = 15.sp
      )
    }
  }
}
