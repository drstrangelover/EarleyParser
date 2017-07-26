package regexp

import java.util.*
import kotlin.collections.ArrayList


internal class NFA(postfix: ArrayList<Token>) {
    var initialState: State


    init {
        var catenationState: CatenationState
        var splitState: SplitState
        var finalStates: MutableList<State>
        var fragment: Fragment
        var fragment1: Fragment
        var fragment2: Fragment
        val stack = Stack<Fragment>()
        for (token in postfix) {
            when (token) {
                is OperatorToken -> {
                    when(token.value) {
                        '?' -> {
                            fragment = stack.pop()
                            splitState = SplitState()
                            splitState.nextState1 = fragment.startState
                            finalStates = LinkedList<State>()
                            finalStates.add(splitState)
                            finalStates.addAll(fragment.finishStates)
                            stack.push(Fragment(splitState, finalStates))
                        }
                        '*' -> {
                            fragment = stack.pop()
                            splitState = SplitState()
                            fragment.attach(splitState)
                            splitState.nextState1 = fragment.startState
                            finalStates = LinkedList<State>()
                            finalStates.add(splitState)
                            stack.push(Fragment(splitState, finalStates))
                        }
                        '+' -> {
                            fragment = stack.pop()
                            splitState = SplitState()
                            fragment.attach(splitState)
                            splitState.nextState1 = fragment.startState
                            finalStates = LinkedList<State>()
                            finalStates.add(splitState)
                            stack.push(Fragment(splitState.nextState1, finalStates))
                        }
                        'c' -> {
                            fragment2 = stack.pop()
                            fragment1 = stack.pop()
                            stack.push(fragment1.patch(fragment2))
                        }
                        '|' -> {
                            fragment2 = stack.pop()
                            fragment1 = stack.pop()
                            splitState = SplitState()
                            splitState.nextState1 = fragment1.startState
                            splitState.nextState2 = fragment2.startState
                            finalStates = LinkedList<State>()
                            finalStates.addAll(fragment1.finishStates)
                            finalStates.addAll(fragment2.finishStates)
                            stack.push(Fragment(splitState, finalStates))
                        }
                    }

                }

                is OperandToken -> {
                    catenationState = CatenationState(token)
                    finalStates = LinkedList<State>()
                    finalStates.add(catenationState)
                    stack.push(Fragment(catenationState, finalStates))
                }
            }
        }
        fragment = stack.pop()
        fragment.attach(AcceptableState())
        initialState = fragment.startState
    }

    fun match(input: String): Boolean {
        var currentStates: MutableList<State> = LinkedList()
        initialState.addTo(currentStates)
        for (inputChar in input.toCharArray()) {
            val nextStates = LinkedList<State>()
            for (state in currentStates) {
                state.transaction(inputChar, nextStates)
            }
            currentStates = nextStates
        }
        return currentStates.any { it is AcceptableState }
    }
}
