package com.example.other

import java.io.File

val words = readAllWordsList("resources/words.txt")

fun readAllWordsList(fileName: String): List<String> {
    val inputStream = File(fileName).inputStream()
    val words = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { words.add(it) }

    return words
}

fun getRandomWords(amount: Int = 3): List<String> {
    var currentAmount = 0
    val result = mutableListOf<String>()

    while (currentAmount < amount) {
        val word = words.random()
        if (!result.contains(word)) {
            result.add(word)
            currentAmount++
        }
    }

    return result
}

fun String.transformToUnderscore() =
    toCharArray().map {
        if (it != ' ') '_' else ' '
    }.joinToString(" ")