import java.util.Random;

public class SpinWheel {
    private final int[] values = {100, 200, 300, 400, 500, 0};
    private final Random random = new Random();

    public int spin() {
        return values[random.nextInt(values.length)];
    }
}
