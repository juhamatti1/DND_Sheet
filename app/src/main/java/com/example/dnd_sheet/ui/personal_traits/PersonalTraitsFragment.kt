package com.example.dnd_sheet.ui.personal_traits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character.EditTextsId
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentPersonalTraitsBinding
import com.example.dnd_sheet.ui.Tools

class PersonalTraitsFragment : Fragment() {

    lateinit var layout: ConstraintLayout
    private var _binding: FragmentPersonalTraitsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalTraitsBinding.inflate(inflater, container, false)

        layout = binding.root.findViewById(R.id.personal_traits_layout)

        // Check if there is already local character file. Load it if there is
        Tools.loadCharacterFromFile(Tools.checkContext(context))

        if (layout.width > 0) {
            // Layout already have dimensions so OnGlobalLayoutListener won't be called
            drawLayout()
        } else {
            layout.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    //Remove the listener before proceeding. This callback is for the initial only
                    layout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    drawLayout()
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        Tools.saveCharacterToFile(Tools.checkContext(context))
    }

    private fun drawLayout() {
        val ctx = Tools.checkContext(context)

        Tools.setLayout(layout)
        Tools.drawableToLayout(R.drawable.personal_traits, ctx)

        var scroll = Tools.createScrollableEditText(
            ctx,
            0.83,
            0.08,
            EditTextsId.PERSONAL_TRAITS
        )
        Tools.setViewToLayout(scroll, 0.09 to 0.02)

        scroll = Tools.createScrollableEditText(
            ctx,
            0.83,
            0.055,
            EditTextsId.IDEALS
        )
        Tools.setViewToLayout(scroll, 0.09 to 0.13)

        scroll = Tools.createScrollableEditText(
            ctx,
            0.83,
            0.055,
            EditTextsId.BONDS
        )
        Tools.setViewToLayout(scroll, 0.09 to 0.215)

        scroll = Tools.createScrollableEditText(
            ctx,
            0.83,
            0.055,
            EditTextsId.FLAWS
        )
        Tools.setViewToLayout(scroll, 0.09 to 0.303)

        scroll = Tools.createScrollableEditText(
            ctx,
            0.9,
            0.57,
            EditTextsId.FEATURES_AND_TRAITS
        )
        Tools.setViewToLayout(scroll, 0.05 to 0.41)
    }
}