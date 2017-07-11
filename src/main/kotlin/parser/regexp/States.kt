package regexp


internal interface  State {
    fun addTo(states: MutableList<State>)
    fun transaction(inputChar: Char, states: MutableList<State>)
    fun setNextState(state: State)
}


internal class AcceptableState : State {
    override fun addTo(states: MutableList<State>) {
        states.add(this)
    }

    override fun transaction(inputChar: Char, states: MutableList<State>) {}
    override fun setNextState(state: State) {}
}




internal class CatenationState(var regexToken: Token ) : State {
    lateinit var nextState1: State


    override fun addTo(states: MutableList<State>) {
        states.add(this)
    }

    override fun transaction(inputChar: Char, states: MutableList<State>) {
        if ((regexToken is ExceptOperandToken && regexToken.value != inputChar)
               || regexToken is AnyOperandToken
                || (regexToken.value == inputChar && regexToken !is ExceptOperandToken)) {
            nextState1.addTo(states)
        }

    }

    override fun setNextState(state: State) {
        nextState1 = state
    }
}



internal class SplitState : State {
    lateinit var nextState1: State
    lateinit var nextState2: State


    override fun addTo(states: MutableList<State>) {
        nextState1.addTo(states)
        nextState2.addTo(states)
    }

    override fun transaction(inputChar: Char, states: MutableList<State>) {
        throw IllegalArgumentException("Illegal regExp") as Throwable
    }

    override fun setNextState(state: State) {
        nextState2 = state
    }
}



internal class Fragment(var startState: State, var finishStates: List<State>) {
    fun attach(state: State) {
        for (s in finishStates) {
            s.setNextState(state)
        }
    }

    fun patch(fragment: Fragment): Fragment {
        attach(fragment.startState)
        return Fragment(startState, fragment.finishStates)
    }
}
