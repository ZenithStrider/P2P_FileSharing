package server;

import common.FileInfo;

import java.io.*;
import java.net.*;
import java.util.*;

public class IndexServer {
    private static final int PORT = 5000;
    private static Map<String, List<String>> fileRegistry = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Index Server started...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                String action = in.readUTF();
                if ("register".equals(action)) {
                    FileInfo fileInfo = (FileInfo) in.readObject();
                    registerFile(fileInfo);
                    out.writeUTF("Registered: " + fileInfo.getFileName());
                } else if ("search".equals(action)) {
                    String fileName = in.readUTF();
                    List<String> peers = searchFile(fileName);
                    out.writeObject(peers.toArray(new String[0]));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void registerFile(FileInfo fileInfo) {
            fileRegistry.computeIfAbsent(fileInfo.getFileName(), k -> new ArrayList<>()).add(fileInfo.getPeerAddress());
            System.out.println("Registered file: " + fileInfo.getFileName() + " from " + fileInfo.getPeerAddress());
        }

        private List<String> searchFile(String fileName) {
            return fileRegistry.getOrDefault(fileName, new ArrayList<>());
        }
    }
}