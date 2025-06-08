import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GameRoom {
    private final int GUESSED_PASSWORD_POINT = 1000;
    private final int MAX_PLAYERS = 3;
    private final int PHRASES_PER_GAME = 1;


    private final int id;
    private final List<ClientHandler> clients = new ArrayList<>(MAX_PLAYERS);
    private final Queue<String> gamePhrases = PhraseProvider.getRandomPhrases(PHRASES_PER_GAME);
    private String currentPhrase;
    private StringBuilder maskedPhrase;
    private int currentPlayerIndex = 0;
    private boolean started = false;
    private final ScoreBoard scoreBoard = new ScoreBoard();
    private final SpinWheel spinWheel = new SpinWheel();

    public GameRoom(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Checks if is possible to join the room.
     */
    public boolean canJoin() {
        return !isFull() && !isStarted();
    }

    /**
     * Adds the new player (ClientHandler / SocketHandler) to the game room.
     */
    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
        scoreBoard.addPlayer(client);

        broadcast("[ROOM " + id + "] " + client.getName() + " joined. Current # of players: " + clients.size());
        client.assignRoom(this);

        if (clients.size() == 3) {
            broadcast("The maximum number of players has been reached. The game will start automatically!");
            startGame();
        } else {
            client.sendMessage("[ROOM " + id + "] Waiting for other players. Type [SURRENDER] to leave the room.");
        }
    }

    /**
     * Handles the player/client input (socket input).
     */
    public synchronized void handleMessage(ClientHandler client, String message) throws IOException {
        PlayerAction playerAction = PlayerAction.tryFrom(message);

        if (playerAction.isSurrender()) {
            closeRoom("Player " + client.getName() + " has left the game. The game has been cancelled.");
            return;
        }

        if (playerAction.isScoreboard()) {
            client.sendMessage(scoreBoard.getFormattedScoreBoard());
            return;
        }

        if (!isStarted()) {
            if (playerAction.isStart() && clients.size() <= MAX_PLAYERS) {
                startGame();
                return;
            }

            client.sendMessage("The game has not started yet. Waiting for a [START] command from a player.");
            return;
        }

        if (!client.equals(currentPlayer())) {
            client.sendMessage("It's not your turn. Please wait for your turn.");
            return;
        }

        if (message.length() == 1 && Character.isLetter(message.charAt(0))) {
            handleLetter(client, message);
        } else {
            handlePhrase(client, message);
        }
    }

    /**
     * Handles the player given letter
     */
    private void handleLetter(ClientHandler client, String message) {
        char guess = Character.toUpperCase(message.charAt(0));
        int guessedLetters = 0;

        for (int i = 0; i < currentPhrase.length(); i++) {
            if (Character.toUpperCase(currentPhrase.charAt(i)) == guess && maskedPhrase.charAt(i) == '_') {
                maskedPhrase.setCharAt(i, currentPhrase.charAt(i));
                guessedLetters++;
            }
        }

        if (guessedLetters == 0) {
            broadcast("Letter " + guess + " does not appear. Next player: " + nextPlayer().getName());
            currentPlayer().sendMessage("It's your turn. Phrase: " + maskedPhrase);
            return;
        }

        int points = spinWheel.spin() * guessedLetters;
        scoreBoard.addPoints(client, points);

        broadcast("Letter " + guess + " is correct! Current points: " + scoreBoard.getByPlayer(client));
        broadcast("Phrase: " + maskedPhrase);

        if (!maskedPhrase.toString().contains("_")) {
            nextRoundOrEnd();
        } else {
            client.sendMessage("Keep guessing or try the full phrase:");
        }
    }

    /**
     * Handles the player given phrase.
     */
    private void handlePhrase(ClientHandler client, String message) {
        if (!message.equalsIgnoreCase(currentPhrase)) {
            broadcast("Incorrect answer. Next player: " + nextPlayer().getName());
            currentPlayer().sendMessage("Your turn. Phrase: " + maskedPhrase);
            return;
        }

        broadcast("Player " + client.getName() + " discovered the phrase!");
        scoreBoard.addPoints(client, GUESSED_PASSWORD_POINT);
        nextRoundOrEnd();
    }

    /**
     * Closes the game room.
     * Optional you can provide the message and broadcast it to all players.
     */
    public void closeRoom(String message) {
        if (message != null) {
            broadcast(message);
        }

        for (ClientHandler client : clients) {
            client.removeRoom();
        }

        Server.removeRoom(id);
    }

    /**
     * Sends the given message to all connected players.
     */
    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    /**
     * Starts the game.
     */
    private void startGame() {
        started = true;
        startNextPhrase();
    }

    /**
     * Move players to next round or finish the game when the no phrases left.
     */
    private void nextRoundOrEnd() {
        if (gamePhrases.isEmpty()) {
            endGame();
            return;
        }

        broadcast("Another phrase...");
        startNextPhrase();
    }

    /**
     * Set new phrase as current game step.
     */
    private void startNextPhrase() {
        currentPhrase = gamePhrases.poll();

        maskedPhrase = new StringBuilder();

        for (char character : currentPhrase.toCharArray()) {
            maskedPhrase.append(
                Character.isLetter(character) ? '_' : character
            );
        }

        broadcast("The game begins! Phrase: " + maskedPhrase);

        currentPlayer().sendMessage("It's your turn. Enter a letter or guess the whole phrase:");
    }

    /**
     * Gets the player whose turn it currently is.
     */
    private ClientHandler currentPlayer() {
        return clients.get(currentPlayerIndex);
    }

    /**
     * Advances the turn to the next player and returns them.
     */
    private ClientHandler nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % clients.size();

        return currentPlayer();
    }

    /**
     * Ends the game by broadcasting the final scores and closing the room.
     */
    private void endGame() {
        broadcast("Game over! Final scores:");
        broadcast(scoreBoard.getFormattedScoreBoard());

        closeRoom("The game has ended. Type [JOIN] to join a new one.");
    }

    /**
     * Checks if the room is full.
     */
    private boolean isFull() {
        return clients.size() >= 3;
    }

    /**
     * Checks if the game is already started
     */
    private boolean isStarted() {
        return started;
    }
}