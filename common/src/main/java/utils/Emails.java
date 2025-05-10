package utils;

import model.Email;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import static protocols.EmailUtilities.TIMESTAMP;

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

        return formattedEmails.substring(0, formattedEmails.length() - delimiter.length());
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
}
