public class ClientHandler implements Runnable {
    private final TCPNetworkLayer networkLayer;
    private final Storage storage;

    public ClientHandler(TCPNetworkLayer networkLayer, Storage storage) {
        this.networkLayer = networkLayer;
        this.storage = storage;
    }

    @Override
    public void run() {

    }
}
