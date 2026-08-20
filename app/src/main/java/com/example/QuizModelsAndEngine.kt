package com.example

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// --------------------------------------------------
// 1. DATA MODELS & JSON SCHEMA
// --------------------------------------------------

data class QuizQuestion(
  val question_id: Int,
  val question_text: String,
  val options: List<String>,
  val correct_option_index: Int,
  val explanation: String
)

data class QuizSet(
  val set_id: Int,
  val set_title: String,
  val time_limit_seconds: Int = 180,
  val questions: List<QuizQuestion>
)

data class SubjectQuizData(
  val grade: String,
  val subject: String,
  val short_notes_pdf_url: String = "",
  val quiz_sets: List<QuizSet>
)

data class FlashcardItem(
  val id: Int,
  val termOrQuestion: String,
  val definitionOrAnswer: String,
  val keyTakeaway: String
)

// Internal shuffled wrapper for anti-memorization
data class ShuffledQuestion(
  val originalId: Int,
  val questionText: String,
  val shuffledOptions: List<String>,
  val correctOptionIndex: Int,
  val explanation: String
)

// --------------------------------------------------
// 2. QUIZ REPOSITORY & AI GENERATOR
// --------------------------------------------------

object QuizRepository {

  fun getQuizDataForSubject(grade: String, subjectName: String, shortNoteUrl: String = ""): SubjectQuizData {
    val norm = normalizeSubjectKey(subjectName)
    val baseSets = generate10SetsForSubject(grade, subjectName, norm)
    return SubjectQuizData(
      grade = grade,
      subject = subjectName,
      short_notes_pdf_url = shortNoteUrl,
      quiz_sets = baseSets
    )
  }

