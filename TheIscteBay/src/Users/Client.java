package Users;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
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
		while (!refreshed) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (usersOnline.size() > 1) // Se houver alguem online(Fora o proprio) enviar busca de ficheiro
			sendFileInfoRequest(keyWord);
		else
			System.out.println("Nenhum utilizador online");
	}

	public void sendFileInfoRequest(WordSearchMessage keyWord) {
		for (User x : usersOnline)
			if (x.getPorto() != clientPort) { // Mudar para getAdress quando usado em diferentes computadores
				connectToPeer(x.getEndereco(), x.getPorto());
				peers.getLast().send(keyWord);
			}
	}

	public void connectToPeer(String ip, int port) {
		try {
			Socket so = new Socket(ip, port);
			System.out.println("Conexão establecida com " + so.getInetAddress());
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
			System.out.println("Servidor-Cliente ligado com sucesso.");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("À espera de pares...");
					while (true) {
						try {
							Socket so = ss.accept();
							System.out.println("Par conectado: " + so.getInetAddress());
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

	public FileDetails[] getFilesWithName(String fileName) throws IOException {
		File[] filesInFolder = new File(filePath).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(fileName);
			}
		});

		FileDetails[] filesWithKeyWord = new FileDetails[filesInFolder.length];

		int i = 0;
		while (i != filesInFolder.length) {
			byte[] fileContent = Files.readAllBytes(filesInFolder[i].toPath());
			filesWithKeyWord[i] = new FileDetails(filesInFolder[i].getName(), fileContent.length);
			i++;
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

	public static void main(String[] args) {
		new Client("192.168.1.75", 8080, 4041, "files"); // Usar args[0], args[1], args[2], args[3]
	}

}
