package earleyParser.parser

import earleyParser.lexer.Grammar
import earleyParser.lexer.NonTerminal
import earleyParser.lexer.Terminal
import earleyParser.lexer.Token
import java.util.*
import kotlin.collections.ArrayList


open class StateE(
        val rule: Rule,
        val dot: Int,
        var isComplited: Boolean,
        val start: Int,
        var node: Node?
) {
    fun equals(state: StateE): Boolean {
        if (this.rule.equals(state.rule) &&
                this.dot == state.dot &&
//                this.isComplited == state.isComplited &&
                this.start == state.start ) {

            if (this.node == null && state.node == null) return true


            if (this.node is IntermideateNode && state.node is IntermideateNode) {
                println("IntermideateNode")
                return (this.node as IntermideateNode).equals(state.node as IntermideateNode)
            } else if (this.node is PacketNode && state.node is PacketNode) {
                if ((this.node as PacketNode).equals(state.node as PacketNode)) {
                    println("PakcetNode true")
                } else {
                    println("PakcetNode false")
                }
                println("PakcetNode")
                return (this.node as PacketNode).equals(state.node as PacketNode)
            } else if (this.node is NonTerminalNode && state.node is NonTerminalNode) {
                println("NonTerminal")
                return (this.node as NonTerminalNode).equals(state.node as NonTerminalNode)
            } else if (this.node is TerminalNode && state.node is TerminalNode) {
                println("Terminal")
                return (this.node as TerminalNode).equals(state.node as TerminalNode)
            } else {

                println("false")
                return false
            }
        }

        return false
    }
}

fun childEquals(childrens1: ArrayList<Node>, childrens2: ArrayList<Node>):Boolean {

    for ((i,child1) in childrens1.withIndex()) {
        if (child1 is IntermideateNode && childrens2[i] is IntermideateNode) {
//            println("IntermideateNode")
            return (child1 as IntermideateNode).equals(childrens2[i] as IntermideateNode)
        } else if (child1 is PacketNode && childrens2[i]is PacketNode) {
            return (child1 as PacketNode).equals(childrens2[i]as PacketNode)
        } else if (child1 is NonTerminalNode && childrens2[i] is NonTerminalNode) {
//            println("NonTerminal")
            return (child1 as NonTerminalNode).equals(childrens2[i]as NonTerminalNode)
        } else if (child1 is TerminalNode && childrens2[i] is TerminalNode) {
//            println("Terminal")
            return (child1 as TerminalNode).equals(childrens2[i] as TerminalNode)
        } else {
//            println("false")
            return false
        }
    }

    return true
}

abstract class Node(open val start: Int,open val end: Int,open val childrens: ArrayList<Node>)

class IntermideateNode(val state: StateE,start: Int, end: Int, childrens: ArrayList<Node>) : Node(start, end, childrens) {
    fun equals(node: IntermideateNode): Boolean {
        if (this.state.equals(node.state) &&
                this.start == node.start ) {
            if (this.childrens.size == node.childrens.size && node.childrens.size ==0) return true
            return childEquals(this.childrens,node.childrens)
        }
        return false
    }
}

class NonTerminalNode(val header: NonTerminal,start: Int, end: Int, childrens: ArrayList<Node>) : Node(start, end, childrens) {
    fun equals(node: NonTerminalNode): Boolean {
        if(this.header.name == node.header.name &&
                this.start == node.start &&
                this.childrens.size == node.childrens.size) {
            if (this.childrens.size == 0) {
                return true
            } else {
                return childEquals(this.childrens,node.childrens)
            }
        }
        return false
    }
}

class TerminalNode(val symbol: String,start: Int, end: Int, childrens: ArrayList<Node>) : Node(start, end, childrens) {
    fun equals(node: TerminalNode): Boolean {
        if (this.symbol == node.symbol &&
                this.start == node.start ) {
            if (this.childrens.size == node.childrens.size && node.childrens.size ==0) return true
            return childEquals(this.childrens,node.childrens)
        }
        return false
    }
}

class PacketNode(start: Int, end: Int, childrens: ArrayList<Node>) : Node(start, end, childrens) {
    fun equals(node: PacketNode): Boolean {
        if (this.start == node.start ) {
            if (this.childrens.size == node.childrens.size && node.childrens.size ==0) return true
            return childEquals(this.childrens,node.childrens)
        }
        return false
    }
}
//Node (x,j,i)
//aj+1 ... ai : substring matched by x
//IntermideateNode : Node (state,j, i,id,childrens<ArrayList<id>>)
//SymbalNode : Node (NonTerminal,j,i,id,childrens<ArrayList<id>>)
//TerminalNode : Node (an,j,i,id,childrens<ArrayList<id>>)
//PacketNode : Node (j,i,id,childrens<ArrayList<id>>)

private var setOfNodesV = arrayListOf<Node>()