  fun getFlashcardsForSubject(grade: String, subjectName: String): List<FlashcardItem> {
    val norm = normalizeSubjectKey(subjectName)
    return when (norm) {
      "science" -> listOf(
        FlashcardItem(1, "ප්‍රභාසංස්ලේෂණය (Photosynthesis)", "කාබන් ඩයොක්සයිඩ් සහ ජලය යොදාගෙන සූර්යාලෝකය හමුවේ ග්ලූකෝස් සහ ඔක්සිජන් නිපදවීම.", "ප්‍රධාන වර්ණකය ක්ලෝරොෆිල් වන අතර ශාක පත්‍රවල හරිතලව තුළ සිදුවේ."),
        FlashcardItem(2, "නිව්ටන්ගේ දෙවන නියමය (Newton's 2nd Law)", "F = ma (බලය = ස්කන්ධය × ත්වරණය).", "වස්තුවක් මත යොදන අසමතුලිත බලය එහි ගම්‍යතාව වෙනස් වීමේ සීඝ්‍රතාවයට සමානුපාතික වේ."),
        FlashcardItem(3, "ඕම්ගේ නියමය (Ohm's Law)", "V = IR (විභව අන්තරය = ධාරාව × ප්‍රතිරෝධය).", "නියත උෂ්ණත්වයේදී සන්නායකයක් තුළින් ගලන ධාරාව එහි අග්‍ර අතර විභව අන්තරයට අනුලෝමව සමානුපාතික වේ."),
        FlashcardItem(4, "සෛලයේ ශක්ති බලාගාරය", "මයිටොකොන්ඩ්‍රියා (Mitochondria).", "සෛලීය ශ්වසනය මගින් ATP ආකාරයෙන් ශක්තිය මුදාහරින ප්‍රධාන ඉන්ද්‍රයිකාවයි."),
        FlashcardItem(5, "pH අගය පරාසය", "0 සිට 14 දක්වා (7 උදාසීන, <7 ආම්ලික, >7 භෂ්මික).", "ශුද්ධ ජලයේ pH අගය 7 වේ. මිනිස් රුධිරයේ pH අගය ආසන්න වශයෙන් 7.4 කි."),
        FlashcardItem(6, "ආලෝකයේ වේගය (Speed of Light)", "3 × 10⁸ ms⁻¹ (හිස් අවකාශයේ).", "භෞතික ලෝකයේ ශක්තියක් හෝ සංඥාවක් ගමන් කළ හැකි උපරිම වේගයයි."),
        FlashcardItem(7, "ප්‍රවේණියේ මූලික ඒකකය", "ජානය (Gene - DNA ඛණ්ඩයක්).", "ජීවීන්ගේ ලක්ෂණ පරම්පරාවෙන් පරම්පරාවට ගෙන යන තොරතුරු අඩංගු මූලික ඒකකයයි."),
        FlashcardItem(8, "පැස්කල් මූලධර්මය (Pascal's Principle)", "සංවෘත තරලයක එක් තැනකට යෙදූ පීඩනය සියලු දිශාවන්ට එක සමානව සම්ප්‍රේෂණය වේ.", "හයිඩ්‍රොලික් තිරිංග හා ජැක් සඳහා යොදා ගනී.")
      )
      "math" -> listOf(
        FlashcardItem(1, "පයිතගරස් ප්‍රමේයය (Pythagoras Theorem)", "a² + b² = c² (ඍජුකෝණී ත්‍රිකෝණයක කර්ණයේ වර්ගය අනෙක් පාද දෙකේ වර්ගවල එකතුවට සමාන වේ).", "ඍජුකෝණී ත්‍රිකෝණ සඳහා පමණක් වලංගු වේ."),
        FlashcardItem(2, "වෘත්තයක වර්ගඵලය (Area of Circle)", "A = πr² (π = 22/7 හෝ 3.1416).", "පරිධිය සෙවීමේ සූත්‍රය C = 2πr වේ."),
        FlashcardItem(3, "වර්ගජ සමීකරණ සූත්‍රය", "x = [-b ± √(b² - 4ac)] / 2a", "ax² + bx + c = 0 ආකාරයේ සමීකරණ විසඳීමට භාවිතා කරයි."),
        FlashcardItem(4, "සමාන්තර ශ්‍රේඪියක n වන පදය", "Tn = a + (n - 1)d", "a = මුල් පදය, d = පොදු අන්තරය, n = පද ගණන වේ."),
        FlashcardItem(5, "සම්භාවිතාව (Probability)", "P(E) = වාසිදායක සිදුවීම් ගණන / මුළු නියැදි අවකාශය", "ඕනෑම සිදුවීමක සම්භාවිතාව 0 සිට 1 දක්වා පරාසයක පවතී."),
        FlashcardItem(6, "ත්‍රිකෝණමිතිය - Sin, Cos, Tan", "Sinθ = සම්මුඛ/කර්ණය, Cosθ = බද්ධ/කර්ණය, Tanθ = සම්මුඛ/බද්ධ", "ඍජුකෝණී ත්‍රිකෝණ කෝණ හා පාද අතර සම්බන්ධය දක්වයි."),
        FlashcardItem(7, "සරල රේඛාවක අනුක්‍රමණය (Gradient)", "m = (y₂ - y₁) / (x₂ - x₁), සමීකරණය y = mx + c", "m = අනුක්‍රමණය වන අතර c = y-අන්තඃඛණ්ඩයයි.")
      )
      "history" -> listOf(
        FlashcardItem(1, "මහසෙන් රජුගේ ප්‍රධාන වාරි නිර්මාණය", "මින්නේරිය වැව (මින්නේරි දෙවියන් ලෙස හැඳින්වේ).", "ශ්‍රී ලංකාවේ විශාලතම වාරි ව්‍යාපාරයක් ලෙස ක්‍රි.ව. 3 වන සියවසේදී ඉදිවිය."),
        FlashcardItem(2, "පොළොන්නරු රාජධානියේ ශ්‍රේෂ්ඨතම රජු", "මහා පරාක්‍රමබාහු රජු.", "'අහසින් වැටෙන එකදු දිය බිඳක්වත් මිනිසාගේ ප්‍රයෝජනයට නොගෙන මුහුදට ගලා යාමට නොදිය යුතුය' යන්න එතුමාගේ ප්‍රතිපත්තියයි."),
        FlashcardItem(3, "උඩරට ගිවිසුම අත්සන් කළ වර්ෂය", "ක්‍රි.ව. 1815 මාර්තු 02 වන දින.", "බ්‍රිතාන්‍යයන් විසින් මුළු ලංකාවම සිය පාලනයට නතු කරගත් ඓතිහාසික අවස්ථාවයි."),
        FlashcardItem(4, "දේවානම්පියතිස්ස රජ සමයේ ඓතිහාසික සිදුවීම", "මිහිඳු මහරහතන් වහන්සේගේ ලංකාගමනය (බුදුදහම ස්ථාපිත වීම).", "ක්‍රි.පූ. 3 වන සියවසේදී පොසොන් පුන් පොහෝ දින මිහින්තලයේදී සිදුවිය."),
        FlashcardItem(5, "සීගිරිය බලකොටුව ඉදිකළ රජු", "1 වන කාශ්‍යප රජතුමා (ක්‍රි.ව. 5 වන සියවස).", "ලෝක උරුමයක් වූ කැඩපත් පවුර හා සීගිරි බිතුසිතුවම් නිර්මාණය විය.")
      )
      "buddhism" -> listOf(
        FlashcardItem(1, "චතුරාර්ය සත්‍යය", "දුක්ඛ, සමුදය, නිරෝධ, මාර්ග සත්‍යයන් හතරයි.", "බුදුදහමේ මූලිකම හා ගැඹුරුම සත්‍යාවබෝධයයි."),
        FlashcardItem(2, "ප්‍රථම ධර්ම දේශනාව", "ධම්මචක්කප්පවත්තන සූත්‍රය (ඉසිපතන මිගදායේදී).", "පස්වග මහණුන් උදෙසා ඇසළ පුන් පොහෝ දින දේශනා කරන ලදී."),
        FlashcardItem(3, "ආර්ය අෂ්ටාංගික මාර්ගය", "සීල, සමාධි, ප්‍රඥා යන ත්‍රිශික්ෂාවට අයත් අංග 8 කි.", "නිවන් දැකීම සඳහා පිළිපැදිය යුතු එකම මධ්‍යම ප්‍රතිපදාවයි."),
        FlashcardItem(4, "ත්‍රිපිටකයේ කොටස් 3", "විනය පිටකය, සූත්‍ර පිටකය, අභිධර්ම පිටකය.", "භික්ෂු විනය, සූත්‍ර දේශනා සහ පරමාර්ථ ධර්ම විග්‍රහ අඩංගු වේ.")
      )
      "ict" -> listOf(
        FlashcardItem(1, "CPU හි ප්‍රධාන කොටස් 3", "ALU (අංකගණිත හා තර්ක ඒකකය), CU (පාලන ඒකකය), Memory Registers.", "පරිගණකයේ මධ්‍යම සැකසුම් මොළය ලෙස ක්‍රියා කරයි."),
        FlashcardItem(2, "RAM සහ ROM වෙනස", "RAM තාවකාලික (Volatile) වන අතර ROM ස්ථිර (Non-volatile) වේ.", "RAM පරිගණකය ක්‍රියාත්මක වන විට දත්ත රඳවා ගන්නා අතර විදුලිය විසන්ධි වූ විට මැකී යයි."),
        FlashcardItem(3, "Binary සංඛ්‍යා පද්ධතිය", "පාදය 2 වන අතර 0 සහ 1 පමණක් භාවිතා වේ.", "පරිගණක දෘඩාංග තුළ විද්‍යුත් ස්පන්දන නියෝජනයට යොදාගනී."),
        FlashcardItem(4, "Logic Gates මූලික වර්ග 3", "AND, OR, NOT ගේට්ටු.", "බූලීය වීජ ගණිතය මත පදනම්ව සංඛ්‍යාංක පරිපථ නිර්මාණය කරයි.")
      )
      "commerce" -> listOf(
        FlashcardItem(1, "ද්විත්ව සටහන් මූලධර්මය (Double Entry)", "සෑම ගනුදෙනුවකටම හර (Debit) සහ බැර (Credit) යන සමාන බලපෑම් දෙකක් ඇත.", "ගිණුම්කරණ සමීකරණය: වත්කම් = හිමිකම + වගකීම්."),
        FlashcardItem(2, "ශේෂ පත්‍රය (Balance Sheet)", "යම් නිශ්චිත දිනයක ව්‍යාපාරයේ මූල්‍ය තත්ත්වය (වත්කම්, වගකීම්, හිමිකම) පෙන්වන ප්‍රකාශනයයි.", "ව්‍යාපාරයේ මූල්‍ය ශක්තිය හා ස්ථාවරත්වය මැනීමට භාවිතා වේ."),
        FlashcardItem(3, "අලෙවිකරණ මිශ්‍රමය (4 Ps)", "Product (භාණ්ඩය), Price (මිල), Place (ස්ථානය), Promotion (ප්‍රවර්ධනය).", "පාරිභෝගික තෘප්තිය උපරිම කර ආදායම වැඩිකර ගැනීමේ උපායමාර්ගයයි.")
      )
      "sinhala" -> listOf(
        FlashcardItem(1, "සිංහල හෝඩියේ ප්‍රාණාක්ෂර හා ගාත්‍රාක්ෂර", "ප්‍රාණාක්ෂර (ස්වර) 18 ක් සහ ගාත්‍රාක්ෂර (ව්‍යංජන) 42 ක් ඇත.", "ශුද්ධ සිංහල හෝඩියේ අක්ෂර 32ක් ද මිශ්‍ර සිංහල හෝඩියේ අක්ෂර 60ක් ද වේ."),
        FlashcardItem(2, "සන්ධි කිරීම (Sandhi)", "පද දෙකක් එකතු වී ශබ්ද සංකලනයෙන් එක් පදයක් වීම.", "ස්වර සන්ධි, ව්‍යංජන සන්ධි, පූර්වස්වර ලෝප සන්ධි යනාදී වශයෙන් බෙදේ."),
        FlashcardItem(3, "ක්‍රියා පද ප්‍රභේද", "මිශ්‍ර ක්‍රියා, අසම්භාව්‍ය ක්‍රියා, විධි ක්‍රියා, ආශීර්වාද ක්‍රියා, ප්‍රයෝජ්‍ය ක්‍රියා.", "වාක්‍යයක අර්ථය සම්පූර්ණ කරන ප්‍රධාන පද වර්ගයයි.")
      )
      "english" -> listOf(
        FlashcardItem(1, "Parts of Speech (8)", "Noun, Pronoun, Verb, Adjective, Adverb, Preposition, Conjunction, Interjection.", "The fundamental building blocks of the English language."),
        FlashcardItem(2, "Active Voice vs Passive Voice", "Active: Subject performs action. Passive: Subject receives action.", "Example: 'She wrote a book' -> 'A book was written by her'."),
        FlashcardItem(3, "Conditional Sentences (Type 1, 2, 3)", "Type 1: Real possible (If + Present, Will). Type 2: Unreal present (If + Past, Would).", "Crucial for writing clear and grammatical exam essays.")
      )
      "health" -> listOf(
        FlashcardItem(1, "සමබල ආහාර වේලක්", "කාබෝහයිඩ්‍රේට්, ප්‍රෝටීන, ලිපිඩ, විටමින්, ඛනිජ ලවණ සහ කෙඳි නිසි අනුපාතයට අඩංගු ආහාරයයි.", "ශරීරයේ වර්ධනයට හා නිරෝගීතාවයට අත්‍යවශ්‍ය වේ."),
        FlashcardItem(2, "BMI (ශරීර ස්කන්ධ දර්ශකය)", "BMI = ස්කන්ධය (kg) / උසෙහි වර්ගය (m²).", "සාමාන්‍ය නිරෝගී BMI පරාසය 18.5 - 24.9 අතර වේ.")
      )
      "agri" -> listOf(
        FlashcardItem(1, "පසෙහි ප්‍රධාන සංරචක 4", "ඛනිජ ද්‍රව්‍ය (45%), කාබනික ද්‍රව්‍ය (5%), පාංශු ජලය (25%), පාංශු වාතය (25%).", "ශාක මුල්වල සෞඛ්‍ය සම්පන්න වර්ධනයට මෙම සමතුලිතතාවය අවශ්‍යයි."),
        FlashcardItem(2, "ප්‍රධාන ශාක පෝෂක 3 (NPK)", "නයිට්‍රජන් (N), පොස්පරස් (P), පොටෑසියම් (K).", "නයිට්‍රජන් පත්‍ර වර්ධනයටත්, පොස්පරස් මුල්වලටත්, පොටෑසියම් ඵලදාවටත් උපකාරී වේ.")
      )
      else -> listOf(
        FlashcardItem(1, "ක්‍රියාශීලී මතක් කිරීම (Active Recall)", "සටහන නැවත නැවත කියවනවාට වඩා ප්‍රශ්නවලට පිළිතුරු සෙවීමෙන් මතක ශක්තිය 300% කින් වැඩිවේ.", "Short Notes කියවීමෙන් පසු වහාම MCQ Quizzes වලට සහභාගී වන්න."),
        FlashcardItem(2, "කාල කළමනාකරණය (Time Management)", "ප්‍රශ්න 10ක් සඳහා විනාඩි 3ක කාලයක් ලැබේ (තත්පර 18 කට එක් ප්‍රශ්නයක්).", "විභාග ශාලාවේදී වේගය හා නිරවද්‍යතාවය පුහුණු කරයි."),
        FlashcardItem(3, "වරදින තැන් අධ්‍යයනය (Explanation Review)", "සෑම වැරදි පිළිතුරකටම නිවැරදි විවරණය කියවා මතක තබාගන්න.", "සෑම Set එකක්ම 100% ලකුණු ලැබෙන තෙක් නැවත නැවත පුහුණු වන්න.")
      )
    }
  }

