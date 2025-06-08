import java.util.*;

public class PhraseProvider {
    private static final List<String> ALL_PHRASES = Arrays.asList(
        "Java",
        "Klient Serwer",
        "YouTube",
        "Konstytucja 3 Maja",
        "Europarlamentarzysta"
    );

    public static Queue<String> getRandomPhrases(int count) {
        List<String> shuffled = new ArrayList<>(ALL_PHRASES);
        Collections.shuffle(shuffled);
        return new LinkedList<>(shuffled.subList(0, Math.min(count, shuffled.size())));
    }
}
