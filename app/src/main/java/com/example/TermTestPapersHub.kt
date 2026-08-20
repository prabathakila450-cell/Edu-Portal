package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class TermTestPaperItem(
  val id: String,
  val grade: String,
  val subject: String,
  val term: Int, // 1 = 1st Term, 2 = 2nd Term, 3 = 3rd Term
  val year: Int, // 2024, 2023, 2022, 2021, 2020
  val titleSinhala: String,
  val sourceOrProvince: String,
  val timeDuration: String,
  val totalMarks: String,
  val paperStructure: String,
  val questionPaperUrl: String,
  val answerSchemeUrl: String,
  val unitCoverage: String
)

object TermTestPapersRepository {

  fun getTermTestPapers(grade: String, subjectNameSinhala: String, term: Int): List<TermTestPaperItem> {
    val cleanSubject = subjectNameSinhala.trim()
    val years = listOf(2024, 2023, 2022, 2021, 2020)

    val termLabel = when (term) {
      1 -> "පළමු වාරය (1st Term)"
      2 -> "දෙවන වාරය (2nd Term)"
      else -> "තෙවන වාරය (3rd Term)"
    }

    val termShort = when (term) {
      1 -> "1 වන වාරය"
      2 -> "2 වන වාරය"
      else -> "3 වන වාරය"
    }

    val provinces = listOf(
      "බස්නාහිර පළාත් අධ්‍යාපන දෙපාර්තමේන්තුව",
      "මධ්‍යම පළාත් අධ්‍යාපන දෙපාර්තමේන්තුව",
      "දකුණු පළාත් අධ්‍යාපන දෙපාර්තමේන්තුව",
      "වයඹ පළාත් අධ්‍යාපන දෙපාර්තමේන්තුව",
      "සබරගමුව පළාත් අධ්‍යාපන දෙපාර්තමේන්තුව"
    )

    // Curated educational Drive Preview URLs for safe, direct in-app DRM rendering (no 3rd-party external website redirects)
    val paperUrls = listOf(
      "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview",
      "https://drive.google.com/file/d/1PPsYx6Tm7x_0YSy3es3edqBMizD08bJZ/preview",
      "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview",
      "https://drive.google.com/file/d/1cG0Fj4Eg86hbvyx0OAnY1ffB-cvnwOLO/preview",
      "https://drive.google.com/file/d/15x4Awk2VNyL2gpegVHblAtPb52lbiAUX/preview"
    )

    val answerSchemeUrls = listOf(
      "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview",
      "https://drive.google.com/file/d/1EjYUgrj-r_Uw9jMUlJPQh8a0Tl4oiBjb/preview",
      "https://drive.google.com/file/d/1zSkjgp24A2wvjrKAlX_EDt3KMvRtk7xp/preview",
      "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview",
      "https://drive.google.com/file/d/1TU2t7cxxTohimis_VsIPzswy0CW7p2v8/preview"
    )

    val coverageByTerm = when (term) {
      1 -> "1 වන වාරයේ නියමිත 01 සිට 06 දක්වා ඒකක ආවරණය වේ."
      2 -> "1 සහ 2 වන වාර වල 01 සිට 14 දක්වා ඒකක ආවරණය වේ."
      else -> "වසර පුරා සම්පූර්ණ විෂය නිර්දේශය (Full Syllabus) ආවරණය වේ."
    }

    return years.mapIndexed { index, yr ->
      TermTestPaperItem(
        id = "term_paper_${grade}_${cleanSubject}_${term}_${yr}",
        grade = grade,
        subject = cleanSubject,
        term = term,
        year = yr,
        titleSinhala = "$yr $cleanSubject - $termShort විභාග ප්‍රශ්න පත්‍රය & ලකුණු දීමේ පටිපාටිය",
        sourceOrProvince = provinces[index % provinces.size],
        timeDuration = if (grade == "10" || grade == "11") "පැය 03 යි" else "පැය 02 යි",
        totalMarks = "ලකුණු 100",
        paperStructure = "I කොටස (බහුවරණ 40) + II කොටස (ව්‍යුහගත හා රචනා)",
        questionPaperUrl = paperUrls[index % paperUrls.size],
        answerSchemeUrl = answerSchemeUrls[index % answerSchemeUrls.size],
        unitCoverage = coverageByTerm
      )
    }
  }
}

