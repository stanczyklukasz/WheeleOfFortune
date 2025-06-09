🎯 WheeleOfFortune – Aplikacja Klient-Serwer (Java TCP)

## 📌 Opis projektu
Niniejszy projekt to uproszczona implementacja gry słownej "Koło Fortuny" w architekturze klient-serwer z użyciem komunikacji TCP/IP.
Aplikacja została napisana w języku Java i umożliwia grę do 3 graczy jednocześnie w trybie tekstowym (CLI).

Projekt powstał w ramach: `Programowanie aplikacji klient-serwer`.

---

## 🛠️ Funkcjonalności
- Obsługa wielu graczy i wielu niezależnych pokojów (lobby).
- Gra w systemie turowym – jeden gracz odpowiada w danym momencie.
- Losowe losowanie wartości punktowych i haseł z puli.
- Gra kończy się po odgadnięciu 3 haseł – następnie można dołączyć do nowej gry.
- Obsługa komend:
  - `[JOIN]` - dołącza do istniejacego bądź nowego lobby (GameRoom)
  - `[START]` – rozpoczyna grę, jeśli jest przynajmniej 2 graczy w lobby (GameRoom)
  - `[SCOREBOARD]` – pokazuje ranking wszystkich graczy w danym lobby (GameRoom)
  - `[SURRENDER]` – rezygnacja z gry, skutuje przerwaniem danej gry
  - `[EXIT]` – zamyka aplikację klienta i informuje innych graczy

---

🧩 Struktura projektu z podziałem na role

📦 server/src/main/java/
- Server.java – punkt startowy serwera, nasłuchuje na połączenia TCP
- ClientHandler.java – tworzy oraz obsługuje osobny wątek dla każdego klienta serwera
- GameRoom.java – serwis zarządzający logiką gry
- ScoreBoard.java – serwis zarządzający punktacją gry
- PhraseProvider.java – serwis odpowiedzialny za dostarczanie haseł do gier
- SpinWheel.java – serwis odpowiedzialny za losowanie wartości punktowych
- PlayerAction.java – spis predefiniowanych akcji użytkownika

📦 client/src/main/java/
- Client.java – klient TCP z interfejsem CLI
