package com.example.dnd_sheet

import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable


@Serializable
class Character : ViewModel() {
    var name: String = ""
    // Need to comment these until they are actually used for serialization
//    lateinit var primary_class: DND_classes
//    lateinit var primary_race: Races
//    var experience: Float = 0f

    var mainStats = IntArray(MainStats.entries.size) { 0 }
    var savingThrows = IntArray(SavingThrows.entries.size) { 0 }
    var skills = IntArray(Skills.entries.size) { 0 }
    var armorClass: Int = 0
    var initiative: Int = 0
    var speed: Int = 0
    var hitpointMaximum: Int = 0
    var currentHitpoint: Int = 0
    var temporaryHitpoint: Int = 0
    var hitDice: Int = 0
    var hitDiceTotal: Int = 0
    var successes: Int = 0
    var failures: Int = 0
    var skillsProficiencyBonuses = BooleanArray(Skills.entries.size) { false }
    var savingThrowProficiencyBonuses = BooleanArray(SavingThrows.entries.size) { false }
    var proficienciesAndLanguages = ""

//    lateinit var saving_throws: HashMap<Saving_throws, Int>
//    lateinit var skills: HashMap<Skills, Boolean>

    enum class MainStats {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA, INSPIRATION, PROFICIENCY_BONUS, PASSIVE_WISDOM
    }

    enum class TypesForEditTexts {
        MainStats, SavingThrows, Skills, ARMOR_CLASS, INITIATIVE, SPEED, HIT_POINT_MAXIMUM,
        CURRENT_HIT_POINTS, TEMPORARY_HIT_POINTS, HIT_DICE, HIT_DICE_TOTAL, SUCCESSES, FAILURES
    }

    enum class SavingThrows {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
    }

    enum class Skills {
        ACROBATICS, ANIMAL_HANDLING, ARCANA, ATHLETICS, DECEPTION, HISTORY, INSIGHT, INTIMIDATION,
        INVESTIGATION, MEDICINE, NATURE, PERCEPTION, PERFORMANCE, PERSUASION, RELIGION,
        SLEIGHT_OF_HAND, STEALTH, SURVIVAL
    }

    class ATTACKS_SPELLCASTING {
        enum class Stats {
            NAME, ATK_BONUS, DAMAGE, DAMAGE_TYPE
        }
    }

    enum class EQUIPMENT {
        CP, SP, EP, GP, PP
    }

//    enum class DND_classes {
//        BARBARIAN, BARD, CLERIC, DRUID, FIGHTER, MONK, PALADIN, RANGER, ROGUE, SORCERER, WARLOCK,
//        WIZARD
//    }

//    enum class Races {
//        DRAGONBORN, DWARF, ELF, GNOME, HALF_ELF, HALFLING, HALF_ORC, HUMAN, TIEFLING
//    }
}
