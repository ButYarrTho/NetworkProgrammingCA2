import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class User {
    @NonNull
    private String email;

    @NonNull
    private String password;
}
