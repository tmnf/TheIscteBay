package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

import Client.User;

public class Server {

	// Sockets
	private ServerSocket ss;

	// Ports
	private int serverPort;

	// Registered Users
	private LinkedList<User> users;

	public Server(int serverPort) {
		this.serverPort = serverPort;
		users = new LinkedList<>();
	}

	/* Starts directory server */
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

	/* Keeps on listening for new connections */
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

	/* Registers user on directory */
	public void registerUser(String adress, int port, int id) {
		synchronized (users) {
			users.add(new User(adress, port, id));
		}
		System.out.println("Cliente registado. ID: " + id);
	}

	/* Removes user from directory */
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

	/* Returns users online list */
	public LinkedList<User> getUsersOnline() {
		return users;
	}

	/* Generates unique ID to each user */
	private int generateRandomID() {
		Random rnd = new Random();
		int id = rnd.nextInt(999999999);

		for (User x : users) // Criar ID unico
			if (x.getID() == id)
				id = rnd.nextInt(999999999);

		return id;
	}

}
