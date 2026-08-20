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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
// 1. DATA MODELS & JSON SCHEMA FOR CHAPTERS & AUDIO
// --------------------------------------------------

data class ChapterItem(
  val chapter_id: Int,
  val title: String,
  val page_start: Int,
  val page_end: Int,
  val audio_note_url: String,
  val audio_duration: String,
  val summary_bullets: List<String>,
  val read_aloud_text: String
)

data class SubjectHubData(
  val grade: String,
  val subject: String,
  val short_notes_pdf_url: String,
  val chapters: List<ChapterItem>,
  val quiz_sets: List<QuizSet>
)

data class ActiveAudioState(
  val chapter: ChapterItem,
  val grade: String,
  val subject: String,
  val pdfUrl: String,
  val isPlaying: Boolean = true,
  val speed: Float = 1.0f,
  val progress: Float = 0.0f
)

// --------------------------------------------------
// 2. CHAPTER REPOSITORY (100+ Pages Indexed Data)
// --------------------------------------------------

object ChapterRepository {

  fun getSubjectHubData(grade: String, subjectName: String, pdfUrl: String = ""): SubjectHubData {
    val norm = normalizeSubjectKey(subjectName)
    val chapters = getChaptersForSubject(grade, subjectName, norm)
    val quizData = QuizRepository.getQuizDataForSubject(grade, subjectName, pdfUrl)

    return SubjectHubData(
      grade = grade,
      subject = subjectName,
      short_notes_pdf_url = if (pdfUrl.isNotBlank()) pdfUrl else "https://drive.google.com/file/d/1sample_100page_notes/preview",
      chapters = chapters,
      quiz_sets = quizData.quiz_sets
    )
  }

