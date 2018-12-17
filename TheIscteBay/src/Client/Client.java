package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import Connections.ServerConnection;
import Handlers.DownloadManager;
import Handlers.DownloadRequestManager;
import Handlers.FileInfoHandler;
import InfoCarriers.FileInfo;
import PeerConnections.ConnectionToPeer;
import PeerConnections.PeerConnected;
import PeerObjects.FileBlockRequestMessage;
import PeerObjects.FileDetails;
import Utils.ConnectionsUtils;

public class Client {

	// Sockets
	private ServerSocket ss;

	// Network Variables
	private String serverIp, filePath;
	private int serverPort, clientPort;

	// GUI
	private GUI gui;

	// Directory Connection
	private ServerConnection serverConected;

	// Lists
	private LinkedList<User> usersOnline;
	private LinkedList<FileBlockRequestMessage> fileParts;

	// Managers
	private DownloadRequestManager requestManager;

	// Keeps control of server's incoming
	private boolean refreshed;

	public Client(String serverIp, int serverPort, int clientPort, String filePath) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.filePath = filePath;

		requestManager = new DownloadRequestManager();

		usersOnline = new LinkedList<>();
		fileParts = new LinkedList<>();

		connectToDirectory();
		startOwnServerSocket();

		gui = new GUI(this);
		gui.open();
	}

	/* Server Connections */

	/* Establishes a connection between the client and the directory */
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

	/* Requests the current list of online users in the directory */
	public void requestClients() {
		serverConected.requestClients();
		refreshed = false;
	}

	/* Refreshes current online users */
	public synchronized void refreshPeersOnline(LinkedList<User> usersOnlineInfo) {
		usersOnline.clear();
		usersOnline.addAll(usersOnlineInfo);
		refreshed = true;
		notify();
	}

	/* Peer Connections */

	/* Asks for the current online user list and checks if there's someone online */
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

		gui.clearList();
		if (usersOnline.size() > 1)
			sendFileInfoRequest(keyWord);
		else
			System.out.println("Nenhum outro utilizador online");

	}

	/* Sends a file info request to all users online */
	private void sendFileInfoRequest(String keyWord) {
		FileInfoHandler fileInfoHandler = new FileInfoHandler(usersOnline.size() - 1, this);
		fileInfoHandler.start();

		for (User x : usersOnline)
			if (x.getPorto() != clientPort || !x.getEndereco().equals(getClientAddress())) {
				ConnectionToPeer peer = ConnectionsUtils.connectToPeer(x.getEndereco(), x.getPorto(), x.getID(), this);
				peer.sendFileInfoRequest(keyWord, fileInfoHandler);
			}
	}

	/* Sends a file download request to all users with file online */
	public void sendDowloadRequest(FileInfo file) {
		FileDetails fileDetails = file.getFileDetails();

		String path = filePath + "/" + fileDetails.getFileName();

		DownloadManager downManager = new DownloadManager(fileDetails.getSize(), this, path, file.getPeersWithFile());
		downManager.start();

		generateFilePartsNeeded(fileDetails);
		connectToPeersWithFile(file.getPeersWithFile(), downManager);
	}

	/* Generates all parts needed to download a certain file */
	private void generateFilePartsNeeded(FileDetails fileDetails) {
		int startingIndex = 0;
		int numberOfBytes = DownloadManager.SIZEPART;

		while (startingIndex < fileDetails.getSize()) {

			if (startingIndex + numberOfBytes > fileDetails.getSize())
				numberOfBytes = fileDetails.getSize() - startingIndex;

			fileParts.add(new FileBlockRequestMessage(fileDetails.getFileName(), startingIndex, numberOfBytes));

			startingIndex += numberOfBytes;
		}
	}

	/* Connects with a peer that has a certain file */
	public void connectToPeersWithFile(ArrayList<User> users, DownloadManager downManager) {
		for (User x : users) {
			ConnectionToPeer peer = ConnectionsUtils.connectToPeer(x.getEndereco(), x.getPorto(), x.getID(), this);
			peer.sendFileRequest(downManager);
		}
	}

	/* Outcome Connections */

	/* Starts own server socket */
	private void startOwnServerSocket() {
		try {
			ss = new ServerSocket(clientPort);
			System.out.println("Servidor_Cliente ligado com sucesso.");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("À espera de pares...");
					while (true) {
						try {
							Socket so = ss.accept();
							System.out.println("Par conectado: " + so.getInetAddress().getHostAddress());
							new PeerConnected(so, Client.this).start();
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

	/* GUI Methods */

	/* Sends a list of files to the GUI */
	public void showOnGuiList(FileInfo[] list) {
		gui.showOnGUI(list);
	}

	/* GETTERS */

	/* Returns Client Port */
	public int getClientPort() {
		return clientPort;
	}

	/* Returns file folder path */
	public String getPath() {
		return filePath;
	}

	/* Returns Local IP Address */
	public String getClientAddress() {
		String address = "";
		try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.err.println("Erro ao obter endreço local");
		}
		return address;
	}

	/* Returns user interface */
	public GUI getGui() {
		return gui;
	}

	/* Returns request manager */
	public DownloadRequestManager getRequestManager() {
		return requestManager;
	}

	/* Returns and removes a part of the file part request list */
	public FileBlockRequestMessage getRequest() {
		synchronized (fileParts) {
			return fileParts.pollFirst();
		}
	}

	/* Adds a lost part request to the list */
	public void addRequest(FileBlockRequestMessage request) {
		synchronized (fileParts) {
			fileParts.add(request);
		}
	}

}
