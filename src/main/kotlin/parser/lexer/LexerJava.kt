
package parser.lexer


import java.io.*

import java.util.*



fun tokenizeSource(sourcePath: String) : List<Token> {
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


            tokens.addAll(currentLine.split(" ").map {
                indexOfWord++
                when {
                    it == "+" -> Terminal("SUM",it,indexOfLine,indexOfWord)
                    it == "-" -> Terminal("MINUS",it,indexOfLine,indexOfWord)
                    it == "*" -> Terminal("MUL",it,indexOfLine,indexOfWord)
                    it == "/" -> Terminal("DIV",it,indexOfLine,indexOfWord)
                    it == "(" -> Terminal("LPAREN",it,indexOfLine,indexOfWord)
                    it == ")" -> Terminal("RPAREN",it,indexOfLine,indexOfWord)
                    it.isNumber() -> Terminal("INT",it.toInt(),indexOfLine,indexOfWord)
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
