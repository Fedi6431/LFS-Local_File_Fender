import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class Main extends JFrame {
    private int serverPort = 65535; // Port number for the server
    private static int clientPort = 53556; // Port number for the server
    private static String ip;

    public static String getCurrentClientIpv4() {
        try {
            InetAddress localHost = InetAddress.getLocalHost(); // Get localhost
            return localHost.getHostAddress(); // Return address
        } catch (Exception e) {
            String message = e.getMessage();// Print stack trace for debugging
            System.out.println(message);
            return ""; // Return empty string on error
        }
    }

    public void reciver() throws IOException {
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
                System.out.println(Arrays.toString(buffer)); // Print the buffer content for debugging
            }
        }
        System.out.println("[\n - ] File received and saved: " + filename + "\n");
    }

    public static void sender(String ip, String absoluteFilePath) throws IOException {
        System.out.println("\n[ - ] Creating socket connection");
        Socket client = new Socket(ip, clientPort); // Create a socket connection to the server
        System.out.println("[ \\- ] Socket connection created");

        System.out.println("\n[ - ] Setting streams");
        OutputStream output = client.getOutputStream();
        DataOutputStream writer = new DataOutputStream(output);
        System.out.println("\n[ - ] Getting path");
        Path path = Path.of(absoluteFilePath); // Get the file path
        System.out.println("[ \\- ] Path retrieved: " + path);
        System.out.println("\n[ - ] Getting file name");
        String filename = path.getFileName().toString(); // Get the file name
        System.out.println("[ \\- ] File name retrived: "+filename);
        System.out.println("\n[ - ] Sending a packet, content: " + filename);
        // Send the file name
        writer.writeUTF(filename);

        // Send the file content
        System.out.println("\n[ ! ] Sending a packet, content:");
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
                System.out.println(Arrays.toString(buffer));
            }
        }
        System.out.println("[ - ] Ensuring that all data is sent\n");
        writer.flush(); // Ensure all data is sent
        System.out.println("[ - ] Closing connections\n");
        writer.close(); // Close the output stream
        client.close(); // Close the socket connection
    }

    private static boolean isValidIP(String ip) {
        // Check if the IP is not empty and has <= 15 characters
        if (Objects.equals(ip, "") ||ip == null || ip.isEmpty() || ip.length() > 15) {
            return false;
        }

        // Regular expression to match a valid IP address
        String ipPattern =
                "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + // First octet
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + // Second octet
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + // Third octet
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";    // Fourth octet

        return ip.matches(ipPattern);
    }

    public static void main(String[] args) {
        System.out.println("[ - ] Creating frame: frame");
        JFrame frame = new JFrame("File Transfer");

        System.out.println("[ | ] Setting frame size");
        frame.setSize(600, 400);
        System.out.println("[ \\- ] Width: " + frame.getWidth() + " Height: " + frame.getHeight());

        frame.setResizable(false);
        System.out.println("[ - ] Resizable: " + frame.isResizable());

        frame.setLayout(new BorderLayout());
        System.out.println("[ - ] Layout: " + frame.getLayout());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.out.println("[ - ] Default close operation: " + frame.getDefaultCloseOperation());

        System.out.println("[ | ] Creating tabbed pane");
        JTabbedPane tabbedPane = new JTabbedPane();
        System.out.println("[ \\- ] Tabbed pane created");

        System.out.println("[ | ] Creating panel: senderPanel");
        JPanel senderPanel = new JPanel();
        System.out.println("[ \\- ] Panel created");
        senderPanel.setLayout(null);

        System.out.println("[ | ] Creating label: senderLabel");
        JLabel senderLabel = new JLabel("Send files via IPv4!");
        System.out.println("[ \\ ] Label text: " + senderLabel.getText());
        senderLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        System.out.println("[ \\ ] Label font: " + senderLabel.getFont());
        senderLabel.setBounds(150, 10, 300, 30);
        System.out.println("[ \\ ] Label bounds: " + senderLabel.getBounds());
        senderPanel.add(senderLabel);
        System.out.println("[ \\- ] Label added to senderPanel");

        JTextField ipTextField = new JTextField("Type the IPv4");
        System.out.println("[ | ] Text field created: ipTextField");
        System.out.println("[ \\ ] Text field default String: " + ipTextField.getText());
        ipTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        System.out.println("[ \\ ] Text field font: " + ipTextField.getFont());
        ipTextField.setBounds(150, 50, 300, 30);
        System.out.println("[ \\ ] Text field bounds: " + ipTextField.getBounds());
        senderPanel.add(ipTextField);
        System.out.println("[ \\- ] Text field added to senderPanel");

        JButton setIp = new JButton("Set ip");
        System.out.println("[ | ] Button created: setIp");
        System.out.println("[ \\ ] Button text: " + setIp.getText());
        setIp.setFont(new Font("Arial", Font.PLAIN, 15));
        System.out.println("[ \\ ] Button font: " + setIp.getFont());
        setIp.setBounds(460, 50, 100, 30);
        System.out.println("[ \\ ] Button bounds: " + setIp.getBounds());
        senderPanel.add(setIp);
        System.out.println("[ \\- ] Button added to senderPanel");

        JTextField portTextField = new JTextField("Type port number (default 53556)");
        System.out.println("[ | ] Text field created: portTextField");
        System.out.println("[ \\ ] Text field default String: " + portTextField.getText());
        portTextField.setFont(new Font("Arial", Font.PLAIN, 13));
        System.out.println("[ \\ ] Text field font: " + portTextField.getFont());
        portTextField.setBounds(150, 90, 300, 30);
        System.out.println("[ \\ ] Text field bounds: " + portTextField.getBounds());
        senderPanel.add(portTextField);
        System.out.println("[ \\- ] Text field added to senderPanel");

        JButton setPort = new JButton("Set port");
        System.out.println("[ | ] Button created: setPort");
        System.out.println("[ \\ ] Button text: " + setPort.getText());
        setPort.setFont(new Font("Arial", Font.PLAIN, 15));
        System.out.println("[ \\ ] Button font: " + setPort.getFont());
        setPort.setBounds(460, 90, 100, 30);
        System.out.println("[ \\ ] Button bounds: " + setPort.getBounds());
        senderPanel.add(setPort);
        System.out.println("[ \\- ] Button added to senderPanel");

        JLabel label = new JLabel("Selected file: None");
        System.out.println("[ | ] Label created: label");
        System.out.println("[ \\ ] Label text: " + label.getText());
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        System.out.println("[ \\ ] Label font: " + label.getFont());
        label.setBounds(150, 130, 300, 30);
        System.out.println("[ \\ ] Label bounds: " + label.getBounds());
        senderPanel.add(label);
        System.out.println("[ \\- ] Label added to senderPanel");

        JButton selectButton = new JButton("Select file");
        System.out.println("[ | ] Button created: selectButton");
        System.out.println("[ \\ ] Button text: " + selectButton.getText());
        selectButton.setFont(new Font("Arial", Font.PLAIN, 15));
        System.out.println("[ \\ ] Button font: " + selectButton.getFont());
        selectButton.setBounds(460, 130, 100, 30);
        System.out.println("[ \\ ] Button bounds: " + selectButton.getBounds());
        senderPanel.add(selectButton);
        System.out.println("[ \\- ] Button added to senderPanel");

        JButton sendButton = new JButton("Send file");
        System.out.println("[ | ] Button created: sendButton");
        System.out.println("[ \\ ] Button text: " + sendButton.getText());
        sendButton.setFont(new Font("Arial", Font.PLAIN, 20));
        System.out.println("[ \\ ] Button font: " + sendButton.getFont());
        sendButton.setBounds(150, 170, 300, 30);
        System.out.println("[ \\ ] Button bounds: " + sendButton.getBounds());
        senderPanel.add(sendButton);
        System.out.println("[ \\- ] Button added to senderPanel");

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("[ ! ] '" + selectButton.getText() + "' button pressed");
                // Create a JFileChooser
                JFileChooser fileChooser = new JFileChooser();
                System.out.println("[ | ] Creating file chooser");
                System.out.println("[ \\ ] Saving return value");
                int returnValue = fileChooser.showOpenDialog(null);
                System.out.println("[ \\\\ ] Return value saved");

                // Check if the user selected a file
                System.out.println("[ \\\\\\ ] Checking return value status");
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    System.out.println("[ \\\\\\- ] Status: " + returnValue);
                    System.out.println("[ \\ ] Retrieving file path");
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("[ \\\\- ] File path retrieved: " + selectedFile);
                    // Get the file icon
                    System.out.println("[ \\ ] Retrieving file image");
                    ImageIcon fileIcon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(selectedFile);
                    System.out.println("[ \\\\- ] File image retrieved: " + fileIcon);
                    System.out.println("[ - ]\n");
                    // Set the icon and text in the label
                    System.out.println("[ | ] Updating label 'label'");
                    System.out.println("[ \\ ] Updating icon");
                    label.setIcon(fileIcon);
                    System.out.println("[ \\\\- ] Icon updated as '" + fileIcon + "'");
                    System.out.println("[ \\ ] Updating text");
                    label.setText("Selected file: " + selectedFile.getAbsolutePath());
                } else {
                    System.out.println("[ | ] Updating label 'label'");
                    System.out.println("[ \\ ] Updating icon");
                    label.setIcon(null); // Clear the icon if no file is selected
                    System.out.println("[ \\\\- ] Icon updated as '" + null + "'");
                    System.out.println("[ \\ ] Updating text");
                    label.setText("No file selected.");
                }
                System.out.println("[ \\\\- ] Text updated as '" + label.getText() + "'");
                System.out.println("[ - ]\n");
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("[ ! ] '" + sendButton.getText() + "' button pressed");
                System.out.println("[ | ] Retrieving information");
                System.out.println("[ \\ ] Retrieving ip ");
                String ip = ipTextField.getText();
                System.out.println("[ \\\\- ] Ip retrieved: " + ip);
                System.out.println("[ \\ ] Retrieving file path");
                String filePath = label.getText();
                System.out.println("[ \\\\- ] File path retrieved: " + filePath);
                System.out.println("[ - ]\n");

                if (filePath.startsWith("Selected file: ")) {
                    System.out.println("[ | ] Removing useless String in the file path");
                    filePath = filePath.replace("Selected file: ", "");
                    System.out.println("[ \\- ] Useless String removed from the file path");
                    System.out.println("[ - ]\n");
                }

                boolean isIpValid;
                boolean isPathValid;

                System.out.println("[ | ] Checking ip validity");
                ip = ipTextField.getText();
                if (!isValidIP(ip)) {
                    System.out.println("[ \\- ] Ip parameters not valid");
                    isIpValid=false;
                    JOptionPane.showMessageDialog(frame, "The ip doesn't respect the parameters: <=15, not blank.", "Invalid ip", JOptionPane.ERROR_MESSAGE);
                    System.out.println("[ - ]\n");
                } else {
                    System.out.println("[ \\- ] Ip parameters valid");
                    isIpValid=true;
                    System.out.println("[ - ]\n");
                }

                System.out.println("[ | ] Checking file path validity");
                if (filePath.equalsIgnoreCase("selected file: none") || filePath.equalsIgnoreCase("no file selected.")) {
                    System.out.println("[ \\- ] File path parameters not valid");
                    isPathValid=false;
                    JOptionPane.showMessageDialog(frame, "The file path isn't specified", "Invalid file path", JOptionPane.ERROR_MESSAGE);
                    System.out.println("[ - ]\n");
                } else {
                    System.out.println("[ \\- ] File path parameters valid");
                    isPathValid=true;
                    System.out.println("[ - ]\n");
                }

                if (isIpValid && isPathValid) {
                    try {
                        sender(ip, filePath);
                    } catch (IOException err) {
                        err.getStackTrace();
                    }
                }

            }
        });

        setPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = portTextField.getText(); // Get text from the text field
                try {
                    clientPort = Integer.parseInt(inputText); // Try to convert to int
                    // If successful, set the port
                    System.out.println("[ - ] Remote server port set to: " + clientPort+"\n");
                    // Here you can set the port in your application logic
                } catch (NumberFormatException ex) {
                    // Handle the case where the input is not a valid integer
                    System.out.println("[ ! ] Invalid input: " + inputText + "\n");
                    // Optionally, you can show a dialog to inform the user
                    JOptionPane.showMessageDialog(frame, "Please enter a valid integer for the port.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setIp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ip = ipTextField.getText(); // Get text from the text field

                // Validate the IP address
                if (isValidIP(ip)) {
                    System.out.println("[ - ] Remote server ip set to: " + ip +"\n");
                } else {
                    System.out.println("[ ! ] Invalid input: " + ip + "\n");
                    JOptionPane.showMessageDialog(frame, "Please enter a valid IP address.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tabbedPane.addTab("Sender", senderPanel);
        System.out.println("[ \\- ] Sender panel added to tabbed pane");

        tabbedPane.addTab("Receiver", new JPanel());
        System.out.println("[ \\- ] Receiver panel added to tabbed pane");

        frame.add(tabbedPane, BorderLayout.CENTER);
        System.out.println("[ \\- ] Tabbed pane added to frame");

        frame.setVisible(true);
        System.out.println("[ - ] Frame set to visible");
    }
}
