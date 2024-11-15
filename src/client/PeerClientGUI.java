package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class PeerClientGUI extends JFrame {
    private JTextField txtPeerAddress;
    private JTextField txtFileName;
    private JTextArea txtAreaOutput;

    private PeerClient peerClient;

    public PeerClientGUI() {
        setTitle("P2P File Transfer");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // Title Label
        JLabel titleLabel = new JLabel("P2P File Transfer - Reg No: 23BCE1159 & 23BCE1471");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font style and size
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; // Span across two columns
        mainPanel.add(titleLabel, gbc);

        // Input fields
        txtPeerAddress = new JTextField("Enter your IP address");
        txtFileName = new JTextField("Enter filename to register or search");

        // Buttons
        JButton btnRegister = new JButton("Register File");
        JButton btnSearch = new JButton("Search File");
        JButton btnSend = new JButton("Send File");
        JButton btnReceive = new JButton("Receive File");

        // Text area for output
        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false); // Make output area non-editable
        txtAreaOutput.setLineWrap(true);
        txtAreaOutput.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(txtAreaOutput);

        // Add components to main panel
        gbc.gridwidth = 1; // Reset grid width for next components

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Peer Address:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(txtPeerAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("File Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(txtFileName, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(btnRegister, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(btnSearch, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(btnSend, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(btnReceive, gbc);

        // Add output area
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel); // Add the main panel to the frame

        // Button actions
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFile();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchFiles();
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });

        btnReceive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receiveFile();
            }
        });

        setVisible(true);
    }

    private void registerFile() {
        try {
            String address = txtPeerAddress.getText().trim();
            String filename = txtFileName.getText().trim();

            if (!filename.isEmpty()) {
                if (peerClient == null) {
                    peerClient = new PeerClient(address);
                }
                peerClient.registerFile(filename, "127.0.0.1"); // Assuming the index server runs on localhost
                txtAreaOutput.append("Registered: " + filename + "\n");
            } else {
                txtAreaOutput.append("Please enter a valid filename.\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            txtAreaOutput.append("Error registering file: " + ex.getMessage() + "\n");
        }
    }

    private void searchFiles() {
        try {
            String address = txtPeerAddress.getText().trim();
            String filename = txtFileName.getText().trim();

            if (!filename.isEmpty()) {
                if (peerClient == null) {
                    peerClient = new PeerClient(address);
                }
                List<String> peers = peerClient.searchFiles(filename, "127.0.0.1"); // Assuming the index server runs on localhost

                if (!peers.isEmpty()) {
                    txtAreaOutput.append("Found on peers:\n");
                    for (String peer : peers) {
                        txtAreaOutput.append(peer + "\n");
                    }
                } else {
                    txtAreaOutput.append("No peers found for: " + filename + "\n");
                }

            } else {
                txtAreaOutput.append("Please enter a valid filename.\n");
            }

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            txtAreaOutput.append("Error searching files: " + ex.getMessage() + "\n");
        }
    }

    private void sendFile() {
        try {
            String host = JOptionPane.showInputDialog(this, "Enter the IP address of the receiver:");
            String filePath = JOptionPane.showInputDialog(this, "Enter the path of the file to send:");

            if (host != null && filePath != null) {
                int port = 6000; // Port to send files to
                peerClient.sendFile(host, port, filePath);
                txtAreaOutput.append("Sent file: " + filePath + " to " + host + "\n");
            } else {
                txtAreaOutput.append("Invalid input.\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            txtAreaOutput.append("Error sending file: " + ex.getMessage() + "\n");
        }
    }

    private void receiveFile() {
        try {
            int port = 6000; // Port to receive files on
            String saveAs = JOptionPane.showInputDialog(this, "Enter the name to save the received file as:");

            if (saveAs != null) {
                Thread receiverThread = new Thread(() -> {
                    try {
                        peerClient.receiveFile(port, saveAs);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                receiverThread.start(); // Start receiving thread

                txtAreaOutput.append("Waiting for incoming file...\n");

                receiverThread.join(); // Wait for receiver thread to finish

                txtAreaOutput.append("Received file saved as: " + saveAs + "\n");

            } else {
                txtAreaOutput.append("Invalid filename.\n");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PeerClientGUI::new); // Ensure GUI is created on the Event Dispatch Thread
    }
}