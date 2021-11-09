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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@SpringBootTest
class BotTest(private val botService: BotService, private val taskDao: TaskDao) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    override fun afterTest(testCase: TestCase, result: TestResult) {
        taskDao.deleteAll()
    }

    init {
        test("bot should create task") {
            botService.processUpdate(update)

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
        test("increase task spent time") {
            botService.processUpdate(update)

            val id = taskDao.getAllByMessageAndTelegramId(update.message.text, update.message.chatId.toString())
                .first().id!!
            botService.processUpdate(updateIncreaseTime(id))

            taskDao.getById(id).spentMinutes shouldBe 15
        }
    }

    private val update = Update().apply {
        message = Message().apply {
            text = "test text old boy"
            chat = Chat(111, "test chat")
        }
    }

    private fun updateIncreaseTime(taskId: Long) = Update().apply {
        callbackQuery = CallbackQuery().apply {
            data = "/increase $taskId"
            message = Message().apply {
                text = "lol"
                chat = Chat(111, "test chat")
            }
        }
    }
}