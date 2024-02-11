import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Application client
public class Client {
    private static Socket socket;
    public static void main(String[] args) throws Exception {
// Adresse et port du serveur
        String serverAddress = "192.168.0.104";
        int port = 5000;

        Scanner scanner = new Scanner(System.in);

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
        scanner.nextLine();
//        scanner.close();

// Création d'une nouvelle connexion aves le serveur
        socket = new Socket(serverAddress, port);
        System.out.format("ClientHandler.java lancé sur [%s:%d]", serverAddress, port);
        System.out.println("\n");


// Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
        DataInputStream in = new DataInputStream(socket.getInputStream());
        //création d'un canal de sortie pour envoyer les messages, par le serveur
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

//        Scanner keyboard = new Scanner(System.in);
        System.out.println("enter your username: ");
        String username = scanner.nextLine();

        System.out.println("enter your password: ");
        String password = scanner.nextLine();

        out.writeUTF(username); // envoi des proprietes du client
        out.writeUTF(password);
        String messageIn = in.readUTF();
        System.out.println(messageIn);
        try {
        // Allow client to send messages
        	Thread messageListener = new Thread(new MessageListener(socket));
            messageListener.start();
            
	        while(true){
	            String messageOut = scanner.nextLine();
	            out.writeUTF(messageOut);
	        }
        }
        catch (IOException e) {
        	scanner.close();
            e.printStackTrace();
        }
        
        String error = in.readUTF();
        if(!error.isEmpty()) {
            System.out.println(error);
            socket.close();
        }

        else{
            // Attente de la réception d'un message envoyé par le, server sur le canal
            String helloMessageFromServer = in.readUTF();
            System.out.println(helloMessageFromServer);
            // fermeture de La connexion avec le serveur
            socket.close();
        }
    }
}