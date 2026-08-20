package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class GradeCardInfo(
  val grade: String,
  val gradeSinhala: String,
  val subtitle: String,
  val description: String,
  val imageRes: Int,
  val color: Color,
  val tag: String
)

val availableGradesList = listOf(
  GradeCardInfo(
    grade = "11",
    gradeSinhala = "11 ශ්‍රේණිය (Grade 11)",
    subtitle = "අ.පො.ස. සාමාන්‍ය පෙළ (O/L)",
    description = "විද්‍යාව, ගණිතය, ඉතිහාසය, කෙටි සටහන් හා පසුගිය ප්‍රශ්න පත්‍ර",
    imageRes = R.drawable.img_portal_header_bg_1786107362621,
    color = Color(0xFF1B5E20),
    tag = "O/L විභාගය"
  ),
  GradeCardInfo(
    grade = "10",
    gradeSinhala = "10 ශ්‍රේණිය (Grade 10)",
    subtitle = "සාමාන්‍ය පෙළ මූලික අධ්‍යයන",
    description = "විද්‍යාව, ගණිතය, කෘෂි, වාණිජ්‍ය හා කෙටි සටහන් PDF",
    imageRes = R.drawable.img_subjects_bg_1786107319789,
    color = Color(0xFF0D47A1),
    tag = "O/L ආරම්භය"
  ),
  GradeCardInfo(
    grade = "09",
    gradeSinhala = "09 ශ්‍රේණිය (Grade 09)",
    subtitle = "කනිෂ්ඨ ද්විතීයික අධ්‍යයන",
    description = "විද්‍යාව, ගණිතය, ඉංග්‍රීසි, සිංහල හා තාක්ෂණික විෂයන්",
    imageRes = R.drawable.img_notes_bg_1786107335103,
    color = Color(0xFF6A1B9A),
    tag = "කනිෂ්ඨ අංශය"
  ),
  GradeCardInfo(
    grade = "08",
    gradeSinhala = "08 ශ්‍රේණිය (Grade 08)",
    subtitle = "ද්විතීයික මූලික විෂය මාලාව",
    description = "සාරාංශ සටහන්, අභ්‍යාස හා විභාග ප්‍රශ්නාවලි",
    imageRes = R.drawable.img_papers_bg_1786107349533,
    color = Color(0xFF00695C),
    tag = "මධ්‍යම අංශය"
  ),
  GradeCardInfo(
    grade = "07",
    gradeSinhala = "07 ශ්‍රේණිය (Grade 07)",
    subtitle = "පදනම් විෂය අධ්‍යයනය",
    description = "පාඩම් සාරාංශ, ප්‍රශ්න පත්‍ර හා අධ්‍යයන අත්පොත්",
    imageRes = R.drawable.img_videos_bg_1786110404209,
    color = Color(0xFFB06000),
    tag = "පදනම් අංශය"
  ),
  GradeCardInfo(
    grade = "06",
    gradeSinhala = "06 ශ්‍රේණිය (Grade 06)",
    subtitle = "ද්විතීයික අධ්‍යාපන ප්‍රවේශය",
    description = "මූලික විෂයයන් සඳහා සරල කෙටි සටහන් හා මඟපෙන්වීම්",
    imageRes = R.drawable.img_portal_header_bg_1786107362621,
    color = Color(0xFFC2185B),
    tag = "නව ප්‍රවේශය"
  )
)

fun getSubjectBgImage(subjectNameSinhala: String): Int {
  return when {
    subjectNameSinhala.contains("විද්‍යාව") || subjectNameSinhala.contains("Science") -> R.drawable.img_subjects_bg_1786107319789
    subjectNameSinhala.contains("ගණිතය") || subjectNameSinhala.contains("Math") -> R.drawable.img_papers_bg_1786107349533
    subjectNameSinhala.contains("ඉතිහාසය") || subjectNameSinhala.contains("බුද්ධ") || subjectNameSinhala.contains("සිංහල") || subjectNameSinhala.contains("භූගෝල") -> R.drawable.img_notes_bg_1786107335103
    subjectNameSinhala.contains("තාක්ෂණ") || subjectNameSinhala.contains("ICT") || subjectNameSinhala.contains("වීඩියෝ") -> R.drawable.img_videos_bg_1786110404209
    subjectNameSinhala.contains("සංගීත") || subjectNameSinhala.contains("නර්තන") || subjectNameSinhala.contains("Music") -> R.drawable.img_subjects_bg_1786107319789
    else -> R.drawable.img_portal_header_bg_1786107362621
  }
}

