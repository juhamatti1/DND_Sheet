package com.example.dnd_sheet.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.TypesForEditTexts
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentDashboardBinding
import com.example.dnd_sheet.ui.Tools

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

                Tools.setLayout(equipmentLayout)
                Tools.drawableToLayout(R.drawable.equipment)

                val armorView = Tools.createEditText(0.2,0.06, TypesForEditTexts.ARMOR_CLASS, null, 28f)
                armorView.setBackgroundColor(Color.RED)
                armorView.background.alpha = 25
                Tools.setViewToLayout(armorView, 0.1 to 0.02)
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