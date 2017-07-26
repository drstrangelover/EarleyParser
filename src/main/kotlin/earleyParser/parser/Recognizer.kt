package earleyParser.parser

import earleyParser.lexer.NonTerminal
import earleyParser.lexer.Terminal
import earleyParser.lexer.Token
import earleyParser.lexer.tokenizeGrammar
import java.util.*
import kotlin.collections.ArrayList


data class Rule(val header: Token,val body: ArrayList<Token>) {

    fun isBodyEquals(body: ArrayList<Token>): Boolean {
        if (body.size == this.body.size) {
            for ((index,token) in body.withIndex()) {
                if (token.name != this.body[index].name) return false
                if ((token is Terminal && this.body[index] is Terminal) ||
                        (token is NonTerminal && this.body[index] is NonTerminal)) {
                    if (this.body[index] is Terminal && token is Terminal) {
                        if (token.value != (this.body[index] as Terminal).value) return false
                    }
                    return true
                }
            }
        }
        return false
    }

    fun equals(rule: Rule): Boolean {
        if (this.header.name == rule.header.name &&
                isBodyEquals(rule.body)) {
            return true
        }
        return false
    }
}


open class State(
        open var id: Int,
        open val childrens: ArrayList<Int>,
        open val rule: Rule,
        open val initialChart: Int,
        open var finalChart: Int,
        open var nextToken: Int) {
    fun equals(state: State): Boolean {
        if (this.rule.equals(state.rule) && this.initialChart == state.initialChart
                && this.nextToken == state.nextToken) {
            return true
        }
        return false
    }
}

var id = 0
fun append(setOfStates: ArrayList<State>, state: State) {
    state.id = id
    for (stateToCompare in setOfStates) {
        if (stateToCompare.equals(state)) return Unit
    }
    setOfStates.add(state)
    id++
}


fun buildStates(input: LinkedList<Token>): ArrayList<ArrayList<State>> {
    val grammarObject  = tokenizeGrammar("src/main/kotlin/earleyParser/data/GRAMMAR.txt")
    val grammar = grammarObject.grammar


    var setOfStates : ArrayList<ArrayList<State>> = arrayListOf(arrayListOf())
    grammar
            .filter { it.header.name == "sum" }
            .forEach { append(setOfStates[0],State(-1, arrayListOf(),it,0,0,0)) }

    var i = 0
    while (i < setOfStates.size) {
        var j = 0
        while (j < setOfStates[i].size) {
            if (setOfStates[i][j].rule.body.size == setOfStates[i][j].nextToken ) {
                setOfStates = complete(setOfStates,i,j)
                j++
                continue
            }
            val nextToken = setOfStates[i][j].rule.body[setOfStates[i][j].nextToken]


            if (nextToken is Terminal) setOfStates = scan(setOfStates,i,j,nextToken,input)
            if (nextToken is NonTerminal) setOfStates = predict(setOfStates, i,nextToken,grammar)
            j++
        }
        i++
    }



    return setOfStates
}

fun predict(setOfStates: ArrayList<ArrayList<State>>, index: Int, nextToken: Token, grammar: ArrayList<Rule>)
         :ArrayList<ArrayList<State>>{
    val state = setOfStates[index]
    grammar
            .filter { it.header.name == nextToken.name }
            .forEach { append(state, State(-1, arrayListOf(),it,index,index,0)) }
    return setOfStates
}




fun scan(setOfStates: ArrayList<ArrayList<State>>, i: Int, j: Int, nextToken: Token, input: LinkedList<Token>)
        :ArrayList<ArrayList<State>>{
    val state = setOfStates[i][j]
    if (input[i].name == state.rule.body[state.nextToken].name) {
        if (setOfStates.size == i + 1) setOfStates.add(arrayListOf<State>())
        append(setOfStates[i+1],State(-1, arrayListOf(),state.rule,state.initialChart,i,state.nextToken + 1))
    }
    return setOfStates
}

fun complete(setOfStates: ArrayList<ArrayList<State>>, i: Int, j: Int)
        :ArrayList<ArrayList<State>>{
    val state = setOfStates[i][j]
    setOfStates[state.initialChart]
            .filter { it.rule.body.size > it.nextToken }
            .filter { it.rule.body[it.nextToken].name == state.rule.header.name }
            .forEach {
                val childrens: ArrayList<Int> = it.childrens

                     childrens.add(state.id)


                append(setOfStates[i], State(-1,childrens,it.rule,
                        it.initialChart,i,
                        it.nextToken + 1))
            }
    return setOfStates
}

