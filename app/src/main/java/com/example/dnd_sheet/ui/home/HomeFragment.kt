package com.example.dnd_sheet.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentHomeBinding? = null
    private val TAG: String = "HomeFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    // get() is property method which is called when variable is read
    // So when binding variable is used get() method is called and get()
    // returns _binding. "!!" marks is "non-null assertion operator" which means that we are
    // telling to Kotlin's type system _binding is not null.
    // This is pretty hacky to me.
    private val binding get() = _binding!!

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

                // Draw helper lines for inputting edittexts
//                val linePaint = Paint()
//                linePaint.color = Color.RED
//                linePaint.strokeWidth = 10f
//                val canvas = Canvas(resizedBitmap)
//                val lineX = resizedBitmap.width / 2f
//                val lineY1 = 0f
//                val lineY2 = resizedBitmap.height.toFloat()
//                canvas.drawLine(lineX, lineY1, lineX, lineY2, linePaint)

                val context = requireContext()

                // Creting edit texts for main stats
                val editText = EditText(context)
                editText.id = View.generateViewId()
                editText.width = dpToPx(75)
                editText.height = dpToPx(45)
                editText.setBackgroundColor(Color.RED)
                editText.background.alpha = 50
                editText.textSize = 20f
                editText.gravity = Gravity.CENTER
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.setText("0")

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

                // Recycle old bitmap to avoid memory leak
                statsBitmap.recycle()

                val statsImageView = ImageView(context)
                statsImageView.id = View.generateViewId()
                statsImageView.setImageBitmap(resizedBitmap)

                // Need to add views in order so views in front are added last
                statsLayout.addView(statsImageView)
                statsLayout.addView(editText)
                statsLayout.addView(bonusView)

                // Set constraints to views after views are added
                setStartTopConstraints(statsLayout, editText, statsLayout, 45, 60)
                setStartTopConstraints(statsLayout, bonusView, editText, 16, 55)
//                val constraintSet = ConstraintSet()
//                constraintSet.clone(statsLayout)
//                // Constraint in horizontal
//                constraintSet.connect(
//                    editText.id, ConstraintSet.START,
//                    statsLayout.id, ConstraintSet.START,
//                    dpToPx(45))
//
//                // Constraint in vertical
//                constraintSet.connect(
//                    editText.id, ConstraintSet.TOP,
//                    statsLayout.id, ConstraintSet.TOP,
//                    dpToPx(60))
//                constraintSet.applyTo(statsLayout)
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