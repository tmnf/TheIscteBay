package Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Connections.PeerConnection;
import Connections.ServerConnection;

public class Client {

	// Sockets
	private ServerSocket ss;

	// NetworkVariables
	private String serverIp, filePath;
	private int serverPort, clientPort;

	// GUI
	private GUI gui;

	// Directory Connections
	private ServerConnection serverConected;
	private LinkedList<User> usersOnline;

	public Client(String serverIp, int serverPort, int clientPort, String filePath) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.filePath = filePath;

		usersOnline = new LinkedList<>();

		connectToDirectory();
		serverConected.registerOnServer();

		startOwnServerSocket();

		gui = new GUI();
		gui.open();
	}

	private void connectToDirectory() {
		try {
			Socket so = new Socket(serverIp, serverPort);
			serverConected = new ServerConnection(so, this);
			serverConected.start();
		} catch (Exception e) {
			System.err.println("Falha na conexão com o servidor");
			System.exit(1);
		}
	}

	public void connectToPeer(String ip, int port) {
		try {
			Socket so = new Socket(serverIp, serverPort);
			new PeerConnection(so, this).start();
		} catch (Exception e) {
			System.err.println("Falha na conexão com o par");
			System.exit(1);
		}
	}

	public void requestClients() {
		serverConected.requestClients();
	}

	public int getClientPort() {
		return clientPort;
	}

	private void startOwnServerSocket() {
		try {
			ss = new ServerSocket(clientPort);
			System.out.println("Servidor-Cliente ligado..");
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						System.out.println("À espera de pares...");
						try {
							Socket so = ss.accept();
							System.out.println("Par conectado: " + so);
							new PeerConnection(so, Client.this).start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshPeersOnline(LinkedList<User> usersOnlineInfo) {
		this.usersOnline.clear();
		for (User x : usersOnlineInfo) {
			this.usersOnline.add(x);
			System.out.println(x);
		}
	}

	public static void main(String[] args) {
		Client client = new Client("192.168.1.75", 8080, 4044, "files"); // Usar args[0], args[1], args[2], args[3]
		client.requestClients();
	}

}
