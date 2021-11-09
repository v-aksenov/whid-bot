package me.aksenov.whidbot.task

import me.aksenov.whidbot.task.dao.TaskDao
import me.aksenov.whidbot.task.model.Task
import me.aksenov.whidbot.utils.Logger
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskDao: TaskDao) : Logger {

    fun addTask(message: String, telegramId: String): Task =
        taskDao.save(Task(message = message, telegramId = telegramId)).also { log.info("saved: $it") }

    fun getTasks(telegramId: String): List<Task> = taskDao.getAllByTelegramId(telegramId)

    fun increaseTaskMinutes(taskId: Long, telegramId: String): Task? =
        taskDao.getFirstByIdAndTelegramId(taskId, telegramId)?.let {
            taskDao.increaseSpentMinutesForTask(it.id!!, it.spentMinutes + MINUTES_INCREASE)
            taskDao.getById(it.id)
        }

    fun getTodayTasks(): Map<String, List<Task>> = taskDao.getDistinctTelegramIds()
        .associateWith { taskDao.getTasksByTelegramIdAndCreated(it) }
}

private const val MINUTES_INCREASE = 15