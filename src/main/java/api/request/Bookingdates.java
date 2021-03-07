package api.request;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookingdates{
	private String checkin;
	private String checkout;
}