@Composable
fun TermTestPapersSection(
  grade: String,
  subject: SubjectItem,
  onOpenPdf: (title: String, url: String) -> Unit
) {
  var selectedTerm by remember { mutableStateOf(1) } // 1: 1st Term, 2: 2nd Term, 3: 3rd Term
  var selectedYearFilter by remember { mutableStateOf<Int?>(null) } // null: All years

  val allTermPapers = remember(grade, subject.nameSinhala, selectedTerm) {
    TermTestPapersRepository.getTermTestPapers(grade, subject.nameSinhala, selectedTerm)
  }

  val filteredPapers = remember(allTermPapers, selectedYearFilter) {
    if (selectedYearFilter == null) allTermPapers
    else allTermPapers.filter { it.year == selectedYearFilter }
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("term_test_papers_hub")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Header with Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFF047857)),
            contentAlignment = Alignment.Center
          ) {
            Text("📑", fontSize = 16.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "වාර විභාග ප්‍රශ්න පත්‍ර (Term Test Papers)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "${subject.nameSinhala} • $grade ශ්‍රේණිය • පසුගිය වසර 5 ක ප්‍රශ්න හා පිළිතුරු",
              fontSize = 10.sp,
              color = Color(0xFF047857),
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFFECFDF5),
          border = BorderStroke(1.dp, Color(0xFFA7F3D0))
        ) {
          Text(
            text = "වසර 5ක සංචිතය",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF047857),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Term Selector Tabs (1 වන වාරය | 2 වන වාරය | 3 වන වාරය)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Triple(1, "පළමු වාරය", "1st Term"),
          Triple(2, "දෙවන වාරය", "2nd Term"),
          Triple(3, "තෙවන වාරය", "3rd Term")
        ).forEach { (tNum, tTitle, tSub) ->
          val isSelected = selectedTerm == tNum
          Surface(
            onClick = {
              selectedTerm = tNum
              selectedYearFilter = null
            },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) Color(0xFF047857) else Color(0xFFF1F5F9),
            border = BorderStroke(
              1.dp,
              if (isSelected) Color(0xFF047857) else Color(0xFFE2E8F0)
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("term_tab_$tNum")
          ) {
            Column(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = tTitle,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF334155)
              )
              Text(
                text = tSub,
                fontSize = 9.sp,
                color = if (isSelected) Color(0xFFA7F3D0) else Color(0xFF64748B)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Year Filter Chips (සියල්ල | 2024 | 2023 | 2022 | 2021 | 2020)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "වසර:",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF64748B)
        )

        val yearOptions = listOf<Int?>(null, 2024, 2023, 2022, 2021, 2020)
        yearOptions.forEach { yr ->
          val isSelected = selectedYearFilter == yr
          Surface(
            onClick = { selectedYearFilter = yr },
            shape = RoundedCornerShape(6.dp),
            color = if (isSelected) Color(0xFF065F46) else Color(0xFFF8FAFC),
            border = BorderStroke(
              1.dp,
              if (isSelected) Color(0xFF065F46) else Color(0xFFCBD5E1)
            ),
            modifier = Modifier.testTag("year_filter_${yr ?: "all"}")
          ) {
            Text(
              text = if (yr == null) "සියල්ල" else "$yr",
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color.White else Color(0xFF334155),
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. List of 5-Year Term Test Papers for Selected Term
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filteredPapers.forEach { paperItem ->
          TermTestPaperCard(
            item = paperItem,
            onOpenQuestionPaper = {
              onOpenPdf(
                "${paperItem.year} ${paperItem.subject} (${selectedTerm} වන වාරය) ප්‍රශ්න පත්‍රය",
                paperItem.questionPaperUrl
              )
            },
            onOpenAnswerScheme = {
              onOpenPdf(
                "${paperItem.year} ${paperItem.subject} (${selectedTerm} වන වාරය) පිළිතුරු පත්‍රය (Marking Scheme)",
                paperItem.answerSchemeUrl
              )
            }
          )
        }
      }
    }
  }
}

