package com.example.dnd_sheet.ui.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.EquipmentStats
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val TAG: String = "DashboardFragment"
    private lateinit var characterViewModel: Character

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var  equipmentLayout : ConstraintLayout
    private lateinit var equipmentSize : Pair<Int, Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        characterViewModel = ViewModelProvider(this)[Character::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)

        equipmentLayout = view.findViewById(R.id.equipment_layout)

        equipmentLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                equipmentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Change layout width x height ratio to match background image
                val equipmentBitmap = BitmapFactory.decodeResource(resources, R.drawable.equipment)

                val layoutWidth = equipmentLayout.measuredWidth

                // Using matrix to scale imageview to fit nicely to layout
                val matrix = Matrix()
                val scale = layoutWidth / equipmentBitmap.width.toFloat()
                matrix.postScale(scale, scale)

                val resizedBitmap = Bitmap.createBitmap(equipmentBitmap, 0, 0, equipmentBitmap.width,
                    equipmentBitmap.height, matrix, false)

                // Recycle old bitmap to avoid memory leak
                equipmentBitmap.recycle()

                val context = requireContext()

                val equipmentImageView = ImageView(context)
                equipmentImageView.id = View.generateViewId()
                equipmentImageView.setImageBitmap(resizedBitmap)
                equipmentLayout.addView(equipmentImageView)

                equipmentSize = resizedBitmap.width to resizedBitmap.height
                equipmentSize.first.toDouble() / equipmentSize.second.toDouble()

                createEditText(context, 0.14,0.14, EquipmentStats.ARMOR_CLASS, )
            }

            private fun createEditText(context: Context, width: Double, height: Double, type: EquipmentStats, textSize: Float = 14f): EditText {
                val editTextView = EditText(context)
                editTextView.id = View.generateViewId()
                editTextView.layoutParams = ViewGroup.LayoutParams(width.rawWidth(),
                    height.rawHeight())
                editTextView.textSize = textSize
                editTextView.gravity = Gravity.CENTER
                editTextView.inputType = InputType.TYPE_CLASS_NUMBER
                editTextView.setTextColor(Color.BLACK)

                when(type) {
                    EquipmentStats.ARMOR_CLASS -> editTextView.setText(characterViewModel.armorClass.toString())
                    EquipmentStats.INITIATIVE -> editTextView.setText(characterViewModel.initiative.toString())
                    EquipmentStats.SPEED -> editTextView.setText(characterViewModel.speed.toString())
                    EquipmentStats.HIT_POINT_MAXIMUM -> editTextView.setText(characterViewModel.hitpointMaximum.toString())
                    EquipmentStats.CURRENT_HIT_POINTS -> editTextView.setText(characterViewModel.currentHitpoint.toString())
                    EquipmentStats.TEMPORARY_HIT_POINTS -> editTextView.setText(characterViewModel.temporaryHitpoint.toString())
                    EquipmentStats.HIT_DICE -> editTextView.setText(characterViewModel.hitDice.toString())
                    EquipmentStats.HIT_DICE_TOTAL -> editTextView.setText(characterViewModel.hitDiceTotal.toString())
                    EquipmentStats.SUCCESSES -> editTextView.setText(characterViewModel.successes.toString())
                    EquipmentStats.FAILURES -> editTextView.setText(characterViewModel.failures.toString())
                }
                return editTextView
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Double.rawWidth(): Int {
        return (this * equipmentSize.first).toInt()
    }

    private fun Double.rawHeight(): Int {
        return (this * equipmentSize.second).toInt()
    }
}