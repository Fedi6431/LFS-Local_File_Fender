import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.Scanner;


public class    Client1 {
    private static final Scanner scanner = new Scanner(System.in);
    private static int serverPort = 65535; // Port number for the server
    private static int clientPort = 53556; // Port number for the server
    private static boolean rsaSecurity;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Method to get the current client's IPv4 address
    public static String getCurrentClientIpv4() {
        try {
            InetAddress localHost = InetAddress.getLocalHost(); // Get localhost
            String localIP = localHost.getHostAddress(); // Get host address
            return localIP; // Return address
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return ""; // Return empty string on error
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Method to receive a file from the client
    public static void reciver() throws IOException {
        String serverIp = getCurrentClientIpv4(); // Get the server IP

        System.out.println("\n[ ! ] Starting socket server, IP: " + serverIp);
        Socket client1; // Socket to accept the client connection
        try (ServerSocket server = new ServerSocket(serverPort)) { // Create a server socket
            System.out.println("[ ! ] Server started without exceptions...\n[ ! ] Listening on port " + serverPort);
            client1 = server.accept(); // Accept the client connection
        }
        System.out.println("[ ! ] " + client1.getInetAddress() + " connected");

        InputStream input = client1.getInputStream(); // Get input stream from the client socket
        DataInputStream reader = new DataInputStream(input); // Create a reader for the input stream

        String filename = reader.readUTF(); // Read the file name

        // Create a new file with the received filename
        File file = new File(filename);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                System.out.println(buffer); // Print the buffer content for debugging
            }
        }

        System.out.println("[ ! ] File received and saved: " + filename + "\n");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Method to send a file to the server
    public static void sender(String ip, String absoluteFilePath) throws IOException {
        System.out.println("[ ! ] Creating socket connection");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Declare class-level variables
        // Socket for the client connection
        Socket client = new Socket(ip, clientPort); // Create a socket connection to the server

        System.out.println("[ ! ] Setting streams");
        OutputStream output = client.getOutputStream();
        DataOutputStream writer = new DataOutputStream(output);
        System.out.println("[ ! ] Getting path");
        Path path = Path.of(absoluteFilePath); // Get the file path
        System.out.println("[ ! ] Getting file name");
        String filename = path.getFileName().toString(); // Get the file name
        System.out.println("[ ! ] Sending a packet, content: " + filename);
        // Send the file name
        writer.writeUTF(filename);

        // Send the file content
        System.out.println("[ ! ] Sending a packet, content:");
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
                System.out.println(buffer);
            }
        }
        System.out.println("[ ! ] Ensuring that all data is sent");
        writer.flush(); // Ensure all data is sent
        System.out.println("[ ! ] Closing connections\n");
        writer.close(); // Close the output stream
        client.close(); // Close the socket connection
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Main method to run the client
    public static void main(String[] args) {
        String menu = """
                #----------LHFS-LocalHost-File-Sender----------#
                #          1. Server management                #
                #          2. Send file                        #
                #          3. Credits                          #
                #                                         V1.0 #
                #----------------------------------------------#
                """;
        String credits = """
                #----------LHFS-LocalHost-File-Sender----------#
                #                    CREDITS                   #
                #                Dev: Fede                     #
                #       Bug resolver: Fede                     #
                #    Security Addond: Fede                     #
                #----------------------------------------------#
                """;
        String serverManagementMenu = """
                #----------LHFS-LocalHost-File-Sender----------#
                #                SERVER-CONFIG                 #
                #                1. Set port                   #
                #                2. Run server                 #
                #                                              #
                #----------------------------------------------#
                """;

        while (true) {
            try {
                System.out.println(menu);
                System.out.print(getCurrentClientIpv4() + "$-");
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 1) {
                    System.out.println(serverManagementMenu);
                    System.out.print(getCurrentClientIpv4() + "$-");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice == 1) {
                        System.out.print("\nType a new port:");
                        serverPort = scanner.nextInt();
                        System.out.println("Press enter to continue");
                        scanner.nextLine();
                        scanner.nextLine();
                    } else if (choice == 2) {
                        try {
                            reciver();
                        } catch (IOException e) {
                            e.getStackTrace();
                        }
                    } else {
                        System.out.println("Enter a valid option");
                    }
                } else if (choice == 2) {
                    System.out.print("\nType server IPv4 address:");
                    String ip = scanner.nextLine();
                    System.out.print("\nType file absolute path:");
                    String filepath = scanner.nextLine();
                    try {
                        sender(ip, filepath);
                    } catch (IOException e) {
                        e.getStackTrace();
                    }
                } else if (choice == 3) {
                    System.out.println(credits);
                    System.out.println("Press enter to continue");
                    scanner.nextLine();
                } else {
                    System.out.println("Enter a valid option");
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}

