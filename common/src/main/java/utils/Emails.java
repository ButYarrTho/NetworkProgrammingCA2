package utils;

import model.Email;

import java.time.format.DateTimeFormatter;

import static protocols.EmailUtilities.*;

public class Emails {
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(TIMESTAMP);

    /**
     * Format the header of an email into a response string
     *
     * @param email The email to be formatted
     * @return String representation of the email's headers
     */
    public static String toHeaderResponseString(Email email) {
        return email.getId() +
                SUBDELIMITER +
                email.getSender() +
                SUBDELIMITER +
                email.getSubject() +
                SUBDELIMITER +
                email.getTimestamp().format(timestampFormat);
    }

    /**
     * Format the headers of multiple emails into a response string
     *
     * @param emails The emails to be formatted
     * @return String representation of the emails headers
     */
    public static String toHeaderResponseString(Email[] emails) {
        StringBuilder formattedEmails = new StringBuilder();
        for (int i = 0; i < emails.length; i++) {
            formattedEmails.append(toHeaderResponseString(emails[i]));
            if (i != emails.length - 1) formattedEmails.append(DELIMITER);
        }

        return formattedEmails.substring(0, formattedEmails.length() - DELIMITER.length());
    }
}
