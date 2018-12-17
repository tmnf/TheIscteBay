package Client;

public class ClientLauncher {

	public static void main(String[] args) {
		new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
	}
}