fun earleyParser(grammarObject: Grammar, source: LinkedList<Token>) : Node {
    val setOfStates: ArrayList<ArrayList<StateE>> = ArrayList(source.size+1)
    for (i in 1..source.size+1) {
        setOfStates.add(arrayListOf())
    }
    var toRemove = arrayListOf<StateE>()
    var processedQsub = arrayListOf<StateE>()
    setOfNodesV = arrayListOf<Node>()

    for (rule in grammarObject.grammar) {
        if (rule.body.isEmpty() || rule.body[0] is NonTerminal) {
            setOfStates[0].add(StateE(rule,0,false,0,null))
        }
        if (rule.body[0] is Terminal && (rule.body[0] as Terminal).value == source[0].name) {
            processedQsub.add(StateE(rule,0,false,0,null))
        }
    }

    for (i in 0..source.size) {

        val emptyRuleMap: MutableMap<Token,Node> = mutableMapOf()
        if (setOfStates.size != i) {
            toRemove = setOfStates[i]
        } else
            toRemove = arrayListOf()

        var processedQ = processedQsub
        processedQsub = arrayListOf()

        while (!toRemove.isEmpty()) {
            val state = toRemove[toRemove.size - 1]
            toRemove = ArrayList(toRemove.dropLast(1))

            if (state.dot != state.rule.body.size) {

                for (rule in grammarObject.grammar) {
                    if (rule.header.name == state.rule.body[state.dot].name) {
                        if ((rule.body.size == 0 || rule.body[0] is NonTerminal)
                                && !(setOfStates[i].any { it.equals(StateE(rule,0,false,i,null))}) ) {
                            setOfStates[i].add(StateE(rule,0,false,i,null))
                            toRemove.add(StateE(rule,0,false,i,null))
                        }
                        if (rule.body[0] is Terminal && (rule.body[0] as Terminal).value == source[i].name) {
                            processedQ.add(StateE(rule,0,false,i,null))
                        }
                    }
                }

                if (emptyRuleMap.containsKey(state.rule.body[state.dot])) {
                    val localNode = makeNode(StateE(state.rule,state.dot+1,state.isComplited,state.start,state.node)
                            ,i,emptyRuleMap[state.rule.body[state.dot]])
                    if ((state.rule.body.size == state.dot+1 || state.rule.body[state.dot + 1] is NonTerminal) &&
                            !(setOfStates[i].any {it.equals(StateE(state.rule,state.dot+1,state.isComplited,state.start,localNode))})) {
                                setOfStates[i].add(StateE(state.rule,state.dot+1,state.isComplited,state.start,localNode))
                                toRemove.add(StateE(state.rule,state.dot+1,state.isComplited,state.start,localNode))
                            }
                    if (state.rule.body[state.dot+1].name == source[i].name) {
                        processedQ.add(StateE(state.rule,state.dot+1,state.isComplited,state.start,localNode))
                    }

                }

            } else {

                if (state.node == null) {
                    var nonTerminalNode : NonTerminalNode? = null
                    if (!isInSetOfNodes(setOfNodesV,state,i,i,true)) {
                        nonTerminalNode = NonTerminalNode(state.rule.header as NonTerminal,i,i, arrayListOf())
                        setOfNodesV.add(NonTerminalNode(state.rule.header as NonTerminal,i,i, arrayListOf()))
                    }

                    if (nonTerminalNode == null) {
                        state.node = getNodeFromSet(setOfNodesV,state,i,i,true) as NonTerminalNode
                    } else {
                        state.node = nonTerminalNode
                    }
                    (state.node as NonTerminalNode).childrens.add(PacketNode(state.start,i, arrayListOf()))
                }

                if (state.start == i) {
                    val localNode = state.node
                    emptyRuleMap.put(state.rule.header,localNode!!)

                }

                for (localState in setOfStates[state.start]) {
                    val localNode = makeNode(
                            StateE(localState.rule,localState.dot+1,localState.isComplited,localState.start,localState.node),
                            i,state.node)
                    if ((localState.rule.body.size == localState.dot+1 || localState.rule.body[localState.dot+1] is NonTerminal) &&
                            !(setOfStates[i].any {it.equals(StateE(localState.rule,localState.dot+1,localState.isComplited,localState.start,localNode))})) {
                        val stateE = StateE(localState.rule,localState.dot+1,localState.isComplited,localState.start,localNode)
                            setOfStates[i].add(stateE)

//                            if (setOfStates[i][i]
//                                    .equals(StateE(localState.rule,localState.dot+1,localState.isComplited,localState.start,localNode))) {
//                                println("==")
//                            } else {
//                                println("!!")
//                            }



                            toRemove.add(StateE(localState.rule,localState.dot+1,localState.isComplited,localState.start,localNode))
                    }

                    if (localState.rule.body[0] is Terminal && (localState.rule.body[0] as Terminal).value == source[i].name) {
                        processedQ.add(StateE(localState.rule,localState.dot+1,localState.isComplited,localState.start,localNode))
                    }

                }
            }
        }

        setOfNodesV = arrayListOf()



        val terminalNode =if (source.size > i) TerminalNode(source[i].name,i,i+1, arrayListOf()) else TerminalNode("Epsilon",-1,-1, arrayListOf())



        while (!processedQ.isEmpty()) {
            val state = processedQ[processedQ.size-1]
            processedQ = ArrayList(processedQ.dropLast(1))

            val node = makeNode(StateE(state.rule,state.dot+1,state.isComplited,state.start,state.node),i+1,terminalNode)

            if (state.rule.body.size == state.dot+1 || state.rule.body[state.dot+1] is NonTerminal) {
                setOfStates[i+1].add(StateE(state.rule,state.dot+1,state.isComplited,state.start,node))
            }

            if (i+1 < state.rule.body.size && state.rule.body[state.dot+1] is Terminal && (state.rule.body[state.dot+1] as Terminal).value == source[i+1].name) {
                processedQsub.add(StateE(state.rule,state.dot+1,state.isComplited,state.start,node))
            }

        }
    }

    if (isInLanguage(setOfStates)) {
        return getRoot(setOfStates)!!
    }

    throw Exception("Something goes wrong")

}



