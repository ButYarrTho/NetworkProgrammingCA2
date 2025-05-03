import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler implements Runnable {
    private final TCPNetworkLayer network;
    private final Storage storage;

    public ClientHandler(TCPNetworkLayer networkLayer, Storage storage) {
        this.network = networkLayer;
        this.storage = storage;
    }

    @Override
    public void run() {
        // TEST Code
        boolean clientSession = true;
        while (clientSession) {
            String request = network.receive();
            String response = "";
            log.info(request);
            response = "Received: " + request;
            network.send(response);
        }
    }
}
