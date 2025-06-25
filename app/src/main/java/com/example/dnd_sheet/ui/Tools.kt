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
import com.example.dnd_sheet.Character.SavingThrows
import com.example.dnd_sheet.Character.Skills
import com.example.dnd_sheet.Character.MainStats
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
        // Context of the Main activity
        private lateinit var context: Context
        // View model keeps data alive as long Main activity is alive (Main activity is the owner)
        private lateinit var characterViewModel: Character
        // Size of drawable image
        private lateinit var drawableSize: Size
        // Layout of the drawable
        private lateinit var drawableLayout: ConstraintLayout

        fun init(context: Context, characterViewModel: Character) {
            this.context = context
            this.characterViewModel = characterViewModel
        }

        fun saveToJson(): File {
            val jsonString = Json.encodeToString(characterViewModel)

            val filesDir = File(context.filesDir, "")

            val file = File(filesDir, fileName)
            file.writeText(jsonString)
            Log.i(TAG, "Saved to:$filesDir\\$fileName")
            Log.i(TAG, "JSON:$jsonString")
            return file
        }

        /**
         * Loads character from JSON file
         * /param characterViewModel - view model of character where json will be loaded
         */
        fun loadFromLocalJson(): Character? {
            val filesDir = File(context.filesDir, "")
            filesDir.mkdirs()
            val file = File(filesDir, fileName)
            val errorPreMessage = "Failed to read file."
            if(!file.exists()) {
                Log.e(TAG, "$errorPreMessage File $fileName is missing")
                return null
            }
            val jsonString = file.readText()
            if(jsonString.isEmpty()) {
                Log.e(TAG, "$errorPreMessage File $fileName is empty")
                return null
            }
            try {
                characterViewModel = Json.decodeFromString<Character>(jsonString)
                return characterViewModel
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

        fun drawableToLayout(drawable: Int) {
            // Change layout width x height ratio to match background image
            val bitmap = BitmapFactory.decodeResource(context.resources, drawable)

            val layoutWidth = drawableLayout.measuredWidth

            // Using matrix to scale imageview to fit nicely to layout
            val matrix = Matrix()
            val scale = layoutWidth / bitmap.width.toFloat()
            matrix.postScale(scale, scale)

            val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, false)

            // Recycle old bitmap to avoid memory leak
            bitmap.recycle()

            val imageView = ImageView(context)
            imageView.id = View.generateViewId()
            imageView.setImageBitmap(resizedBitmap)
            drawableLayout.addView(imageView)

            drawableSize = Size(resizedBitmap.width, resizedBitmap.height)
        }

        fun createEditText(width: Double, height: Double, type: TypesForEditTexts, index: Int?, textSize: Float = 14f): EditText {
            val editTextView = EditText(this.context)
            editTextView.id = View.generateViewId()
            editTextView.layoutParams = ViewGroup.LayoutParams(width.rawWidth(),
                height.rawHeight())
            editTextView.textSize = textSize
            editTextView.gravity = Gravity.CENTER
            editTextView.inputType = InputType.TYPE_CLASS_NUMBER
            editTextView.setTextColor(Color.BLACK)

            when(type) {
                TypesForEditTexts.Skills -> index?.let{ editTextView.setText(characterViewModel.skills[index].toString()) }
                TypesForEditTexts.MainStats -> index?.let{ editTextView.setText(characterViewModel.mainStats[index].toString()) }
                TypesForEditTexts.SavingThrows -> index?.let{ editTextView.setText(characterViewModel.savingThrows[index].toString()) }
                TypesForEditTexts.ARMOR_CLASS -> editTextView.setText(characterViewModel.armorClass.toString())
                TypesForEditTexts.INITIATIVE -> editTextView.setText(characterViewModel.initiative.toString())
                TypesForEditTexts.SPEED -> editTextView.setText(characterViewModel.speed.toString())
                TypesForEditTexts.HIT_POINT_MAXIMUM -> editTextView.setText(characterViewModel.hitpointMaximum.toString())
                TypesForEditTexts.CURRENT_HIT_POINTS -> editTextView.setText(characterViewModel.currentHitpoint.toString())
                TypesForEditTexts.TEMPORARY_HIT_POINTS -> editTextView.setText(characterViewModel.temporaryHitpoint.toString())
                TypesForEditTexts.HIT_DICE -> editTextView.setText(characterViewModel.hitDice.toString())
                TypesForEditTexts.HIT_DICE_TOTAL -> editTextView.setText(characterViewModel.hitDiceTotal.toString())
                TypesForEditTexts.SUCCESSES -> editTextView.setText(characterViewModel.successes.toString())
                TypesForEditTexts.FAILURES -> editTextView.setText(characterViewModel.failures.toString())
                else -> {}
            }
            return editTextView
        }

        fun createMainStatsViews() {
            for (i in 0 .. 5) {
                // Creating edit texts for main stats
                val mainStatEditText = createEditText(0.14, 0.03, TypesForEditTexts.MainStats, i)

                // Creating text views for bonus stats
                val bonusView = TextView(this.context)
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
                    if(mainValue == Int.MIN_VALUE) {
                        return@listener
                    }

                    val subValue: Int = calculateSubValue(mainValue)
                    bonusView.text = subValue.toString()

                    removeZerosFromBegin(mainStatEditText)

                    val stat: MainStats = MainStats.entries[i]
                    if (!stat.equals(null)) {
                        characterViewModel.mainStats[stat.ordinal] = mainValue
                    }
                })

                setViewToLayout(mainStatEditText, 0.155 to (0.06 + i.toDouble() * 0.109))
                setViewToLayout(bonusView, 0.155 to (0.095 + i.toDouble() * 0.109))
            }
        }

        fun createInspirationAndProficiencyBonusViews() {

            val context2 = this.context
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
                        Toast.makeText(context2, toastText, Toast.LENGTH_SHORT).show()
                        return
                    }
                    removeZerosFromBegin(statText)
                    characterViewModel.mainStats[i] = value
                }
            }

            // Creating edit text for inspiration
            val inspirationText = createEditText(0.14, 0.034, TypesForEditTexts.MainStats, MainStats.INSPIRATION.ordinal)
            inspirationText.addTextChangedListener(CharacterStatUpdater(inspirationText, MainStats.INSPIRATION.ordinal))
            setViewToLayout(inspirationText, 0.41 to 0.01)

            // Creating edit text for proficiency bonus
            val proficiencyText = createEditText(0.145,0.034, TypesForEditTexts.MainStats, MainStats.PROFICIENCY_BONUS.ordinal)
            proficiencyText.addTextChangedListener(
                CharacterStatUpdater(
                    proficiencyText,
                    MainStats.PROFICIENCY_BONUS.ordinal
                )
            )
            setViewToLayout(proficiencyText, 0.41 to 0.067)
        }

        fun createSavingThrows() {
            class SavingThrowUpdater(val statText: EditText, val savingThrows: SavingThrows) :
                TextWatcher {
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
                val proficiencyButton = createRadioButton(savingThrow.ordinal, TypesForEditTexts.SavingThrows)

                setViewToLayout(proficiencyButton, 0.445 to 0.129 + savingThrow.ordinal.toDouble() * 0.02065)

                val savingThrowView = createEditText(0.08, 0.026, TypesForEditTexts.SavingThrows, savingThrow.ordinal, 10f)
                savingThrowView.addTextChangedListener(SavingThrowUpdater(savingThrowView, savingThrow))
                setViewToLayout(savingThrowView, 0.505 to 0.124+ savingThrow.ordinal.toDouble() * 0.0207)
            }
        }

        fun createSkills() {
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
                val proficiencyButton = createRadioButton(skills.ordinal, TypesForEditTexts.Skills)
                setViewToLayout(proficiencyButton, 0.445 to 0.305 + skills.ordinal.toDouble() * 0.0206)

                val skillsView = createEditText(0.07, 0.026, TypesForEditTexts.Skills,  skills.ordinal, 10f)
                skillsView.addTextChangedListener(SkillsUpdater(skillsView, skills))
                setViewToLayout(skillsView,  0.5 to 0.3 + skills.ordinal.toDouble() * 0.02065)
            }
        }

        fun createPassiveWisdom() {
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
                    characterViewModel.mainStats[i] = value
                }
            }

            // Creating edit text for passive wisdom
            val passiveWisdomText = createEditText(0.145, 0.04, TypesForEditTexts.MainStats, MainStats.PASSIVE_WISDOM.ordinal)
            passiveWisdomText.addTextChangedListener(CharacterStatUpdater(passiveWisdomText, MainStats.PASSIVE_WISDOM.ordinal))
            setViewToLayout(passiveWisdomText, 0.082 to 0.71)
        }

        fun createProficienciesAndLanguages() {
            val layout = RelativeLayout(this.context)
            layout.id = View.generateViewId()
            layout.layoutParams = ViewGroup.LayoutParams(0.85.rawWidth(), 0.205.rawHeight())
            setViewToLayout(layout, 0.1 to 0.765)

            val nestedScrollView = NestedScrollView(this.context)
            nestedScrollView.id = View.generateViewId()
            nestedScrollView.isFillViewport = true
            nestedScrollView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            layout.addView(nestedScrollView)

            val editText = EditText(this.context)
            editText.id = View.generateViewId()
            editText.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
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
        private fun createRadioButton(i: Int, type: TypesForEditTexts): RadioButton {
            val radioButton = RadioButton(this.context)
            val width = 0.038
            val height = 0.012
            radioButton.id = View.generateViewId()

            val layoutWidth = (width.rawWidth() * 1.5).toInt()
            val layoutHeight = (height.rawHeight() * 1.5).toInt()
            val layoutParams = LinearLayout.LayoutParams(layoutWidth, layoutHeight)
            layoutParams.gravity = Gravity.CENTER
            radioButton.layoutParams = layoutParams

            // Resize check and uncheck-drawables to correct size
            val checkedDrawable = ResourcesCompat.getDrawable(this.context.resources, R.drawable.baseline_radio_button_checked_24, null)
            val uncheckedDrawable = ResourcesCompat.getDrawable(this.context.resources, R.drawable.baseline_radio_button_unchecked_24, null)

            assert(uncheckedDrawable != null && checkedDrawable != null)

            val checkedBitmap = checkedDrawable!!.toBitmap()
            val uncheckedBitmap = uncheckedDrawable!!.toBitmap()

            val checkedResized = Bitmap.createScaledBitmap(checkedBitmap, width.rawWidth(), height.rawHeight(), true)
            val uncheckedResized = Bitmap.createScaledBitmap(uncheckedBitmap, width.rawWidth(), height.rawHeight(), true)

            val checkedResizedDrawable = BitmapDrawable(this.context.resources, checkedResized)
            val uncheckedResizedDrawable = BitmapDrawable(this.context.resources, uncheckedResized)

            radioButton.buttonDrawable = uncheckedResizedDrawable
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.buttonDrawable = if(isChecked) {
                    checkedResizedDrawable
                } else {
                    uncheckedResizedDrawable
                }
            }

            radioButton.setOnClickListener(object : View.OnClickListener {
                var m_previousStatus: Boolean = radioButton.isChecked

                override fun onClick(p0: View?) {
                    val button = p0 as RadioButton
                    if(m_previousStatus == button.isChecked) {
                        m_previousStatus = !button.isChecked
                    } else {
                        m_previousStatus = button.isChecked
                    }
                    button.isChecked = m_previousStatus
                    when(type) {
                        TypesForEditTexts.Skills -> characterViewModel.skillsProficiencyBonuses[i] = button.isChecked
                        TypesForEditTexts.SavingThrows -> characterViewModel.savingThrowProficiencyBonuses[i] = button.isChecked
                        else -> {}
                    }
                }
            })

            when(type) {
                TypesForEditTexts.Skills -> radioButton.isChecked = characterViewModel.skillsProficiencyBonuses[i]
                TypesForEditTexts.SavingThrows -> radioButton.isChecked = characterViewModel.savingThrowProficiencyBonuses[i]
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
                position.first.rawWidth())

            // Constraint in vertical
            constraintSet.connect(
                view.id, ConstraintSet.TOP,
                drawableLayout.id, ConstraintSet.TOP,
                position.second.rawHeight())

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

        private fun stringToInt(text: String): Int {
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
    }
}