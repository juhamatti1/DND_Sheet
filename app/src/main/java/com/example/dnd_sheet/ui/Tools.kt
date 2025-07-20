package com.example.dnd_sheet.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import androidx.core.widget.NestedScrollView
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.EditTextsId
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

        fun saveCharacterToFile(context: Context): File {
            val jsonString = Json.encodeToString<Character>(Character.getInstance())
            val filesDir = File(context.filesDir, "")

            val file = File(filesDir, fileName)
            file.writeText(jsonString)
            return file
        }

        /**
         * Loads character from JSON file
         * /param Character.getInstance() - view model of character where json will be loaded
         */
        fun loadCharacterFromFile(context: Context): Character? {
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

        fun setDrawableToLayout(drawable: Int, context: Context) {
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
            type: EditTextsId,
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
            editTextView.setPadding(0, 0, 0, 0)

            when (type) {
                EditTextsId.SKILLS -> enumOrdinal?.let { editTextView.setText(Character.getInstance().skills[enumOrdinal].toString()) }
                EditTextsId.MAINSTATS -> enumOrdinal?.let { editTextView.setText(Character.getInstance().mainStats[enumOrdinal].toString()) }
                EditTextsId.SAVING_THROWS -> enumOrdinal?.let { editTextView.setText(Character.getInstance().savingThrows[enumOrdinal].toString()) }
                EditTextsId.ARMOR_CLASS -> editTextView.setText(Character.getInstance().armorClass.toString())
                EditTextsId.INITIATIVE -> editTextView.setText(Character.getInstance().initiative.toString())
                EditTextsId.SPEED -> editTextView.setText(Character.getInstance().speed.toString())
                EditTextsId.PROFICIENCIES_AND_LANGUAGES -> editTextView.setText(Character.getInstance().proficienciesAndLanguages)
                EditTextsId.HIT_POINT_MAXIMUM -> editTextView.setText(Character.getInstance().hitpointMaximum.toString())
                EditTextsId.CURRENT_HIT_POINTS -> editTextView.setText(Character.getInstance().currentHitpoint.toString())
                EditTextsId.TEMPORARY_HIT_POINTS -> editTextView.setText(Character.getInstance().temporaryHitpoint.toString())
                EditTextsId.HIT_DICE -> editTextView.setText(Character.getInstance().hitDice)
                EditTextsId.HIT_DICE_TOTAL -> editTextView.setText(Character.getInstance().hitDiceTotal.toString())
                EditTextsId.SUCCESSES -> editTextView.setText(Character.getInstance().successes.toString())
                EditTextsId.FAILURES -> editTextView.setText(Character.getInstance().failures.toString())
                EditTextsId.ATTACKS_SPELLCASTING -> {
                    if (index != null) {
                        editTextView.setText(
                            Character.getInstance().attacksSpellcasting[index][enumOrdinal] ?: ""
                        )
                    }
                }

                EditTextsId.ATTACKS_SPELLCASTING_TEXT -> editTextView.setText(Character.getInstance().attacksSpellcastingText)
                EditTextsId.CP -> editTextView.setText(Character.getInstance().cp.toString())
                EditTextsId.SP -> editTextView.setText(Character.getInstance().sp.toString())
                EditTextsId.EP -> editTextView.setText(Character.getInstance().ep.toString())
                EditTextsId.GP -> editTextView.setText(Character.getInstance().gp.toString())
                EditTextsId.PP -> editTextView.setText(Character.getInstance().pp.toString())
                EditTextsId.EQUIPMENT_TEXT -> editTextView.setText(Character.getInstance().equipmentText)
                EditTextsId.PERSONAL_TRAITS -> editTextView.setText(Character.getInstance().personalTraitText)
                EditTextsId.IDEALS -> editTextView.setText(Character.getInstance().idealsText)
                EditTextsId.BONDS -> editTextView.setText(Character.getInstance().bondsText)
                EditTextsId.FLAWS -> editTextView.setText(Character.getInstance().flawsText)
                EditTextsId.FEATURES_AND_TRAITS -> editTextView.setText(Character.getInstance().featuresAndTraitsText)
            }
            return editTextView
        }

        fun createScrollableEditText(
            context: Context,
            width: Double,
            height: Double,
            id: EditTextsId
        ): NestedScrollView {
            val editText = createEditText(
                0.0,
                0.0,
                id,
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                gravity = Gravity.START or Gravity.TOP,
                context = context
            )

            editText.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    when (id) {
                        EditTextsId.PERSONAL_TRAITS -> Character.getInstance().personalTraitText =
                            (view as EditText).text.toString()

                        EditTextsId.PROFICIENCIES_AND_LANGUAGES -> Character.getInstance().proficienciesAndLanguages =
                            (view as EditText).text.toString()

                        EditTextsId.ATTACKS_SPELLCASTING_TEXT -> Character.getInstance().attacksSpellcastingText =
                            (view as EditText).text.toString()

                        EditTextsId.EQUIPMENT_TEXT -> Character.getInstance().equipmentText =
                            (view as EditText).text.toString()

                        EditTextsId.IDEALS -> Character.getInstance().idealsText =
                            (view as EditText).text.toString()

                        EditTextsId.BONDS -> Character.getInstance().bondsText =
                            (view as EditText).text.toString()

                        EditTextsId.FLAWS -> Character.getInstance().flawsText =
                            (view as EditText).text.toString()

                        EditTextsId.FEATURES_AND_TRAITS -> Character.getInstance().featuresAndTraitsText =
                            (view as EditText).text.toString()

                        else -> throw Exception("Invalid edit text id in createScrollableEditText: $id")
                    }
                }
            }
            editText.addTextChangedListener(
                object : TextWatcher {
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        // Request to calculate edit text size so scrollableView can determine if edit text
                        // fits in it or needs to enable scrolling
                        editText.requestLayout()
                    }

                    override fun afterTextChanged(p0: Editable?) {}
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                }
            )

            val nestedScrollView = NestedScrollView(context)
            nestedScrollView.id = View.generateViewId()
            nestedScrollView.isFillViewport = true
            nestedScrollView.layoutParams =
                ConstraintLayout.LayoutParams(width.rawWidth(), height.rawHeight())
            nestedScrollView.smoothScrollBy(0, 0)

            editText.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            nestedScrollView.addView(editText)

            return nestedScrollView
        }

        // ToDo: RadioButton icon is not same size for different display resolutions and dpi. Make something to match those
        fun createRadioButton(
            i: Int,
            type: EditTextsId,
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
                checkedBitmap.scale(width.rawWidth(), height.rawHeight())
            val uncheckedResized =
                uncheckedBitmap.scale(width.rawWidth(), height.rawHeight())

            val checkedResizedDrawable = checkedResized.toDrawable(context.resources)
            val uncheckedResizedDrawable = uncheckedResized.toDrawable(context.resources)

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
                        EditTextsId.SKILLS -> Character.getInstance().skillsProficiencyBonuses[i] =
                            button.isChecked

                        EditTextsId.SAVING_THROWS -> Character.getInstance().savingThrowProficiencyBonuses[i] =
                            button.isChecked

                        EditTextsId.SUCCESSES -> Character.getInstance().successes[i] =
                            button.isChecked

                        EditTextsId.FAILURES -> Character.getInstance().failures[i] =
                            button.isChecked

                        else -> {}
                    }
                }
            })

            when (type) {
                EditTextsId.SKILLS -> radioButton.isChecked =
                    Character.getInstance().skillsProficiencyBonuses[i]

                EditTextsId.SAVING_THROWS -> radioButton.isChecked =
                    Character.getInstance().savingThrowProficiencyBonuses[i]

                EditTextsId.SUCCESSES -> radioButton.isChecked =
                    Character.getInstance().successes[i]

                EditTextsId.FAILURES -> radioButton.isChecked =
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


        fun removeZerosFromBegin(editText: EditText) {
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

        fun Double.rawWidth(): Int {
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