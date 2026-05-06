import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ApiTest {
    private static final String BASE_URL = "https://postman-echo.com";
    private static final String REQUEST_BODY = "{\"name\":\"Pavel\",\"course\":\"AQA\"}";
    
    @Test
    void getMethod() {
        RestAssured.baseURI = BASE_URL;

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
        RestAssured.baseURI = BASE_URL;

        given()
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)
        .when()
                .post("/post")
        .then()
                .statusCode(200)
                .body("json.name", equalTo("Pavel"))
                .body("json.course", equalTo("AQA"));
    }

    @Test
    void putMethod() {
        RestAssured.baseURI = BASE_URL;

        given()
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)
        .when()
                .put("/put")
        .then()
                .statusCode(200)
                .body("json.name", equalTo("Pavel"))
                .body("json.course", equalTo("AQA"));
    }

    @Test
    void patchMethod() {
        RestAssured.baseURI = BASE_URL;

        given()
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)
        .when()
                .patch("/patch")
        .then()
                .statusCode(200)
                .body("json.name", equalTo("Pavel"))
                .body("json.course", equalTo("AQA"));
    }

    @Test
    void deleteMethod() {
        RestAssured.baseURI = BASE_URL;

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
