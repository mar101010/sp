
import io.restassured.RestAssured.given
import io.restassured.response.Response
import io.restassured.path.json.JsonPath
import java.util.Base64

object TokenManager {

    private var accessToken = ""
    private var refreshToken = ""
    private var expirationTime = 0L

    private fun login() {
        val response = given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body("""
                {
                    "username": "${Config.username}",
                    "password": "${Config.password}",
                    "expiresInMins": ${Config.tokenExpirationMinutes}
                }
            """.trimIndent())
            .`when`()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .response()

        saveTokens(response)
    }

    private fun saveTokens(response: Response) {
        accessToken = response.jsonPath().getString("accessToken")
        refreshToken = response.jsonPath().getString("refreshToken")
        expirationTime = getExpirationTime(accessToken)
    }

    private fun getExpirationTime(token: String): Long {
        val payload = token.split(".")[1]

        val decodedPayload = String(
            Base64.getUrlDecoder().decode(payload),
            Charsets.UTF_8
        )

        return JsonPath(decodedPayload).getLong("exp") * 1000L
    }

    private fun isTokenExpired(): Boolean {
        return System.currentTimeMillis() +
                Config.tokenRefreshBufferSeconds * 1000L >= expirationTime
    }

    private fun refresh() {
        val response = given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body("""
                {
                    "refreshToken": "$refreshToken",
                    "expiresInMins": ${Config.tokenExpirationMinutes}
                }
            """.trimIndent())
            .`when`()
            .post("/auth/refresh")

        when (response.statusCode()) {
            200 -> saveTokens(response)
            400, 401, 403 -> login()
            else -> error("Token refresh failed: ${response.statusCode()}")
        }
    }

    fun getValidToken(): String {
        if (accessToken.isBlank() || refreshToken.isBlank()) {
            login()
        } else if (isTokenExpired()) {
            refresh()
        }

        return accessToken
    }

    fun clearTokens() {
        accessToken = ""
        refreshToken = ""
        expirationTime = 0L
    }
}
