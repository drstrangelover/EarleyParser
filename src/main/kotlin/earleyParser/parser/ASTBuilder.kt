package earleyParser.parser

import earleyParser.lexer.Terminal
import earleyParser.lexer.Token
import java.util.*
import kotlin.collections.ArrayList


fun removeScans(setOfStates: ArrayList<ArrayList<State>>) : ArrayList<ArrayList<State>> {
    val stateWithoutScans = arrayListOf(arrayListOf<State>())



    for ((i,states) in setOfStates.withIndex()) {

        for (state in states) {

            if (state.nextToken == state.rule.body.size) {
                stateWithoutScans[i].add(state)
            }
        }
        if (i + 1 != setOfStates.size) {
            stateWithoutScans.add(arrayListOf<State>())
        }
    }
    return stateWithoutScans

}


fun getRootsIds(root: String, tokenizedSource: List<Token>, setOfStates: ArrayList<ArrayList<State>>): Set<Int> {

    val matchedTokens = mutableSetOf<Int>()

    if (tokenizedSource.size == setOfStates.size ) {
        for ((index,state) in setOfStates[setOfStates.size - 1].withIndex()) {
            if (state.nextToken == state.rule.body.size
                    && state.initialChart == 0
                    && state.rule.header.name == root) {
                matchedTokens.add(state.id)
            }
        }
    }

    return matchedTokens
}





fun removeWrongWay(oldTree: MutableMap<Int,ArrayList<Int>>,globalNewTree: MutableMap<Int,ArrayList<Int>>, rootId: Int) : MutableMap<Int,ArrayList<Int>> {

    var newTree  = globalNewTree
    // parent id : child id


    val childrens = oldTree[rootId]//findNode(rootId,oldTree)

    newTree.put(rootId, arrayListOf<Int>())

    if (childrens == null) return newTree

    for (childId in childrens) {
        if (childId == rootId) continue
        newTree = findAndDeletePrevNode(childId,newTree)
        newTree[rootId]!!.add(childId)
        newTree = removeWrongWay(oldTree,newTree,childId)
    }

    return newTree

}



fun findAndDeletePrevNode(childId: Int,newTree: MutableMap<Int, ArrayList<Int>>): MutableMap<Int, ArrayList<Int>>{
    val cleanNewTree = mutableMapOf<Int, ArrayList<Int>>()

    if (newTree.isEmpty()) return cleanNewTree

    if (newTree.values.any { it.contains(childId) }) {
        val keys = newTree.keys

        for (key in keys) {
            cleanNewTree.put(key,ArrayList(newTree[key]!!.filter { it != childId }))
        }

        return cleanNewTree
    }

    return newTree
}



fun getOldTree(setOfStates: ArrayList<ArrayList<State>>): MutableMap<Int, ArrayList<Int>> {
    val oldTree = mutableMapOf<Int,ArrayList<Int>>()

    setOfStates
            .flatMap { it }
            .forEach { oldTree.put(it.id, it.childrens) }

    return oldTree
}


fun printAllLinks(setOfStates: ArrayList<ArrayList<State>>) {


    val oldTree  = arrayListOf<Pair<Int,ArrayList<Int>>>()

    for (states in setOfStates) {
        for (state in states) {
            if (!state.childrens.isEmpty()) {
               println("${state.id} : ${state.childrens}")
                //oldTree.add(Pair(state.id,state.childrens))
            }
        }
    }
    println()

}











var j = 0

fun buildParseTree(setOfStates: ArrayList<ArrayList<State>>, rootId: Int, source: LinkedList<Token>, cleanTree: MutableMap<Int, ArrayList<Int>>) : String {

    j++
    var parseTree = ""

    var root : State = State(-1, arrayListOf(), Rule(Token("",-1,-1), arrayListOf()),-1,-1,-1)
    loop@for (states in setOfStates) {
        for (state in states) {

            if (state.id == rootId) {
                root = state
                break@loop
            }
        }
    }
    val childrens = cleanTree[rootId]!!



    parseTree += "(${root.rule.header.name} "

    if (root.childrens.size == 0) {

        parseTree += "\"${(source[root.initialChart] as Terminal).value}\""

    } else {
        for (childId in childrens) {

            parseTree += buildParseTree(setOfStates,childId,source, cleanTree)
        }
    }

    parseTree += ")"
    j--
    return parseTree
}


























