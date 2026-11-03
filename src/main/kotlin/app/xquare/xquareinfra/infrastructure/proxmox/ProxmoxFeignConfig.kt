package app.xquare.xquareinfra.infrastructure.proxmox

import feign.Feign
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

// @Configuration 금지: 붙이면 전역 설정이 되어 다른 Feign 클라이언트도 Proxmox CA만 신뢰하게 됨
class ProxmoxFeignConfig {
    @Bean
    fun proxmoxFeignBuilder(
        okHttpClient: OkHttpClient,
        proxmoxProperties: ProxmoxProperties,
    ): Feign.Builder {
        val httpClientBuilder = okHttpClient.newBuilder()
        if (proxmoxProperties.caCert.isNotBlank()) {
            val trustManager = trustManagerOf(proxmoxProperties.caCert)
            val sslContext = SSLContext.getInstance("TLS").apply { init(null, arrayOf(trustManager), null) }
            httpClientBuilder.sslSocketFactory(sslContext.socketFactory, trustManager)
        }
        return Feign.builder().client(feign.okhttp.OkHttpClient(httpClientBuilder.build()))
    }

    private fun trustManagerOf(caCertPem: String): X509TrustManager {
        val certificates = CertificateFactory.getInstance("X.509").generateCertificates(caCertPem.byteInputStream())
        require(certificates.isNotEmpty()) { "proxmox.ca-cert 에서 인증서를 읽지 못했습니다." }

        val keyStore =
            KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                certificates.forEachIndexed { index, certificate -> setCertificateEntry("proxmox-ca-$index", certificate) }
            }
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(keyStore) }
        return trustManagerFactory.trustManagers.filterIsInstance<X509TrustManager>().single()
    }
}
