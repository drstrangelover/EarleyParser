package parser

import parser.lexer.tokenizeGrammar
import parser.lexer.tokenizeSource
import parser.parser.buildStates
import java.util.*


fun main(args: Array<String>) {



    val grammarObject = tokenizeGrammar("src/main/kotlin/parser/data/GRAMMAR.txt")
    val tokens  = LinkedList(tokenizeSource("src/main/kotlin/parser/data/source.txt",grammarObject))
    val grammar = tokenizeGrammar("src/main/kotlin/parser/data/GRAMMAR.txt")
    val setOfStates = buildStates(tokens)

    println()

//
//    val parser = ParseThis(tokens)
//    parser.next()
//    val result = parser.parseE()

}



