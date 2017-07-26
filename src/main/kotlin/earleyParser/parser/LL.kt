//package parser.parser
//
//import earleyParser.lexer.*
//
//
//// support only ( ) + *
//class ParseThis(val tokens: List<Token>) {
//
//
//
//    internal lateinit var currentToken: Token
//    internal var currentId = 0
//    internal val limit = tokens.size
//
//    /** read the next token into currentToken  */
//    internal operator fun next() {
//
//        currentToken = tokens[currentId]
//        currentId++
//
//
//    }
//
//    internal fun error(msg: String) {
//        System.err.println(msg)
//        System.exit(-1)
//    }
//
//    internal fun parseE(): Int {
//        // E -> T E1
//        val x = parseT()
//        return parseE1(x)
//    }
//
//    internal fun parseE1(x: Int): Int {
//        // E1 -> T E1 | epsilon
//        if (currentToken is PLUS) {
//            next()
//            val y = parseT()
//            return parseE1(x + y)
//        } else if (currentToken is RBRACKET || currentToken is EndOfFile) {
//            return x
//        } else {
//            error("Unexpected :" + currentToken)
//            return x // to make compiler happy
//        }
//    }
//
//    internal fun parseT(): Int {
//        // T -> F T1
//        val x = parseF()
//        return parseT1(x)
//    }
//
//    internal fun parseT1(x: Int): Int {
//        // T1 -> * F T1 | epsilon
//        if (currentToken is MUL) {
//            next()
//            val y = parseF()
//            return parseT1(x * y)
//        } else if (currentToken is PLUS || currentToken is RBRACKET || currentToken is EndOfFile) {
//            return x
//        } else {
//            error("Unexpected :" + currentToken)
//            return x // to make compiler happy
//        }
//    }
//
//    internal fun parseF(): Int {
//        // F -> ( E ) | a
//        if (currentToken is LBRACKET) {
//            next()
//            val x = parseE()
//            if (currentToken is RBRACKET) {
//                next()
//                return x
//            } else {
//                error(") expected.")
//                return -1
//            }
//        } else {
//            val x = currentToken.value as Int
//            next()
//            return x
//        }
//
//    }
//
//
//}
