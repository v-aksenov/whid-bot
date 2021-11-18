package me.aksenov.whidbot.telegram

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.aksenov.whidbot.task.dao.TaskDao
import me.aksenov.whidbot.task.model.TaskStatus
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
                    task.started shouldNotBe null
                    task.spentMinutes shouldBe 0
                    task.telegramId shouldBe update.message.chatId.toString()
                }
            }
        }
        test("task stop") {
            botService.processUpdate(update)

            val id = taskDao.getAllByMessageAndTelegramId(update.message.text, update.message.chatId.toString())
                .first().id!!
            botService.processUpdate(updateStop(id))

            taskDao.getById(id) should {
                it.updated shouldNotBe it.started
                it.status shouldBe TaskStatus.STOPPED
            }
        }
        test("task continue") {
            botService.processUpdate(update)

            val id = taskDao.getAllByMessageAndTelegramId(update.message.text, update.message.chatId.toString())
                .first().id!!
            botService.processUpdate(updateStop(id))
            botService.processUpdate(updateContinue(id))

            taskDao.getById(id) should {
                it.updated shouldNotBe it.started
                it.status shouldBe TaskStatus.IN_PROGRESS
            }
        }
        test("get today tasks") {
            botService.processUpdate(update)

            botService.getTodayTasks() should {
                it.size shouldBe 1
                it.first() should { task ->
                    task.chatId shouldBe update.message.chatId.toString()
                    task.text.contains("test text old boy") shouldBe true
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

    private fun updateStop(taskId: Long) = Update().apply {
        callbackQuery = CallbackQuery().apply {
            data = "${TaskStatus.STOPPED.command}$taskId"
            message = Message().apply {
                text = "lol"
                chat = Chat(111, "test chat")
            }
        }
    }

    private fun updateContinue(taskId: Long) = Update().apply {
        callbackQuery = CallbackQuery().apply {
            data = "${TaskStatus.IN_PROGRESS.command}$taskId"
            message = Message().apply {
                text = "lol"
                chat = Chat(111, "test chat")
            }
        }
    }
}