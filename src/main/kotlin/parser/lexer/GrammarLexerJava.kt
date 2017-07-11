package parser.lexer


import parser.parser.Rule
import regexp.findFirst
import regexp.findLast
import regexp.match
import regexp.patternSplit
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


class Grammar(val separator: String, val root: String, val grammar: ArrayList<Rule>)



fun tokenizeGrammar(sourcePath: String) : Grammar {
    var separator = " +"
    var root      = ""
    val sourceGrammar = arrayListOf<String>()
    val formatedSource = arrayListOf<String>()
    val grammar : ArrayList<Rule> = ArrayList()

    try {
        val sourceFile = File(sourcePath)
        val bufferReader = BufferedReader(FileReader(sourceFile) as Reader?)
        var currentLine = ""
        while (true) {
            currentLine = bufferReader.readLine() ?: "//EndOfFile"
            if (currentLine == "//EndOfFile") break

            else if (currentLine.match(":separator.*")) {
                separator = currentLine.findFirst("\'.*\'").drop(1).dropLast(1)
                sourceGrammar.add("")
            } else if (currentLine.match(":root.*")) {
                root = currentLine.findLast("= *(^ )+").drop(1).dropWhile { it == ' ' }
                sourceGrammar.add("")
            } else {
                sourceGrammar.add(currentLine)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    var i = -1
    for ( line in sourceGrammar) {
        if (line == "") continue
        if (line.match("(^  )+ *=.*")) {
            throw Exception("Declaration of assignment should be at the very begging of the line")
        }
        if (line.match(".*^=.*")) {
            formatedSource.add(line)
            i++
            continue
        }
        formatedSource[i] += " $line"
    }


    var indexOfExpr = 1

    for (expression in formatedSource) {
        var halfTerminal = ""
        var header : Token = Token("temp",-1,-1)
        val body    = ArrayList<Token>()
        val words = expression.patternSplit(" +")
        for ((indexOfWord,word )in words.withIndex()) {
            if (indexOfWord == 0) {
                header = NonTerminal(word,indexOfExpr,indexOfWord)
                continue
            }
            if (indexOfWord == 1) continue


            if (word.match("'.*'")) {
                body.add(Terminal(word.drop(1).dropLast(1),word.drop(1).dropLast(1),indexOfExpr,indexOfWord))
                continue
            }

            if (word.match("'.*")) {
                halfTerminal = word
                continue
            }

            if (word.match(".*'")) {
                body.add(Terminal("$halfTerminal ${word.dropLast(1)}","$halfTerminal ${word.dropLast(1)}",indexOfExpr,indexOfWord))
                continue
            }

            body.add(NonTerminal(word,indexOfExpr,indexOfWord))
        }
        indexOfExpr++


        grammar.add(Rule(header,body))


        }


    return Grammar(separator,root,grammar)


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
