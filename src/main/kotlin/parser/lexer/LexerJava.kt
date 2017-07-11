
package parser.lexer


import regexp.patternSplit
import java.io.*

import java.util.*



fun tokenizeSource(sourcePath: String,separator: String) : List<Token> {
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


            tokens.addAll(currentLine.patternSplit(separator).map {
                indexOfWord++
                when {
                    it == "+" -> Terminal("summinus",it,indexOfLine,indexOfWord)
                    it == "-" -> Terminal("summinus",it,indexOfLine,indexOfWord)
                    it == "*" -> Terminal("divmul",it,indexOfLine,indexOfWord)
                    it == "/" -> Terminal("divmul",it,indexOfLine,indexOfWord)
                    it == "(" -> Terminal("lparen",it,indexOfLine,indexOfWord)
                    it == ")" -> Terminal("rparen",it,indexOfLine,indexOfWord)
                    it.isNumber() -> Terminal("int",it.toInt(),indexOfLine,indexOfWord)
                    else -> NoSuchToken(it,indexOfLine,indexOfWord)
                }

            })

            indexOfLine++
        }

        tokens.add(EndOfFile("EndOfFile",indexOfLine,indexOfWord))

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return tokens


}
