package br.com.docosal.integrationstests.controller.withjson

import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerJsonTest : AbstractIntegrationTest() {



}