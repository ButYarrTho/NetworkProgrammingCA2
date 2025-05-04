import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static protocols.EmailUtilities.*;


public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Scanner scanner;

    public Client() {
        try {
            socket = new Socket(HOSTNAME, PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to server at " + HOSTNAME + ":" + PORT);
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
            System.exit(1);
        }
    }

    public void start() {
        boolean sessionActive = true;
        while (sessionActive) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Send Email");
            System.out.println("4. List Received");
            System.out.println("5. List Sent");
            System.out.println("6. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> sendEmail();
                case "4" -> listReceived();
                case "5" -> listSent();
                case "6" -> {
                    exit();
                    sessionActive = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }

        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String message = LOGIN + DELIMITER + username + DELIMITER + password;
        sendAndReceive(message);
    }

    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String message = REGISTER + DELIMITER + username + DELIMITER + password;
        sendAndReceive(message);
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
        close();
    }

    private void sendAndReceive(String message) {
        writer.println(message);
        try {
            String response = reader.readLine();
            if (response != null) {
                System.out.println("Server: " + response);
            } else {
                System.out.println("Server closed the connection.");
            }
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }

    private void close() {
        try {
            socket.close();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            System.out.println("Error closing connection.");
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}