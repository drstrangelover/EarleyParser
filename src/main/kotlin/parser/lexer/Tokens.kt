package parser.lexer

open class Token(open val name: String,
                 open val indexOfLine : Int,
                 open val indexOfWord: Int)



open class Terminal(override val name: String,
               open val value : Any,
               override val indexOfLine: Int,
               override val indexOfWord: Int)
    : Token(name, indexOfLine , indexOfWord)



class NonTerminal(override val name: String,
                  override val indexOfLine: Int,
                  override val indexOfWord: Int)
    : Token(name, indexOfLine , indexOfWord)




class NoSuchToken(override val name: String,
                  override val indexOfLine: Int,
                  override val indexOfWord: Int)
    : Token(name, indexOfLine, indexOfWord)



class EndOfFile(override val name: String,
                override val indexOfLine: Int,
                override val indexOfWord: Int)
    : Token(name, indexOfLine, indexOfWord)


class MetaToken(override val name: String,
                override val value: ArrayList<Terminal>,
                override val indexOfLine: Int,
                override val indexOfWord: Int)
    : Terminal(name, value, indexOfLine, indexOfWord)