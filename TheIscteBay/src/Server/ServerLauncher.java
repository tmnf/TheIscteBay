package Server;

public class ServerLauncher {

	public static void main(String[] args) {
		Server server = new Server(8080); // Usar args[0]
		server.startServer();
	}

}
