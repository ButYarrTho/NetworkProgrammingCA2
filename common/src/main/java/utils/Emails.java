package utils;

import model.Email;

import java.time.format.DateTimeFormatter;

import static protocols.EmailUtilities.*;

public class Emails {
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(TIMESTAMP);

    public static String toHeaderResponseString(Email email) {
        return email.getId() +
                SUBDELIMITER +
                email.getSender() +
                SUBDELIMITER +
                email.getSubject() +
                SUBDELIMITER +
                email.getTimestamp().format(timestampFormat);
    }

    public static String toHeaderResponseList(Email[] emails) {
        StringBuilder formattedEmails = new StringBuilder();
        for (int i = 0; i < emails.length; i++) {
            formattedEmails.append(toHeaderResponseString(emails[i]));
            if (i != emails.length - 1) formattedEmails.append(DELIMITER);
        }

        return formattedEmails.substring(0, formattedEmails.length() - DELIMITER.length());
    }
}
