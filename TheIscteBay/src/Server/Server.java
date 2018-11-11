package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

import User.User;

public class Server {

	// Ligações
	private ServerSocket ss;

	// Portas
	private int serverPort;

	// Utilizadores registados
	private LinkedList<User> users;

	public Server(int serverPort) {
		this.serverPort = serverPort;
		users = new LinkedList<>();
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
			users.add(new User(adress, port, id));
		}
		System.out.println("Cliente registado. ID: " + id);
	}

	public void disconectUser(int ID) {
		synchronized (users) {
			User aux = null;
			for (User x : users)
				if (x.getID() == ID)
					aux = x;
			users.remove(aux);
		}
		System.out.println("Cliente disconectou: ID: " + ID);
	}

	public LinkedList<User> getUsersOnline() {
		return users;
	}

	private int generateRandomID() {
		Random rnd = new Random();
		int id = rnd.nextInt(999999999);

		for (User x : users) // Criar ID unico
			if (x.getID() == id)
				id = rnd.nextInt(999999999);

		return id;
	}

	public static void main(String[] args) {
		Server server = new Server(8080); // Usar args[0]
		server.startServer();
	}

}
