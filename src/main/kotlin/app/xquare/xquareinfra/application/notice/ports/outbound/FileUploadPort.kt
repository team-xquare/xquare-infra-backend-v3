package app.xquare.xquareinfra.application.notice.ports.outbound

import org.springframework.web.multipart.MultipartFile

interface FileUploadPort {
    fun upload(file: MultipartFile): String
}