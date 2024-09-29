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
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
    private lateinit var  scrollViewLayout : ScrollView


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

        scrollViewLayout = view.findViewById(R.id.stats_scroll_view)

        val statsLayout: ConstraintLayout = view.findViewById(R.id.stats_layout)
        statsLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                statsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)

                val layoutWidth = statsLayout.measuredWidth

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
                statsLayout.addView(statsImageView)

                createMainStatsViews(context)

                createInspirationAndProficiencyBonusViews(context)

                createSavingThrows(context)

                createSkills(context)

                createPassiveWisdom(context)

                createProficienciesAndLanguages(context)
            }

            private fun stringToInt(text: String): Int {
                val value: Int = try {
                    text.toInt()
                } catch (e: NumberFormatException) {
                    val errorMessage = "Invalid number"
                    Log.w(TAG, errorMessage)
                    return Int.MIN_VALUE;
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
                    val mainStatEditText = createEditText(context, 75, 45, StatType.Stats, i)

                    // Creating text views for bonus stats
                    val bonusView = TextView(context)
                    bonusView.id = View.generateViewId()
                    bonusView.width = dpToPx(40)
                    bonusView.height = dpToPx(30)
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

                    // Need to add views in order so views in front are added last
                    statsLayout.addView(mainStatEditText)
                    statsLayout.addView(bonusView)

                    // Set constraints to views after views are added
                    setStartTopConstraints(
                        statsLayout,
                        mainStatEditText,
                        statsLayout,
                        45,
                        60 + i * 130
                    )
                    setStartTopConstraints(statsLayout, bonusView, mainStatEditText, 16, 55)
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
                val inspirationText = createEditText(context, 45, 40, StatType.Stats, Stats.INSPIRATION.ordinal)
                inspirationText.addTextChangedListener(CharacterStatUpdater(inspirationText, Stats.INSPIRATION.ordinal))
                statsLayout.addView(inspirationText)
                setStartTopConstraints(statsLayout, inspirationText, statsLayout, 150, 10)

                // Creating edit text for proficiency bonus
                val proficiencyText = createEditText(context, 45, 40, StatType.Stats, Stats.PROFICIENCY_BONUS.ordinal)
                proficiencyText.addTextChangedListener(
                    CharacterStatUpdater(
                        proficiencyText,
                        Stats.PROFICIENCY_BONUS.ordinal
                    )
                )
                statsLayout.addView(proficiencyText)
                setStartTopConstraints(statsLayout, proficiencyText, statsLayout, 152, 77)
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
                    val proficiencyButton = createSavingThrowsProficiencyRadioButton(context, 30, 30, savingThrow.ordinal)
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
                    statsLayout.addView(proficiencyButton)
                    setStartTopConstraints(statsLayout, proficiencyButton, statsLayout, 151, 148 + (savingThrow.ordinal * 24.9).toInt())

                    val savingThrowView = createEditText(context, 30, 10, StatType.SavingThrows, savingThrow.ordinal)
                    savingThrowView.addTextChangedListener(SavingThrowUpdater(savingThrowView, savingThrow))
                    statsLayout.addView(savingThrowView)
                    setStartTopConstraints(statsLayout, savingThrowView, statsLayout, 180, 143 + (savingThrow.ordinal * 24.9).toInt())
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
                    val proficiencyButton = createSkillsProficiencyRadioButton(context, 30, 30, skills.ordinal)
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
                    statsLayout.addView(proficiencyButton)
                    setStartTopConstraints(statsLayout, proficiencyButton, statsLayout, 151, 358 + (skills.ordinal * 24.7).toInt())

                    val skillsView = createEditText(context, 30, 10, StatType.Skills,  skills.ordinal)
                    skillsView.addTextChangedListener(SkillsUpdater(skillsView, skills))
                    statsLayout.addView(skillsView)
                    setStartTopConstraints(statsLayout, skillsView, statsLayout, 180, 354 + (skills.ordinal * 24.6).toInt())
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
                val passiveWisdomText = createEditText(context, 45, 40, StatType.Skills, Stats.PASSIVE_WISDOM.ordinal)
                passiveWisdomText.addTextChangedListener(CharacterStatUpdater(passiveWisdomText, Stats.PASSIVE_WISDOM.ordinal))
                statsLayout.addView(passiveWisdomText)
                setStartTopConstraints(statsLayout, passiveWisdomText, statsLayout, 32, 848)
            }

            // ToDo: Merge createSkillsProficiencyRadioButton and createSavingThrowsProficiencyRadioButton to single method
            private fun createSkillsProficiencyRadioButton(context: Context, width: Int, height: Int, i: Int): RadioButton {
                val radioButton = RadioButton(context)
                radioButton.id = View.generateViewId()
                radioButton.width = dpToPx(width)
                radioButton.height = dpToPx(height)
                radioButton.setBackgroundColor(Color.RED)
                radioButton.background.alpha = 50
                radioButton.isChecked = characterViewModel.skillsProficiencyBonuses[i]
                return radioButton
            }

            private fun createSavingThrowsProficiencyRadioButton(context: Context, width: Int, height: Int, i: Int): RadioButton {
                val radioButton = RadioButton(context)
                radioButton.id = View.generateViewId()
                radioButton.width = dpToPx(width)
                radioButton.height = dpToPx(height)
                radioButton.setBackgroundColor(Color.RED)
                radioButton.background.alpha = 50
                radioButton.isChecked = characterViewModel.savingThrowProficiencyBonuses[i]
                return radioButton
            }

            private fun createEditText(context: Context, width: Int, height: Int, type: StatType, i: Int): EditText {
                val editTextView = EditText(context)
                editTextView.id = View.generateViewId()
                editTextView.width = dpToPx(width)
                editTextView.height = dpToPx(height)
                editTextView.setBackgroundColor(Color.RED)
                editTextView.background.alpha = 50
                editTextView.textSize = 14f
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

            private fun createProficienciesAndLanguages(context: Context) {
                val editText = EditText(context)
                editText.id = View.generateViewId()
                editText.width = dpToPx(315)
                editText.height = dpToPx(258)
                editText.setBackgroundColor(Color.RED)
                editText.background.alpha = 50
                editText.textSize = 20f
                editText.gravity = Gravity.START or Gravity.TOP
                editText.setTextColor(Color.BLACK)
                editText.setText(characterViewModel.proficienciesAndLanguages)

                editText.addTextChangedListener (afterTextChanged = listener@{ editedText: Editable? ->
                    characterViewModel.proficienciesAndLanguages = editedText.toString()
                })

                statsLayout.addView(editText)
                setStartTopConstraints(statsLayout, editText, statsLayout, 36, 910)
            }

            private fun setStartTopConstraints(layout: ConstraintLayout, firstView: View,
                                               secondView: View,
                                               startMargin: Int, topMargin: Int) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)
                // Constraint in horizontal
                constraintSet.connect(
                    firstView.id, ConstraintSet.START,
                    secondView.id, ConstraintSet.START,
                    dpToPx(startMargin))

                // Constraint in vertical
                constraintSet.connect(
                    firstView.id, ConstraintSet.TOP,
                    secondView.id, ConstraintSet.TOP,
                    dpToPx(topMargin))
                constraintSet.applyTo(layout)
            }

            private fun dpToPx(dp: Number): Int {
                return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
                ).toInt()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        saveToJson()
        //saveToGoogleDocs(text)
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