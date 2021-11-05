package me.aksenov.whidbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WhidBotApplication

fun main(args: Array<String>) {
    runApplication<WhidBotApplication>(*args)
}
