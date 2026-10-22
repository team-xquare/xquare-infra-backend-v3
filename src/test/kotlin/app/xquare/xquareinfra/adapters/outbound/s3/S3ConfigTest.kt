package app.xquare.xquareinfra.adapters.outbound.s3

import software.amazon.awssdk.services.s3.model.GetUrlRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class S3ConfigTest {
    @Test
    fun `custom endpoint with path style builds object url on that endpoint`() {
        val s3Client =
            S3Config(
                accessKey = "access",
                secretKey = "secret",
                region = "ap-northeast-2",
                endpoint = "http://localhost:9000",
                pathStyleAccess = true,
            ).s3Client()

        val url = s3Client.utilities().getUrl(urlRequest()).toExternalForm()

        assertEquals("http://localhost:9000/test-bucket/notice/file.pdf", url)
    }

    @Test
    fun `blank endpoint falls back to aws virtual hosted url`() {
        val s3Client =
            S3Config(
                accessKey = "",
                secretKey = "",
                region = "ap-northeast-2",
                endpoint = "",
                pathStyleAccess = false,
            ).s3Client()

        val url = s3Client.utilities().getUrl(urlRequest()).toExternalForm()

        assertEquals("https://test-bucket.s3.ap-northeast-2.amazonaws.com/notice/file.pdf", url)
    }

    private fun urlRequest(): GetUrlRequest =
        GetUrlRequest
            .builder()
            .bucket("test-bucket")
            .key("notice/file.pdf")
            .build()
}
