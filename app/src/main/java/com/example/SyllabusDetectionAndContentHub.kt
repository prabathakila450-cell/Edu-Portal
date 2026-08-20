package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ==============================================================================
// SYLLABUS DETECTION & OFFICIAL LESSON SEQUENCE DATA MODEL
// ශ්‍රී ලංකා විෂය නිර්දේශයේ පාඩම් ස්වයංක්‍රීයව හඳුනා ගැනීමේ දත්ත ආකෘතිය
// ==============================================================================

data class TableRowData(
  val column1: String,
  val column2: String,
  val column3: String = ""
)

data class ComparisonTable(
  val title: String,
  val header1: String,
  val header2: String,
  val header3: String = "",
  val rows: List<TableRowData>
)

data class MemoryTrick(
  val title: String,
  val mnemonicSentence: String, // උදා: "කාලා නාන මගෙ අලුත් සින්දුව..."
  val explanation: String,
  val appliesTo: String
)

data class UnitQuestion(
  val questionNumber: Int,
  val questionText: String,
  val type: String, // "MCQ", "SHORT", "STRUCTURED"
  val options: List<String> = emptyList(),
  val correctAnswer: String,
  val markingScheme: String,
  val marksAllocated: Int
)

data class AttachedGoogleDrivePdf(
  val id: String,
  val title: String,
  val driveUrl: String,
  val uploadDate: String = "2026-08-16",
  val fileSize: String = "1.8 MB",
  val type: String = "NOTE" // "NOTE" or "PAPER"
)

data class SyllabusUnitItem(
  val id: String,
  val grade: String, // "06", "07", "08", "09", "10", "11"
  val subject: String, // "විද්‍යාව", "ගණිතය", "ඉතිහාසය", etc.
  val unitNumber: String, // "01 වන පාඩම", "02 වන පාඩම", etc.
  val unitTitleSinhala: String,
  val unitTitleEnglish: String,
  val term: String, // "1 වන වාරය", "2 වන වාරය", "3 වන වාරය"
  val summaryNotes: List<String>,
  val comparisonTables: List<ComparisonTable>,
  val memoryTricks: List<MemoryTrick>,
  val practiceQuestions: List<UnitQuestion>,
  val defaultDrivePdfUrl: String = "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview",
  val attachedDrivePdfs: MutableList<AttachedGoogleDrivePdf> = mutableListOf()
)

// Helper to convert any Google Drive URL into high-performance embeddable preview format
fun formatToGoogleDriveEmbedUrl(url: String): String {
  val cleanUrl = url.trim()
  if (cleanUrl.isBlank()) return "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview"
  
  // Format 1: drive.google.com/file/d/{ID}/view...
  val filePattern = Regex("""drive\.google\.com/file/d/([a-zA-Z0-9_-]+)""")
  val match1 = filePattern.find(cleanUrl)
  if (match1 != null) {
    val fileId = match1.groupValues[1]
    return "https://drive.google.com/file/d/$fileId/preview"
  }

  // Format 2: drive.google.com/open?id={ID} or uc?id={ID}
  val idPattern = Regex("""[?&]id=([a-zA-Z0-9_-]+)""")
  val match2 = idPattern.find(cleanUrl)
  if (match2 != null) {
    val fileId = match2.groupValues[1]
    return "https://drive.google.com/file/d/$fileId/preview"
  }

  // If already preview or other URL, return as is
  return if (cleanUrl.startsWith("http")) cleanUrl else "https://$cleanUrl"
}

// ==============================================================================
// CURRICULUM SYLLABUS REPOSITORY (Grades 06 - 11 Official NIE Sequence)
// ==============================================================================

object SyllabusRepository {

