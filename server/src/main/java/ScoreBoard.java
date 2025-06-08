import java.util.HashMap;
import java.util.Map;

public class ScoreBoard {
    private final Map<ClientHandler, Integer> scores = new HashMap<>();

    /**
     * Adds a new player to the scoreboard with an initial score of 0.
     */
    public void addPlayer(ClientHandler player) {
        scores.put(player, 0);
    }

    /**
     * Adds points to a player's score.
     */
    public void addPoints(ClientHandler player, int points) {
        scores.put(player, scores.getOrDefault(player, 0) + points);
    }

    /**
     * Returns a formatted string representing the current scoreboard.
     */
    public String getFormattedScoreBoard() {
        StringBuilder sb = new StringBuilder("=== SCOREBOARD ===\n");

        scores.forEach((player, score) ->
            sb.append(player.getName())
                .append(": ")
                .append(score)
                .append(" points\n")
        );

        return sb.toString();
    }

    /**
     * Gets the current score of a specific player.
     */
    public int getByPlayer(ClientHandler client) {
        return scores.getOrDefault(client, 0);
    }
}
