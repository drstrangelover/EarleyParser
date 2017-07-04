package parser

import parser.lexer.tokenizeGrammar
import parser.lexer.tokenizeSource
import parser.parser.buildEarleyItems
import parser.regexp.NFA
import parser.regexp.infixToPrefix
import java.util.*


fun main(args: Array<String>) {
    fun mathRegExp(string : String, regexp : String) {
        println("$string  >>>>  $regexp  = ${NFA(infixToPrefix(regexp)).match(string)}")
    }

    mathRegExp("@b/&&","[>-z]+/*&+p?m*")

    val tokens  = LinkedList(tokenizeSource("src/main/kotlin/parser/data/source.txt"))
    val grammar = tokenizeGrammar("src/main/kotlin/parser/data/GRAMMAR.txt")
    val setOfStates = buildEarleyItems(tokens)

    println()

//
//    val parser = ParseThis(tokens)
//    parser.next()
//    val result = parser.parseE()

}



