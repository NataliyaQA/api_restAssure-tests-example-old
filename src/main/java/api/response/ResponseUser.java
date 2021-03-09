package api.response;

import api.request.Bookingdates;
import api.request.RequestUser;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseUser{
	private RequestUser booking;
	private int bookingid;
}