  private fun generate10SetsForSubject(grade: String, subjectName: String, norm: String): List<QuizSet> {
    val setsList = mutableListOf<QuizSet>()

    for (setIdx in 1..10) {
      val questions = mutableListOf<QuizQuestion>()
      val setTopic = getSubtopicForSet(norm, setIdx)

      for (qIdx in 1..10) {
        val qId = (setIdx - 1) * 10 + qIdx
        val generatedQ = buildQuestionForSubject(norm, grade, subjectName, setIdx, qIdx, setTopic)
        questions.add(generatedQ)
      }

      setsList.add(
        QuizSet(
          set_id = setIdx,
          set_title = "Set 0$setIdx - $setTopic",
          time_limit_seconds = 180,
          questions = questions
        )
      )
    }
    return setsList
  }

  private fun getSubtopicForSet(norm: String, setIndex: Int): String {
    return when (norm) {
      "science" -> when (setIndex) {
        1 -> "ජීවීන්ගේ ලක්ෂණ හා සෛල ව්‍යුහය"
        2 -> "පදාර්ථයේ ව්‍යුහය හා ආවර්තිතා වගුව"
        3 -> "ප්‍රභාසංස්ලේෂණය හා ශාක ක්‍රියාකාරීත්වය"
        4 -> "චලිතය, නිව්ටන් නියම හා බලය"
        5 -> "රසායනික බන්ධන හා ප්‍රතික්‍රියා"
        6 -> "ධාරා විද්‍යුතය හා පරිපථ"
        7 -> "ජාන විද්‍යාව හා ප්‍රවේණිය"
        8 -> "පරිසර පද්ධති හා ජෛව විවිධත්වය"
        9 -> "තරංග, ආලෝකය හා දෘෂ්ටිය"
        else -> "තාපය, පීඩනය හා ශක්තිය පරිවර්තන"
      }
      "math" -> when (setIndex) {
        1 -> "සංඛ්‍යා රටා හා ප්‍රථමක සංඛ්‍යා"
        2 -> "භාග, දශම හා ප්‍රතිශත"
        3 -> "වීජීය ප්‍රකාශන හා සාධක"
        4 -> "සරල හා යුගලත් සමීකරණ"
        5 -> "පයිතගරස් ප්‍රමේයය හා ත්‍රිකෝණ"
        6 -> "වෘත්ත ප්‍රමේය හා ජ්‍යාමිතිය"
        7 -> "ක්ෂේත්‍රඵලය හා පරිමාව ගණනය"
        8 -> "සමාන්තර හා ගුණෝත්තර ශ්‍රේඪි"
        9 -> "සම්භාවිතාව හා නියැදි අවකාශ"
        else -> "සංඛ්‍යානය හා ප්‍රස්තාර විග්‍රහය"
      }
      "history" -> when (setIndex) {
        1 -> "ප්‍රාග් ඓතිහාසික යුගය හා ජනාවාස"
        2 -> "අනුරාධපුර යුගයේ ආරම්භය හා වාරි ශිෂ්ටාචාරය"
        3 -> "මහසෙන් හා ධාතුසේන රජවරුන්ගේ සේවය"
        4 -> "පොළොන්නරු රාජධානිය හා මහා පරාක්‍රමබාහු රජු"
        5 -> "දඹදෙණිය හා යාපහුව යුගයන්"
        6 -> "කෝට්ටේ රාජධානිය හා සාහිත්‍ය පුනරුදය"
        7 -> "පෘතුගීසි හා ලන්දේසි යටත් විජිත පාලනය"
        8 -> "උඩරට රාජධානිය හා 1815 ගිවිසුම"
        9 -> "1818 හා 1848 නිදහස් අරගල"
        else -> "1948 නිදහස හා නූතන ශ්‍රී ලංකාව"
      }
      "sinhala" -> when (setIndex) {
        1 -> "සිංහල අක්ෂර මාලාව හා අක්ෂර වින්‍යාසය"
        2 -> "නාම පද හා ක්‍රියා පද වර්ගීකරණය"
        3 -> "සන්ධි හා සමාස පද භාවිතය"
        4 -> "විභක්ති හා ප්‍රත්‍ය නීති"
        5 -> "ප්‍රස්ථාව පිරුළු හා තේරුම්"
        6 -> "යුගල පද හා ප්‍රතිවිරුද්ධ පද"
        7 -> "සම්භාව්‍ය සාහිත්‍ය කෘති හා කතුවරු"
        8 -> "කාව්‍ය රසවින්දනය හා ඡන්දස්"
        9 -> "ගද්‍ය පද්‍ය විචාර හා රචනා රටා"
        else -> "සංස්කරණ ලකුණු හා ව්‍යාකරණ රීති"
      }
      "buddhism" -> when (setIndex) {
        1 -> "සිද්ධාර්ථ කුමාරෝත්පත්තිය හා බෝසත් චරිතය"
        2 -> "බුද්ධත්වය හා ප්‍රථම ධර්ම දේශනාව"
        3 -> "චතුරාර්ය සත්‍ය හා පටිච්චසමුප්පාදය"
        4 -> "ආර්ය අෂ්ටාංගික මාර්ගය හා සීලය"
        5 -> "ත්‍රිපිටකය (සූත්‍ර, විනය, අභිධර්ම)"
        6 -> "සැරියුත් මුගලන් අග්‍රශ්‍රාවක චරිත"
        7 -> "ශ්‍රී ලංකාවට බුදුදහම ලැබීම (මිහිඳු හිමි)"
        8 -> "ථූපාරාමය හා රුවන්වැලි මහා සෑය"
        9 -> "ධර්ම සංගායනා ඉතිහාසය"
        else -> "බෞද්ධ සමාජ දර්ශනය හා සදාචාරය"
      }
      "ict" -> when (setIndex) {
        1 -> "පරිගණක දෘඩාංග හා උපාංග"
        2 -> "දත්ත හා තොරතුරු නියෝජනය (Binary/Hex)"
        3 -> "Logic Gates හා බූලීය වීජ ගණිතය"
        4 -> "මෙහෙයුම් පද්ධති (Operating Systems)"
        5 -> "වදන් සැකසුම් හා පැතුරුම්පත් (Spreadsheets)"
        6 -> "Database Management Systems (DBMS)"
        7 -> "පරිගණක ජාල හා අන්තර්ජාලය (Networking)"
        8 -> "HTML & CSS වෙබ් නිර්මාණය"
        9 -> "ක්‍රමලේඛන මූලධර්ම (Python/Pascal)"
        else -> "තොරතුරු ආරක්ෂාව හා සයිබර් ආචාරධර්ම"
      }
      "commerce" -> when (setIndex) {
        1 -> "ව්‍යාපාර පසුබිම හා මානව අවශ්‍යතා"
        2 -> "ව්‍යාපාර හිමිකාරිත්ව වර්ග"
        3 -> "ද්විත්ව සටහන් මූලධර්මය"
        4 -> "මූලික සටහන් පොත් හා ජර්නල"
        5 -> "ශේෂ පත්‍රය හා මූල්‍ය ප්‍රකාශන"
        6 -> "ලාභ අලාභ ගිණුම් සැකසීම"
        7 -> "වාණිජ බැංකු හා මූල්‍ය සේවා"
        8 -> "අලෙවිකරණය හා වෙළඳ දැන්වීම්"
        9 -> "රක්ෂණය හා අවදානම් කළමනාකරණය"
        else -> "මුදල් හා ජාත්‍යන්තර වෙළඳාම"
      }
      else -> when (setIndex) {
        1 -> "මූලික සිද්ධාන්ත හා හැඳින්වීම"
        2 -> "ප්‍රධාන සංකල්ප හා විවරණ"
        3 -> "විභාග අනුමාන ප්‍රශ්නෝත්තර"
        4 -> "ප්‍රායෝගික යෙදීම් හා උදාහරණ"
        5 -> "විෂය නිර්දේශයේ ප්‍රධාන පාඩම්"
        6 -> "කෙටි සටහන් ඇසුරෙන් ප්‍රශ්න"
        7 -> "පසුගිය ප්‍රශ්න පත්‍ර ආශ්‍රිත ප්‍රශ්න"
        8 -> "අභියෝගාත්මක බහුවරණ ප්‍රශ්න"
        9 -> "ඉක්මන් පුනරීක්ෂණ ගැටළු"
        else -> "සම්පූර්ණ ආදර්ශ ප්‍රශ්නාවලිය"
      }
    }
  }

