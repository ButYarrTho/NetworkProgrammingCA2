import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

import static protocols.EmailUtilities.*;

@Slf4j
public class EmailClient {
    public static void main(String[] args) {
        // TEST Code
        Scanner sc = new Scanner(System.in);

        TCPNetworkLayer network = new TCPNetworkLayer(HOSTNAME, PORT);
        try {
            network.connect();
        } catch (IOException e) {
            log.error("Could not establish connection with the server");
            System.exit(1);
            return;
        }

        boolean clientRunning = true;
        while (clientRunning) {
            System.out.print("> ");
            String request = sc.nextLine();
            network.send(request);
            String response = network.receive();
            System.out.println(response);
        }
    }
}
