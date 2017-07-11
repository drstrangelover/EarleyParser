package regexp


abstract class Token(open val value : Char)

class OperatorToken(override  val value: Char): Token(value)

open class OperandToken(override open val value: Char): Token(value)

class AnyOperandToken(override val value: Char): OperandToken(value)

class ExceptOperandToken(override val value: Char): OperandToken(value)
