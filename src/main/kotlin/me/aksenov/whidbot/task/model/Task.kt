package me.aksenov.whidbot.task.model

import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "user_task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column
    val created: Timestamp = Timestamp.from(Instant.now()),
    @Column(name = "spent_minutes")
    val spentMinutes: Int = 0,
    @Column
    val message: String? = null,
    @Column(name = "telegram_id")
    val telegramId: String? = null
)