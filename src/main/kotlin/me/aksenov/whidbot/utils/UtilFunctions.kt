package me.aksenov.whidbot.utils

import me.aksenov.whidbot.task.model.TaskStatus
import org.telegram.telegrambots.meta.api.objects.Update

fun Update.isStop(): Boolean = callbackQuery?.data?.startsWith(TaskStatus.STOPPED.command) ?: false
fun Update.isContinue(): Boolean = callbackQuery?.data?.startsWith(TaskStatus.IN_PROGRESS.command) ?: false
fun Update.isGet(): Boolean = with(message ?: callbackQuery.message) { text.startsWith("/get") }

fun Long.toHumanHours(): String = if (div(60) > 0) "${this / 60}h ${this % 60}m" else "${this % 60}m"