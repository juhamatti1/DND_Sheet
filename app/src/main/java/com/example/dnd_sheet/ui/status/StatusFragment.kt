package com.example.dnd_sheet.ui.status

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.EditTextsId
import com.example.dnd_sheet.Character.MainStats
import com.example.dnd_sheet.Character.SavingThrows
import com.example.dnd_sheet.Character.Skills
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentStatusBinding
import com.example.dnd_sheet.ui.Tools
import com.example.dnd_sheet.ui.Tools.Companion.rawHeight
import com.example.dnd_sheet.ui.Tools.Companion.rawWidth

class StatusFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentStatusBinding? = null
    lateinit var layout: ConstraintLayout
    private val TAG = "StatusFragment"


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

        Tools.setDrawableToLayout(R.drawable.stats, ctx)

        createMainStatsViews(ctx)

        createInspirationAndProficiencyBonusViews(ctx)

        createSavingThrows(ctx)

        createSkills(ctx)

        createPassiveWisdom(ctx)

        val scrollableView = Tools.createScrollableEditText(
            ctx,
            0.85,
            0.195,
            EditTextsId.PROFICIENCIES_AND_LANGUAGES
        )
        Tools.setViewToLayout(scrollableView, 0.11 to 0.769)
    }


    private fun createMainStatsViews(context: Context) {
        for (i in 0..5) {
            // Creating edit texts for main stats
            val mainStatEditText =
                Tools.createEditText(0.14, 0.03, EditTextsId.MAINSTATS, i, context = context)

            // Creating text views for bonus stats
            val bonusView = TextView(context)
            bonusView.id = View.generateViewId()
            bonusView.layoutParams = ViewGroup.LayoutParams(0.14.rawWidth(), 0.03.rawHeight())
            bonusView.textSize = 20f
            bonusView.gravity = Gravity.CENTER
            bonusView.text =
                calculateSubValue(Tools.stringToInt(mainStatEditText.text.toString())).toString()
            bonusView.setTextColor(Color.BLACK)

            // Listener to update bonus stats when main stat is edited
            mainStatEditText.addTextChangedListener(afterTextChanged = listener@{ editedText: Editable? ->
                // Logic for increasing/decreasing stat bonus based on typed main value
                val mainValue: Int = Tools.stringToInt(editedText.toString())
                if (mainValue == Int.MIN_VALUE) {
                    return@listener
                }

                val subValue: Int = calculateSubValue(mainValue)
                bonusView.text = subValue.toString()

                Tools.removeZerosFromBegin(mainStatEditText)

                val stat: MainStats = MainStats.entries[i]
                if (!stat.equals(null)) {
                    Character.getInstance().mainStats[stat.ordinal] = mainValue
                }
            })

            Tools.setViewToLayout(mainStatEditText, 0.155 to (0.06 + i.toDouble() * 0.109))
            Tools.setViewToLayout(bonusView, 0.155 to (0.095 + i.toDouble() * 0.109))
        }
    }

    private fun createInspirationAndProficiencyBonusViews(context: Context) {

        class CharacterStatUpdater(val statText: EditText, val i: Int) : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(newText: Editable?) {
                val value: Int = try {
                    newText.toString().toInt()
                } catch (e: NumberFormatException) {
                    Log.e(TAG, e.message ?: "Invalid number")
                    return
                }
                Tools.removeZerosFromBegin(statText)
                Character.getInstance().mainStats[i] = value
            }
        }

        // Creating edit text for inspiration
        val inspirationText =
            Tools.createEditText(
                0.14,
                0.034,
                EditTextsId.MAINSTATS,
                MainStats.INSPIRATION.ordinal,
                context = context
            )
        inspirationText.addTextChangedListener(
            CharacterStatUpdater(
                inspirationText,
                MainStats.INSPIRATION.ordinal
            )
        )
        Tools.setViewToLayout(inspirationText, 0.41 to 0.01)

        // Creating edit text for proficiency bonus
        val proficiencyText = Tools.createEditText(
            0.145,
            0.034,
            EditTextsId.MAINSTATS,
            MainStats.PROFICIENCY_BONUS.ordinal,
            context = context
        )
        proficiencyText.addTextChangedListener(
            CharacterStatUpdater(
                proficiencyText,
                MainStats.PROFICIENCY_BONUS.ordinal
            )
        )
        Tools.setViewToLayout(proficiencyText, 0.41 to 0.067)
    }

    private fun createSkills(context: Context) {
        class SkillsUpdater(val statText: EditText, val skills: Skills) : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(newText: Editable?) {
                val value = Tools.stringToInt(newText.toString())
                if (value == Int.MIN_VALUE) {
                    return
                }
                Tools.removeZerosFromBegin(statText)
                Character.getInstance().skills[skills.ordinal] = value
            }
        }

        // Creating edit texts for skills
        for (skills in Skills.entries) {
            val proficiencyButton =
                Tools.createRadioButton(skills.ordinal, EditTextsId.SKILLS, context = context)
            Tools.setViewToLayout(
                proficiencyButton,
                0.445 to 0.304 + skills.ordinal.toDouble() * 0.02065
            )

            val skillsView =
                Tools.createEditText(
                    0.07,
                    0.026,
                    EditTextsId.SKILLS,
                    skills.ordinal,
                    10f,
                    context = context
                )
            skillsView.addTextChangedListener(SkillsUpdater(skillsView, skills))
            Tools.setViewToLayout(skillsView, 0.5 to 0.3 + skills.ordinal.toDouble() * 0.02065)
        }
    }

    private fun createPassiveWisdom(context: Context) {
        class CharacterStatUpdater(val statText: EditText, val i: Int) : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(newText: Editable?) {
                val value: Int = try {
                    newText.toString().toInt()
                } catch (e: NumberFormatException) {
                    return
                }
                Tools.removeZerosFromBegin(statText)
                Character.getInstance().mainStats[i] = value
            }
        }

        // Creating edit text for passive wisdom
        val passiveWisdomText = Tools.createEditText(
            0.145,
            0.04,
            EditTextsId.MAINSTATS,
            MainStats.PASSIVE_WISDOM.ordinal,
            context = context
        )
        passiveWisdomText.addTextChangedListener(
            CharacterStatUpdater(
                passiveWisdomText,
                MainStats.PASSIVE_WISDOM.ordinal
            )
        )
        Tools.setViewToLayout(passiveWisdomText, 0.082 to 0.71)
    }

    private fun createSavingThrows(context: Context) {
        class SavingThrowUpdater(val statText: EditText, val savingThrows: SavingThrows) :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(newText: Editable?) {
                val value = Tools.stringToInt(newText.toString())
                if (value == Int.MIN_VALUE) {
                    return
                }
                Tools.removeZerosFromBegin(statText)
                Character.getInstance().savingThrows[savingThrows.ordinal] = value
            }
        }

        // Creating edit texts for saving throws
        for (savingThrow in SavingThrows.entries) {
            val proficiencyButton =
                Tools.createRadioButton(
                    savingThrow.ordinal,
                    EditTextsId.SAVING_THROWS,
                    context = context
                )

            Tools.setViewToLayout(
                proficiencyButton,
                0.445 to 0.128 + savingThrow.ordinal.toDouble() * 0.02065
            )

            val savingThrowView = Tools.createEditText(
                0.08,
                0.026,
                EditTextsId.SAVING_THROWS,
                savingThrow.ordinal,
                10f,
                context = context
            )
            savingThrowView.addTextChangedListener(
                SavingThrowUpdater(
                    savingThrowView,
                    savingThrow
                )
            )
            Tools.setViewToLayout(
                savingThrowView,
                0.505 to 0.124 + savingThrow.ordinal.toDouble() * 0.0207
            )
        }
    }

    fun calculateSubValue(mainValue: Int): Int {
        var subValue: Int = (mainValue - 10) / 2
        val fragmentValue = (mainValue - 10) % 2
        if (fragmentValue > 0) {
            subValue += 1
        } else if (fragmentValue < 0) {
            subValue -= 1
        }
        return subValue
    }
}