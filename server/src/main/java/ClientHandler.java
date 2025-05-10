import lombok.extern.slf4j.Slf4j;
import model.Email;
import networking.INetworkLayer;
import utils.Emails;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static protocols.EmailUtilities.*;

@Slf4j
public class ClientHandler implements Runnable {
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(TIMESTAMP);
    private final INetworkLayer network;
    private final Storage storage;
    private String loggedInUsername = null;

    public ClientHandler(INetworkLayer networkLayer, Storage storage) {
        this.network = networkLayer;
        this.storage = storage;
    }

    @Override
    public void run() {
        boolean clientSession = true;
        try {
            while (clientSession) {
                String request = network.receive();
                if (request == null) break;
                log.info("Received request: {}", request);
                String[] parts = request.split(DELIMITER);

                String response = switch (parts[0]) {
                    case LOGIN -> handleLogin(parts);
                    case REGISTER -> handleRegister(parts);
                    case SEND -> handleSend(parts);
                    case LIST_RECEIVED -> handleListReceived();
                    case LIST_SENT -> handleListSent();
                    case SEARCH_RECEIVED -> handleSearchReceived(parts);
                    case SEARCH_SENT -> handleSearchSent(parts);
                    case READ -> handleRead(parts);
                    case DELETE -> handleDelete(parts);
                    case LOGOUT -> handleLogout(parts);
                    case EXIT -> {
                        clientSession = false;
                        yield handleExit();
                    }
                    default -> INVALID_REQUEST;
                };

                network.send(response);
            }
        } catch (Exception e) {
            log.error("Client connection lost.");
        } finally {
            network.close();
        }
    }

    /**
     * Handle user login
     *
     * @param parts Split parts of the request
     * @return Response to the request.
     */
    public String handleLogin(String[] parts) {
        if (parts.length != 3) return INVALID_REQUEST;
        if (loggedInUsername != null) return ALREADY_LOGGED_IN;
        String username = parts[1];
        String password = parts[2];
        boolean success = storage.userManager.validateLogin(username, password);

        if (success) {
            loggedInUsername = username;
            return LOGIN_SUCCESS;
        } else {
            return INVALID_CREDENTIALS;
        }
    }

    public String handleRegister(String[] parts) {
        if (parts.length != 3) return INVALID_REQUEST;
        if (loggedInUsername != null) return ALREADY_LOGGED_IN;
        String username = parts[1];
        String password = parts[2];

        boolean success = storage.userManager.add(username, password);
        if (success) {
            loggedInUsername = username;
            return REGISTER_SUCCESS;
        } else {
            return USER_ALREADY_EXISTS;
        }
    }

    private String handleSend(String[] parts) {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        if (parts.length != 4) return INVALID_REQUEST;

        String recipientPart = parts[1];
        String subject = parts[2];
        String body = parts[3];

        String[] recipientArray = recipientPart.split(LIST_DELIMITER);
        List<String> recipients = Arrays.asList(recipientArray);

        List<String> invalidRecipients = new ArrayList<>();

        for (String recipient : recipients) {
            if (!storage.userManager.userExists(recipient)) {
                invalidRecipients.add(recipient);
            }
        }

        if (!invalidRecipients.isEmpty()) {
            return USER_NOT_FOUND + DELIMITER + String.join(LIST_DELIMITER, invalidRecipients);
        }

        storage.emailManager.sendEmail(loggedInUsername, recipients, subject, body);

        return EMAIL_SENT;
    }

    /**
     * List received emails handler
     *
     * @return Response string
     */
    private String handleListReceived() {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        List<Email> inbox = storage.emailManager.getInbox(loggedInUsername);
        if (inbox.isEmpty()) return NO_EMAILS;

        Email[] emails = new Email[inbox.size()];
        inbox.toArray(emails);

        return Emails.toHeaderResponseString(emails, DELIMITER, SUBDELIMITER);
    }

    /**
     * List sent emails handler.
     *
     * @return Response string
     */
    private String handleListSent() {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        List<Email> outbox = storage.emailManager.getSent(loggedInUsername);
        if (outbox.isEmpty()) return NO_EMAILS;

        Email[] emails = new Email[outbox.size()];
        outbox.toArray(emails);

        return Emails.toHeaderResponseString(emails, DELIMITER, SUBDELIMITER);
    }

    /**
     * Search received email handler.
     *
     * @param parts Parts of a request
     * @return Response string
     */
    private String handleSearchReceived(String[] parts) {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        if (parts.length != 2) return INVALID_REQUEST;
        String query = parts[1];
        List<Email> inbox = storage.emailManager.getInbox(loggedInUsername);
        if (inbox.isEmpty()) return NO_EMAILS;

        Email[] results = Emails.query(inbox, query);

        return Emails.toHeaderResponseString(results, DELIMITER, SUBDELIMITER);
    }

    /**
     * Search sent emails action.
     *
     * @param parts Parts of a request
     * @return Response string
     */
    private String handleSearchSent(String[] parts) {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        if (parts.length != 2) return INVALID_REQUEST;
        String query = parts[1];
        List<Email> outbox = storage.emailManager.getSent(loggedInUsername);
        if (outbox.isEmpty()) return NO_EMAILS;

        Email[] results = Emails.query(outbox, query);

        return Emails.toHeaderResponseString(results, DELIMITER, SUBDELIMITER);
    }

    private String handleRead(String[] parts) {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        if (parts.length != 2) return INVALID_REQUEST;

        try {
            int emailId = Integer.parseInt(parts[1]);

            Email email = storage.emailManager.findEmailInInbox(loggedInUsername, emailId);
            if (email == null) {
                return EMAIL_NOT_FOUND;
            }

            return EMAIL_CONTENT + DELIMITER + email.getSubject() + SUBDELIMITER + email.getBody();
        } catch (NumberFormatException e) {
            return INVALID_REQUEST;
        }
    }

    private String handleDelete(String[] parts) {
        if (loggedInUsername == null) return UNAUTHENTICATED;
        if (parts.length != 2) return INVALID_REQUEST;

        try {
            int emailId = Integer.parseInt(parts[1]);

            boolean deletedFromInbox = storage.emailManager.deleteEmailFromInbox(loggedInUsername, emailId);
            boolean deletedFromSent = storage.emailManager.deleteEmailFromSent(loggedInUsername, emailId);

            if (deletedFromInbox || deletedFromSent) {
                return EMAIL_DELETED;
            } else {
                return EMAIL_NOT_FOUND;
            }
        } catch (NumberFormatException e) {
            return INVALID_REQUEST;
        }
    }

    private String handleLogout(String[] parts) {
        if (loggedInUsername == null) return UNAUTHENTICATED;

        loggedInUsername = null;
        return LOGOUT_SUCCESS;
    }

    private String handleExit() {
        return BYE;
    }
}
