package main.kotlin.regexp

import regexp.*
import java.util.*
import kotlin.collections.ArrayList


val operators = setOf('+','|','*','(',')','[',']','?')

internal fun tokenize(input: String) : ArrayList<Token> {
    val regex = arrayListOf<Token>()
    for ((index,char) in input.withIndex()) {
        if (char == '\\' || char == '^') {}
        else if (index != 0 && input[index-1] == '\\' ) {
            regex.add(OperandToken(char))
        } else if (index != 0  && input[index-1] == '^') {
            if (index == 1 ) {
                regex.add(ExceptOperandToken(char))
            } else if (input[index-2] != '\\') {
                regex.add(ExceptOperandToken(char))
            }
        } else if (char in operators) {
            regex.add(OperatorToken(char))
        } else if(char == '.') {
            regex.add(AnyOperandToken(char))
        } else {
            regex.add(OperandToken(char))
        }
    }
    return regex
}


internal fun rangeToUnion(regExp: ArrayList<Token>) : ArrayList<Token> {
    val union = arrayListOf<Token>()
    val length = regExp.size
    var i = 0
    var rangeFrom : Token
    var rangeTo   : Token
    while (i < length) {
        if (regExp[i] is OperatorToken && regExp[i].value == '[') {
            rangeFrom = regExp[i+1]
            rangeTo   = regExp[i+2]
            union.add(OperatorToken('('))
            var b = false
            for (char in rangeFrom.value..rangeTo.value) {
                if (b) { union.add(OperatorToken('|'))}
                union.add(OperandToken(char))
                b = true
            }
            union.add(OperatorToken(')'))
            i += 4
        }
        union.add(regExp[i])
        i++
    }

    return union
}


internal fun explicitConcatination(regExp: ArrayList<Token>) : ArrayList<Token> {
    val concatinated = arrayListOf<Token>()
    val length = regExp.size
    for (i in 0..(length-1)) {
        concatinated.add(regExp[i])
        if (!(setOf('|','(').contains(regExp[i].value)
                && regExp[i] is OperatorToken )
                && (i != length - 1)
                && !(setOf('|',')','*','+','?').contains(regExp[i+1].value)
                && regExp[i+1] is OperatorToken )) {
            concatinated.add(OperatorToken('c'))
        }
    }
    return concatinated

}

internal fun getPrecedence(ch: Char): Int {
    when (ch) {
        '|' -> return 1
        '?' -> return 2
        '*' -> return 2
        '+' -> return 2
        'c' -> return 3
    }
    return -1
}


internal fun infixToPostfix(rawInfix: String): ArrayList<Token> {
    val tokenized = tokenize(rawInfix)
    val unionInfix = rangeToUnion(tokenized)

    val infix = explicitConcatination(unionInfix)
    val stack = Stack<Token>()
    val postfix = ArrayList<Token>(infix.size)
    for (i in 0..infix.size - 1) {
        val c = infix[i]
        when(c) {
            is OperandToken -> postfix.add(c)
            is OperatorToken -> {
                when(c.value) {
                    '('       -> stack.push(c)
                    ')'       -> {
                        while (!stack.isEmpty() && stack.peek().value != '(') {
                            postfix.add(stack.pop())
                        }
                        if (!stack.isEmpty())
                            stack.pop()
                    }
                    '?'       -> postfix.add(c)
                    '*'       -> postfix.add(c)
                    '+'       -> postfix.add(c)
                    else -> {
                        if (!stack.isEmpty() && getPrecedence(c.value) <= getPrecedence(stack.peek().value)) {
                            postfix.add(stack.pop())
                        }
                        stack.push(c)
                    }
                }

            }

        }
    }
    while (!stack.isEmpty()) {
        postfix.add(stack.pop())
    }
    return postfix
}
