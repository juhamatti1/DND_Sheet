package com.example.dnd_sheet

import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StatListener<T>(initialValue: T, val stat: Character.Stats, val parent: Character) : ReadWriteProperty<Any?, T> {
    private var oldValue = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return oldValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (oldValue != value) {
            println("${stat.name} changed from $oldValue to $value")
            oldValue = value
        }
        val outputStream = ByteArrayOutputStream()
    }
}

@Serializable
class Character : ViewModel() {
    val TAG = "CHARACTER"
    var name: String = ""
    lateinit var primary_class: DND_classes
    lateinit var primary_race: Races
    var experience: Float = 0f

    var strength: Int by StatListener(0, Stats.STRENGTH, this)
    var dexterity: Int by StatListener(0, Stats.DEXTERITY, this)
    var constitution: Int = 0
    var intelligence: Int = 0
    var wisdom: Int = 0
    var charisma: Int = 0
    var inspiration: Int = 0
    var profience_bonus: Int = 0
    var passive_wisdom: Int = 0

    lateinit var saving_throws: HashMap<Saving_throws, Int>
    lateinit var skills: HashMap<Skills, Boolean>

    enum class Stats {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
    }

    enum class DND_classes {
        BARBARIAN, BARD, CLERIC, DRUID, FIGHTER, MONK, PALADIN, RANGER, ROGUE, SORCERER, WARLOCK,
        WIZARD
    }

    enum class Races {
        DRAGONBORN, DWARF, ELF, GNOME, HALF_ELF, HALFLING, HALF_ORC, HUMAN, TIEFLING
    }

    enum class Saving_throws {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
    }
    enum class Skills {
        ACROBATICS, ANIMAL_HANDLING, ARCANA, ATHLETICS, DECEPTION, HISTORY, INSIGHT, INTIMIDATION,
        INVESTIGATION, MEDICINE, NATURE, PERCEPTION, PERFORMANCE, PERSUASION, RELIGION,
        SLEIGHT_OF_HAND, STEATLH, SURVIVAL
    }

    fun setStatValue(stat: Int, value: Int): Boolean {
        val statEnum: Stats = Stats.values().getOrNull(stat) ?: return false
        when(statEnum) {
            Stats.STRENGTH -> strength = value
            Stats.DEXTERITY -> dexterity = value
            Stats.CONSTITUTION -> constitution = value
            Stats.INTELLIGENCE -> intelligence = value
            Stats.WISDOM -> wisdom = value
            Stats.CHARISMA -> charisma = value
        }
        return true
    }
}
