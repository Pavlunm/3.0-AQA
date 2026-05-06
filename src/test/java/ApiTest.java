import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ApiTest {

    @Test
    void getMethod() {
        RestAssured.baseURI = "https://postman-echo.com";

        given()
                .queryParam("name", "Pavel")
                .queryParam("course", "AQA")
        .when()
                .get("/get")
        .then()
                .statusCode(200)
                .body("args.name", equalTo("Pavel"))
                .body("args.course", equalTo("AQA"));
    }

    @Test
    void postMethod() {
        RestAssured.baseURI = "https://postman-echo.com";

        String body = "{\"name\":\"Pavel\",\"course\":\"AQA\"}";

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/post")
        .then()
                .statusCode(200)
                .body("json.name", equalTo("Pavel"))
                .body("json.course", equalTo("AQA"));
    }

    @Test
    void putMethod() {
        RestAssured.baseURI = "https://postman-echo.com";

        String body = "{\"name\":\"Pavel\",\"course\":\"AQA\"}";

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .put("/put")
        .then()
                .statusCode(200)
                .body("json.name", equalTo("Pavel"))
                .body("json.course", equalTo("AQA"));
    }

    @Test
    void patchMethod() {
        RestAssured.baseURI = "https://postman-echo.com";

        String body = "{\"name\":\"Pavel\",\"course\":\"AQA\"}";

        given()
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .patch("/patch")
        .then()
                .statusCode(200)
                .body("json.name", equalTo("Pavel"))
                .body("json.course", equalTo("AQA"));
    }

    @Test
    void deleteMethod() {
        RestAssured.baseURI = "https://postman-echo.com";

        given()
                .queryParam("name", "Pavel")
                .queryParam("course", "AQA")
        .when()
                .delete("/delete")
        .then()
                .statusCode(200)
                .body("args.name", equalTo("Pavel"))
                .body("args.course", equalTo("AQA"));
    }
}
