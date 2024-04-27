package com.example.dnd_sheet

import androidx.lifecycle.ViewModel

class Character() : ViewModel() {
    var name: String = ""
    lateinit var primary_class: DND_classes
    lateinit var primary_race: Races
    var experience: Float = 0f

    var strength: Int = 0
    var dexterity: Int = 0
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
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA, INSPIRATION
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


}
