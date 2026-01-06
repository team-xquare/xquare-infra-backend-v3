package app.xquare.xquareinfra.adapters.outbound.vault

sealed class VaultException : Exception() {
    class VaultSecretNotFound : VaultException()
}
