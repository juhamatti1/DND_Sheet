package com.example.dnd_sheet.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentHomeBinding


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

    override fun onStart() {
        super.onStart()

        val statsLayout: ConstraintLayout = view?.findViewById(R.id.stats_layout) ?: return
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
                    val mainStatEditText = createEditText(context, 75, 45)

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

                        val textString = editedText.toString()
                        if (textString[0] == '0' && textString.length > 1) {
                            mainStatEditText.setText(textString.removeRange(0, 1))
                        }
                        val toastText = if(characterViewModel.setStatValue(i, mainValue))
                            "Set ${Character.Stats.values()[i]} to $mainValue"
                        else
                            "Failed to set stat"
                        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    })

                    // Need to add views in order so views in front are added last
                    statsLayout.addView(mainStatEditText)
                    statsLayout.addView(bonusView)

                    Log.i(TAG, "main stat id:${mainStatEditText.id}, i:$i")

                    // Set constraints to views after views are added
                    setStartTopConstraints(statsLayout, mainStatEditText, statsLayout, 45, 60 + i*130)
                    setStartTopConstraints(statsLayout, bonusView, mainStatEditText, 16, 55)
                }

                // Creating edit text for inspiration
                val inspirationText = createEditText(context, 45, 40)
                inspirationText.addTextChangedListener(afterTextChanged = { editedText: Editable? ->
                    val toastText: String
                    val value: Int = try {
                        editedText.toString().toInt()
                    } catch (e: NumberFormatException) {
                        toastText = "Invalid number"
                        Log.e(TAG, toastText)
                        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                        return@addTextChangedListener
                    }
                    characterViewModel.inspiration = value
                    toastText = "Set inspiration to $value"
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    characterViewModel.inspiration = value
                })

                statsLayout.addView(inspirationText)
                setStartTopConstraints(statsLayout, inspirationText, statsLayout, 150, 10)
            }

            // Custom method for creating edit text
            private fun createEditText(context: Context, width: Int, height: Int): EditText {
                val mainStatEditText = EditText(context)
                mainStatEditText.id = View.generateViewId()
                mainStatEditText.width = dpToPx(width)
                mainStatEditText.height = dpToPx(height)
                mainStatEditText.setBackgroundColor(Color.RED)
                mainStatEditText.background.alpha = 50
                mainStatEditText.textSize = 20f
                mainStatEditText.gravity = Gravity.CENTER
                mainStatEditText.inputType = InputType.TYPE_CLASS_NUMBER
                mainStatEditText.setText("0")
                return mainStatEditText
            }
        })
    }

    fun setStartTopConstraints(layout: ConstraintLayout, firstView: View, secondView: View,
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

    fun dpToPx(dp: Number): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        val statsLayout: LinearLayout = view?.findViewById(R.id.stats_layout) ?: return root
        statsLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                statsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)
                val sdw = statsBitmap.width
                val sdh = statsBitmap.height

                val hw = statsLayout.measuredWidth
                val hh = statsLayout.measuredHeight
                Log.i(TAG, "stats w:$sdw h:$sdh, fragment w:$hw h:$hh")

            }
        })

//        val homeFragmentLayout: LinearLayout = layoutInflater.inflate(R.layout.fragment_home, null) as LinearLayout

        // the object keyword is used to create an anonymous object that implements the
        // ViewTreeObserver.OnGlobalLayoutListener interface

//        val l: LinearLayout = view?.findViewById(R.id.stats_layout) ?: return root
//        val hw = homeFragmentLayout.measuredWidth
//        val hh = homeFragmentLayout.measuredHeight
//        Log.i(TAG, "layout w:${l.measuredWidth} h:${l.measuredHeight} fragment w:$hw h:$hh")
//
//        homeFragmentLayout.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                //Remove the listener before proceeding
//                homeFragmentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                // Change layout width x height ratio to match background image
//                val statsBitmap = BitmapFactory.decodeResource(resources, R.drawable.stats)
//                val sdw = statsBitmap.width
//                val sdh = statsBitmap.height
//
//                val hw = homeFragmentLayout.measuredWidth
//                val hh = homeFragmentLayout.measuredHeight
//                Log.i(TAG, "stats w:$sdw h:$sdh, fragment w:$hw h:$hh")
//            }
//        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}