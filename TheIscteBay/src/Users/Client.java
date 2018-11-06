package Users;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;

import Connections.GeneralConnection;
import Connections.PeerConnection;
import Connections.ServerConnection;
import SearchClasses.FileDetails;
import SearchClasses.WordSearchMessage;

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

	// Lists
	private LinkedList<User> usersOnline;
	private LinkedList<PeerConnection> peers;

	private boolean refreshed; // Temp

	public Client(String serverIp, int serverPort, int clientPort, String filePath) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.filePath = filePath;

		usersOnline = new LinkedList<>();
		peers = new LinkedList<>();

		connectToDirectory();
		startOwnServerSocket();

		gui = new GUI(this);
		gui.open();
	}

	// LIGAÇÕES COM O SERVIDOR

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

	public void requestClients() {
		serverConected.requestClients();
		refreshed = false;
	}

	public void refreshPeersOnline(LinkedList<User> usersOnlineInfo) {
		usersOnline.clear();
		for (User x : usersOnlineInfo)
			usersOnline.add(x);
		refreshed = true;
	}

	// LIGAÇÕES COM UM PAR

	public void requestAndConnectAndSearch(WordSearchMessage keyWord) { // Temp, para testing
		requestClients();
		while (!refreshed) {
			System.out.println("A processar lista...");
		}

		if (usersOnline.size() != 0) { // Se houver alguem online procurar enviar busca de ficheiro
			for (User x : usersOnline) {
				if (x.getPorto() != clientPort) // Mudar para getAdress quando usado em diferentes computadores
					connectToPeer(x.getEndereco(), x.getPorto());
			}
			for (PeerConnection peer : peers)
				peer.send(keyWord);
		}
	}

	public void connectToPeer(String ip, int port) {
		try {
			Socket so = new Socket(ip, port);
			System.out.println("conexao establecida com " + so);
			PeerConnection temp = new PeerConnection(so, this);
			temp.start();
			peers.add(temp);
		} catch (Exception e) {
			System.err.println("Falha na conexão com o par");
			System.exit(1);
		}
	}

	public void disconectPeer(GeneralConnection peer_Client) {
		synchronized (peers) {
			peers.remove(peer_Client);
		}
	}

	// LIGAÇÕES DE FORA

	private void startOwnServerSocket() {
		try {
			ss = new ServerSocket(clientPort);
			System.out.println("Servidor-Cliente ligado..");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("À espera de pares...");
					while (true) {
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
			System.err.println("Erro na inicialização do ServerSocket do cliente");
			System.exit(1);
		}
	}

	public ArrayList<FileDetails> getFilesWithName(String fileName) throws IOException {
		File[] files = new File(filePath).listFiles();
		ArrayList<FileDetails> aux = new ArrayList<>();
		int i = 0;

		while (i != files.length) {
			if (files[i].getName().contains(fileName)) {
				byte[] fileContent = Files.readAllBytes(files[i].toPath());
				aux.add(new FileDetails(files[i].getName(), fileContent.length));
			}
			i++;
		}
		return aux;
	}

	// METODOS GUI

	public void showOnGuiList(ArrayList<FileDetails> list) { // A funcionar so para uma thread ainda
		gui.showOnList(list);
	}

	// GETTERS

	public int getClientPort() {
		return clientPort;
	}

	public static void main(String[] args) {
		new Client("192.168.1.75", 8080, 4042, "files"); // Usar args[0], args[1], args[2], args[3]
		new Client("192.168.1.75", 8080, 4043, "files");
	}

}