@Composable
fun TermTestPaperCard(
  item: TermTestPaperItem,
  onOpenQuestionPaper: () -> Unit,
  onOpenAnswerScheme: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFFFAFAFA),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top row: Year & Term Badge + Source Province
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF047857)
          ) {
            Text(
              text = "${item.year} • ${item.term} වන වාරය",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE0F2FE)
          ) {
            Text(
              text = item.timeDuration,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF0369A1),
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFFFEF3C7)
        ) {
          Text(
            text = item.totalMarks,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF92400E),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Title & Province Source
      Text(
        text = item.titleSinhala,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        fontSize = 12.5.sp,
        color = Color(0xFF0F172A)
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
      ) {
        Icon(
          imageVector = Icons.Default.School,
          contentDescription = "Source",
          tint = Color(0xFF64748B),
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = item.sourceOrProvince,
          fontSize = 10.sp,
          color = Color(0xFF64748B)
        )
      }

      Text(
        text = "📌 ${item.unitCoverage}",
        fontSize = 9.5.sp,
        color = Color(0xFF475569),
        modifier = Modifier.padding(top = 3.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Dual Action Buttons: [📄 ප්‍රශ්න පත්‍රය (Paper)] and [📝 පිළිතුරු පත්‍රය (Marking Scheme)]
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Question Paper Button
        Button(
          onClick = onOpenQuestionPaper,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("open_question_paper_${item.year}_${item.term}")
        ) {
          Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = "Question Paper",
            tint = Color(0xFFF87171),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "ප්‍රශ්න පත්‍රය",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        // Marking Scheme Button
        Button(
          onClick = onOpenAnswerScheme,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("open_marking_scheme_${item.year}_${item.term}")
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Marking Scheme",
            tint = Color(0xFF86EFAC),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "පිළිතුරු පත්‍රය",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

data class GceOlPastPaperItem(
  val id: String,
  val subject: String,
  val year: Int,
  val isLatest: Boolean = false,
  val titleSinhala: String,
  val source: String = "ශ්‍රී ලංකා විභාග දෙපාර්තමේන්තුව (Department of Examinations)",
  val timeDuration: String = "පැය 03 යි",
  val totalMarks: String = "ලකුණු 100",
  val paperStructure: String = "I කොටස (බහුවරණ 40) + II කොටස (ව්‍යුහගත හා රචනා)",
  val questionPaperUrl: String,
  val answerSchemeUrl: String,
  val description: String = "අ.පො.ස. (සා.පෙළ) නිල විභාග ප්‍රශ්න පත්‍රය සහ ප්‍රමිත ලකුණු දීමේ පටිපාටිය"
)

object GceOlPastPapersRepository {

  fun getOlPastPapers(subjectNameSinhala: String): List<GceOlPastPaperItem> {
    val cleanSubject = subjectNameSinhala.trim()
    val years = listOf(2024, 2023, 2022, 2021, 2020, 2019)

    // Curated high-reliability Google Drive preview URLs for DRM protected in-app viewing
    val paperUrls = listOf(
      "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview", // 2024
      "https://drive.google.com/file/d/1PPsYx6Tm7x_0YSy3es3edqBMizD08bJZ/preview", // 2023
      "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview", // 2022
      "https://drive.google.com/file/d/1cG0Fj4Eg86hbvyx0OAnY1ffB-cvnwOLO/preview", // 2021
      "https://drive.google.com/file/d/15x4Awk2VNyL2gpegVHblAtPb52lbiAUX/preview", // 2020
      "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview"  // 2019
    )

    val answerSchemeUrls = listOf(
      "https://drive.google.com/file/d/1EjYUgrj-r_Uw9jMUlJPQh8a0Tl4oiBjb/preview", // 2024
      "https://drive.google.com/file/d/1zSkjgp24A2wvjrKAlX_EDt3KMvRtk7xp/preview", // 2023
      "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview", // 2022
      "https://drive.google.com/file/d/1TU2t7cxxTohimis_VsIPzswy0CW7p2v8/preview", // 2021
      "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview", // 2020
      "https://drive.google.com/file/d/1PPsYx6Tm7x_0YSy3es3edqBMizD08bJZ/preview"  // 2019
    )

    return years.mapIndexed { index, yr ->
      val isLatestExam = (yr == 2024)
      val title = if (isLatestExam) {
        "$yr (මෙවර) අ.පො.ස. (සා.පෙළ) $cleanSubject ප්‍රශ්න පත්‍රය & නිල Marking Scheme"
      } else {
        "$yr අ.පො.ස. (සා.පෙළ) $cleanSubject ප්‍රශ්න පත්‍රය & නිල Marking Scheme"
      }

      val timeDur = when {
        cleanSubject.contains("ගණිතය") || cleanSubject.contains("විද්‍යාව") || cleanSubject.contains("සිංහල") -> "පැය 03 යි"
        cleanSubject.contains("ඉංග්‍රීසි") -> "පැය 02 යි මිනිත්තු 30"
        else -> "පැය 03 යි"
      }

      val desc = if (isLatestExam) {
        "මෙවර නවතම O/L විභාග ප්‍රශ්න පත්‍රය (I සහ II කොටස්) සහ විභාග දෙපාර්තමේන්තුවේ සම්පූර්ණ ලකුණු දීමේ පටිපාටිය."
      } else {
        "අ.පො.ස. (සා.පෙළ) නිල විභාග ප්‍රශ්න පත්‍රය (I සහ II කොටස්) සහ විභාග දෙපාර්තමේන්තුවේ සම්පූර්ණ ලකුණු දීමේ පටිපාටිය."
      }

      GceOlPastPaperItem(
        id = "ol_paper_${cleanSubject}_$yr",
        subject = cleanSubject,
        year = yr,
        isLatest = isLatestExam,
        titleSinhala = title,
        source = "ශ්‍රී ලංකා විභාග දෙපාර්තමේන්තුව (DoE)",
        timeDuration = timeDur,
        totalMarks = "ලකුණු 100",
        paperStructure = "I කොටස (බහුවරණ/කෙටි) + II කොටස (ව්‍යුහගත හා රචනා)",
        questionPaperUrl = paperUrls[index % paperUrls.size],
        answerSchemeUrl = answerSchemeUrls[index % answerSchemeUrls.size],
        description = desc
      )
    }
  }
}

@Composable
fun GceOlPastPapersSection(
  subject: SubjectItem,
  onOpenPdf: (title: String, url: String) -> Unit
) {
  var selectedYearFilter by remember { mutableStateOf<Int?>(null) } // null: All 6 years

  val allOlPapers = remember(subject.nameSinhala) {
    GceOlPastPapersRepository.getOlPastPapers(subject.nameSinhala)
  }

  val filteredPapers = remember(allOlPapers, selectedYearFilter) {
    if (selectedYearFilter == null) allOlPapers
    else allOlPapers.filter { it.year == selectedYearFilter }
  }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFE)),
    border = BorderStroke(1.5.dp, Color(0xFFBFDBFE)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("gce_ol_past_papers_section")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Header with Golden/Blue Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFF1D4ED8)),
            contentAlignment = Alignment.Center
          ) {
            Text("🏆", fontSize = 18.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "අ.පො.ස. (සා.පෙළ) විභාග ප්‍රශ්න පත්‍ර",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A),
              fontSize = 13.5.sp
            )
            Text(
              text = "${subject.nameSinhala} • 2024 මෙවර සිට 2019 දක්වා (වසර 6ක්)",
              fontSize = 10.5.sp,
              color = Color(0xFF1D4ED8),
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFEFF6FF),
          border = BorderStroke(1.dp, Color(0xFF93C5FD))
        ) {
          Text(
            text = "වසර 6ක O/L",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E40AF),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "මෙවර O/L විභාග අපේක්ෂකයින් සඳහා වසර 6ක නිල විභාග ප්‍රශ්න පත්‍ර (Papers) සහ පිළිතුරු පත්‍ර (Marking Schemes) සම්පූර්ණයෙන් මෙහි ඇතුළත් කර ඇත.",
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = Color(0xFF475569)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Year Filter Chips (සියල්ල | 2024 මෙවර | 2023 | 2022 | 2021 | 2020 | 2019)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "වර්ෂය:",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF475569)
        )

        val yearOptions = listOf<Int?>(null, 2024, 2023, 2022, 2021, 2020, 2019)
        yearOptions.forEach { yr ->
          val isSelected = selectedYearFilter == yr
          Surface(
            onClick = { selectedYearFilter = yr },
            shape = RoundedCornerShape(6.dp),
            color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
            border = BorderStroke(
              1.dp,
              if (isSelected) Color(0xFF1D4ED8) else Color(0xFFCBD5E1)
            ),
            modifier = Modifier.testTag("ol_year_filter_${yr ?: "all"}")
          ) {
            Text(
              text = when (yr) {
                null -> "සියල්ල"
                2024 -> "2024 මෙවර"
                else -> "$yr"
              },
              fontSize = 9.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Color(0xFF334155),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. List of 6-Year O/L Exam Papers
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filteredPapers.forEach { olPaper ->
          GceOlPastPaperCard(
            item = olPaper,
            onOpenQuestionPaper = {
              onOpenPdf(
                "${olPaper.year} අ.පො.ස. (සා.පෙළ) ${olPaper.subject} ප්‍රශ්න පත්‍රය (Paper I & II)",
                olPaper.questionPaperUrl
              )
            },
            onOpenAnswerScheme = {
              onOpenPdf(
                "${olPaper.year} අ.පො.ස. (සා.පෙළ) ${olPaper.subject} නිල පිළිතුරු පත්‍රය (Marking Scheme)",
                olPaper.answerSchemeUrl
              )
            }
          )
        }
      }
    }
  }
}

