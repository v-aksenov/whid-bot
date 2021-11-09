package me.aksenov.whidbot.telegram

import me.aksenov.whidbot.task.TaskService
import me.aksenov.whidbot.task.model.Task
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Service
class BotService(private val taskService: TaskService) {

    fun processUpdate(update: Update): SendMessage {
        val message = update.message ?: update.callbackQuery.message
        val chatId = message.chatId.toString()
        return message.run {
            when {
                text?.startsWith("/get") ?: false -> SendMessage(chatId, taskService.getTasks(chatId).toString())
                update.callbackQuery?.data?.startsWith("/increase ") ?: false ->
                    taskService.increaseTaskMinutes(
                        update.callbackQuery.data.substringAfter("/increase ").toLong(), chatId
                    )
                        ?.let { convert(it) } ?: SendMessage(chatId, "unknown task")
                else -> convert(taskService.addTask(text, chatId))
            }
        }
    }

    private fun convert(task: Task): SendMessage =
        SendMessage().apply {
            chatId = task.telegramId!!
            text = task.message!!.substringBefore("\n\n[minutes spent ")
                .let { "$it\n\n[minutes spent ${task.spentMinutes}]" }
            replyMarkup = InlineKeyboardMarkup(listOf(listOf(
                InlineKeyboardButton().apply {
                    text = "+ 15 min"
                    callbackData = "/increase ${task.id}"
                }
            )))
        }
}