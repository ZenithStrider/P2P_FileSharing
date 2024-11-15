package client;

import common.FileInfo;

import java.io.*;
import java.net.*;
import java.util.*;

public class PeerClient {
    private String peerAddress;

    public PeerClient(String peerAddress) {
        this.peerAddress = peerAddress;
    }

    public void registerFile(String fileName, String serverAddress) throws IOException {
        try (Socket socket = new Socket(serverAddress, 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeUTF("register");
            out.writeObject(new FileInfo(fileName, peerAddress));
            out.flush();
        }
    }

    public List<String> searchFiles(String fileName, String serverAddress) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddress, 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeUTF("search");
            out.writeUTF(fileName);
            out.flush();

            // Read the array of peer addresses from the server
            String[] peersArray = (String[]) in.readObject();
            return Arrays.asList(peersArray); // Convert array to list
        }
    }

    // Method to receive a file from another peer
    public void receiveFile(int port, String saveAs) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        try (Socket socket = serverSocket.accept();
             InputStream inputStream = socket.getInputStream();
             FileOutputStream fos = new FileOutputStream(saveAs)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("File received successfully: " + saveAs);
        } finally {
            serverSocket.close();
        }
    }

    // Method to send a file to another peer
    public void sendFile(String host, int port, String filePath) throws IOException {
        try (Socket socket = new Socket(host, port);
             FileInputStream fis = new FileInputStream(filePath);
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("File sent successfully: " + filePath);
        }
    }
}