import org.junit.jupiter.api.Test
import io.restassured.RestAssured.*
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    private var accessToken = ""

    @BeforeEach
    fun prepareToken() {
        accessToken = TokenManager.getValidToken()
    }

    @AfterAll
    fun tearDown() {
        TokenManager.clearTokens()
        accessToken = ""
    }

    @Test
    fun createUser() {
        given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .body("""
            {
                "firstName": "Syrnik",
                "lastName": "Pavlisin",
                "age": 16
            }
        """.trimIndent())
            .`when`()
            .post("/users/add")
            .then()
            .log().all()
            .statusCode(201)
            .body("firstName", equalTo("Syrnik"))
            .body("age", equalTo(16))
    }

    @Test
    fun updateUser() {
        given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .pathParam("id", 1)
            .body("""
            {
                "firstName": "Peggy",
                "lastName": "Pavlisina",
                "age": 8
            }
        """.trimIndent())
            .`when`()
            .put("/users/{id}")
            .then()
            .log().all()
            .statusCode(200)
            .body("firstName", equalTo("Peggy"))
            .body("lastName", equalTo("Pavlisina"))
            .body("age", equalTo(8))
    }

    @Test
    fun partiallyUpdateUser() {
        given()
            .baseUri(Config.baseUrl)
            .contentType("application/json")
            .pathParam("id", 1)
            .body("""
            {
                "firstName": "Alisa dog"
            }
        """.trimIndent())
            .`when`()
            .patch("/users/{id}")
            .then()
            .log().all()
            .statusCode(200)
            .body("firstName", equalTo("Alisa dog"))
    }

    @Test
    fun deleteUser() {
        given()
            .baseUri(Config.baseUrl)
            .pathParam("id", 1)
            .`when`()
            .delete("/users/{id}")
            .then()
            .log().all()
            .statusCode(200)
            .body("isDeleted", equalTo(true))
    }

    @Test
    fun getUserById() {
        given()
            .baseUri(Config.baseUrl)
            .pathParam("id", 1)
            .`when`()
            .get("/users/{id}")
            .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("firstName", equalTo("Emily"))
    }

}