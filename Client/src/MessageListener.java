// MessageListener.java
import java.io.*;
import java.net.Socket;

public class MessageListener implements Runnable {
    private Socket socket;

    public MessageListener(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (true) {
                try {
                    String messageIn = in.readUTF();
                    System.out.println("Received from server: " + messageIn);
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                    break; // Exit the loop on IO error
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
