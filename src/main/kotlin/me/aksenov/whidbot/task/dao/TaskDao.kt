package me.aksenov.whidbot.task.dao

import me.aksenov.whidbot.task.model.Task
import me.aksenov.whidbot.task.model.TaskStatus
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

    fun getFirstByIdAndTelegramId(id: Long, telegramId: String): Task?

    fun getFirstByTelegramId(telegramId: String): Task?

    @Modifying
    @Query("update Task t set t.spentMinutes = :spentMinutes, t.updated = CURRENT_TIMESTAMP, t.status = :status where t.id = :id")
    fun update(@Param("id") id: Long, @Param("spentMinutes") spentMinutes: Long, @Param("status") status: TaskStatus)

    @Query("select t from Task t where t.telegramId = :telegramId and t.started > DATEADD('DAY',-1, CURRENT_TIMESTAMP)")
    fun getTodayTasksByTelegramId(@Param("telegramId") telegramId: String): List<Task>

    @Query("select distinct t.telegramId from Task t")
    fun getDistinctTelegramIds(): List<String>
}