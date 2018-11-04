package Users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Server.User;

public class Client extends Thread {

	// Sockets
	private ServerSocket ss;
	private Socket so;

	// Channels
	private ObjectInputStream in;
	private ObjectOutputStream out;

	// NetworkVariables
	private String serverIp, filePath;
	private int serverPort, clientPort;

	// GUI
	private GUI gui;

	public Client(String serverIp, int serverPort, int clientPort, String filePath) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.filePath = filePath; // Useless for now...

		connectToDirectory();
		registerOnServer();

		gui = new GUI();
		gui.open();
	}

	private void connectToDirectory() {
		try {
			so = new Socket(serverIp, serverPort);

			out = new ObjectOutputStream(so.getOutputStream());
			in = new ObjectInputStream(so.getInputStream());
		} catch (Exception e) {
			System.err.println("Falha na conexão");
			System.exit(1);
		}
	}

	private void registerOnServer() {
		String insc;
		try {
			insc = "INSC " + InetAddress.getLocalHost() + " " + clientPort;
			out.writeObject(insc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				Object aux = in.readObject();
				for (User x : (LinkedList<User>) aux)
					System.out.println(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void requestClients() {
		try {
			out.writeObject("CLT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client("192.168.1.75", 8080, 4043, "Alguma"); // Usar args[0], args[1], args[2], args[3]
		client.start();

		client.requestClients();
	}

}
