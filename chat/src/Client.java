import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 3000);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print("Kullanıcı adınızı giriniz: ");
            String username = userInputReader.readLine();
            writer.write(username + "\n");
            writer.flush();

            Thread listenerThread = new Thread(new MessageListener(reader));
            listenerThread.start();

            while (true) {
                String userMessage = userInputReader.readLine();
                writer.write(userMessage + "\n");
                writer.flush();

                // Çıkış koşulunu kontrol et
                if (userMessage.equalsIgnoreCase("/exit")) {
                    break; // "/exit" girildiğinde döngüden çık
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class MessageListener implements Runnable {
        private BufferedReader reader;

        public MessageListener(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = reader.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                Logger.getLogger(Client.class.getName()).log(Level.INFO, "Chatten Ayrıldınız.");
            }
        }
    }
}