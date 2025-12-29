package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonValue

enum class GitOpsAddonType(
    @JsonValue val value: String,
) {
    MYSQL("mysql"),
    POSTGRESQL("postgresql"),
    REDIS("redis"),
    MONGODB("mongodb"),
    KAFKA("kafka"),
    RABBITMQ("rabbitmq"),
    ELK("elk"),
    DEBEZIUM("debezium"),
}
