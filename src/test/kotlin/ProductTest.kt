import org.junit.jupiter.api.Test
import io.restassured.RestAssured.*
import io.restassured.specification.RequestSpecification

class ProductTest {

    @Test
    fun getProductById() {
    given()
    .baseUri(Config.baseUrl)
        .get("/products/1")
        .then()
        .statusCode(201)
        .log().all()
    }

    @Test
    fun getProductByWrongId() {
        given()
            .baseUri(Config.baseUrl)
            .get("/products/123412345")
            .then()
            .statusCode(404)
            .log().all()
    }

    @Test
    fun getProductsWithParams() {
        given()
            .baseUri(Config.baseUrl)
            .queryParam("limit",5)
            .queryParam("skip",10)
            .get("/products/")
            .then()
            .statusCode(200)
            .log().all()
    }

}