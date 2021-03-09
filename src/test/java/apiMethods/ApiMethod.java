package apiMethods;

import api.request.RequestUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiMethod {

    public Response postMethod (String json, String posty) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(posty);
    }

    public Response postMethod (RequestUser json, String posty) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(posty);
    }

    public Response postMethod (RequestUser json, String posty, String auth) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .post(posty);
    }

}