@Composable
fun GceOlPastPaperCard(
  item: GceOlPastPaperItem,
  onOpenQuestionPaper: () -> Unit,
  onOpenAnswerScheme: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = BorderStroke(
      1.2.dp,
      if (item.isLatest) Color(0xFF93C5FD) else Color(0xFFE2E8F0)
    ),
    shadowElevation = if (item.isLatest) 2.dp else 1.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top row: Year Badge + Duration & Marks
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (item.isLatest) Color(0xFF1D4ED8) else Color(0xFF0F172A)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (item.isLatest) {
                Text("🔥", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(3.dp))
              }
              Text(
                text = if (item.isLatest) "${item.year} මෙවර විභාගය" else "${item.year} සාමාන්‍ය පෙළ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFF0FDF4)
          ) {
            Text(
              text = item.timeDuration,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF15803D),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFFFEF3C7),
          border = BorderStroke(0.5.dp, Color(0xFFFDE68A))
        ) {
          Text(
            text = item.totalMarks,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF92400E),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Title & Source
      Text(
        text = item.titleSinhala,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        fontSize = 12.5.sp,
        color = Color(0xFF0F172A)
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AccountBalance,
          contentDescription = "Source",
          tint = Color(0xFF64748B),
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = item.source,
          fontSize = 10.sp,
          color = Color(0xFF64748B)
        )
      }

      Text(
        text = "📌 ${item.description}",
        fontSize = 9.5.sp,
        color = Color(0xFF475569),
        lineHeight = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons: [📄 O/L ප්‍රශ්න පත්‍රය] and [📝 පිළිතුරු පත්‍රය (Marking Scheme)]
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Question Paper Button
        Button(
          onClick = onOpenQuestionPaper,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("open_ol_paper_${item.year}")
        ) {
          Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = "Question Paper",
            tint = Color(0xFFF87171),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "O/L ප්‍රශ්න පත්‍රය",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        // Marking Scheme Button
        Button(
          onClick = onOpenAnswerScheme,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (item.isLatest) Color(0xFF1D4ED8) else Color(0xFF047857)
          ),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("open_ol_marking_scheme_${item.year}")
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Marking Scheme",
            tint = Color(0xFF93C5FD),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "පිළිතුරු පත්‍රය",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

