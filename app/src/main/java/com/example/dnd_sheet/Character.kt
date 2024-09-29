package com.example.dnd_sheet

import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable


@Serializable
class Character : ViewModel() {
    val TAG = "CHARACTER"
    var name: String = ""
    // Need to comment these until they are actually used for serialization
//    lateinit var primary_class: DND_classes
//    lateinit var primary_race: Races
//    var experience: Float = 0f

    val stats = IntArray(Stats.entries.size) { 0 }
    val savingThrows = IntArray(SavingThrows.entries.size) { 0 }
    val skills = IntArray(Skills.entries.size) { 0 }
    val savingThrowProficiencyBonuses = BooleanArray(SavingThrows.entries.size) { false }
    val skillsProficiencyBonuses = BooleanArray(Skills.entries.size) { false }
    var proficienciesAndLanguages = ""

//    lateinit var saving_throws: HashMap<Saving_throws, Int>
//    lateinit var skills: HashMap<Skills, Boolean>

    enum class StatType { Stats, SavingThrows, Skills }

    enum class Stats {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA, INSPIRATION, PROFICIENCY_BONUS, PASSIVE_WISDOM
    }

    enum class SavingThrows {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
    }

    enum class Skills {
        ACROBATICS, ANIMAL_HANDLING, ARCANA, ATHLETICS, DECEPTION, HISTORY, INSIGHT, INTIMIDATION,
        INVESTIGATION, MEDICINE, NATURE, PERCEPTION, PERFORMANCE, PERSUASION, RELIGION,
        SLEIGHT_OF_HAND, STEALTH, SURVIVAL
    }

//    enum class DND_classes {
//        BARBARIAN, BARD, CLERIC, DRUID, FIGHTER, MONK, PALADIN, RANGER, ROGUE, SORCERER, WARLOCK,
//        WIZARD
//    }

//    enum class Races {
//        DRAGONBORN, DWARF, ELF, GNOME, HALF_ELF, HALFLING, HALF_ORC, HUMAN, TIEFLING
//    }
}