  val allSyllabusUnits: MutableList<SyllabusUnitItem> = mutableListOf(
    // --------------------------------------------------------------------------
    // GRADE 10 & 11 - ORIENTAL MUSIC (සංගීතය කෙටි සටහන් 1)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_11_music_u1",
      grade = "11",
      subject = "සංගීතය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය හා 11 ශ්‍රේණිය පෙරදිග සංගීතය කෙටි සටහන් 1",
      unitTitleEnglish = "Grade 10 & 11 Oriental Music Short Notes 1",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "රාග සංගීතය: බිලාවල්, කල්‍යාණ, ඛමාජ්, කාෆි, ආසාවරී, භෛරව, භෛරවි, භූපාලි, යමන් රාග පිළිබඳ මූලික හැඳින්වීම.",
        "තාල විස්තරය: ත්‍රිතාලය, ඛෙමට්ටා තාලය, දීප්චන්දී තාලය, දාද්රා තාලය සහ රූපාක් තාලය (මාත්‍ර, මාත්‍රා විභාග, තාළි, ඛාලි).",
        "ස්වර හා ශ්‍රැති: ශුද්ධ ස්වර 7, කෝමල ස්වර 4 (රෙ, ග, ධ, නි) සහ තීව්‍ර ස්වර 1 (ම). ශ්‍රැති 22 විභාජනය.",
        "ශ්‍රී ලංකාවේ දේශීය සංගීතය, ජන ගී (නෙලුම් කවි, පැල් කවි, කරත්ත කවි) සහ නූර්ති/නාදගම් ගී.",
        "වාද්‍ය භාණ්ඩ වර්ගීකරණය: තත, සුසිර, ඝන, අවනද්ධ භාණ්ඩ."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ප්‍රධාන රාග ලක්ෂණ සංසන්දනය",
          header1 = "රාගය (Raga)",
          header2 = "ථාටය / වාදී-සංවාදී",
          header3 = "විශේෂ ස්වර / ගායන වේලාව",
          rows = listOf(
            TableRowData("භූපාලි (Bhupali)", "කල්‍යාණ / ග - ධ", "ඖඩව-ඖඩව (ම, නි වර්ජිත) • රාත්‍රී 1 වන ප්‍රහරය"),
            TableRowData("යමන් (Yaman)", "කල්‍යාණ / ග - නි", "සම්පූර්ණ (තීව්‍ර මධ්‍යම) • රාත්‍රී 1 වන ප්‍රහරය"),
            TableRowData("ඛමාජ් (Khamaj)", "ඛමාජ් / ග - නි", "ශාඩව-සම්පූර්ණ (ආරෝහණයේ රෙ වර්ජිත, කෝමල නි) • රාත්‍රී 2 වන ප්‍රහරය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "භූපාලි රාගයේ ස්වර මතක තබා ගැනීම",
          mnemonicSentence = "ස රෙ ග ප ධ ස' (ම, නි නෑ!)",
          explanation = "භූපාලි රාගයේ ම සහ නි ස්වර නොයෙදෙන බැවින් එය ඖඩව-ඖඩව ජාතියට අයත් වේ.",
          appliesTo = "පෙරදිග සංගීතය - රාග අධ්‍යයනය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "භූපාලි රාගයේ වර්ජිත (නොයෙදෙන) ස්වර යුගලය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. රෙ, ධ", "2. ග, නි", "3. ම, නි", "4. රෙ, ප"),
          correctAnswer = "3. ම, නි",
          markingScheme = "භූපාලි රාගයේ ආරෝහණයේ සහ අවරෝහණයේ ම සහ නි ස්වර වර්ජිත වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1BYhGyyvqcVfQP7YYgWynzV_oZ0coLjbL/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "music_g10_11_pdf_1",
          title = "10 හා 11 ශ්‍රේණිය පෙරදිග සංගීතය කෙටි සටහන් 1 (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/16z-qVWM6nwPErhsYoT5L0WOL3-jsEoDv/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.4 MB",
          type = "NOTE"
        ),
        AttachedGoogleDrivePdf(
          id = "music_g10_11_pdf_2",
          title = "10 හා 11 ශ්‍රේණිය පෙරදිග සංගීතය කෙටි සටහන් සංග්‍රහය (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1BYhGyyvqcVfQP7YYgWynzV_oZ0coLjbL/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.8 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 06-11 - ENGLISH LANGUAGE & GRAMMAR (06-11 ශ්‍රේණි ඉංග්‍රීසි කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g06_11_english_u1",
      grade = "11",
      subject = "ඉංග්‍රීසි",
      unitNumber = "Grammar & Writing",
      unitTitleSinhala = "06-11 ශ්‍රේණි ඉංග්‍රීසි ව්‍යාකරණ, Tenses හා Writing කෙටි සටහන්",
      unitTitleEnglish = "Grades 06-11 English Grammar, Tenses, Prepositions & Essay Writing",
      term = "සියලු වාර",
      summaryNotes = listOf(
        "Parts of Speech: Nouns, Pronouns, Verbs, Adjectives, Adverbs, Prepositions, Conjunctions, Interjections.",
        "Tenses (කාල 12): Simple Present, Present Continuous, Present Perfect, Simple Past, Past Continuous, Past Perfect, Simple Future ආශ්‍රිත Active/Passive Voice නීති.",
        "Direct and Indirect Speech: ප්‍රකාශන වාක්‍ය, ප්‍රශ්නාර්ථ වාක්‍ය සහ ආඥා/ඉල්ලීම් වාක්‍ය අනියම් ප්‍රකාශනයට පරිවර්තනය කිරීමේ නීති.",
        "Prepositions & Conjunctions: in, on, at, by, with, although, despite, because of, in order to නිවැරදි භාවිතය.",
        "Writing & Composition: Formal/Informal Letters, Graphs/Pie-charts Description, Notices, Notes, and Articles ලියන ආකාරය සහ ලකුණු ලැබෙන ආකෘති (Format)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "Active Voice vs Passive Voice සාරාංශය",
          header1 = "Tense",
          header2 = "Active Voice Form",
          header3 = "Passive Voice Form (be + V3)",
          rows = listOf(
            TableRowData("Simple Present", "V1 (writes / write)", "am/is/are + written"),
            TableRowData("Present Continuous", "is/are + writing", "is/are + being + written"),
            TableRowData("Simple Past", "V2 (wrote)", "was/were + written"),
            TableRowData("Present Perfect", "has/have + written", "has/have + been + written"),
            TableRowData("Simple Future", "will + write", "will + be + written")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "Prepositions of Time (AT, ON, IN) මතක තබා ගැනීම",
          mnemonicSentence = "AT for Time (වේලාව) • ON for Days (දින) • IN for Months/Years (මාස/වර්ෂ)!",
          explanation = "at 5.00 PM | on Monday, on 15th August | in July, in 2026.",
          appliesTo = "Prepositions of Time"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "Fill in the blank with the correct preposition: 'The national examination will commence _____ 8.30 a.m. _____ Monday.'",
          type = "MCQ",
          options = listOf("1. in / at", "2. at / on", "3. on / in", "4. by / at"),
          correctAnswer = "2. at / on",
          markingScheme = "Time of clock takes 'at' (at 8.30 a.m.) and days of the week take 'on' (on Monday). (2 Marks)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/155eu00n0_0IdrKc0wiWDqcwkqLa08ndI/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "eng_g06_11_pdf_1",
          title = "06-11 ශ්‍රේණි ඉංග්‍රීසි පූර්ණ කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/155eu00n0_0IdrKc0wiWDqcwkqLa08ndI/preview",
          uploadDate = "2026-08-18",
          fileSize = "4.2 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10 & 11 - HISTORY TABLES (10 හා 11 ශ්‍රේණි ඉතිහාසය වගු කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_11_hist_tables_u1",
      grade = "11",
      subject = "ඉතිහාසය",
      unitNumber = "වගු සටහන්",
      unitTitleSinhala = "10 හා 11 ශ්‍රේණි ඉතිහාසය පූර්ණ වගු කෙටි සටහන්",
      unitTitleEnglish = "Grades 10 & 11 History Comprehensive Table Notes",
      term = "සියලු වාර",
      summaryNotes = listOf(
        "ඓතිහාසික මූලාශ්‍ර: සාහිත්‍ය මූලාශ්‍ර (දීපවංශය, මහාවංශය, පූජාවලිය) සහ පුරාවිද්‍යාත්මක මූලාශ්‍ර (සෙල්ලිපි, කාසි, නටබුන්).",
        "ලංකා රාජධානි අනුපිළිවෙල හා පාලකයින්: අනුරාධපුර, පොළොන්නරුව, දඹදෙණිය, යාපහුව, කුරුණෑගල, ගම්පොළ, කෝට්ටේ, සීතාවක, මහනුවර යුගයන්හි ප්‍රධාන රජවරුන් සහ ඔවුන්ගේ සේවාවන්.",
        "යුරෝපීය ආක්‍රමණ සහ ප්‍රතිසංස්කරණ: පෘතුගීසි, ලන්දේසි සහ ඉංග්‍රීසි පාලන සමයන් (කෝල්බෲක්, ක්‍රෲ-මැකලම්, ඩොනමෝර්, සෝල්බරි ආණ්ඩුක්‍රම).",
        "ලෝක ඉතිහාස සංධිස්ථාන: කාර්මික විප්ලවය, පුනරුදය, ප්‍රංශ විප්ලවය සහ පළමු හා දෙවන ලෝක යුද්ධ පිළිබඳ සාරාංශ වගු."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ශ්‍රී ලංකාවේ බ්‍රිතාන්‍ය ආණ්ඩුක්‍රම ප්‍රතිසංස්කරණ සංසන්දනය",
          header1 = "ප්‍රතිසංස්කරණය (Reform)",
          header2 = "වර්ෂය (Year)",
          header3 = "ප්‍රධාන ලක්ෂණ / වැදගත්කම",
          rows = listOf(
            TableRowData("කෝල්බෲක්-කැමරන්", "1833", "රාජකාරි ක්‍රමය අහෝසි කිරීම, පළාත් 5 කට බෙදීම, විධායක හා ව්‍යවස්ථාදායක සභා පිහිටුවීම"),
            TableRowData("ක්‍රෲ-මැකලම්", "1910", "උගත් ලාංකිකයන්ට ඡන්ද බලය ලබාදීම (සීමිත නියෝජනය)"),
            TableRowData("ඩොනමෝර්", "1931", "සර්වජන ඡන්ද බලය ලබාදීම, විධායක කාරක සභා ක්‍රමය, රාජ්‍ය මන්ත්‍රණ සභාව"),
            TableRowData("සෝල්බරි", "1947", "පාර්ලිමේන්තු ආණ්ඩුක්‍රමය (නියෝජිත මන්ත්‍රී මණ්ඩලය හා සෙනෙට් සභාව)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ආණ්ඩුක්‍රම ප්‍රතිසංස්කරණ අනුපිළිවෙල මතක තබා ගැනීම",
          mnemonicSentence = "කෝල් කළ ක්‍රෲ ඩොනමෝර් සෝල්බරිට!",
          explanation = "කෝල්බෲක් (1833) → ක්‍රෲ-මැකලම් (1910) → ඩොනමෝර් (1931) → සෝල්බරි (1947).",
          appliesTo = "බ්‍රිතාන්‍ය ආණ්ඩුක්‍රම ප්‍රතිසංස්කරණ"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ශ්‍රී ලංකාවේ වයස අවුරුදු 21 ට වැඩි සියලුම පුරවැසියන්ට සර්වජන ඡන්ද බලය හිමි වූයේ කුමන ආණ්ඩුක්‍රම ප්‍රතිසංස්කරණය යටතේද?",
          type = "MCQ",
          options = listOf("1. කෝල්බෲක් ප්‍රතිසංස්කරණය", "2. ක්‍රෲ-මැකලම් ප්‍රතිසංස්කරණය", "3. ඩොනමෝර් ප්‍රතිසංස්කරණය", "4. සෝල්බරි ප්‍රතිසංස්කරණය"),
          correctAnswer = "3. ඩොනමෝර් ප්‍රතිසංස්කරණය",
          markingScheme = "1931 ඩොනමෝර් ආණ්ඩුක්‍රම ප්‍රතිසංස්කරණය මඟින් ආසියාවේ ප්‍රථම වරට ශ්‍රී ලංකාවට සර්වජන ඡන්ද බලය හිමි විය. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/13ctYgSQ0jefoMGg3cpJg2t74h0jIZyx7/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "hist_tables_pdf_1",
          title = "10 හා 11 ශ්‍රේණිය ඉතිහාසය වගු කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/13ctYgSQ0jefoMGg3cpJg2t74h0jIZyx7/preview",
          uploadDate = "2026-08-18",
          fileSize = "3.8 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10 - HISTORY (10 ශ්‍රේණිය ඉතිහාසය කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_hist_u1",
      grade = "10",
      subject = "ඉතිහාසය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය ඉතිහාසය පූර්ණ කෙටි සටහන් හා මූලාශ්‍ර",
      unitTitleEnglish = "Grade 10 History Comprehensive Short Notes & Sources",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ඉතිහාසය හැදෑරීමේ මූලාශ්‍ර: සාහිත්‍ය මූලාශ්‍ර (දීපවංශය, මහාවංශය, රාජාවලිය) සහ පුරාවිද්‍යා මූලාශ්‍ර (සෙල්ලිපි, කාසි, නටබුන්).",
        "ශ්‍රී ලංකාවේ ප්‍රාග් ඓතිහාසික හා පූර්ව ඓතිහාසික යුගය: පාහියංගල, බටදොඹලෙන, ඉබ්බන්කටුව සුසාන භූමිය සහ බලංගොඩ මානවයා.",
        "අනුරාධපුර රාජධානියේ ආරම්භය හා විකාශනය: පණ්ඩුකාභය, දේවානම්පියතිස්ස, දුටුගැමුණු, වළගම්බා සහ ධාතුසේන රජවරුන්ගේ දේශපාලන හා ආගමික මෙහෙවර.",
        "පුරාණ වාරි ශිෂ්ටාචාරය සහ කෘෂිකර්මාන්තය: ඇළ මාර්ග, වැව්, බිසෝකොටුව සහ රළපනාව තාක්ෂණය.",
        "ලෝක ඉතිහාසය: මුල්කාලීන නදී නිම්න ශිෂ්ටාචාර (මෙයසපොතේමියාව, මිසරය, ඉන්දු නිම්නය, හොවැංහෝ)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ඉතිහාස මූලාශ්‍ර වර්ගීකරණය සංසන්දනය",
          header1 = "මූලාශ්‍ර වර්ගය",
          header2 = "උදාහරණ (Examples)",
          header3 = "විශ්වසනීයත්වය / විශේෂත්වය",
          rows = listOf(
            TableRowData("සාහිත්‍ය මූලාශ්‍ර (දේශීය)", "දීපවංශය, මහාවංශය, ථූපවංශය, පූජාවලිය", "කතුවරයාගේ දෘෂ්ටිකෝණය බලපෑ හැක"),
            TableRowData("සාහිත්‍ය මූලාශ්‍ර (විදේශීය)", "පාහියන්, ඉබන් බතුතා, ටොලමිගේ සටහන්", "විදේශීය නිරීක්ෂකයන්ගේ වාර්තා"),
            TableRowData("පුරාවිද්‍යාත්මක මූලාශ්‍ර", "සෙල්ලිපි (බ්‍රාහ්මී), කාසි, කැටයම්, නටබුන්", "කාල නිර්ණය නිවැරදිව කළ හැකි ප්‍රාථමික මූලාශ්‍ර")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ඓතිහාසික වැවේ ප්‍රධාන කොටස් 4 මතක තබා ගැනීම",
          mnemonicSentence = "වැව් බැම්මෙන් වතුර රඳවා - බිසෝකොටුවෙන් පීඩනය පාලනය කර - සොරොව්වෙන් බෙදා - පිටවානෙන් පිටකරයි!",
          explanation = "වැව් බැම්ම, බිසෝකොටුව (පීඩන පාලනය), සොරොව්ව (ජලය නිකුත් කිරීම), පිටවාන (අතිරික්ත ජලය පිටකිරීම).",
          appliesTo = "පුරාණ වාරි තාක්ෂණය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "පුරාණ වාරි ඉංජිනේරු විද්‍යාවේදී ගැඹුරු වැව්වල අධික ජල පීඩනය පාලනය කිරීම සඳහා යොදාගත් විශිෂ්ටතම සිංහල නිර්මාණය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. පිටවාන", "2. බිසෝකොටුව", "3. රළපනාව", "4. ඇළහැර ඇළ"),
          correctAnswer = "2. බිසෝකොටුව",
          markingScheme = "බිසෝකොටුව යනු ගැඹුරු වැව්වල ජල පීඩනය පාලනය කර සොරොව්ව ආරක්ෂා කරමින් ජලය පිටකිරීමට නිර්මාණය කළ විශිෂ්ට උපාංගයකි. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1Ry6utaFim_tZl8OkTG5hoD6oB4RB8Uxl/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "hist_g10_pdf_1",
          title = "10 ශ්‍රේණිය ඉතිහාසය කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1Ry6utaFim_tZl8OkTG5hoD6oB4RB8Uxl/preview",
          uploadDate = "2026-08-18",
          fileSize = "3.6 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 06-11 - HISTORY MAPS (06/07/08/09/10/11 ශ්‍රේණි ඉතිහාසය සිතියම් ලකුණු කිරීම)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_hist_maps_u1",
      grade = "11",
      subject = "ඉතිහාසය",
      unitNumber = "සිතියම්",
      unitTitleSinhala = "06-11 ශ්‍රේණි ඉතිහාසය ලංකා හා ලෝක සිතියම් ලකුණු කිරීම",
      unitTitleEnglish = "Grades 06-11 History Sri Lanka & World Map Marking Guide",
      term = "සියලු වාර",
      summaryNotes = listOf(
        "ශ්‍රී ලංකා සිතියම: ප්‍රධාන ඓතිහාසික රාජධානි (අනුරාධපුරය, පොළොන්නරුව, දඹදෙණිය, යාපහුව, කුරුණෑගල, ගම්පොළ, කෝට්ටේ, සීතාවක, මහනුවර).",
        "ප්‍රධාන වාරිමාර්ග හා ජලාශ: පරාක්‍රම සමුද්‍රය, මින්නේරිය, කලා වැව, කාන්තලේ, නුවර වැව, තිසා වැව, යෝධ ඇළ සහ ඇළහැර ඇළ.",
        "ඓතිහාසික වරායන් හා වෙළඳ මධ්‍යස්ථාන: මාතොට (මහාතිත්ථ), ගෝකණ්ණ (ත්‍රිකුණාමලය), ජම්බුකෝලපට්ටන (දඹකොළපටුන), ගාලු වරාය, මන්නාරම.",
        "පුරාවිද්‍යාත්මක හා ආගමික ස්ථාන: සීගිරිය, මිහින්තලය, රංගිරි දඹුල්ල, දිඹුලාගල, තන්තිරිමලේ, මුතියංගනය, කතරගම, නගදීපය.",
        "ලෝක සිතියම (Grade 10/11 O/L): පැරණි ශිෂ්ටාචාර මධ්‍යස්ථාන (මෙයසපොතේමියාව, මිසරය, ඉන්දු නිම්නය, චීන ශිෂ්ටාචාරය), සේද මාවත සහ ප්‍රධාන සමුද්‍ර සන්ධි."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ප්‍රධාන ඓතිහාසික වාරි කර්මාන්ත හා නිර්මාතෘ රජවරු",
          header1 = "වාරි කර්මාන්තය (Reservoir/Canal)",
          header2 = "නිර්මාණය කළ රජතුමා (King)",
          header3 = "පිහිටි ප්‍රදේශය / වැදගත්කම",
          rows = listOf(
            TableRowData("මින්නේරිය වැව / ඇළහැර ඇළ", "මහසෙන් රජතුමා", "පොළොන්නරුව / රජරට ගොවිතැන"),
            TableRowData("කලා වැව / ජය ගඟ (යෝධ ඇළ)", "ධාතුසේන රජතුමා", "අනුරාධපුරයට ජලය සැපයීම (සැතපුමකට අඟල් 6 ක බැස්ම)"),
            TableRowData("පරාක්‍රම සමුද්‍රය", "මහා පරාක්‍රමබාහු රජතුමා", "පොළොන්නරුව (තෝපා, දුඹුටුලු, එරබදු වැව් එක්කර)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ප්‍රධාන වරායන් 3 සිතියමේ පිහිටීම මතක තබා ගැනීම",
          mnemonicSentence = "උතුරට දඹකොළ - වයඹට මාතොට - නැගෙනහිරට ගෝකණ්ණ!",
          explanation = "දඹකොළපටුන = උතුරු අර්ධද්වීපය, මාතොට = මන්නාරම/වයඹ වෙරළ, ගෝකණ්ණ = ත්‍රිකුණාමලය/නැගෙනහිර.",
          appliesTo = "ඉතිහාස සිතියම් ලකුණු කිරීම"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "අනුරාධපුර යුගයේ ප්‍රධානතම ජාත්‍යන්තර වෙළඳ වරාය වූයේ කුමක්ද?",
          type = "MCQ",
          options = listOf("1. ගෝකණ්ණ වරාය", "2. මහාතිත්ථ (මාතොට) වරාය", "3. ජම්බුකෝලපට්ටන", "4. ගාලු වරාය"),
          correctAnswer = "2. මහාතිත්ථ (මාතොට) වරාය",
          markingScheme = "මහාතිත්ථ (වර්තමාන මන්නාරම මාතොට) අනුරාධපුර යුගයේ ප්‍රධාන ජාත්‍යන්තර වෙළඳ වරාය විය. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1BVguuBjT1_iQVO296Zn4Dek2AOahBFsp/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "hist_maps_pdf_1",
          title = "06/07/08/09/10/11 ශ්‍රේණි ඉතිහාසය සිතියම් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1BVguuBjT1_iQVO296Zn4Dek2AOahBFsp/preview",
          uploadDate = "2026-08-18",
          fileSize = "4.5 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10/11 - SINHALA LITERATURE (10/11 ශ්‍රේණි සිංහල සාහිත්‍යය කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_sin_lit_u1",
      grade = "11",
      subject = "සිංහල",
      unitNumber = "සාහිත්‍යය",
      unitTitleSinhala = "10/11 ශ්‍රේණි සිංහල සාහිත්‍යය පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grades 10 & 11 Sinhala Literature Comprehensive Notes",
      term = "සියලු වාර",
      summaryNotes = listOf(
        "පද්‍ය සාහිත්‍යය: සම්භාව්‍ය කාව්‍ය (කව්සිළුමිණ, ගුත්තිලය, කාව්‍යශේඛරය, සැලලිහිණි සන්දේශය), ජනකවි සහ නූතන නිසඳැස් කාව්‍ය විචාර.",
        "ගද්‍ය සාහිත්‍යය: අමාවතුර, බුත්සරණ, පූජාවලිය, සද්ධර්මරත්නාවලිය ආශ්‍රිත භාෂා රටා, ආඛ්‍යාන ශෛලිය සහ චරිත නිරූපණය.",
        "නාට්‍ය කලාව සහ කෙටිකතා: සාහිත්‍ය නිර්මාණවල ව්‍යංගාර්ථ, උපමා, රූපක, ධ්වනි රසය සහ සමාජ විවරණය.",
        "සාහිත්‍ය විචාර ක්‍රමවේදය: ප්‍රශ්නවලට පිළිතුරු ලිවීමේදී කාව්‍ය/ගද්‍ය පාඨ උපුටා දැක්වීම සහ තාර්කිකව කරුණු ගොඩනැගීම."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ගුරුළුගෝමී (අමාවතුර) vs ධර්මසේන හිමි (සද්ධර්මරත්නාවලිය) ශෛලිය",
          header1 = "ලක්ෂණය",
          header2 = "අමාවතුර (ගුරුළුගෝමී)",
          header3 = "සද්ධර්මරත්නාවලිය (ධර්මසේන හිමි)",
          rows = listOf(
            TableRowData("භාෂා විලාශය", "පෙරදිග ශුද්ධ සිංහල, ගාම්භීර හා සංක්ෂිප්ත", "ගැමි ව්‍යවහාරය, උපමා උපමේය බහුල රසවත් බස"),
            TableRowData("අරමුණ", "බුදුරජාණන් වහන්සේගේ පුරිසදම්මසාරථී ගුණය විදහා දැක්වීම", "ගිහි සමාජයට ධර්මය අවබෝධ කරවීම සහ උපදෙස් දීම")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ගද්‍ය ග්‍රන්ථ හා කතුවරුන් මතක තබා ගැනීම",
          mnemonicSentence = "ගුරුගේ අමාවතුරෙන් - විදුහලේ බුත්සරණින් - මයූරපාද පූජාවෙන් - ධර්මසේන රත්නාවලියෙන්!",
          explanation = "ගුරුළුගෝමී = අමාවතුර, විද්‍යාචක්‍රවර්තී = බුත්සරණ, මයුරපාද පරිවේණාධිපති = පූජාවලිය, ධර්මසේන හිමි = සද්ධර්මරත්නාවලිය.",
          appliesTo = "සිංහල සාහිත්‍යය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "අමාවතුර ග්‍රන්ථය රචනා කර ඇත්තේ බුදුරදුන්ගේ කුමන බුදු ගුණය විස්තර කිරීම සඳහාද?",
          type = "MCQ",
          options = listOf("1. අරහං ගුණය", "2. පුරිසදම්මසාරථී ගුණය", "3. විජ්ජාචරණසම්පන්න ගුණය", "4. සුගත ගුණය"),
          correctAnswer = "2. පුරිසදම්මසාරථී ගුණය",
          markingScheme = "ගුරුළුගෝමී පඬිවරයා විසින් අමාවතුර රචනා කරන ලද්දේ බුදුරජාණන් වහන්සේගේ 'පුරිසදම්මසාරථී' ගුණය විස්තර කිරීමටයි. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/17jFpMgfgojJgdAT0K4Ja9dfDocoBAnsl/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "sin_lit_pdf_1",
          title = "10/11 ශ්‍රේණි සිංහල සාහිත්‍යය කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/17jFpMgfgojJgdAT0K4Ja9dfDocoBAnsl/preview",
          uploadDate = "2026-08-18",
          fileSize = "3.9 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 06-11 - SINHALA GRAMMAR (06-11 ශ්‍රේණි සිංහල ව්‍යාකරණ කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_sin_grammar_u1",
      grade = "11",
      subject = "සිංහල",
      unitNumber = "ව්‍යාකරණ",
      unitTitleSinhala = "06-11 ශ්‍රේණි සිංහල ව්‍යාකරණ පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grades 06-11 Sinhala Grammar Comprehensive Notes",
      term = "සියලු වාර",
      summaryNotes = listOf(
        "සිංහල හෝඩිය: ස්වර, ව්‍යංජන, ශුද්ධ සිංහල හෝඩිය (අක්ෂර 32) සහ මිශ්‍ර සිංහල හෝඩිය (අක්ෂර 54 / 60).",
        "නාම පද හා ක්‍රියා පද: නාම පද ප්‍රභේද (ද්‍රව්‍ය, ගුණ, ක්‍රියා, සමූහ, ආවෘති), ආඛ්‍යාතය සහ උක්ත-ආඛ්‍යාත පද සම්බන්ධය.",
        "විභක්ති: ප්‍රථමා, කර්ම, කර්තෘ, කරණ, සම්ප්‍රදාන, අවධි, සම්බන්ධ, ආධාර සහ ආලපන විභක්ති.",
        "සන්ධි හා සමාස: ස්වර සන්ධි, ව්‍යංජන සන්ධි, පූර්ව-පර රූප සන්ධි සහ තත්පුරුෂ, කර්මධාරය, ද්වන්ද, ද්විගු සමාස.",
        "ණ-න, ළ-ල, ශ-ෂ-ස අක්ෂර වින්‍යාස නීති සහ විරාම ලක්ෂණ නිවැරදිව භාවිතය."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ශුද්ධ සිංහල හෝඩිය vs මිශ්‍ර සිංහල හෝඩිය",
          header1 = "ලක්ෂණය",
          header2 = "ශුද්ධ සිංහල හෝඩිය",
          header3 = "මිශ්‍ර සිංහල හෝඩිය",
          rows = listOf(
            TableRowData("අක්ෂර ගණන", "අක්ෂර 32 කි (ස්වර 12, ව්‍යංජන 20)", "අක්ෂර 54 හෝ 60 (මහප්‍රාණ, මූර්ධජ ආදිය සහිතයි)"),
            TableRowData("භාවිතය", "හෙළ බසට පමණක් ගැළපෙන පද ලිවීමට", "පාලි, සංස්කෘත සහ විදේශීය තත්සම පද ලිවීමට")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "උක්ත ආඛ්‍යාත පද සම්බන්ධය මතක තබා ගැනීම",
          mnemonicSentence = "උක්තය ඒකවචන නම් ක්‍රියාවත් ඒකවචන • උක්තය බහුවචන නම් ක්‍රියාවත් බහුවචන!",
          explanation = "පුරුෂය (ප්‍රථම, මධ්‍යම, උත්තම) සහ වචනය (ඒක, බහු) අනුව ආඛ්‍යාතය වෙනස් වේ.",
          appliesTo = "සිංහල ව්‍යාකරණ"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "මිශ්‍ර සිංහල හෝඩියේ සම්මත අක්ෂර ගණන කොපමණද?",
          type = "MCQ",
          options = listOf("1. 32 කි", "2. 54 කි", "3. 20 කි", "4. 12 කි"),
          correctAnswer = "2. 54 කි",
          markingScheme = "සම්මත මිශ්‍ර සිංහල හෝඩියේ අක්ෂර 54 ක් අඩංගු වේ. (නූතන හෝඩියේ ඇ, ඈ, ඓ, ඖ ඇතුළත්ව 60 කි). (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1XB8up4GLB9mvcavvtVsbOaAZAO1027tc/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "sin_grammar_pdf_1",
          title = "06/07/08/09/10/11 ශ්‍රේණි සිංහල ව්‍යාකරණ කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1XB8up4GLB9mvcavvtVsbOaAZAO1027tc/preview",
          uploadDate = "2026-08-18",
          fileSize = "4.1 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 11 - GEOGRAPHY (11 වසර භූගෝල විද්‍යාව කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_geo_u1",
      grade = "11",
      subject = "භූගෝල විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "11 ශ්‍රේණිය භූගෝල විද්‍යාව පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grade 11 Geography Comprehensive Short Notes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ලෝකයේ භෞතික ලක්ෂණ හා භූ තැටි චලන: භූ තැටි මායිම් (අපසාරී, අභිසාරී, පරිවර්තන), භූමිකම්පා සහ ගිනි කඳු ව්‍යාප්තිය.",
        "ලෝක දේශගුණ කලාප: සමක, නිවර්තන, සෞම්‍ය සහ ධ්‍රැවාසන්න කලාපවල ලක්ෂණ හා වෘක්ෂලතා.",
        "සිතියම් කියවීම සහ පරිමාණ: 1:50,000 භූලක්ෂණ සිතියම්, සමෝච්ච රේඛා ආශ්‍රයෙන් භූරූප හඳුනාගැනීම (කඳු වැටි, බෑවුම්, සානු, නිම්න).",
        "මානව ක්‍රියාකාරකම් හා පරිසර අර්බුද: ගෝලීය උණුසුම ඉහළ යාම, කාන්තාරීකරණය, වන විනාශය සහ තිරසාර සංවර්ධන සංකල්පය.",
        "ලෝක ජනාවාස සහ සංවර්ධන ප්‍රවණතා: නාගරීකරණය, සංක්‍රමණ සහ ජනගහන වර්ධනය."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ප්‍රධාන භූ තැටි මායිම් 3 සංසන්දනය",
          header1 = "මායිම් වර්ගය (Boundary)",
          header2 = "චලන ස්වභාවය (Movement)",
          header3 = "නිර්මාණය වන භූරූප / උදාහරණ",
          rows = listOf(
            TableRowData("අපසාරී (Divergent)", "තැටි දෙකක් එකිනෙකින් ඈත්වීම", "මැද අත්ලාන්තික් සාගර වැටිය, පැලුම් නිම්න"),
            TableRowData("අභිසාරී (Convergent)", "තැටි දෙකක් එකිනෙක ගැටීම", "හිමාලය වැනි නැමි කඳුවැටි, අගාධ"),
            TableRowData("පරිවර්තන (Transform)", "තැටි දෙකක් එකිනෙක පිරිමදිමින් ලිස්සා යාම", "සැන් ඇන්ඩ්‍රියාස් විභේදය (භූමිකම්පා බහුලයි)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "භූ තැටි මායිම් 3 මතක තබා ගැනීම",
          mnemonicSentence = "ඈත් වුණොත් අපසාරී • හැප්පුණොත් අභිසාරී • ලිස්සුවොත් පරිවර්තන!",
          explanation = "අපසාරී (ඈත්වීම) | අභිසාරී (හැප්පීම/එකතු වීම) | පරිවර්තන (ලිස්සා යාම).",
          appliesTo = "භූ තැටි චලන"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "හිමාලය කඳුවැටිය නිර්මාණය වී ඇත්තේ කුමන ආකාරයේ භූ තැටි මායිමක් ආශ්‍රිතවද?",
          type = "MCQ",
          options = listOf("1. අපසාරී මායිමක", "2. අභිසාරී මායිමක", "3. පරිවර්තන මායිමක", "4. නිෂ්ක්‍රීය මායිමක"),
          correctAnswer = "2. අභිසාරී මායිමක",
          markingScheme = "ඉන්දු-ඕස්ට්‍රේලියානු තැටිය සහ යුරේසියානු තැටිය එකිනෙක ගැටෙන (අභිසාරී) මායිම ආශ්‍රිතව හිමාලය කඳුවැටිය නිර්මාණය වී ඇත. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1dfKR5Cb8ZAE07E_3ddeYc1EDfXWOALHO/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "geo_g11_pdf_1",
          title = "11 ශ්‍රේණිය භූගෝල විද්‍යාව කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1dfKR5Cb8ZAE07E_3ddeYc1EDfXWOALHO/preview",
          uploadDate = "2026-08-17",
          fileSize = "3.2 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10 - GEOGRAPHY (10 වසර භූගෝල විද්‍යාව කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_geo_u1",
      grade = "10",
      subject = "භූගෝල විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය භූගෝල විද්‍යාව පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grade 10 Geography Comprehensive Short Notes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ශ්‍රී ලංකාවේ භෞතික පිහිටීම: අක්ෂාංශ හා දේශාංශ පිහිටීම, දූපත් ස්වභාවය සහ උපායමාර්ගික වැදගත්කම.",
        "ශ්‍රී ලංකාවේ භූරූප හා උන්නතාංශ කලාප: වෙරළබඩ තැන්න, අභ්‍යන්තර තැනිබිම, මධ්‍යම කඳුකරය (ප්‍රධාන කඳු මුදුන් සහ සානුවල ලක්ෂණ).",
        "දේශගුණය සහ කාලගුණය: නිරිතදිග මෝසම, ඊසානදිග මෝසම, අන්තර් මෝසම් සුළං සහ සංවහන වැසි.",
        "ශ්‍රී ලංකාවේ ප්‍රධාන ගංගා පද්ධතිය සහ ජලාපවහන රටාව: මධ්‍යම කඳුකරයෙන් ආරම්භ වී අරීය රටාවකට ගලා යාම (මහවැලි, කැලණි, කළු, වලවේ).",
        "ස්වභාවික සම්පත් සහ ජනගහන ව්‍යාප්තිය: ඛනිජ, පාංශු වර්ග, වනාන්තර සහ කෘෂිකාර්මික භාවිතය."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ප්‍රධාන මෝසම් සුළං 2 සංසන්දනය",
          header1 = "මෝසම් සුළඟ (Monsoon)",
          header2 = "කාලසීමාව (Period)",
          header3 = "වැසි ලැබෙන ප්‍රදේශ",
          rows = listOf(
            TableRowData("නිරිතදිග මෝසම (SW)", "මැයි සිට සැප්තැම්බර් දක්වා", "තෙත් කලාපය, බස්නාහිර සහ නිරිතදිග බෑවුම්"),
            TableRowData("ඊසානදිග මෝසම (NE)", "දෙසැම්බර් සිට පෙබරවාරි දක්වා", "වියළි කලාපය, උතුරු සහ නැගෙනහිර ප්‍රදේශ")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "මෝසම් සුළං කාල සීමා මතක තබා ගැනීම",
          mnemonicSentence = "නිරිතට මැයි-සැප් • ඊසානට දෙසැ-පෙබ!",
          explanation = "නිරිතදිග මෝසම (මැයි - සැප්තැම්බර්) | ඊසානදිග මෝසම (දෙසැම්බර් - පෙබරවාරි).",
          appliesTo = "ශ්‍රී ලංකාවේ දේශගුණය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ශ්‍රී ලංකාවේ දිගම ගංගාව කුමක්ද? එය ආරම්භ වන ස්ථානය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. කළු ගඟ (ශ්‍රී පාද අඩවිය)", "2. මහවැලි ගඟ (ශ්‍රී පාද කඳුවැටිය / හෝර්ටන් තැන්න)", "3. කැලණි ගඟ (කැළණිය)", "4. වලවේ ගඟ (ඇඩම්ස් පීක්)"),
          correctAnswer = "2. මහවැලි ගඟ (ශ්‍රී පාද කඳුවැටිය / හෝර්ටන් තැන්න)",
          markingScheme = "මහවැලි ගඟ ශ්‍රී ලංකාවේ දිගම ගංගාව වන අතර එහි දිග කි.මී. 335 කි. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1uKVJN3GsKephOV9In73EBW7R1bSSe47Z/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "geo_g10_pdf_1",
          title = "10 ශ්‍රේණිය භූගෝල විද්‍යාව කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1uKVJN3GsKephOV9In73EBW7R1bSSe47Z/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.9 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10 - BUDDHISM (10 වසර බුද්ධ ධර්මය කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_bud_u1",
      grade = "10",
      subject = "බුද්ධ ධර්මය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය බුද්ධ ධර්මය පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grade 10 Buddhism Comprehensive Short Notes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "සම්බුද්ධ චරිතය හා බුද්ධ ඥාන: සර්වඥතා ඥානය, ආසවානක්ඛය ඥානය, දසබලධාරී ඥාන.",
        "චතුරාර්ය සත්‍යය: දුක්ඛ, සමුදය, නිරෝධ, මාර්ග (ආර්ය අෂ්ටාංගික මාර්ගය).",
        "බෞද්ධ සදාචාරය සහ ගිහි සමාජ ජීවිතය: සිඟාලෝවාද සූත්‍රය, මංගල සූත්‍රය, පරාභව සූත්‍රය සහ ව්‍යග්ඝපජ්ජ සූත්‍රය.",
        "ශාසන ඉතිහාසය: තෙවන ධර්ම සංගායනාව, මිහිඳු මහ රහතන් වහන්සේගේ ලංකාගමනය, ශ්‍රී මහා බෝධීන් වහන්සේ වැඩමවීම.",
        "ප්‍රධාන බෞද්ධ සිද්ධස්ථාන, සෑගිරි හා අනුරාධපුර පූජනීය නටබුන් සහ සංස්කෘතික උරුමය."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ආර්ය අෂ්ටාංගික මාර්ගයේ ත්‍රිශික්ෂා විභාජනය",
          header1 = "ශික්ෂාව (Training)",
          header2 = "අන්තර්ගත අංග (Path Factor)",
          header3 = "ප්‍රධාන අරමුණ",
          rows = listOf(
            TableRowData("ප්‍රඥා ශික්ෂාව (Wisdom)", "සම්මා දිට්ඨි, සම්මා සංකප්ප", "යථාර්ථාවබෝධය හා නිවැරදි දැක්ම"),
            TableRowData("ශීල ශික්ෂාව (Morality)", "සම්මා වාචා, සම්මා කම්මන්ත, සම්මා ආජීව", "කය හා වචනයේ සංවරභාවය"),
            TableRowData("සමාධි ශික්ෂාව (Concentration)", "සම්මා වායාම, සම්මා සති, සම්මා සමාධි", "සිතේ එකඟතාව හා මානසික ශාන්තිය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ත්‍රිශික්ෂාවට අෂ්ටාංගික මාර්ගය බෙදීම",
          mnemonicSentence = "ප්‍රඥාවෙන් දැක සිතුවා (දිට්ඨි, සංකප්ප) • ශීලයෙන් කීවා කළා ජීවත්වුණා (වාචා, කම්මන්ත, ආජීව) • සමාධියෙන් වෙහෙසුණා සිහි කළා තැන්පත් වුණා (වායාම, සති, සමාධි)!",
          explanation = "ප්‍රඥා (2) + ශීල (3) + සමාධි (3) = අංග 8.",
          appliesTo = "ආර්ය අෂ්ටාංගික මාර්ගය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ආර්ය අෂ්ටාංගික මාර්ගයේ ප්‍රඥා ශික්ෂාවට අයත් අංග 2 මොනවාද?",
          type = "MCQ",
          options = listOf("1. සම්මා වාචා, සම්මා කම්මන්ත", "2. සම්මා දිට්ඨි, සම්මා සංකප්ප", "3. සම්මා වායාම, සම්මා සති", "4. සම්මා ආජීව, සම්මා සමාධි"),
          correctAnswer = "2. සම්මා දිට්ඨි, සම්මා සංකප්ප",
          markingScheme = "ප්‍රඥා ශික්ෂාවට අයත් වන්නේ සම්මා දිට්ඨි (නිවැරදි දැක්ම) සහ සම්මා සංකප්ප (නිවැරදි කල්පනාව) වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/17O97RA-IbgZnpKt9cVynjoNZ7y5SCE-Q/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "bud_g10_pdf_1",
          title = "10 ශ්‍රේණිය බුද්ධ ධර්මය කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/17O97RA-IbgZnpKt9cVynjoNZ7y5SCE-Q/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.6 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10 - HEALTH & PHYSICAL EDUCATION (10 වසර සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_health_u1",
      grade = "10",
      subject = "සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grade 10 Health & Physical Education Comprehensive Short Notes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "සෞඛ්‍යය හා යහපැවැත්ම: ශාරීරික, මානසික, සමාජයීය හා ආධ්‍යාත්මික යහපැවැත්මේ සමබරතාව.",
        "පෝෂණය හා සමබල ආහාර වේල: ප්‍රධාන පෝෂක 6, ක්ෂුද්‍ර හා සාර්ව පෝෂක, පෝෂණ ඌනතා රෝග සහ ශරීර ස්කන්ධ දර්ශකය (BMI).",
        "ශාරීරික යෝග්‍යතාව සහ අභ්‍යාස: හෘද්-ශ්වසන දරාගැනීම, පේශි ශක්තිය, නම්‍යශීලීතාව සහ ශරීර සංයුතිය.",
        "ක්‍රීඩා සහ මලල ක්‍රීඩා කුසලතා: ධාවන, පැනීම්, විසිකිරීම් ඉසව්වල නිවැරදි ඉරියව් සහ නීති රීති.",
        "ප්‍රථමාධාර සහ අනතුරු වළක්වා ගැනීම: තුවාල, බිඳීම්, විෂවීම් සහ හදිසි අවස්ථාවලදී ක්‍රියා කළ යුතු ආකාරය (RICE ප්‍රතිකාරය)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "සෞඛ්‍ය සම්පන්න ශාරීරික යෝග්‍යතා සංරචක",
          header1 = "යෝග්‍යතා සංරචකය",
          header2 = "අර්ථ දැක්වීම",
          header3 = "මනිනු ලබන පරීක්ෂණය / උදාහරණ",
          rows = listOf(
            TableRowData("හෘද්-ශ්වසන දරාගැනීම", "දිගු වේලාවක් වෙහෙස නොවී ක්‍රියාකාරකම් කිරීමේ හැකියාව", "මීටර් 1500 දිවීම / බීප් පරීක්ෂණය"),
            TableRowData("නම්‍යශීලීතාව", "සන්ධිවල පූර්ණ චලන පරාසයක් සහිතව නැමීමේ හැකියාව", "ඉඳගෙන ඉදිරියට නැමීමේ පරීක්ෂණය (Sit and Reach)"),
            TableRowData("පේශි ශක්තිය හා දරාගැනීම", "බාහිර ප්‍රතිරෝධයකට එරෙහිව බලය යෙදීමේ හැකියාව", "Push-ups / Sit-ups පරීක්ෂණය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "මෘදු පටක තුවාල සඳහා RICE ප්‍රතිකාර ක්‍රමය",
          mnemonicSentence = "R (Rest - විවේකය) → I (Ice - අයිස් තැබීම) → C (Compression - තදකර වෙළීම) → E (Elevation - ඔසවා තැබීම)!",
          explanation = "උළුක්කු වීම් සහ පේශි ඉරීම් සඳහා ක්ෂණික ප්‍රථමාධාරය RICE වේ.",
          appliesTo = "ප්‍රථමාධාර ක්‍රමවේදය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ක්‍රීඩා අනතුරකදී සිදුවන උළුක්කු වීමකදී මුලින්ම ලබාදිය යුතු සම්මත ප්‍රථමාධාර ක්‍රමය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. සම්බාහනය කිරීම", "2. RICE ක්‍රමය", "3. උණුසුම් වතුරෙන් තැවීම", "4. වහාම ඇවිද්දවීම"),
          correctAnswer = "2. RICE ක්‍රමය",
          markingScheme = "මෘදු පටක තුවාල සඳහා මුල් පැය 48 තුළ RICE (Rest, Ice, Compression, Elevation) ප්‍රතිකාරය ලබාදිය යුතුය. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1qEPI5g8KYCmX__5fzHSodtFoLUvj0BWi/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "health_g10_pdf_1",
          title = "10 ශ්‍රේණිය සෞඛ්‍ය හා ශාරීරික අධ්‍යාපනය කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1qEPI5g8KYCmX__5fzHSodtFoLUvj0BWi/preview",
          uploadDate = "2026-08-18",
          fileSize = "3.4 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 11 - CIVIC EDUCATION (පුරවැසි අධ්‍යාපනය කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_civic_u1",
      grade = "11",
      subject = "පුරවැසි අධ්‍යාපනය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "11 ශ්‍රේණිය පුරවැසි අධ්‍යාපනය කෙටි සටහන්",
      unitTitleEnglish = "Grade 11 Civic Education Short Notes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ප්‍රජාතන්ත්‍රවාදී පාලන ක්‍රමය සහ පුරවැසි කාර්යභාරය.",
        "ශ්‍රී ලංකා ආණ්ඩුක්‍රම ව්‍යවස්ථාව, මූලික මිනිස් අයිතිවාසිකම් සහ යුතුකම්.",
        "රාජ්‍ය පාලනය: ව්‍යවස්ථාදායකය (පාර්ලිමේන්තුව), විධායකය (ජනාධිපති හා අමාත්‍ය මණ්ඩලය), අධිකරණය.",
        "යහපාලනය (Good Governance), විනිවිදභාවය සහ නීතියේ ආධිපත්‍යය.",
        "ජාත්‍යන්තර සබඳතා සහ එක්සත් ජාතීන්ගේ සංවිධානය (UN)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "රාජ්‍යයේ ප්‍රධාන ආයතන 3 සංසන්දනය",
          header1 = "ආයතනය (Organ)",
          header2 = "ප්‍රධාන කාර්යය (Function)",
          header3 = "නියෝජිතයන් / ප්‍රධානියා",
          rows = listOf(
            TableRowData("ව්‍යවස්ථාදායකය", "නීති පැනවීම හා සම්මත කිරීම", "කථානායක / පාර්ලිමේන්තු මන්ත්‍රීවරු"),
            TableRowData("විධායකය", "නීති ක්‍රියාත්මක කිරීම හා පාලනය", "ජනාධිපති, අගමැති, අමාත්‍ය මණ්ඩලය"),
            TableRowData("අධිකරණය", "නීතිය අර්ථ නිරූපණය හා යුක්තිය පසිඳලීම", "අගවිනිසුරු සහ විනිසුරුවරු")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "රාජ්‍යයේ ආයතන 3 පහසුවෙන් මතක තබා ගැනීම",
          mnemonicSentence = "ව්‍යවස්ථා හැදුවා - විධායක පැදෙව්වා - අධිකරණ බෙදුවා!",
          explanation = "ව්‍යවස්ථාදායකය (නීති හදයි) → විධායකය (පාලනය ගෙනියයි) → අධිකරණය (යුක්තිය තීන්දු කරයි).",
          appliesTo = "ප්‍රජාතන්ත්‍රවාදී රාජ්‍ය ආයතන 3"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ප්‍රජාතන්ත්‍රවාදී රාජ්‍යයක නීති පැනවීමේ බලය පැවරී ඇත්තේ කුමන ආයතනයටද?",
          type = "MCQ",
          options = listOf("1. විධායකයට", "2. ව්‍යවස්ථාදායකයට", "3. අධිකරණයට", "4. රාජ්‍ය සේවයට"),
          correctAnswer = "2. ව්‍යවස්ථාදායකයට",
          markingScheme = "නීති පැනවීම හා සම්මත කිරීම ව්‍යවස්ථාදායකයේ (පාර්ලිමේන්තුවේ) ප්‍රධාන වගකීම වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1kuL7jmciw_ZKLK4JYz8WV1lbSeeWOf08/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "civic_g11_pdf_1",
          title = "11 ශ්‍රේණිය පුරවැසි අධ්‍යාපනය කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1kuL7jmciw_ZKLK4JYz8WV1lbSeeWOf08/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.1 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 11 & 10 - DANCING (නර්තනය කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_dance_u1",
      grade = "11",
      subject = "නර්තනය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "11 ශ්‍රේණිය නර්තනය පූර්ණ කෙටි සටහන් හා මූලධර්ම",
      unitTitleEnglish = "Grade 11 Traditional & Aesthetic Dancing",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ශ්‍රී ලංකාවේ ප්‍රධාන සම්ප්‍රදායික නර්තන සම්ප්‍රදායන් 3: උඩරට නර්තනය, පහතරට නර්තනය, සබරගමු නර්තනය.",
        "උඩරට නර්තන සම්ප්‍රදායේ ප්‍රධාන බෙරය: ගැටබෙරය (ගෝෂකය). මූලික තාණ්ඩව ලක්ෂණ සහිතයි. ප්‍රධාන ශාන්තිකර්මය: කොහොඹා කංකාරිය.",
        "පහතරට නර්තන සම්ප්‍රදායේ ප්‍රධාන බෙරය: යක් බෙරය (දෙවොල් බෙරය). ලාස්‍ය සහ විකාර රූපී ලක්ෂණ සහිතයි. ප්‍රධාන ශාන්තිකර්ම: ගම්මඩුව, දෙවොල් මඩුව, සන්නි යකුම.",
        "සබරගමු නර්තන සම්ප්‍රදායේ ප්‍රධාන බෙරය: දවුල. ප්‍රධාන ශාන්තිකර්මය: මහසමන් දේවාල පෙරහැර සහ මඩු ශාන්තිකර්ම.",
        "නර්තනයේ මූලික අංග: නෘත්ත (තාලානුකූල චලන), නෘත්‍ය (භාව ප්‍රකාශන සහිත), නාට්‍ය (කතා පුවතක් නිරූපණය කරන)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ශ්‍රී ලංකාවේ දේශීය නර්තන සම්ප්‍රදායන් 3 සංසන්දනය",
          header1 = "සම්ප්‍රදාය (Tradition)",
          header2 = "ප්‍රධාන බෙරය (Drum)",
          header3 = "ප්‍රධාන ශාන්තිකර්මය / ලක්ෂණ",
          rows = listOf(
            TableRowData("උඩරට (Upcountry)", "ගැටබෙරය", "කොහොඹා කංකාරිය / තාණ්ඩව ලක්ෂණ"),
            TableRowData("පහතරට (Lowcountry)", "යක් බෙරය / රුහුණු බෙරය", "දෙවොල් මඩුව, දහඅට සන්නිය"),
            TableRowData("සබරගමු (Sabaragamuwa)", "දවුල", "සමන් දේවාල පුද සිරිත් / ගම්මඩු")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "දේශීය නර්තන බෙර 3 මතක තබා ගැනීම",
          mnemonicSentence = "උඩට ගැටේ - පහළට යකා - සබරෙට දවුල!",
          explanation = "උඩරට = ගැටබෙරය, පහතරට = යක් බෙරය, සබරගමුව = දවුල.",
          appliesTo = "දේශීය නර්තන වාද්‍ය භාණ්ඩ"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "උඩරට නර්තන සම්ප්‍රදායේ මූලික ශාන්තිකර්මය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. දෙවොල් මඩුව", "2. කොහොඹා කංකාරිය", "3. දහඅට සන්නිය", "4. ගම්මඩුව"),
          correctAnswer = "2. කොහොඹා කංකාරිය",
          markingScheme = "උඩරට සම්ප්‍රදායේ ප්‍රධාන හා මූලික ශාන්තිකර්මය කොහොඹා කංකාරිය වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/12GBk7Eg8H558fgpOPGwFSqGskwUXyMfK/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "dance_g11_pdf_1",
          title = "10 සහ 11 ශ්‍රේණි නර්තනය පූර්ණ කෙටි සටහන් සංග්‍රහය (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/12GBk7Eg8H558fgpOPGwFSqGskwUXyMfK/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.4 MB",
          type = "NOTE"
        )
      )
    ),
    SyllabusUnitItem(
      id = "g10_dance_u1",
      grade = "10",
      subject = "නර්තනය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය නර්තනය මූලික සටහන් හා තාක්ෂණික අංග",
      unitTitleEnglish = "Grade 10 Aesthetics & Dancing Basics",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "නර්තන ඉරියව් සහ මණ්ඩිය: පූර්ණ මණ්ඩිය සහ අඩ මණ්ඩිය.",
        "පාද බෙදීම් සහ සරඹ අභ්‍යාස: උඩරට, පහතරට සහ සබරගමු සම්ප්‍රදායන්හි මූලික පා සරඹ.",
        "නර්තන ඇඳුම් කට්ටලය: වෙස් ඇඳුම (ශීර්ෂාභරණ, අවුල්හැරය, බන්දි වළලු, කරපටිය, දේවකරය).",
        "තාල සහ මාත්‍රා: ඒකතාල, දෙතිස් මාත්‍රා සහ විවිධ ලය (විලම්භ, මධ්‍ය, දෘත)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "නර්තන ලය ප්‍රභේද සංසන්දනය",
          header1 = "ලය වර්ගය (Laya)",
          header2 = "වේගය (Speed)",
          header3 = "භාවිතය",
          rows = listOf(
            TableRowData("විලම්භ ලය (Vilambitha)", "මන්දගාමී / සෙමින්", "ආරම්භක පද හා ශාන්ත කොටස්"),
            TableRowData("මධ්‍ය ලය (Madhya)", "සාමාන්‍ය වේගය", "නර්තනයේ මධ්‍යම කොටස්"),
            TableRowData("දෘත ලය (Drutha)", "ඉතා වේගවත්", "නර්තනයේ අවසාන කස්තිරම් හා තීර්මාන")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ලය වර්ග 3 මතක තබා ගැනීම",
          mnemonicSentence = "විලෙන් මධ්‍යයට දුවමු!",
          explanation = "විලෙන් (විලම්භ - සෙමින්) → මධ්‍යයට (මධ්‍ය - සාමාන්‍ය) → දුවමු (දෘත - වේගවත්).",
          appliesTo = "නර්තන ලය 3"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "නර්තනයේ වේගවත්ම ලය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. විලම්භ ලය", "2. මධ්‍ය ලය", "3. දෘත ලය", "4. ඒක ලය"),
          correctAnswer = "3. දෘත ලය",
          markingScheme = "දෘත ලය යනු නර්තනයේ වේගවත්ම ලය වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/12GBk7Eg8H558fgpOPGwFSqGskwUXyMfK/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "dance_g10_pdf_1",
          title = "10 සහ 11 ශ්‍රේණි නර්තනය පූර්ණ කෙටි සටහන් සංග්‍රහය (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/12GBk7Eg8H558fgpOPGwFSqGskwUXyMfK/preview",
          uploadDate = "2026-08-17",
          fileSize = "2.4 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 10 - CIVIC EDUCATION (10 වසර පුරවැසි අධ්‍යාපනය කෙටි සටහන්)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_civic_u1",
      grade = "10",
      subject = "පුරවැසි අධ්‍යාපනය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "10 ශ්‍රේණිය පුරවැසි අධ්‍යාපනය පූර්ණ කෙටි සටහන්",
      unitTitleEnglish = "Grade 10 Civic Education Short Notes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ප්‍රජාතන්ත්‍රවාදය: ජනතාව විසින්, ජනතාව උදෙසා, ජනතාවගේ පාලනයයි. ප්‍රධාන ලක්ෂණ: නීතියේ ආධිපත්‍යය, මූලික මිනිස් අයිතිවාසිකම්, නිදහස් හා සාධාරණ මැතිවරණ.",
        "ආණ්ඩුවේ ප්‍රධාන අංග 3: ව්‍යවස්ථාදායකය (පාර්ලිමේන්තුව - නීති පැනවීම), විධායකය (ජනාධිපති/අමාත්‍ය මණ්ඩලය - නීති ක්‍රියාත්මක කිරීම), අධිකරණය (නීතිය අර්ථ නිරූපණය හා සාධාරණය ඉටු කිරීම).",
        "යහපාලනයේ මූලධර්ම: විනිවිදභාවය, වගවීම, නීතියේ ආධිපත්‍යය, සහභාගීත්වය, කාර්යක්ෂමතාව සහ සාධාරණත්වය.",
        "පුරවැසි අයිතිවාසිකම් සහ යුතුකම්: රටේ ව්‍යවස්ථාවෙන් තහවුරු කර ඇති මූලික අයිතිවාසිකම් භුක්ති විඳින අතරම රටට හා සමාජයට ඉටු කළ යුතු වගකීම් සහ යුතුකම්."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ආණ්ඩුවේ ප්‍රධාන අංග 3 සංසන්දනය",
          header1 = "අංගය (Branch)",
          header2 = "ප්‍රධාන කාර්යභාරය (Function)",
          header3 = "ශ්‍රී ලංකාවේ ආයතනය / නියෝජනය",
          rows = listOf(
            TableRowData("ව්‍යවස්ථාදායකය (Legislature)", "නීති සම්පාදනය කිරීම", "පාර්ලිමේන්තුව (මන්ත්‍රීවරු 225)"),
            TableRowData("විධායකය (Executive)", "නීති හා ප්‍රතිපත්ති ක්‍රියාත්මක කිරීම", "විධායක ජනාධිපති සහ අමාත්‍ය මණ්ඩලය"),
            TableRowData("අධිකරණය (Judiciary)", "නීතිය අර්ථ නිරූපණය හා විනිශ්චය", "ශ්‍රේෂ්ඨාධිකරණය, අභියාචනාධිකරණය ඇතුළු උසාවි")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ආණ්ඩුවේ අංග 3 පහසුවෙන් මතක තබා ගැනීම",
          mnemonicSentence = "ව්‍යවස්ථාවෙන් පනවා - විධායකයෙන් ක්‍රියාකර - අධිකරණයෙන් රකී!",
          explanation = "ව්‍යවස්ථාදායකය (නීති සම්පාදනය) → විධායකය (ක්‍රියාත්මක කිරීම) → අධිකරණය (සාධාරණය හා රැකවරණය).",
          appliesTo = "ආණ්ඩුවේ අංග 3"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ශ්‍රී ලංකාවේ නීති සම්පාදනය කිරීමේ බලය හිමි වන්නේ කාටද?",
          type = "MCQ",
          options = listOf("1. අමාත්‍ය මණ්ඩලයට", "2. පාර්ලිමේන්තුවට", "3. ශ්‍රේෂ්ඨාධිකරණයට", "4. පළාත් සභාවලට පමණි"),
          correctAnswer = "2. පාර්ලිමේන්තුවට",
          markingScheme = "ශ්‍රී ලංකාවේ පරමාධිපත්‍යය යටතේ නීති සම්පාදනය කිරීමේ බලය ව්‍යවස්ථාදායකය හෙවත් පාර්ලිමේන්තුව සතු වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1-H0WHiCYob1T4kQ9Sol4n6SQSJZc9LEX/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "civic_g10_pdf_1",
          title = "10 ශ්‍රේණිය පුරවැසි අධ්‍යාපනය පූර්ණ කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1-H0WHiCYob1T4kQ9Sol4n6SQSJZc9LEX/preview",
          uploadDate = "2026-08-17",
          fileSize = "3.1 MB",
          type = "NOTE"
        )
      )
    ),
    // --------------------------------------------------------------------------
    // GRADE 11 - SCIENCE (11 වසර විද්‍යාව)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_sci_u1",
      grade = "11",
      subject = "විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "ජීවයේ රසායනික පදනම (Chemical Basis of Life)",
      unitTitleEnglish = "Chemical Basis of Life",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ජීවීන්ගේ දේහ සෑදී ඇති ප්‍රධාන මූලද්‍රව්‍ය 4: කාබන් (C), හයිඩ්‍රජන් (H), ඔක්සිජන් (O), නයිට්‍රජන් (N) වේ.",
        "ජෛව අණු ප්‍රධාන කාණ්ඩ 4කි: කාබෝහයිඩ්‍රේට, ප්‍රෝටීන, ලිපිඩ, සහ නියුක්ලික් අම්ල.",
        "කාබෝහයිඩ්‍රේට: C, H, O අඩංගු අතර H:O අනුපාතය 2:1 වේ. ප්‍රධාන කාණ්ඩ: මොනොසැකරයිඩ (ග්ලූකෝස්, ෆෲක්ටෝස්, ගැලැක්ටෝස්), ඩයිසැකරයිඩ (සුක්‍රෝස්, මෝල්ටෝස්, ලැක්ටෝස්), පොලිසැකරයිඩ (පිෂ්ඨය, සෙලියුලෝස්, ග්ලයිකොජන්).",
        "ප්‍රෝටීන: C, H, O, N අඩංගු අතර ගොඩනැගුම් ඒකකය ඇමයිනෝ අම්ලයි. පෙප්ටයිඩ බන්ධන මඟින් බැඳී ඇත. පරීක්ෂාව: බයියුරෙට් පරීක්ෂාව (දම් පැහැය).",
        "ලිපිඩ: C, H, O අඩංගුය. ජලයේ අද්‍රාව්‍ය කාබනික ද්‍රාවකවල ද්‍රාව්‍ය වේ. පරීක්ෂාව: සුඩාන් III හෝ පාරභාසක පරීක්ෂාව.",
        "ජලයේ සුවිශේෂී ගුණ: විශිෂ්ට ද්‍රාවක ගුණය, ඉහළ විශිෂ්ට තාප ධාරිතාව, ඉහළ වාෂ්පීකරණයේ ගුප්ත තාපය, ජලජ ජීවීන්ට හිතකර ඝනත්ව විචලනය (4°C දී උපරිම ඝනත්වය)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "කාබෝහයිඩ්‍රේට වර්ග සංසන්දනය (Types of Carbohydrates)",
          header1 = "කාණ්ඩය (Type)",
          header2 = "උදාහරණ (Examples)",
          header3 = "පරීක්ෂාව & ප්‍රතිඵලය (Test & Result)",
          rows = listOf(
            TableRowData("මොනොසැකරයිඩ", "ග්ලූකෝස්, ෆෲක්ටෝස්, ගැලැක්ටෝස්", "බෙනඩික්ට් පරීක්ෂාව → ගඩොල් රතු අවක්ෂේපය"),
            TableRowData("ඩයිසැකරයිඩ", "මෝල්ටෝස්, සුක්‍රෝස්, ලැක්ටෝස්", "මෝල්ටෝස්/ලැක්ටෝස් බෙනඩික්ට් ධන, සුක්‍රෝස් සෘණ"),
            TableRowData("පොලිසැකරයිඩ", "පිෂ්ඨය, සෙලියුලෝස්, ග්ලයිකොජන්", "අයඩින් පරීක්ෂාව → නිල්-කළු පැහැය (පිෂ්ඨය සඳහා)")
          )
        ),
        ComparisonTable(
          title = "ප්‍රෝටීන vs ලිපිඩ සංසන්දනය",
          header1 = "ලක්ෂණය",
          header2 = "ප්‍රෝටීන (Proteins)",
          header3 = "ලිපිඩ (Lipids)",
          rows = listOf(
            TableRowData("මූලද්‍රව්‍ය සංයුතිය", "C, H, O, N (සමහර විට S)", "C, H, O (අඩු O ප්‍රමාණයක්)"),
            TableRowData("ගොඩනැගුම් ඒකකය", "ඇමයිනෝ අම්ල (Amino acids)", "මේද අම්ල + ග්ලිසරෝල්"),
            TableRowData("ප්‍රධාන කාර්යය", "දේහ වර්ධනය, එන්සයිම, ප්‍රතිදේහ", "ශක්ති ගබඩාව, පරිවරණය, හෝමෝන")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "මොනොසැකරයිඩ මතක තබා ගැනීමේ කෙටි ක්‍රමය",
          mnemonicSentence = "ග්ලු ගාලා ෆෲට් බිව්වම ගැලැක්සිය පෙනෙයි!",
          explanation = "ග්ලු = ග්ලූකෝස් (Glucose), ෆෲට් = ෆෲක්ටෝස් (Fructose), ගැලැක්සි = ගැලැක්ටෝස් (Galactose).",
          appliesTo = "මොනොසැකරයිඩ 3"
        ),
        MemoryTrick(
          title = "ඩයිසැකරයිඩ සංයුතිය මතක තබා ගැනීම",
          mnemonicSentence = "මෝල්ට් (ග්ලු+ග්ලු), සුදු සීනි (ග්ලු+ෆෲට්), කිරි (ග්ලු+ගැලැක්සි)",
          explanation = "මෝල්ටෝස් = ග්ලූකෝස් + ග්ලූකෝස්, සුක්‍රෝස් = ග්ලූකෝස් + ෆෲක්ටෝස්, ලැක්ටෝස් = ග්ලූකෝස් + ගැලැක්ටෝස්.",
          appliesTo = "ඩයිසැකරයිඩ බිඳවැටීම"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ග්ලූකෝස් ද්‍රාවණයකට බෙනඩික්ට් ප්‍රතිකාරකය එක්කර රත් කළ විට ලැබෙන අවසාන වර්ණ විපර්යාසය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. නිල්-කළු පැහැය", "2. ගඩොල් රතු අවක්ෂේපය", "3. දම් පැහැය", "4. කහ පැහැති අවක්ෂේපය"),
          correctAnswer = "2. ගඩොල් රතු අවක්ෂේපය",
          markingScheme = "බෙනඩික්ට් ද්‍රාවණය ඔක්සිහාරක සීනි හමුවේ නිල් පැහැයේ සිට කොළ → කහ → තැඹිලි → ගඩොල් රතු බවට පත්වේ. (ලකුණු 2)",
          marksAllocated = 2
        ),
        UnitQuestion(
          questionNumber = 2,
          questionText = "ජලජ ජීවීන්ට අයිස් තට්ටු යට ශීත ඍතුවේදී ජීවත් වීමට උපකාරී වන ජලයේ භෞතික ගුණය පැහැදිලි කරන්න.",
          type = "SHORT",
          correctAnswer = "4°C දී ජලයේ උපරිම ඝනත්වය පැවතීම සහ අයිස් බවට පත්වීමේදී ඝනත්වය අඩුවී මතුපිට පාවීම.",
          markingScheme = "• 4°C දී උපරිම ඝනත්වය ලැබීම (ලකුණු 2) • මතුපිට මිදුණු අයිස් තට්ටුව තාප පරිවාරකයක් ලෙස ක්‍රියා කිරීම (ලකුණු 2). මුළු ලකුණු 4.",
          marksAllocated = 4
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/17TcFs1wECaHB4C3LMdC8mO2YDKrtrEOI/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "sci_g11_pdf_1",
          title = "11 ශ්‍රේණිය විද්‍යාව පූර්ණ කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/17TcFs1wECaHB4C3LMdC8mO2YDKrtrEOI/preview",
          uploadDate = "2026-08-17",
          fileSize = "3.8 MB",
          type = "NOTE"
        )
      )
    ),

    SyllabusUnitItem(
      id = "g11_sci_u2",
      grade = "11",
      subject = "විද්‍යාව",
      unitNumber = "02 වන පාඩම",
      unitTitleSinhala = "ප්‍රභාසංශ්ලේෂණය (Photosynthesis)",
      unitTitleEnglish = "Photosynthesis",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ප්‍රභාසංශ්ලේෂණය යනු හරිත ශාක සූර්යාලෝක ශක්තිය රසායනික ශක්තිය බවට පරිවර්තනය කර කාබනික ආහාර නිපදවීමේ ක්‍රියාවලියයි.",
        "සමස්ත සමීකරණය: 6CO₂ + 6H₂O + ආලෝක ශක්තිය (හරිතප්‍රද හමුවේ) → C₆H₁₂O₆ + 6O₂",
        "ප්‍රධාන අදියර දෙකකි: 1. ආලෝක ප්‍රතික්‍රියාව (තයිලකොයිඩ පටලවලදී සිදුවේ - ජලය විච්ඡේදනය වී O₂ පිටවේ, ATP හා NADPH නිපදවේ). 2. අඳුරු ප්‍රතික්‍රියාව/කැල්වින් චක්‍රය (ස්ට්‍රෝමාවේදී සිදුවේ - CO₂ තිර වී ග්ලූකෝස් නිපදවේ).",
        "ප්‍රභාසංශ්ලේෂණ අනුපාතයට බලපාන සාධක: ආලෝක තීව්‍රතාව, CO₂ සාන්ද්‍රණය, උෂ්ණත්වය (ප්‍රශස්ත උෂ්ණත්වය 25°C - 35°C)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ආලෝක ප්‍රතික්‍රියාව vs අඳුරු ප්‍රතික්‍රියාව (Light vs Dark Reactions)",
          header1 = "ලක්ෂණය",
          header2 = "ආලෝක ප්‍රතික්‍රියාව (Light Phase)",
          header3 = "අඳුරු ප්‍රතික්‍රියාව (Calvin Cycle)",
          rows = listOf(
            TableRowData("සිදුවන ස්ථානය", "හරිතලවේ තයිලකොයිඩ පටලය", "හරිතලවේ ස්ට්‍රෝමාව (Stroma)"),
            TableRowData("ආලෝක අවශ්‍යතාව", "ආලෝකය අත්‍යවශ්‍යයි", "ආලෝකය සෘජුව අවශ්‍ය නොවේ"),
            TableRowData("ප්‍රධාන ඵල", "O₂ වායුව, ATP, NADPH", "ග්ලූකෝස් (C₆H₁₂O₆), ADP, NADP⁺"),
            TableRowData("අමුද්‍රව්‍යය", "ජලය (H₂O) සහ ආලෝකය", "කාබන් ඩයොක්සයිඩ් (CO₂)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ප්‍රභාසංශ්ලේෂණ ප්‍රතික්‍රියා ස්ථාන මතක තබා ගැනීම",
          mnemonicSentence = "තයිලයේ ආලෝකය - ස්ට්‍රෝමාවේ අඳුර!",
          explanation = "තයිලකොයිඩ = ආලෝක ප්‍රතික්‍රියාව (Light), ස්ට්‍රෝමාව = අඳුරු ප්‍රතික්‍රියාව (Dark / Calvin).",
          appliesTo = "හරිතලව අභ්‍යන්තර ව්‍යුහය"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ප්‍රභාසංශ්ලේෂණයේ ආලෝක ප්‍රතික්‍රියාවේදී පිටවන ඔක්සිජන් (O₂) වායුවේ මූලාශ්‍රය වන්නේ කුමක්ද?",
          type = "MCQ",
          options = listOf("1. කාබන් ඩයොක්සයිඩ් (CO₂)", "2. ජලය (H₂O)", "3. ග්ලූකෝස්", "4. හරිතප්‍රද"),
          correctAnswer = "2. ජලය (H₂O)",
          markingScheme = "ආලෝක ශක්තියෙන් ජල අණු ප්‍රකාශ විච්ඡේදනය වීමෙන් O₂ මුදාහැරේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview"
    ),

    // --------------------------------------------------------------------------
    // GRADE 11 - MATHEMATICS (11 වසර ගණිතය)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_math_u1",
      grade = "11",
      subject = "ගණිතය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "වර්ගජ සමීකරණ (Quadratic Equations)",
      unitTitleEnglish = "Quadratic Equations",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "වර්ගජ සමීකරණයක සම්මත ආකාරය: ax² + bx + c = 0 (a ≠ 0).",
        "විසඳන ප්‍රධාන ක්‍රම 3කි: 1. සාධක සෙවීමේ ක්‍රමය, 2. වර්ගපූර්ණ ක්‍රමය, 3. වර්ගජ සූත්‍රය භාවිතය.",
        "වර්ගජ සූත්‍රය: x = (-b ± √(b² - 4ac)) / (2a)",
        "විවේචකය (Discriminant) Δ = b² - 4ac:",
        "• Δ > 0 නම් තාත්වික සහ එකිනෙකට වෙනස් මූල 2කි.",
        "• Δ = 0 නම් තාත්වික සහ සමාන මූල 2කි (සමපාත මූල).",
        "• Δ < 0 නම් තාත්වික මූල නොපවතී."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "වර්ගජ සමීකරණ විසඳීමේ ක්‍රම සංසන්දනය",
          header1 = "ක්‍රමය",
          header2 = "භාවිත කළ හැකි අවස්ථා",
          header3 = "ප්‍රධාන පියවර",
          rows = listOf(
            TableRowData("සාධක ක්‍රමය", "පහසුවෙන් පද වෙන්කළ හැකි විට", "(x - p)(x - q) = 0 ආකාරයට ලියා x = p හෝ x = q ලබාගැනීම"),
            TableRowData("වර්ගපූර්ණ ක්‍රමය", "වර්ගජ සූත්‍රය ගොඩනැගීමට සහ ප්‍රමේයවලට", "x² + (b/a)x + (b/2a)² එකතු කර පූර්ණ වර්ගයක් සෑදීම"),
            TableRowData("වර්ගජ සූත්‍රය", "ඕනෑම ax² + bx + c = 0 සමීකරණයකට (දශම සහිත විට)", "a, b, c හඳුනාගෙන x = [-b ± √(b² - 4ac)] / 2a ආදේශය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "වර්ගජ සූත්‍රය පහසුවෙන් මතක තබා ගැනීම",
          mnemonicSentence = "ඍණ b ගෙදරින් එළියට ආවා, ප්ලස් මයිනස් රූට් ඇතුලේ b වර්ග වෙලා 4ac අඩු කරලා 2a ගෙන් බෙදුවා!",
          explanation = "x = [-b ± √(b² - 4ac)] / (2a)",
          appliesTo = "Quadratic Formula"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "2x² - 5x + 2 = 0 සමීකරණයේ මූල සොයන්න.",
          type = "STRUCTURED",
          correctAnswer = "x = 2 හෝ x = 1/2",
          markingScheme = "• සාධක වෙන්කිරීම: (2x - 1)(x - 2) = 0 (ලකුණු 2) • 2x - 1 = 0 => x = 1/2 (ලකුණු 1) • x - 2 = 0 => x = 2 (ලකුණු 1). මුළු ලකුණු 4.",
          marksAllocated = 4
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview"
    ),

    // --------------------------------------------------------------------------
    // GRADE 11 - HISTORY (11 වසර ඉතිහාසය)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g11_hist_u1",
      grade = "11",
      subject = "ඉතිහාසය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "ශ්‍රී ලංකාවේ බ්‍රිතාන්‍ය බලය තහවුරු වීම (1796-1833)",
      unitTitleEnglish = "Establishment of British Power in Sri Lanka",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "1796 දී ලන්දේසීන්ගෙන් මුහුදුබඩ ප්‍රදේශ බ්‍රිතාන්‍යයන් අතට පත්විය.",
        "1802 ඒමියන්ස් ගිවිසුම මඟින් ශ්‍රී ලංකාවේ මුහුදුබඩ ප්‍රදේශ බ්‍රිතාන්‍ය කිරීටයේ යටත් විජිතයක් බවට පත්විය.",
        "1815 මාර්තු 02 දින උඩරට ගිවිසුම අත්සන් කිරීමෙන් මුළු දිවයිනම බ්‍රිතාන්‍ය කිරීටයට යටත් විය. ශ්‍රී වික්‍රම රාජසිංහ රජු සිරභාරයට ගැනිණි.",
        "1817-1818 ඌව වෙල්ලස්ස නිදහස් අරගලය: නායකත්වය - කැප්පෙටිපොළ නිලමේ, මඩුගල්ලේ නිලමේ, කිවුලේගෙදර මොහොට්ටාල.",
        "1833 කෝල්බෲක්-කැමරන් ප්‍රතිසංස්කරණ: පළාත් 5ක් පිහිටුවීම, රාජකාරි ක්‍රමය අහෝසි කිරීම, ව්‍යවස්ථාදායක හා විධායක සභා පිහිටුවීම."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "1818 සහ 1848 නිදහස් අරගල සංසන්දනය",
          header1 = "ලක්ෂණය",
          header2 = "1818 ඌව වෙල්ලස්ස අරගලය",
          header3 = "1848 මාතලේ නිදහස් අරගලය",
          rows = listOf(
            TableRowData("නායකත්වය", "කැප්පෙටිපොළ, මඩුගල්ලේ නිලමේවරු", "වීර පුරන් අප්පු, ගොන්ගාලේගොඩ බණ්ඩා"),
            TableRowData("ප්‍රධාන හේතුව", "උඩරට සම්මුතිය කඩවීම, සංස්කෘතික හා ආගමික නොසලකා හැරීම්", "අසාධාරණ බදු පැනවීම (බලු බද්ද, තුවක්කු බද්ද, කරත්ත බද්ද)"),
            TableRowData("ස්වභාවය", "රාජ්‍යත්වය යළි ලබාගැනීමේ රදළ/වැසියන්ගේ සටනක්", "නව ධනපති/පීඩිත පොදු ජනතා අරගලයක්")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "කෝල්බෲක් ප්‍රතිසංස්කරණ 4ක් මතක තබා ගැනීම",
          mnemonicSentence = "පළාත් 5යි - රාජකාරි බායි - සභා 2යි - ඉංග්‍රීසි හයි!",
          explanation = "1. පළාත් 5කට බෙදීම, 2. රාජකාරි අහෝසිය, 3. විධායක/ව්‍යවස්ථාදායක සභා 2, 4. ඉංග්‍රීසි අධ්‍යාපනය ඇරඹීම.",
          appliesTo = "Colebrooke Reforms (1833)"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "1815 උඩරට ගිවිසුම අත්සන් කළ දිනය සහ ඉංග්‍රීසි ආණ්ඩුකාරවරයා කවුද?",
          type = "SHORT",
          correctAnswer = "1815 මාර්තු 02 වන දින, ශ්‍රීමත් රොබට් බ්‍රවුන්රිග් ආණ්ඩුකාරවරයා.",
          markingScheme = "• 1815 මාර්තු 02 (ලකුණු 2) • රොබට් බ්‍රවුන්රිග් (ලකුණු 2). මුළු ලකුණු 4.",
          marksAllocated = 4
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1zAddaGRd4loU0yxwWaMDi14G3rcFOvP4/preview"
    ),

    // --------------------------------------------------------------------------
    // GRADE 10 - SCIENCE (10 වසර විද්‍යාව)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g10_sci_u1",
      grade = "10",
      subject = "විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "ජීවයේ ඒකකය - සෛලය (The Cell)",
      unitTitleEnglish = "The Cell as Basic Unit of Life",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "සියලුම ජීවීන්ගේ ව්‍යුහාත්මක හා කෘත්‍යමය මූලික ඒකකය සෛලයයි.",
        "සෛල වාදය ඉදිරිපත් කළ විද්‍යාඥයින්: මැතියස් ශ්ලයිඩන්, තියඩෝර් ශ්වාන් සහ රුඩොල්ෆ් වර්චව්.",
        "ප්‍රධාන ඉන්ද්‍රයිකා: න්‍යෂ්ටිය (ජානමය පාලනය), මයිටොකොන්ඩ්‍රියා (සෛලීය ශ්වසනය සහ ATP බලශක්ති බලාගාරය), හරිතලව (ප්‍රභාසංශ්ලේෂණය), රයිබොසෝම (ප්‍රෝටීන සංස්ලේෂණය), සෛල බිත්තිය (ශාකවල පමණක් ඇත - සෙලියුලෝස් වලින් සෑදී ඇත)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ශාක සෛල vs සත්ත්ව සෛල සංසන්දනය (Plant vs Animal Cell)",
          header1 = "ලක්ෂණය",
          header2 = "ශාක සෛලය (Plant Cell)",
          header3 = "සත්ත්ව සෛලය (Animal Cell)",
          rows = listOf(
            TableRowData("සෛල බිත්තිය", "පවතී (සෙලියුලෝස් සහිතයි)", "නොපවතී"),
            TableRowData("හරිතලව (Chloroplasts)", "පවතී", "නොපවතී"),
            TableRowData("රික්තකය", "මධ්‍යයේ විශාල ස්ථිර රික්තකයක් ඇත", "කුඩා තාවකාලික රික්තක ඇත"),
            TableRowData("කේන්ද්‍රදේහ (Centrosomes)", "නොපවතී (උසස් ශාකවල)", "පවතී (සෛල බෙදීමට උපකාරී වේ)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "සෛල වාදයේ විද්‍යාඥයින් තිදෙනා මතක තබා ගැනීම",
          mnemonicSentence = "ශ්ලයිඩන් ශාක බැලුවා - ශ්වාන් සතුන් බැලුවා - වර්චව් සෛල බෙදුවා!",
          explanation = "ශ්ලයිඩන් = ශාක සෛල, ශ්වාන් = සත්ත්ව සෛල, රුඩොල්ෆ් වර්චව් = පෙර පැවති සෛල බෙදීමෙන් නව සෛල සෑදේ.",
          appliesTo = "Cell Theory Scientists"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "සෛලයේ බලශක්ති බලාගාරය (Powerhouse of the cell) ලෙස හඳුන්වන්නේ කුමන ඉන්ද්‍රයිකාවද?",
          type = "MCQ",
          options = listOf("1. න්‍යෂ්ටිය", "2. මයිටොකොන්ඩ්‍රියාව", "3. හරිතලවය", "4. ගොල්ගි දේහය"),
          correctAnswer = "2. මයිටොකොන්ඩ්‍රියාව",
          markingScheme = "සෛලීය ශ්වසනය මඟින් ATP ශක්තිය ජනනය කරන බැවින් මයිටොකොන්ඩ්‍රියාව බලශක්ති බලාගාරයයි. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1vx9uXTL_pKexaA5g0IHPa47h6eKdINZl/preview",
      attachedDrivePdfs = mutableListOf(
        AttachedGoogleDrivePdf(
          id = "sci_g10_pdf_1",
          title = "10 ශ්‍රේණිය විද්‍යාව කෙටි සටහන් (Google Drive)",
          driveUrl = "https://drive.google.com/file/d/1vx9uXTL_pKexaA5g0IHPa47h6eKdINZl/preview",
          uploadDate = "2026-08-18",
          fileSize = "3.5 MB",
          type = "NOTE"
        )
      )
    ),

    // --------------------------------------------------------------------------
    // GRADE 09 COMPREHENSIVE SYLLABUS UNITS (09 ශ්‍රේණිය පූර්ණ විෂය නිර්දේශ ඒකක)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g09_sci_u1",
      grade = "09",
      subject = "විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "පදාර්ථයේ ව්‍යුහය සහ රසායනික බන්ධන",
      unitTitleEnglish = "Structure of Matter and Chemical Bonding",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "පරමාණුවක ප්‍රධාන උප පරමාණුක අංශු 3: ප්‍රෝටෝන (+1), නියුට්‍රෝන (0), සහ ඉලෙක්ට්‍රෝන (-1).",
        "පරමාණුක ක්‍රමාංකය (Z) = න්‍යෂ්ටියේ ප්‍රෝටෝන ගණන. ස්කන්ධ ක්‍රමාංකය (A) = ප්‍රෝටෝන + නියුට්‍රෝන ගණන.",
        "ඉලෙක්ට්‍රොනික වින්‍යාසය: 1 වන කවචය (K = උපරිම 2), 2 වන කවචය (L = උපරිම 8), 3 වන කවචය (M = උපරිම 8).",
        "අයනික බන්ධන: ලෝහ පරමාණු ඉලෙක්ට්‍රෝන පිටකර කැටායන සාදන අතර, අලෝහ පරමාණු ඉලෙක්ට්‍රෝන ලබාගෙන ඇනායන සාදයි.",
        "සහසංයුජ බන්ධන: අලෝහ පරමාණු අතර ඉලෙක්ට්‍රෝන යුගල හවුලේ තබා ගැනීමෙන් සෑදේ."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "උප පරමාණුක අංශු සංසන්දනය",
          header1 = "අංශුව",
          header2 = "ආරෝපණය",
          header3 = "පිහිටීම හා ස්කන්ධය",
          rows = listOf(
            TableRowData("ප්‍රෝටෝනය (p)", "+1 (ධන)", "න්‍යෂ්ටිය තුළ • සාපේක්ෂ ස්කන්ධය 1"),
            TableRowData("නියුට්‍රෝනය (n)", "0 (උදාසීන)", "න්‍යෂ්ටිය තුළ • සාපේක්ෂ ස්කන්ධය 1"),
            TableRowData("ඉලෙක්ට්‍රෝනය (e)", "-1 (ඍණ)", "න්‍යෂ්ටිය වටා කවචවල • ස්කන්ධය 1/1840")
          )
        ),
        ComparisonTable(
          title = "අයනික බන්ධන vs සහසංයුජ බන්ධන",
          header1 = "ලක්ෂණය",
          header2 = "අයනික සංයෝග (Ionic)",
          header3 = "සහසංයුජ සංයෝග (Covalent)",
          rows = listOf(
            TableRowData("සෑදෙන ආකාරය", "ඉලෙක්ට්‍රෝන හුවමාරුවෙන් (ලෝහ + අලෝහ)", "ඉලෙක්ට්‍රෝන හවුලේ තබාගැනීමෙන් (අලෝහ + අලෝහ)"),
            TableRowData("ද්‍රවාංක / තාපාංක", "ඉතා ඉහළයි (ශක්තිමත් දැලිස්)", "සාපේක්ෂව පහළයි"),
            TableRowData("විද්‍යුත් සන්නායකතාව", "විලයනයේදී හෝ ජලීය ද්‍රාවණයේදී සන්නයනය කරයි", "සාමාන්‍යයෙන් විද්‍යුතය සන්නයනය නොකරයි"),
            TableRowData("උදාහරණ", "NaCl, MgO, CaCl2", "H2O, CO2, CH4, O2")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "පරමාණුක අංශු ආරෝපණ මතක කෙටි ක්‍රමය",
          mnemonicSentence = "P for Positive, N for Neutral, E for Electronic minus!",
          explanation = "P = Positive (+1), N = Neutral (0), E = Negative (-1).",
          appliesTo = "Subatomic Particles"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "සෝඩියම් (Na, Z=11) පරමාණුවේ නිවැරදි ඉලෙක්ට්‍රොනික වින්‍යාසය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. 2, 8, 1", "2. 2, 9", "3. 8, 2, 1", "4. 2, 8, 8, 1"),
          correctAnswer = "1. 2, 8, 1",
          markingScheme = "පළමු කවචයට 2, දෙවන කවචයට 8, තුන්වන කවචයට 1 ලෙස ඉලෙක්ට්‍රෝන 11 පිරේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
    ),

    SyllabusUnitItem(
      id = "g09_math_u1",
      grade = "09",
      subject = "ගණිතය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "සමගාමී සමීකරණ, වීජීය ප්‍රකාශන හා පයිතගරස් ප්‍රමේයය",
      unitTitleEnglish = "Simultaneous Equations & Pythagoras Theorem",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "සමගාමී සමීකරණ විසඳීම: විචල්‍යයක් ඉවත් කිරීමේ ක්‍රමය (Elimination) හෝ ආදේශ කිරීමේ ක්‍රමය (Substitution).",
        "වීජීය ප්‍රකාශනවල ගුණිත: (a + b)(c + d) = ac + ad + bc + bd.",
        "වර්ග දෙකක අන්තරය: a² - b² = (a - b)(a + b).",
        "පූර්ණ වර්ග ත්‍රිපද: (a + b)² = a² + 2ab + b² සහ (a - b)² = a² - 2ab + b².",
        "පයිතගරස් ප්‍රමේයය: ඍජුකෝණී ත්‍රිකෝණයක කර්ණයේ වර්ගය, අනෙක් පාද දෙකේ වර්ගවල එකතුවට සමාන වේ (c² = a² + b²)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "සුලබ පයිතගරස් ත්‍රිත්ව (Pythagorean Triples)",
          header1 = "කට්ටලය",
          header2 = "පාද a, b (ලම්බක පාද)",
          header3 = "කර්ණය c (දිගම පාදය)",
          rows = listOf(
            TableRowData("1 වන ත්‍රිත්වය", "3, 4", "5 (3² + 4² = 9 + 16 = 25 = 5²)"),
            TableRowData("2 වන ත්‍රිත්වය", "6, 8", "10 (6² + 8² = 36 + 64 = 100 = 10²)"),
            TableRowData("3 වන ත්‍රිත්වය", "5, 12", "13 (5² + 12² = 25 + 144 = 169 = 13²)"),
            TableRowData("4 වන ත්‍රිත්වය", "8, 15", "17 (8² + 15² = 64 + 225 = 289 = 17²)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "පයිතගරස් ප්‍රමේයයේ කර්ණය සෙවීමේ සූත්‍රය",
          mnemonicSentence = "කර්ණය² = පාදය1² + පාදය2² (දිගම පැත්ත තනිවම පැත්තක!)",
          explanation = "ඍජුකෝණයට ඉදිරියෙන් ඇති කර්ණය හැමවිටම අනෙක් පාදවල වර්ග එකතුවට සමාන වේ.",
          appliesTo = "Pythagoras Theorem"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ඍජුකෝණී ත්‍රිකෝණයක ලම්බක පාද 6 cm සහ 8 cm නම්, කර්ණයේ දිග කොපමණද?",
          type = "MCQ",
          options = listOf("1. 10 cm", "2. 12 cm", "3. 14 cm", "4. 100 cm"),
          correctAnswer = "1. 10 cm",
          markingScheme = "c² = 6² + 8² = 36 + 64 = 100 => c = √100 = 10 cm. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
    ),

    SyllabusUnitItem(
      id = "g09_hist_u1",
      grade = "09",
      subject = "ඉතිහාසය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "දඹදෙණිය, යාපහුව, කුරුණෑගල, ගම්පොළ සහ කෝට්ටේ යුගය",
      unitTitleEnglish = "Medieval Sri Lankan Kingdoms (Dambadeniya to Kotte)",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "පොළොන්නරු රාජධානියේ බිඳවැටීමෙන් පසු නිරිතදිග රාජධානි බිහිවීම: දඹදෙණිය (3 වන විජයබාහු, 2 වන පරාක්‍රමබාහු - පණ්ඩිත පරාක්‍රමබාහු).",
        "යාපහුව රාජධානිය: 1 වන බුවනෙකබාහු රජු (විශේෂිත සිංහ කැටයම් සහිත පියගැට පෙළ).",
        "කුරුණෑගල රාජධානිය: 2 වන බුවනෙකබාහු, 4 වන පරාක්‍රමබාහු (දළදා සිරිත, ජාතක පොත සිංහලට පරිවර්තනය).",
        "ගම්පොළ රාජධානිය: 4 වන බුවනෙකබාහු, 3 වන වික්‍රමබාහු (ලංකාතිලක, ගඩලාදෙණිය, ඇම්බැක්ක දේවාල).",
        "කෝට්ටේ යුගය: 6 වන පරාක්‍රමබාහු රජු (මුළු ලංකාවම එක්සේසත් කළ අවසන් සිංහල රජු, සන්දේශ කාව්‍ය සාහිත්‍යයේ ස්වර්ණමය යුගය)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "නිරිතදිග රාජධානි හා ශ්‍රේෂ්ඨ රජවරු සංසන්දනය",
          header1 = "රාජධානිය",
          header2 = "ප්‍රධාන රජු",
          header3 = "වැදගත් සේවාව / ස්මාරකය",
          rows = listOf(
            TableRowData("දඹදෙණිය", "2 වන පරාක්‍රමබාහු", "කලිඟු මාඝ පලවා හැරීම, කව්සිළුමිණ, පූජාවලිය"),
            TableRowData("යාපහුව", "1 වන බුවනෙකබාහු", "යාපහුව පර්වත බලකොටුව සහ අලංකාර දොරටුව"),
            TableRowData("කුරුණෑගල", "4 වන පරාක්‍රමබාහු", "පන්සිය පණස් ජාතක පොත සිංහලට නැඟීම"),
            TableRowData("ගම්පොළ", "4 වන බුවනෙකබාහු", "ගඩලාදෙණිය හා ලංකාතිලක විහාර නිර්මාණය"),
            TableRowData("කෝට්ටේ", "6 වන පරාක්‍රමබාහු", "ලංකාව එක්සේසත් කිරීම, සන්දේශ කාව්‍ය යුගය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "නිරිතදිග රාජධානි අනුපිළිවෙල මතක තබා ගැනීම",
          mnemonicSentence = "දඹේ ඉඳන් යාපහුවට ගිහින් - කුරුණෑගල හරහා ගම්පොළින් කෝට්ටේට ආවා!",
          explanation = "දඹදෙණිය → යාපහුව → කුරුණෑගල → ගම්පොළ → කෝට්ටේ.",
          appliesTo = "Medieval Sri Lanka Capitals"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "මුළු ලංකාවම එක්සේසත් කළ කෝට්ටේ යුගයේ ශ්‍රේෂ්ඨතම රජතුමා කවුද?",
          type = "MCQ",
          options = listOf("1. 6 වන පරාක්‍රමබාහු රජු", "2. 2 වන පරාක්‍රමබාහු රජු", "3. 1 වන බුවනෙකබාහු රජු", "4. ධර්මපාල රජු"),
          correctAnswer = "1. 6 වන පරාක්‍රමබාහු රජු",
          markingScheme = "6 වන පරාක්‍රමබාහු රජු මුළු ලංකාවම එක්සේසත් කර වසර 55ක් රජකම් කළේය. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview"
    ),

    // --------------------------------------------------------------------------
    // GRADE 08 COMPREHENSIVE SYLLABUS UNITS (08 ශ්‍රේණිය පූර්ණ විෂය නිර්දේශ ඒකක)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g08_sci_u1",
      grade = "08",
      subject = "විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "ශාක විවිධත්වය, ක්ෂුද්‍රජීවීන් හා රසායනික වෙනස්වීම්",
      unitTitleEnglish = "Plant Diversity, Microorganisms & Chemical Changes",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ශාක වර්ගීකරණය: අපුෂ්ප ශාක (ඇල්ගී, බ්‍රයෝෆයිට, පර්ණාංග) සහ සපුෂ්ප ශාක (ඒකබීජපත්‍රී හා ද්විබීජපත්‍රී).",
        "ක්ෂුද්‍රජීවී කාණ්ඩ 4: බැක්ටීරියා, දිලීර, ප්‍රෝටොසෝවා සහ වෛරස (අජීවී/ජීවී අතරමැදි).",
        "භෞතික වෙනස්වීම්: නව ද්‍රව්‍ය නොසෑදෙන, ප්‍රතිවර්ත්‍ය වෙනස්වීම් (උදා: අයිස් දියවීම, ජලය වාෂ්පවීම).",
        "රසායනික වෙනස්වීම්: නව ද්‍රව්‍ය සෑදෙන, අප්‍රතිවර්ත්‍ය වෙනස්වීම් (උදා: යකඩ මලබැඳීම, දර දැවීම, කිරි මුදවා ගැනීම)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ඒකබීජපත්‍රී vs ද්විබීජපත්‍රී ශාක සංසන්දනය",
          header1 = "ලක්ෂණය",
          header2 = "ඒකබීජපත්‍රී (Monocot)",
          header3 = "ද්විබීජපත්‍රී (Dicot)",
          rows = listOf(
            TableRowData("බීජ පත්‍ර ගණන", "1කි", "2කි"),
            TableRowData("නාරටි වින්‍යාසය", "සමාන්තර නාරටි වින්‍යාසය (Parallel)", "ජාලාකාර නාරටි වින්‍යාසය (Reticulate)"),
            TableRowData("මුල් පද්ධතිය", "කෙඳි මුල් පද්ධතිය (Fibrous roots)", "මුදුන් මුල් පද්ධතිය (Tap root system)"),
            TableRowData("පුෂ්ප අංග", "ත්‍රිඅංගික (3 හෝ 3 ගුණාකාර)", "චතුර් හෝ පංචාංගික (4, 5 ගුණාකාර)"),
            TableRowData("උදාහරණ", "වී, බඩඉරිඟු, පොල්, තෘණ", "අඹ, කොස්, රනිල බෝග, සියඹලා")
          )
        ),
        ComparisonTable(
          title = "භෞතික වෙනස්වීම් vs රසායනික වෙනස්වීම්",
          header1 = "ලක්ෂණය",
          header2 = "භෞතික වෙනස්වීම (Physical)",
          header3 = "රසායනික වෙනස්වීම (Chemical)",
          rows = listOf(
            TableRowData("නව ද්‍රව්‍ය සෑදීම", "නව ද්‍රව්‍ය සෑදෙන්නේ නැත", "සම්පූර්ණයෙන්ම නව ද්‍රව්‍ය සෑදේ"),
            TableRowData("ප්‍රතිවර්ත්‍ය බව", "ප්‍රතිවර්ත්‍ය කළ හැක (ආපසු හැරවිය හැක)", "අප්‍රතිවර්ත්‍ය වේ (ආපසු හැරවිය නොහැක)"),
            TableRowData("ස්කන්ධ වෙනස", "ද්‍රව්‍යයේ ස්කන්ධය වෙනස් නොවේ", "රසායනික සංයුතිය වෙනස් වේ"),
            TableRowData("උදාහරණ", "ඉටි දියවීම, ලුණු වතුරේ දියවීම", "යකඩ මලකඩ කෑම, කඩදාසි දැවීම")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ඒකබීජපත්‍රී ශාක ලක්ෂණ 3 මතක තබා ගැනීම",
          mnemonicSentence = "එක බීජේ - කෙඳි මුලයි - සමාන්තර නාරටියයි!",
          explanation = "ඒකබීජපත්‍රී = කෙඳි මුල් + සමාන්තර නාරටි.",
          appliesTo = "Monocotyledonous plants"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "පහත සඳහන් සිදුවීම් අතරින් රසායනික වෙනස්වීමක් වන්නේ කුමක්ද?",
          type = "MCQ",
          options = listOf("1. අයිස් කැටයක් ජලය බවට පත්වීම", "2. යකඩ ඇණයක් මලබැඳීම", "3. වීදුරුවක් බිඳී යාම", "4. සීනි ජලයේ දියවීම"),
          correctAnswer = "2. යකඩ ඇණයක් මලබැඳීම",
          markingScheme = "යකඩ මලබැඳීමේදී යකඩ ඔක්සයිඩ් නම් නව රසායනික ද්‍රව්‍යයක් සෑදේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
    ),

    SyllabusUnitItem(
      id = "g08_math_u1",
      grade = "08",
      subject = "ගණිතය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "වීජීය ප්‍රකාශන, සාධක, කාටිසීය තලය හා කෝණ",
      unitTitleEnglish = "Algebraic Factors, Angles & Cartesian Coordinates",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "පොදු සාධක ඉවතට ගැනීම: ax + ay = a(x + y).",
        "පද හතරක ප්‍රකාශන කාණ්ඩ කිරීමෙන් සාධක සෙවීම: ax + ay + bx + by = a(x + y) + b(x + y) = (x + y)(a + b).",
        "කාටිසීය තලය: x අක්ෂය (තිරස් අක්ෂය) සහ y අක්ෂය (සිරස් අක්ෂය). ඛණ්ඩාංක ලියන ක්‍රමය: (x, y).",
        "ත්‍රිකෝණයක අභ්‍යන්තර කෝණවල එකතුව = 180°.",
        "සරල රේඛාවක් මත යාබද කෝණවල එකතුව = 180°.",
        "ප්‍රතිමුඛ කෝණ: සරල රේඛා දෙකක් ඡේදනය වීමේදී සෑදෙන ප්‍රතිමුඛ කෝණ විශාලත්වයෙන් සමාන වේ."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "සමාන්තර රේඛා හා තිරස් ඡේදකයකින් සෑදෙන කෝණ",
          header1 = "කෝණ වර්ගය",
          header2 = "හඳුනාගන්නා අක්ෂර හැඩය",
          header3 = "විශේෂ ලක්ෂණය",
          rows = listOf(
            TableRowData("ඒකාන්තර කෝණ (Alternate)", "Z හැඩය", "විශාලත්වයෙන් සමාන වේ (a = b)"),
            TableRowData("අනුරූප කෝණ (Corresponding)", "F හැඩය", "විශාලත්වයෙන් සමාන වේ (p = q)"),
            TableRowData("මිත්‍ර කෝණ (Allied / Co-interior)", "C හෝ U හැඩය", "කෝණ දෙකේ එකතුව 180° වේ (x + y = 180°)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "සමාන්තර රේඛා කෝණ 3 මතක තබා ගැනීමේ කෙටි ක්‍රමය",
          mnemonicSentence = "Z එකෙන් ඒකාන්තර - F එකෙන් අනුරූප - C එකෙන් මිත්‍ර (180°)!",
          explanation = "Z = ඒකාන්තර කෝණ සමානයි, F = අනුරූප කෝණ සමානයි, C = මිත්‍ර කෝණ එකතුව 180°.",
          appliesTo = "Parallel Line Angles"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ත්‍රිකෝණයක කෝණ දෙකක් 50° සහ 70° නම්, තුන්වන කෝණයේ අගය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. 60°", "2. 70°", "3. 80°", "4. 90°"),
          correctAnswer = "1. 60°",
          markingScheme = "180° - (50° + 70°) = 180° - 120° = 60°. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
    ),

    SyllabusUnitItem(
      id = "g08_hist_u1",
      grade = "08",
      subject = "ඉතිහාසය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "පොළොන්නරු රාජධානිය හා ශ්‍රේෂ්ඨ පාලකයෝ",
      unitTitleEnglish = "Polonnaruwa Kingdom and Great Rulers",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "චෝල ආක්‍රමණිකයන් පලවා හැර පොළොන්නරුවේ රජ වූ පළමුවැන්නා: 1 වන විජයබාහු රජු (ක්‍රි.ව. 1055 - 1110).",
        "මහා පරාක්‍රමබාහු රජු (ක්‍රි.ව. 1153 - 1186): 'අහසින් වැටෙන එකදු දිය බිඳකුදු මිනිසාගේ ප්‍රයෝජනයට නොගෙන මුහුදට ගලා යාමට ඉඩ නොදිය යුතුය' ප්‍රතිපත්තිය.",
        "පරාක්‍රම සමුද්‍රය නිර්මාණය කිරීම සහ ලක්දිව ධාන්‍යාගාරය බවට පත් කිරීම.",
        "නිශ්ශංකමල්ල රජු: නිශ්ශංක ලතා මණ්ඩපය, හැටදාගෙය, රන්කොත් වෙහෙර ප්‍රතිසංස්කරණය සහ සෙල්ලිපි රැසක් පිහිටුවීම (ගල්පොත සෙල්ලිපිය)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "පොළොන්නරු යුගයේ රජවරු 3 සංසන්දනය",
          header1 = "රජතුමා",
          header2 = "දේශපාලනික / ආගමික මෙහෙවර",
          header3 = "ප්‍රධාන වාරි හා ගෘහනිර්මාණ දායකත්වය",
          rows = listOf(
            TableRowData("1 වන විජයබාහු", "සොළී පාලනය නිමා කිරීම, බුරුමයෙන් උපසම්පදාව ගෙන ඒම", "අටදාගෙය නිර්මාණය, වාරි පද්ධති ප්‍රතිසංස්කරණය"),
            TableRowData("මහා පරාක්‍රමබාහු", "ලංකාව එක්සේසත් කිරීම, සාසන සංශෝධනය", "පරාක්‍රම සමුද්‍රය, සත්මහල් ප්‍රාසාදය, ගල් විහාරය"),
            TableRowData("නිශ්ශංකමල්ල", "බදු සහන ලබාදීම, ධාර්මික පාලනය", "නිශ්ශංක ලතා මණ්ඩපය, හැටදාගෙය, ගල්පොත සෙල්ලිපිය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "පොළොන්නරුවේ ශ්‍රේෂ්ඨ රජවරු 3 මතක තබා ගැනීම",
          mnemonicSentence = "විජය නිදහස් කළා - පරාක්‍රම වැව් හැදුවා - නිශ්ශංක සෙල්ලිපි කෙටුවා!",
          explanation = "1 වන විජයබාහු (නිදහස) → මහා පරාක්‍රමබාහු (පරාක්‍රම සමුද්‍රය) → නිශ්ශංකමල්ල (සෙල්ලිපි/ශිලා ලේඛන).",
          appliesTo = "Polonnaruwa Kings"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "පරාක්‍රම සමුද්‍රය නිර්මාණය කළ සහ ලක්දිව සහලින් ස්වයංපෝෂිත කළ රජු කවුද?",
          type = "MCQ",
          options = listOf("1. 1 වන විජයබාහු රජු", "2. මහා පරාක්‍රමබාහු රජු", "3. නිශ්ශංකමල්ල රජු", "4. ධාතුසේන රජු"),
          correctAnswer = "2. මහා පරාක්‍රමබාහු රජු",
          markingScheme = "මහා පරාක්‍රමබාහු රජු පරාක්‍රම සමුද්‍රය නිර්මාණය කළේය. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview"
    ),

    // --------------------------------------------------------------------------
    // GRADE 07 COMPREHENSIVE SYLLABUS UNITS (07 ශ්‍රේණිය පූර්ණ විෂය නිර්දේශ ඒකක)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g07_sci_u1",
      grade = "07",
      subject = "විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "ශාක හා සත්ත්ව ලෝකය, තාපය හා ආලෝකය",
      unitTitleEnglish = "Plant & Animal Diversity, Heat and Light",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "පෘෂ්ඨවංශීන් කාණ්ඩ 5: මත්ස්‍ය, උභයජීවී, උරග, පක්ෂි, ක්ෂීරපායී.",
        "තාපය සහ උෂ්ණත්වය: තාපය යනු ශක්ති ආකාරයකි (ජූල් - J). උෂ්ණත්වය යනු උණුසුම් හෝ සිසිල් බව මනින මිනුමකි (සෙල්සියස් - °C, කෙල්වින් - K).",
        "තාප සම්ප්‍රේෂණ ක්‍රම 3: සන්නයනය (ඝන ද්‍රව්‍යවල), සංවහනය (ද්‍රව සහ වායුවල), විකිරණය (මාධ්‍යයක් අවශ්‍ය නොවේ - රික්තකය හරහා).",
        "ආලෝකය පරාවර්තනය: පතන කෝණය (i) = පරාවර්තන කෝණය (r). තල දර්පණයකින් සෑදෙන ප්‍රතිබිම්බය: අතාත්විකයි, උඩුකුරුයි, වස්තුවේ ප්‍රමාණයට සමානයි, පාර්ශ්විකව අපවර්තනය වේ."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "තාප සම්ප්‍රේෂණ ක්‍රම 3 සංසන්දනය",
          header1 = "ක්‍රමය",
          header2 = "සිදුවන මාධ්‍යය",
          header3 = "උදාහරණය",
          rows = listOf(
            TableRowData("සන්නයනය (Conduction)", "ඝන ද්‍රව්‍ය (අංශු කම්පනයෙන්)", "ලෝහ හැන්දක කෙළවරක් රත්වීම"),
            TableRowData("සංවහනය (Convection)", "ද්‍රව සහ වායු (ධාරා මඟින්)", "ජලය රත්වීමේදී ඉහළ පහළ යාම / මුහුදු සුළං"),
            TableRowData("විකිරණය (Radiation)", "මාධ්‍යයක් අනවශ්‍යයි (තරංග ලෙස)", "සූර්ය තාපය පෘථිවියට ලැබීම / ගිනි ගොඩකින් තාපය දැනීම")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "පෘෂ්ඨවංශී කාණ්ඩ 5 මතක තබා ගැනීම",
          mnemonicSentence = "මාළු - ගෙම්බෝ - නයි - කුරුල්ලෝ - අලි!",
          explanation = "මාළු (මත්ස්‍ය) - ගෙම්බෝ (උභයජීවී) - නයි (උරග) - කුරුල්ලෝ (පක්ෂී) - අලි (ක්ෂීරපායී).",
          appliesTo = "5 Vertebrate Classes"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "සූර්යයාගේ සිට පෘථිවියට තාපය සම්ප්‍රේෂණය වන ප්‍රධාන ක්‍රමය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. සන්නයනය", "2. සංවහනය", "3. විකිරණය", "4. වාෂ්පීභවනය"),
          correctAnswer = "3. විකිරණය",
          markingScheme = "හිස් අවකාශය (රික්තකය) හරහා විද්‍යුත් චුම්භක තරංග ලෙස තාපය ගමන් කරන්නේ විකිරණයෙනි. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
    ),

    SyllabusUnitItem(
      id = "g07_math_u1",
      grade = "07",
      subject = "ගණිතය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "භාග, දශම, ප්‍රතිශත, සමීකරණ හා පරිමිතිය",
      unitTitleEnglish = "Fractions, Decimals, Percentages & Perimeter",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "භාග එකතු කිරීම හා අඩු කිරීම: හරයන් සමාන කිරීම (කු.පො.ගු සෙවීම).",
        "භාග ගුණ කිරීම: හරය හරයෙන්ද, ලවය ලවයෙන්ද ගුණ කිරීම.",
        "භාග බෙදීම: දෙවන භාගයේ පරස්පරයෙන් ගුණ කිරීම (a/b ÷ c/d = a/b × d/c).",
        "ප්‍රතිශත: 100 න් කොපමණද යන්න දැක්වීම (උදා: 25% = 25/100 = 1/4 = 0.25).",
        "සරල සමීකරණ විසඳීම: විචල්‍යය තනි කිරීම (x + 5 = 12 => x = 12 - 5 = 7).",
        "පරිමිතිය: සංවෘත තල රූපයක වට මායිමේ මුළු දිගයි."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ප්‍රධාන ජ්‍යාමිතික රූපවල පරිමිතිය හා වර්ගඵල සූත්‍ර",
          header1 = "ජ්‍යාමිතික රූපය",
          header2 = "පරිමිතිය (Perimeter)",
          header3 = "වර්ගඵලය (Area)",
          rows = listOf(
            TableRowData("සමචතුරස්‍රය (පාදය a)", "4 × a", "a × a = a²"),
            TableRowData("සෘජුකෝණාස්‍රය (දිග l, පළල w)", "2(l + w)", "l × w"),
            TableRowData("ත්‍රිකෝණය (පාද a, b, c)", "a + b + c", "1/2 × ආධාරකය × ලම්බ උස")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "භාග බෙදීමේ කෙටි රීතිය",
          mnemonicSentence = "පළමු භාගය තියාගන්න - ලකුණ ගුණ කරන්න - දෙවැන්න උඩුයටිකුරු කරන්න!",
          explanation = "Keep, Change, Flip (KCF) රීතිය.",
          appliesTo = "Fraction Division"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "2/3 ÷ 4/5 හි සරල කළ අගය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. 8/15", "2. 5/6", "3. 6/5", "4. 10/12"),
          correctAnswer = "2. 5/6",
          markingScheme = "2/3 × 5/4 = (2×5)/(3×4) = 10/12 = 5/6. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
    ),

    SyllabusUnitItem(
      id = "g07_hist_u1",
      grade = "07",
      subject = "ඉතිහාසය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "අනුරාධපුර රාජධානියේ ස්වර්ණමය යුගය",
      unitTitleEnglish = "Golden Era of Anuradhapura Kingdom",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "දේවානම්පියතිස්ස රජු (ක්‍රි.පූ. 3 වන සියවස): මහින්දාගමනය සිදුවීම, ශ්‍රී මහා බෝධීන් වහන්සේ වැඩමවීම, ථූපාරාමය හා තිස්ස වැව ඉදි කිරීම.",
        "දුටුගැමුණු රජු (ක්‍රි.පූ. 161 - 137): එළාර පරාජය කර රට එක්සේසත් කිරීම, රුවන්වැලි සෑය (මහාථූපය), මිරිසවැටිය හා ලෝවාමහාපාය ඉදිකිරීම.",
        "වළගම්බා රජු: සත්ද්‍රවිඩ ආක්‍රමණ පරාජය කිරීම, අභයගිරි විහාරය ඉදිකිරීම, මාතලේ අලුවිහාරයේදී ත්‍රිපිටකය ග්‍රන්ථාරූඪ කිරීම.",
        "ධාතුසේන රජු: කලා වැව සහ යෝධ ඇළ නිර්මාණය කිරීම."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "අනුරාධපුර යුගයේ රජවරුන්ගේ ඓතිහාසික මෙහෙවර",
          header1 = "රජතුමා",
          header2 = "ආගමික හා සංස්කෘතික ස්මාරක",
          header3 = "වාරි කර්මාන්ත",
          rows = listOf(
            TableRowData("දේවානම්පියතිස්ස", "ථූපාරාමය, මහමෙව්නා උයන, ඉසුරුමුණිය", "තිස්ස වැව"),
            TableRowData("දුටුගැමුණු", "රුවන්වැලිසෑය, මිරිසවැටිය, ලෝවාමහාපාය", "රට එක්සේසත් කිරීම හා ගොවිතැන දියුණු කිරීම"),
            TableRowData("වළගම්බා", "අභයගිරිය, ත්‍රිපිටකය ග්‍රන්ථාරූඪ කිරීම", "ආක්‍රමණිකයන් පලවා හැරීම"),
            TableRowData("ධාතුසේන", "අවුකන බුදු පිළිමය", "කලා වැව, ජය ගඟ (යෝධ ඇළ)")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "අනුරාධපුර රජවරුන්ගේ ප්‍රධාන ස්මාරක මතකය",
          mnemonicSentence = "තිස්සට ථූපාරාම - ගැමුණුට රුවන්වැලි - වළගම්බාට අභයගිරි - ධාතුසේනට කලා වැව!",
          explanation = "ප්‍රධාන රජවරු සහ ඔවුන්ගේ අග්‍රගණ්‍ය නිර්මාණ පහසුවෙන් මතක තබා ගන්න.",
          appliesTo = "Anuradhapura Kings & Monuments"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ශ්‍රී ලංකාවේ මුල්වරට ත්‍රිපිටක ධර්මය මාතලේ අලුවිහාරයේදී ග්‍රන්ථාරූඪ කළේ කුමන රජුගේ සමයේද?",
          type = "MCQ",
          options = listOf("1. දේවානම්පියතිස්ස රජු", "2. දුටුගැමුණු රජු", "3. වළගම්බා රජු", "4. මහසෙන් රජු"),
          correctAnswer = "3. වළගම්බා රජු",
          markingScheme = "වළගම්බා රජුගේ රාජ්‍ය සමයේදී මාතලේ අලුවිහාරයේදී ත්‍රිපිටකය ග්‍රන්ථාරූඪ විය. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview"
    ),

    // --------------------------------------------------------------------------
    // GRADE 06 COMPREHENSIVE SYLLABUS UNITS (06 ශ්‍රේණිය පූර්ණ විෂය නිර්දේශ ඒකක)
    // --------------------------------------------------------------------------
    SyllabusUnitItem(
      id = "g06_sci_u1",
      grade = "06",
      subject = "විද්‍යාව",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "අප අවට පරිසරයේ ඇති ද්‍රව්‍ය, ශක්තිය හා ජීවී ලෝකය",
      unitTitleEnglish = "Materials, Energy & Living World Around Us",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ද්‍රව්‍ය පවතින ප්‍රධාන භෞතික අවස්ථා 3: ඝන, ද්‍රව, වායු.",
        "ඝන ද්‍රව්‍ය: නිශ්චිත හැඩයක් සහ නිශ්චිත පරිමාවක් ඇත (ගල්, ලී, යකඩ).",
        "ද්‍රව ද්‍රව්‍ය: නිශ්චිත පරිමාවක් ඇති නමුත් නිශ්චිත හැඩයක් නැත - අඩංගු බඳුනේ හැඩය ගනී (ජලය, තෙල්).",
        "වායු ද්‍රව්‍ය: නිශ්චිත හැඩයක් හෝ නිශ්චිත පරිමාවක් නැත (වාතය, ඔක්සිජන්).",
        "ජීවී ලක්ෂණ 7: වර්ධනය, පෝෂණය, චලනය, ශ්වසනය, ප්‍රජනනය, උද්දීප්‍යතාව සහ බහිස්ස්‍රාවය.",
        "ශක්ති ප්‍රභව: සූර්ය ශක්තිය, ජෛව ස්කන්ධ, සුළං ශක්තිය, ජල විදුලිය, ෆොසිල ඉන්ධන."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ඝන, ද්‍රව, වායු භෞතික අවස්ථා 3 සංසන්දනය",
          header1 = "ලක්ෂණය",
          header2 = "ඝන (Solids)",
          header3 = "ද්‍රව (Liquids) & වායු (Gases)",
          rows = listOf(
            TableRowData("හැඩය", "නිශ්චිත හැඩයක් ඇත", "ද්‍රව: බඳුනේ හැඩය ගනී • වායු: හැඩයක් නැත"),
            TableRowData("පරිමාව", "නිශ්චිත පරිමාවක් ඇත", "ද්‍රව: නිශ්චිත පරිමාවක් ඇත • වායු: පරිමාවක් නැත"),
            TableRowData("අංශු සැකැස්ම", "ඉතා තදින් අසුරා ඇත", "ද්‍රව: ලිහිල්ව චලනය වේ • වායු: නිදැල්ලේ චලනය වේ"),
            TableRowData("උදාහරණ", "ගල්, යකඩ, ලී", "ජලය, කිරි • වාතය, ඔක්සිජන්")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ජීවී ලක්ෂණ 7 මතක තබා ගන්නා කෙටි ක්‍රමය",
          mnemonicSentence = "වැඩෙයි - කයි - දුවයි - හුස්ම ගනියි - පැටව් ගසයි - දැනෙයි - මළපහ කරයි!",
          explanation = "වර්ධනය, පෝෂණය, චලනය, ශ්වසනය, ප්‍රජනනය, උද්දීප්‍යතාව, බහිස්ස්‍රාවය.",
          appliesTo = "7 Characteristics of Living Organisms"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "නිශ්චිත පරිමාවක් තිබුණද නිශ්චිත හැඩයක් නොමැති ද්‍රව්‍ය අයත් වන්නේ කුමන අවස්ථාවටද?",
          type = "MCQ",
          options = listOf("1. ඝන", "2. ද්‍රව", "3. වායු", "4. ප්ලාස්මා"),
          correctAnswer = "2. ද්‍රව",
          markingScheme = "ද්‍රවවලට නිශ්චිත පරිමාවක් ඇති නමුත් නිශ්චිත හැඩයක් නැත. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1IQntv3Yh1Oaxh42-btqYaFNNA9uijfx_/preview"
    ),

    SyllabusUnitItem(
      id = "g06_math_u1",
      grade = "06",
      subject = "ගණිතය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "නිඛිල, කෝණ, පරිමිතිය හා දත්ත නිරූපණය",
      unitTitleEnglish = "Integers, Angles, Perimeter & Data Representation",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "සංඛ්‍යා රේඛාව සහ නිඛිල: ධන නිඛිල (+1, +2, +3...), ශුන්‍යය (0), සහ ඍණ නිඛිල (-1, -2, -3...).",
        "කෝණ වර්ග: සුළු කෝණය (0° ට වැඩි, 90° ට අඩු), ඍජුකෝණය (90° ට හරියටම සමානයි), මහා කෝණය (90° ට වැඩි, 180° ට අඩු), සරල කෝණය (180°), පරාවර්තී කෝණය (180° ට වැඩි, 360° ට අඩු).",
        "පරිමිතිය: ඕනෑම තල රූපයක වට මායිමේ ඇති සියලුම පාදවල දිග එකතුවයි.",
        "දත්ත නිරූපණය: ගණන් කිරීමේ සලකුණු (Tally marks), රූපාක්ෂර (Pictographs), සහ තීරු ප්‍රස්ථාර (Bar charts)."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "කෝණ වර්ග සංසන්දනය",
          header1 = "කෝණ වර්ගය",
          header2 = "අංශක පරාසය",
          header3 = "උදාහරණ අගයක්",
          rows = listOf(
            TableRowData("සුළු කෝණය (Acute)", "0° < θ < 90°", "45°, 60°"),
            TableRowData("ඍජුකෝණය (Right angle)", "θ = 90°", "90° (L අකුරේ හැඩය)"),
            TableRowData("මහා කෝණය (Obtuse)", "90° < θ < 180°", "120°, 150°"),
            TableRowData("සරල කෝණය (Straight)", "θ = 180°", "180° (සරල රේඛාවක්)"),
            TableRowData("පරාවර්තී කෝණය (Reflex)", "180° < θ < 360°", "240°, 300°")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "කෝණ වර්ග පහසුවෙන් මතක තබා ගැනීම",
          mnemonicSentence = "සුළු (කුඩායි) → ඍජු (හරියටම 90) → මහා (විශාලයි) → සරල (කෙළින් රේඛාව)!",
          explanation = "0°-90° සුළු, 90° ඍජු, 90°-180° මහා, 180° සරල.",
          appliesTo = "Types of Angles"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "අංශක 125° ක කෝණයක් අයත් වන කෝණ වර්ගය කුමක්ද?",
          type = "MCQ",
          options = listOf("1. සුළු කෝණය", "2. ඍජුකෝණය", "3. මහා කෝණය", "4. පරාවර්තී කෝණය"),
          correctAnswer = "3. මහා කෝණය",
          markingScheme = "90° ට වැඩි සහ 180° ට අඩු කෝණ මහා කෝණ ලෙස හැඳින්වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1V3y65z_15X6zjruQ_I11WhG4EOfDHGm-/preview"
    ),

    SyllabusUnitItem(
      id = "g06_hist_u1",
      grade = "06",
      subject = "ඉතිහාසය",
      unitNumber = "01 වන පාඩම",
      unitTitleSinhala = "ඉතිහාසය හැදෑරීමේ මූලාශ්‍ර සහ ප්‍රාග් ඓතිහාසික යුගය",
      unitTitleEnglish = "Historical Sources & Prehistoric Era of Sri Lanka",
      term = "1 වන වාරය",
      summaryNotes = listOf(
        "ඉතිහාස මූලාශ්‍ර ප්‍රධාන කාණ්ඩ 2කි: සාහිත්‍ය මූලාශ්‍ර සහ පුරාවිද්‍යාත්මක මූලාශ්‍ර.",
        "දේශීය සාහිත්‍ය මූලාශ්‍ර: දීපවංශය, මහාවංශය, චූලවංශය, රාජාවලිය, පූජාවලිය.",
        "විදේශීය සාහිත්‍ය මූලාශ්‍ර: හියුංසාං, පාහියන් හිමිගේ වාර්තා, ටොලමිගේ සිතියම, රොබට් නොක්ස්ගේ 'එදා හෙළදිව'.",
        "පුරාවිද්‍යාත්මක මූලාශ්‍ර: සෙල්ලිපි (ශිලා ලේඛන), කාසි, නටබුන්, කැටයම්, සිතුවම් සහ මැටි බඳුන් කැබලි.",
        "ප්‍රාග් ඓතිහාසික මානවයා (බලංගොඩ මානවයා / හෝමෝ සේපියන්ස්): පාහියන්ගල, බටදොඹලෙන, කිතුල්ගල බෙලිලෙන, බෙල්ලන්බැඳිපැලැස්ස."
      ),
      comparisonTables = listOf(
        ComparisonTable(
          title = "ඉතිහාස මූලාශ්‍ර වර්ගීකරණය",
          header1 = "මූලාශ්‍ර වර්ගය",
          header2 = "විස්තරය",
          header3 = "ප්‍රධාන උදාහරණ",
          rows = listOf(
            TableRowData("දේශීය සාහිත්‍ය", "ශ්‍රී ලාංකිකයන් විසින් ලියන ලද ග්‍රන්ථ", "මහාවංශය, දීපවංශය, ථූපවංශය"),
            TableRowData("විදේශීය සාහිත්‍ය", "විදේශික සංචාරකයින් විසින් ලියූ වාර්තා", "පාහියන් වාර්තා, රොබට් නොක්ස්ගේ වාර්තාව"),
            TableRowData("පුරාවිද්‍යාත්මක", "භෞතිකව හමුවන පුරාණ අවශේෂ", "සෙල්ලිපි, කාසි, රුවන්වැලිසෑය, සීගිරිය")
          )
        )
      ),
      memoryTricks = listOf(
        MemoryTrick(
          title = "ශ්‍රී ලංකාවේ පැරණිතම වංසකතා 2 මතක තබා ගැනීම",
          mnemonicSentence = "දීපෙන් පටන්ගෙන මහාවංශයට ආවා!",
          explanation = "පළමුවෙන්ම ලියවුණේ දීපවංශයයි. ඉන්පසු මහානාම හිමියන් විසින් මහාවංශය රචනා කරන ලදී.",
          appliesTo = "Ancient Chronicles of Sri Lanka"
        )
      ),
      practiceQuestions = listOf(
        UnitQuestion(
          questionNumber = 1,
          questionText = "ශ්‍රී ලංකාවේ දැනට හමුවී ඇති පැරණිතම වංසකථාව කුමක්ද?",
          type = "MCQ",
          options = listOf("1. මහාවංශය", "2. දීපවංශය", "3. චූලවංශය", "4. පූජාවලිය"),
          correctAnswer = "2. දීපවංශය",
          markingScheme = "දීපවංශය ශ්‍රී ලංකාවේ පැරණිතම වංසකථාව වේ. (ලකුණු 2)",
          marksAllocated = 2
        )
      ),
      defaultDrivePdfUrl = "https://drive.google.com/file/d/1cQvoqODfVR6aBWLTO4JGEWVOBnf3C_4J/preview"
    )
  )

  fun getUnitsForGradeAndSubject(grade: String, subject: String): List<SyllabusUnitItem> {
    val gradeUnits = allSyllabusUnits.filter { it.grade == grade }
    val matchSubject = gradeUnits.filter { 
      it.subject.equals(subject, ignoreCase = true) || 
      it.subject.contains(subject, ignoreCase = true) ||
      subject.contains(it.subject, ignoreCase = true)
    }
    return if (matchSubject.isNotEmpty()) matchSubject else gradeUnits
  }

  fun attachDrivePdfToUnit(unitId: String, title: String, driveUrl: String, type: String = "NOTE"): Boolean {
    val target = allSyllabusUnits.find { it.id == unitId }
    if (target != null) {
      val embedUrl = formatToGoogleDriveEmbedUrl(driveUrl)
      target.attachedDrivePdfs.add(
        0,
        AttachedGoogleDrivePdf(
          id = System.currentTimeMillis().toString(),
          title = title,
          driveUrl = embedUrl,
          type = type
        )
      )
      return true
    }
    return false
  }
}

// ==============================================================================
// SYLLABUS DETECTION & CONTENT HUB SCREEN
// ==============================================================================

@Composable
fun SyllabusDetectionAndContentScreen(
  initialGrade: String = "11",
  initialSubject: String = "විද්‍යාව",
  isApproved: Boolean = false,
  onRequireApproval: () -> Unit = {},
  onBack: () -> Unit,
  onOpenGoogleDrivePdfModal: (url: String, title: String) -> Unit
) {
  val context = LocalContext.current
  var selectedGrade by remember { mutableStateOf(initialGrade) }
  var selectedSubject by remember { mutableStateOf(initialSubject) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedTabContent by remember { mutableStateOf(0) } // 0: Notes & Summary, 1: Markdown Tables, 2: Memory Tricks, 3: Papers & Marking Schemes, 4: Google Drive PDFs

  // Add Google Drive PDF Modal state
  var showAttachDrivePdfDialog by remember { mutableStateOf(false) }
  var inputPdfTitle by remember { mutableStateOf("") }
  var inputDriveUrl by remember { mutableStateOf("") }
  var targetUnitForUpload by remember { mutableStateOf<SyllabusUnitItem?>(null) }

  val gradeList = listOf("11", "10", "09", "08", "07", "06")
  val subjectsForGrade = when (selectedGrade) {
    "11", "10" -> listOf("විද්‍යාව", "ගණිතය", "ඉතිහාසය", "English", "සිංහල", "ICT", "බුද්ධ ධර්මය", "භූගෝල විද්‍යාව", "පුරවැසි අධ්‍යාපනය", "නර්තනය", "සංගීතය", "ව්‍යාපාර හා ගිණුම්කරණය")
    else -> listOf("විද්‍යාව", "ගණිතය", "ඉතිහාසය", "English", "සිංහල", "බුද්ධ ධර්මය", "භූගෝල විද්‍යාව", "පුරවැසි අධ්‍යාපනය", "නර්තනය", "සංගීතය", "සෞඛ්‍යය")
  }

  // Ensure valid subject when grade changes
  LaunchedEffect(selectedGrade) {
    if (!subjectsForGrade.contains(selectedSubject)) {
      selectedSubject = subjectsForGrade.first()
    }
  }

  val units = remember(selectedGrade, selectedSubject, searchQuery) {
    val list = SyllabusRepository.getUnitsForGradeAndSubject(selectedGrade, selectedSubject)
    if (searchQuery.isBlank()) list else {
      list.filter {
        it.unitTitleSinhala.contains(searchQuery, ignoreCase = true) ||
        it.unitTitleEnglish.contains(searchQuery, ignoreCase = true) ||
        it.unitNumber.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  var selectedUnit by remember(units) { mutableStateOf(units.firstOrNull()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Header Bar
    Surface(
      color = Color(0xFF1E1B4B),
      shadowElevation = 4.dp,
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
              color = Color(0xFF312E81)
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
              Text(
                text = "📚 Syllabus Detection & Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "ශ්‍රී ලංකා විෂය නිර්දේශයේ පාඩම්, වගු, කෙටි ක්‍රම & Google Drive PDFs",
                fontSize = 10.sp,
                color = Color(0xFFC7D2FE)
              )
            }
          }

          // Upload / Attach Google Drive Button
          FilledTonalButton(
            onClick = {
              if (!isApproved) {
                Toast.makeText(context, "🔒 Drive PDF ගොනු එක් කිරීමට ඇඩ්මින් අනුමැතිය අවශ්‍ය වේ.", Toast.LENGTH_SHORT).show()
                onRequireApproval()
              } else {
                targetUnitForUpload = selectedUnit
                inputPdfTitle = ""
                inputDriveUrl = ""
                showAttachDrivePdfDialog = true
              }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = Color(0xFF4338CA),
              contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.AddLink, contentDescription = "Add Drive PDF", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Drive PDF එකතු කරන්න", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Security & Approval Status Strip
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isApproved) Color(0xFF064E3B).copy(alpha = 0.6f) else Color(0xFF7F1D1D).copy(alpha = 0.6f),
          border = BorderStroke(1.dp, if (isApproved) Color(0xFF059669) else Color(0xFFDC2626)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(if (isApproved) "🛡️" else "🔒", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isApproved) "ආරක්ෂිත අධ්‍යාපන කලාපය • Screen Record, Screenshots & Downloads අවහිරයි" else "නොමිලේ පූර්වදර්ශනය • PDF & Marking Scheme සඳහා ඇඩ්මින් අනුමැතිය අවශ්‍යයි",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
              )
            }
            if (!isApproved) {
              TextButton(
                onClick = onRequireApproval,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
              ) {
                Text("අනුමැතිය ගන්න", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grade Selector Tabs
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(gradeList) { g ->
            val isSelected = selectedGrade == g
            Surface(
              onClick = {
                selectedGrade = g
                selectedUnit = null
              },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color(0xFF6366F1) else Color(0xFF312E81),
              border = BorderStroke(1.dp, if (isSelected) Color(0xFFA5B4FC) else Color(0xFF3730A3))
            ) {
              Text(
                text = "$g ශ්‍රේණිය",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subject Selector Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(subjectsForGrade) { sub ->
            val isSelected = selectedSubject == sub
            Surface(
              onClick = {
                selectedSubject = sub
                selectedUnit = null
              },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color.White else Color(0xFF312E81).copy(alpha = 0.6f)
            ) {
              Text(
                text = sub,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF1E1B4B) else Color(0xFFE0E7FF),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      }
    }

    // Search and Unit Sequence Bar
    Surface(
      color = Color.White,
      shadowElevation = 1.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("පාඩම හෝ මාතෘකාව සෙවීම (Search Lessons)...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B)) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4F46E5),
            unfocusedBorderColor = Color(0xFFE2E8F0)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Detected Unit Sequence Chips
        Text(
          text = "📌 $selectedGrade ශ්‍රේණිය $selectedSubject අධ්‍යාපන දෙපාර්තමේන්තු පාඩම් මාලාව (Unit Sequence):",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF4338CA)
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(units) { u ->
            val isCurrent = (selectedUnit?.id == u.id) || (selectedUnit == null && units.firstOrNull()?.id == u.id)
            Surface(
              onClick = { selectedUnit = u },
              shape = RoundedCornerShape(8.dp),
              color = if (isCurrent) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, if (isCurrent) Color(0xFF6366F1) else Color(0xFFCBD5E1))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = u.unitNumber,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) Color(0xFF4338CA) else Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = u.unitTitleSinhala,
                  fontSize = 11.sp,
                  fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                  color = if (isCurrent) Color(0xFF1E1B4B) else Color(0xFF334155),
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    }

    // Active Unit Content & Tabs Section
    val currentActiveUnit = selectedUnit ?: units.firstOrNull()

    if (currentActiveUnit == null) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "මෙම ශ්‍රේණිය හා විෂය සඳහා පාඩම් සටහන් සූදානම් වෙමින් පවතී...",
          color = Color(0xFF64748B),
          fontSize = 13.sp
        )
      }
    } else {
      // Content Category Tabs
      TabRow(
        selectedTabIndex = selectedTabContent,
        containerColor = Color.White,
        contentColor = Color(0xFF4F46E5),
        modifier = Modifier.fillMaxWidth()
      ) {
        listOf(
          "📖 සටහන්",
          "📊 වගු",
          "💡 කෙටි ක්‍රම",
          "📝 ප්‍රශ්න & Marking",
          "📄 Drive PDFs"
        ).forEachIndexed { index, title ->
          Tab(
            selected = selectedTabContent == index,
            onClick = { selectedTabContent = index },
            text = {
              Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (selectedTabContent == index) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
              )
            }
          )
        }
      }

      // Tab Content Body
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
      ) {
        // Active Unit Hero Banner
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
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
                  color = Color(0xFF6366F1)
                ) {
                  Text(
                    text = "${currentActiveUnit.grade} ශ්‍රේණිය • ${currentActiveUnit.unitNumber} • ${currentActiveUnit.term}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFF10B981).copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                  Text(
                    text = "Official Syllabus Unit",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = currentActiveUnit.unitTitleSinhala,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )

              Text(
                text = currentActiveUnit.unitTitleEnglish,
                fontSize = 12.sp,
                color = Color(0xFFA5B4FC)
              )
            }
          }
        }

        when (selectedTabContent) {
          0 -> {
            // COMPREHENSIVE SHORT NOTES (මුළු පාඩමම ආවරණය වන සවිස්තර කෙටි සටහන්)
            item {
              Text(
                text = "📖 මුළු පාඩමම ආවරණය වන ප්‍රධාන කරුණු (Complete Lesson Summary):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }

            items(currentActiveUnit.summaryNotes) { note ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .size(20.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF4F46E5)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(12.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = note,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 19.sp
                  )
                }
              }
            }
          }

          1 -> {
            // MARKDOWN COMPARISON TABLES (සංසන්දනාත්මක වගු)
            item {
              Text(
                text = "📊 සංසන්දනාත්මක වගු (Comparison Tables):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }

            if (currentActiveUnit.comparisonTables.isEmpty()) {
              item {
                Text(
                  text = "මෙම පාඩම සඳහා සංසන්දනාත්මක වගු සූදානම් වෙමින් පවතී...",
                  fontSize = 12.sp,
                  color = Color(0xFF64748B)
                )
              }
            } else {
              items(currentActiveUnit.comparisonTables) { table ->
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                      text = table.title,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF4338CA)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Table Header
                    Surface(
                      color = Color(0xFFEEF2FF),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = table.header1,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF312E81),
                          modifier = Modifier.weight(1f)
                        )
                        Text(
                          text = table.header2,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF312E81),
                          modifier = Modifier.weight(1.2f)
                        )
                        if (table.header3.isNotEmpty()) {
                          Text(
                            text = table.header3,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF312E81),
                            modifier = Modifier.weight(1.2f)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Table Rows
                    table.rows.forEachIndexed { idx, row ->
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(if (idx % 2 == 0) Color.White else Color(0xFFF8FAFC))
                          .padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = row.column1,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = Color(0xFF1E293B),
                          modifier = Modifier.weight(1f)
                        )
                        Text(
                          text = row.column2,
                          fontSize = 11.sp,
                          color = Color(0xFF334155),
                          modifier = Modifier.weight(1.2f)
                        )
                        if (table.header3.isNotEmpty()) {
                          Text(
                            text = row.column3,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.weight(1.2f)
                          )
                        }
                      }
                      HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                  }
                }
              }
            }
          }

          2 -> {
            // MEMORY TRICKS & MNEMONICS (මතක තබා ගැනීමේ කෙටි ක්‍රම)
            item {
              Text(
                text = "💡 මතක තබා ගැනීමේ කෙටි ක්‍රම (Memory Tricks & Mnemonics):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }

            items(currentActiveUnit.memoryTricks) { trick ->
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = trick.title,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF92400E)
                    )

                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color(0xFFFEF3C7)
                    ) {
                      Text(
                        text = trick.appliesTo,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Text(
                        text = "🎯 කෙටි වාක්‍යය (Mnemonic):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "\"${trick.mnemonicSentence}\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "විස්තරය: ${trick.explanation}",
                    fontSize = 12.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 17.sp
                  )
                }
              }
            }
          }

          3 -> {
            // UNIT PRACTICE PAPERS & MARKING SCHEMES (ප්‍රශ්න පත්‍ර & Marking Schemes)
            item {
              Text(
                text = "📝 ප්‍රශ්න පත්‍රය සහ විස්තරාත්මක Marking Scheme:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }

            items(currentActiveUnit.practiceQuestions) { q ->
              var showMarkingScheme by remember { mutableStateOf(false) }

              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                      color = Color(0xFFEEF2FF)
                    ) {
                      Text(
                        text = "ප්‍රශ්න අංක 0${q.questionNumber} • ${q.type}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }

                    Text(
                      text = "ලකුණු [ ${q.marksAllocated} ]",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF15803D)
                    )
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Text(
                    text = q.questionText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    lineHeight = 20.sp
                  )

                  if (q.options.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    q.options.forEach { opt ->
                      Text(
                        text = opt,
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // Marking Scheme Toggle Button
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = if (showMarkingScheme) "✅ පිළිතුරු හා ලකුණු ක්‍රමය:" else "නිවැරදි පිළිතුර සහ Marking Scheme බලන්න",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (showMarkingScheme) Color(0xFF15803D) else Color(0xFF64748B)
                    )

                    Button(
                      onClick = {
                        if (!isApproved) {
                          Toast.makeText(context, "🔒 විස්තරාත්මක ලකුණු ක්‍රමවේදය (Marking Scheme) නැරඹීමට ඇඩ්මින් අනුමැතිය අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
                          onRequireApproval()
                        } else {
                          showMarkingScheme = !showMarkingScheme
                        }
                      },
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (showMarkingScheme) Color(0xFFF1F5F9) else Color(0xFF4F46E5),
                        contentColor = if (showMarkingScheme) Color(0xFF334155) else Color.White
                      ),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                      Text(
                        text = if (showMarkingScheme) "සඟවන්න" else "Marking Scheme",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  if (showMarkingScheme) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = Color(0xFFF0FDF4),
                      border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                          text = "🎯 නිවැරදි පිළිතුර: ${q.correctAnswer}",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF166534)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = "📋 ලකුණු ලබාදීමේ පටිපාටිය (Marking Breakdown):",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF15803D)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                          text = q.markingScheme,
                          fontSize = 11.sp,
                          color = Color(0xFF14532D),
                          lineHeight = 16.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          4 -> {
            // GOOGLE DRIVE PDFS (ගූගල් ඩ්‍රයිව් පීඩීඑෆ් සෘජුව විවෘත කිරීම)
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "📄 අදාළ Google Drive PDFs (කෙටි සටහන් & පත්‍ර):",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E293B)
                )

                TextButton(
                  onClick = {
                    targetUnitForUpload = currentActiveUnit
                    inputPdfTitle = ""
                    inputDriveUrl = ""
                    showAttachDrivePdfDialog = true
                  }
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("නව Drive PDF එකතු කරන්න", fontSize = 11.sp)
                }
              }
            }

            // Default Official Unit PDF
            item {
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    if (!isApproved) {
                      Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් (PDF) සටහන් නැරඹීමට ඇඩ්මින් අනුමැතිය අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
                      onRequireApproval()
                    } else {
                      onOpenGoogleDrivePdfModal(
                        currentActiveUnit.defaultDrivePdfUrl,
                        "${currentActiveUnit.unitTitleSinhala} (Google Drive PDF)"
                      )
                    }
                  }
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "${currentActiveUnit.unitNumber} සම්පූර්ණ කෙටි සටහන් PDF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                      )
                      Text(
                        text = "Google Drive Cloud • 2.4 MB • නිල විෂය නිර්දේශය",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                      )
                    }
                  }

                  Button(
                    onClick = {
                      if (!isApproved) {
                        Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් (PDF) සටහන් නැරඹීමට ඇඩ්මින් අනුමැතිය අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
                        onRequireApproval()
                      } else {
                        onOpenGoogleDrivePdfModal(
                          currentActiveUnit.defaultDrivePdfUrl,
                          "${currentActiveUnit.unitTitleSinhala} (Google Drive PDF)"
                        )
                      }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.PictureAsPdf,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF කියවන්න", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            // Attached Custom Uploaded Google Drive PDFs
            if (currentActiveUnit.attachedDrivePdfs.isNotEmpty()) {
              items(currentActiveUnit.attachedDrivePdfs) { attachedPdf ->
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
                  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      if (!isApproved) {
                        Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් (PDF) සටහන් නැරඹීමට ඇඩ්මින් අනුමැතිය අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
                        onRequireApproval()
                      } else {
                        onOpenGoogleDrivePdfModal(
                          attachedPdf.driveUrl,
                          "${attachedPdf.title} (Google Drive)"
                        )
                      }
                    }
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
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
                          .clip(CircleShape)
                          .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = Icons.Default.CloudDownload,
                          contentDescription = "Drive Upload",
                          tint = Color(0xFF2563EB),
                          modifier = Modifier.size(24.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          text = attachedPdf.title,
                          fontSize = 13.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF0F172A)
                        )
                        Text(
                          text = "පරිශීලක Drive PDF • ${attachedPdf.fileSize} • ${attachedPdf.uploadDate}",
                          fontSize = 10.sp,
                          color = Color(0xFF2563EB)
                        )
                      }
                    }

                    Button(
                      onClick = {
                        if (!isApproved) {
                          Toast.makeText(context, "🔒 මෙම පීඩීඑෆ් (PDF) සටහන් නැරඹීමට ඇඩ්මින් අනුමැතිය අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
                          onRequireApproval()
                        } else {
                          onOpenGoogleDrivePdfModal(
                            attachedPdf.driveUrl,
                            "${attachedPdf.title} (Google Drive)"
                          )
                        }
                      },
                      shape = RoundedCornerShape(10.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Drive එකෙන් කියවන්න", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

  // Dialog to attach / upload a new Google Drive PDF to this exact lesson
  if (showAttachDrivePdfDialog) {
    AlertDialog(
      onDismissRequest = { showAttachDrivePdfDialog = false },
      title = {
        Text(
          text = "📄 ඔබගේ Google Drive PDF එකතු කරන්න",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E1B4B)
        )
      },
      text = {
        Column {
          Text(
            text = "අදාළ පාඩම: ${targetUnitForUpload?.unitNumber} - ${targetUnitForUpload?.unitTitleSinhala}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4338CA)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "ඔබගේ Google Drive එකේ ඇති PDF එකේ Shareable Link එක මෙතැනට ඇතුළත් කරන්න. එය ඇප් එක තුළින්ම සෘජුවම විවෘත වේ.",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = inputPdfTitle,
            onValueChange = { inputPdfTitle = it },
            label = { Text("PDF ලේඛනයේ නම (Title)") },
            placeholder = { Text("උදා: 1 වන පාඩම කෙටි සටහන් සම්පූර්ණ PDF") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = inputDriveUrl,
            onValueChange = { inputDriveUrl = it },
            label = { Text("Google Drive Link (URL)") },
            placeholder = { Text("https://drive.google.com/file/d/...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (inputPdfTitle.isNotBlank() && inputDriveUrl.isNotBlank() && targetUnitForUpload != null) {
              val success = SyllabusRepository.attachDrivePdfToUnit(
                unitId = targetUnitForUpload!!.id,
                title = inputPdfTitle,
                driveUrl = inputDriveUrl
              )
              if (success) {
                Toast.makeText(context, "Google Drive PDF සාර්ථකව එක් කරන ලදී!", Toast.LENGTH_LONG).show()
                showAttachDrivePdfDialog = false
              }
            } else {
              Toast.makeText(context, "කරුණාකර නම සහ Drive Link එක ඇතුළත් කරන්න", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA))
        ) {
          Icon(Icons.Default.CloudUpload, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("PDF අමුණන්න (Attach)")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAttachDrivePdfDialog = false }) {
          Text("අවලංගුයි")
        }
      }
    )
  }
}
