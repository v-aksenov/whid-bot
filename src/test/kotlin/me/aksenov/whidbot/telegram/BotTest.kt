package me.aksenov.whidbot.telegram

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.aksenov.whidbot.task.dao.TaskDao
import org.springframework.boot.test.context.SpringBootTest
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@SpringBootTest
class BotTest(private val bot: Bot, private val taskDao: TaskDao) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("bot should create task") {
            bot.onUpdateReceived(update)

            taskDao.getAllByMessageAndTelegramId(update.message.text, update.message.chatId.toString()) should {
                it.size shouldBe 1
                it.first() should { task ->
                    task.message shouldBe update.message.text
                    task.id shouldNotBe null
                    task.created shouldNotBe null
                    task.spentMinutes shouldBe 0
                    task.telegramId shouldBe update.message.chatId.toString()
                }
            }
        }
    }

    private val update = Update().apply {
        message = Message().apply {
            text = "test text old boy"
            chat = Chat(111, "test chat")
        }
    }
}