/**
 * STEP 1: Grade Selection Screen (Home Screen)
 * Displays Grades 11 down to 06 in descending order with rich educational background photos
 */
@Composable
fun GradesHomeScreen(
  onGradeSelected: (String) -> Unit,
  onOpenAddModal: () -> Unit,
  onOpenAnalytics: () -> Unit = {},
  onOpenStructuredEssay: () -> Unit = {},
  onOpenFormulaHandbook: () -> Unit = {},
  onOpenVoiceQuiz: () -> Unit = {},
  onOpenEnglishBuilder: () -> Unit = {},
  onOpenSyllabusHub: () -> Unit = {},
  onOpenCountdownPlanner: () -> Unit = {},
  onOpenFlashcards: () -> Unit = {},
  onOpenStudyStreak: () -> Unit = {},
  onOpenSpokenEnglishVoice: () -> Unit = {},
  onOpenBookmarks: () -> Unit = {},
  onOpenMockExam: () -> Unit = {},
  onOpenSpotTopics: () -> Unit = {},
  onOpenLiveDailyQuiz: () -> Unit = {},
  isFeatureTrialActive: Boolean = true,
  isApproved: Boolean = false,
  remainingTrialHours: Int = 24
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 🏆 FEATURE: DAILY AUTOMATED LIVE 7:00 PM QUIZ CONTEST BANNER
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent),
      modifier = Modifier
        .fillMaxWidth()
        .shadow(6.dp, RoundedCornerShape(18.dp))
        .clip(RoundedCornerShape(18.dp))
        .background(
          Brush.horizontalGradient(
            colors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
          )
        )
        .clickable(onClick = onOpenLiveDailyQuiz)
        .testTag("live_daily_quiz_entry_card")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFFF59E0B)),
          contentAlignment = Alignment.Center
        ) {
          Text("🏆", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "⚡ සජීවී දෛනික Quiz තරගය",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFEF4444)
            ) {
              Text(
                text = "7:00 PM LIVE",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "Grades 6-11 • ප්‍රශ්න 20 • Top 10 Leaderboard",
            fontSize = 11.sp,
            color = Color(0xFFC7D2FE)
          )
        }

        Icon(
          Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Open",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }
    // Top Smart Study Tools Quick Strip (විශේෂාංග කලාපය)
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Section Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⚡", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "විශේෂාංග කලාපය (Features Hub)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                fontSize = 15.sp
              )
              Text(
                text = "සියලුම ස්මාර්ට් අධ්‍යයන මෙවලම් සහ කෙටි ක්‍රම",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }
          }
        }

        // Feature 1: Standout English Master Class Hero Banner (Full width)
        EnglishHeroFeatureCard(
          onClick = onOpenEnglishBuilder
        )

        // Row 1: O/L Countdown & Flashcards (Each with unique background photo)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeatureSubCardWithBg(
            title = "1. O/L Countdown",
            subtitle = "දින ගණකය & Planner",
            emoji = "⏱️",
            badgeText = "PLANNER",
            badgeColor = Color(0xFFEA580C),
            backgroundImageRes = R.drawable.img_papers_bg_1786107349533,
            modifier = Modifier.weight(1f),
            onClick = onOpenCountdownPlanner
          )

          FeatureSubCardWithBg(
            title = "2. Flashcards",
            subtitle = "මතක කාඩ්පත්",
            emoji = "💡",
            badgeText = "CARDS",
            badgeColor = Color(0xFF16A34A),
            backgroundImageRes = R.drawable.img_vocab_card_bg_1787064760289,
            modifier = Modifier.weight(1f),
            onClick = onOpenFlashcards
          )
        }

        // Row 2: Study Streak & Bookmarks (Each with unique background photo)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeatureSubCardWithBg(
            title = "3. Daily Streak",
            subtitle = "XP & පදක්කම්",
            emoji = "🔥",
            badgeText = "REWARDS",
            badgeColor = Color(0xFFD97706),
            backgroundImageRes = R.drawable.img_videos_bg_1786110404209,
            modifier = Modifier.weight(1f),
            onClick = onOpenStudyStreak
          )

          FeatureSubCardWithBg(
            title = "4. Bookmarks Hub",
            subtitle = "සුරැකි සටහන්",
            emoji = "🔖",
            badgeText = "SAVED",
            badgeColor = Color(0xFF4F46E5),
            backgroundImageRes = R.drawable.img_portal_header_bg_1786107362621,
            modifier = Modifier.weight(1f),
            onClick = onOpenBookmarks
          )
        }

        // Row 3: AI Voice Quiz & Analytics (Each with unique background photo)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeatureSubCardWithBg(
            title = "5. AI Voice Quiz",
            subtitle = "කටහඬින් ප්‍රශ්න & Viva",
            emoji = "🎙️",
            badgeText = "VOICE AI",
            badgeColor = Color(0xFFDC2626),
            backgroundImageRes = R.drawable.img_listening_card_bg_1787064805369,
            modifier = Modifier.weight(1f),
            onClick = onOpenVoiceQuiz
          )

          FeatureSubCardWithBg(
            title = "6. ප්‍රගති වාර්තා",
            subtitle = "Analytics & Progress",
            emoji = "📊",
            badgeText = "STUDY STATS",
            badgeColor = Color(0xFF2563EB),
            backgroundImageRes = R.drawable.img_subjects_bg_1786107319789,
            modifier = Modifier.weight(1f),
            onClick = onOpenAnalytics
          )
        }

        // Row 4: Structured Essays & Formulas/Timelines (Each with unique background photo)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeatureSubCardWithBg(
            title = "7. ව්‍යුහගත/රචනා",
            subtitle = "Marking Schemes",
            emoji = "📝",
            badgeText = "ESSAY",
            badgeColor = Color(0xFF9333EA),
            backgroundImageRes = R.drawable.img_writing_card_bg_1787064772136,
            modifier = Modifier.weight(1f),
            onClick = onOpenStructuredEssay
          )

          FeatureSubCardWithBg(
            title = "8. සූත්‍ර & Timeline",
            subtitle = "Handbook & Rules",
            emoji = "🧮",
            badgeText = "FORMULA",
            badgeColor = Color(0xFF059669),
            backgroundImageRes = R.drawable.img_grammar_card_bg_1787064790400,
            modifier = Modifier.weight(1f),
            onClick = onOpenFormulaHandbook
          )
        }

        // Row 5: Live Mock Exam Hall & Spot Topics (Exam Simulator & Predictor)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeatureSubCardWithBg(
            title = "9. සජීවී Exam Hall",
            subtitle = "OMR & Timer Simulator",
            emoji = "🏛️",
            badgeText = "EXAM HALL",
            badgeColor = Color(0xFFDC2626),
            backgroundImageRes = R.drawable.img_papers_bg_1786107349533,
            modifier = Modifier.weight(1f),
            onClick = onOpenMockExam
          )

          FeatureSubCardWithBg(
            title = "10. Spot Topics",
            subtitle = "විභාග අනුමාන මාතෘකා",
            emoji = "🎯",
            badgeText = "PREDICTOR",
            badgeColor = Color(0xFF0284C7),
            backgroundImageRes = R.drawable.img_notes_bg_1786107335103,
            modifier = Modifier.weight(1f),
            onClick = onOpenSpotTopics
          )
        }
      }
    }

    // Grid of Grade Cards
    availableGradesList.forEach { item ->
      GradeBannerCard(
        gradeInfo = item,
        onClick = { onGradeSelected(item.grade) }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))
  }
}