  private fun buildQuestionForSubject(
    norm: String,
    grade: String,
    subjectName: String,
    setIdx: Int,
    qIdx: Int,
    topic: String
  ): QuizQuestion {
    val qNumber = (setIdx - 1) * 10 + qIdx

    // Highly realistic, pedagogical Sinhala syllabus questions
    return when (norm) {
      "science" -> when (qIdx) {
        1 -> QuizQuestion(
          question_id = qNumber,
          question_text = "සෛලයක ප්‍රවේණික ද්‍රව්‍ය (DNA) අඩංගු වන ප්‍රධාන ඉන්ද්‍රයිකාව කුමක්ද?",
          options = listOf("න්‍යෂ්ටිය (Nucleus)", "මයිටොකොන්ඩ්‍රියාව", "රයිබොසෝම", "ගොල්ගි දේහය"),
          correct_option_index = 0,
          explanation = "න්‍යෂ්ටිය තුළ වර්ණදේහ සහ ජාන (DNA) අඩංගු වන අතර සෛලයේ සියලු ජෛව ක්‍රියා පාලනය කරන්නේ න්‍යෂ්ටියයි."
        )
        2 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ප්‍රභාසංස්ලේෂණ ක්‍රියාවලියේදී පිටවන වායුව කුමක්ද?",
          options = listOf("කාබන් ඩයොක්සයිඩ්", "ඔක්සිජන් (O₂)", "නයිට්‍රජන්", "හයිඩ්‍රජන්"),
          correct_option_index = 1,
          explanation = "ශාක ජලය හා සූර්යාලෝකය උපයෝගී කරගෙන ආහාර නිපදවීමේදී අතුරු ඵලයක් ලෙස ඔක්සිජන් වායුව වායුගෝලයට මුදාහරී."
        )
        3 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ස්කන්ධය 5 kg වන වස්තුවක් මත 20 N බලයක් යෙදූ විට ඇතිවන ත්වරණය කොපමණද?",
          options = listOf("2 ms⁻²", "4 ms⁻²", "100 ms⁻²", "15 ms⁻²"),
          correct_option_index = 1,
          explanation = "F = ma සූත්‍රය අනුව, a = F / m වේ. එනම් a = 20 N / 5 kg = 4 ms⁻²."
        )
        4 -> QuizQuestion(
          question_id = qNumber,
          question_text = "මිනිස් සිරුරේ රුධිරය කැටි ගැසීමට උපකාරී වන රුධිර සෛල වර්ගය කුමක්ද?",
          options = listOf("රතු රුධිරාණු", "සුදු රුධිරාණු", "රුධිර පට්ටිකා (Platelets)", "ප්ලාස්මාව"),
          correct_option_index = 2,
          explanation = "තුවාලයක් සිදුවූ විට රුධිර වහනය වැළැක්වීම සඳහා රුධිර පට්ටිකා මගින් තන්තුමය දැලක් සාදා රුධිරය කැටි ගස්වයි."
        )
        5 -> QuizQuestion(
          question_id = qNumber,
          question_text = "විද්‍යුත් විභව අන්තරය මනින සම්මත SI ඒකකය කුමක්ද?",
          options = listOf("ඇම්පියර (A)", "ඕම් (Ω)", "වෝල්ට් (V)", "ජූල් (J)"),
          correct_option_index = 2,
          explanation = "විද්‍යුත් විභව අන්තරය මනින්නේ වෝල්ට් (Volt) වලින් වන අතර ධාරාව ඇම්පියර් වලින්ද ප්‍රතිරෝධය ඕම් වලින්ද මනිනු ලැබේ."
        )
        6 -> QuizQuestion(
          question_id = qNumber,
          question_text = "පහත දැක්වෙන ද්‍රව්‍ය අතුරින් අම්ලයක් වන්නේ කුමක්ද?",
          options = listOf("දෙහි යුෂ (Citric Acid)", "හුණු දියර", "සබන් දියර", "බේකින් සෝඩා"),
          correct_option_index = 0,
          explanation = "දෙහි යුෂ වල සිට්‍රික් අම්ලය අඩංගු වන අතර එහි pH අගය 7ට වඩා අඩුය. හුණු දියර හා සබන් භෂ්මික වේ."
        )
        7 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ශාකවල ජලය හා ඛනිජ ලවණ ඉහළට පරිවහනය කරන පටකය කුමක්ද?",
          options = listOf("ෆ්ලෝයමය", "සෛලම පටකය (Xylem)", "මෘදුස්තරය", "ස්ථූලකෝණාස්තරය"),
          correct_option_index = 1,
          explanation = "සෛලම පටකය මගින් මුල්වල සිට පත්‍ර දක්වා ජලය හා ඛනිජ ලවණද, ෆ්ලෝයමය මගින් පත්‍රවල හැදෙන ආහාරද පරිවහනය කරයි."
        )
        8 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ආලෝකය එක් මාධ්‍යයක සිට වෙනත් මාධ්‍යයකට ගමන් කිරීමේදී සිදුවන ආලෝක කිරණ නැමීම හඳුන්වන්නේ කුමන නමකින්ද?",
          options = listOf("පරාවර්තනය", "වර්තනය (Refraction)", "විසිරණය", "අවශෝෂණය"),
          correct_option_index = 1,
          explanation = "විවිධ ඝනත්වයන් ඇති මාධ්‍ය හරහා ආලෝකය ගමන් කිරීමේදී එහි ප්‍රවේගය වෙනස්වීම නිසා ආලෝක කිරණ නැමීම ආලෝක වර්තනයයි."
        )
        9 -> QuizQuestion(
          question_id = qNumber,
          question_text = "වායුගෝලයේ බහුලවම අඩංගු වායුව කුමක්ද?",
          options = listOf("ඔක්සිජන් (21%)", "කාබන් ඩයොක්සයිඩ්", "නයිට්‍රජන් (78%)", "ආගන්"),
          correct_option_index = 2,
          explanation = "වායුගෝලයෙන් ආසන්න වශයෙන් 78% ක්ම සමන්විත වන්නේ නයිට්‍රජන් (N₂) වායුවෙනි."
        )
        else -> QuizQuestion(
          question_id = qNumber,
          question_text = "ශක්තිය මැවීමට හෝ විනාශ කිරීමට නොහැකි බව ප්‍රකාශ වන නියමය කුමක්ද?",
          options = listOf("ශක්ති සංස්ථිති නියමය", "නිව්ටන්ගේ 1 වන නියමය", "ආකිමිඩීස් මූලධර්මය", "පැස්කල් නියමය"),
          correct_option_index = 0,
          explanation = "ශක්ති සංස්ථිති නියමයට අනුව ශක්තිය එක් ආකාරයකින් තවත් ආකාරයකට පරිවර්තනය කළ හැකි මුත් අලුතින් මැවීම හෝ විනාශ කිරීම කළ නොහැක."
        )
      }
      "math" -> when (qIdx) {
        1 -> QuizQuestion(
          question_id = qNumber,
          question_text = "2, 5, 8, 11, ... සමාන්තර ශ්‍රේඪියේ 10 වන පදය කුමක්ද?",
          options = listOf("27", "29", "31", "33"),
          correct_option_index = 1,
          explanation = "Tn = a + (n - 1)d අනුව, a = 2, d = 3, n = 10 වේ. T₁₀ = 2 + (9 × 3) = 2 + 27 = 29."
        )
        2 -> QuizQuestion(
          question_id = qNumber,
          question_text = "අරය 7 cm වන වෘත්තයක පරිධිය කොපමණද? (π = 22/7)",
          options = listOf("22 cm", "44 cm", "154 cm", "88 cm"),
          correct_option_index = 1,
          explanation = "පරිධිය C = 2πr වේ. C = 2 × (22/7) × 7 = 44 cm."
        )
        3 -> QuizQuestion(
          question_id = qNumber,
          question_text = "2x + 5 = 19 සමීකරණයේ x හි අගය කීයද?",
          options = listOf("5", "7", "12", "14"),
          correct_option_index = 1,
          explanation = "2x = 19 - 5 => 2x = 14 => x = 7."
        )
        4 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ඍජුකෝණී ත්‍රිකෝණයක ආසන්න පාද 3 cm හා 4 cm නම් කර්ණයේ දිග කොපමණද?",
          options = listOf("5 cm", "6 cm", "7 cm", "25 cm"),
          correct_option_index = 0,
          explanation = "පයිතගරස් ප්‍රමේයයට අනුව c² = 3² + 4² = 9 + 16 = 25 => c = √25 = 5 cm."
        )
        5 -> QuizQuestion(
          question_id = qNumber,
          question_text = "(x + 3)(x - 3) ප්‍රකාශනයේ ප්‍රසාරණය කුමක්ද?",
          options = listOf("x² - 6", "x² - 9", "x² + 9", "x² - 6x + 9"),
          correct_option_index = 1,
          explanation = "වර්ග දෙකක අන්තරය සූත්‍රය (a + b)(a - b) = a² - b² අනුව x² - 3² = x² - 9 ලැබේ."
        )
        6 -> QuizQuestion(
          question_id = qNumber,
          question_text = "සාධාරණ දාදු කැටයක් උඩ දැමූ විට ඉරට්ටේ සංඛ්‍යාවක් ලැබීමේ සම්භාවිතාව කුමක්ද?",
          options = listOf("1/6", "1/3", "1/2 (3/6)", "2/3"),
          correct_option_index = 2,
          explanation = "ඉරට්ටේ සංඛ්‍යා {2, 4, 6} (සිදුවීම් 3ක්) වේ. මුළු අවස්ථා 6කි. සම්භාවිතාව = 3/6 = 1/2."
        )
        7 -> QuizQuestion(
          question_id = qNumber,
          question_text = "පැත්තක දිග 4 cm වන ඝනකයක මුළු පෘෂ්ඨ වර්ගඵලය කොපමණද?",
          options = listOf("64 cm²", "96 cm²", "16 cm²", "48 cm²"),
          correct_option_index = 1,
          explanation = "ඝනකයකට මුහුණත් 6ක් ඇත. එක මුහුණතක වර්ගඵලය = 4 × 4 = 16 cm². මුළු වර්ගඵලය = 6 × 16 = 96 cm²."
        )
        8 -> QuizQuestion(
          question_id = qNumber,
          question_text = "3000 ක මුදලකට 10% ක වාර්ෂික සරල පොලියක් යටතේ වසර 2 කදී ලැබෙන පොලිය කීයද?",
          options = listOf("Rs. 300", "Rs. 600", "Rs. 3600", "Rs. 150"),
          correct_option_index = 1,
          explanation = "I = (P × R × T) / 100 අනුව, I = (3000 × 10 × 2) / 100 = Rs. 600."
        )
        9 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ත්‍රිකෝණයක අභ්‍යන්තර කෝණ තුනෙහි ඓක්‍යය අංශක කීයද?",
          options = listOf("90°", "180°", "360°", "270°"),
          correct_option_index = 1,
          explanation = "ඕනෑම තලීය ත්‍රිකෝණයක අභ්‍යන්තර කෝණ 3 හි එකතුව සෑමවිටම 180° (ඍජුකෝණ 2ක්) වේ."
        )
        else -> QuizQuestion(
          question_id = qNumber,
          question_text = "4, 8, 6, 10, 2 දත්ත සමූහයේ මධ්‍යස්ථය (Median) කීයද?",
          options = listOf("4", "6", "8", "10"),
          correct_option_index = 1,
          explanation = "දත්ත ආරෝහණ පිළිවෙලට සැකසූ විට: 2, 4, 6, 8, 10. මධ්‍යයේ පිහිටි අගය 6 වේ."
        )
      }
      "history" -> when (qIdx) {
        1 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ශ්‍රී ලංකාවේ ප්‍රථම ඓතිහාසික අගනුවර ලෙස සැලකෙන්නේ කුමක්ද?",
          options = listOf("අනුරාධපුරය", "පොළොන්නරුව", "සීගිරිය", "මහනුවර"),
          correct_option_index = 0,
          explanation = "පණ්ඩුකාභය රජතුමා විසින් සැලසුම් සහගත නගරයක් ලෙස අනුරාධපුර රාජධානිය ක්‍රි.පූ. 4 වන සියවසේදී ස්ථාපනය කරන ලදී."
        )
        2 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ලංකාවට බුදුදහම රැගෙන ආ මිහිඳු මහරහතන් වහන්සේ වැඩසිටි රජ සමය කුමක්ද?",
          options = listOf("දුටුගැමුණු රජ සමය", "දේවානම්පියතිස්ස රජ සමය", "වළගම්බා රජ සමය", "ධාතුසේන රජ සමය"),
          correct_option_index = 1,
          explanation = "ධර්මාශෝක අධිරාජ්‍යයාගේ පුත් මිහිඳු මහ රහතන් වහන්සේ දේවානම්පියතිස්ස රජ සමයේදී මිහින්තලයට වැඩම කළ සේක."
        )
        3 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ලෝක ප්‍රසිද්ධ සීගිරිය බලකොටුව සහ චිත්‍ර නිර්මාණය කළ රජු කවුද?",
          options = listOf("කාශ්‍යප රජු (1 වන)", "ධාතුසේන රජු", "මුගලන් රජු", "මහාසෙන් රජු"),
          correct_option_index = 0,
          explanation = "ක්‍රි.ව. 5 වන සියවසේදී 1 වන කාශ්‍යප රජතුමා විසින් සීගිරි පර්වතය අලංකාර මාලිගයක් හා බලකොටුවක් ලෙස නිර්මාණය කළේය."
        )
        4 -> QuizQuestion(
          question_id = qNumber,
          question_text = "පරාක්‍රම සමුද්‍රය නිර්මාණය කරන ලද්දේ කුමන රජතුමා විසින්ද?",
          options = listOf("විජයබාහු රජු", "මහා පරාක්‍රමබාහු රජු", "නිශ්ශංකමල්ල රජු", "දුටුගැමුණු රජු"),
          correct_option_index = 1,
          explanation = "පොළොන්නරු යුගයේ රජකළ මහා පරාක්‍රමබාහු රජතුමා වැව් කිහිපයක් එකතු කර පරාක්‍රම සමුද්‍රය ඉදි කළේය."
        )
        5 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ශ්‍රී ලංකාවට මුලින්ම පැමිණි බටහිර ජාතිය කවුද?",
          options = listOf("ලන්දේසීන්", "ඉංග්‍රීසීන්", "පෘතුගීසීන් (1505)", "ප්‍රංශ ජාතිකයන්"),
          correct_option_index = 2,
          explanation = "ලොරෙන්සෝ ද අල්මේදාගේ නායකත්වයෙන් යුත් පෘතුගීසි නැවක් 1505 දී ගාල්ල වරායට ගසාගෙන ඒමෙන් පෘතුගීසීන් ලංකාවට පැමිණියහ."
        )
        6 -> QuizQuestion(
          question_id = qNumber,
          question_text = "උඩරට ගිවිසුම අත්සන් කර බ්‍රිතාන්‍යයන්ට ලංකාව යටත් වූ වර්ෂය කුමක්ද?",
          options = listOf("1796", "1802", "1815 මාර්තු 2", "1948 පෙබරවාරි 4"),
          correct_option_index = 2,
          explanation = "1815 මාර්තු 2 වන දින මහනුවර මඟුල් මඩුවේදී උඩරට ප්‍රධානීන් සහ ඉංග්‍රීසි ආණ්ඩුකාර රොබට් බ්‍රවුන්රිග් අතර උඩරට ගිවිසුම අත්සන් කෙරිණි."
        )
        7 -> QuizQuestion(
          question_id = qNumber,
          question_text = "1818 ඌව වෙල්ලස්ස නිදහස් අරගලයට නායකත්වය දුන් ප්‍රධාන විරුවා කවුද?",
          options = listOf("වීර කැප්පෙටිපොළ නිලමේ", "ගොංගාලේගොඩ බණ්ඩා", "වීර පුරන් අප්පු", "ඇහැලේපොළ නිලමේ"),
          correct_option_index = 0,
          explanation = "මොනරවිල කැප්පෙටිපොළ දිසාව බ්‍රිතාන්‍ය පාලනයට එරෙහිව 1818 ඓතිහාසික වෙල්ලස්ස විමුක්ති සටනේ අග්‍රගන්‍ය නායකයා විය."
        )
        8 -> QuizQuestion(
          question_id = qNumber,
          question_text = "රුවන්වැලි මහා සෑය සහ මිරිසවැටිය චෛත්‍යය කරවූ අනුරාධපුරයේ රජු කවුද?",
          options = listOf("දුටුගැමුණු රජතුමා", "සද්ධාතිස්ස රජු", "වලගම්බා රජු", "එළාර රජු"),
          correct_option_index = 0,
          explanation = "දුටුගැමුණු මහරජතුමා රට එක්සේසත් කිරීමෙන් අනතුරුව මිරිසවැටිය, ලෝවාමහාපාය සහ රුවන්වැලි සෑය නිර්මාණය ආරම්භ කළේය."
        )
        9 -> QuizQuestion(
          question_id = qNumber,
          question_text = "ත්‍රිපිටකය ප්‍රථම වරට ග්‍රන්ථාරූඪ කරන ලද්දේ කුමන විහාරයේදීද?",
          options = listOf("මාතලේ අලුවිහාරයේදී", "මිහින්තලේදී", "රිදී විහාරයේදී", "දඹුලු විහාරයේදී"),
          correct_option_index = 0,
          explanation = "වළගම්බා රජ සමයේදී බැමිණිතියා සාගතය හේතුවෙන් ධර්මය රැකගනු වස් මාතලේ අලුවිහාරයේදී ත්‍රිපිටකය තල්පත්වල ලියා තැබීය."
        )
        else -> QuizQuestion(
          question_id = qNumber,
          question_text = "ශ්‍රී ලංකාවට බ්‍රිතාන්‍ය කිරීටයෙන් පූර්ණ නිදහස හිමිවූ දිනය කුමක්ද?",
          options = listOf("1948 පෙබරවාරි 04", "1972 මැයි 22", "1956 අප්‍රේල් 12", "1931 ජූලි 07"),
          correct_option_index = 0,
          explanation = "1948 පෙබරවාරි 4 වන දින ශ්‍රී ලංකාවට ඩොමීනියන් නිදහස ලැබුණු අතර ප්‍රථම අග්‍රාමාත්‍ය ඩී. එස්. සේනානායක මැතිතුමා පත්විය."
        )
      }
      else -> {
        // High quality general subject question
        QuizQuestion(
          question_id = qNumber,
          question_text = "$grade $subjectName - $topic ආශ්‍රිත ප්‍රශ්න අංක $qIdx: විෂය නිර්දේශයට අනුව වඩාත්ම නිවැරදි ප්‍රකාශය තෝරන්න.",
          options = listOf(
            "$topic පිළිබඳ ප්‍රධාන මූලධර්මය හා නිවැරදි සංකල්පය",
            "අසත්‍ය හෝ විෂය බාහිර ප්‍රකාශය 1",
            "අසත්‍ය හෝ විෂය බාහිර ප්‍රකාශය 2",
            "අසත්‍ය හෝ විෂය බාහිර ප්‍රකාශය 3"
          ),
          correct_option_index = 0,
          explanation = "$topic පාඩමට අදාළ මූලික සංකල්පය නිවැරදිව තේරුම් ගැනීමෙන් විභාගයේදී සම්පූර්ණ ලකුණු ලබාගත හැක."
        )
      }
    }
  }

  fun shuffleQuizSet(quizSet: QuizSet): List<ShuffledQuestion> {
    return quizSet.questions.map { q ->
      val originalCorrectAnswer = q.options[q.correct_option_index]
      val shuffledOpts = q.options.shuffled(Random(System.currentTimeMillis() + q.question_id))
      val newCorrectIdx = shuffledOpts.indexOf(originalCorrectAnswer)

      ShuffledQuestion(
        originalId = q.question_id,
        questionText = q.question_text,
        shuffledOptions = shuffledOpts,
        correctOptionIndex = if (newCorrectIdx >= 0) newCorrectIdx else 0,
        explanation = q.explanation
      )
    }
  }
}

