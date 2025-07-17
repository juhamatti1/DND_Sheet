package com.example.dnd_sheet.ui.equipment

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.EditTextsId
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentEquipmentBinding
import com.example.dnd_sheet.ui.Tools

class EquipmentFragment : Fragment() {

    private var _binding: FragmentEquipmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var layout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentBinding.inflate(inflater, container, false)

        layout = binding.root.findViewById(R.id.equipment_layout)

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
        Tools.drawableToLayout(R.drawable.equipment, ctx)

        val armorView =
            Tools.createEditText(
                0.2,
                0.05,
                EditTextsId.ARMOR_CLASS,
                textSize = 28f,
                context = ctx
            )
        Tools.setViewToLayout(armorView, 0.1 to 0.026)
        armorView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().armorClass =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val initiativeView =
            Tools.createEditText(
                0.25,
                0.06,
                EditTextsId.INITIATIVE,
                textSize = 28f,
                context = ctx
            )
        Tools.setViewToLayout(initiativeView, 0.372 to 0.02)
        initiativeView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().initiative =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val speedView =
            Tools.createEditText(
                0.25,
                0.06,
                EditTextsId.SPEED,
                textSize = 28f,
                context = ctx
            )
        Tools.setViewToLayout(speedView, 0.685 to 0.02)
        speedView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().speed =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val hitPointsMaxView =
            Tools.createEditText(
                0.47,
                0.02,
                EditTextsId.HIT_POINT_MAXIMUM,
                gravity = Gravity.START,
                context = ctx
            )
        Tools.setViewToLayout(hitPointsMaxView, 0.44 to 0.116)
        hitPointsMaxView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().hitpointMaximum =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val currentHitPointsView = Tools.createEditText(
            0.4,
            0.05,
            EditTextsId.CURRENT_HIT_POINTS,
            textSize = 28f,
            context = ctx
        )
        Tools.setViewToLayout(currentHitPointsView, 0.32 to 0.14)
        currentHitPointsView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().currentHitpoint =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val tempHitPointsView = Tools.createEditText(
            0.4,
            0.05,
            EditTextsId.TEMPORARY_HIT_POINTS,
            textSize = 28f, context = ctx
        )
        Tools.setViewToLayout(tempHitPointsView, 0.32 to 0.22)
        tempHitPointsView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().temporaryHitpoint =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val totalHitDiceView =
            Tools.createEditText(
                0.25,
                0.02,
                EditTextsId.HIT_DICE_TOTAL,
                gravity = Gravity.START,
                context = ctx
            )
        Tools.setViewToLayout(totalHitDiceView, 0.2 to 0.303)
        totalHitDiceView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().hitDiceTotal =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }

        val hitDiceView = Tools.createEditText(
            0.26,
            0.04,
            EditTextsId.HIT_DICE,
            textSize = 28f,
            inputType = InputType.TYPE_CLASS_TEXT, context = ctx
        )
        Tools.setViewToLayout(hitDiceView, 0.15 to 0.317)
        hitDiceView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().hitDice = (view as EditText).text.toString()
            }
        }

        Character.getInstance().successes.forEachIndexed { index, _ ->
            run {
                val successesRadioButton =
                    Tools.createRadioButton(
                        index,
                        EditTextsId.SUCCESSES,
                        0.06,
                        0.015,
                        context = ctx
                    )
                Tools.setViewToLayout(successesRadioButton, 0.72 + index * 0.069 to 0.304)
            }
        }

        Character.getInstance().failures.forEachIndexed { index, _ ->
            run {
                val failuresRadioButton =
                    Tools.createRadioButton(
                        index,
                        EditTextsId.FAILURES,
                        0.06,
                        0.015,
                        context = ctx
                    )
                Tools.setViewToLayout(failuresRadioButton, 0.72 + index * 0.069 to 0.327)
            }
        }

        for (row in 0..2) {
            val name = Character.Attacks_spellcasting.NAME.ordinal
            val atk_bonus = Character.Attacks_spellcasting.ATK_BONUS.ordinal
            val damage_type = Character.Attacks_spellcasting.DAMAGE_TYPE.ordinal

            val nameView = Tools.createEditText(
                0.29,
                0.025,
                EditTextsId.ATTACKS_SPELLCASTING,
                Character.Attacks_spellcasting.NAME.ordinal,
                18f,
                Gravity.START,
                InputType.TYPE_CLASS_TEXT,
                row, context = ctx
            )
            Tools.setViewToLayout(nameView, 0.075 to 0.417 + row * 0.032)
            nameView.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    Character.getInstance().attacksSpellcasting[row][name] =
                        (view as EditText).text.toString()
                }
            }

            val atkBonusView = Tools.createEditText(
                0.15,
                0.025,
                EditTextsId.ATTACKS_SPELLCASTING,
                Character.Attacks_spellcasting.ATK_BONUS.ordinal,
                18f,
                Gravity.START,
                InputType.TYPE_CLASS_TEXT,
                row, context = ctx
            )
            Tools.setViewToLayout(atkBonusView, 0.44 to 0.417 + row * 0.032)
            atkBonusView.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    Character.getInstance().attacksSpellcasting[row][atk_bonus] =
                        (view as EditText).text.toString()
                }
            }

            val damageTypeView = Tools.createEditText(
                0.33,
                0.025,
                EditTextsId.ATTACKS_SPELLCASTING,
                Character.Attacks_spellcasting.DAMAGE_TYPE.ordinal,
                18f,
                Gravity.START,
                InputType.TYPE_CLASS_TEXT,
                row, context = ctx
            )
            Tools.setViewToLayout(damageTypeView, 0.63 to 0.417 + row * 0.032)
            damageTypeView.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    Character.getInstance().attacksSpellcasting[row][damage_type] =
                        (view as EditText).text.toString()
                }
            }
        }

        val attacksSpellcastingText = Tools.createEditText(
            0.0,
            0.0,
            EditTextsId.ATTACKS_SPELLCASTING_TEXT,
            gravity = Gravity.START or Gravity.TOP,
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            context = ctx
        )
        attacksSpellcastingText.layoutParams = ViewGroup.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT
        )
        attacksSpellcastingText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().attacksSpellcastingText =
                    (view as EditText).text.toString()
                // Request to calculate edit text size so scrollableView can determine if edit text
                // fits in it or needs to enable scrolling
                attacksSpellcastingText.requestLayout()
            }
        }
        var scrollableView =
            Tools.createScrollableView(ctx, attacksSpellcastingText, 0.9, 0.17)
        Tools.setViewToLayout(scrollableView, 0.06 to 0.52)

        val cp = Tools.createEditText(
            0.2,
            0.03,
            EditTextsId.CP,
            textSize = 18f,
            context = ctx
        )
        cp.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().cp =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }
        Tools.setViewToLayout(cp, 0.077 to 0.734)

        val sp = Tools.createEditText(
            0.2,
            0.03,
            EditTextsId.SP,
            textSize = 18f,
            context = ctx
        )
        sp.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().sp =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }
        Tools.setViewToLayout(sp, 0.077 to 0.775)

        val ep = Tools.createEditText(
            0.2,
            0.03,
            EditTextsId.EP,
            textSize = 18f,
            context = ctx
        )
        ep.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().ep =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }
        Tools.setViewToLayout(ep, 0.077 to 0.8145)

        val gp = Tools.createEditText(
            0.2,
            0.03,
            EditTextsId.GP,
            textSize = 18f,
            context = ctx
        )
        gp.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().gp =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }
        Tools.setViewToLayout(gp, 0.077 to 0.8545)

        val pp = Tools.createEditText(
            0.2,
            0.03,
            EditTextsId.PP,
            textSize = 18f,
            context = ctx
        )
        pp.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().pp =
                    Tools.stringToInt((view as EditText).text.toString())
            }
        }
        Tools.setViewToLayout(pp, 0.077 to 0.895)

        val equipmentText = Tools.createEditText(
            0.0,
            0.0,
            EditTextsId.EQUPIMENT_TEXT,
            gravity = Gravity.START or Gravity.TOP,
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            context = ctx
        )
        equipmentText.layoutParams = ViewGroup.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT
        )
        equipmentText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                Character.getInstance().equipmentText =
                    (view as EditText).text.toString()
                // Request to calculate edit text size so scrollableView can determine if edit text
                // fits in it or needs to enable scrolling
                equipmentText.requestLayout()
            }
        }
        scrollableView =
            Tools.createScrollableView(ctx, equipmentText, 0.6, 0.25)
        Tools.setViewToLayout(scrollableView, 0.35 to 0.73)
    }
}