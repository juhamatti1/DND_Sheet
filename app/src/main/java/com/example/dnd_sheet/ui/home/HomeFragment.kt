package com.example.dnd_sheet.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.SavingThrows
import com.example.dnd_sheet.Character.Skills
import com.example.dnd_sheet.Character.StatType
import com.example.dnd_sheet.Character.Stats
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentHomeBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

class HomeFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentHomeBinding? = null
    private val TAG: String = "HomeFragment"
    private lateinit var characterViewModel: Character
    private val name = "character.json"
    private lateinit var  m_statsLayout : ConstraintLayout
    private lateinit var m_statsSize : Pair<Int, Int>


    // This property is only valid between onCreateView and
    // onDestroyView.
    // get() is property method which is called when variable is read
    // So when binding variable is used get() method is called and get()
    // returns _binding. "!!" marks is "non-null assertion operator" which means that we are
    // telling to Kotlin's type system _binding is not null.
    // This is pretty hacky to me.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        characterViewModel = ViewModelProvider(this)[Character::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        m_statsLayout = view.findViewById(R.id.stats_layout)

        m_statsLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                m_statsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)

                val layoutWidth = m_statsLayout.measuredWidth

                // Using matrix to scale imageview to fit nicely to layout
                val matrix = Matrix()
                val scale = layoutWidth / statsBitmap.width.toFloat()
                matrix.postScale(scale, scale)

                val resizedBitmap = Bitmap.createBitmap(statsBitmap, 0, 0, statsBitmap.width,
                    statsBitmap.height, matrix, false)

                // Recycle old bitmap to avoid memory leak
                statsBitmap.recycle()

                val context = requireContext()

                val statsImageView = ImageView(context)
                statsImageView.id = View.generateViewId()
                statsImageView.setImageBitmap(resizedBitmap)
                m_statsLayout.addView(statsImageView)

                m_statsSize = resizedBitmap.width to resizedBitmap.height
                m_statsSize.first.toDouble() / m_statsSize.second.toDouble()

                createMainStatsViews(context)

                createInspirationAndProficiencyBonusViews(context)

                createSavingThrows(context)

                createSkills(context)

                createPassiveWisdom(context)

                createProficienciesAndLanguages(context)

                // drawing grid
