package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Users.User;

public class Server {

	private ServerSocket ss;

	private int serverPort, currentId;

	private LinkedList<User> usersOnline;

	public Server(int serverPort) {
		this.serverPort = serverPort;
		usersOnline = new LinkedList<>();
	}

	public void startServer() {
		try {
			ss = new ServerSocket(serverPort);
			System.out.println("Server iniciado" + ss);
			acceptClients();
		} catch (Exception e) {
			System.err.println("Erro ao inicializar servidor");
			System.exit(1);
		}
	}

	private void acceptClients() {
		System.out.println("À espera de conexões...");
		while (true) {
			try {
				Socket so = ss.accept();
				new ClientHandler(so, this, currentId++).start();
				System.out.println("Client conectado" + so);
			} catch (Exception e) {
				System.err.println("Erro ao conectar");
			}
		}
	}

	public void registerUser(String adress, int port, int id) {
		synchronized (usersOnline) {
			usersOnline.add(new User(adress, port, id));
		}
	}

	public synchronized void disconectUser(int ID) {
		usersOnline.remove(ID);
		currentId--;
	}

	public synchronized LinkedList<User> getUsersOnline() {
		return usersOnline;
	}

	public static void main(String[] args) {
		Server server = new Server(8080); // Usar args[0]
		server.startServer();
	}

}
