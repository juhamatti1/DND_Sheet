package com.example.dnd_sheet.ui.equipment

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.TypesForEditTexts
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentEquipmentBinding
import com.example.dnd_sheet.ui.Tools

class EquipmentFragment : Fragment() {

    private var _binding: FragmentEquipmentBinding? = null
    private val TAG: String = "EquipmentFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var  equipmentLayout : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)

        equipmentLayout = view.findViewById(R.id.equipment_layout)

        equipmentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener before proceeding
                equipmentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val ctx = Tools.checkContext(context)

                Tools.setLayout(equipmentLayout)
                Tools.drawableToLayout(R.drawable.equipment, ctx)

                val armorView =
                    Tools.createEditText(0.2, 0.05, TypesForEditTexts.ARMOR_CLASS, textSize = 28f, context = ctx)
                Tools.setViewToLayout(armorView, 0.1 to 0.026)
                armorView.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        Character.getInstance().armorClass =
                            Tools.stringToInt((view as EditText).text.toString())
                    }
                }

                val initiativeView =
                    Tools.createEditText(0.25, 0.06, TypesForEditTexts.INITIATIVE, textSize = 28f, context = ctx)
                Tools.setViewToLayout(initiativeView, 0.372 to 0.02)
                initiativeView.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        Character.getInstance().initiative =
                            Tools.stringToInt((view as EditText).text.toString())
                    }
                }

                val speedView =
                    Tools.createEditText(0.25, 0.06, TypesForEditTexts.SPEED, textSize = 28f, context = ctx)
                Tools.setViewToLayout(speedView, 0.685 to 0.02)
                speedView.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        Character.getInstance().speed =
                            Tools.stringToInt((view as EditText).text.toString())
                    }
                }

                val hitPointsMaxView =
                    Tools.createEditText(0.5, 0.03, TypesForEditTexts.HIT_POINT_MAXIMUM, context = ctx)
                hitPointsMaxView.gravity = Gravity.BOTTOM
                Tools.setViewToLayout(hitPointsMaxView, 0.44 to 0.112)
                hitPointsMaxView.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        Character.getInstance().hitpointMaximum =
                            Tools.stringToInt((view as EditText).text.toString())
                    }
                }

                val currentHitPointsView = Tools.createEditText(
                    0.4,
                    0.05,
                    TypesForEditTexts.CURRENT_HIT_POINTS,
                    textSize = 28f, context = ctx
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
                    TypesForEditTexts.TEMPORARY_HIT_POINTS,
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
                    Tools.createEditText(0.26, 0.03, TypesForEditTexts.HIT_DICE_TOTAL, context = ctx)
                totalHitDiceView.gravity = Gravity.BOTTOM
                Tools.setViewToLayout(totalHitDiceView, 0.2 to 0.298)
                totalHitDiceView.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) {
                        Character.getInstance().hitDiceTotal =
                            Tools.stringToInt((view as EditText).text.toString())
                    }
                }

                val hitDiceView = Tools.createEditText(
                    0.26,
                    0.04,
                    TypesForEditTexts.HIT_DICE,
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
                            Tools.createRadioButton(index, TypesForEditTexts.SUCCESSES, 0.06, 0.015, context = ctx)
                        Tools.setViewToLayout(successesRadioButton, 0.72 + index * 0.069 to 0.304)
                    }
                }

                Character.getInstance().failures.forEachIndexed { index, _ ->
                    run {
                        val failuresRadioButton =
                            Tools.createRadioButton(index, TypesForEditTexts.FAILURES, 0.06, 0.015, context = ctx)
                        Tools.setViewToLayout(failuresRadioButton, 0.72 + index * 0.069 to 0.327)
                    }
                }


                for(row in 0..2) {
                    val name = Character.Attacks_spellcasting.NAME.ordinal
                    val atk_bonus = Character.Attacks_spellcasting.ATK_BONUS.ordinal
                    val damage_type = Character.Attacks_spellcasting.DAMAGE_TYPE.ordinal

                    val nameView = Tools.createEditText(
                        0.29,
                        0.025,
                        TypesForEditTexts.ATTACKS_SPELLCASTING,
                        Character.Attacks_spellcasting.NAME.ordinal,
                        18f,
                        Gravity.START,
                        InputType.TYPE_CLASS_TEXT,
                        row, context = ctx
                    )
                    Tools.setViewToLayout(nameView, 0.075 to 0.417 + row * 0.032)
                    nameView.setPadding(0)
                    nameView.setOnFocusChangeListener { view, hasFocus ->
                        if (!hasFocus) {
                            Character.getInstance().attacksSpellcasting[row][name] = (view as EditText).text.toString()
                        }
                    }

                    val atkBonusView = Tools.createEditText(
                        0.15,
                        0.025,
                        TypesForEditTexts.ATTACKS_SPELLCASTING,
                        Character.Attacks_spellcasting.ATK_BONUS.ordinal,
                        18f,
                        Gravity.START,
                        InputType.TYPE_CLASS_TEXT,
                        row, context = ctx
                    )
                    Tools.setViewToLayout(atkBonusView, 0.44 to 0.417 + row * 0.032)
                    atkBonusView.setPadding(0)
                    atkBonusView.setOnFocusChangeListener { view, hasFocus ->
                        if (!hasFocus) {
                            Character.getInstance().attacksSpellcasting[row][atk_bonus] = (view as EditText).text.toString()
                        }
                    }

                    val damageTypeView = Tools.createEditText(
                        0.33,
                        0.025,
                        TypesForEditTexts.ATTACKS_SPELLCASTING,
                        Character.Attacks_spellcasting.DAMAGE_TYPE.ordinal,
                        18f,
                        Gravity.START,
                        InputType.TYPE_CLASS_TEXT,
                        row, context = ctx
                    )
                    Tools.setViewToLayout(damageTypeView, 0.63 to 0.417 + row * 0.032)
                    damageTypeView.setPadding(0)
                    damageTypeView.setOnFocusChangeListener { view, hasFocus ->
                        if (!hasFocus) {
                            Character.getInstance().attacksSpellcasting[row][damage_type] = (view as EditText).text.toString()
                        }
                    }
                }

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        Tools.saveToJson(Tools.checkContext(context))
    }

    override fun onResume() {
        super.onResume()
        // Check if there is already local character file. Load it if yes
        Tools.loadFromLocalJson(Tools.checkContext(context))
    }
}