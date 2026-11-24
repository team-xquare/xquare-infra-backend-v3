package app.xquare.xquareinfra.infrastructure.proxmox

import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyStore
import java.util.Base64
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

/** A throwaway loopback identity for this test JVM; no repository keystores or trust overrides. */
internal object PlacementTestTls {
    val context: SSLContext
    val certificate: String

    init {
        val password = UUID.randomUUID().toString()
        val dir = Files.createTempDirectory("placement-tls-")
        val store = dir.resolve("loopback.p12")
        val keys = KeyStore.getInstance("PKCS12")
        try {
            val generator =
                ProcessBuilder(
                    Path.of(System.getProperty("java.home"), "bin", "keytool").toString(),
                    "-genkeypair",
                    "-alias",
                    "loopback",
                    "-keyalg",
                    "EC",
                    "-groupname",
                    "secp256r1",
                    "-storetype",
                    "PKCS12",
                    "-keystore",
                    store.toString(),
                    "-storepass",
                    password,
                    "-dname",
                    "CN=localhost",
                    "-ext",
                    "SAN=ip:127.0.0.1",
                    "-validity",
                    "2",
                    "-noprompt",
                ).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start()
            try {
                check(generator.waitFor(20, TimeUnit.SECONDS) && generator.exitValue() == 0)
            } finally {
                generator.destroyForcibly()
            }
            Files.newInputStream(store).use { keys.load(it, password.toCharArray()) }
        } finally {
            Files.deleteIfExists(store)
            Files.deleteIfExists(dir)
        }
        val managers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply { init(keys, password.toCharArray()) }
        context = SSLContext.getInstance("TLS").apply { init(managers.keyManagers, null, null) }
        val der = keys.getCertificate("loopback").encoded
        certificate = "-----BEGIN CERTIFICATE-----\n${Base64.getMimeEncoder().encodeToString(der)}\n-----END CERTIFICATE-----"
    }
}
