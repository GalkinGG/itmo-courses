"use strict"

class ParseError extends Error {
    constructor(message) {
        super(message);
    }
}

class emptyInputError extends ParseError {
    constructor() {
        super("input string is empty");
    }
}

class wrongArgCountError extends ParseError {
    constructor(message) {
        super(message);
    }
}


class unexpectedTokenError extends ParseError {
    constructor(token, index) {
        super("Unexpected token: \'" + token + "\' Pos: " + index);
    }
}

class bracketBalanceError extends ParseError {
    constructor(message) {
        super(message);
    }
}

class unknownOperationError extends ParseError {
    constructor(token, index) {
        super("unknown operation \'" + token + "\' Pos: " + index);
    }
}

class excessiveInfoError extends ParseError {
    constructor(inp) {
        super("Excessive info in string: " + inp);
    }
}

let OPERATIONS = new Map()

const VARIABLES = {"x": 0, "y": 1, "z": 2}

function Const(value) {
    this.evaluate = () => value
    this.toString = () => (String)(value)
    this.prefix = () => (String)(value)
}

function Variable(v) {
    this.toString = () => v
    this.prefix = () => v
    this.evaluate = function() {
        return arguments[VARIABLES[v]]
    }
}

function Operation(name, f, argCount, operation, ...args) {
    OPERATIONS.set(operation, [name, argCount, f])
    this.operation = operation
    this.args = args;
}

function createOperation(f, argCount, operation) {
    const func = function () {
        Operation.call(this, func, f, argCount, operation, ...arguments)
    }
    func.prototype = Object.create(Operation.prototype)
    func.prototype.calc = f
    return func
}

Operation.prototype.evaluate = function(x, y, z) {
    return OPERATIONS.get(this.operation)[2](...this.args.map(element => element.evaluate(x, y, z)))
}

Operation.prototype.toString = function() {
    return this.args.map(element => element.toString()).join(" ") + " " + this.operation
}

Operation.prototype.prefix = function() {
    return "(" + this.operation + " " + this.args.map(element => element.prefix()).join(" ") + ")"
}

const Add = createOperation((a, b) => a + b, 2, "+")
const Subtract = createOperation((a, b) => a - b, 2, "-")
const Multiply = createOperation((a, b) => a * b, 2, "*")
const Divide = createOperation((a, b) => a/b, 2, "/")
const Negate = createOperation(a => -a, 1, "negate")
const Ln = createOperation(Math.log, 1, "ln")
const Exp = createOperation(Math.exp, 1, "exp")
const Sum = createOperation((...args) => args.reduce((res, currentEl) => res + currentEl, 0), Infinity, "sum")
const Avg = createOperation((...args) => (Sum.prototype.calc)(...args)/(args.length), Infinity, "avg")

const parse = input => {
    input = input.split(' ').filter(t => t.length > 0)
    let stack = []
    for (const token of input) {
        if (token in VARIABLES) {
            stack.push(new Variable(token))
        } else if (OPERATIONS.has(token)) {
            stack.push(new (OPERATIONS.get(token)[0])(...stack.splice(-OPERATIONS.get(token)[1], OPERATIONS.get(token)[1])))
        } else {
            stack.push(new Const(parseInt(token)))
        }
    }
    return stack.pop()
}

let BRACKETS = new Set(["(", ")"])

const parsePrefix = input => {
    input = input.replaceAll("(", " ( ")
    input = input.replaceAll(")", " ) ")
    input = input.split(' ').filter(t => t.length > 0);
    if (input.length === 0) {
        throw new emptyInputError()
    }
    let pos = 0
    let stack = []
    let openBrackets = []
    let bracketsBalance = 0
    let opExpected = false
    for (const token of input) {
        if (BRACKETS.has(token)) {
            if (token === '(') {
                opExpected = true
                bracketsBalance++
                stack.push('(')
                openBrackets.push(stack.length-1)
            } else {
                let lastOpenBracket = openBrackets.pop()
                let operation = stack[lastOpenBracket + 1]
                let operands = stack.slice(lastOpenBracket + 2)
                if (!(OPERATIONS.has(operation))) {
                    throw new unknownOperationError(operation, pos)
                }
                if ((operands.length !== OPERATIONS.get(operation)[1]) && OPERATIONS.get(operation)[1] !== Infinity) {
                    throw new wrongArgCountError("Expected " + OPERATIONS.get(operation)[1] +
                        " arguments. Received: " + operands.length + " Pos: " + pos)
                }
                stack = stack.slice(0, lastOpenBracket)
                stack.push(new (OPERATIONS.get(operation)[0])(...operands))
                bracketsBalance--
            }
            if (bracketsBalance < 0) {
                throw new bracketBalanceError("extra \')\' found")
            }
        } else {
            if (OPERATIONS.has(token) && opExpected) {
                opExpected = false
                stack.push(token)
            } else if (token in VARIABLES && !opExpected) {
                stack.push(new Variable(token))
            } else {
                if (isFinite(token) && !opExpected) {
                    stack.push(new Const(parseInt(token)))
                } else {
                    throw new unexpectedTokenError(token, pos)
                }
            }
        }
        pos += token.length
    }
    stack = stack.filter(element => !(element in BRACKETS))
    if (bracketsBalance !== 0) {
        throw new bracketBalanceError("\')\' missing");
    }
    if (stack.length !== 1) {
        throw new excessiveInfoError(input.toString().replaceAll(',', ' '))
    }
    return stack.pop()
}