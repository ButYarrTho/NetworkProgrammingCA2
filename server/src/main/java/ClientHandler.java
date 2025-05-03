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
}
