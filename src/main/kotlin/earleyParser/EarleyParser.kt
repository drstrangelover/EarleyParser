package earleyParser

import earleyParser.lexer.tokenizeGrammar
import earleyParser.lexer.tokenizeSource
import earleyParser.parser.*
import java.util.*
import java.nio.file.Paths
import java.nio.file.Files


fun main(args: Array<String>) {

    val parseTrees = mutableSetOf<String>()

    val grammarObject = tokenizeGrammar("src/main/kotlin/earleyParser/data/GRAMMAR.txt")
    val source  = LinkedList(tokenizeSource("src/main/kotlin/earleyParser/data/source.txt",grammarObject))
    val setOfStates = buildStates(source)
    val statesWithoutScans = removeScans(setOfStates)
    val rootsIds = getRootsIds(grammarObject.root,source, statesWithoutScans)



    for (rootId in rootsIds) {
        val newWay = removeWrongWay(getOldTree(statesWithoutScans), mutableMapOf(),rootId)
        parseTrees.add(buildParseTree(statesWithoutScans,rootId,source,newWay))
        println()
    }


    Files.newBufferedWriter(Paths.get("output.tree")).use { writer ->
        for (parseTree in parseTrees) {
            println(parseTree)
            writer.write(parseTree)
        }
    }




    println()


}



