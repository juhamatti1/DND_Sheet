package com.example.dnd_sheet

import kotlinx.serialization.Serializable


@Serializable
class Character private constructor() {
    companion object {

        // @Volatile, you ensure that:
        // - Reads and writes to the instance are not reordered by the compiler or processor.
        // - Changes made by one thread are visible to other threads immediately.
        @Volatile
        private var instance: Character? = null


        /**
         * If Character instance is null, lock the code block with synchronized,
         * check again if character is null, create a new Character instance and assign it to instance.
         * @param newCharacter Character to be loaded. Used when loading character from json file
         */
        fun getInstance(newCharacter: Character? = null): Character {
            instance ?: synchronized(this) {
                instance ?: Character().also { instance = it }
            }

            newCharacter ?: return instance!!

            synchronized(this) {
                instance = newCharacter
            }

            return instance!!
        }
    }

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
    var hitDice: String = "0"
    var hitDiceTotal: Int = 0
    val attacksSpellcasting = Array(3) {
        mutableMapOf(
            Attacks_spellcasting.NAME.ordinal to "",
            Attacks_spellcasting.ATK_BONUS.ordinal to "",
            Attacks_spellcasting.DAMAGE_TYPE.ordinal to ""
        )
    }
    var attacksSpellcastingText: String = ""
    var failures = BooleanArray(3) { false }
    var successes = BooleanArray(3) { false }
    var skillsProficiencyBonuses = BooleanArray(Skills.entries.size) { false }
    var savingThrowProficiencyBonuses = BooleanArray(SavingThrows.entries.size) { false }
    var proficienciesAndLanguages = ""
    var cp: Int = 0
    var sp: Int = 0
    var ep: Int = 0
    var gp: Int = 0
    var pp: Int = 0
    var equipmentText: String = ""

    enum class MainStats {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA, INSPIRATION,
        PROFICIENCY_BONUS, PASSIVE_WISDOM
    }

    enum class EditTextsId {
        MAINSTATS, SAVING_THROWS, SKILLS, ARMOR_CLASS, INITIATIVE, SPEED, PROFIENCIES_AND_LANGUAGES,
        HIT_POINT_MAXIMUM, CURRENT_HIT_POINTS, TEMPORARY_HIT_POINTS, HIT_DICE, HIT_DICE_TOTAL,
        SUCCESSES, FAILURES, ATTACKS_SPELLCASTING, ATTACKS_SPELLCASTING_TEXT, CP, SP, EP, GP, PP,
        EQUPIMENT_TEXT
    }

    enum class SavingThrows {
        STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
    }

    enum class Skills {
        ACROBATICS, ANIMAL_HANDLING, ARCANA, ATHLETICS, DECEPTION, HISTORY, INSIGHT, INTIMIDATION,
        INVESTIGATION, MEDICINE, NATURE, PERCEPTION, PERFORMANCE, PERSUASION, RELIGION,
        SLEIGHT_OF_HAND, STEALTH, SURVIVAL
    }

    enum class Attacks_spellcasting {
        NAME, ATK_BONUS, DAMAGE_TYPE
    }

//    enum class DND_classes {
//        BARBARIAN, BARD, CLERIC, DRUID, FIGHTER, MONK, PALADIN, RANGER, ROGUE, SORCERER, WARLOCK,
//        WIZARD
//    }

//    enum class Races {
//        DRAGONBORN, DWARF, ELF, GNOME, HALF_ELF, HALFLING, HALF_ORC, HUMAN, TIEFLING
//    }
}
