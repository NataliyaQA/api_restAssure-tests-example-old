package ra;


import api.request.Bookingdates;
import api.request.RequestUser;
import api.response.ResponseUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TryRestAssured {

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.port = 443;
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

    @Test // needs explanation, why without when() before get?
    public void givenUrl_whenSuccessOnGetsResponseAndJsonHasRequiredKV_thenCorrect() {
        get("/booking/7")
                .then().statusCode(200)
                .assertThat()
                .body("totalprice", equalTo(810));
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

        Response r = given() //= get("/booking") after POST?
                .contentType(ContentType.JSON)
                .body(user)
                .post("/booking");

        Assert.assertEquals(r.statusCode(), HttpStatus.SC_OK);
        ResponseUser responseUser = r.as(ResponseUser.class);

        Assert.assertNotNull(responseUser.getBookingid(), "Booking ID validation");
        //getBookingid = we don't have method get in ResponseUser. How system knows action of method and does not refuse it?
        Assert.assertEquals(firstname, responseUser.getBooking().getFirstname(), "firstname validation");
        // previous question + why we .getFirstname() after .getBooking()?
        Assert.assertEquals(lastname, responseUser.getBooking().getLastname(), "lastname validation");
        Assert.assertEquals(totalPrice, responseUser.getBooking().getTotalprice(), "totalPrice validation");
    }
}