@Composable
fun EnglishHeroFeatureCard(
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.5.dp, Color(0xFFF59E0B).copy(alpha = 0.7f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_english_header_1787064736646),
        contentDescription = "English Class",
        contentScale = ContentScale.Crop,
        modifier = Modifier.matchParentSize()
      )
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color(0xFF0F172A).copy(alpha = 0.92f),
                Color(0xFF1E1B4B).copy(alpha = 0.88f),
                Color(0xFF312E81).copy(alpha = 0.80f)
              )
            )
          )
      )
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Text("🇬🇧", fontSize = 22.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "ඉංග්‍රීසි පන්තිය (English Hub)",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFDE047)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF59E0B)
              ) {
                Text(
                  text = "06-11 ALL",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF78350F),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "කෙටි සටහන්, ව්‍යාකරණ, රචනා & 🎙️ Voice Mic AI",
              fontSize = 9.5.sp,
              color = Color(0xFFE2E8F0),
              maxLines = 1
            )
          }
        }
        Icon(
          imageVector = Icons.Default.ArrowForwardIos,
          contentDescription = "Open",
          tint = Color(0xFFFDE047),
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
fun FeatureSubCardWithBg(
  title: String,
  subtitle: String,
  emoji: String,
  badgeText: String? = null,
  badgeColor: Color = Color(0xFFF59E0B),
  backgroundImageRes: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
    ) {
      Image(
        painter = painterResource(id = backgroundImageRes),
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.matchParentSize()
      )
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xFF0F172A).copy(alpha = 0.85f),
                Color(0xFF1E293B).copy(alpha = 0.90f),
                Color(0xFF0F172A).copy(alpha = 0.96f)
              )
            )
          )
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(emoji, fontSize = 15.sp)
          if (badgeText != null) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = badgeColor
            ) {
              Text(
                text = badgeText,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }
        Column {
          Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
          )
          Text(
            text = subtitle,
            fontSize = 8.5.sp,
            color = Color(0xFFCBD5E1),
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
fun GradeBannerCard(
  gradeInfo: GradeCardInfo,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("grade_card_${gradeInfo.grade}")
      .clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp)
    ) {
      Image(
        painter = painterResource(id = gradeInfo.imageRes),
        contentDescription = gradeInfo.gradeSinhala,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.85f),
                Color.Black.copy(alpha = 0.45f)
              )
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = gradeInfo.color
          ) {
            Text(
              text = gradeInfo.tag,
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.25f)
          ) {
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = "Open Grade",
              tint = Color.White,
              modifier = Modifier
                .padding(6.dp)
                .size(18.dp)
            )
          }
        }

        Column {
          Text(
            text = gradeInfo.gradeSinhala,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = gradeInfo.subtitle,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFFD54F)
          )
          Text(
            text = gradeInfo.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

/**
 * STEP 2: Subject Selection Screen for a Chosen Grade
 * Displays list of subjects for that grade with individual background photos
 */
@Composable
fun GradeSubjectsScreen(
  grade: String,
  subjectsList: SnapshotStateList<SubjectItem>,
  onSubjectSelected: (SubjectItem) -> Unit,
  onBackToGrades: () -> Unit,
  onAddSubject: () -> Unit,
  onDeleteSubject: (SubjectItem) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Navigation bar back button + Grade title
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = BluePrimaryContainer,
      border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.2f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBackToGrades,
            modifier = Modifier.testTag("back_to_grades_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Grades",
              tint = BlueOnPrimaryContainer
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Column {
            Text(
              text = "$grade ශ්‍රේණිය • විෂයන් (Subjects)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = BlueOnPrimaryContainer
            )
            Text(
              text = "විෂයයක් තෝරා කෙටි සටහන්, ප්‍රශ්න පත්‍ර හා වීඩියෝ බලන්න",
              style = MaterialTheme.typography.bodySmall,
              color = BlueOnPrimaryContainer.copy(alpha = 0.8f),
              fontSize = 11.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = BluePrimary,
          onClick = onAddSubject
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Subject",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("නව විෂයක්", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (subjectsList.isEmpty()) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceVariantLight,
        border = BorderStroke(1.dp, NeutralBorderLight),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = "Empty Subjects",
            tint = BluePrimary,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "මෙම ශ්‍රේණිය සඳහා විෂයන් නොමැත",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NeutralDark
          )
          Text(
            text = "නව විෂයක් එක් කිරීමට ඉහත 'නව විෂයක්' බොත්තම ඔබන්න.",
            fontSize = 12.sp,
            color = NeutralMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
          )
        }
      }
    } else {
      subjectsList.forEach { subject ->
        SubjectPhotoCard(
          subject = subject,
          grade = grade,
          onClick = { onSubjectSelected(subject) },
          onDelete = { onDeleteSubject(subject) }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))
  }
}

