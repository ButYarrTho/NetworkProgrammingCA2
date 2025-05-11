package utils;

import model.Email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static protocols.EmailUtilities.*;

public class Emails {
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(TIMESTAMP);

    /**
     * Format the header of an email into a response string
     *
     * @param email     Email to be formatted
     * @param delimiter Character sequence delimiting the values
     * @return String representation of the email headers
     */
    public static String toHeaderResponseString(Email email, String delimiter) {
        return email.getId() +
                delimiter +
                email.getSender() +
                delimiter +
                email.getSubject() +
                delimiter +
                email.getTimestamp().format(timestampFormat);
    }

    /**
     * Format the headers of multiple emails into a response string
     *
     * @param emails       The emails to be formatted
     * @param delimiter    Character sequence delimiting the different emails
     * @param subdelimiter Character sequence delimiting the values
     * @return String representation of the emails headers
     */
    public static String toHeaderResponseString(List<Email> emails, String delimiter, String subdelimiter) {
        StringBuilder formattedEmails = new StringBuilder();
        for (int i = 0; i < emails.size(); i++) {
            formattedEmails.append(toHeaderResponseString(emails.get(i), subdelimiter));
            if (i != emails.size() - 1) formattedEmails.append(delimiter);
        }

        return formattedEmails.toString();
    }

    /**
     * Search for a string in emails. Searches through the body, subject, sender, and recipients.
     *
     * @param emails       Emails to search through
     * @param searchString The string to search for
     * @return List of emails containing the search string.
     */
    public static List<Email> query(Collection<Email> emails, String searchString) {
        return emails.stream()
                .filter(email -> {
                    String lowerCaseQuery = searchString.toLowerCase();
                    return email.getBody().toLowerCase().contains(lowerCaseQuery)
                            || email.getSubject().toLowerCase().contains(lowerCaseQuery)
                            || email.getSender().toLowerCase().contains(lowerCaseQuery)
                            || email.getRecipients().stream().anyMatch(recipient -> recipient.toLowerCase().contains(lowerCaseQuery));
                })
                .toList();
    }

    /**
     * Parse emails from a response string. Utilises parseEmail method.
     *
     * @param response Response returned by the server
     * @return List of emails. Empty list if no emails were contained in the response.
     */
    public static List<Email> parseEmailsResponse(String response) {
        String[] splitResponse = response.split(DELIMITER);
        List<Email> emails = new ArrayList<>();

        if (splitResponse.length < 2) return emails;

        // Start at 1 to ignore the action name
        for (int i = 1; i < splitResponse.length; i++) {
            emails.add(parseEmail(splitResponse[i]));
        }

        return emails;
    }

    /**
     * Parse an email from string
     *
     * @param emailString Email as string
     * @return Email object or null if the format could not be determined.
     */
    public static Email parseEmail(String emailString) {
        String[] emailParts = emailString.split(SUBDELIMITER);

        return switch (emailParts.length) {
            case 4 -> {
                int id;
                try {
                    id = Integer.parseInt(emailParts[0]);
                } catch (NumberFormatException e) {
                    id = 0;
                }
                LocalDateTime timestamp;
                try {
                    timestamp = LocalDateTime.parse(emailParts[3], timestampFormat);
                } catch (DateTimeParseException e) {
                    timestamp = LocalDateTime.MIN;
                }

                yield Email.builder()
                        .id(id)
                        .sender(emailParts[1])
                        .subject(emailParts[2])
                        .timestamp(timestamp)
                        .build();
            }
            case 2 -> Email.builder()
                    .subject(emailParts[0])
                    .body(emailParts[1])
                    .build();
            default -> null;
        };
    }
}
