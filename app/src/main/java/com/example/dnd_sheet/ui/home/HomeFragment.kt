package com.example.dnd_sheet.ui.home

import android.content.Context
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
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dnd_sheet.Character
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

                // Creating edit text and text views for main and bonus stats
                for(i in 0..5) {
                    // Creating edit texts for main stats
                    val mainStatEditText = createEditText(context, 75, 45, Stats.entries[i])

                    // Creating text views for bonus stats
                    val bonusView = TextView(context)
                    bonusView.id = View.generateViewId()
                    bonusView.width = dpToPx(40)
                    bonusView.height = dpToPx(30)
                    bonusView.setBackgroundColor(Color.BLUE)
                    bonusView.background.alpha = 50
                    bonusView.textSize = 20f
                    bonusView.gravity = Gravity.CENTER
                    bonusView.text = "0"

                    // Listener to update bonus stats when main stat is edited
                    mainStatEditText.addTextChangedListener(afterTextChanged = listener@{ editedText: Editable? ->
                        // Logic for increasing/decreasing stat bonus based on typed main value
                        val mainValue: Int = try {
                            editedText.toString().toInt()
                        } catch (e: NumberFormatException) {
                            val errorMessage = "Invalid number"
                            Log.e(TAG, errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            return@listener
                        }

                        var subValue: Int = (mainValue - 10) / 2
                        val fragmentValue = (mainValue - 10) % 2
                        if (fragmentValue > 0) {
                            subValue += 1
                        } else if (fragmentValue < 0) {
                            subValue -= 1
                        }
                        bonusView.text = subValue.toString()

                        cleanFrontZeros(mainStatEditText)

                        val stat: Stats = Stats.values()[i]
                        val toastText: String
                        if(stat.equals(null)) {
                            toastText = "Invalid stat id:${i}"
                        } else {
                            characterViewModel.stats[stat.ordinal] = mainValue
                            toastText = "Set ${Stats.values()[i]} to ${characterViewModel.stats[stat.ordinal]}"
                        }
                        Toast.makeText(context,
                            toastText,
                            Toast.LENGTH_SHORT).show()
                    })

                    // Need to add views in order so views in front are added last
                    statsLayout.addView(mainStatEditText)
                    statsLayout.addView(bonusView)

                    // Set constraints to views after views are added
                    setStartTopConstraints(statsLayout, mainStatEditText, statsLayout, 45, 60 + i*130)
                    setStartTopConstraints(statsLayout, bonusView, mainStatEditText, 16, 55)
                }

                class Listener(val statText: EditText, val stat: Stats) : TextWatcher {
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
                        cleanFrontZeros(statText)
                        characterViewModel.stats[stat.ordinal] = value
                        toastText = "Set $stat to ${characterViewModel.stats[stat.ordinal]}"
                        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    }
                }

                // Creating edit text for inspiration
                val inspirationText = createEditText(context, 45, 40, Stats.INSPIRATION)
                inspirationText.addTextChangedListener(Listener(inspirationText, Stats.INSPIRATION))
                statsLayout.addView(inspirationText)
                setStartTopConstraints(statsLayout, inspirationText, statsLayout, 150, 10)

                // Creating edit text for proficiency bonus
                val proficiencyText = createEditText(context, 45, 40, Stats.PROFICIENCY_BONUS)
                proficiencyText.addTextChangedListener(Listener(proficiencyText, Stats.PROFICIENCY_BONUS))
                statsLayout.addView(proficiencyText)
                setStartTopConstraints(statsLayout, proficiencyText, statsLayout, 152, 77)
            }

            // -------------------------------------------------------------------------------------
            // HELPER FUNCTIONS --------------------------------------------------------------------
            // -------------------------------------------------------------------------------------
            private fun cleanFrontZeros(editText: EditText) {
                val textString = editText.text
                if (textString[0] == '0' && textString.length > 1) {
                    editText.setText(textString.removeRange(0, 1))
                    // Set cursor to the end of edit text after removing "0"
                    editText.setSelection(editText.length())
                }
            }

            private fun createEditText(context: Context, width: Int, height: Int, stat: Stats): EditText {
                val mainStatEditText = EditText(context)
                mainStatEditText.id = View.generateViewId()
                mainStatEditText.width = dpToPx(width)
                mainStatEditText.height = dpToPx(height)
                mainStatEditText.setBackgroundColor(Color.RED)
                mainStatEditText.background.alpha = 50
                mainStatEditText.textSize = 20f
                mainStatEditText.gravity = Gravity.CENTER
                mainStatEditText.inputType = InputType.TYPE_CLASS_NUMBER
                mainStatEditText.setTextColor(Color.BLACK)
                mainStatEditText.setText(characterViewModel.stats[stat.ordinal].toString())
                return mainStatEditText
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

    val name = "character"

    override fun onPause() {
        super.onPause()
        val text = Json.encodeToString(characterViewModel)
        println("WRITE:$text")
        requireContext().openFileOutput(name, Context.MODE_PRIVATE).use {
            it.write(text.toByteArray())
        }
    }

    override fun onResume() {
        super.onResume()
        val bytes: ByteArray
        try {
            requireContext().openFileInput(name).use {
                bytes = it.readBytes()
                it.close()
            }
        } catch (e: FileNotFoundException) {
            println("Can't find \"$name\" file")
            return
        }
        println("READ:${bytes.decodeToString()}")
        var character: Character? = null
        val error = kotlin.runCatching {
            character = Json.decodeFromString<Character>(bytes.decodeToString())
        }
        if(character == null) {
            println(error.exceptionOrNull())
            return
        }
        characterViewModel = character as Character
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}