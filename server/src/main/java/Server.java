import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12345;

    private static final Map<Integer, GameRoom> rooms = new ConcurrentHashMap<>();
    private static int roomCounter = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            printServerLog("Server started on port " + PORT + ".");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                printServerLog("New connection from " + clientSocket + ".");

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException exception) {
            printServerLog("IOException: " + exception.getMessage());
        }
    }

    public static synchronized void joinRoom(ClientHandler client) {
        boolean joined = false;

        for (GameRoom room : rooms.values()) {
            if (room.canJoin()) {
                room.addClient(client);
                joined = true;
                break;
            }
        }

        if (!joined) {
            GameRoom newRoom = new GameRoom(roomCounter++);
            newRoom.addClient(client);
            rooms.put(newRoom.getId(), newRoom);
        }
    }

    public static synchronized void removeRoom(int roomId) {
        rooms.remove(roomId);
    }

    /**
     * Handles a newly connected client.
     */
    private static synchronized void handleClient(Socket clientSocket) {
        try {
            ClientHandler handler = new ClientHandler(clientSocket);
            handler.sendMessage("Type [JOIN] to join a game or [EXIT] to leave.");
        } catch (IOException e) {
            printServerLog("Client error: " + e.getMessage());
        }
    }

    /**
     * Prints a server log message.
     */
    private static synchronized void printServerLog(String message) {
        System.out.println("[SERVER] " + message);
    }
}