//                val canvas = Canvas(resizedBitmap)
//                val paint = Paint()
//                paint.color = Color.BLUE
//                paint.strokeWidth = 5f
//                paint.alpha = 60
//                for(i in 1..9) {
//
//                    var startX = 0f
//                    var startY = i.toFloat()*0.1f * m_statsSize.second.toFloat()
//                    var endX = m_statsSize.first.toFloat()
//                    var endY = startY
//
//                    canvas.drawLine(startX, startY, endX, endY, paint)
//
//                    startY = 0f
//                    startX = i*0.1f * m_statsSize.first.toFloat()
//                    endY = m_statsSize.second.toFloat()
//                    endX = startX
//
//                    canvas.drawLine(startX, startY, endX, endY, paint)
//                }
//                statsImageView.invalidate()
            }

            private fun stringToInt(text: String): Int {
                val value: Int = try {
                    text.toInt()
                } catch (e: NumberFormatException) {
                    Log.w(TAG, "Invalid number")
                    return Int.MIN_VALUE
                }
                return value
            }


            // -------------------------------------------------------------------------------------
            // HELPER FUNCTIONS --------------------------------------------------------------------
            // -------------------------------------------------------------------------------------
            private fun removeZerosFromBegin(editText: EditText) {
                val textString = editText.text
                if (textString[0] == '0' && textString.length > 1) {
                    editText.setText(textString.removeRange(0, 1))
                    // Set cursor to the end of edit text after removing "0"
                    editText.setSelection(editText.length())
                }
            }

            private fun createMainStatsViews(context: Context) {
                for (i in 0 .. 5) {
                    // Creating edit texts for main stats
                    val mainStatEditText = createEditText(context, 0.14, 0.03, StatType.Stats, i)

                    // Creating text views for bonus stats
                    val bonusView = TextView(context)
                    bonusView.id = View.generateViewId()
                    bonusView.layoutParams = ViewGroup.LayoutParams(0.14.ratioToWidth(), 0.03.ratioToHeigth())
                    bonusView.setBackgroundColor(Color.BLUE)
                    bonusView.background.alpha = 50
                    bonusView.textSize = 20f
                    bonusView.gravity = Gravity.CENTER
                    bonusView.text =
                        calculateSubValue(stringToInt(mainStatEditText.text.toString())).toString()

                    // Listener to update bonus stats when main stat is edited
                    mainStatEditText.addTextChangedListener(afterTextChanged = listener@{ editedText: Editable? ->
                        // Logic for increasing/decreasing stat bonus based on typed main value
                        val mainValue: Int = stringToInt(editedText.toString())
                        if(mainValue == Int.MIN_VALUE) {
                            return@listener
                        }

                        val subValue: Int = calculateSubValue(mainValue)
                        bonusView.text = subValue.toString()

                        removeZerosFromBegin(mainStatEditText)

                        val stat: Stats = Stats.entries[i]
                        if (!stat.equals(null)) {
                            characterViewModel.stats[stat.ordinal] = mainValue
                        }
                    })

                    setViewToStatsLayout(mainStatEditText, 0.155 to (0.06 + i.toDouble() * 0.109))
                    setViewToStatsLayout(bonusView, 0.155 to (0.095 + i.toDouble() * 0.109))
                }
            }

            private fun calculateSubValue(mainValue: Int): Int {
                var subValue: Int = (mainValue - 10) / 2
                val fragmentValue = (mainValue - 10) % 2
                if (fragmentValue > 0) {
                    subValue += 1
                } else if (fragmentValue < 0) {
                    subValue -= 1
                }
                return subValue
            }

            private fun createInspirationAndProficiencyBonusViews(context: Context) {
                class CharacterStatUpdater(val statText: EditText, val i: Int) : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(newText: Editable?) {
                        val toastText: String
                        val value: Int = try {
                            newText.toString().toInt()
                        } catch (e: NumberFormatException) {
                            toastText = "Invalid number"
                            Log.e(TAG, toastText)
                            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                            return
                        }
                        removeZerosFromBegin(statText)
                        characterViewModel.stats[i] = value
                    }
                }

                // Creating edit text for inspiration
                val inspirationText = createEditText(context, 0.14, 0.034, StatType.Stats, Stats.INSPIRATION.ordinal)
                inspirationText.addTextChangedListener(CharacterStatUpdater(inspirationText, Stats.INSPIRATION.ordinal))
                setViewToStatsLayout(inspirationText, 0.41 to 0.01)

                // Creating edit text for proficiency bonus
                val proficiencyText = createEditText(context, 0.145,0.034, StatType.Stats, Stats.PROFICIENCY_BONUS.ordinal)
                proficiencyText.addTextChangedListener(
                    CharacterStatUpdater(
                        proficiencyText,
                        Stats.PROFICIENCY_BONUS.ordinal
                    )
                )
                setViewToStatsLayout(proficiencyText, 0.41 to 0.067)
            }

            private fun createSavingThrows(context: Context) {
                class SavingThrowUpdater(val statText: EditText, val savingThrows: SavingThrows) : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(newText: Editable?) {
                        val value = stringToInt(newText.toString())
                        if(value == Int.MIN_VALUE) {
                            return
                        }
                        removeZerosFromBegin(statText)
                        characterViewModel.savingThrows[savingThrows.ordinal] = value
                    }
                }

                // Creating edit texts for saving throws
                for(savingThrow in SavingThrows.entries) {
                    val proficiencyButton = createRadioButton(context, 0.07, 0.02, savingThrow.ordinal, StatType.SavingThrows)
                    val status = proficiencyButton.isChecked
                    proficiencyButton.setOnClickListener(object : View.OnClickListener {
                        var m_previousStatus: Boolean = status

                        override fun onClick(p0: View?) {
                            val button = p0 as RadioButton
                            if(m_previousStatus == button.isChecked) {
                                m_previousStatus = !button.isChecked
                            } else {
                                m_previousStatus = button.isChecked
                            }
                            button.isChecked = m_previousStatus
                            characterViewModel.savingThrowProficiencyBonuses[savingThrow.ordinal] = m_previousStatus
                        }
                    })
                    setViewToStatsLayout(proficiencyButton, 0.425 to 0.127 + savingThrow.ordinal.toDouble() * 0.0207)

                    val savingThrowView = createEditText(context, 0.08, 0.026, StatType.SavingThrows, savingThrow.ordinal, 10f)
                    savingThrowView.addTextChangedListener(SavingThrowUpdater(savingThrowView, savingThrow))
                    setViewToStatsLayout(savingThrowView, 0.505 to 0.124+ savingThrow.ordinal.toDouble() * 0.0207)
                }
            }

            private fun createSkills(context: Context) {
                class SkillsUpdater(val statText: EditText, val skills: Skills) : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(newText: Editable?) {
                        val value = stringToInt(newText.toString())
                        if(value == Int.MIN_VALUE) {
                            return
                        }
                        removeZerosFromBegin(statText)
                        characterViewModel.skills[skills.ordinal] = value
                    }
                }

                // Creating edit texts for skills
                for(skills in Skills.entries) {
                    val proficiencyButton = createRadioButton(context, 0.07, 0.02, skills.ordinal, StatType.Skills)
                    val status = proficiencyButton.isChecked
                    proficiencyButton.setOnClickListener(object : View.OnClickListener {
                        var m_previousStatus: Boolean = status

                        override fun onClick(p0: View?) {
                            val button = p0 as RadioButton
                            if(m_previousStatus == button.isChecked) {
                                m_previousStatus = !button.isChecked
                            } else {
                                m_previousStatus = button.isChecked
                            }
                            button.isChecked = m_previousStatus
                            characterViewModel.skillsProficiencyBonuses[skills.ordinal] = m_previousStatus
                        }
                    })
                    setViewToStatsLayout(proficiencyButton, 0.425 to 0.303 + skills.ordinal.toDouble() * 0.02065)

                    val skillsView = createEditText(context, 0.07, 0.026, StatType.Skills,  skills.ordinal, 10f)
                    skillsView.addTextChangedListener(SkillsUpdater(skillsView, skills))
                    setViewToStatsLayout(skillsView,  0.5 to 0.3016 + skills.ordinal.toDouble() * 0.02065)
                }
            }

            private fun createPassiveWisdom(context: Context) {
                class CharacterStatUpdater(val statText: EditText, val i: Int) : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(newText: Editable?) {
                        val value: Int = try {
                            newText.toString().toInt()
                        } catch (e: NumberFormatException) {
                            return
                        }
                        removeZerosFromBegin(statText)
                        characterViewModel.stats[i] = value
                    }
                }

                // Creating edit text for passive wisdom
                val passiveWisdomText = createEditText(context, 0.145, 0.04, StatType.Skills, Stats.PASSIVE_WISDOM.ordinal)
                passiveWisdomText.addTextChangedListener(CharacterStatUpdater(passiveWisdomText, Stats.PASSIVE_WISDOM.ordinal))
                setViewToStatsLayout(passiveWisdomText, 0.082 to 0.71)
            }

            private fun createProficienciesAndLanguages(context: Context) {
                val layout = RelativeLayout(context)
                layout.id = View.generateViewId()
                layout.layoutParams = ViewGroup.LayoutParams(0.85.ratioToWidth(), 0.205.ratioToHeigth())
                setViewToStatsLayout(layout, 0.1 to 0.765)

                val nestedScrollView = NestedScrollView(context)
                nestedScrollView.id = View.generateViewId()
                nestedScrollView.isFillViewport = true
                nestedScrollView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

                layout.addView(nestedScrollView)

                val editText = EditText(context)
                editText.id = View.generateViewId()
                editText.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                editText.setBackgroundColor(Color.RED)
                editText.background.alpha = 50
                editText.setTextColor(Color.BLACK)
                editText.textSize = 15f
                editText.gravity = Gravity.START or Gravity.TOP
                editText.setText(characterViewModel.proficienciesAndLanguages)
                editText.addTextChangedListener (afterTextChanged = { editedText: Editable? ->
                    characterViewModel.proficienciesAndLanguages = editedText.toString()
                })

                nestedScrollView.addView(editText)
            }

            // ToDo: RadioButton icon is not same size for different display resolutions and dpi. Make something to match those
            private fun createRadioButton(context: Context, width: Double, height: Double, i: Int, type: StatType): RadioButton {
                val radioButton = RadioButton(context)
                radioButton.id = View.generateViewId()
                radioButton.layoutParams = ViewGroup.LayoutParams(width.ratioToWidth(),
                                                                  height.ratioToHeigth())

                radioButton.setBackgroundColor(Color.RED)
                radioButton.background.alpha = 50
                when(type) {
                    StatType.Skills -> radioButton.isChecked = characterViewModel.skillsProficiencyBonuses[i]
                    StatType.SavingThrows -> radioButton.isChecked = characterViewModel.savingThrowProficiencyBonuses[i]
                    else -> {}
                }
                return radioButton
            }

            private fun createEditText(context: Context, width: Double, height: Double, type: StatType, i: Int, textSize: Float = 14f): EditText {
                val editTextView = EditText(context)
                editTextView.id = View.generateViewId()
                editTextView.layoutParams = ViewGroup.LayoutParams(width.ratioToWidth(),
                                                                   height.ratioToHeigth())
                editTextView.setBackgroundColor(Color.RED)
                editTextView.background.alpha = 50
                editTextView.textSize = textSize
                editTextView.gravity = Gravity.CENTER
                editTextView.inputType = InputType.TYPE_CLASS_NUMBER
                editTextView.setTextColor(Color.BLACK)

                when(type) {
                    StatType.Skills -> editTextView.setText(characterViewModel.skills[i].toString())
                    StatType.Stats -> editTextView.setText(characterViewModel.stats[i].toString())
                    StatType.SavingThrows -> editTextView.setText(characterViewModel.savingThrows[i].toString())
                }
                return editTextView
            }

            private fun Double.ratioToWidth(): Int {
                return (this * m_statsSize.first).toInt()
            }

            private fun Double.ratioToHeigth(): Int {
                return (this * m_statsSize.second).toInt()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        saveToJson()
        //saveToGoogleDocs(text)
    }

    private fun setViewToStatsLayout(view: View, position: Pair<Double, Double>) {
        m_statsLayout.addView(view)

        val constraintSet = ConstraintSet()
        constraintSet.clone(m_statsLayout)
        // Constraint in horizontal
        constraintSet.connect(
            view.id, ConstraintSet.START,
            m_statsLayout.id, ConstraintSet.START,
            (m_statsSize.first.toDouble() * position.first).toInt())

        // Constraint in vertical
        constraintSet.connect(
            view.id, ConstraintSet.TOP,
            m_statsLayout.id, ConstraintSet.TOP,
            (m_statsSize.second.toDouble() * position.second).toInt())
        constraintSet.applyTo(m_statsLayout)
    }

    private fun saveToGoogleDocs(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text) // Replace with your JSON data
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage("com.google.android.apps.docs")
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    private fun saveToJson() : String {
        val text = Json.encodeToString(characterViewModel)
        requireContext().openFileOutput(name, Context.MODE_PRIVATE).use {
            it.write(text.toByteArray())
            it.close()
        }
        return text
    }


    private fun loadFromJson(): Boolean {
        val bytes: ByteArray
        try {
            requireContext().openFileInput(name).use {
                bytes = it.readBytes()
                it.close()
            }
        } catch (e: FileNotFoundException) {
            Log.w(TAG, "\"$name\" file not found")
            return false
        }
        var character: Character? = null
        val error = kotlin.runCatching {
            character = Json.decodeFromString<Character>(bytes.decodeToString())
        }
        if(character == null || error.isFailure) {
            Log.e(TAG, error.exceptionOrNull().toString())
            return false
        }
        characterViewModel = character as Character
        return true
    }

    override fun onResume() {
        super.onResume()
        // Check if there is already local character file. Load it if yes
        loadFromJson()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}