package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// ==============================================================================
// 1. LIVE MOCK EXAM HALL SIMULATION WITH OMR BUBBLE SHEET (සැබෑ විභාග ශාලා අනුකරණය)
// ==============================================================================

data class MockExamQuestion(
  val id: Int,
  val questionText: String,
  val options: List<String>,
  val correctOption: Int, // 1, 2, 3, 4
  val explanation: String,
  val topicName: String
)

data class MockExamPaper(
  val id: String,
  val titleSinhala: String,
  val subject: String,
  val grade: String,
  val durationMinutes: Int,
  val totalQuestions: Int,
  val questions: List<MockExamQuestion>,
  val pdfUri: String? = null
)

object MockExamRepository {
  fun getAvailableMockExams(grade: String): List<MockExamPaper> {
    val scienceExam = MockExamPaper(
      id = "mock_sci_${grade}",
      titleSinhala = "$grade ශ්‍රේණිය - විද්‍යාව I පත්‍රය (MCQ පෙරහුරු විභාගය)",
      subject = "විද්‍යාව",
      grade = grade,
      durationMinutes = 20,
      totalQuestions = 10,
      pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview",
      questions = listOf(
        MockExamQuestion(
          id = 1,
          questionText = "ප්‍රභාසංශ්ලේෂණයේදී ආලෝක ප්‍රතික්‍රියාව සිදුවන්නේ හරිතලවයේ කුමන කොටසේද?",
          options = listOf("ස්ට්‍රෝමාව (Stroma)", "තයිලකොයිඩ පටලය (Thylakoid)", "පිටත පටලය", "රයිබොසෝම"),
          correctOption = 2,
          explanation = "ආලෝක ප්‍රතික්‍රියාව තයිලකොයිඩ පටලයේ ග්‍රානා තුළ සිදුවන අතර අඳුරු ප්‍රතික්‍රියාව ස්ට්‍රෝමාවේ සිදුවේ.",
          topicName = "ජීවයේ රසායනික පදනම"
        ),
        MockExamQuestion(
          id = 2,
          questionText = "සම්මත උෂ්ණත්වයේ හා පීඩනයේදී (STP) ඕනෑම වායුවක මවුල 1 ක පරිමාව කොපමණද?",
          options = listOf("22.4 dm³", "24.0 dm³", "11.2 dm³", "22400 cm³"),
          correctOption = 1,
          explanation = "STP හිදී මවුලික පරිමාව 22.4 dm³ (හෝ 22400 cm³) වන අතර කාමර උෂ්ණත්වයේදී (RTP) 24 dm³ වේ.",
          topicName = "මවුලය හා පරිමා"
        ),
        MockExamQuestion(
          id = 3,
          questionText = "නිව්ටන්ගේ දෙවන චලිත නියමයට අනුකූල නිවැරදි සමීකරණය කුමක්ද?",
          options = listOf("F = m / a", "F = m × a", "F = v / t", "F = m × v"),
          correctOption = 2,
          explanation = "F = ma (බලය = ස්කන්ධය × ත්වරණය) යනු නිව්ටන්ගේ දෙවන චලිත නියමයයි.",
          topicName = "චලිතය හා බලය"
        ),
        MockExamQuestion(
          id = 4,
          questionText = "පහත දැක්වෙන ද්‍රව්‍ය අතුරින් උභයගුණී ඔක්සයිඩයක් වන්නේ කුමක්ද?",
          options = listOf("Na₂O", "SO₂", "Al₂O₃", "CaO"),
          correctOption = 3,
          explanation = "ඇලුමිනියම් ඔක්සයිඩ් (Al₂O₃) සහ සින්ක් ඔක්සයිඩ් (ZnO) අම්ල හා භෂ්ම දෙක සමගම ප්‍රතික්‍රියා කරන උභයගුණී ඔක්සයිඩ වේ.",
          topicName = "මූලද්‍රව්‍ය ආවර්තිතාව"
        ),
        MockExamQuestion(
          id = 5,
          questionText = "මානව හෘදයේ ඔක්සිජනීකෘත රුධිරය මුළු ශරීරය පුරා පොම්ප කරන්නේ කුමන කුටීරයෙන්ද?",
          options = listOf("දකුණු කර්ණිකාව", "දකුණු කෝෂිකාව", "වම් කර්ණිකාව", "වම් කෝෂිකාව"),
          correctOption = 4,
          explanation = "වම් කෝෂිකාවේ ඝනකම් බිත්තිය මගින් මහා ධමනිය හරහා මුළු ශරීරය පුරාම ඔක්සිජන් සහිත රුධිරය පොම්ප කරයි.",
          topicName = "පරිවහනය හා සංසරණය"
        ),
        MockExamQuestion(
          id = 6,
          questionText = "ප්‍රතිරෝධක 3ක් (6Ω, 3Ω) සමාන්තරගතව සම්බන්ධ කළ විට සමක ප්‍රතිරෝධය කොපමණද?",
          options = listOf("9 Ω", "2 Ω", "18 Ω", "0.5 Ω"),
          correctOption = 2,
          explanation = "1/R = 1/6 + 1/3 = 3/6 = 1/2 => R = 2 Ω.",
          topicName = "ධාරා විද්‍යුතය"
        ),
        MockExamQuestion(
          id = 7,
          questionText = "ශාකවල අග්‍රස්ථ ප්‍රමුඛතාවය ඇති කිරීමට ප්‍රධාන වශයෙන් දායක වන හෝමෝනය කුමක්ද?",
          options = listOf("ඔක්සින් (Auxin)", "ජිබරලින්", "සයිටොකයිනින්", "එතිලීන්"),
          correctOption = 1,
          explanation = "අග්‍රස්ථ අංකුරයේ නිපදවෙන ඔක්සින් (Auxin) පාර්ශ්වික අංකුර වර්ධනය නිශේධනය කරමින් අග්‍රස්ථ ප්‍රමුඛතාව ඇති කරයි.",
          topicName = "ශාක හෝමෝන"
        ),
        MockExamQuestion(
          id = 8,
          questionText = "විකිරණශීලී ක්ෂයවීමකදී විද්‍යුත් හා චුම්භක ක්ෂේත්‍ර මගින් කිසිදු අපගමනයක් නොපෙන්වන විකිරණය කුමක්ද?",
          options = listOf("ඇල්ෆා කිරණ", "බීටා කිරණ", "ගැමා කිරණ", "ප්‍රෝටෝන"),
          correctOption = 3,
          explanation = "ගැමා (γ) කිරණ යනු ආරෝපණයක් සහ ස්කන්ධයක් නොමැති අධි ශක්ති විද්‍යුත් චුම්භක තරංග බැවින් ක්ෂේත්‍ර මගින් අපගමනය නොවේ.",
          topicName = "න්‍යෂ්ටික විකිරණ"
        ),
        MockExamQuestion(
          id = 9,
          questionText = "හයිඩ්‍රොක්ලෝරික් අම්ලය (HCl) සහ සෝඩියම් හයිඩ්‍රොක්සයිඩ් (NaOH) අතර උදාසීනීකරණ ප්‍රතික්‍රියාවේ එන්තැල්පි විපර්යාසයේ ස්වභාවය කුමක්ද?",
          options = listOf("තාප අවශෝෂක (ΔH ධන)", "තාප දායක (ΔH ඍණ)", "ශක්ති විපර්යාසයක් නැත", "ප්‍රතිවර්ත්‍ය වේ"),
          correctOption = 2,
          explanation = "ප්‍රබල අම්ල සහ ප්‍රබල භෂ්ම අතර උදාසීනීකරණය සැමවිටම තාප දායක (Exothermic - ΔH ඍණ) වේ.",
          topicName = "රසායනික ශක්ති විද්‍යාව"
        ),
        MockExamQuestion(
          id = 10,
          questionText = "පරාවර්තන දුරේක්ෂයක (Reflecting Telescope) වස්තු කාචය වෙනුවට භාවිත කරන්නේ කුමක්ද?",
          options = listOf("අවතල දර්පණයක්", "උත්තල දර්පණයක්", "අවතල කාචයක්", "ප්‍රිස්මයක්"),
          correctOption = 1,
          explanation = "නිව්ටෝනියානු පරාවර්තන දුරේක්ෂවල ආලෝකය නාභිගත කිරීමට විශාල අවතල දර්පණයක් (Concave Mirror) යොදාගනී.",
          topicName = "ආලෝකය හා දෘෂ්ටි උපකරණ"
        )
      )
    )

    val mathExam = MockExamPaper(
      id = "mock_math_${grade}",
      titleSinhala = "$grade ශ්‍රේණිය - ගණිතය I පත්‍රය (OMR සැබෑ පෙරහුරුව)",
      subject = "ගණිතය",
      grade = grade,
      durationMinutes = 20,
      totalQuestions = 8,
      pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview",
      questions = listOf(
        MockExamQuestion(
          id = 1,
          questionText = "2x² - 8 = 0 සමීකරණයේ විසඳුම් කුමක්ද?",
          options = listOf("x = 4 පමණි", "x = ±2", "x = 2 පමණි", "x = ±4"),
          correctOption = 2,
          explanation = "2x² = 8 => x² = 4 => x = +2 හෝ x = -2 (x = ±2).",
          topicName = "වර්ගජ සමීකරණ"
        ),
        MockExamQuestion(
          id = 2,
          questionText = "වෘත්තයක කේන්ද්‍රයේ සිට ජ්‍යායකට අඳින ලද ලම්භය මගින් ජ්‍යාය,",
          options = listOf("සමච්ඡේද කරයි", "තුන් සමාන කරයි", "වර්ග කරයි", "වෙනසක් නොකරයි"),
          correctOption = 1,
          explanation = "ජ්‍යාමිතික ප්‍රමේයය: වෘත්තයක කේන්ද්‍රයේ සිට ජ්‍යායකට අඳින ලම්භය මගින් එම ජ්‍යාය සමච්ඡේද වේ.",
          topicName = "වෘත්ත ජ්‍යාමිතිය"
        ),
        MockExamQuestion(
          id = 3,
          questionText = "3, 7, 11, 15, ... සමාන්තර ශ්‍රේඪියේ 10 වන පදය කුමක්ද?",
          options = listOf("36", "39", "43", "40"),
          correctOption = 2,
          explanation = "Tn = a + (n-1)d => T10 = 3 + (10-1)4 = 3 + 36 = 39.",
          topicName = "සමාන්තර ශ්‍රේඪි"
        ),
        MockExamQuestion(
          id = 4,
          questionText = "ලඝුගණක ප්‍රකාශනය log₂ 32 හි අගය කුමක්ද?",
          options = listOf("4", "5", "16", "2"),
          correctOption = 2,
          explanation = "2⁵ = 32 බැවින් log₂ 32 = 5 වේ.",
          topicName = "ලඝුගණක"
        ),
        MockExamQuestion(
          id = 5,
          questionText = "ත්‍රිකෝණයක කෝණ 2x, 3x, 4x නම්, විශාලතම කෝණයේ අගය කුමක්ද?",
          options = listOf("40°", "60°", "80°", "100°"),
          correctOption = 3,
          explanation = "2x + 3x + 4x = 180° => 9x = 180° => x = 20°. විශාලතම කෝණය = 4 × 20° = 80°.",
          topicName = "කෝණ හා ත්‍රිකෝණ"
        ),
        MockExamQuestion(
          id = 6,
          questionText = "සාධාරණ දාදු කැටයක් එක් වරක් දැමූ විට 3 ට වඩා වැඩි ඔත්තේ සංඛ්‍යාවක් ලැබීමේ සම්භාවිතාව,",
          options = listOf("1/6", "1/3", "1/2", "2/3"),
          correctOption = 1,
          explanation = "දාදු කැටයේ සාම්පල අවකාශය = {1, 2, 3, 4, 5, 6}. 3ට වැඩි ඔත්තේ සංඛ්‍යාව වන්නේ {5} පමණි. එබැවින් සම්භාවිතාව = 1/6.",
          topicName = "සම්භාවිතාව"
        ),
        MockExamQuestion(
          id = 7,
          questionText = "අරය 7 cm වූ අර්ධ ගෝලයක වක්‍ර පෘෂ්ඨ වර්ගඵලය කුමක්ද? (π = 22/7)",
          options = listOf("308 cm²", "154 cm²", "616 cm²", "462 cm²"),
          correctOption = 1,
          explanation = "අර්ධ ගෝලයේ වක්‍ර පෘෂ්ඨ වර්ගඵලය = 2πr² = 2 × (22/7) × 7 × 7 = 308 cm².",
          topicName = "ඝන වස්තු වර්ගඵලය"
        ),
        MockExamQuestion(
          id = 8,
          questionText = "sin 30° හි අගය කුමක්ද?",
          options = listOf("1", "1/2", "√3/2", "1/√2"),
          correctOption = 2,
          explanation = "සම්මත ත්‍රිකෝණමිතික අනුපාත අනුව sin 30° = 1/2 සහ cos 60° = 1/2 වේ.",
          topicName = "ත්‍රිකෝණමිතිය"
        )
      )
    )

    val historyExam = MockExamPaper(
      id = "mock_hist_${grade}",
      titleSinhala = "$grade ශ්‍රේණිය - ඉතිහාසය OMR පූර්ණ ආදර්ශ විභාගය",
      subject = "ඉතිහාසය",
      grade = grade,
      durationMinutes = 15,
      totalQuestions = 6,
      pdfUri = "https://drive.google.com/file/d/1Ry6utaFim_tZl8OkTG5hoD6oB4RB8Uxl/preview",
      questions = listOf(
        MockExamQuestion(
          id = 1,
          questionText = "ශ්‍රී ලංකාවේ ප්‍රථම ඓතිහාසික රජු ලෙස සලකනු ලබන්නේ කවුරුන්ද?",
          options = listOf("විජය රජු", "පණ්ඩුකාභය රජු", "දේවානම්පියතිස්ස රජු", "දුටුගැමුණු රජු"),
          correctOption = 2,
          explanation = "අනුරාධපුර නගරය නිසි සැලැස්මකට අනුව මුලින්ම ඉදිකළ පණ්ඩුකාභය රජු ප්‍රථම ඓතිහාසික රජු ලෙස පිළිගැනේ.",
          topicName = "අනුරාධපුර රාජධානිය"
        ),
        MockExamQuestion(
          id = 2,
          questionText = "ලංකාවට බුදුදහම හඳුන්වා දෙනු ලැබුවේ කුමන රජුගේ පාලන සමයේදීද?",
          options = listOf("පණ්ඩුකාභය රජු", "දේවානම්පියතිස්ස රජු", "මහසෙන් රජු", "ධාතුසේන රජු"),
          correctOption = 2,
          explanation = "ක්‍රි.පූ. 3 වන සියවසේදී අශෝක අධිරාජ්‍යයාගේ පුත් මිහිඳු මහ රහතන් වහන්සේ විසින් දේවානම්පියතිස්ස රජ සමයේදී බුදුදහම රැගෙන එන ලදී.",
          topicName = "ශාසන ඉතිහාසය"
        ),
        MockExamQuestion(
          id = 3,
          questionText = "යෝධ ඇළ (ජය ගඟ) ඉදිකරන ලද්දේ කුමන රජතුමා විසිනි?",
          options = listOf("මහසෙන් රජු", "පරාක්‍රමබාහු රජු", "ධාතුසේන රජු", "වසභ රජු"),
          correctOption = 3,
          explanation = "කලා වැවේ සිට තිසා වැව දක්වා සැතපුම් 54ක් දිග යෝධ ඇළ ඉදිකළේ ධාතුසේන රජතුමා විසිනි.",
          topicName = "වාරි ශිෂ්ටාචාරය"
        ),
        MockExamQuestion(
          id = 4,
          questionText = "1815 උඩරට ගිවිසුමට බ්‍රිතාන්‍ය ආණ්ඩුකාරවරයා ලෙස අත්සන් තැබුවේ කවුරුන්ද?",
          options = listOf("ෆෙඩ්රික් නෝර්ත්", "රොබට් බ්‍රවුන්රිග්", "තෝමස් මේට්ලන්ඩ්", "විලියම් ග්‍රෙගරි"),
          correctOption = 2,
          explanation = "1815 මාර්තු 02 වන දින මහනුවර මඟුල් මඩුවේදී රොබට් බ්‍රවුන්රිග් ආණ්ඩුකාරවරයා සහ උඩරට ප්‍රධානීන් අතර ගිවිසුම අත්සන් කෙරිණි.",
          topicName = "යටත්විජිත යුගය"
        ),
        MockExamQuestion(
          id = 5,
          questionText = "1948 නිදහස් ශ්‍රී ලංකාවේ ප්‍රථම අග්‍රාමාත්‍යවරයා කවුරුන්ද?",
          options = listOf("එස්.ඩබ්.ආර්.ඩී. බණ්ඩාරනායක", "ඩී.එස්. සේනානායක", "ඩඩ්ලි සේනානායක", "සර් ජෝන් කොතලාවල"),
          correctOption = 2,
          explanation = "ජාතියේ පියා ලෙස සැලකෙන ඩී.එස්. සේනානායක මහතා 1947/1948 ප්‍රථම අග්‍රාමාත්‍යවරයා විය.",
          topicName = "නිදහස් ශ්‍රී ලංකාව"
        ),
        MockExamQuestion(
          id = 6,
          questionText = "ලොව ප්‍රථම අග්‍රාමාත්‍යවරිය ලෙස 1960 දී පත්වූයේ කවුරුන්ද?",
          options = listOf("ඉන්දිරා ගාන්ධි", "සිරිමාවෝ බණ්ඩාරනායක", "මාග්‍රට් තැචර්", "ගෝල්ඩා මේයර්"),
          correctOption = 2,
          explanation = "ගරු සිරිමාවෝ බණ්ඩාරනායක මැතිණිය 1960 ජූලි මාසයේදී ලොව ප්‍රථම අග්‍රාමාත්‍යවරිය ලෙස ඉතිහාසයට එක්විය.",
          topicName = "ශ්‍රී ලංකා දේශපාලන ඉතිහාසය"
        )
      )
    )

    return listOf(scienceExam, mathExam, historyExam)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMockExamHallScreen(
  grade: String,
  onBack: () -> Unit,
  onOpenPdfModal: (url: String, title: String) -> Unit = { _, _ -> }
) {
  val mockExams = remember(grade) { MockExamRepository.getAvailableMockExams(grade) }
  var selectedExam by remember { mutableStateOf<MockExamPaper?>(null) }

  if (selectedExam == null) {
    // EXAM LOBBY SCREEN
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Column {
              Text("⏱️ සැබෑ විභාග ශාලා අනුකරණය", fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Live Mock Exam Hall • OMR Bubble Sheet", fontSize = 11.sp, color = NeutralMedium)
            }
          },
          navigationIcon = {
            IconButton(onClick = onBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
      }
    ) { padding ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(Color(0xFFF8FAFC))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Hero Card
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFEF4444)
                ) {
                  Text(
                    text = "LIVE EXAM SIMULATOR",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("🔥 සැබෑ විභාග අත්දැකීම", color = Color(0xFFFDE047), fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "නියමිත කාල සීමාව තුළ OMR පත්‍රයක තිත් තබමින් පෙරහුරු විභාගයට මුහුණ දෙන්න!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 22.sp
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "✓ Live Countdown Timer • ✓ OMR Digital Answer Sheet • ✓ ක්ෂණික ලකුණු & Marking Scheme විවරණය",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }
        }

        item {
          Text(
            text = "පෙරහුරු විභාග පත්‍රිකාව තෝරන්න (Select Mock Exam):",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NeutralDark
          )
        }

        items(mockExams) { exam ->
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
                  shape = RoundedCornerShape(8.dp),
                  color = when (exam.subject) {
                    "විද්‍යාව" -> Color(0xFFE8F5E9)
                    "ගණිතය" -> Color(0xFFE3F2FD)
                    else -> Color(0xFFF3E8FF)
                  }
                ) {
                  Text(
                    text = exam.subject,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (exam.subject) {
                      "විද්‍යාව" -> Color(0xFF1B5E20)
                      "ගණිතය" -> Color(0xFF0D47A1)
                      else -> Color(0xFF6B21A8)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Timer, contentDescription = "Time", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("${exam.durationMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = exam.titleSinhala,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NeutralDark
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "ප්‍රශ්න ගණන: ${exam.totalQuestions} • OMR පිළිතුරු පත්‍රය සහිතයි",
                fontSize = 12.sp,
                color = NeutralMedium
              )

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                if (exam.pdfUri != null) {
                  OutlinedButton(
                    onClick = { onOpenPdfModal(exam.pdfUri, exam.titleSinhala) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ප්‍රශ්න පත්‍රය (PDF)", fontSize = 11.sp)
                  }
                }

                Button(
                  onClick = { selectedExam = exam },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.weight(1.3f).testTag("start_mock_exam_${exam.id}")
                ) {
                  Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("විභාගය ආරම්භ කරන්න", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  } else {
    // ACTIVE EXAM SESSION / GRADING VIEW
    ActiveMockExamSession(
      exam = selectedExam!!,
      onExit = { selectedExam = null }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMockExamSession(
  exam: MockExamPaper,
  onExit: () -> Unit
) {
  var remainingSeconds by remember { mutableStateOf(exam.durationMinutes * 60) }
  var isTimerRunning by remember { mutableStateOf(true) }
  var currentQuestionIndex by remember { mutableStateOf(0) }
  val answersMap = remember { mutableStateMapOf<Int, Int>() } // Question ID -> Selected Bubble (1..4)
  val flaggedQuestions = remember { mutableStateListOf<Int>() }
  var isExamSubmitted by remember { mutableStateOf(false) }
  var showConfirmSubmitDialog by remember { mutableStateOf(false) }
  var activeTab by remember { mutableStateOf(0) } // 0: ප්‍රශ්න විචාරය (Question View), 1: OMR Sheet Matrix

  // Timer Effect
  LaunchedEffect(isTimerRunning, isExamSubmitted) {
    while (isTimerRunning && !isExamSubmitted && remainingSeconds > 0) {
      delay(1000L)
      remainingSeconds--
    }
    if (remainingSeconds == 0 && !isExamSubmitted) {
      isExamSubmitted = true
    }
  }

  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val timeString = String.format("%02d:%02d", minutes, seconds)

  if (isExamSubmitted) {
    // ----------------------------------------------------
    // EXAM RESULTS & REVIEW SCREEN
    // ----------------------------------------------------
    val correctCount = exam.questions.count { answersMap[it.id] == it.correctOption }
    val totalCount = exam.questions.size
    val scorePercentage = ((correctCount.toFloat() / totalCount) * 100).toInt()
    val gradeStatus = when {
      scorePercentage >= 75 -> "A (විශිෂ්ට සාමාර්ථ්‍යයක්!)"
      scorePercentage >= 65 -> "B (ඉතා හොඳ සාමාර්ථ්‍යයක්)"
      scorePercentage >= 50 -> "C (සම්මාන සාමාර්ථ්‍යයක්)"
      scorePercentage >= 35 -> "S (සාමාන්‍ය සාමාර්ථ්‍යයක්)"
      else -> "W (නැවත උත්සාහ කරන්න)"
    }

    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("📊 විභාග ප්‍රතිඵල සහ විවරණය", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
          navigationIcon = {
            IconButton(onClick = onExit) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
      }
    ) { padding ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(Color(0xFFF8FAFC))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (scorePercentage >= 50) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
            ),
            border = BorderStroke(1.5.dp, if (scorePercentage >= 50) Color(0xFF86EFAC) else Color(0xFFFECACA)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (scorePercentage >= 75) "🎉 සුබ පැතුම්! විශිෂ්ට ජයග්‍රහණයක්!" else if (scorePercentage >= 50) "👏 සාර්ථකයි! දිගටම පුහුණු වන්න" else "💪 ධෛර්යයෙන් නැවත උත්සාහ කරන්න!",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (scorePercentage >= 50) Color(0xFF15803D) else Color(0xFFB91C1C)
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "$scorePercentage%",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = if (scorePercentage >= 50) Color(0xFF15803D) else Color(0xFFB91C1C)
              )

              Text(
                text = "සාමාර්ථය: $gradeStatus",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeutralDark
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("නිවැරදි පිළිතුරු", fontSize = 11.sp, color = NeutralMedium)
                  Text("✅ $correctCount / $totalCount", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF15803D))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("වැරදුණු පිළිතුරු", fontSize = 11.sp, color = NeutralMedium)
                  Text("❌ ${totalCount - correctCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFB91C1C))
                }
              }
            }
          }
        }

        item {
          Text(
            text = "ප්‍රශ්න විවරණය සහ නිවැරදි පිළිතුරු (Marking Scheme Review):",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NeutralDark
          )
        }

        itemsIndexed(exam.questions) { idx, q ->
          val userChoice = answersMap[q.id]
          val isCorrect = userChoice == q.correctOption

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, if (isCorrect) Color(0xFFBBF7D0) else Color(0xFFFECDD3)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ප්‍රශ්නය ${idx + 1} (${q.topicName})",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = if (isCorrect) Color(0xFF15803D) else Color(0xFFB91C1C)
                )

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (isCorrect) Color(0xFFDCFCE7) else Color(0xFFFFE4E6)
                ) {
                  Text(
                    text = if (isCorrect) "නිවැරදියි (+1)" else "වැරදියි (0)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) Color(0xFF15803D) else Color(0xFFBE123C),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = q.questionText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeutralDark
              )

              Spacer(modifier = Modifier.height(8.dp))

              q.options.forEachIndexed { optIdx, optText ->
                val bubbleNum = optIdx + 1
                val isSelectedByUser = userChoice == bubbleNum
                val isThisCorrectAnswer = q.correctOption == bubbleNum

                val bubbleBg = when {
                  isThisCorrectAnswer -> Color(0xFFDCFCE7)
                  isSelectedByUser -> Color(0xFFFFE4E6)
                  else -> Color(0xFFF8FAFC)
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = bubbleBg,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "($bubbleNum) $optText",
                      fontSize = 12.sp,
                      fontWeight = if (isThisCorrectAnswer || isSelectedByUser) FontWeight.Bold else FontWeight.Normal,
                      color = if (isThisCorrectAnswer) Color(0xFF15803D) else if (isSelectedByUser) Color(0xFFBE123C) else NeutralDark
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isThisCorrectAnswer) {
                      Text("✓ නිවැරදි පිළිතුර", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                    } else if (isSelectedByUser) {
                      Text("ඔබගේ තේරීම", fontSize = 10.sp, color = Color(0xFFBE123C))
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "💡 විවරණය: ${q.explanation}",
                  fontSize = 11.sp,
                  color = Color(0xFF334155),
                  modifier = Modifier.padding(8.dp)
                )
              }
            }
          }
        }

        item {
          Button(
            onClick = onExit,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Text("පෙරහුරු විභාග මෙනුවට ආපසු යන්න", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  } else {
    // ----------------------------------------------------
    // LIVE EXAM RUNNING SCREEN (OMR + QUESTION NAVIGATOR)
    // ----------------------------------------------------
    Scaffold(
      topBar = {
        Surface(
          color = Color(0xFF0F172A),
          shadowElevation = 4.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .statusBarsPadding()
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = exam.titleSinhala,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "පිළිතුරු සැපයූ ප්‍රශ්න: ${answersMap.size} / ${exam.questions.size}",
                  fontSize = 11.sp,
                  color = Color(0xFF94A3B8)
                )
              }

              // Live Countdown Pill
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (remainingSeconds < 180) Color(0xFFDC2626) else Color(0xFF1E293B),
                border = BorderStroke(1.dp, if (remainingSeconds < 180) Color(0xFFEF4444) else Color(0xFF334155))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.HourglassBottom, contentDescription = "Timer", tint = Color.White, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = timeString,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-Tabs: Single Question View vs Full OMR Sheet Matrix
            TabRow(
              selectedTabIndex = activeTab,
              containerColor = Color(0xFF1E293B),
              contentColor = Color.White,
              indicator = {}
            ) {
              Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("📝 ප්‍රශ්නය සහ විකල්ප", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
              )
              Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("⭕ OMR පිළිතුරු පත්‍රය (${answersMap.size}/${exam.questions.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
              )
            }
          }
        }
      },
      bottomBar = {
        Surface(
          color = Color.White,
          shadowElevation = 8.dp,
          modifier = Modifier.navigationBarsPadding()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                if (currentQuestionIndex > 0) currentQuestionIndex--
              },
              enabled = currentQuestionIndex > 0,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("පෙර ප්‍රශ්නය", fontSize = 11.sp)
            }

            Button(
              onClick = { showConfirmSubmitDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("submit_exam_button")
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = "Submit", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("විභාගය අවසන් කරන්න", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
              onClick = {
                if (currentQuestionIndex < exam.questions.size - 1) currentQuestionIndex++
              },
              enabled = currentQuestionIndex < exam.questions.size - 1,
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("ඊළඟ ප්‍රශ්නය", fontSize = 11.sp)
            }
          }
        }
      }
    ) { padding ->
      if (activeTab == 0) {
        // SINGLE QUESTION + OMR BUBBLE BAR
        val q = exam.questions[currentQuestionIndex]
        val selectedBubble = answersMap[q.id]
        val isFlagged = flaggedQuestions.contains(q.id)

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Question Header & Flag Toggle
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE2E8F0)
              ) {
                Text(
                  text = "ප්‍රශ්නය ${currentQuestionIndex + 1} / ${exam.questions.size}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = NeutralDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }

              Surface(
                onClick = {
                  if (isFlagged) flaggedQuestions.remove(q.id) else flaggedQuestions.add(q.id)
                },
                shape = RoundedCornerShape(8.dp),
                color = if (isFlagged) Color(0xFFFEF3C7) else Color.White,
                border = BorderStroke(1.dp, if (isFlagged) Color(0xFFF59E0B) else Color(0xFFCBD5E1))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = if (isFlagged) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Review",
                    tint = if (isFlagged) Color(0xFFD97706) else NeutralMedium,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isFlagged) "නැවත බැලීමට (Marked)" else "නැවත බැලීමට ලකුණු කරන්න",
                    fontSize = 10.sp,
                    color = if (isFlagged) Color(0xFFD97706) else NeutralMedium
                  )
                }
              }
            }
          }

          // Question Card
          item {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = q.questionText,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = NeutralDark,
                  lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                q.options.forEachIndexed { optIdx, optText ->
                  val bubbleNum = optIdx + 1
                  val isSelected = selectedBubble == bubbleNum

                  Surface(
                    onClick = {
                      answersMap[q.id] = bubbleNum
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                    border = BorderStroke(1.5.dp, if (isSelected) BluePrimary else Color(0xFFE2E8F0)),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      // OMR Circle Bubble
                      Box(
                        modifier = Modifier
                          .size(28.dp)
                          .clip(CircleShape)
                          .background(if (isSelected) Color(0xFF1E293B) else Color.White)
                          .border(2.dp, if (isSelected) Color(0xFF1E293B) else Color(0xFF94A3B8), CircleShape),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = "$bubbleNum",
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp,
                          color = if (isSelected) Color.White else Color(0xFF64748B)
                        )
                      }

                      Spacer(modifier = Modifier.width(12.dp))

                      Text(
                        text = optText,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BluePrimary else NeutralDark
                      )
                    }
                  }
                }
              }
            }
          }

          // Fast OMR Bubble Strip at the bottom
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("OMR තිත් තැබීම:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  (1..4).forEach { bNum ->
                    val isSelected = selectedBubble == bNum
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF0F172A) else Color.White)
                        .border(1.5.dp, if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8), CircleShape)
                        .clickable { answersMap[q.id] = bNum },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "($bNum)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else NeutralDark
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // FULL OMR SHEET MATRIX (All 1..20/40 questions bubble sheet)
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF8FAFC))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
              border = BorderStroke(1.dp, Color(0xFFFDE68A)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "සැබෑ OMR පත්‍රයක මෙන් එක් එක් ප්‍රශ්නයට අදාළ අංකය මත ක්ලික් කර පිළිතුරු සලකුණු කරන්න.",
                  fontSize = 11.sp,
                  color = Color(0xFF92400E)
                )
              }
            }
          }

          itemsIndexed(exam.questions) { idx, q ->
            val selected = answersMap[q.id]
            val isFlagged = flaggedQuestions.contains(q.id)

            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (selected != null) Color(0xFFF0FDF4) else Color.White
              ),
              border = BorderStroke(1.dp, if (selected != null) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                  currentQuestionIndex = idx
                  activeTab = 0
                }) {
                  Text(
                    text = "Q${idx + 1}.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (selected != null) Color(0xFF15803D) else NeutralDark
                  )
                  if (isFlagged) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🚩", fontSize = 10.sp)
                  }
                }

                // 4 OMR Bubbles
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  (1..4).forEach { bNum ->
                    val isChosen = selected == bNum
                    Box(
                      modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isChosen) Color(0xFF0F172A) else Color.White)
                        .border(1.5.dp, if (isChosen) Color(0xFF0F172A) else Color(0xFFCBD5E1), CircleShape)
                        .clickable { answersMap[q.id] = bNum },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "$bNum",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isChosen) Color.White else NeutralDark
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

  // Confirm Submit Dialog
  if (showConfirmSubmitDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmSubmitDialog = false },
      title = { Text("විභාගය අවසන් කරන්නද?", fontWeight = FontWeight.Bold) },
      text = {
        Text("ඔබ ප්‍රශ්න ${exam.questions.size} න් ${answersMap.size} කට පිළිතුරු සපයා ඇත. විභාගය අවසන් කර ප්‍රතිඵල සහ ලකුණු විවරණය බැලීමට අවශ්‍යද?")
      },
      confirmButton = {
        Button(
          onClick = {
            showConfirmSubmitDialog = false
            isExamSubmitted = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
        ) {
          Text("ඔව්, අවසන් කරන්න")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmSubmitDialog = false }) {
          Text("තවදුරටත් ලියන්න")
        }
      }
    )
  }
}

