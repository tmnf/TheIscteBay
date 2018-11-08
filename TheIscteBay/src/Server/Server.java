package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import User.User;

public class Server {

	// Ligações
	private ServerSocket ss;

	// Portas
	private int serverPort;

	// Utilizadores registados
	private HashMap<Integer, User> users;

	public Server(int serverPort) {
		this.serverPort = serverPort;
		users = new HashMap<>();
	}

	public void startServer() {
		try {
			ss = new ServerSocket(serverPort);
			System.out.println("Server iniciado: " + ss);
			acceptClients();
		} catch (Exception e) {
			System.err.println("Erro ao inicializar servidor");
			System.exit(1);
		}
	}

	private synchronized void acceptClients() {
		System.out.println("À espera de conexões...");
		while (true) {
			try {
				Socket so = ss.accept();
				new ClientHandler(so, this, generateRandomID()).start();
				System.out.println("Client conectado" + so);
			} catch (Exception e) {
				System.err.println("Erro ao conectar");
			}
		}
	}

	public void registerUser(String adress, int port, int id) {
		synchronized (users) {
			users.put(id, new User(adress, port, id));
		}
		System.out.println("Cliente registado. ID: " + id);
	}

	public void disconectUser(int ID) {
		synchronized (users) {
			users.remove(ID);
		}
		System.out.println("Cliente disconectou: ID: " + ID);
	}

	public LinkedList<User> getUsersOnline() {
		LinkedList<User> temp = new LinkedList<>(users.values());
		return temp;
	}

	private int generateRandomID() {
		Random rnd = new Random();
		int id = rnd.nextInt(999999999);

		while (users.containsKey(id))
			id = rnd.nextInt(999999999);

		return id;
	}

	public static void main(String[] args) {
		Server server = new Server(8080); // Usar args[0]
		server.startServer();
	}

}
