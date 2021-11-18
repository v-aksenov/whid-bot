package me.aksenov.whidbot.telegram

import me.aksenov.whidbot.task.TaskService
import me.aksenov.whidbot.task.model.Task
import me.aksenov.whidbot.task.model.TaskStatus
import me.aksenov.whidbot.utils.isContinue
import me.aksenov.whidbot.utils.isGet
import me.aksenov.whidbot.utils.isStop
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
        return when {
            update.isGet() -> handleGet(chatId)
            update.isStop() -> handleStop(update.callbackQuery.data, chatId)
            update.isContinue() -> handleContinue(update.callbackQuery.data, chatId)
            else -> convert(taskService.addTask(message.text, chatId))
        }
    }

    fun getTodayTasks(): List<SendMessage> =
        taskService.getTodayTasks()
            .map { SendMessage(it.key, it.value.joinToString("\n") { task -> task.toMessageBody() }) }

    private fun handleStop(data: String, chatId: String): SendMessage {
        val taskId = data.substringAfter(TaskStatus.STOPPED.command).toLong()
        val stoppedTask = taskService.stopTask(taskId, chatId)
        return if (stoppedTask != null) convert(stoppedTask, TaskStatus.IN_PROGRESS) else getDefaultUnknown(chatId)
    }

    private fun handleContinue(data: String, chatId: String): SendMessage {
        val taskId = data.substringAfter(TaskStatus.IN_PROGRESS.command).toLong()
        val stoppedTask = taskService.continueTask(taskId, chatId)
        return if (stoppedTask != null) convert(stoppedTask, TaskStatus.STOPPED) else getDefaultUnknown(chatId)
    }

    private fun handleGet(chatId: String): SendMessage {
        val todayTasks = taskService.getTodayTasksFor(chatId)
        val message = todayTasks
            .joinToString("________\n") { it.toString() }
            .plus(todayTasks.sumOf { it.spentMinutes })
        return SendMessage(chatId, message)
    }

    private fun convert(task: Task, nextStatus: TaskStatus = TaskStatus.STOPPED): SendMessage =
        SendMessage().apply {
            chatId = task.telegramId!!
            text = task.toMessageBody()
            replyMarkup = InlineKeyboardMarkup(listOf(listOf(
                InlineKeyboardButton().apply {
                    text = nextStatus.text
                    callbackData = "${nextStatus.command}${task.id}"
                }
            )))
        }

    private fun getDefaultUnknown(chatId: String): SendMessage = SendMessage(chatId, "unknown task")
}