  fun getChaptersForSubject(grade: String, subjectName: String, norm: String): List<ChapterItem> {
    return when (norm) {
      "science" -> listOf(
        ChapterItem(
          chapter_id = 1,
          title = "1 වන පරිච්ඡේදය: ජීවීන්ගේ ලක්ෂණ හා සෛලීය සංවිධානය",
          page_start = 1,
          page_end = 18,
          audio_note_url = "https://example.com/audio_sci_ch1.mp3",
          audio_duration = "04:15",
          summary_bullets = listOf(
            "සෛල වාදය: සියලුම ජීවීන් එක් සෛලයකින් හෝ සෛල කිහිපයකින් සමන්විත වේ.",
            "ප්ලාස්ම පටලය අර්ධ පාරගම්‍ය වන අතර පාලිතව ද්‍රව්‍ය හුවමාරු කරයි.",
            "මයිටොකොන්ඩ්‍රියා සෛලයේ ශක්ති බලාගාරය ලෙස හැඳින්වේ (ATP නිෂ්පාදනය).",
            "ශාක සෛලවල පමණක් සෙලියුලෝස් සෛල බිත්තියක් හා හරිතලව ඇත.",
            "න්‍යෂ්ටිය සෛලයේ සියලු ජෛව ක්‍රියා පාලනය කරන ප්‍රධාන ඉන්ද්‍රයිකාවයි."
          ),
          read_aloud_text = "ජීවීන්ගේ මූලික ව්‍යුහාත්මක හා ක්‍රියාකාරී ඒකකය සෛලයයි. සෛල තුළ සෛල පටලය, සෛල ප්ලාස්මය සහ න්‍යෂ්ටිය ප්‍රධාන කොටස් වේ. ශාක සෛල සහ සත්ත්ව සෛල අතර ප්‍රධාන වෙනස්කම් කිහිපයකි. ශාක සෛල සතුව හරිතලව, සෛල බිත්තිය සහ විශාල මධ්‍ය රික්තකයක් පවතී."
        ),
        ChapterItem(
          chapter_id = 2,
          title = "2 වන පරිච්ඡේදය: පදාර්ථයේ ව්‍යුහය, පරමාණු හා ආවර්තිතා වගුව",
          page_start = 19,
          page_end = 36,
          audio_note_url = "https://example.com/audio_sci_ch2.mp3",
          audio_duration = "04:50",
          summary_bullets = listOf(
            "පරමාණුවක ප්‍රෝටෝන, නියුට්‍රෝන (න්‍යෂ්ටියේ) සහ ඉලෙක්ට්‍රෝන (කවචවල) පවතී.",
            "පරමාණුක ක්‍රමාංකය (Z) = ප්‍රෝටෝන ගණන.",
            "ස්කන්ධ ක්‍රමාංකය (A) = ප්‍රෝටෝන ගණන + නියුට්‍රෝන ගණන.",
            "ආවර්ත යනු තිරස් පේළි වන අතර කාණ්ඩ යනු සිරස් තීරු වේ.",
            "ඉලෙක්ට්‍රෝන වින්‍යාසය 2, 8, 8, 2 ආදී වශයෙන් කවචවල පිරේ."
          ),
          read_aloud_text = "පදාර්ථය සෑදී ඇත්තේ පරමාණු වලිනි. පරමාණුවේ කේන්ද්‍රයේ ධන ආරෝපිත න්‍යෂ්ටිය පිහිටා ඇති අතර ඒ වටා සෘණ ආරෝපිත ඉලෙක්ට්‍රෝන ශක්ති මට්ටම්වල භ්‍රමණය වේ. ආවර්තිතා වගුවේ මුල් මූලද්‍රව්‍ය 20 හි ගුණාංග හා ඉලෙක්ට්‍රෝන වින්‍යාසය විභාගයට අතිශය වැදගත් වේ."
        ),
        ChapterItem(
          chapter_id = 3,
          title = "3 වන පරිච්ඡේදය: ප්‍රභාසංස්ලේෂණය සහ ශාක කායික විද්‍යාව",
          page_start = 37,
          page_end = 55,
          audio_note_url = "https://example.com/audio_sci_ch3.mp3",
          audio_duration = "03:45",
          summary_bullets = listOf(
            "සමීකරණය: 6CO₂ + 6H₂O + ආලෝකය → C₆H₁₂O₆ + 6O₂.",
            "ප්‍රධාන වර්ණකය ක්ලෝරොෆිල් (හරිතප්‍රද) වේ.",
            "දිවා කාලයේදී ආලෝක ප්‍රතික්‍රියාව සහ අඳුරු ප්‍රතික්‍රියාව සිදුවේ.",
            "නිපදවන ග්ලූකෝස් පිෂ්ඨය ලෙස ශාක තුළ තැන්පත් කෙරේ.",
            "ප්‍රභාසංස්ලේෂණයට බලපාන සාධක: ආලෝක තීව්‍රතාව, CO₂ සාන්ද්‍රණය, උෂ්ණත්වය."
          ),
          read_aloud_text = "ප්‍රභාසංස්ලේෂණය යනු හරිත ශාක සූර්යාලෝක ශක්තිය උපයෝගී කරගනිමින් කාබන් ඩයොක්සයිඩ් සහ ජලයෙන් කාබනික ආහාර නිපදවීමේ ක්‍රියාවලියයි. මෙහිදී අතුරු ඵලයක් ලෙස වායුගෝලයට ඔක්සිජන් මුදාහැරේ."
        ),
        ChapterItem(
          chapter_id = 4,
          title = "4 වන පරිච්ඡේදය: චලිතය, නිව්ටන් නියම හා බලයේ ඵල",
          page_start = 56,
          page_end = 75,
          audio_note_url = "https://example.com/audio_sci_ch4.mp3",
          audio_duration = "05:10",
          summary_bullets = listOf(
            "වේගය = දුර / කාලය (v = s/t). ප්‍රවේගය = විස්ථාපනය / කාලය.",
            "ත්වරණය = (අවසාන ප්‍රවේගය - ආරම්භක ප්‍රවේගය) / කාලය (a = (v-u)/t).",
            "නිව්ටන්ගේ 1 වන නියමය: අසමතුලිත බලයක් නොයෙදේ නම් වස්තුවක් නිශ්චලතාවයේ හෝ ඒකාකාර ප්‍රවේගයෙන් පවතී.",
            "නිව්ටන්ගේ 2 වන නියමය: F = ma (බලය = ස්කන්ධය × ත්වරණය).",
            "නිව්ටන්ගේ 3 වන නියමය: සෑම ක්‍රියාවකටම විශාලත්වයෙන් සමාන හා දිශාවෙන් ප්‍රතිවිරුද්ධ ප්‍රතික්‍රියාවක් ඇත."
          ),
          read_aloud_text = "චලිතය විස්තර කිරීමේදී දුර, විස්ථාපනය, ප්‍රවේගය සහ ත්වරණය මූලික රාශීන් වේ. නිව්ටන්ගේ නියම තුන මගින් වස්තුවල චලිතය සහ ඒ මත බලපෑම් ඇති කරන බලයන් අතර සම්බන්ධතාව මනාව පැහැදිලි කරයි."
        ),
        ChapterItem(
          chapter_id = 5,
          title = "5 වන පරිච්ඡේදය: ධාරා විද්‍යුතය, පරිපථ හා ඉලෙක්ට්‍රොනික විද්‍යාව",
          page_start = 76,
          page_end = 98,
          audio_note_url = "https://example.com/audio_sci_ch5.mp3",
          audio_duration = "04:40",
          summary_bullets = listOf(
            "ඕම්ගේ නියමය: V = IR (විභව අන්තරය = ධාරාව × ප්‍රතිරෝධය).",
            "ශ්‍රේණිගත පරිපථවල ධාරාව සමාන වන අතර විභව අන්තරය බෙදී යයි (R = R₁ + R₂).",
            "සමාන්තරගත පරිපථවල විභව අන්තරය සමාන වේ (1/R = 1/R₁ + 1/R₂).",
            "විද්‍යුත් ක්ෂමතාව P = VI = I²R = V²/R.",
            "ඩයෝඩ ධාරාව එකම දිශාවකට පමණක් ගැලීමට ඉඩ සලසයි."
          ),
          read_aloud_text = "විද්‍යුත් ධාරාව යනු ආරෝපණ ගලායාමේ සීඝ්‍රතාවයි. පරිපථ විශ්ලේෂණයේදී ඕම්ගේ නියමය අතිශය ප්‍රයෝජනවත් වේ. ගෘහස්ථ විදුලි පරිපථ සැමවිටම සමාන්තරගතව සම්බන්ධ කරනු ලබන්නේ උපකරණ ස්වාධීනව ක්‍රියා කරවීම සඳහාය."
        ),
        ChapterItem(
          chapter_id = 6,
          title = "6 වන පරිච්ඡේදය: ජාන විද්‍යාව, ප්‍රවේණිය හා පරිසර පද්ධති",
          page_start = 99,
          page_end = 120,
          audio_note_url = "https://example.com/audio_sci_ch6.mp3",
          audio_duration = "04:20",
          summary_bullets = listOf(
            "ග්‍රෙගර් මෙන්ඩල් ප්‍රවේණි විද්‍යාවේ පියා ලෙස හැඳින්වේ (මහ මල් පැළ අත්හදා බැලීම්).",
            "DNA ද්විත්ව හෙලික්සීය ව්‍යුහය ජානමය තොරතුරු ගබඩා කරයි.",
            "මිනිසාගේ වර්ණදේහ 23 යුගලක් (46 ක්) ඇත. 23 වන යුගලය ලිංග වර්ණදේහ වේ (XX / XY).",
            "ආහාර ජාලයක ශක්ති ගැලීම ඒක දිශානතික වන අතර පෝෂී මට්ටම් ඔස්සේ 10% නියමය අනුව සිදුවේ.",
            "ජෛව විවිධත්වය සුරැකීම පරිසර තුල්‍යතාවට අත්‍යවශ්‍ය වේ."
          ),
          read_aloud_text = "දෙමාපියන්ගෙන් දරුවන්ට ලක්ෂණ උරුම වීම ප්‍රවේණියයි. වර්ණදේහ මත පිහිටි ජාන මගින් මෙම ලක්ෂණ තීරණය කරයි. පරිසර පද්ධතියක නිෂ්පාදකයන්, පාරිභෝගිකයන් හා වියෝජකයන් අතර අන්තර්ක්‍රියාකාරිත්වය ජීවයේ පැවැත්ම තහවුරු කරයි."
        )
      )
      "math" -> listOf(
        ChapterItem(
          chapter_id = 1,
          title = "1 වන පරිච්ඡේදය: සංඛ්‍යා රටා, ප්‍රථමක සාධක හා ල.පො.ගු.",
          page_start = 1,
          page_end = 20,
          audio_note_url = "https://example.com/audio_math_ch1.mp3",
          audio_duration = "03:50",
          summary_bullets = listOf(
            "සමාන්තර ශ්‍රේඪියක n වන පදය: Tn = a + (n - 1)d.",
            "පළමු පද n වල එකතුව: Sn = n/2 [2a + (n - 1)d].",
            "ගුණෝත්තර ශ්‍රේඪියක n වන පදය: Tn = arⁿ⁻¹.",
            "ම.පො.ස. යනු පොදු සාධක අතරින් විශාලතම සාධකයයි.",
            "ල.පො.ගු. යනු පොදු ගුණාකාර අතරින් කුඩාම ගුණාකාරයයි."
          ),
          read_aloud_text = "සංඛ්‍යා රටාවක නියත අගයක් එකතු වීමෙන් හෝ අඩු වීමෙන් සෑදෙන රටා සමාන්තර ශ්‍රේඪි වේ. විභාගයේදී පද ගණන සෙවීම සහ පදවල එකතුව සෙවීම සඳහා අදාළ සූත්‍ර නිවැරදිව භාවිත කිරීම අත්‍යවශ්‍ය වේ."
        ),
        ChapterItem(
          chapter_id = 2,
          title = "2 වන පරිච්ඡේදය: වීජීය ප්‍රකාශන, සාධක හා වර්ගජ සමීකරණ",
          page_start = 21,
          page_end = 45,
          audio_note_url = "https://example.com/audio_math_ch2.mp3",
          audio_duration = "04:30",
          summary_bullets = listOf(
            "වර්ග දෙකක අන්තරය: a² - b² = (a - b)(a + b).",
            "පූර්ණ වර්ග ත්‍රිපද: (a + b)² = a² + 2ab + b².",
            "වර්ගජ සමීකරණ සූත්‍රය: x = [-b ± √(b² - 4ac)] / 2a.",
            "ax² + bx + c = 0 සමීකරණ සාධක මගින් හෝ වර්ගපූර්ණයෙන් විසඳිය හැක.",
            "යුගලත් සමීකරණ ආදේශයෙන් හෝ සංගුණක සමාන කිරීමෙන් විසඳිය හැක."
          ),
          read_aloud_text = "වීජ ගණිතයේ මූලික පදනම සාධක සෙවීමයි. වර්ග දෙකක අන්තරය සහ ත්‍රිපද ප්‍රකාශන සාධක කිරීම ප්‍රගුණ කිරීමෙන් සංකීර්ණ වර්ගජ සමීකරණ පහසුවෙන් විසඳාගත හැක."
        ),
        ChapterItem(
          chapter_id = 3,
          title = "3 වන පරිච්ඡේදය: පයිතගරස් ප්‍රමේයය, ත්‍රිකෝණ හා ජ්‍යාමිතිය",
          page_start = 46,
          page_end = 72,
          audio_note_url = "https://example.com/audio_math_ch3.mp3",
          audio_duration = "05:00",
          summary_bullets = listOf(
            "පයිතගරස් ප්‍රමේයය: ඍජුකෝණී ත්‍රිකෝණයක a² + b² = c² (කර්ණය c).",
            "ත්‍රිකෝණයක අභ්‍යන්තර කෝණවල එකතුව 180° කි.",
            "සමද්විපාද ත්‍රිකෝණයක සමාන පාද ඉදිරියෙන් ඇති කෝණ සමාන වේ.",
            "ත්‍රිකෝණ අංගසාම්‍ය අවස්ථා: පා.පා.පා, පා.කෝ.පා, කෝ.කෝ.පා, කර්ණ.පා.",
            "බහුඅස්‍රයක අභ්‍යන්තර කෝණ එකතුව = (2n - 4) × 90°."
          ),
          read_aloud_text = "ජ්‍යාමිතික ප්‍රමේය සාධනය කිරීමේදී දත්තය, සාධනය කළ යුතු දෙය, නිර්මාණය සහ සාධනය පැහැදිලිව ලිවිය යුතුය. පයිතගරස් ප්‍රමේයය ඍජුකෝණී ත්‍රිකෝණවල නොදන්නා පාද සෙවීමට යොදාගනී."
        ),
        ChapterItem(
          chapter_id = 4,
          title = "4 වන පරිච්ඡේදය: වෘත්ත ප්‍රමේය, චාප හා ක්ෂේත්‍රඵලය",
          page_start = 73,
          page_end = 95,
          audio_note_url = "https://example.com/audio_math_ch4.mp3",
          audio_duration = "04:15",
          summary_bullets = listOf(
            "වෘත්තයක පරිධිය C = 2πr, වර්ගඵලය A = πr².",
            "කේන්ද්‍ර කෝණය පරිධියේ කෝණය මෙන් දෙගුණයක් වේ.",
            "එකම ඛණ්ඩයේ කෝණ එකිනෙකට සමාන වේ.",
            "අර්ධ වෘත්තයක කෝණය ඍජුකෝණයකි (90°).",
            "චක්‍රීය චතුරස්‍රයක සම්මුඛ කෝණ පරිපූරක වේ (එකතුව 180°)."
          ),
          read_aloud_text = "වෘත්ත ආශ්‍රිත ප්‍රමේය විභාගයේ ජ්‍යාමිතික ප්‍රශ්න පත්‍රයේ ප්‍රධාන කොටසකි. චක්‍රීය චතුරස්‍ර සහ ස්පර්ශක ආශ්‍රිත ප්‍රමේය නිවැරදිව භාවිත කිරීමට පුහුණු වන්න."
        ),
        ChapterItem(
          chapter_id = 5,
          title = "5 වන පරිච්ඡේදය: ත්‍රිකෝණමිතිය, උන්නතාංශ හා අවනතාංශ කෝණ",
          page_start = 96,
          page_end = 115,
          audio_note_url = "https://example.com/audio_math_ch5.mp3",
          audio_duration = "04:00",
          summary_bullets = listOf(
            "sin θ = සම්මුඛ පාදය / කර්ණය.",
            "cos θ = බද්ධ පාදය / කර්ණය.",
            "tan θ = සම්මුඛ පාදය / බද්ධ පාදය.",
            "උන්නතාංශ කෝණය: තිරස් රේඛාවෙන් ඉහළට මනින කෝණය.",
            "අවනතාංශ කෝණය: තිරස් රේඛාවෙන් පහළට මනින කෝණය."
          ),
          read_aloud_text = "ත්‍රිකෝණමිතිය මගින් උස ගොඩනැගිලිවල උස, කඳු මුදුන් සහ දුරවල් සෘජුව නොමැන ගණනය කළ හැක. සුළු කෝණවල ත්‍රිකෝණමිතික අනුපාත සටහන් භාවිතයෙන් ගැටළු විසඳීම ප්‍රගුණ කරන්න."
        )
      )
      "history" -> listOf(
        ChapterItem(
          chapter_id = 1,
          title = "1 වන පරිච්ඡේදය: ප්‍රාග් ඓතිහාසික මානවයා හා ශ්‍රී ලංකාවේ මුල් ජනාවාස",
          page_start = 1,
          page_end = 22,
          audio_note_url = "https://example.com/audio_hist_ch1.mp3",
          audio_duration = "04:10",
          summary_bullets = listOf(
            "පාහියංගල, බටදොඹලෙන, බෙල්ලන්බැඳිපැලැස්ස ප්‍රධාන ප්‍රාග් ඓතිහාසික ස්ථාන වේ.",
            "බලංගොඩ මානවයා (Homo sapiens balangodensis) ක්ෂුද්‍ර ශිලා මෙවලම් භාවිත කළේය.",
            "ක්‍රි.පූ. 6 වන සියවසේදී විජය කුමරුගේ පැමිණීම ලිඛිත ඉතිහාසයේ ආරම්භය ලෙස සැලකේ.",
            "අනුරාධපුරය මුල්ම අගනගරය ලෙස පණ්ඩුකාභය රජු විසින් සැලසුම් සහගතව නිර්මාණය කරන ලදී.",
            "බසවක්කුලම (අභය වැව) පණ්ඩුකාභය රජුගේ ප්‍රධාන නිර්මාණයකි."
          ),
          read_aloud_text = "ලංකාවේ ප්‍රාග් ඓතිහාසික යුගය වසර ලක්ෂ ගණනක අතීතයකට දිවයයි. පණ්ඩුකාභය රජු අනුරාධපුරය අගනගරය කරගනිමින් ග්‍රාම සීමා නියම කර පරිපාලන ව්‍යුහය ගොඩනැගීම ඉතිහාසයේ වැදගත් සන්ධිස්ථානයකි."
        ),
        ChapterItem(
          chapter_id = 2,
          title = "2 වන පරිච්ඡේදය: වාරි ශිෂ්ටාචාරයේ ස්වර්ණමය යුගය හා මහා වැව්",
          page_start = 23,
          page_end = 48,
          audio_note_url = "https://example.com/audio_hist_ch2.mp3",
          audio_duration = "04:45",
          summary_bullets = listOf(
            "මහසෙන් රජු මින්නේරිය, කවුඩුල්ල, පදවිය ඇතුළු මහා වැව් 16ක් කරවීය.",
            "ධාතුසේන රජු කලා වැව සහ ජය ගඟ (යෝධ ඇළ) නිර්මාණය කළේය (සැතපුමකට අඟලක බැස්ම).",
            "බිසෝකොටුව යනු වැවේ ජල පීඩනය පාලනය කර පිටතට නිකුත් කරන විශ්මිත තාක්ෂණයයි.",
            "සොරොව්ව, රළපනාව, පිටවාන වාරි තාක්ෂණයේ ප්‍රධාන අංග වේ.",
            "වසභ රජු භූගත ජල මාර්ග හා උමං ජල තාක්ෂණය හඳුන්වා දුන්නේය."
          ),
          read_aloud_text = "පුරාණ හෙළ වාරි ශිල්පීන්ගේ තාක්ෂණික විශිෂ්ටත්වය ලෝකයේ කිසිදු රටක දැකිය නොහැකි තරම් අසමසම වේ. බිසෝකොටුවේ නිපැයුම නිසා විශාල වැව් බැඳ ජලය එක්රැස් කර ගොවිතැනට මුදාහැරීමට හැකි විය."
        ),
        ChapterItem(
          chapter_id = 3,
          title = "3 වන පරිච්ඡේදය: පොළොන්නරු යුගය හා මහා පරාක්‍රමබාහු රජුගේ සේවය",
          page_start = 49,
          page_end = 75,
          audio_note_url = "https://example.com/audio_hist_ch3.mp3",
          audio_duration = "04:30",
          summary_bullets = listOf(
            "1 වන විජයබාහු රජු චෝළ ආක්‍රමණිකයන් පලවා හැර පොළොන්නරුව අගනුවර කරගත්තේය.",
            "මහා පරාක්‍රමබාහු රජු 'පරාක්‍රම සමුද්‍රය' නිර්මාණය කර මුළු රටම ස්වයංපෝෂිත කළේය.",
            "'අහසින් වැටෙන එකදු දිය බිඳක්වත් මුහුදට ගලා යාමට නොදිය යුතුය' යන්න එතුමාගේ ප්‍රතිපත්තියයි.",
            "නිශ්ශංකමල්ල රජු හැටදාගෙය, නිශ්ශංක ලතා මණ්ඩපය ආදී ඉදිකිරීම් රැසක් කළේය.",
            "කාලිංග මාඝගේ ආක්‍රමණයත් සමඟ පොළොන්නරු රාජධානිය බිඳ වැටුණි."
          ),
          read_aloud_text = "පොළොන්නරු යුගය ශ්‍රී ලංකාවේ කෘෂිකාර්මික හා ආර්ථික ස්වර්ණමය යුගයකි. මහා පරාක්‍රමබාහු රජුගේ වාරි ප්‍රතිපත්තිය නිසා පෙරදිග ධාන්‍යාගාරය බවට ශ්‍රී ලංකාව පත් විය."
        ),
        ChapterItem(
          chapter_id = 4,
          title = "4 වන පරිච්ඡේදය: යටත් විජිත පාලනය හා 1815 උඩරට ගිවිසුම",
          page_start = 76,
          page_end = 105,
          audio_note_url = "https://example.com/audio_hist_ch4.mp3",
          audio_duration = "05:00",
          summary_bullets = listOf(
            "පෘතුගීසීන් 1505 දී ද, ලන්දේසීන් 1658 දී ද මුහුදුබඩ පළාත් අත්පත් කරගත්හ.",
            "බ්‍රිතාන්‍යයන් 1796 දී මුහුදුබඩ ප්‍රදේශ ද, 1815 දී උඩරට රාජධානිය ද යටත් කරගත්තේය.",
            "1815 මාර්තු 02 වන දින උඩරට ගිවිසුම අත්සන් කිරීමෙන් මුළු ලංකාවම බ්‍රිතාන්‍ය කිරීටයට යටත් විය.",
            "1818 වෙල්ලස්ස කැරැල්ල (කැප්පෙටිපොළ නිලමේ) හා 1848 මාතලේ කැරැල්ල (වීර පුරන් අප්පු, ගොංගාලේගොඩ බණ්ඩා) ප්‍රධාන නිදහස් අරගල වේ.",
            "1948 පෙබරවාරි 04 දින ශ්‍රී ලංකාවට ඩොමීනියන් නිදහස හිමි විය."
          ),
          read_aloud_text = "වසර 2000කට වැඩි නිදහස් රාජ්‍යයක්ව පැවති ශ්‍රී ලංකාව 1815 උඩරට ගිවිසුමෙන් බ්‍රිතාන්‍ය අධිරාජ්‍යයට නතු විය. අනතුරුව ජාතික වීරයන් විසින් මෙහෙයවන ලද විමුක්ති අරගල ඔස්සේ 1948 දී නිදහස දිනාගන්නා ලදී."
        )
      )
      else -> listOf(
        ChapterItem(
          chapter_id = 1,
          title = "1 වන පරිච්ඡේදය: මූලික සංකල්ප හා විෂය හැඳින්වීම",
          page_start = 1,
          page_end = 25,
          audio_note_url = "https://example.com/audio_gen_ch1.mp3",
          audio_duration = "03:45",
          summary_bullets = listOf(
            "විෂය නිර්දේශයේ පළමු ඒකකයේ මූලික න්‍යාය සහ අර්ථ දැක්වීම්.",
            "විභාග ගැටළු විසඳීමට අවශ්‍ය මූලික සූත්‍ර සහ කෙටි ක්‍රම.",
            "පසුගිය ප්‍රශ්න පත්‍රවල නිතර අසන ප්‍රධාන විභාග ඉලක්ක.",
            "පාඩමේ වැදගත්ම නිර්වචන සහ පාරිභාෂික වචන.",
            "ඉක්මන් පුනරීක්ෂණ සටහන් සහ මතක සටහන් (Mnemonics)."
          ),
          read_aloud_text = "මෙම පරිච්ඡේදය මගින් විෂය නිර්දේශයේ පළමු ඒකකය ආවරණය කෙරේ. මෙහි ඇති මූලධර්ම සහ නියමයන් නිවැරදිව ග්‍රහණය කරගැනීමෙන් විභාගයේ ඉහළ ලකුණු ලබාගැනීමට හැකියාව ලැබේ."
        ),
        ChapterItem(
          chapter_id = 2,
          title = "2 වන පරිච්ඡේදය: ප්‍රායෝගික යෙදීම් හා ගැටළු විග්‍රහය",
          page_start = 26,
          page_end = 55,
          audio_note_url = "https://example.com/audio_gen_ch2.mp3",
          audio_duration = "04:15",
          summary_bullets = listOf(
            "දෙවන ඒකකයේ සංකීර්ණ ගැටළු පියවරෙන් පියවර විසඳීම.",
            "විභාග ප්‍රශ්න පත්‍ර ආකෘති සහ ලකුණු ලබාදීමේ පටිපාටිය.",
            "ප්‍රායෝගික ක්‍රියාකාරකම් සහ ක්ෂේත්‍ර නිරීක්ෂණ සටහන්.",
            "වැරදීමට ඉඩ ඇති ස්ථාන සහ ඒවා මඟහරවා ගන්නා ආකාරය.",
            "කාල කළමනාකරණය සමඟ පිළිතුරු ලිවීමේ උපක්‍රම."
          ),
          read_aloud_text = "දෙවන ඒකකයේ ප්‍රායෝගික ගැටළු විසඳීමේදී විභාග ක්‍රමවේදය අනුගමනය කිරීම වැදගත්ය. සියලුම පියවර පැහැදිලිව දැක්වීමෙන් සම්පූර්ණ ලකුණු ලබාගත හැක."
        ),
        ChapterItem(
          chapter_id = 3,
          title = "3 වන පරිච්ඡේදය: උසස් සිද්ධාන්ත හා විභාග ආදර්ශ ප්‍රශ්න",
          page_start = 56,
          page_end = 90,
          audio_note_url = "https://example.com/audio_gen_ch3.mp3",
          audio_duration = "04:50",
          summary_bullets = listOf(
            "විභාග අපේක්ෂිත ආදර්ශ ප්‍රශ්න 100 ක සාකච්ඡාව.",
            "විශිෂ්ට සාමාර්ථයක් (A Pass) සඳහා විශේෂිත සටහන්.",
            "සංසන්දනාත්මක විශ්ලේෂණ සහ ප්‍රස්තාරික සටහන්.",
            "විභාග ශාලාවේදී මතකය ආවර්ජනය කරගන්නා ක්‍රමවේද.",
            "සම්පූර්ණ කෙටි සටහන එක් බැල්මකින් (Master Summary)."
          ),
          read_aloud_text = "මෙම පරිච්ඡේදය මගින් සම්පූර්ණ විෂය නිර්දේශයේ උසස් සිද්ධාන්ත සහ විභාග ප්‍රශ්න සාකච්ඡා කෙරේ. ස්වයං පුහුණු ප්‍රශ්නාවලි සහ Flashcards සමඟින් පුහුණුව තවදුරටත් ශක්තිමත් කරගන්න."
        )
      )
    }
  }

