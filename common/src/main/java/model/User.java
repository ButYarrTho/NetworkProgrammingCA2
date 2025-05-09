package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class User {
    @NonNull
    private String email;

    @NonNull
    @EqualsAndHashCode.Exclude
    private String password;
}
