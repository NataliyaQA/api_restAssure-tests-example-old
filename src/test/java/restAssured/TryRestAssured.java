package restAssured;


import api.Helper.UtilsNew;
import api.request.Bookingdates;
import api.request.RequestUser;
import api.response.ResponseUser;
import apiMethods.ApiMethod;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

//import static Helper.Utils.generateStringFromResource;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class TryRestAssured {
    ApiMethod apiMethod = new ApiMethod();
    UtilsNew utils = new UtilsNew();


    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.port = 443;
//        RestAssured.authentication = preemptive().basic("username", "password");
    }

    @Test
    public void whenRequestGet_thenOK() {
        when().request("GET", "/booking")
                .then().statusCode(HttpStatus.SC_OK);
        // the same but another way
        when().get("/booking")
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
        String checkin = "01.01.2021";
        String checkout = "01.02.2021";

        RequestUser user = RequestUser.builder()
                .firstname(firstname)
                .lastname(lastname)
                .additionalneeds("Breakfast")
                .bookingdates(Bookingdates.builder().checkin(checkin).checkout(checkout).build())
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

        Response response = apiMethod.patchMethod(user, "/booking/8");

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

    @Test
    public void postResultBadRequestTwo() throws IOException {
        String jsonBody = utils.generateStringFromResource(System.getProperty("user.dir")
                +"\\src\\test\\java\\ra\\badRequest.json");
        Response response = apiMethod.postMethod(jsonBody, "/booking");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test // does not work with authorisation
    public void putchResultWithCreds() {

        String firstname = "NewName";
        String lastname = "NewLastname";
        String checkin = "01.01.2021";
        String checkout = "01.02.2021";

        RequestUser user = RequestUser.builder()  //write fields to be changed
                .firstname(firstname)
                .lastname(lastname)
                .bookingdates(Bookingdates.builder().checkin(checkin).checkout(checkout).build())
                .build();

        Response response = apiMethod.patchMethod(user, "/booking/8", "admin", "password123");

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        RequestUser responseUser = response.as(RequestUser.class);

        Assert.assertEquals(firstname, responseUser.getFirstname(), "firstname validation");
        Assert.assertEquals(lastname, responseUser.getLastname(), "lastname validation");
    }

    @Test
    public void putchResultWithCookie() {

        String firstname = randomAlphanumeric(20).toLowerCase();
        String lastname = randomAlphanumeric(20).toLowerCase();
        String checkin = "01.01.2021";
        String checkout = "01.02.2021";

        RequestUser user = RequestUser.builder()  //write fields to be changed
                .firstname(firstname)
                .lastname(lastname)
                .bookingdates(Bookingdates.builder().checkin(checkin).checkout(checkout).build())
                .build();
        Response response = apiMethod.patchMethod(user, "/booking/8", apiMethod.userToken());

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        RequestUser responseUser = response.as(RequestUser.class);
        Assert.assertEquals(firstname, responseUser.getFirstname(), "firstname validation");
        Assert.assertEquals(lastname, responseUser.getLastname(), "lastname validation");
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
        given()
                .baseUri("https://restful-booker.herokuapp.com/booking/8") // how to do it shorter?
                .cookie("token", apiMethod.userToken())
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
