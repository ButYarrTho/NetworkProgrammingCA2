package networking;

import java.io.IOException;

public interface INetworkLayer {
    void connect() throws IOException;

    void send(String message);

    String receive();

    void close();

    void disconnect() throws IOException;
}
