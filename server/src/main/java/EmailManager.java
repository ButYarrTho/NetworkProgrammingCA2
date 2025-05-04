import model.Email;

import java.time.LocalDateTime;
import java.util.*;

public class EmailManager {
    private final Map<String, List<Email>> inboxes = new HashMap<>();   // username -> inbox
    private final Map<String, List<Email>> sentItems = new HashMap<>(); // username -> sent

    private int emailIdCounter = 1;

    public synchronized Email sendEmail(String sender, List<String> recipients, String subject, String body) {
        Email email = new Email(emailIdCounter++, sender, recipients, subject, body, LocalDateTime.now());

        sentItems.putIfAbsent(sender, new ArrayList<>());
        sentItems.get(sender).add(email);

        for (String recipient : recipients) {
            inboxes.putIfAbsent(recipient, new ArrayList<>());
            inboxes.get(recipient).add(email);
        }

        return email;
    }


    public synchronized List<Email> getInbox(String username) {
        return inboxes.getOrDefault(username, new ArrayList<>());
    }

    public synchronized List<Email> getSent(String username) {
        return sentItems.getOrDefault(username, new ArrayList<>());
    }

    public synchronized Email findEmailInInbox(String username, int id) {
        return inboxes.getOrDefault(username, new ArrayList<>()).stream()
                .filter(email -> email.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public synchronized Email findEmailInSent(String username, int id) {
        return sentItems.getOrDefault(username, new ArrayList<>()).stream()
                .filter(email -> email.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public synchronized boolean deleteEmailFromInbox(String username, int id) {
        List<Email> inbox = inboxes.get(username);
        if (inbox != null) {
            return inbox.removeIf(email -> email.getId() == id);
        }
        return false;
    }

    public synchronized boolean deleteEmailFromSent(String username, int id) {
        List<Email> sent = sentItems.get(username);
        if (sent != null) {
            return sent.removeIf(email -> email.getId() == id);
        }
        return false;
    }
}
