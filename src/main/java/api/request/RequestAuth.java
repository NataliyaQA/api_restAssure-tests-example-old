package api.request;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestAuth{
    @NonNull
    private String password;
    @NonNull
    private String username;
}
