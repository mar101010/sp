import org.junit.jupiter.api.Test
import io.restassured.RestAssured.*

class AuthTest {

    private fun getAccessToken(): String {
        return given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body("""
            {
                "username": "emilys",
                "password": "emilyspass"
            }
        """.trimIndent())
            .`when`()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path<String>("accessToken")
    }

    private fun getRefreshToken(): String {
        return given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body("""
            {
                "username": "emilys",
                "password": "emilyspass"
            }
        """.trimIndent())
            .`when`()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path<String>("refreshToken")
    }

    @Test
    fun loginWithValidCredentials() {
        val accessToken=given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body(
                """{
            "username": "emilys",
            "password": "emilyspass"
            }
            """.trimIndent()
            )
            .post("/auth/login")
            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .path<String>("accessToken")
    }

    @Test
    fun loginWithInvalidCredentials() {
        given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body(
                """{
            "username": "emilys1",
            "password": "emilyspass1"
            }
            """.trimIndent()
            )
            .post("/auth/login")
            .then()
            .log().all()
            .statusCode(400) // it returns 400 instead
    }

    @Test
    fun getUserWithValidToken() {
        given()
            .baseUri(Config.baseUrl)
            .auth().oauth2(getAccessToken())
            .`when`()
            .get("/auth/me")
            .then()
            .log().all()
            .statusCode(200)
    }

    @Test
    fun getUserWithoutToken() {
        given()
            .baseUri(Config.baseUrl)
            .`when`()
            .get("/auth/me")
            .then()
            .log().all()
            .statusCode(401)
    }

    @Test
    fun getUserWithInvalidToken() {
        given()
            .baseUri(Config.baseUrl)
            .auth().oauth2("invalidToken")
            .`when`()
            .get("/auth/me")
            .then()
            .log().all()
            .statusCode(401)
    }

    @Test
    fun refreshAccessToken() {
        given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body("""
            {
                "refreshToken": "${getRefreshToken()}",
                "expiresInMins": 30
            }
        """.trimIndent())
            .`when`()
            .post("/auth/refresh")
            .then()
            .log().all()
            .statusCode(200)
    }


}