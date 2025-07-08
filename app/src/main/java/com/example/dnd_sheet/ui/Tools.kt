package com.example.dnd_sheet.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.MainStats
import com.example.dnd_sheet.Character.SavingThrows
import com.example.dnd_sheet.Character.Skills
import com.example.dnd_sheet.Character.TypesForEditTexts
import com.example.dnd_sheet.R
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class Tools {
    companion object {
        private const val TAG: String = "Tools"

        // Name for json file
        private val fileName = "character.json"

        // Size of drawable image
        private lateinit var drawableSize: Size

        // Layout of the drawable
        private lateinit var drawableLayout: ConstraintLayout

        fun saveToJson(context: Context): File {
            val jsonString = Json.encodeToString(Character.getInstance())
            val filesDir = File(context.filesDir, "")

            val file = File(filesDir, fileName)
            file.writeText(jsonString)
            return file
        }

        /**
         * Loads character from JSON file
         * /param Character.getInstance() - view model of character where json will be loaded
         */
        fun loadFromLocalJson(context: Context): Character? {
            val filesDir = File(context.filesDir, "")
            filesDir.mkdirs()
            val file = File(filesDir, fileName)
            val errorPreMessage = "Failed to read file."
            if (!file.exists()) {
                Log.e(TAG, "$errorPreMessage File $fileName is missing")
                return null
            }
            val jsonString = file.readText()
            if (jsonString.isEmpty()) {
                Log.e(TAG, "$errorPreMessage File $fileName is empty")
                return null
            }
            try {
                return Character.getInstance(Json.decodeFromString<Character>(jsonString))
            } catch (e: Exception) {
                e.message?.let { Log.e(TAG, "$errorPreMessage $it") }
            }
            return null
        }

        /**
         * Sets layout to be used in Tools
         */
        fun setLayout(layout: ConstraintLayout) {
            drawableLayout = layout
        }

        fun drawableToLayout(drawable: Int, context: Context) {
            // Change layout width x height ratio to match background image
            val bitmap = BitmapFactory.decodeResource(context.resources, drawable)

            val layoutWidth = drawableLayout.measuredWidth

            // Using matrix to scale imageview to fit nicely to layout
            val matrix = Matrix()
            val scale = layoutWidth / bitmap.width.toFloat()
            matrix.postScale(scale, scale)

            val resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, false
            )

            // Recycle old bitmap to avoid memory leak
            bitmap.recycle()

            val imageView = ImageView(context)
            imageView.id = View.generateViewId()
            imageView.setImageBitmap(resizedBitmap)
            drawableLayout.addView(imageView)

            drawableSize = Size(resizedBitmap.width, resizedBitmap.height)
        }

        fun createEditText(
            width: Double,
            height: Double,
            type: TypesForEditTexts,
            enumOrdinal: Int? = null,
            textSize: Float = 14f,
            gravity: Int = Gravity.CENTER,
            inputType: Int = InputType.TYPE_CLASS_NUMBER,
            index: Int? = null,
            context: Context
        ): EditText {
            val editTextView = EditText(context)
            editTextView.id = View.generateViewId()
            editTextView.layoutParams = ViewGroup.LayoutParams(
                width.rawWidth(),
                height.rawHeight()
            )
            editTextView.textSize = textSize
            editTextView.gravity = gravity
            editTextView.inputType = inputType
            editTextView.setTextColor(Color.BLACK)
            editTextView.background = null

            when (type) {
                TypesForEditTexts.SKILLS -> enumOrdinal?.let { editTextView.setText(Character.getInstance().skills[enumOrdinal].toString()) }
                TypesForEditTexts.MAINSTATS -> enumOrdinal?.let { editTextView.setText(Character.getInstance().mainStats[enumOrdinal].toString()) }
                TypesForEditTexts.SAVING_THROWS -> enumOrdinal?.let { editTextView.setText(Character.getInstance().savingThrows[enumOrdinal].toString()) }
                TypesForEditTexts.ARMOR_CLASS -> editTextView.setText(Character.getInstance().armorClass.toString())
                TypesForEditTexts.INITIATIVE -> editTextView.setText(Character.getInstance().initiative.toString())
                TypesForEditTexts.SPEED -> editTextView.setText(Character.getInstance().speed.toString())
                TypesForEditTexts.PROFIENCIES_AND_LANGUAGES -> editTextView.setText(Character.getInstance().proficienciesAndLanguages)
                TypesForEditTexts.HIT_POINT_MAXIMUM -> editTextView.setText(Character.getInstance().hitpointMaximum.toString())
                TypesForEditTexts.CURRENT_HIT_POINTS -> editTextView.setText(Character.getInstance().currentHitpoint.toString())
                TypesForEditTexts.TEMPORARY_HIT_POINTS -> editTextView.setText(Character.getInstance().temporaryHitpoint.toString())
                TypesForEditTexts.HIT_DICE -> editTextView.setText(Character.getInstance().hitDice)
                TypesForEditTexts.HIT_DICE_TOTAL -> editTextView.setText(Character.getInstance().hitDiceTotal.toString())
                TypesForEditTexts.SUCCESSES -> editTextView.setText(Character.getInstance().successes.toString())
                TypesForEditTexts.FAILURES -> editTextView.setText(Character.getInstance().failures.toString())
                TypesForEditTexts.ATTACKS_SPELLCASTING -> {
                    if (index != null) {
                        editTextView.setText(
                            Character.getInstance().attacksSpellcasting[index][enumOrdinal] ?: ""
                        )
                    }
                }
                TypesForEditTexts.ATTACKS_SPELLCASTING_TEXT -> editTextView.setText(Character.getInstance().attacksSpellcastingText)
            }
            return editTextView
        }

        fun createMainStatsViews(context: Context) {
            for (i in 0..5) {
                // Creating edit texts for main stats
                val mainStatEditText = createEditText(0.14, 0.03, TypesForEditTexts.MAINSTATS, i, context = context)

                // Creating text views for bonus stats
                val bonusView = TextView(context)
                bonusView.id = View.generateViewId()
                bonusView.layoutParams = ViewGroup.LayoutParams(0.14.rawWidth(), 0.03.rawHeight())
                bonusView.textSize = 20f
                bonusView.gravity = Gravity.CENTER
                bonusView.text =
                    calculateSubValue(stringToInt(mainStatEditText.text.toString())).toString()
                bonusView.setTextColor(Color.BLACK)

                // Listener to update bonus stats when main stat is edited
                mainStatEditText.addTextChangedListener(afterTextChanged = listener@{ editedText: Editable? ->
                    // Logic for increasing/decreasing stat bonus based on typed main value
                    val mainValue: Int = stringToInt(editedText.toString())
                    if (mainValue == Int.MIN_VALUE) {
                        return@listener
                    }

                    val subValue: Int = calculateSubValue(mainValue)
                    bonusView.text = subValue.toString()

                    removeZerosFromBegin(mainStatEditText)

                    val stat: MainStats = MainStats.entries[i]
                    if (!stat.equals(null)) {
                        Character.getInstance().mainStats[stat.ordinal] = mainValue
                    }
                })

                setViewToLayout(mainStatEditText, 0.155 to (0.06 + i.toDouble() * 0.109))
                setViewToLayout(bonusView, 0.155 to (0.095 + i.toDouble() * 0.109))
            }
        }

        fun createInspirationAndProficiencyBonusViews(context: Context) {

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
                    Character.getInstance().mainStats[i] = value
                }
            }

            // Creating edit text for inspiration
            val inspirationText =
                createEditText(
                    0.14,
                    0.034,
                    TypesForEditTexts.MAINSTATS,
                    MainStats.INSPIRATION.ordinal,
                    context = context
                )
            inspirationText.addTextChangedListener(
                CharacterStatUpdater(
                    inspirationText,
                    MainStats.INSPIRATION.ordinal
                )
            )
            setViewToLayout(inspirationText, 0.41 to 0.01)

            // Creating edit text for proficiency bonus
            val proficiencyText = createEditText(
                0.145,
                0.034,
                TypesForEditTexts.MAINSTATS,
                MainStats.PROFICIENCY_BONUS.ordinal,
                context = context
            )
            proficiencyText.addTextChangedListener(
                CharacterStatUpdater(
                    proficiencyText,
                    MainStats.PROFICIENCY_BONUS.ordinal
                )
            )
            setViewToLayout(proficiencyText, 0.41 to 0.067)
        }

        fun createSavingThrows(context: Context) {
            class SavingThrowUpdater(val statText: EditText, val savingThrows: SavingThrows) :
                TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(newText: Editable?) {
                    val value = stringToInt(newText.toString())
                    if (value == Int.MIN_VALUE) {
                        return
                    }
                    removeZerosFromBegin(statText)
                    Character.getInstance().savingThrows[savingThrows.ordinal] = value
                }
            }

            // Creating edit texts for saving throws
            for (savingThrow in SavingThrows.entries) {
                val proficiencyButton =
                    createRadioButton(savingThrow.ordinal, TypesForEditTexts.SAVING_THROWS, context = context)

                setViewToLayout(
                    proficiencyButton,
                    0.445 to 0.129 + savingThrow.ordinal.toDouble() * 0.02065
                )

                val savingThrowView = createEditText(
                    0.08,
                    0.026,
                    TypesForEditTexts.SAVING_THROWS,
                    savingThrow.ordinal,
                    10f,
                    context = context
                )
                savingThrowView.addTextChangedListener(
                    SavingThrowUpdater(
                        savingThrowView,
                        savingThrow
                    )
                )
                setViewToLayout(
                    savingThrowView,
                    0.505 to 0.124 + savingThrow.ordinal.toDouble() * 0.0207
                )
            }
        }

        fun createSkills(context: Context) {
            class SkillsUpdater(val statText: EditText, val skills: Skills) : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(newText: Editable?) {
                    val value = stringToInt(newText.toString())
                    if (value == Int.MIN_VALUE) {
                        return
                    }
                    removeZerosFromBegin(statText)
                    Character.getInstance().skills[skills.ordinal] = value
                }
            }

            // Creating edit texts for skills
            for (skills in Skills.entries) {
                val proficiencyButton = createRadioButton(skills.ordinal, TypesForEditTexts.SKILLS, context = context)
                setViewToLayout(
                    proficiencyButton,
                    0.445 to 0.305 + skills.ordinal.toDouble() * 0.0206
                )

                val skillsView =
                    createEditText(0.07, 0.026, TypesForEditTexts.SKILLS, skills.ordinal, 10f, context = context)
                skillsView.addTextChangedListener(SkillsUpdater(skillsView, skills))
                setViewToLayout(skillsView, 0.5 to 0.3 + skills.ordinal.toDouble() * 0.02065)
            }
        }

        fun createPassiveWisdom(context: Context) {
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
                    Character.getInstance().mainStats[i] = value
                }
            }

            // Creating edit text for passive wisdom
            val passiveWisdomText = createEditText(
                0.145,
                0.04,
                TypesForEditTexts.MAINSTATS,
                MainStats.PASSIVE_WISDOM.ordinal,
                context = context
            )
            passiveWisdomText.addTextChangedListener(
                CharacterStatUpdater(
                    passiveWisdomText,
                    MainStats.PASSIVE_WISDOM.ordinal
                )
            )
            setViewToLayout(passiveWisdomText, 0.082 to 0.71)
        }

        fun createScrollableView(context: Context, editText: EditText, width: Double, height: Double): RelativeLayout {
            val layout = RelativeLayout(context)
            layout.id = View.generateViewId()
            layout.layoutParams = ViewGroup.LayoutParams(width.rawWidth(), height.rawHeight())

            val nestedScrollView = NestedScrollView(context)
            nestedScrollView.id = View.generateViewId()
            nestedScrollView.isFillViewport = true
            nestedScrollView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            nestedScrollView.smoothScrollBy(0, 0)

            layout.addView(nestedScrollView)

            editText.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            nestedScrollView.addView(editText)

            return layout
        }

        // ToDo: RadioButton icon is not same size for different display resolutions and dpi. Make something to match those
        fun createRadioButton(
            i: Int,
            type: TypesForEditTexts,
            width: Double = 0.038,
            height: Double = 0.012,
            context: Context
        ): RadioButton {
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()

            val layoutWidth = (width.rawWidth() * 1.5).toInt()
            val layoutHeight = (height.rawHeight() * 1.5).toInt()
            val layoutParams = LinearLayout.LayoutParams(layoutWidth, layoutHeight)
            layoutParams.gravity = Gravity.CENTER
            radioButton.layoutParams = layoutParams

            // Resize check and uncheck-drawables to correct size
            val checkedDrawable = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.baseline_radio_button_checked_24,
                null
            )
            val uncheckedDrawable = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.baseline_radio_button_unchecked_24,
                null
            )

            assert(uncheckedDrawable != null && checkedDrawable != null)

            val checkedBitmap = checkedDrawable!!.toBitmap()
            val uncheckedBitmap = uncheckedDrawable!!.toBitmap()

            val checkedResized =
                Bitmap.createScaledBitmap(checkedBitmap, width.rawWidth(), height.rawHeight(), true)
            val uncheckedResized =
                Bitmap.createScaledBitmap(
                    uncheckedBitmap,
                    width.rawWidth(),
                    height.rawHeight(),
                    true
                )

            val checkedResizedDrawable = BitmapDrawable(context.resources, checkedResized)
            val uncheckedResizedDrawable = BitmapDrawable(context.resources, uncheckedResized)

            radioButton.buttonDrawable = uncheckedResizedDrawable
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.buttonDrawable = if (isChecked) {
                    checkedResizedDrawable
                } else {
                    uncheckedResizedDrawable
                }
            }

            radioButton.setOnClickListener(object : View.OnClickListener {
                var m_previousCheck: Boolean = radioButton.isChecked

                override fun onClick(p0: View?) {
                    val button = p0 as RadioButton

                    if (button.isChecked && m_previousCheck) {
                        button.isChecked = false
                        m_previousCheck = false
                    } else if (button.isChecked) {
                        m_previousCheck = true
                    }
                    when (type) {
                        TypesForEditTexts.SKILLS -> Character.getInstance().skillsProficiencyBonuses[i] =
                            button.isChecked

                        TypesForEditTexts.SAVING_THROWS -> Character.getInstance().savingThrowProficiencyBonuses[i] =
                            button.isChecked

                        TypesForEditTexts.SUCCESSES -> Character.getInstance().successes[i] =
                            button.isChecked

                        TypesForEditTexts.FAILURES -> Character.getInstance().failures[i] =
                            button.isChecked

                        else -> {}
                    }
                }
            })

            when (type) {
                TypesForEditTexts.SKILLS -> radioButton.isChecked =
                    Character.getInstance().skillsProficiencyBonuses[i]

                TypesForEditTexts.SAVING_THROWS -> radioButton.isChecked =
                    Character.getInstance().savingThrowProficiencyBonuses[i]

                TypesForEditTexts.SUCCESSES -> radioButton.isChecked =
                    Character.getInstance().successes[i]

                TypesForEditTexts.FAILURES -> radioButton.isChecked =
                    Character.getInstance().failures[i]

                else -> {}
            }
            return radioButton
        }

        fun setViewToLayout(view: View, position: Pair<Double, Double>) {
            drawableLayout.addView(view)

            val constraintSet = ConstraintSet()
            constraintSet.clone(drawableLayout)
            // Constraint in horizontal
            constraintSet.connect(
                view.id, ConstraintSet.START,
                drawableLayout.id, ConstraintSet.START,
                position.first.rawWidth()
            )

            // Constraint in vertical
            constraintSet.connect(
                view.id, ConstraintSet.TOP,
                drawableLayout.id, ConstraintSet.TOP,
                position.second.rawHeight()
            )

            constraintSet.applyTo(drawableLayout)
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

        private fun removeZerosFromBegin(editText: EditText) {
            val textString = editText.text
            if (textString[0] == '0' && textString.length > 1) {
                editText.setText(textString.removeRange(0, 1))
                // Set cursor to the end of edit text after removing "0"
                editText.setSelection(editText.length())
            }
        }

        fun stringToInt(text: String): Int {
            val value: Int = try {
                text.toInt()
            } catch (e: NumberFormatException) {
                Log.w(TAG, "Invalid number")
                return Int.MIN_VALUE
            }
            return value
        }

        private fun Double.rawWidth(): Int {
            return (this * drawableSize.width).toInt()
        }

        fun Double.rawHeight(): Int {
            return (this * drawableSize.height).toInt()
        }

        fun checkContext(context: Context?): Context {
            return context ?: throw Exception("Context is null")
        }
    }
}