package br.com.docosal.data.vo.v1

class UploadFileResponseDTO(
    var fileName: String = "",
    var fileDownloadUri: String = "",
    var fileType: String = "",
    var fileSize: Long = 0

)