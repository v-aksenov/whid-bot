package me.aksenov.whidbot.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import me.aksenov.whidbot.utils.Logger
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.annotation.PostConstruct

@Service
class Bot(
    private val telegramProperties: TelegramProperties,
    private val botService: BotService,
    private val objectMapper: ObjectMapper
) : TelegramLongPollingBot(), Logger {

    override fun onUpdateReceived(update: Update) {
        log.info(objectMapper.writeValueAsString(update))
        sendApiMethod(botService.processUpdate(update))
    }

    override fun getBotUsername(): String = telegramProperties.username

    override fun getBotToken(): String = telegramProperties.token

    @PostConstruct
    fun startup() {
        if (telegramProperties.active) {
            TelegramBotsApi(DefaultBotSession::class.java).registerBot(this)
            log.info("whid-bot started")
        } else {
            log.info("bot is not active")
        }

    }
}