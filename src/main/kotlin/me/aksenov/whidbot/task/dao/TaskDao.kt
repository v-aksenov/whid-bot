package me.aksenov.whidbot.task.dao

import me.aksenov.whidbot.task.model.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskDao : JpaRepository<Task, Long> {

    fun deleteAllByMessageAndTelegramId(message: String, telegramId: String)

    fun getAllByMessageAndTelegramId(message: String, telegramId: String): List<Task>
}