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
            System.out.println("4. List Received Emails");
            System.out.println("5. Read Email");
            System.out.println("6. Delete Email");
            System.out.println("7. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> sendEmail();
                case "4" -> listReceivedEmails();
                case "5" -> readEmail();
                case "6" -> deleteEmail();
                case "7" -> {
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

        String message = "LOGIN__" + username + "__" + password;
        sendAndReceive(message);
    }

    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String message = "REGISTER__" + username + "__" + password;
        sendAndReceive(message);
    }

    private void exit() {
        sendAndReceive("EXIT");
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

    private void sendEmail() {
        System.out.print("Recipient(s) (comma-separated usernames): ");
        String recipients = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Body: ");
        String body = scanner.nextLine();

        String message = "SEND__" + recipients + "__" + subject + "__" + body;
        sendAndReceive(message);
    }

    private void readEmail() {
        System.out.print("Enter Email ID to read: ");
        String emailId = scanner.nextLine();

        writer.println("READ__" + emailId);

        try {
            String response = reader.readLine();
            if (response == null) {
                System.out.println("Server closed the connection.");
                return;
            }

            if (response.startsWith("EMAIL_CONTENT__")) {
                String[] parts = response.substring("EMAIL_CONTENT__".length()).split("::", 2);
                System.out.println("\nSubject: " + parts[0]);
                System.out.println("Body: " + parts[1]);
            } else if (response.equals(EMAIL_NOT_FOUND)) {
                System.out.println("Email not found.");
            } else {
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }

    private void listReceivedEmails() {
        writer.println("LIST_RECEIVED");
        try {
            String response = reader.readLine();
            if (response == null) {
                System.out.println("Server closed the connection.");
                return;
            }
            if (response.startsWith("RECEIVED__")) {
                String[] emails = response.substring("RECEIVED__".length()).split("__");
                System.out.println("\nYour Inbox:");
                for (String emailMeta : emails) {
                    String[] metaParts = emailMeta.split("::");
                    System.out.println("ID: " + metaParts[0] +
                            ", From: " + metaParts[1] +
                            ", Subject: " + metaParts[2] +
                            ", Timestamp: " + metaParts[3]);
                }
            } else if (response.equals(NO_EMAILS)) {
                System.out.println("Inbox is empty.");
            } else {
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }

    private void deleteEmail() {
        System.out.print("Enter Email ID to delete: ");
        String emailId = scanner.nextLine();

        writer.println("DELETE__" + emailId);

        try {
            String response = reader.readLine();
            if (response == null) {
                System.out.println("Server closed the connection.");
                return;
            }

            switch (response) {
                case EMAIL_DELETED -> System.out.println("Email deleted successfully.");
                case EMAIL_NOT_FOUND -> System.out.println("Email not found.");
                case UNAUTHENTICATED -> System.out.println("You must login first.");
                default -> System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }




    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}