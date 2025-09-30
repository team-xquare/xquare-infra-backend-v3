package app.xquare.xquareinfra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class XquareInfraV3Application

fun main(args: Array<String>) {
    runApplication<XquareInfraV3Application>(*args)
}
