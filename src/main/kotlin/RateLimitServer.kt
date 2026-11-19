import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun main() {

    val server = HttpServer.create(InetSocketAddress(8080), 0)

    val limit = 5
    val window = 60_000L

    var counter = 0
    var windowStart = System.currentTimeMillis() / window

    server.createContext("/users") { exchange ->

        val currentWindow = System.currentTimeMillis() / window

        if (currentWindow != windowStart) {
            counter = 0
            windowStart = currentWindow
        }

        counter++

        val status = if (counter <= limit) 200 else 429

        val response = if (status == 200) {
            "Request successful"
        } else {
            "Too Many Requests"
        }

        val bytes = response.toByteArray()

        exchange.sendResponseHeaders(status, bytes.size.toLong())

        exchange.responseBody.use {
            it.write(bytes)
        }
    }

    server.start()

    println("Server started on http://localhost:8080")
}