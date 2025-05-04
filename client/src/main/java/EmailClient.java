import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

import static protocols.EmailUtilities.*;
//TEST CLIENT
@Slf4j
public class EmailClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TCPNetworkLayer network = new TCPNetworkLayer(HOSTNAME, PORT);
        try {
            network.connect();
        } catch (IOException e) {
            log.error("Could not establish connection with the server");
            System.exit(1);
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("Commands: LOGIN, REGISTER, SEND, LIST_RECEIVED, LIST_SENT, READ, DELETE, LOGOUT, EXIT");
            System.out.print("> ");
            String command = sc.nextLine();
            network.send(command);
            String response = network.receive();
            System.out.println(response);
            if (command.equals(EXIT)) {
                running = false;
                network.close();
            }
        }
    }
}
