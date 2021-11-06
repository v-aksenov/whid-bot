package me.aksenov.whidbot.telegram

import me.aksenov.whidbot.task.TaskService
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class BotService(private val taskService: TaskService) {

    fun processUpdate(update: Update): SendMessage {
        val chatId = update.message.chatId.toString()
        return update.message.run {
            when {
                text.startsWith("/get") -> SendMessage(chatId, taskService.getTasks(text).toString())
                else -> SendMessage(chatId, taskService.addTaskFrom(text, chatId).message!!)
            }
        }
    }
}