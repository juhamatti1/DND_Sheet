package com.example.dnd_sheet.ui.basic_name

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentBasicNameBinding
import com.example.dnd_sheet.ui.Tools

class BasicNameFragment : Fragment() {
    // ? makes possible that variable can be declared as null
    private var _binding: FragmentBasicNameBinding? = null
    lateinit var layout: ConstraintLayout

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Making orientation to landscape because view is really wide and will look tiny if showing
        // it in portrait
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        _binding = FragmentBasicNameBinding.inflate(inflater, container, false)

        layout = binding.root.findViewById(R.id.basic_name_layout)

        // Check if there is already local character file. Load it if yes
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

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun drawLayout() {
        val ctx = Tools.checkContext(context)

        Tools.setLayout(layout)
        Tools.drawableToLayout(R.drawable.basic_name, ctx)
    }
}