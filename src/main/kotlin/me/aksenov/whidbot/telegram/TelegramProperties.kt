package me.aksenov.whidbot.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("bot")
data class TelegramProperties(val username: String, val token: String, val active: Boolean)