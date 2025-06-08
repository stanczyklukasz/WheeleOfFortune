import java.io.*;
import java.net.*;

public class ClientHandler {
    private static int clientCounter = 1;

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final String name;

    private GameRoom room;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);

        this.name = "Player_" + clientCounter++;

        new Thread(this::listen).start();
    }

    /**
     * Assigns a game room to the player.
     */
    public void assignRoom(GameRoom room) {
        this.room = room;
    }

    /**
     * Removes the player from the current game room.
     */
    public void removeRoom() {
        this.room = null;
    }

    /**
     * Sends a message to the client.
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * Returns the client's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Listens for input from the client and handles actions accordingly.
     */
    private void listen() {
        try {
            String clientInput;

            while ((clientInput = reader.readLine()) != null) {
                clientInput = clientInput.trim();

                PlayerAction action = PlayerAction.tryFrom(clientInput);

                if (action.isExit()) {
                    handleExitAction();
                    break;
                }

                if (action.isJoin()) {
                    handleJoinAction();
                    continue;
                }

                if (room == null) {
                    sendMessage("Type [JOIN] to join a game or [EXIT] to leave.");
                    continue;
                }

                room.handleMessage(this, clientInput);
            }
        } catch (IOException e) {
            closeConnection();
        } finally {
            closeConnection();
        }
    }

    /**
     * Handles the client's [EXIT] action.
     * If the player is in a game room, the room is closed and a message is broadcast.
     */
    private void handleExitAction() throws IOException {
        if (room != null) {
            room.closeRoom("Player " + getName() + " has left the game. The game has been cancelled.");
        }

        sendMessage("You have left the game. See you next time!");
    }

    /**
     * Handles the client's [JOIN] action.
     * If the player is already in a room, a message is sent.
     * Otherwise, the player is added to a game room.
     */
    private void handleJoinAction() throws IOException {
        if (room != null) {
            sendMessage("You are already in game room: " + room.getId());
            return;
        }

        Server.joinRoom(this);
    }

    /**
     * Closes the connection to the client.
     */
    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
