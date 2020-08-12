package flashcards

import java.io.File
import java.io.FileNotFoundException
import kotlin.random.Random.Default.nextInt
import kotlin.system.exitProcess

data class Flashcard (val card: String, val definition: String, var mistakesMade: Int = 0)

class Flashcards {

    private val cards = mutableMapOf<String, Flashcard>()
    private val loggedLines = mutableListOf<String>()
    var commandLineArgument = false
    var commandLineExportFile = ""

    fun handleAction() {

        while (true) {
            println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
            when (readLine()!!) {
                "add" -> add()
                "remove" -> remove()
                "import" -> import()
                "export" -> export()
                "ask" -> ask()
                "log" -> log()
                "hardest card" -> hardestCard()
                "reset stats" -> resetStats()
                "exit" -> exit()
            }
        }

    }
    private fun readln(): String {
        val line = readLine()!!
        loggedLines.add(line)
        return line
    }
    private fun writeln(line: String) {
        loggedLines.add(line)
        println(line)
    }

    private fun log() {
        writeln("File name:")
        val fileName = readln()
        val myFile = File(fileName)
        try {
            myFile.writeText(loggedLines.joinToString("\n"))
            writeln("The log has been saved.")
        } catch (e: FileNotFoundException) {
            writeln("File not Found.")
        }
    }

    private fun hardestCard() {
        val hardestCards = mutableListOf<Flashcard>()
        val maxMistakes = cards.values.maxBy { it.mistakesMade }?.mistakesMade
        if(maxMistakes == 0 || maxMistakes == null) {
            writeln("There are no cards with errors.")
            return
        }

        for (card in cards.values) {
            if (card.mistakesMade == maxMistakes) hardestCards.add(card)
        }

        if (hardestCards.size == 1) {
            writeln("The hardest card is \"${hardestCards.first().card}\". You have $maxMistakes answering it.")
        } else {
            writeln("The hardest cards are ${hardestCards.joinToString(", ") { "\"${it.card}\"" }}. " +
                    "You have $maxMistakes errors answering them.")
        }

    }

    private fun resetStats() {
        cards.values.forEach { it.mistakesMade = 0}
        writeln("Card statistics has been reset.")
    }

    private fun exit() {
        if (!commandLineArgument) {
            writeln("Bye bye!")
            exitProcess(1)
        } else {
            writeln("Bye bye!")
            val myFile = File(commandLineExportFile)
            myFile.writeText(cards.values.joinToString("\n") { "${it.card}:${it.definition}:${it.mistakesMade}"})
            writeln("${cards.size} cards have been saved.")
            exitProcess(1)
        }
    }

    private fun add() {
        writeln("The card:")
        val term = readln()
        if(cards.containsKey(term)) {
            writeln("The card \"$term\" already exists.")
            return
        }
        writeln("The definition of the card:")
        val definition = readln()
        if (cards.filterValues { it.definition == definition }.keys.firstOrNull() != null) {
            writeln("The definition \"$definition\" already exists.")
            return
        }
        cards[term] = Flashcard(term, definition)
        writeln("The pair (\"$term\":\"$definition\") has been added.")
    }

    private fun remove(){
        writeln("The card:")
        val cardToBeRemoved = readln()
        if (cards.containsKey(cardToBeRemoved)) {
            cards.remove(cardToBeRemoved)
            writeln("The card has been removed")
        } else {
            writeln("Can't remove \"$cardToBeRemoved\": there is no such card.")
        }
    }

    private fun import() {
        writeln("File name:")
        val fileName = readln()
        val myFile = File("/Users/marcobraun/IdeaProjects/Flashcards/Flashcards/task/$fileName")
        try {
            val importLines = myFile.readLines()
            importLines.forEach {
                val entry = it.split(":")
                val card = Flashcard(entry[0], entry[1])
                card.mistakesMade = entry[2].toInt()
                cards[entry[0]] = card
            }
            writeln("${importLines.size} cards have been loaded")
        } catch (e: FileNotFoundException) {
            writeln("File not Found.")
            return
        }
    }

    private fun export() {
        writeln("File name:")
        val fileName = readln()
        val myFile = File(fileName)
        try {
            myFile.writeText(cards.values.joinToString("\n") { "${it.card}:${it.definition}:${it.mistakesMade}"})
            writeln("${cards.size} cards have been saved.")
        } catch (e: FileNotFoundException) {
            writeln("File not Found.")
        }
    }

    private fun ask() {
        writeln("How many times to ask?")
        val timesToAsk = readln().toInt()

        repeat (timesToAsk) {
            val randomCard = cards.entries.elementAt(nextInt(cards.size)).value
            val (_, definition) = randomCard

            writeln("Print the definition of \"${randomCard.card}\":")
            val answer = readln()

            if (definition == answer) {
                writeln("Correct answer.")
            }
            else {
                randomCard.mistakesMade++
                val actualCard = cards.filterValues { it.definition == answer }.keys.firstOrNull()
                if(actualCard != null) {
                    writeln("Wrong. The right answer is \"$definition\"," +
                            "but your definition is correct for \"$actualCard\".")
                } else {
                    writeln("Wrong. The right answer is \"$definition\". " +
                            "The asked card was \"${randomCard.card}\", the answer was \"$answer\".")
                }
            }
        }
    }

    fun importantImportCommand(file: String) {
        val importFile = File(file)
        val importFileList = importFile.readLines()
        importFileList.forEach {
            val entry = it.split(":")
            val card = Flashcard(entry[0], entry[1])
            card.mistakesMade = entry[2].toInt()
            cards[entry[0]] = card
        }
        writeln("${importFileList.size} cards have been loaded")
    }
}
fun main(args: Array<String>) {
    val fc = Flashcards()
    if (args.isNotEmpty()){
        for (index in args.indices) {
            if (args[index] == "-import") fc.importantImportCommand(args[index + 1])
            if (args[index] == "-export") {
                fc.commandLineArgument = true
                fc.commandLineExportFile = args[index + 1]
            }
        }
    }
    fc.handleAction()
}
