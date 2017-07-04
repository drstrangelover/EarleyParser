package parser.parser

import parser.lexer.NonTerminal
import parser.lexer.Terminal
import parser.lexer.Token
import parser.lexer.tokenizeGrammar
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
                        if (!token.value.equals((this.body[index] as Terminal).value)) return false
                    }
                    return true
                }
            }
        }
        return false
    }

    fun equals(rule: Rule): Boolean {
        if (this.header.name.equals(rule.header.name) &&
                isBodyEquals(rule.body)) {
            return true
        }
        return false
    }
}


data class State(
        val rule: Rule,
        val initialState: Int,
        var nextToken: Int) {
    fun equals(state: State): Boolean {
        if (this.rule.equals(state.rule) && this.initialState == state.initialState
                && this.nextToken == state.nextToken) {
            return true
        }
        return false
    }
}


fun append(setOfStates: ArrayList<State>, state: State) {
    for (stateToCompare in setOfStates) {
        if (stateToCompare.equals(state)) return Unit
    }
    setOfStates.add(state)
}


fun buildEarleyItems(input: LinkedList<Token>): ArrayList<ArrayList<State>> {
    val grammar : ArrayList<Rule> = tokenizeGrammar("src/main/kotlin/parser/data/GRAMMAR.txt")

    var setOfStates : ArrayList<ArrayList<State>> = arrayListOf(arrayListOf())
    grammar
            .filter { it.header.name == "SUM" }
            .forEach { setOfStates[0].add(State(it,0,0)) }


    var i = 0
    while (i < setOfStates.size) {
        var j = 0
        while (j < setOfStates[i].size) {
            if (setOfStates[i][j].rule.body.size == setOfStates[i][j].nextToken ) {
                setOfStates = complete(setOfStates,i,j,grammar)
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
    val currentEarleyItems = setOfStates[index]
    grammar
            .filter {it.header.name.equals(nextToken.name)}
            .forEach { append(currentEarleyItems, State(it,index,0)) }
    setOfStates[index] = currentEarleyItems
    return setOfStates
}




fun scan(setOfStates: ArrayList<ArrayList<State>>, i: Int, j: Int, nextToken: Token, input: LinkedList<Token>)
        :ArrayList<ArrayList<State>>{
    val state = setOfStates[i][j]
    if (input[i].name == state.rule.body[state.nextToken].name) {
        if (setOfStates.size == i + 1) setOfStates.add(arrayListOf<State>())
        setOfStates[i+1].add(State(state.rule,state.initialState,state.nextToken + 1))
    }
    return setOfStates
}

fun complete(setOfStates: ArrayList<ArrayList<State>>, i: Int, j: Int, grammar: ArrayList<Rule>)
        :ArrayList<ArrayList<State>>{
    val state = setOfStates[i][j]
    setOfStates[state.initialState]
            .filter { it.rule.body[it.nextToken].name == state.rule.header.name }
            .forEach {
                append(setOfStates[i], State(it.rule,
                        it.initialState,
                        it.nextToken + 1))
            }
    return setOfStates
}


