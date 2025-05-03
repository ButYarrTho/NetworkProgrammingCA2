import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPNetworkLayer {
    private Socket dataSocket;
    private Scanner scanner;
    private PrintWriter outputStream;
    private final String hostname;
    private final int port;

    // Client-side constructor
    public TCPNetworkLayer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    // Server-side constructor
    public TCPNetworkLayer(Socket socket) throws IOException {
        this.dataSocket = socket;
        this.hostname = null;
        this.port = -1;
        setStreams();
    }

    private void setStreams() throws IOException {
        this.scanner = new Scanner(dataSocket.getInputStream());
        this.outputStream = new PrintWriter(dataSocket.getOutputStream(), true);
    }

    public void connect() throws IOException {
        this.dataSocket = new Socket(hostname, port);
        setStreams();
    }

    public void send(String message) {
        outputStream.println(message);
        outputStream.flush();
    }

    public String receive() {
        return scanner.nextLine();
    }

    public void disconnect() throws IOException {
        if (dataSocket != null) dataSocket.close();
    }
}
