package br.com.docosal.services

import br.com.docosal.config.FileStorageConfig
import br.com.docosal.exceptions.FileStorageException
import br.com.docosal.exceptions.MyFileNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Logger

@Service
class FileStorageService @Autowired constructor(fileStorageConfig: FileStorageConfig) {

    private val fileStorageLocation: Path

    private val logger : Logger = Logger.getLogger(FileStorageService::class.java.name)

    init {
        fileStorageLocation = Paths.get(fileStorageConfig.uploadDir).toAbsolutePath().normalize()
        try {
            Files.createDirectory(fileStorageLocation)
        } catch (e: FileAlreadyExistsException) {
            logger.info("Diretorio ja existe. ${fileStorageLocation.toString()}")
        }
        catch (e: Exception){
            throw FileStorageException("ERRO - Could not create the directory where the uploaded file would be stored. ${fileStorageLocation.toString()}", e)
        }
    }

    fun storageFile(file: MultipartFile) : String {
        val fileName = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            //myFile..txt
            if (fileName.contains(".."))
                throw FileStorageException("ERRO - Filename contains invalid path sequence  $fileName.")
            val targetLocation = fileStorageLocation.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            fileName
        } catch (e: Exception){
            throw FileStorageException("ERRO - Could not store file $fileName. Please try again!", e)
        }
    }

    fun loadFileAsResource(fileName: String) : Resource {
        return try {
            val filePath = fileStorageLocation.resolve(fileName).normalize()
            val resource : Resource = UrlResource(filePath.toUri())
            if (resource.exists())
                resource
            else throw MyFileNotFoundException("ERRO - File not found $fileName. Please try again!")
        } catch (e: Exception){
            throw MyFileNotFoundException("ERRO - File not found $fileName. Please try again!", e)
        }
    }
}