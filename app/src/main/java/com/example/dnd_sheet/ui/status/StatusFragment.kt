package com.example.dnd_sheet.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character.EditTextsId
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentStatusBinding
import com.example.dnd_sheet.ui.Tools

class StatusFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentStatusBinding? = null
    lateinit var layout: ConstraintLayout


    // This property is only valid between onCreateView and
    // onDestroyView.
    // get() is property method which is called when variable is read
    // So when binding variable is used get() method is called and get()
    // returns _binding. "!!" marks is "non-null assertion operator" which means that we are
    // telling to Kotlin's type system _binding is not null.
    // This is pretty hacky to me.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)

        layout = binding.root.findViewById(R.id.stats_layout)

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

    override fun onStop() {
        super.onStop()
        Tools.saveCharacterToFile(Tools.checkContext(context))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun drawLayout() {
        Tools.setLayout(layout)

        val ctx = Tools.checkContext(context)

        Tools.drawableToLayout(R.drawable.stats, ctx)

        Tools.createMainStatsViews(ctx)

        Tools.createInspirationAndProficiencyBonusViews(ctx)

        Tools.createSavingThrows(ctx)

        Tools.createSkills(ctx)

        Tools.createPassiveWisdom(ctx)

        val scrollableView = Tools.createScrollableEditText(
            ctx,
            0.85,
            0.195,
            EditTextsId.PROFICIENCIES_AND_LANGUAGES
        )
        Tools.setViewToLayout(scrollableView, 0.11 to 0.769)
    }
}