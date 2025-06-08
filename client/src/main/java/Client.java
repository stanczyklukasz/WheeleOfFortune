import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Scanner scanner = new Scanner(System.in);

        System.out.println("[CLIENT] Połączono z serwerem. Wpisz [JOIN] aby dolaczyc do gry lub [EXIT] aby wyjsc.");

        Thread readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("[CLIENT] Rozłączono z serwerem.");
                System.exit(0);
            }
        });
        readerThread.start();

        while (true) {
            String input = scanner.nextLine();
            out.println(input);
            if (input.trim().equalsIgnoreCase("[EXIT]")) {
                break;
            }
        }

        socket.close();
        System.exit(0);
    }
}