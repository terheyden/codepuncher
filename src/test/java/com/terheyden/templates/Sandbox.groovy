package com.terheyden.templates

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

////////////////////////////////////////
// STRINGS

def name = "Cora"

// Multi-line strings in Groovy are slightly different:

def userJson= """\
    {
      "name": "Cora",
      "age": 5
    }""".stripIndent()

def isTrue = "true".toBoolean()
assert isTrue

// Output the var's type.

////////////////////////////////////////
// Let's make some maps (LinkedHashMaps).

def emptyMap = [:]
def codes = [ 1: 'one', 2: 'two', 3: 'three' ]
println "$codes.dump()"

////////////////////////////////////////
// DATE TIME

DateTimeFormatter usDate = DateTimeFormatter.ofPattern("M/d/yyyy");
DateTimeFormatter usDateTime = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

// What time is it in Tokyo?
def tokyo1 = ZonedDateTime.now().withZoneSameInstant(java.time.ZoneId.of("Asia/Tokyo"))
def tokyo2 = ZonedDateTime.now(java.time.ZoneId.of("Asia/Tokyo"))
println "Tokyo time: ${usDateTime.format(tokyo1)}"
println "Tokyo time: ${usDateTime.format(tokyo2)}"

println "UTC right now: ${ZonedDateTime.now(java.time.ZoneId.of("UTC"))}"
println "UTC right now: ${usDateTime.format(ZonedDateTime.now(java.time.ZoneId.of("UTC")))}"

// Let's say we have a UTC time from the DB.
def utc8pm = "2022-02-22T20:00:00Z[UTC]"
def utcTime = ZonedDateTime.parse(utc8pm)

// And we read in their time zone is PST.
def timeZone = "America/Los_Angeles"

// Let's convert the UTC time to their local time.
def zoneId = java.time.ZoneId.of(timeZone)
def localTime = utcTime.withZoneSameInstant(zoneId)

println "UTC time: ${usDateTime.format(utcTime)}"
println "Local time: ${usDateTime.format(localTime)}"

date = ZonedDateTime.now().toString()
println date
ZonedDateTime dateTime = ZonedDateTime.parse(date).plusHours(3)
println dateTime

////////////////////////////////////////
// Let's work with JSON.

// In Groovy, imports can be placed anywhere.

def slurper = new JsonSlurper()
def userMap = slurper.parseText(userJson)
// parseText() parses into a Map (LazyMap).
assert userMap.name == 'Cora'
assert userMap.age == 5

def userJson2 = JsonOutput.toJson(codes)
println userJson2

////////////////////////////////////////
// METHODS

// Groovy methods always return something.
// If you don't specify a return value, it returns the last statement.

def greet(greeting = 'Hello', name) {
    println("$greeting, $name!")
}

greet("Cora")
greet('Wassup', 'Tashi')

println(
    ["one": 1, "two": 2, "three": 3]["two"]
)
