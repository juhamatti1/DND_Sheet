package com.example.dnd_sheet.ui.home

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dnd_sheet.Character
import com.example.dnd_sheet.Character.Stats
import com.example.dnd_sheet.R
import com.example.dnd_sheet.databinding.FragmentHomeBinding

open class Stat(stat: Stats, stat_view: View, statUpdated: ((stat: Stat) -> Unit)) {
    private val TAG: String = "STAT"
    private var main_value: EditText
    private var sub_value: TextView
    val type: Stats

    init {
        main_value = stat_view.findViewById(R.id.main_value)
        sub_value = stat_view.findViewById(R.id.sub_value)
        type = stat
        stat_view.findViewById<TextView>(R.id.title).text = type.toString()

        main_value.addTextChangedListener(afterTextChanged = listener@{ text: Editable? ->
            statUpdated.invoke(this)
            // Logic for increasing/decreasing stat bonus based on typed main value
            if (text.isNullOrEmpty()) {
                Log.d(TAG, "Failed to add text changed listener")
                return@listener
            }
            val mainValue: Int = try {
                text.toString().toInt()
            } catch (e: NumberFormatException) {
                Log.d(TAG, "Failed to add text changed listener")
                return@listener
            }

            var subValue: Int = (mainValue -10) / 2
            val fragmentValue = (mainValue-10)%2
            if(fragmentValue > 0) {
                subValue += 1
            } else if(fragmentValue < 0) {
                subValue -= 1
            }
            sub_value.text = subValue.toString()

            val textString = text.toString()
            if (textString[0] == '0' && textString.length > 1) {
                main_value.setText(textString.removeRange(0, 1))
            }
        })
    }

    fun getValue(): Int {
        return sub_value.text.toString().toInt()
    }
}

class HomeFragment : Fragment() {

    // ? makes possible that variable can be declared as null
    private var _binding: FragmentHomeBinding? = null
    private val TAG: String = "HomeFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    // get() is property method which is called when variable is read
    // So when binding variable is used get() method is called and get()
    // returns _binding. "!!" marks is "non-null assertion operator" which means that we are
    // telling to Kotlin's type system _binding is not null.
    // This is pretty hacky to me.
    private val binding get() = _binding!!
    private lateinit var character: Character

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        character = ViewModelProvider(this)[Character::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val stat_list: MutableList<Stat> = ArrayList<Stat>().toMutableList()

        Stats.values().forEach { stat ->
            val stat_view = inflater.inflate(R.layout.stat_view, container, false)
            stat_list.add(Stat(stat, stat_view)  { updateStat ->
                // update character stats when user changes value of stat
                when (updateStat.type) {
                    Stats.STRENGTH -> character.strength = updateStat.getValue()
                    Stats.CHARISMA -> character.charisma = updateStat.getValue()
                    Stats.DEXTERITY -> character.dexterity = updateStat.getValue()
                    Stats.CONSTITUTION -> character.constitution = updateStat.getValue()
                    Stats.INTELLIGENCE -> character.intelligence = updateStat.getValue()
                    Stats.WISDOM -> character.wisdom = updateStat.getValue()
                    Stats.INSPIRATION -> character.inspiration = updateStat.getValue()
                }
            })
            binding.statsLayout.addView(stat_view)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}