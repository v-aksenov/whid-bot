package me.aksenov.whidbot.task.dao

import me.aksenov.whidbot.task.model.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
interface TaskDao : JpaRepository<Task, Long> {

    fun getAllByMessageAndTelegramId(message: String, telegramId: String): List<Task>

    fun getAllByTelegramId(telegramId: String): List<Task>

    fun getFirstByIdAndTelegramId(id: Long, telegramId: String): Task?

    @Modifying
    @Query("update Task t set t.spentMinutes = :spentMinutes where t.id = :id")
    fun increaseSpentMinutesForTask(@Param("id") id: Long, @Param("spentMinutes") spentMinutes: Int)
}