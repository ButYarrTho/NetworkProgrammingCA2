import lombok.extern.slf4j.Slf4j;
import networking.INetworkLayer;
import networking.TCPNetworkLayer;

import java.io.IOException;
import java.util.Scanner;

import static protocols.EmailUtilities.*;

@Slf4j
public class Client {
    private final Scanner scanner;
    private final INetworkLayer network;

    public Client() {
        network = new TCPNetworkLayer(HOSTNAME, PORT);
        scanner = new Scanner(System.in);
        try {
            network.connect();
        } catch (IOException e) {
            log.error("Unable to connect to server.");
            System.exit(1);
            return;
        }
        System.out.println("Connected to server at " + HOSTNAME + ":" + PORT);
    }

    public void start() {
        boolean clientRunning = true;
        boolean userSignedIn = false;

        while (clientRunning) {
            if (userSignedIn) {
                System.out.println("1. Send model.Email");
                System.out.println("2. List Received");
                System.out.println("3. List Sent");
                System.out.println("4. Log out");
                System.out.println("5. Exit");
                System.out.print("Choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> sendEmail();
                    case "2" -> listReceived();
                    case "3" -> listSent();
                    case "4" -> {
                        logout();
                        userSignedIn = false;
                    }
                    case "5" -> {
                        exit();
                        clientRunning = false;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> {
                        if (login()) userSignedIn = true;
                    }
                    case "2" -> {
                        if (register()) userSignedIn = true;
                    }
                    case "3" -> {
                        clientRunning = false;
                        exit();
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String message = LOGIN + DELIMITER + username + DELIMITER + password;
        String response = sendAndReceive(message);

        return response.equalsIgnoreCase(LOGIN_SUCCESS);
    }

    private boolean register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String message = REGISTER + DELIMITER + username + DELIMITER + password;
        String response = sendAndReceive(message);

        return response.equalsIgnoreCase(REGISTER_SUCCESS);
    }

    private void sendEmail() {
        System.out.print("Recipient(s) (comma-separated usernames): ");
        String recipients = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Body: ");
        String body = scanner.nextLine();

        String message = SEND + DELIMITER + recipients + DELIMITER + subject + DELIMITER + body;
        sendAndReceive(message);
    }

    private void listReceived() {
        sendAndReceive(LIST_RECEIVED);
    }

    private void listSent() {
        sendAndReceive(LIST_SENT);
    }

    private void exit() {
        sendAndReceive(EXIT);
        try {
            network.disconnect();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            log.error("Error closing connection.");
        }
    }

    private void logout() {
        sendAndReceive(LOGOUT);
    }

    private String sendAndReceive(String message) {
        network.send(message);
        String response = network.receive();
        if (response != null) {
            System.out.println("Server: " + response);
        } else {
            System.out.println("Server closed the connection.");
        }

        return response;
    }

    public static void main(String[] args) {
        new Client().start();
    }
}