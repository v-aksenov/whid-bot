package me.aksenov.whidbot.task.model

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Task(
    @Id
    @GeneratedValue
    val id: Int? = null,
    @Column
    val created: Timestamp? = null,
    @Column(name = "spent_minutes")
    val spentMinutes: Int? = null,
    @Column
    val message: String? = null,
    @Column(name = "telegram_id")
    val telegramId: String? = null
)