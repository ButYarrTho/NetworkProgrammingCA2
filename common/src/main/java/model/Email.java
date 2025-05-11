package model;

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
    private String sender;              // Sender username
    private List<String> recipients;    // Recipients list
    private String subject;             // Subject of email
    private String body;                // Body of email
    private LocalDateTime timestamp;    // When it was sent
}
