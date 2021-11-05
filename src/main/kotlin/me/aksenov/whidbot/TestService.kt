package me.aksenov.whidbot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TestService(@Value("\${testProperty}") private val testProperty: String) {

}