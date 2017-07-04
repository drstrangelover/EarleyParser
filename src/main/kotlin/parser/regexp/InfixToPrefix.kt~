package regexp

import java.util.*



val alphabet = (' '..'z').filter { !setOf('+','|','*','(',')','.',']','[','?').contains(it) }


private fun rangeToUnion(regExp: String) : String {
    var union = ""
    val length = regExp.length
    var i = 0
    var rangeFrom : Char
    var rangeTo   : Char
    while (i < length) {
        if (regExp[i] == '[') {
            rangeFrom = regExp[i+1]
            rangeTo   = regExp[i+3]
            union += '('
            var b = 0
            for (char in rangeFrom..rangeTo) {
                if (!alphabet.contains(char)) continue
                if (b != 0) { union += '|' }
                union += char
                b++
            }
            union += ')'
            i += 5
        }
        union += regExp[i]
        i++
    }

    return union
}


private fun explicitConcatination(regExp:String) : String {
    var concatinated = ""
    val length = regExp.length
    for (i in 0..(length-1)) {
        concatinated += regExp[i]
        if ( !setOf('|','.','(').contains(regExp[i]) && (i != length - 1)
                && !setOf('|','.',')','*','+','?').contains(regExp[i+1]) ) {
            concatinated += "."
        }
    }
    return concatinated

}

private fun getPrecedence(ch: Char): Int {
    when (ch) {
        '|' -> return 1
        '?' -> return 2
        '*' -> return 2
        '+' -> return 2
        '.' -> return 3
    }
    return -1
}



private fun Char.isOperator(): Boolean {
    return setOf('+','|','*','(',')','.','?').contains(this)
}


private fun Char.isOperand(): Boolean {
    return alphabet.contains(this)
}

fun infixToPrefix(rawInfix: String): String {
    val unionInfix = rangeToUnion(rawInfix)

    val infix = explicitConcatination(unionInfix)
    val stack = Stack<Char>()
    val postfix = StringBuffer(infix.length)

    for (i in 0..infix.length - 1) {
        val c = infix[i]
        when {
            c.isOperand()  -> postfix.append(c)
            c == '('       -> stack.push(c)
            c == ')'       -> {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop())
                }
                if (!stack.isEmpty())
                    stack.pop()
            }
            c == '?'       -> postfix.append(c)
            c == '*'       -> postfix.append(c)
            c == '+'       -> postfix.append(c)
            c.isOperator() -> {
                if (!stack.isEmpty() && getPrecedence(c) <= getPrecedence(stack.peek())) {
                    postfix.append(stack.pop())
                }
                stack.push(c)
            }
        }
    }
    while (!stack.isEmpty()) {
        postfix.append(stack.pop())
    }
    return postfix.toString()
}
