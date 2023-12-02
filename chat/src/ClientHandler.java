import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Kullanıcı adını terminal üzerinden al
            System.out.print("Kullanıcı adınızı giriniz: ");
            this.username = reader.readLine();
            if (this.username == null || this.username.trim().isEmpty()) {
                throw new IllegalArgumentException("Geçersiz kullanıcı adı");
            }

            ChatServer.broadcastMessage("SUNUCU: " + username + " chate katıldı!", this);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                ChatServer.broadcastMessage(username + ": " + clientMessage, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeEverything();
            ChatServer.broadcastMessage("SUNUCU: " + username + " chatten ayrıldı!", this);
            ChatServer.clients.remove(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }

    private void closeEverything() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}