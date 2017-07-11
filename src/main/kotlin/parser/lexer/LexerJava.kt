
package parser.lexer


import regexp.match
import regexp.patternSplit
import java.io.*

import java.util.*



fun tokenizeSource(sourcePath: String,grammarObject: Grammar) : List<Token> {
    val separator = grammarObject.separator
    val grammar = grammarObject.grammar
    val tokens : LinkedList<Token> = LinkedList()

    try {

        val sourceFile = File(sourcePath)

        val bufferReader = BufferedReader(FileReader(sourceFile) as Reader?)

        var currentLine = ""

        var indexOfLine = 1
        var indexOfWord = 0
        while (true) {
            currentLine = bufferReader.readLine() ?: "//endOfFile"
            if (currentLine == "//endOfFile") break


            loop@ for(currentWord in currentLine.patternSplit(separator)) {
                indexOfWord++

                for ((index,rule) in grammar.withIndex()) {
                    for (token in rule.body) {
                        if (token is Terminal) {
                            if (currentWord.match(token.value as String)) {
                              tokens.add(Terminal(token.value as String,currentWord,indexOfLine,indexOfWord))
                                continue@loop
                            }
                        }
                    }
                    if (grammar.size - 1 == index) {
                       tokens.add(NoSuchToken(currentWord,indexOfLine,indexOfWord))
                    }
                }



            }

            indexOfLine++
        }

        tokens.add(EndOfFile("EndOfFile",indexOfLine,indexOfWord))

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return tokens


}
