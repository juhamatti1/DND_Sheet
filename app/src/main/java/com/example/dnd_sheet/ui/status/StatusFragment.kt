package com.example.dnd_sheet.ui.status

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.EditTextsId
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentStatusBinding
import com.example.dnd_sheet.ui.Tools

class StatusFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentStatusBinding? = null
    private val TAG: String = "HomeFragment"
    lateinit var statsLayout: ConstraintLayout


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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statsLayout = view.findViewById(R.id.stats_layout)

        statsLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                statsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                Tools.setLayout(statsLayout)

                val ctx = Tools.checkContext(context)

                Tools.drawableToLayout(R.drawable.stats, ctx)

                Tools.createMainStatsViews(ctx)

                Tools.createInspirationAndProficiencyBonusViews(ctx)

                Tools.createSavingThrows(ctx)

                Tools.createSkills(ctx)

                Tools.createPassiveWisdom(ctx)

                val proficienciesEditText = Tools.createEditText(
                    0.0,
                    0.0,
                    EditTextsId.PROFIENCIES_AND_LANGUAGES,
                    textSize = 15f,
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                    gravity = Gravity.START or Gravity.TOP,
                    context = ctx
                )
                proficienciesEditText.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        Character.getInstance().proficienciesAndLanguages = (view as EditText).text.toString()
                    }
                }
                val scrollableView = Tools.createScrollableView(
                    ctx,
                    proficienciesEditText,
                    0.85,
                    0.205
                )
                Tools.setViewToLayout(scrollableView, 0.1 to 0.765)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Tools.saveToFile(Tools.checkContext(context))
    }

    override fun onResume() {
        super.onResume()
        // Check if there is already local character file. Load it if yes
        Tools.loadFromLocalJson(Tools.checkContext(context))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}