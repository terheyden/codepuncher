package com.terheyden.templates

// Here are just a few Groovy idioms that I think are worth knowing.
// First is println() - like System.out.println() - it's built in.
// Why this is useful: quick, easy way to dump info for debug.

println("Hello Groovy!");

// Semicolons are not required in Groovy.
// Methods with a single arg don't need parens.

println "Hello Groovy!"

// In Groovy, asserts are always enabled, and objects are compared by-value
// (that means you can use == everywhere instead of .equals()).
// Asserts and == are great ways to verify state.

assert "Cora" == "Cora"       // Can use single- or double-quotes.
assert "Cora" == "Co" + "ra"  // Though only double-quotes support "$interpolation".

// As in most scripting and high-level languages, Groovy supports "truth-like" values.
// Useful for quickly verifying the existence of objects and variables.

assert 1
assert "Non-zero non-empty objects are true"
assert 'a'
assert ' '

assert ! 0
assert ! []
assert ! ''

// In Groovy, var typing is optional. (Also typing is handled at runtime by Groovy.)

String json1    = '{"name": "Cora", "age": 11}' // Java strong typing.
def json2 = '{"name": "Cora", "age": 11}' // 'def' instead of 'var', like in Java.
json3           = '{"name": "Cora", "age": 11}' // No typing; in scripts this var will go into the Binding.

// Strings — you can use both single- and double-quotes.

def name = 'Cora'
def doubleQuoted = "Only double-quoted strings support interpolation: ${name}."
// There are also multi-line versions of both single- and double-quoted strings:
def multiLine = """\
    This is a multi-line string.
    It's useful for writing JSON, XML, and other text.
    """.stripIndent()

// In Groovy, imports can be placed anywhere.
import groovy.json.JsonSlurper

// How to parse JSON in Groovy:
def slurper = new JsonSlurper()

Map<String, String> map1 = slurper.parseText('{ "name": "Cora" }')
def map2 = slurper.parseText('{ "name": "Mika" }')
map3 = slurper.parseText('{ "name": "Tashi" }')

assert map1.name == 'Cora'
assert map2.name == 'Mika'
assert map3.name == 'Tashi'

// There is one HUGE DIFFERENCE though.
// Only variables defined WITHOUT A TYPE get persisted to the context binding!
// (I.e. accessible outside the script.)
// So in the above example, ONLY 'map3' will be available to Java / other scripts.

// Groovy adds useful methods to everything... one of the most useful for scripting
// is the dump() method, so you can easily see what you're dealing with.

map3.dump()  // Every object gets this method added.