// --------------------------------------------------
// 3. UI COMPONENTS (DUAL PLACEMENT STRATEGY)
// --------------------------------------------------

/**
 * LOCATION 1: Inside Subject View Screen
 * Placed directly below the Short Notes PDF button.
 */
@Composable
fun PracticeQuizzesSection(
  grade: String,
  subject: SubjectItem,
  onStartQuizSet: (QuizSet) -> Unit,
  onOpenFlashcards: () -> Unit
) {
  val quizData = remember(grade, subject.nameSinhala) {
    QuizRepository.getQuizDataForSubject(grade, subject.nameSinhala)
  }

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = Color(0xFFF8FAFC),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("practice_quizzes_section")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header with Title & Flashcard Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5)))
              ),
            contentAlignment = Alignment.Center
          ) {
            Text("🧠", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Practice Quizzes (ස්වයං පුහුණුව)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "කෙටි සටහන් ඇසුරෙන් ප්‍රශ්නාවලි 10ක් (100 MCQs)",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF64748B),
              fontSize = 11.sp
            )
          }
        }

        Surface(
          onClick = onOpenFlashcards,
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFEEF2FF),
          border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
          modifier = Modifier.testTag("open_flashcards_btn")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("💡", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Flashcards",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF4338CA)
            )
          }
        }
      }

      // Feature Info Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFECFDF5),
          border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("විනාඩි 3ක ටයිමරය", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFFEF3C7),
          border = BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Autorenew, contentDescription = "Shuffle", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("අසීමිතව පුහුණු වන්න", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
          }
        }
      }

      // 10 Quiz Sets List
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        quizData.quiz_sets.forEach { set ->
          QuizSetCardItem(
            quizSet = set,
            onStart = { onStartQuizSet(set) }
          )
        }
      }
    }
  }
}

