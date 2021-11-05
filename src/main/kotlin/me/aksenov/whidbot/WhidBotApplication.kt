package me.aksenov.whidbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class WhidBotApplication

fun main(args: Array<String>) {
    runApplication<WhidBotApplication>(*args)
}
