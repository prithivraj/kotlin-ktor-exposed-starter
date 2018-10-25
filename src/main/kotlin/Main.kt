import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import service.DatabaseFactory
import service.WidgetService
import web.widget

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets)

    install(StatusPages){
        exception<Throwable> { cause ->
            call.respond(cause.message.toString())
        }
    }

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    DatabaseFactory.init()

    val widgetService = WidgetService()

    install(Routing) {
        widget(widgetService)
    }

}

fun main(args: Array<String>) {
    embeddedServer(
            Netty,
            8080,
            watchPaths = listOf("MainKt"),
            module = Application::module
    ).start()
}