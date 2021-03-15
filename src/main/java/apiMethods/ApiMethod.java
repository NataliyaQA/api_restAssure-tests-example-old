package apiMethods;

import api.request.RequestAuth;
import api.request.RequestUser;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiMethod {

    public String userToken() {
        RequestAuth requestAuth = RequestAuth
                .builder()
                .username("admin")
                .password("password123").build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestAuth)
                .post("/auth");

        response.then().statusCode(200);
        return response.then().extract().path("token");
    }

    public Response postMethod(String json, String url) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(url);
    }

    public Response postMethod(RequestUser user, String url) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .post(url);
    }

    public Response patchMethod(RequestUser user, String url, String token) {
        return given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(user)
                .patch(url);
    }

    public Response patchMethod(RequestUser user, String url, String admin, String password) {
        return given()
                .auth()
                .preemptive()
                .basic(admin, password)
                .contentType(ContentType.JSON)
                .body(user)
                .patch(url);
    }

    public Response patchMethod(RequestUser user, String url) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .patch(url);
    }

}
