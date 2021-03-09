package ra;


import api.request.Bookingdates;
import api.request.RequestUser;
import api.response.ResponseUser;
import apiMethods.ApiMethod;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TryRestAssured {
    ApiMethod apiMethod = new ApiMethod();

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.port = 443;
        RestAssured.authentication = preemptive().basic("username", "password");
    }

    @Test
    public void whenRequestGet_thenOK() {
        when().request("GET", "/booking")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void whenValidateResponseTime_thenSuccess() {
        when().get("/users/booking")
                .then().time(lessThan(5000L));
    }

    @Test
    public void givenUrl_whenSuccessOnGetsResponseAndJsonHasRequiredKV_thenCorrect() {
        when().get("/booking/7")
                .then().statusCode(200)
                .assertThat()
                .body("totalprice", equalTo(793));
    }

    @Test
    public void given_post_createUser() {
        String firstname = "Lola";
        String lastname = "Lee";
        int totalPrice = 777;
        RequestUser user = RequestUser.builder()
                .firstname(firstname)
                .lastname(lastname)
                .additionalneeds("Breakfast")
                .bookingdates(Bookingdates.builder().checkin("2021-01-01").checkout("2021-12-01").build())
                .totalprice(totalPrice)
                .depositpaid(true)
                .build();

        Response response = apiMethod.postMethod(user, "/booking");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        ResponseUser responseUser = response.as(ResponseUser.class);

        Assert.assertNotNull(responseUser.getBookingid(), "Booking ID validation");
        Assert.assertEquals(firstname, responseUser.getBooking().getFirstname(), "firstname validation");
        Assert.assertEquals(lastname, responseUser.getBooking().getLastname(), "lastname validation");
        Assert.assertEquals(totalPrice, responseUser.getBooking().getTotalprice(), "totalPrice validation");
    }

    @Test
    public void putchResultForbidden() {
        String firstname = "Name";
        String lastname = "Forbidden";
        String checkin = "01.01.2021";
        String checkout = "01.02.2021";

        RequestUser user = RequestUser.builder()
                .firstname(firstname)
                .lastname(lastname)
                .bookingdates(Bookingdates.builder().checkin(checkin).checkout(checkout).build())
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .patch("/booking/8");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_FORBIDDEN);
    }

    @Test // does not work, although in Postman I can make 400 when "total saving" = 000
    public void postResultBadRequest() {
        String json = "{\n" +
                "    \"firstname\" : \"Jim\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 000,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast123\"\n" +
                "}";

        Response response = apiMethod.postMethod(json, "/booking");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);

    }

    @Test // does not work with authorisation
    public void putchResult() {

        String firstname = "NewName";
        String lastname = "NewLastname";
        String checkin = "01.01.2021";
        String checkout = "01.02.2021";
        int totalPrice = 123;

        RequestUser user = RequestUser.builder()  //write fields to be changed
                .firstname(firstname)
                .lastname(lastname)
                .bookingdates(Bookingdates.builder().checkin(checkin).checkout(checkout).build())
                .build();

        given().auth()
                .preemptive()
                .basic("admin", "password123")
                .and()
                .body(user)
                .when()
                .patch("/booking/8")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        Response response = given()
                .cookie("token", "9b2f63b9064da0b") //how write automated getting of token?
                .contentType(ContentType.JSON)
                .body(user)
                .patch("/booking/8");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        ResponseUser responseUser = response.as(ResponseUser.class);

        Assert.assertNotNull(responseUser.getBookingid(), "Booking ID validation");
//        Assert.assertEquals(firstname, responseUser.getBooking().getFirstname(), "firstname validation");
//        Assert.assertEquals(lastname, responseUser.getBooking().getLastname(), "lastname validation");
    }

    @Test //passed but there are questions
    public void putchResultNew() {

        String firstname = "NewName";
        String lastname = "NewLastname";
        String checkin = "01.01.2021";
        String checkout = "01.02.2021";
        int totalPrice = 123;

        RequestUser user = RequestUser.builder()
                .firstname(firstname)
                .lastname(lastname)
                .additionalneeds("Breakfast")
                .bookingdates(Bookingdates.builder().checkin(checkin).checkout(checkout).build())
                .totalprice(totalPrice)
                .depositpaid(true)
                .build();

        //GIVEN
        RestAssured.given()
                .baseUri("https://restful-booker.herokuapp.com/booking/8") // how to do it shorter?
                .cookie("token", "05263ab9fe524a8")
                .contentType(ContentType.JSON)
                .body(user)
                // WHEN
                .when()
                .patch() // how system knows which fields should be edited?
                // THEN
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", Matchers.equalTo("NewName"))
                .body("lastname", Matchers.equalTo("NewLastname"));

    }
}
