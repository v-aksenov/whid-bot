package me.aksenov.whidbot.telegram

import me.aksenov.whidbot.task.TaskService
import me.aksenov.whidbot.utils.Logger
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.annotation.PostConstruct

@Service
class Bot(
    private val telegramProperties: TelegramProperties,
    private val taskService: TaskService
) : TelegramLongPollingBot(), Logger {

    override fun onUpdateReceived(update: Update) {
        update.message.run {
            when {
                text.startsWith("/get") -> chatId.toString().let { send(it, taskService.getTasks(it).toString()) }
                else -> taskService.addTaskFrom(text, chatId.toString())
            }
        }
    }

    private fun send(to: String, text: String) {
        sendApiMethod(SendMessage(to, text))
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