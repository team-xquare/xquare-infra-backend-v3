package app.xquare.xquareinfra.adapters.outbound.s3

import app.xquare.xquareinfra.application.notice.ports.outbound.FileUploadPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Component
class S3FileUploadAdapter(
    private val s3Client: S3Client,
    @Value("\${s3.bucket}") private val bucket: String,
    @Value("\${s3.region}") private val region: String,
) : FileUploadPort {
    override fun upload(file: MultipartFile): String {
        val key = "notice/${UUID.randomUUID()}-${file.originalFilename}"

        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(request, RequestBody.fromBytes(file.bytes))

        return "https://$bucket.s3.$region.amazonaws.com/$key"
    }
}
