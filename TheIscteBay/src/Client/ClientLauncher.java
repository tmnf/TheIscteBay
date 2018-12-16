package Client;

import java.net.InetAddress;

public class ClientLauncher {

	public static void main(String[] args) {
		try {
			new Client(InetAddress.getLocalHost().getHostAddress(), 8080, 4022, "files");
			// Usar args[0], args[1], args[2],args[3] depois.
			// Inet usado aqui para aceder ao ip local de servidor
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
