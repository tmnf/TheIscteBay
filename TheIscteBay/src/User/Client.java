package User;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

import Connections.GeneralConnection;
import Connections.PeerConnection;
import Connections.ServerConnection;
import Downloads.DownloadManager;
import Downloads.RequestManager;
import SearchClasses.FileBlockRequestMessage;
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

	private RequestManager req;
	public DownloadManager down;

	private boolean refreshed; // Manter controlo sobre a resposta do servidor

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

	public void refreshPeersOnline(LinkedList<User> usersOnlineInfo) {
		usersOnline.clear();
		for (User x : usersOnlineInfo)
			usersOnline.add(x);
		refreshed = true;
	}

	// LIGAÇÕES COM UM PAR

	public void requestFileSearch(WordSearchMessage keyWord) { // Temp, para testing
		requestClients();
		System.out.println("A processar lista...");
		while (!refreshed)
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		System.out.println("Lista processada.");

		if (usersOnline.size() > 1) // Se houver alguem online (Fora o proprio) enviar busca de ficheiro
			sendFileInfoRequest(keyWord);
		else
			System.out.println("Nenhum outro utilizador online");
	}

	private void sendFileInfoRequest(WordSearchMessage keyWord) {
		req = new RequestManager(this, usersOnline.size() - 1);
		for (User x : usersOnline)
			if (x.getPorto() != clientPort) { // Mudar para getAdress quando usado em diferentes computadores e redes
				connectToPeer(x.getEndereco(), x.getPorto(), x.getID());
				peers.getLast().send(keyWord);
			}
	}

	public void sendDowloadRequest(FileDetails file) {
		int startingIndex = 0;
		int numberOfBytes = DownloadManager.SIZEPART;
		int i = -1;
		while ((startingIndex + numberOfBytes) <= file.getSize()) {
			i++;
			if (i >= peers.size())
				i = 0;
			if (numberOfBytes > file.getSize())
				numberOfBytes = file.getSize() - startingIndex;
			peers.get(i).send(new FileBlockRequestMessage(file.getFileName(), startingIndex, numberOfBytes));
			startingIndex += numberOfBytes;
		}
		down = new DownloadManager(peers.size(), file.getSize());
		down.start();
	}

	public byte[] getFilePart(FileBlockRequestMessage temp) throws IOException { // Devolve a parte do
																					// ficheiro
		byte[] file = Files.readAllBytes(Paths.get(filePath + "/" + temp.getFileName()));
		byte[] filePart = new byte[temp.getNumberOfBytes()];
		for (int i = 0, aux = temp.getStartingIndex(); i != temp.getNumberOfBytes(); i++, aux++)
			filePart[i] = file[aux];
		return filePart;
	}

	public void connectToPeer(String ip, int port, int id) {
		synchronized (peers) {
			try {
				Socket so = new Socket(ip, port);
				System.out.println("Conexão establecida com " + so.getInetAddress().getHostAddress());
				PeerConnection temp = new PeerConnection(so, this, req);
				temp.start();
				peers.add(temp);
			} catch (Exception e) {
				System.err.println("Falha na conexão com o par");
				System.exit(1);
			}
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
			System.out.println("Servidor-Cliente ligado com sucesso.");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("À espera de pares...");
					while (true) {
						try {
							Socket so = ss.accept();
							System.out.println("Par conectado: " + so.getInetAddress().getHostAddress());
							new PeerConnection(so, Client.this, req).start();
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

	public FileDetails[] getFilesWithName(String fileName) throws IOException {
		File[] filesInFolder = new File(filePath).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(fileName);
			}
		});

		FileDetails[] filesWithKeyWord = new FileDetails[filesInFolder.length];

		for (int i = 0; i != filesInFolder.length; i++) {
			byte[] fileContent = Files.readAllBytes(filesInFolder[i].toPath());
			filesWithKeyWord[i] = new FileDetails(filesInFolder[i].getName(), fileContent.length);
		}

		return filesWithKeyWord;
	}

	// METODOS GUI

	public void showOnGuiList(FileDetails[] list) { // A funcionar so para uma thread ainda
		gui.showOnList(list);
	}

	// GETTERS

	public int getClientPort() {
		return clientPort;
	}

	public int getPeers() {
		return peers.size();
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
