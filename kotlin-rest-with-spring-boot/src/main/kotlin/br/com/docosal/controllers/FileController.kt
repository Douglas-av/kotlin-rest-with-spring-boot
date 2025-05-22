package br.com.docosal.controllers

import br.com.docosal.data.vo.v1.UploadFileResponseDTO
import br.com.docosal.services.FileStorageService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.apache.logging.log4j.Logger

@Tag(name = "File Endpoint")
@RestController
@RequestMapping("/api/file/v1")
class FileController {

    private val logger: Logger = LogManager.getLogger(FileController::class.java.name)

    @Autowired
    private lateinit var fileStorageService: FileStorageService


    @PostMapping("/uploadFile")
    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileResponseDTO {
        val fileName = fileStorageService.storageFile(file)
        var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/file/v1/uploadFile/")
            .path(fileName)
            .toUriString()
        return UploadFileResponseDTO(fileName, fileDownloadUri, file.contentType!!, file.size)
    }

    @PostMapping("/uploadMultipleFile")
    fun uploadMultipleFile(@RequestParam("files") files: ArrayList<MultipartFile>): List<UploadFileResponseDTO> {
        val uploadFileResponseDTOs = ArrayList<UploadFileResponseDTO>()

        for (file in files) {
            var uploadFileResponseDTO: UploadFileResponseDTO = uploadFile(file)
            uploadFileResponseDTOs.add(uploadFileResponseDTO)
        }

        return uploadFileResponseDTOs
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {
        val resource = fileStorageService.loadFileAsResource(fileName)
        var contentType = ""
        try {
            contentType = request.servletContext.getMimeType(resource.file.absolutePath)
        } catch (e: Exception) {
            logger.info("Could not determine file type.")
        }
        if (contentType.isBlank()) contentType = "application/octect-stream"

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, """attachment; filename="${resource.filename}"""")
            .body(resource)

    }

}