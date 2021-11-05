package me.aksenov.whidbot.telegram

import me.aksenov.whidbot.utils.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.annotation.PostConstruct

@Service
class Bot(
    @Value("\${bot.token}") private val token: String,
    @Value("\${bot.username}") private val username: String
) : TelegramLongPollingBot(), Logger {

    override fun onUpdateReceived(update: Update) {
        log.info(update.toString())
    }

    override fun getBotUsername(): String = username

    override fun getBotToken(): String = token

    @PostConstruct
    fun startup() {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(this)
        log.info("whid-bot started")
    }
}