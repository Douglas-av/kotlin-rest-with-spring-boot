package br.com.docosal.unittests.mapper

import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.model.Person
import br.com.docosal.unittests.mocks.MockPerson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class DozerMapperTest {
    var inputObject : MockPerson? = null

    @BeforeEach
    fun setUp(){
        inputObject = MockPerson()
    }

    @Test
    fun parseVOToEntityTest() {

        val output: Person = DozerMapper.parseObject(inputObject!!.mockVO(), Person::class.java)

        assertEquals(0, output.id)
        assertEquals("First Name Test 0", output.firstName)
        assertEquals("Last Name Test 0", output.lastName)
        assertEquals("Address Test 0", output.address)
        assertEquals("Male", output.gender)
    }

    @Test
    fun parserVOListToEntityListTest() {

        val outputList: ArrayList<Person> = DozerMapper.parseListObjects(inputObject!!.mockVOList(), Person::class.java)

        val outputZero: Person = outputList[0]
        assertEquals(0, outputZero.id)
        assertEquals("First Name Test 0", outputZero.firstName)
        assertEquals("Last Name Test 0", outputZero.lastName)
        assertEquals("Address Test 0", outputZero.address)
        assertEquals("Male", outputZero.gender)

        val outputSeven: Person = outputList[7]
        assertEquals(7, outputSeven.id)
        assertEquals("First Name Test 7", outputSeven.firstName)
        assertEquals("Last Name Test 7", outputSeven.lastName)
        assertEquals("Address Test 7", outputSeven.address)
        assertEquals("Female", outputSeven.gender)

        val outputTwelve: Person = outputList[12]
        assertEquals(12, outputTwelve.id)
        assertEquals("First Name Test 12", outputTwelve.firstName)
        assertEquals("Last Name Test 12", outputTwelve.lastName)
        assertEquals("Address Test 12", outputTwelve.address)
        assertEquals("Male", outputTwelve.gender)
    }

    @Test
    fun parseEntityToVOTest(){
        val output: PersonVO = DozerMapper.parseObject(inputObject!!.mockEntity(), PersonVO::class.java)
        assertEquals(0, output.key)
        assertEquals("First Name Test 0", output.firstName)
        assertEquals("Last Name Test 0", output.lastName)
        assertEquals("Address Test 0", output.address)
        assertEquals("Male", output.gender)
    }

    @Test
    fun parseEntityListToVOListTest(){
        val outputList: ArrayList<PersonVO> = DozerMapper.parseListObjects(inputObject!!.mockEntityList(), PersonVO::class.java)

        val outputZero: PersonVO = outputList[0]

        assertEquals(0, outputZero.key)
        assertEquals("First Name Test 0", outputZero.firstName)
        assertEquals("Last Name Test 0", outputZero.lastName)
        assertEquals("Address Test 0", outputZero.address)
        assertEquals("Male", outputZero.gender)

        val outputSeven: PersonVO = outputList[7]

        assertEquals(7, outputSeven.key)
        assertEquals("First Name Test 7", outputSeven.firstName)
        assertEquals("Last Name Test 7", outputSeven.lastName)
        assertEquals("Address Test 7", outputSeven.address)
        assertEquals("Female", outputSeven.gender)

        val outputEleven: PersonVO = outputList[11]

        assertEquals(11, outputEleven.key)
        assertEquals("First Name Test 11", outputEleven.firstName)
        assertEquals("Last Name Test 11", outputEleven.lastName)
        assertEquals("Address Test 11", outputEleven.address)
        assertEquals("Female", outputEleven.gender)

    }

}