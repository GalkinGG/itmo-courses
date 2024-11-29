"use strict"

const VARIABLES = {"x": 0, "y": 1, "z": 2}

const cnst = a => () => a
const one = cnst(1)
const two = cnst(2)

const variable = v => (...args) => args[VARIABLES[v]]

const evaluate = f => (...args) => (x, y, z) => f(...args.map(elements => elements(x, y, z)))

const add =  evaluate((a, b) => a + b)
const subtract = evaluate((a, b) => a - b)
const multiply = evaluate((a, b) => a * b)
const divide = evaluate((a, b) =>  a / b)
const negate = evaluate(a => -a);
const sin = evaluate(Math.sin)
const cos = evaluate(Math.cos)

const expression = add(subtract(multiply(variable("x"), variable("x")), multiply(cnst(2),variable("x"))), cnst(1));

for (let x = 0; x < 11; x++) {
    console.log(expression(x, 0, 0));
}





