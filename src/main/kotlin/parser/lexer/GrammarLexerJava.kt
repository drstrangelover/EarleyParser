package parser.lexer


import parser.parser.Rule
import java.io.*

import java.util.*
import kotlin.collections.ArrayList


fun String.isNumber() : Boolean {
    try {
        Integer.valueOf(this)
        return true
    } catch (e : Exception) {
        return false
    }
}


fun tokenizeGrammar(sourcePath: String) : ArrayList<Rule> {
    val grammar : ArrayList<Rule> = ArrayList()

    try {

        val sourceFile = File(sourcePath)

        val bufferReader = BufferedReader(FileReader(sourceFile) as Reader?)

        var currentLine = ""



        var indexOfLine = 1
        while (true) {
            currentLine = bufferReader.readLine() ?: "//EndOfFile"
            if (currentLine == "//EndOfFile") break

            var header : Token = Token("temp",-1,-1)
            val body    = ArrayList<Token>()
            val words = currentLine.split(" ")
            for ((indexOfWord,word )in words.withIndex()) {
                if (indexOfWord == 0) {
                    header = NonTerminal(word,indexOfLine,indexOfWord)
                    continue
                }
                if (indexOfWord == 1) continue
                if (!word.isNumber() && !listOf("(",")","+","-","*","/").contains(word)) {
                    body.add(NonTerminal(word,indexOfLine,indexOfWord))
                    continue
                }
                body.add(
                        when {
                            word == "+" -> Terminal("SUM", word, indexOfLine, indexOfWord)
                            word == "-" -> Terminal("MINUS", word, indexOfLine, indexOfWord)
                            word == "*" -> Terminal("MUL", word, indexOfLine, indexOfWord)
                            word == "/" -> Terminal("DIV", word, indexOfLine, indexOfWord)
                            word == "(" -> Terminal("LPAREN", word, indexOfLine, indexOfWord)
                            word == ")" -> Terminal("RPAREN", word, indexOfLine, indexOfWord)
                            word.isNumber() -> Terminal("INT", word.toInt(), indexOfLine, indexOfWord)
                            else -> NoSuchToken(word, indexOfLine, indexOfWord)
                        }
                )
            }
            indexOfLine++


            grammar.add(Rule(header,body))


        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return grammar


}



//Sum     = Sum    + Product
//Sum     = Sum    - Product
//Sum     = Product
//Product = Product * Factor
//Product = Product / Factor
//Product = Factor
//Factor  = '(' Sum ')'
//Factor  = Number
//Number  = 1 Number
//Number  = 2 Number
//Number  = 3 Number
//Number  = 4 Number
//Number  = 1
//Number  = 2
//Number  = 3
//Number  = 4
//
//
//
//
//val grammar = Grammar(arrayListOf(
//        Rule(NonTerminal("SUM"), arrayListOf(NonTerminal("SUM"),Terminal("SUM","+"),NonTerminal("PRODUCT")),)
//))