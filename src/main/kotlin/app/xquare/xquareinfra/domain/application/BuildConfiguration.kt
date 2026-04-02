package app.xquare.xquareinfra.domain.application

import java.util.*

enum class BuildConfigurationField {
    VERSION,
    BUILD_COMMAND,
    START_COMMAND,
    INPUT_PATH,
    OUTPUT_PATH,
    WORKING_DIRECTORY,
}

enum class BuildConfigurationType(
    val fields: EnumSet<BuildConfigurationField>,
) {
    GRADLE(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    NODE_JS(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.START_COMMAND,
        ),
    ),
    REACT(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    VITE(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    VUE(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    NEXT_JS(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.START_COMMAND,
        ),
    ),
    NEXT_JS_EXPORT(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    GO(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    RUST(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    MAVEN(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.OUTPUT_PATH,
        ),
    ),
    DJANGO(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.START_COMMAND,
        ),
    ),
    FLASK(
        EnumSet.of(
            BuildConfigurationField.VERSION,
            BuildConfigurationField.BUILD_COMMAND,
            BuildConfigurationField.START_COMMAND,
        ),
    ),
    DOCKER(
        EnumSet.of(
            BuildConfigurationField.INPUT_PATH,
            BuildConfigurationField.WORKING_DIRECTORY,
        ),
    ),
}

data class BuildConfiguration(
    val type: BuildConfigurationType,
    val version: String? = null,
    val buildCommand: String? = null,
    val startCommand: String? = null,
    val inputPath: String? = null,
    val outputPath: String? = null,
    val workingDirectory: String? = null,
) {
    init {
        for (field in BuildConfigurationField.entries) {
            val isPresent =
                when (field) {
                    BuildConfigurationField.VERSION -> version != null
                    BuildConfigurationField.BUILD_COMMAND -> buildCommand != null
                    BuildConfigurationField.START_COMMAND -> startCommand != null
                    BuildConfigurationField.INPUT_PATH -> inputPath != null
                    BuildConfigurationField.OUTPUT_PATH -> outputPath != null
                    BuildConfigurationField.WORKING_DIRECTORY -> workingDirectory != null
                }
            val isRequired = field in type.fields
            if (isPresent != isRequired) {
                throw IllegalArgumentException() // todo domain error
            }
        }
    }
}
