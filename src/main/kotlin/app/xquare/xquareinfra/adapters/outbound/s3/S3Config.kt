package app.xquare.xquareinfra.adapters.outbound.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration // 추가
class S3Config(
    @Value("\${s3.access-key}") private val accessKey: String,
    @Value("\${s3.secret-key}") private val secretKey: String,
    @Value("\${s3.region}") private val region: String,
) {
    @Bean
    fun s3Client(): S3Client =
        S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                if (accessKey.isNotBlank() && secretKey.isNotBlank()) {
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey),
                    )
                } else {
                    DefaultCredentialsProvider.create()
                },
            )
            .build()
}