package app.xquare.xquareinfra.infrastructure.proxmox

import com.sun.net.httpserver.HttpsConfigurator
import com.sun.net.httpserver.HttpsServer
import feign.RetryableException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.net.InetSocketAddress
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLHandshakeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

object TestProxmoxServer {
    val url: String by lazy { start() }
    val caCert: String by lazy { ClassPathResource("proxmox/test-ca.pem").getContentAsString(Charsets.UTF_8) }

    private fun start(): String {
        val keyStore =
            KeyStore.getInstance("PKCS12").apply {
                ClassPathResource("proxmox/test-server.p12").inputStream.use { load(it, "changeit".toCharArray()) }
            }
        val keyManagerFactory =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply { init(keyStore, "changeit".toCharArray()) }
        val sslContext = SSLContext.getInstance("TLS").apply { init(keyManagerFactory.keyManagers, null, null) }

        val server = HttpsServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.httpsConfigurator = HttpsConfigurator(sslContext)
        server.createContext("/api2/json/version") { exchange ->
            val body = """{"data":{"version":"8.0.0","release":"8.0","repoid":"test"}}""".toByteArray()
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()
        return "https://localhost:${server.address.port}"
    }
}

@SpringBootTest
@ActiveProfiles("test")
class ProxmoxClientWithCaTest {
    @Autowired
    lateinit var proxmoxClient: ProxmoxClient

    @Test
    fun `ca-cert 가 있으면 자체 서명 인증서 서버에 연결된다`() {
        assertEquals("8.0.0", proxmoxClient.getVersion("PVEAPIToken=test").data.version)
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("proxmox.url") { TestProxmoxServer.url }
            registry.add("proxmox.ca-cert") { TestProxmoxServer.caCert }
        }
    }
}

@SpringBootTest
@ActiveProfiles("test")
class ProxmoxClientWithoutCaTest {
    @Autowired
    lateinit var proxmoxClient: ProxmoxClient

    @Test
    fun `ca-cert 가 없으면 자체 서명 인증서 서버에 연결되지 않는다`() {
        val exception = assertFailsWith<RetryableException> { proxmoxClient.getVersion("PVEAPIToken=test") }
        assertIs<SSLHandshakeException>(exception.cause)
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("proxmox.url") { TestProxmoxServer.url }
            registry.add("proxmox.ca-cert") { "" }
        }
    }
}
