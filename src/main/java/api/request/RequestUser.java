package api.request;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestUser {
	private String firstname;
	private String lastname;
	private String additionalneeds;
	private Bookingdates bookingdates;
	private int totalprice;
	private boolean depositpaid;

}
