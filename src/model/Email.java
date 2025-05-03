package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Email {
    private String subject;
    @NonNull
    private String body;
    private LocalDateTime timestamp;
}
