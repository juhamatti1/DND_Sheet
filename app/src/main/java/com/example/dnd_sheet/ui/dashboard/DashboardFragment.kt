package com.example.dnd_sheet.ui.dashboard

import android.os.Bundle
import android.view.Gravity
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
                Tools.setViewToLayout(armorView, 0.1 to 0.02)

                val initiativeView = Tools.createEditText(0.25,0.06, TypesForEditTexts.INITIATIVE, null, 28f)
                Tools.setViewToLayout(initiativeView, 0.372 to 0.02)

                val speedView = Tools.createEditText(0.25,0.06, TypesForEditTexts.SPEED, null, 28f)
                Tools.setViewToLayout(speedView, 0.685 to 0.02)

                val hitPointsMaxView = Tools.createEditText(0.5,0.03, TypesForEditTexts.HIT_POINT_MAXIMUM, null, 14f)
                hitPointsMaxView.gravity = Gravity.BOTTOM
                Tools.setViewToLayout(hitPointsMaxView, 0.44 to 0.112)

                val currentHitPointsMaxView = Tools.createEditText(0.4,0.05, TypesForEditTexts.HIT_POINT_MAXIMUM, null, 28f)
//                currentHitPointsMaxView.setBackgroundColor(Color.RED)
//                currentHitPointsMaxView.background.alpha = 40
                Tools.setViewToLayout(currentHitPointsMaxView, 0.32 to 0.14)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}