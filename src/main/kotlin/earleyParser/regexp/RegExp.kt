package regexp

import main.kotlin.regexp.infixToPostfix

fun String.matchInside(regexp: String): Boolean {
    return NFA(infixToPostfix(".*$regexp.*")).match(this)
}

fun String.match(regexp: String) : Boolean {
    return NFA(infixToPostfix(regexp)).match(this)
}


fun String.findFirst(regexp: String) : String {
    if (this.matchInside(regexp)) {
        var i = 0
        while (!this.dropLast(this.length - 1 - i).matchInside( regexp)) {
            i++
        }
        var j = 0
        while (this.drop(j).dropLast(this.length - 1 - i).matchInside( regexp)) {
            j++
        }
        return this.drop(j-1).dropLast(this.length - 1 -i)

    }
    return ""
}

fun String.findLast(regexp: String) : String {
    if (this.matchInside(regexp)) {
        var i = 1
        while (this.drop(i).matchInside(regexp)) {
            i++
        }
        i--
        var j = 0
        while (!(this.drop(i).dropLast(j).match(regexp))) {
            j++
        }

        return this.drop(i).dropLast(j)

    }
    return ""
}


fun String.patternSplit(regexp: String) : List<String> {
    val splitted = mutableListOf<String>()
    var temp = this
    while (temp.matchInside(regexp)) {
        var i = 0
        while (!temp.dropLast(temp.length - i).match(".*$regexp")) {
            i++
        }
        if (temp.dropLast(temp.length  -i).match(regexp)) {

                temp = temp.drop(i)

        } else {
            splitted.add(temp.dropLast(temp.length - i + 1))
            temp = temp.drop(i)
        }
    }
    splitted.add(temp)
    return splitted


}
