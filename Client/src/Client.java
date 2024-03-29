import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// Application client
public class Client {
    private static Socket socket;
    public static void main(String[] args) throws Exception {
// Adresse et port du serveur
        String serverAddress = "192.168.0.104";
        int port = 0;

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

        while(port < 5000 || port > 5050){
            System.out.println("Entrer un port: ");
            port = scanner.nextInt();
        }
        scanner.nextLine();

// Création d'une nouvelle connexion aves le serveur
        try {
        	socket = new Socket(serverAddress, port);
        } catch (IOException e) {
        	scanner.close();
        	System.out.println("Error");
        }
        System.out.format("ClientHandler.java lancé sur [%s:%d]", serverAddress, port);
        System.out.println("\n");


// Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
        DataInputStream in = new DataInputStream(socket.getInputStream());
        //création d'un canal de sortie pour envoyer les messages, par le serveur
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

//        Scanner keyboard = new Scanner(System.in);
        System.out.println("enter your username: ");
        String username = scanner.nextLine();
        while (username == null)
        {
        	System.out.println("enter your username: ");
            username = scanner.nextLine();
        }
        System.out.println("enter your password: ");
        String password = scanner.nextLine();
        while (password == null)
        {
        	System.out.println("enter your password: ");
            password = scanner.nextLine();
        }

        out.writeUTF(username); // envoi des proprietes du client
        out.writeUTF(password);
        String messageIn = in.readUTF();
        System.out.println(messageIn);
        try {
        // Allow client to send messages
        	Thread messageListener = new Thread(new MessageListener(socket));
            messageListener.start();
            
	        while(true){
	            String messageScanned = scanner.next();
	            if (messageScanned.length() <= 200)
	            {
	            	LocalDateTime now = LocalDateTime.now();
	            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
	            	String formattedDateTime = now.format(formatter);
	            	String messageOut = "[" + username + " - " + serverAddress + ":" + port + " - " + formattedDateTime + "]: " + messageScanned;
		            out.writeUTF(messageOut);
	            }
	            else {
	            	System.out.println("Message is too long!" + '\n');
	            }
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
            //socket.close();
        }
    }
}