// j = state.start, i = position, w = state.node, v = node to compare, V = set of Nodes







fun makeNode(state: StateE, position : Int, node: Node?) : Node {
    var localState = state
    if (localState.rule.body.size == localState.dot) {
        localState.isComplited = true
    }
    if (localState.dot == 1 && localState.rule.body.size >= 2) {
        return node!!
    }

    //localState.isComplited
    if (localState.rule.body.size == localState.dot) {
        if (!isInSetOfNodes(setOfNodesV,localState,localState.start,position,false)) {
            setOfNodesV.add(IntermideateNode(localState,localState.start,position, arrayListOf()))
        }

        if (state.node == null) {
            return NonTerminalNode(state.rule.header as NonTerminal,state.start,position,
                    arrayListOf(PacketNode(state.start,position, arrayListOf(node!!))))
        } else {
            return NonTerminalNode(state.rule.header as NonTerminal,state.start,position,
                    arrayListOf(PacketNode(state.start,position, arrayListOf(node!!,state.node!!))))
        }


    } else {
        if (!isInSetOfNodes(setOfNodesV,localState,localState.start,position,true)) {
            setOfNodesV.add(NonTerminalNode(localState.rule.header as NonTerminal, localState.start, position, arrayListOf()))
        }
        if (state.node == null) {
            return IntermideateNode(state,state.start,position,
                    arrayListOf(PacketNode(state.start,position, arrayListOf(node!!))))
        } else {
            return IntermideateNode(state,state.start,position,
                    arrayListOf(PacketNode(state.start,position, arrayListOf(node!!,state.node!!))))
        }


    }

}




fun isInSetOfNodes(setOfNodes: ArrayList<Node>, state: StateE, start: Int,end: Int, checkNonTerminal: Boolean): Boolean {

    for (node in setOfNodes) {
        if (node is NonTerminalNode && checkNonTerminal) {
            if ((node as NonTerminalNode).header.name == state.rule.header.name &&
                    (node as NonTerminalNode).start == start &&
                    node.end == end) return true

        }
        if (node is IntermideateNode && !checkNonTerminal) {
            if ((node as IntermideateNode).state.rule == state.rule &&
                    (node as IntermideateNode).state.dot == state.dot &&
                    (node as IntermideateNode).state.start == start &&
                    node.end == end) return true
        }
    }

    return false

}



fun getNodeFromSet(setOfNodes: ArrayList<Node>, state: StateE, start: Int,end: Int, checkNonTerminal: Boolean): Node {

    for (node in setOfNodes) {
        if (node is NonTerminalNode && checkNonTerminal) {
            if ((node as NonTerminalNode).header.name == state.rule.header.name &&
                    (node as NonTerminalNode).start == start &&
                    node.end == end) return node

        }
        if (node is IntermideateNode && !checkNonTerminal) {
            if ((node as IntermideateNode).state.rule == state.rule &&
                    (node as IntermideateNode).state.dot == state.dot &&
                    (node as IntermideateNode).state.start == start &&
                    node.end == end) return node
        }
    }

    throw IllegalArgumentException("Given set does not have state with given pattern")

}





fun isInLanguage(setOfStates: ArrayList<ArrayList<StateE>>): Boolean {

    for (states in setOfStates) {
        for (state in states) {
            if (state.rule.body.size == state.dot && state.start == 0)
                return true
        }
    }
    return false
}

fun getRoot(setOfStates: ArrayList<ArrayList<StateE>>): Node? {

    for (states in setOfStates) {
        for (state in states) {
            if (state.rule.body.size == state.dot && state.start == 0)
                return state.node
        }
    }
    throw IllegalArgumentException("There is no root")
}












