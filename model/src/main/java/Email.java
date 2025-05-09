import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Email {
    private int id;                     // Unique ID
    @NonNull
    private String sender;              // Sender username
    @NonNull
    private List<String> recipients;    // Recipients list
    @NonNull
    private String subject;             // Subject of email
    @NonNull
    private String body;                // Body of email
    private LocalDateTime timestamp;    // When it was sent
}
