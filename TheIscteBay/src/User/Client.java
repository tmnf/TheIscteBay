package User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import Connections.ServerConnection;
import Downloads.DownloadManager;
import Downloads.DownloadRequestManager;
import Downloads.FileInfoHandler;
import HandlerClasses.FileInfo;
import PeerConnections.ConnectionToPeer;
import PeerConnections.PeerConnected;
import SearchClasses.FileDetails;

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

	// Managers
	private DownloadRequestManager requestManager;

	private boolean refreshed; // Manter controlo sobre a resposta do servidor

	public Client(String serverIp, int serverPort, int clientPort, String filePath) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.filePath = filePath;

		requestManager = new DownloadRequestManager();
		usersOnline = new LinkedList<>();

		connectToDirectory();
		startOwnServerSocket();

		gui = new GUI(this);
		gui.open();
	}

	// LIGAÇÕES COM O SERVIDOR

	private void connectToDirectory() {
		System.out.println("A tentar conectar ao servidor...");
		try {
			long initialTime = System.currentTimeMillis();
			Socket so = new Socket(serverIp, serverPort);
			serverConected = new ServerConnection(so, this);
			serverConected.start();
			System.out.println("Conectado ao servidor, Time: " + (System.currentTimeMillis() - initialTime) + " ms");
		} catch (Exception e) {
			System.err.println("Falha na conexão com o servidor");
			System.exit(1);
		}
	}

	public void requestClients() {
		serverConected.requestClients();
		refreshed = false;
	}

	public synchronized void refreshPeersOnline(LinkedList<User> usersOnlineInfo) {
		usersOnline.clear();
		for (User x : usersOnlineInfo)
			usersOnline.add(x);
		refreshed = true;
		notify();
	}

	// LIGAÇÕES COM UM PAR

	public synchronized void requestFileSearch(String keyWord) {
		requestClients();
		System.out.println("A processar lista...");

		while (!refreshed)
			try {
				wait();
			} catch (Exception e) {
				e.printStackTrace();
			}

		System.out.println("Lista processada.");

		if (usersOnline.size() > 1) // Se houver alguem online (Fora o proprio) enviar busca de ficheiro
			sendFileInfoRequest(keyWord);
		else
			System.out.println("Nenhum outro utilizador online");
	}

	private void sendFileInfoRequest(String keyWord) {
		FileInfoHandler fileInfoHandler = new FileInfoHandler(usersOnline.size() - 1, this);
		fileInfoHandler.start();

		for (User x : usersOnline)
			if (x.getPorto() != clientPort) { // Mudar isto
				ConnectionToPeer peer = connectToPeer(x.getEndereco(), x.getPorto(), x.getID());
				peer.sendFileInfoRequest(keyWord, fileInfoHandler);
			}
	}

	public void sendDowloadRequest(FileInfo file) {

		FileDetails fileDetails = file.getFileDetails();
		ArrayList<User> usersWithFile = file.getPeersWithFile();

		DownloadManager downManager = new DownloadManager(fileDetails.getSize());

		int startingIndex = 0;
		int numberOfBytes = fileDetails.getSize();

		int i = 0;
		while (startingIndex < fileDetails.getSize()) {
			if (i >= usersWithFile.size())
				i = 0;

			if (startingIndex + numberOfBytes > fileDetails.getSize())
				numberOfBytes = fileDetails.getSize() - startingIndex;

			ConnectionToPeer peer = connectToPeerWithFile(usersWithFile.get(i));
			peer.sendFilePartRequest(fileDetails.getFileName(), startingIndex, numberOfBytes, downManager);

			startingIndex += numberOfBytes;
			i++;
		}
		downManager.start();
	}

	private ConnectionToPeer connectToPeerWithFile(User x) {
		return connectToPeer(x.getEndereco(), x.getPorto(), x.getID());
	}

	public ConnectionToPeer connectToPeer(String ip, int port, int id) {
		ConnectionToPeer temp = null;
		try {
			Socket so = new Socket(ip, port);
			System.out.println("Conexão establecida com " + so.getInetAddress().getHostAddress());
			temp = new ConnectionToPeer(so, this, new User(ip, port, id));
			temp.start();
			return temp;
		} catch (Exception e) {
			System.err.println("Falha na conexão com o par");
			return temp;
		}
	}

	// LIGAÇÕES DE FORA

	private void startOwnServerSocket() {
		try {
			ss = new ServerSocket(clientPort);
			System.out.println("Servidor-Cliente ligado com sucesso.");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("À espera de pares...");
					while (true) {
						try {
							Socket so = ss.accept();
							System.out.println("Par conectado: " + so.getInetAddress().getHostAddress());
							new PeerConnected(so, Client.this, requestManager).start();
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

	// METODOS GUI

	public void showOnGuiList(FileInfo[] list) {
		gui.showOnList(list);
	}

	// GETTERS

	public int getClientPort() {
		return clientPort;
	}

	public String getPath() {
		return filePath;
	}

	public static void main(String[] args) {
		try {
			new Client(InetAddress.getLocalHost().getHostAddress(), 8080, 4042, "files");
			// Usar args[0], args[1], args[2],args[3] depois.
			// Inet usado aqui para aceder ao ip local de servidor
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