// ==============================================================================
// 2. EXAM SPOT TOPICS & TREND PREDICTOR (විභාග ඉලක්කගත අනුමාන මාතෘකා)
// ==============================================================================

data class SpotTopicItem(
  val id: String,
  val topicSinhala: String,
  val probabilityPercent: Int, // e.g. 95%
  val priorityBadge: String, // 🔥 ඉහළම සම්භාවිතාව, 🎯 නිශ්චිත රචනා, ⭐️ සූත්‍ර හා ගණනය
  val questionFormat: String, // e.g. "I පත්‍රයේ MCQ 3ක් සහ II පත්‍රයේ ව්‍යුහගත රචනා B කොටසේ 1 ප්‍රශ්නයක්"
  val keyConcepts: List<String>,
  val pastExamFrequency: String, // e.g. "2019, 2021, 2023, 2024 වසරවල අඛණ්ඩව ප්‍රශ්න පැමිණ ඇත."
  val pdfUri: String? = null
)

object SpotTopicsRepository {
  fun getSpotTopics(subject: String, grade: String): List<SpotTopicItem> {
    return when (subject) {
      "විද්‍යාව" -> listOf(
        SpotTopicItem(
          id = "spot_sci_1",
          topicSinhala = "ප්‍රභාසංශ්ලේෂණය සහ ජීවයේ රසායනික පදනම",
          probabilityPercent = 95,
          priorityBadge = "🔥 95% ඉහළම සම්භාවිතාව",
          questionFormat = "Part I හි MCQ 2-3ක් සහ Part II හි ව්‍යුහගත රචනා ප්‍රශ්න අංක 1",
          keyConcepts = listOf("ආලෝක හා අඳුරු ප්‍රතික්‍රියා සංසන්දනය", "ප්‍රභාසංශ්ලේෂණ අනුපාතය කෙරෙහි බලපාන සාධක පරීක්ෂණ", "ATP සහ NADPH භාවිතය"),
          pastExamFrequency = "පසුගිය වසර 7 තුළම ප්‍රශ්න පත්‍රයේ නිශ්චිතව අන්තර්ගත විය.",
          pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
        ),
        SpotTopicItem(
          id = "spot_sci_2",
          topicSinhala = "ධාරා විද්‍යුතය සහ ඕම්ගේ නියමය ප්‍රස්තාරික විශ්ලේෂණය",
          probabilityPercent = 92,
          priorityBadge = "🎯 නිශ්චිත ප්‍රායෝගික පරීක්ෂණ",
          questionFormat = "Part II හි ව්‍යුහගත ප්‍රශ්න අංක 3 හෝ 4",
          keyConcepts = listOf("ශ්‍රේණිගත හා සමාන්තරගත ප්‍රතිරෝධ සමක සෙවීම", "V-I ප්‍රස්තාරයෙන් ප්‍රතිරෝධය සෙවීම", "විද්‍යුත් බලය P = VI සූත්‍ර ගණනය කිරීම්"),
          pastExamFrequency = "2018 සිට 2024 දක්වා සෑම වාර විභාගයකම ප්‍රධාන ප්‍රශ්නයකි.",
          pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
        ),
        SpotTopicItem(
          id = "spot_sci_3",
          topicSinhala = "මූලද්‍රව්‍ය ආවර්තිතාවය සහ රසායනික බන්ධන",
          probabilityPercent = 88,
          priorityBadge = "⭐️ වැදගත් රසායන විද්‍යා ඉලක්ක",
          questionFormat = "Part I MCQ 3ක් සහ Part II රචනා ප්‍රශ්න",
          keyConcepts = listOf("අයනික හා සහසංයුජ බන්ධන ලක්ෂ්‍ය-ලක්ෂ ක්‍රමය", "විද්‍යුත් සෘණතාව සහ අයනීකරණ ශක්ති රටා", "ලෝහවල ප්‍රතික්‍රියාශීලීතා ශ්‍රේණිය"),
          pastExamFrequency = "පසුගිය වසරවල අඛණ්ඩව ප්‍රශ්න අන්තර්ගත විය.",
          pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
        )
      )
      "ගණිතය" -> listOf(
        SpotTopicItem(
          id = "spot_math_1",
          topicSinhala = "වෘත්ත ජ්‍යාමිතිය සහ ස්පර්ශක ප්‍රමේය (Theorems)",
          probabilityPercent = 98,
          priorityBadge = "🔥 98% නිශ්චිත ප්‍රමේය සාධන",
          questionFormat = "Part II B කොටසේ ලකුණු 10ක අනිවාර්ය ජ්‍යාමිතික සාධනය",
          keyConcepts = listOf("කේන්ද්‍ර කෝණය පරිධි කෝණය මෙන් දෙගුණයකි", "චක්‍රීය චතුරස්‍රවල සම්මුඛ කෝණ පරිපූරක වේ", "ඒකාන්තර ඛණ්ඩ ප්‍රමේයය (Alternate Segment Theorem)"),
          pastExamFrequency = "සාමාන්‍ය පෙළ සහ වාර විභාගවල 100% ක් අනිවාර්ය ප්‍රශ්නයකි.",
          pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
        ),
        SpotTopicItem(
          id = "spot_math_2",
          topicSinhala = "ත්‍රිකෝණමිතිය උන්නතාංශ හා අවනතාංශ ගැටලු",
          probabilityPercent = 90,
          priorityBadge = "🎯 ලකුණු 10ක රචනා ගැටලුව",
          questionFormat = "Part II B කොටසේ ලකුණු 10ක ප්‍රශ්නයක්",
          keyConcepts = listOf("sin, cos, tan අනුපාත යෙදීම", "උන්නතාංශ හා අවනතාංශ රූපසටහන් ඇඳීම", "ලඝුගණක මගින් සුළු කිරීම්"),
          pastExamFrequency = "සෑම වසරකම Part II ප්‍රශ්න පත්‍රයට පැමිණේ.",
          pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
        ),
        SpotTopicItem(
          id = "spot_math_3",
          topicSinhala = "සමාන්තර හා ගුණෝත්තර ශ්‍රේඪි (Series)",
          probabilityPercent = 89,
          priorityBadge = "⭐️ සූත්‍ර හා පද සෙවීම්",
          questionFormat = "Part I කෙටි ප්‍රශ්න 1ක් සහ Part II හි ලකුණු 10ක ප්‍රශ්නයක්",
          keyConcepts = listOf("Tn = a + (n-1)d සහ Sn = n/2 [2a + (n-1)d]", "ගුණෝත්තර ශ්‍රේඪි Tn = ar^(n-1)", "වචන ගැටලු සමීකරණ බවට පත්කිරීම"),
          pastExamFrequency = "2020-2024 අඛණ්ඩව ප්‍රශ්න අන්තර්ගත විය.",
          pdfUri = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
        )
      )
      "ඉතිහාසය" -> listOf(
        SpotTopicItem(
          id = "spot_hist_1",
          topicSinhala = "අනුරාධපුර වාරි කර්මාන්තය සහ මහා පරාක්‍රමබාහු රජු",
          probabilityPercent = 96,
          priorityBadge = "🔥 96% ප්‍රධාන රචනා ඉලක්කය",
          questionFormat = "Part II හි ලකුණු 15ක ප්‍රධාන රචනා ප්‍රශ්නයක් + සිතියම් ලකුණු කිරීම්",
          keyConcepts = listOf("මහා පරාක්‍රමබාහු රජුගේ වාරි හා කෘෂිකාර්මික ප්‍රතිසංස්කරණ", "පරාක්‍රම සමුද්‍රය හා ඇළ මාර්ග", "සිතියමේ වාරි වැව් 3ක් ලකුණු කිරීම"),
          pastExamFrequency = "සෑම වසරකම පාහේ රචනා හෝ සිතියම් ප්‍රශ්නයක් ලෙස පැමිණේ.",
          pdfUri = "https://drive.google.com/file/d/1Ry6utaFim_tZl8OkTG5hoD6oB4RB8Uxl/preview"
        ),
        SpotTopicItem(
          id = "spot_hist_2",
          topicSinhala = "1818 සහ 1848 නිදහස් අරගල සංසන්දනය",
          probabilityPercent = 91,
          priorityBadge = "🎯 වැදගත් ඓතිහාසික පසුබිම",
          questionFormat = "Part II රචනා ප්‍රශ්න",
          keyConcepts = listOf("1818 කැරැල්ලට හේතු සහ කැප්පෙටිපොළ නිලමේගේ නායකත්වය", "1848 මාතලේ කැරැල්ල හා පුරන් අප්පු", "බ්‍රිතාන්‍ය ප්‍රතිපත්තිවල බලපෑම"),
          pastExamFrequency = "පසුගිය වසරවල මාරුවෙන් මාරුවට අසන ප්‍රධාන මාතෘකාවකි.",
          pdfUri = "https://drive.google.com/file/d/1Ry6utaFim_tZl8OkTG5hoD6oB4RB8Uxl/preview"
        )
      )
      else -> listOf(
        SpotTopicItem(
          id = "spot_gen_1",
          topicSinhala = "$subject - ප්‍රධාන විෂය නිර්දේශ ඉලක්ක හා සංකල්ප",
          probabilityPercent = 90,
          priorityBadge = "🔥 ඉහළ සම්භාවිතාව",
          questionFormat = "Part I MCQ සහ Part II ව්‍යුහගත රචනා",
          keyConcepts = listOf("ප්‍රධාන නියම සහ අර්ථ දැක්වීම්", "ප්‍රායෝගික උදාහරණ හා යෙදීම්", "කෙටි සටහන් සම්පූර්ණ කියවීම"),
          pastExamFrequency = "විභාග ප්‍රශ්න පත්‍රයේ ප්‍රධාන කොටසකි.",
          pdfUri = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
        )
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSpotTopicsScreen(
  grade: String,
  onBack: () -> Unit,
  onOpenPdfModal: (url: String, title: String) -> Unit = { _, _ -> }
) {
  val subjects = listOf("විද්‍යාව", "ගණිතය", "ඉතිහාසය", "සිංහල", "තොරතුරු තාක්ෂණය (ICT)", "බුද්ධ ධර්මය", "භූගෝල විද්‍යාව", "වාණිජ")
  var selectedSubject by remember { mutableStateOf("විද්‍යාව") }
  val spotTopics = remember(selectedSubject, grade) { SpotTopicsRepository.getSpotTopics(selectedSubject, grade) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("🎯 විභාග ඉලක්කගත අනුමාන මාතෘකා", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("O/L & Term Exam Spot Topics & Trends", fontSize = 11.sp, color = NeutralMedium)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(Color(0xFFF8FAFC))
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header Banner
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF4338CA)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔥", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("පසුගිය වසර 10 ප්‍රශ්න රටා විශ්ලේෂණය", color = Color(0xFFFDE047), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "විභාගයට ඒමට වැඩිම සම්භාවිතාවක් ඇති ප්‍රධාන පාඩම් සහ රචනා ප්‍රශ්න රටාවන් මෙහි දක්වා ඇත.",
              color = Color.White,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
          }
        }
      }

      // Subject Selector Chips
      item {
        Text("විෂය තෝරන්න:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeutralDark)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(subjects) { sub ->
            FilterChip(
              selected = selectedSubject == sub,
              onClick = { selectedSubject = sub },
              label = { Text(sub, fontSize = 11.sp, fontWeight = if (selectedSubject == sub) FontWeight.Bold else FontWeight.Normal) }
            )
          }
        }
      }

      items(spotTopics) { item ->
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
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFECACA))
              ) {
                Text(
                  text = item.priorityBadge,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFDC2626),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFEEF2FF)
              ) {
                Text(
                  text = "සම්භාවිතාව: ${item.probabilityPercent}%",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF4338CA),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = item.topicSinhala,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = NeutralDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF1F5F9),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("📋 විභාග ප්‍රශ්න ආකෘතිය:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                Text(item.questionFormat, fontSize = 12.sp, color = Color(0xFF1E293B))
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("🎯 අනිවාර්යයෙන් පාඩම් කළ යුතු ප්‍රධාන සංකල්ප:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
            Spacer(modifier = Modifier.height(4.dp))
            item.keyConcepts.forEach { concept ->
              Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                Text("• ", fontSize = 12.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                Text(concept, fontSize = 12.sp, color = Color(0xFF475569))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.HistoryEdu, contentDescription = "Past Trends", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(item.pastExamFrequency, fontSize = 11.sp, color = Color(0xFFB45309))
            }

            if (item.pdfUri != null) {
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { onOpenPdfModal(item.pdfUri, item.topicSinhala) },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(38.dp)
              ) {
                Icon(Icons.Default.MenuBook, contentDescription = "Read Note", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("අදාළ කෙටි සටහන බලන්න (PDF)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

// ==============================================================================
// 3. SMART PDF DIGITAL STICKY NOTES & HIGHLIGHT TOOL (සටහන් Highlight කිරීම)
// ==============================================================================

data class PdfStickyNote(
  val id: String,
  val pdfTitle: String,
  val noteText: String,
  val colorHex: Long, // Color int
  val timestamp: String,
  val tag: String
)

object PdfAnnotationManager {
  val notesList = mutableStateListOf(
    PdfStickyNote(
      id = "note_1",
      pdfTitle = "විද්‍යාව කෙටි සටහන්",
      noteText = "ආලෝක ප්‍රතික්‍රියාව සිදුවන්නේ තයිලකොයිඩ පටලයේය. අඳුරු ප්‍රතික්‍රියාව ස්ට්‍රෝමාවේය. විභාගයට අනිවාර්යයි!",
      colorHex = 0xFFFEF08A, // Yellow
      timestamp = "අද, පෙ.ව. 10:30",
      tag = "🔥 විභාග ඉලක්ක"
    ),
    PdfStickyNote(
      id = "note_2",
      pdfTitle = "ගණිතය ජ්‍යාමිතිය",
      noteText = "වෘත්තයක කේන්ද්‍රයේ සිට ජ්‍යායකට අඳින ලම්භය මගින් ජ්‍යාය සමච්ඡේද වේ. සාධනය සූදානම් කරගන්න.",
      colorHex = 0xFFBBF7D0, // Green
      timestamp = "ඊයේ, ප.ව. 4:15",
      tag = "⭐️ ප්‍රමේයය"
    )
  )

  fun addNote(pdfTitle: String, text: String, colorHex: Long, tag: String) {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    notesList.add(
      0,
      PdfStickyNote(
        id = System.currentTimeMillis().toString(),
        pdfTitle = pdfTitle,
        noteText = text,
        colorHex = colorHex,
        timestamp = sdf.format(Date()),
        tag = tag
      )
    )
  }

  fun deleteNote(id: String) {
    notesList.removeAll { it.id == id }
  }
}

@Composable
fun SmartPdfStickyNotesOverlay(
  pdfTitle: String,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  var newNoteText by remember { mutableStateOf("") }
  var selectedColorHex by remember { mutableStateOf(0xFFFEF08A) } // Yellow
  var selectedTag by remember { mutableStateOf("🔥 විභාග ඉලක්ක") }

  val relevantNotes = PdfAnnotationManager.notesList.filter {
    it.pdfTitle.contains(pdfTitle, ignoreCase = true) || pdfTitle.contains(it.pdfTitle, ignoreCase = true)
  }

  Box(modifier = modifier) {
    if (!isExpanded) {
      // Floating Sticky Notes Button
      Surface(
        onClick = { isExpanded = true },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFEF08A),
        border = BorderStroke(1.dp, Color(0xFFFACC15)),
        shadowElevation = 6.dp,
        modifier = Modifier.padding(12.dp).testTag("pdf_sticky_notes_toggle")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("📝", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Sticky Notes (${relevantNotes.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color(0xFF713F12)
          )
        }
      }
    } else {
      // Expanded Notes Panel Dialog / Bottom Drawer
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .heightIn(max = 420.dp)
          .padding(8.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("📝", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("මගේ සටහන් & Highlights", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeutralDark)
            }

            IconButton(onClick = { isExpanded = false }, modifier = Modifier.size(26.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = NeutralMedium)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Color Palette Picker (Yellow, Green, Pink, Blue)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("වර්ණය:", fontSize = 10.sp, color = NeutralMedium)
            listOf(
              0xFFFEF08A to "කහ",
              0xFFBBF7D0 to "කොළ",
              0xFFFBCFE8 to "රෝස",
              0xFFBFDBFE to "නිල්"
            ).forEach { (cHex, _) ->
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(Color(cHex))
                  .border(
                    if (selectedColorHex == cHex) 2.dp else 1.dp,
                    if (selectedColorHex == cHex) Color.Black else Color.LightGray,
                    CircleShape
                  )
                  .clickable { selectedColorHex = cHex }
              )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick Tag Chip
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFF1F5F9)
            ) {
              Text(
                text = selectedTag,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Add Note Field
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            androidx.compose.material3.OutlinedTextField(
              value = newNoteText,
              onValueChange = { newNoteText = it },
              placeholder = { Text("වැදගත් කරුණක් මෙහි ලියා සුරකින්න...", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              singleLine = false,
              maxLines = 2
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
              onClick = {
                if (newNoteText.isNotBlank()) {
                  PdfAnnotationManager.addNote(pdfTitle, newNoteText.trim(), selectedColorHex, selectedTag)
                  newNoteText = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(selectedColorHex)),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
              Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = Color(0xFFF1F5F9))
          Spacer(modifier = Modifier.height(6.dp))

          // Saved Notes List
          LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (relevantNotes.isEmpty()) {
              item {
                Text(
                  text = "තවමත් මෙම PDF එක සඳහා සටහන් එකතු කර නැත.",
                  fontSize = 11.sp,
                  color = NeutralMedium,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
              }
            }
            items(relevantNotes) { note ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(note.colorHex),
                border = BorderStroke(1.dp, Color(note.colorHex).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(note.tag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.7f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(note.timestamp, fontSize = 9.sp, color = Color.Black.copy(alpha = 0.5f))
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                          .size(14.dp)
                          .clickable { PdfAnnotationManager.deleteNote(note.id) }
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(note.noteText, fontSize = 11.sp, color = Color.Black, lineHeight = 15.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
