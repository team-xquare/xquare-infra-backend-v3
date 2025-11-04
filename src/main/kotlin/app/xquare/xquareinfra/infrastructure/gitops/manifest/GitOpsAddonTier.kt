package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonValue

enum class GitOpsAddonTier(
    @JsonValue val value: String,
) {
    NANO("x3.nano"),
    MICRO("x3.micro"),
    SMALL("x3.small"),
    MEDIUM("x3.medium"),
    LARGE("x3.large"),
}
