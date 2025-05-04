import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static protocols.EmailUtilities.*;

@Slf4j
public class ClientHandler implements Runnable {
    private final TCPNetworkLayer network;
    private final Storage storage;
    private String loggedInUsername = null;
    public ClientHandler(TCPNetworkLayer networkLayer, Storage storage) {
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

                String response;

                switch (parts[0]) {
                    case LOGIN -> response = handleLogin(parts);
                    case REGISTER -> response = handleRegister(parts);
                    case SEND -> response = handleSend(parts);
                   // case LIST_RECEIVED -> response = handleListReceived(parts);
                    //case LIST_SENT -> response = handleListSent(parts);
                    //case SEARCH_RECEIVED -> response = handleSearchReceived(parts);
                    //case SEARCH_SENT -> response = handleSearchSent(parts);
                    //case READ -> response = handleRead(parts);
                    //case DELETE -> response = handleDelete(parts);
                    case LOGOUT -> {
                        loggedInUsername = null;
                        response = LOGOUT_SUCCESS;
                    }
                    case EXIT -> {
                        response = BYE;
                        clientSession = false;
                    }
                    default -> response = INVALID_REQUEST;
                }
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
        // TODO: Replace with UserManager.getUser(username) or add UserManager as property of storage
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

        boolean success = storage.userManager.addUser(username, password);
        if (success) {
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

        String[] recipientArray = recipientPart.split(",");
        List<String> recipients = Arrays.asList(recipientArray);

        List<String> invalidRecipients = new ArrayList<>();

        // Validate recipients
        for (String recipient : recipients) {
            if (!storage.userManager.userExists(recipient)) {
                invalidRecipients.add(recipient);
            }
        }

        if (!invalidRecipients.isEmpty()) {
            return "USER_NOT_FOUND__" + String.join(",", invalidRecipients);
        }

        storage.emailManager.sendEmail(loggedInUsername, recipients, subject, body);

        return EMAIL_SENT;
    }

}
