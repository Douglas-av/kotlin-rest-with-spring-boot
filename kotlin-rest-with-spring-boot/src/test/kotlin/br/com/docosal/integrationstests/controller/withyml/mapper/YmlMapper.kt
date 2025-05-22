package br.com.docosal.integrationstests.controller.withyml.mapper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.restassured.mapper.ObjectMapper
import io.restassured.mapper.ObjectMapperDeserializationContext
import io.restassured.mapper.ObjectMapperSerializationContext
import org.apache.logging.log4j.LogManager
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException
import org.apache.logging.log4j.Logger
import com.fasterxml.jackson.databind.ObjectMapper as JacksonObjectMapper

class YMLMapper : ObjectMapper{

    private val objectMapper: JacksonObjectMapper = JacksonObjectMapper(YAMLFactory())
    private val typeFactory: TypeFactory = TypeFactory.defaultInstance()

    private val logger : Logger = LogManager.getLogger(YMLMapper::class.java.name)

    init {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    override fun deserialize(context: ObjectMapperDeserializationContext): Any? {
        try {
            val dataToSerialize = context.dataToDeserialize.asString()
            val type = context.type as Class<*>
            logger.info("Trying deserialize object of type $type")
            return objectMapper.readValue(dataToSerialize, typeFactory.constructType(type))
        } catch (e: JsonMappingException) {
            logger.error("Error deserializing object")
            e.printStackTrace()
        } catch (e: JsonProcessingException) {
            logger.error("Error deserializing object")
            e.printStackTrace()
        }
        return null
    }

    override fun serialize(context: ObjectMapperSerializationContext): Any? {
        try {
            logger.info("Trying serialize object ${context.objectToSerialize}")
            return objectMapper.writeValueAsString(context.objectToSerialize)
        } catch (e: JsonProcessingException) {
            logger.error("Error serializing object")
            e.printStackTrace()
        }
        return null
    }
}