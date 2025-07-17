package com.example.dnd_sheet.ui.personal_traits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character
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

    private fun drawLayout() {
        val ctx = Tools.checkContext(context)

        Tools.setLayout(layout)
        Tools.drawableToLayout(R.drawable.personal_traits, ctx)
    }
}