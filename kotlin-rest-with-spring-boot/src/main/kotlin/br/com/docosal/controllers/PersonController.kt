package br.com.docosal.controllers

import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.exceptions.ExceptionResponse
import br.com.docosal.exceptions.handler.GlobalExceptionHandler
import br.com.docosal.data.vo.v2.PersonVO as PersonVOV2
import br.com.docosal.services.PersonService
import br.com.docosal.util.MediaType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

//@CrossOrigin
@RestController
@RequestMapping("/api/person/v1")
@Tag(name="People", description="Endpoints for Managing People.")
class PersonController {

    @Autowired
    private lateinit var service: PersonService
//  var service: PersonService = PersonService()

    @GetMapping(produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    @Operation(summary = "Finds All People", description = "Finds All People",
        tags = ["People"],
        responses = [
            ApiResponse(description = "Success", responseCode = "200", content = [
                Content(schema = Schema(implementation = PersonVO::class))
            ]),
            ApiResponse(description = "No Content", responseCode = "204", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = ExceptionResponse::class))
            ])
        ]
        )
    fun findAll(): List<PersonVO>{
        return service.findAll()
    }

    @GetMapping(value=["/firstName/{firstName}"], produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    @Operation(summary = "Finds a Person By First Name", description = "Finds a Person By First Name",
        tags = ["People"],
        responses = [
            ApiResponse(description = "Success", responseCode = "200", content = [
                Content(schema = Schema(implementation = PersonVO::class))
            ]),
            ApiResponse(description = "No Content", responseCode = "204", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = GlobalExceptionHandler::class))
            ])
        ]
    )
    fun findAllByFirstName(@PathVariable("firstName") firstName: String): List<PersonVO>{
        return service.findAllByFirstName(firstName)
    }

    @CrossOrigin(origins = ["http://localhost:8080"])
    @Operation(summary = "Finds a Person By ID", description = "Finds a Person By ID",
        tags = ["People"],
        responses = [
            ApiResponse(description = "Success", responseCode = "200", content = [
                Content(schema = Schema(implementation = PersonVO::class))
            ]),
            ApiResponse(description = "No Content", responseCode = "204", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = GlobalExceptionHandler::class))
            ])
        ]
    )
    @GetMapping(value=["/{id}"], produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun findById(@PathVariable(value="id") id: Long): PersonVO{
        return service.findById(id)
    }

    @CrossOrigin(origins = ["http://localhost:8080"])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    @Operation(summary = "Adds a new Person", description = "Adds a new Person",
        tags = ["People"],
        responses = [
            ApiResponse(description = "Success", responseCode = "200", content = [
                Content(schema = Schema(implementation = PersonVO::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = GlobalExceptionHandler::class))
            ])
        ]
    )
    fun create(@RequestBody personVO: PersonVO): PersonVO{
        return service.create(personVO)
    }


    @PostMapping(value=["/v2"],consumes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    @Operation(summary = "Adds a new Person V2", description = "Adds a new Person V2",
        tags = ["People"],
        responses = [
            ApiResponse(description = "Success", responseCode = "200", content = [
                Content(schema = Schema(implementation = PersonVO::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = GlobalExceptionHandler::class))
            ])
        ]
    )
    fun createV2(@RequestBody personVOV2: PersonVOV2): PersonVOV2{
        return service.createV2(personVOV2)
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    @Operation(summary = "Updates a person`s information", description = "Updates a person`s information",
        tags = ["People"],
        responses = [
            ApiResponse(description = "Success", responseCode = "200", content = [
                Content(schema = Schema(implementation = PersonVO::class))
            ]),
            ApiResponse(description = "No Content", responseCode = "204", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = GlobalExceptionHandler::class))
            ])
        ]
    )
    fun update(@RequestBody personVO: PersonVO): PersonVO{
        return service.update(personVO)
    }

    @DeleteMapping(value=["/{id}"], produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    @Operation(summary = "Deletes a Person", description = "Deletes a Person",
        tags = ["People"],
        responses = [
            ApiResponse(description = "No Content", responseCode = "204", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Bad Request", responseCode = "400", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Unauthorized", responseCode = "401", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Not Found", responseCode = "404", content = [
                Content(schema = Schema(implementation = Unit::class))
            ]),
            ApiResponse(description = "Internal Error", responseCode = "500", content = [
                Content(schema = Schema(implementation = Unit::class))
            ])
        ]
    )
    fun delete(@PathVariable(value="id") id: Long): ResponseEntity<*>{
        service.delete(id)
        return ResponseEntity.noContent().build<Any>()

    }

}