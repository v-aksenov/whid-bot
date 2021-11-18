package me.aksenov.whidbot.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UtilFunctionsKtTest : FunSpec() {

    init {
        test("toHumanHours") {
            120L.toHumanHours() shouldBe "2h 0m"
            10L.toHumanHours() shouldBe "10m"
            77L.toHumanHours() shouldBe "1h 17m"
        }
    }

}