@Composable
fun SubjectPhotoCard(
  subject: SubjectItem,
  grade: String,
  onClick: () -> Unit,
  onDelete: (() -> Unit)? = null
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, NeutralBorderLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("subject_card_${subject.id}")
      .clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(105.dp)
    ) {
      Image(
        painter = painterResource(id = getSubjectBgImage(subject.nameSinhala)),
        contentDescription = subject.nameSinhala,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.85f),
                Color.Black.copy(alpha = 0.4f)
              )
            )
          )
      )

      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(subject.color.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = subject.icon,
              contentDescription = subject.name,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = subject.nameSinhala,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "${subject.name} • $grade ශ්‍රේණිය",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFFFD54F),
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "කෙටි සටහන් • ප්‍රශ්න පත්‍ර • වීඩියෝ",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (onDelete != null) {
            Surface(
              onClick = onDelete,
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.2f),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Delete Subject",
                  tint = Color(0xFFFF8A80),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.25f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "විවෘත කරන්න",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }
  }
}

fun normalizeSubjectKey(name: String): String {
  val lower = name.lowercase().trim()
  return when {
    lower.contains("විද්‍යා") || lower.contains("science") -> "science"
    lower.contains("ගණිත") || lower.contains("math") -> "math"
    lower.contains("ඉතිහාස") || lower.contains("history") -> "history"
    lower.contains("බුද්ධ") || lower.contains("buddhism") -> "buddhism"
    lower.contains("ව්‍යාපාර") || lower.contains("ගිණුම්") || lower.contains("commerce") || lower.contains("accounting") -> "commerce"
    lower.contains("තොරතුරු") || lower.contains("තාක්ෂණ") || lower.contains("ict") -> "ict"
    lower.contains("කෘෂි") || lower.contains("ආහාර") || lower.contains("agri") -> "agri"
    lower.contains("සෞඛ්‍ය") || lower.contains("ශාරීරික") || lower.contains("health") -> "health"
    lower.contains("සිංහල") || lower.contains("sinhala") -> "sinhala"
    lower.contains("ඉංග්‍රීසි") || lower.contains("english") -> "english"
    lower.contains("භූගෝල") || lower.contains("geography") -> "geography"
    lower.contains("පුරවැසි") || lower.contains("civic") -> "civic"
    else -> lower
  }
}

