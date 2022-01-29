package me.aksenov.whidbot.task

import me.aksenov.whidbot.task.dao.TaskDao
import me.aksenov.whidbot.task.model.Task
import me.aksenov.whidbot.task.model.TaskStatus
import me.aksenov.whidbot.utils.Logger
import org.springframework.stereotype.Service
import java.time.Instant.now
import java.time.temporal.ChronoUnit

@Service
class TaskService(private val taskDao: TaskDao) : Logger {

    fun addTask(message: String, telegramId: String): Task {
        taskDao.getFirstByTelegramId(telegramId)?.let { stopTask(it.id!!, telegramId) }
        return taskDao.save(Task(message = message, telegramId = telegramId)).also { log.info("saved: $it") }
    }

    fun getTodayTasksFor(telegramId: String): List<Task> = taskDao.getTodayTasksByTelegramId(telegramId)

    fun stopTask(taskId: Long, telegramId: String): Task? =
        taskDao.getFirstByIdAndTelegramId(taskId, telegramId)?.let {
            val lastSpent = it.spentMinutes + ChronoUnit.MINUTES.between(it.updated.toInstant(), now())
            taskDao.update(it.id!!, lastSpent, TaskStatus.STOPPED)
            taskDao.getById(it.id)
        }

    fun continueTask(taskId: Long, telegramId: String): Task? =
        taskDao.getFirstByIdAndTelegramId(taskId, telegramId)?.let {
            taskDao.update(it.id!!, it.spentMinutes, TaskStatus.IN_PROGRESS)
            taskDao.getById(it.id)
        }

    fun getTodayTasks(): Map<String, List<Task>> = taskDao.getDistinctTelegramIds()
        .associateWith { taskDao.getTodayTasksByTelegramId(it) }
}