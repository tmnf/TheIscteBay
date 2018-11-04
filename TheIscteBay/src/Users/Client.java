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

	private ServerSocket ss;
	private Socket so;

	// Channels
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private String serverIp, filePath;
	private int serverPort, clientPort;

	public Client(String serverIp, int serverPort, int clientPort, String filePath) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.filePath = filePath; // Useless for now...

		connectToServer();
		registerOnServer();
	}

	private void connectToServer() {
		try {
			so = new Socket(serverIp, serverPort);

			out = new ObjectOutputStream(so.getOutputStream());
			in = new ObjectInputStream(so.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
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
					System.out.println(x.getEndereco() + "," + x.getPorto());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void requestClients() {
		try {
			out.writeObject("CLT");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client("192.168.1.75", 8080, 4043, "Alguma");
		client.start();

		client.requestClients();
	}

}
