import lombok.extern.slf4j.Slf4j;

import static protocols.EmailUtilities.*;

@Slf4j
public class ClientHandler implements Runnable {
    private final TCPNetworkLayer network;
    private final Storage storage;
    private User user = null;

    public ClientHandler(TCPNetworkLayer networkLayer, Storage storage) {
        this.network = networkLayer;
        this.storage = storage;
    }

    @Override
    public void run() {
        boolean clientSession = true;
        while (clientSession) {
            String request = network.receive();
            log.info("Received request: {}", request);
            String[] parts = request.split(DELIMITER);

            String response = switch (parts[0]) {
                case LOGIN -> handleLogin(parts);
                default -> INVALID_REQUEST;
            };

            network.send(response);
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
        if (user != null) return ALREADY_LOGGED_IN;
        String username = parts[1];
        String password = parts[2];
        // TODO: Replace with UserManager.getUser(username) or add UserManager as property of storage
        User loginUser = storage.users.getUser(username);

        if (loginUser == null) return INVALID_CREDENTIALS;
        if (!loginUser.getPassword().equals(password)) return INVALID_CREDENTIALS;

        user = loginUser;

        return LOGIN_SUCCESS;
    }
}
