package me.aksenov.whidbot.utils

import me.aksenov.whidbot.task.model.TaskStatus
import org.telegram.telegrambots.meta.api.objects.Update

fun Update.isStop(): Boolean = isCommandContain(TaskStatus.STOPPED)
fun Update.isContinue(): Boolean = isCommandContain(TaskStatus.IN_PROGRESS)
fun Update.isGet(): Boolean = with(message ?: callbackQuery.message) { text.startsWith("/get") }

private fun Update.isCommandContain(taskStatus: TaskStatus): Boolean =
    callbackQuery?.data?.startsWith(taskStatus.command) ?: false

fun Long.toHumanHours(): String = if (div(60) > 0) "${this / 60}h ${this % 60}m" else "${this % 60}m"