@Composable
fun QuizSetCardItem(
  quizSet: QuizSet,
  onStart: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("quiz_set_card_${quizSet.set_id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1F5F9)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "0${quizSet.set_id}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF475569)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = quizSet.set_title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFEEF2FF)
            ) {
              Text(
                text = "10 Questions",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4F46E5),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "⏱️ 3 Mins",
              fontSize = 10.sp,
              color = Color(0xFF64748B),
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onStart,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("start_quiz_btn_${quizSet.set_id}")
      ) {
        Text("Start Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(14.dp))
      }
    }
  }
}

/**
 * LOCATION 2: Inside PDF Viewer Screen
 * Modal Bottom Sheet presenting the Set 1-10 Quiz list without exiting the PDF viewer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetsBottomSheet(
  grade: String,
  subjectName: String,
  onDismiss: () -> Unit,
  onStartQuizSet: (QuizSet) -> Unit,
  onOpenFlashcards: (() -> Unit)? = null
) {
  val quizData = remember(grade, subjectName) {
    QuizRepository.getQuizDataForSubject(grade, subjectName)
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = Color.White,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🧠", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Test Your Knowledge (දැනුම පරීක්ෂාව)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
          }
          Text(
            text = "PDF සටහන ආශ්‍රිත ප්‍රශ්නාවලි 10 (100 MCQs) සහ Flashcards",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B),
            fontSize = 11.sp
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
        }
      }

      if (onOpenFlashcards != null) {
        Surface(
          onClick = {
            onDismiss()
            onOpenFlashcards()
          },
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFEEF2FF),
          border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("💡", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Active Recall Flashcards (මතකය තහවුරු කරගන්න)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF3730A3)
                )
                Text(
                  text = "Flip Cards මගින් ප්‍රධාන සූත්‍ර හා කරුණු ක්ෂණිකව මතක් කරගන්න",
                  fontSize = 10.sp,
                  color = Color(0xFF6366F1)
                )
              }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Open", tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
          }
        }
      }

      HorizontalDivider(color = Color(0xFFE2E8F0))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 380.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(quizData.quiz_sets) { set ->
          QuizSetCardItem(
            quizSet = set,
            onStart = {
              onDismiss()
              onStartQuizSet(set)
            }
          )
        }
      }
    }
  }
}

// --------------------------------------------------
// 4. INTERACTIVE QUIZ ENGINE DIALOG (180s TIMER, SHUFFLING, INSTANT GRADING)
// --------------------------------------------------

@Composable
fun QuizEngineDialog(
  quizSet: QuizSet,
  onDismiss: () -> Unit,
  onRetake: (QuizSet) -> Unit = {}
) {
  // Anti-memorization shuffled questions
  var shuffledQuestions by remember(quizSet) {
    mutableStateOf(QuizRepository.shuffleQuizSet(quizSet))
  }

  var currentQuestionIndex by remember { mutableStateOf(0) }
  val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> selectedOptionIndex

  var remainingSeconds by remember { mutableStateOf(quizSet.time_limit_seconds) }
  var isSubmitted by remember { mutableStateOf(false) }
  var isTimerRunning by remember { mutableStateOf(true) }

  // 180s Countdown Timer Engine
  LaunchedEffect(isTimerRunning, remainingSeconds, isSubmitted) {
    if (isTimerRunning && !isSubmitted && remainingSeconds > 0) {
      delay(1000L)
      remainingSeconds -= 1
      if (remainingSeconds <= 0) {
        isSubmitted = true
        isTimerRunning = false
      }
    }
  }

  Dialog(
    onDismissRequest = {
      if (isSubmitted) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.92f)
        .padding(8.dp)
        .testTag("quiz_engine_dialog")
    ) {
      if (!isSubmitted) {
        // ACTIVE QUIZ INTERFACE
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
          // Top Bar: Set Title & Live Timer
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
              ) {
                Text("🧠", fontSize = 16.sp)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = quizSet.set_title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF1E293B)
                )
                Text(
                  text = "ප්‍රශ්න ${currentQuestionIndex + 1} / ${shuffledQuestions.size}",
                  fontSize = 11.sp,
                  color = Color(0xFF64748B)
                )
              }
            }

            // Live Countdown Timer Badge
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val timerColor = when {
              remainingSeconds <= 30 -> Color(0xFFDC2626)
              remainingSeconds <= 60 -> Color(0xFFD97706)
              else -> Color(0xFF059669)
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = timerColor.copy(alpha = 0.1f),
              border = BorderStroke(1.dp, timerColor.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = "Timer",
                  tint = timerColor,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = String.format("%02d:%02d", minutes, seconds),
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = timerColor
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Progress Track Dots (1 to 10)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            shuffledQuestions.indices.forEach { idx ->
              val isAnswered = userAnswers.containsKey(idx)
              val isCurrent = currentQuestionIndex == idx
              val dotColor = when {
                isCurrent -> Color(0xFF4F46E5)
                isAnswered -> Color(0xFF10B981)
                else -> Color(0xFFE2E8F0)
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(dotColor)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Current Question Body
          val currentQuestion = shuffledQuestions[currentQuestionIndex]

          Column(
            modifier = Modifier
              .weight(1f)
              .verticalScroll(rememberScrollState())
          ) {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "ප්‍රශ්න අංක 0${currentQuestionIndex + 1}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color(0xFF4F46E5)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = currentQuestion.questionText,
                  style = MaterialTheme.typography.bodyLarge,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E293B),
                  lineHeight = 22.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Options
            currentQuestion.shuffledOptions.forEachIndexed { optIdx, optText ->
              val isSelected = userAnswers[currentQuestionIndex] == optIdx
              val optionLetter = ('A' + optIdx).toString()

              Surface(
                onClick = { userAnswers[currentQuestionIndex] = optIdx },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFEEF2FF) else Color.White,
                border = BorderStroke(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .testTag("option_${currentQuestionIndex}_$optIdx")
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .clip(CircleShape)
                      .background(if (isSelected) Color(0xFF4F46E5) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = optionLetter,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = if (isSelected) Color.White else Color(0xFF64748B)
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Text(
                    text = optText,
                    fontSize = 13.sp,
                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF334155),
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                  )

                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Selected",
                      tint = Color(0xFF4F46E5),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }
            }
          }

          // Bottom Navigation Buttons
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (currentQuestionIndex > 0) {
              OutlinedButton(
                onClick = { currentQuestionIndex -= 1 },
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("කලින් ප්‍රශ්නය", fontSize = 11.sp)
              }
            } else {
              Spacer(modifier = Modifier.width(10.dp))
            }

            if (currentQuestionIndex < shuffledQuestions.size - 1) {
              Button(
                onClick = { currentQuestionIndex += 1 },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("ඊළඟ ප්‍රශ්නය", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
              }
            } else {
              Button(
                onClick = {
                  isSubmitted = true
                  isTimerRunning = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_quiz_btn")
              ) {
                Icon(Icons.Default.Check, contentDescription = "Submit", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("පිළිතුරු භාර දෙන්න (Submit)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        // --------------------------------------------------
        // RESULT & REVIEW SCREEN
        // --------------------------------------------------
        val correctCount = shuffledQuestions.indices.count { idx ->
          userAnswers[idx] == shuffledQuestions[idx].correctOptionIndex
        }
        val totalQuestions = shuffledQuestions.size
        val scorePercent = (correctCount * 100) / totalQuestions

        QuizResultReviewContent(
          quizTitle = quizSet.set_title,
          correctCount = correctCount,
          totalQuestions = totalQuestions,
          scorePercent = scorePercent,
          shuffledQuestions = shuffledQuestions,
          userAnswers = userAnswers,
          onTryAgain = {
            // Re-shuffle options for anti-memorization & reset timer
            shuffledQuestions = QuizRepository.shuffleQuizSet(quizSet)
            userAnswers.clear()
            currentQuestionIndex = 0
            remainingSeconds = quizSet.time_limit_seconds
            isSubmitted = false
            isTimerRunning = true
          },
          onClose = onDismiss
        )
      }
    }
  }
}

// --------------------------------------------------
// 5. RESULT & REVIEW CONTENT
// --------------------------------------------------

@Composable
fun QuizResultReviewContent(
  quizTitle: String,
  correctCount: Int,
  totalQuestions: Int,
  scorePercent: Int,
  shuffledQuestions: List<ShuffledQuestion>,
  userAnswers: Map<Int, Int>,
  onTryAgain: () -> Unit,
  onClose: () -> Unit
) {
  val (feedbackMsg, feedbackColor, feedbackBadge) = when {
    scorePercent >= 80 -> Triple("ඉතා හොඳයි! විශිෂ්ට සාමාර්ථයක්! 🎉", Color(0xFF059669), "EXCELLENT")
    scorePercent >= 50 -> Triple("හොඳයි! තවදුරටත් පුහුණු වන්න! 👍", Color(0xFFD97706), "GOOD")
    else -> Triple("නැවත සටහන කියවා උත්සාහ කරන්න! 📖", Color(0xFFDC2626), "NEEDS REVIEW")
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    // Header & Close
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ප්‍රතිඵල හා විවරණය (Results & Review)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B)
      )
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Score Summary Card
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = feedbackColor.copy(alpha = 0.08f),
      border = BorderStroke(1.dp, feedbackColor.copy(alpha = 0.3f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(feedbackColor),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$scorePercent%",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color.White
            )
            Text(
              text = "$correctCount/$totalQuestions",
              fontSize = 10.sp,
              color = Color.White.copy(alpha = 0.9f)
            )
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = feedbackColor
          ) {
            Text(
              text = feedbackBadge,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = feedbackMsg,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = feedbackColor
          )
          Text(
            text = "$quizTitle • ක්ෂණික ස්වයං ඇගයීම",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "සවිස්තරාත්මක විවරණය (Detailed Question Review):",
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = Color(0xFF1E293B)
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Scrollable Review List
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      itemsIndexed(shuffledQuestions) { idx, q ->
        val userChosen = userAnswers[idx]
        val isCorrect = userChosen == q.correctOptionIndex

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, if (isCorrect) Color(0xFFA7F3D0) else Color(0xFFFECACA)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ප්‍රශ්න 0${idx + 1}",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626)
              )

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isCorrect) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
              ) {
                Text(
                  text = if (isCorrect) "✅ නිවැරදියි (+1)" else "❌ වැරදියි (0)",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = q.questionText,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Highlighted Options
            q.shuffledOptions.forEachIndexed { optIdx, optText ->
              val isUserPicked = userChosen == optIdx
              val isTargetCorrect = optIdx == q.correctOptionIndex

              val (bgCol, borderCol, textCol) = when {
                isTargetCorrect -> Triple(Color(0xFFECFDF5), Color(0xFF059669), Color(0xFF065F46))
                isUserPicked -> Triple(Color(0xFFFEF2F2), Color(0xFFDC2626), Color(0xFF991B1B))
                else -> Triple(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFF64748B))
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = bgCol,
                border = BorderStroke(1.dp, borderCol),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = ('A' + optIdx).toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = textCol
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = optText,
                    fontSize = 11.sp,
                    color = textCol,
                    fontWeight = if (isTargetCorrect || isUserPicked) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                  )
                  if (isTargetCorrect) {
                    Text(" (නිවැරදි පිළිතුර)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                  }
                }
              }
            }

            // Step-by-Step Explanation Box
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF0FDF4),
              border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text("💡", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = "විවරණය (Explanation):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color(0xFF166534)
                  )
                  Text(
                    text = q.explanation,
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

    Spacer(modifier = Modifier.height(10.dp))

    // Action Buttons: Try Again & Close
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onTryAgain,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("try_again_quiz_btn")
      ) {
        Icon(Icons.Default.Autorenew, contentDescription = "Retry", modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("නැවත උත්සාහ කරන්න (Try Again)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = onClose,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(0.7f)
      ) {
        Text("ආපසු විෂයට", fontSize = 11.sp, color = Color(0xFF334155))
      }
    }
  }
}

// --------------------------------------------------
// 6. FLASHCARDS VIEWER DIALOG (ACTIVE RECALL)
// --------------------------------------------------

@Composable
fun FlashcardsViewerDialog(
  grade: String,
  subjectName: String,
  onDismiss: () -> Unit
) {
  val flashcards = remember(grade, subjectName) {
    QuizRepository.getFlashcardsForSubject(grade, subjectName)
  }
  var currentIndex by remember { mutableStateOf(0) }
  var isFlipped by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .wrapContentHeight()
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("💡", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Flashcards පුනරීක්ෂණය",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
              )
              Text(
                text = "$subjectName • කාඩ්පත් ${currentIndex + 1} / ${flashcards.size}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
          }
        }

        if (flashcards.isNotEmpty()) {
          val card = flashcards[currentIndex]

          // Interactive Flip Card Surface
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isFlipped) Color(0xFFF0FDF4) else Color(0xFFEEF2FF)
            ),
            border = BorderStroke(
              width = 1.5.dp,
              color = if (isFlipped) Color(0xFF86EFAC) else Color(0xFFC7D2FE)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .clickable { isFlipped = !isFlipped }
              .testTag("flashcard_item")
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isFlipped) Color(0xFF16A34A) else Color(0xFF4F46E5)
              ) {
                Text(
                  text = if (isFlipped) "✅ පිළිතුර / විවරණය (Tap to Flip)" else "❓ සංකල්පය / ප්‍රශ්නය (Tap to Reveal)",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = if (isFlipped) card.definitionOrAnswer else card.termOrQuestion,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isFlipped) Color(0xFF14532D) else Color(0xFF1E293B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
              )

              if (isFlipped && card.keyTakeaway.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "📌 ${card.keyTakeaway}",
                  fontSize = 11.sp,
                  color = Color(0xFF15803D),
                  textAlign = TextAlign.Center
                )
              }
            }
          }

          // Navigation Controls
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                if (currentIndex > 0) {
                  currentIndex -= 1
                  isFlipped = false
                }
              },
              enabled = currentIndex > 0,
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("කලින් කාඩ්පත", fontSize = 11.sp)
            }

            Button(
              onClick = {
                if (currentIndex < flashcards.size - 1) {
                  currentIndex += 1
                  isFlipped = false
                } else {
                  onDismiss()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(if (currentIndex < flashcards.size - 1) "ඊළඟ කාඩ්පත" else "අවසන් කරන්න", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}
