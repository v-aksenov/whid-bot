package me.aksenov.whidbot.task

import me.aksenov.whidbot.task.dao.TaskDao
import me.aksenov.whidbot.task.model.Task
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskDao: TaskDao) {

    fun addTaskFrom(message: String, telegramId: String) {
        taskDao.save(Task(message = message, telegramId = telegramId))
    }
}