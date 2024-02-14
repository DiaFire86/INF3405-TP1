import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.net.Socket;


public class Server {
    private static ServerSocket Listener; // Application ClientHandler.java
    private static List<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) throws Exception {
    	// Compteur incrémenté à chaque connexion d'un client au serveur
        int clientNumber = 0;
        // Adresse et port du serveur
        Scanner scanner = new Scanner(System.in);
        String serverAddress = "192.168.0.104";

        boolean addresseValide = false;
        while(!addresseValide) {
            addresseValide = true;
            System.out.println("Entrez une addresse: ");
            serverAddress = scanner.nextLine();

            String[] arrOfIp = serverAddress.split("\\.");
            if(arrOfIp.length != 4){
                System.out.println("Mauvaise adresse");
                addresseValide = false;
                continue;
            }
            for (String part : arrOfIp) {
                int numPart = Integer.parseInt(part);
                if(numPart<0||numPart>255) {
                    addresseValide = false;
                    break;
                }

            }
        }
        int serverPort = 0;
        while(serverPort < 5000 || serverPort > 5050){
            System.out.println("Entrer un port: ");
            serverPort = scanner.nextInt();
        }
        scanner.close();

        // Création de la connexien pour communiquer ave les, clients
        Listener = new ServerSocket();
        Listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(serverAddress);

        
        // Association de l'adresse et du port à la connexien
        Listener.bind(new InetSocketAddress(serverIP, serverPort));
        System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
        try {
        	// À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
        	// run() de l'objet ClientHandler
            while (true) {
            	// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
            	// Une nouvetle connection : on incémente le compteur clientNumber 
            	ClientHandler clientHandler = new ClientHandler(Listener.accept(), clientNumber++);
            	clients.add(clientHandler);
            	clientHandler.start();
            }
        } finally {
        	// Fermeture de la connexion
            Listener.close();
        }  
    } 
    
    public static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client removed. Number of clients: " + clients.size());
    }
    
    public static void broadcastMessage(String message, int clientNumber) throws IOException {
        for (ClientHandler client : clients) {
        	if (client.getClientNumber() != clientNumber)
            client.sendMessage(message);
        }
    }
}