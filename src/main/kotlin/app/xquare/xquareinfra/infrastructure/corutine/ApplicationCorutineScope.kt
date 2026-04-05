package app.xquare.xquareinfra.infrastructure.corutine

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.SupervisorJob
import org.springframework.stereotype.Component


@Component
class ApplicationCoroutineScope : CoroutineScope {
    override val coroutineContext = SupervisorJob() + Dispatchers.IO

    @PreDestroy
    fun destroy() {
        cancel()
    }
}