  // Deep-index search function that searches the 100+ page syllabus
  fun searchPdfIndex(query: String, chapters: List<ChapterItem>): List<SearchResultItem> {
    if (query.isBlank()) return emptyList()
    val cleanQuery = query.trim().lowercase()

    val results = mutableListOf<SearchResultItem>()
    for (ch in chapters) {
      val titleMatch = ch.title.lowercase().contains(cleanQuery)
      val bulletMatches = ch.summary_bullets.filter { it.lowercase().contains(cleanQuery) }
      val textMatch = ch.read_aloud_text.lowercase().contains(cleanQuery)

      if (titleMatch || bulletMatches.isNotEmpty() || textMatch) {
        val snippet = when {
          bulletMatches.isNotEmpty() -> bulletMatches.first()
          textMatch -> ch.read_aloud_text.take(90) + "..."
          else -> ch.title
        }
        results.add(
          SearchResultItem(
            chapter = ch,
            targetPage = ch.page_start,
            matchSnippet = snippet,
            matchType = if (titleMatch) "පරිච්ඡේද මාතෘකාව" else "සටහන් අන්තර්ගතය"
          )
        )
      }
    }
    return results
  }
}

data class SearchResultItem(
  val chapter: ChapterItem,
  val targetPage: Int,
  val matchSnippet: String,
  val matchType: String
)