fun isMatchSubject(itemSubject: String, itemTitle: String, targetSubject: SubjectItem): Boolean {
  val targetKey = normalizeSubjectKey(targetSubject.nameSinhala + " " + targetSubject.name)
  val itemKey = normalizeSubjectKey(itemSubject + " " + itemTitle)
  return targetKey == itemKey ||
      itemSubject.contains(targetSubject.nameSinhala, ignoreCase = true) ||
      targetSubject.nameSinhala.contains(itemSubject, ignoreCase = true)
}

/**
 * STEP 3: Content Tabs Screen for a Selected Subject
 * Shows 3 Tabs:
 * Tab 0: කෙටි සටහන් (Short Notes - PDFs)
 * Tab 1: ප්‍රශ්න පත්‍ර (Question Papers - PDFs)
 * Tab 2: කෙටි සටහන් වීඩියෝස් (Video Lessons)
 */
@Composable
fun SubjectContentScreen(
  grade: String,
  subject: SubjectItem,
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  notesList: List<ShortNoteItem>,
  papersList: List<QuestionPaperItem>,
  videosList: List<VideoLessonItem>,
  onPdfClick: (title: String, pdfUri: String?, isPasswordProtected: Boolean, password: String?) -> Unit,
  onDeleteNote: (ShortNoteItem) -> Unit,
  onDeletePaper: (QuestionPaperItem) -> Unit,
  onDeleteVideo: (VideoLessonItem) -> Unit,
  onVideoClick: (VideoLessonItem) -> Unit,
  onBackToSubjects: () -> Unit,
  onAddContent: (String) -> Unit,
  onStartQuizSet: (QuizSet) -> Unit = {},
  onOpenFlashcards: () -> Unit = {},
  activeAudio: ActiveAudioState? = null,
  onPlayAudio: (ChapterItem, speed: Float) -> Unit = { _, _ -> },
  onPauseAudio: () -> Unit = {},
  onResumeAudio: () -> Unit = {},
  onOpenPdfAtPage: (pdfUrl: String, title: String, page: Int) -> Unit = { _, _, _ -> },
  onOpenAnalytics: () -> Unit = {},
  onOpenStructuredEssay: () -> Unit = {},
  onOpenFormulaHandbook: () -> Unit = {},
  onOpenVoiceQuiz: () -> Unit = {},
  onOpenEnglishBuilder: () -> Unit = {},
  onOpenSyllabusHub: () -> Unit = {}
) {
  val subjectNotes = notesList.filter { isMatchSubject(it.subject, it.topicSinhala, subject) }
  val subjectPapers = papersList.filter { isMatchSubject(it.subject, it.titleSinhala, subject) }
  val activePdfUrl = subjectNotes.firstOrNull()?.pdfUri ?: "https://drive.google.com/file/d/1sample_100page_notes/preview"

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Subject Banner Header with Back Button
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, NeutralBorderLight),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
      ) {
        Image(
          painter = painterResource(id = getSubjectBgImage(subject.nameSinhala)),
          contentDescription = subject.nameSinhala,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.85f),
                  Color.Black.copy(alpha = 0.35f)
                )
              )
            )
        )

        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.25f),
              onClick = onBackToSubjects,
              modifier = Modifier.testTag("back_to_subjects_btn")
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
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = subject.color
              ) {
                Text(
                  text = "$grade ශ්‍රේණිය",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = subject.nameSinhala,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "${subject.name} • 5-Section Hub",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // Quick Tool Strip inside Subject
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Surface(
        onClick = onOpenAnalytics,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("📊", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(2.dp))
          Text("ප්‍රගතිය", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8), maxLines = 1)
        }
      }

      Surface(
        onClick = onOpenStructuredEssay,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFAF5FF),
        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("📝", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(2.dp))
          Text("රචනා", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE), maxLines = 1)
        }
      }

      Surface(
        onClick = onOpenFormulaHandbook,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("🧮", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(2.dp))
          Text("සූත්‍ර", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), maxLines = 1)
        }
      }

      Surface(
        onClick = onOpenVoiceQuiz,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFFECACA)),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("🎙️", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(2.dp))
          Text("Viva", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C), maxLines = 1)
        }
      }

      Surface(
        onClick = onOpenEnglishBuilder,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFFBEB),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text("🔤", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(2.dp))
          Text("English", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), maxLines = 1)
        }
      }
    }

    // 5-Section Subject Hub: 1. කෙටි සටහන් | 2. ප්‍රශ්න පත්‍ර | 3. ස්වයං පුහුණු | 4. 🎧 Audio | 5. ⚡ AI Assistant
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = SurfaceVariantLight,
      border = BorderStroke(1.dp, NeutralBorderLight),
      modifier = Modifier.fillMaxWidth()
    ) {
      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = BluePrimary,
        edgePadding = 8.dp,
        indicator = { tabPositions ->
          if (selectedTab < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = if (selectedTab >= 3) Color(0xFF7C3AED) else BluePrimary,
              height = 3.dp
            )
          }
        }
      ) {
        val tabTitles = listOf(
          "කෙටි සටහන්",
          "ප්‍රශ්න පත්‍ර",
          "ස්වයං පුහුණු",
          "🎧 Audio Notes",
          "⚡ AI Assistant"
        )
        val tabIcons = listOf(
          Icons.Default.Description,
          Icons.Default.Quiz,
          Icons.Default.MenuBook,
          Icons.Default.Headphones,
          Icons.Default.AutoAwesome
        )

        tabTitles.forEachIndexed { index, title ->
          val isSelected = selectedTab == index
          val isAiHighlighted = index >= 3

          Tab(
            selected = isSelected,
            onClick = { onTabSelected(index) },
            text = {
              Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = when {
                  isSelected && isAiHighlighted -> Color(0xFF7C3AED)
                  isSelected -> BluePrimary
                  isAiHighlighted -> Color(0xFF6D28D9)
                  else -> NeutralMedium
                },
                maxLines = 1
              )
            },
            icon = {
              Icon(
                imageVector = tabIcons[index],
                contentDescription = title,
                tint = when {
                  isSelected && isAiHighlighted -> Color(0xFF7C3AED)
                  isSelected -> BluePrimary
                  isAiHighlighted -> Color(0xFF6D28D9)
                  else -> NeutralMedium
                },
                modifier = Modifier.size(17.dp)
              )
            },
            modifier = Modifier.testTag("content_tab_$index")
          )
        }
      }
    }

    // DEDICATED FEATURE BAR: Syllabus Detection & Google Drive Content Hub (Slim & Compact)
    Surface(
      onClick = onOpenSyllabusHub,
      shape = RoundedCornerShape(10.dp),
      color = Color(0xFF1E1B4B),
      border = BorderStroke(1.dp, Color(0xFF6366F1)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("syllabus_detection_subject_bar")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(0xFF4F46E5)),
            contentAlignment = Alignment.Center
          ) {
            Text("📚", fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "විෂය නිර්දේශ හඳුනාගැනීම & Drive PDFs",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.width(5.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF10B981)
              ) {
                Text(
                  text = "${subject.nameSinhala} • $grade වසර",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "නිල පාඩම් මාලා • Markdown වගු • මතක ක්‍රම • Marking Schemes",
              fontSize = 8.5.sp,
              color = Color(0xFFC7D2FE)
            )
          }
        }

        Surface(
          shape = CircleShape,
          color = Color(0xFF4338CA)
        ) {
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Open Syllabus Hub",
            tint = Color.White,
            modifier = Modifier
              .padding(4.dp)
              .size(13.dp)
          )
        }
      }
    }

    // SECTION 1 (Tab 0): කෙටි සටහන් (Short Notes - PDFs)
    if (selectedTab == 0) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "කෙටි සටහන් හා PDF ලේඛන (${subjectNotes.size})",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = NeutralDark
        )

        Button(
          onClick = { onAddContent("NOTE") },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("PDF එකක් එක් කරන්න", fontSize = 11.sp)
        }
      }

      if (subjectNotes.isEmpty()) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = SurfaceVariantLight,
          border = BorderStroke(1.dp, NeutralBorderLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.PictureAsPdf,
              contentDescription = "PDF Empty",
              tint = Color(0xFFC62828),
              modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "${subject.nameSinhala} සඳහා කෙටි සටහන් නොමැත",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = NeutralDark
            )
            Text(
              text = "ඔබ සතු PDF එකතු කිරීමට 'PDF එකක් එක් කරන්න' බොත්තම ඔබන්න.",
              fontSize = 11.sp,
              color = NeutralMedium,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      } else {
        subjectNotes.forEach { note ->
          ShortNoteCardRow(
            note = note,
            onClick = {
              onPdfClick(note.topicSinhala, note.pdfUri, note.isPasswordProtected, note.password)
            },
            onDelete = { onDeleteNote(note) },
            onOpenPdf = {
              onPdfClick(note.topicSinhala, note.pdfUri, note.isPasswordProtected, note.password)
            },
            onOpenQuiz = {
              val quizData = QuizRepository.getQuizDataForSubject(grade, subject.nameSinhala, note.pdfUri ?: note.id)
              onStartQuizSet(quizData.quiz_sets.first())
            },
            onOpenFlashcards = {
              onOpenFlashcards()
            }
          )
        }
      }
    }

    // SECTION 2 (Tab 1): ප්‍රශ්න පත්‍ර (G.C.E. O/L Papers & Term Test Papers & Custom Papers)
    if (selectedTab == 1) {
      // 1. GRADE 11 SPECIAL: G.C.E. O/L 6-Year Exam Papers & Marking Schemes (2024 මෙවර සිට 2019 දක්වා)
      if (grade == "11") {
        GceOlPastPapersSection(
          subject = subject,
          onOpenPdf = { title, url ->
            onPdfClick(title, url, false, null)
          }
        )

        Spacer(modifier = Modifier.height(16.dp))
      }

      // 2. SUB-SECTION: වාර විභාග ප්‍රශ්න පත්‍ර (Term Test Evaluation Papers - 1st, 2nd, 3rd Terms across 5 Years)
      TermTestPapersSection(
        grade = grade,
        subject = subject,
        onOpenPdf = { title, url ->
          onPdfClick(title, url, false, null)
        }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 2. SUB-SECTION: අතිරේක හා ගුරුභවතුන් එක් කළ ප්‍රශ්න පත්‍ර (Custom/Community Added Papers)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "අතිරේක හා ආදර්ශ ප්‍රශ්න පත්‍ර (${subjectPapers.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = NeutralDark
          )
          Text(
            text = "අභිරුචි PDF ප්‍රශ්න පත්‍ර හා සාකච්ඡා සටහන්",
            fontSize = 10.sp,
            color = NeutralMedium
          )
        }

        Button(
          onClick = { onAddContent("PAPER") },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333)),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("PDF එකක් එක් කරන්න", fontSize = 11.sp)
        }
      }

      if (subjectPapers.isNotEmpty()) {
        subjectPapers.forEach { paper ->
          QuestionPaperCardRow(
            paper = paper,
            onClick = {
              onPdfClick(paper.titleSinhala, paper.pdfUri, paper.isPasswordProtected, paper.password)
            },
            onDelete = { onDeletePaper(paper) },
            onOpenPdf = {
              onPdfClick(paper.titleSinhala, paper.pdfUri, paper.isPasswordProtected, paper.password)
            }
          )
        }
      }
    }

    // SECTION 3 (Tab 2): ස්වයං පුහුණු (Practice Quizzes & Flashcards)
    if (selectedTab == 2) {
      PracticeQuizzesSection(
        grade = grade,
        subject = subject,
        onStartQuizSet = onStartQuizSet,
        onOpenFlashcards = onOpenFlashcards
      )
    }

    // SECTION 4 (Tab 3): 🎧 Audio Notes & Podcasts (AI Powered)
    if (selectedTab == 3) {
      AudioNotesSection(
        grade = grade,
        subject = subject,
        pdfUrl = activePdfUrl,
        activeAudio = activeAudio,
        onPlayAudio = onPlayAudio,
        onPauseAudio = onPauseAudio,
        onResumeAudio = onResumeAudio,
        onOpenPdfAtPage = onOpenPdfAtPage
      )
    }

    // SECTION 5 (Tab 4): ⚡ AI Smart Assistant & Voice Helper
    if (selectedTab == 4) {
      AiSmartAssistantSection(
        grade = grade,
        subject = subject,
        pdfUrl = activePdfUrl,
        onOpenPdfAtPage = onOpenPdfAtPage
      )
    }

    Spacer(modifier = Modifier.height(10.dp))
  }
}
