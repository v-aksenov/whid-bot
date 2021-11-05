package me.aksenov.whidbot.task.dao

import me.aksenov.whidbot.task.model.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskDao : JpaRepository<Task, Long>{
}