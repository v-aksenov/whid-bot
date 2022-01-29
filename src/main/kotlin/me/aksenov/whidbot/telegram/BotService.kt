package me.aksenov.whidbot.telegram

import me.aksenov.whidbot.task.TaskService
import me.aksenov.whidbot.task.model.Task
import me.aksenov.whidbot.task.model.TaskStatus
import me.aksenov.whidbot.utils.isContinue
import me.aksenov.whidbot.utils.isGet
import me.aksenov.whidbot.utils.isStop
import me.aksenov.whidbot.utils.toHumanHours
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
            else -> taskService.addTask(message.text, chatId).toSendMessage()
        }
    }

    fun getTodayTasks(): List<SendMessage> =
        taskService.getTodayTasks().map { SendMessage(it.key, createMultiTaskMessage(it.value)) }

    private fun handleStop(data: String, chatId: String): SendMessage =
        changeTaskStatus(data, chatId, TaskStatus.STOPPED, TaskStatus.IN_PROGRESS)

    private fun handleContinue(data: String, chatId: String): SendMessage =
        changeTaskStatus(data, chatId, TaskStatus.IN_PROGRESS, TaskStatus.STOPPED)

    private fun handleGet(chatId: String): SendMessage =
        SendMessage(
            chatId,
            createMultiTaskMessage(taskService.getTodayTasksFor(chatId))
        )

    private fun changeTaskStatus(data: String, chatId: String, from: TaskStatus, to: TaskStatus): SendMessage {
        val taskId = data.substringAfter(from.command).toLong()
        val stoppedTask = taskService.stopTask(taskId, chatId)
        return stoppedTask?.toSendMessage(to) ?: getMessageWithError(chatId)
    }

    private fun createMultiTaskMessage(tasks: List<Task>): String =
        tasks
            .joinToString(MESSAGE_SEPARATOR) { it.toMessageBodyWithoutStatus() }
            .plus(SUMMARY_SPENT_TEMPLATE.format(tasks.sumOf { it.spentMinutes }.toHumanHours()))

    private fun Task.toSendMessage(nextStatus: TaskStatus = TaskStatus.STOPPED): SendMessage =
        SendMessage().apply {
            chatId = telegramId!!
            text = toMessageBody()
            replyMarkup = InlineKeyboardMarkup(listOf(listOf(
                InlineKeyboardButton().apply {
                    text = nextStatus.text
                    callbackData = "${nextStatus.command}${id}"
                }
            )))
        }

    private fun getMessageWithError(chatId: String): SendMessage = SendMessage(chatId, "unknown task")
}

private const val MESSAGE_SEPARATOR = """
__________


"""

private const val SUMMARY_SPENT_TEMPLATE = """
__________

Summary spent: 
%s
"""