package Utils;

import java.net.Socket;

import Client.Client;
import Client.User;
import PeerConnections.ConnectionToPeer;

public class ConnectionsUtils {

	/* Connects to a peer and returns this connection */
	public static ConnectionToPeer connectToPeer(String ip, int port, int id, Client client) {
		ConnectionToPeer temp = null;
		try {
			Socket so = new Socket(ip, port);
			System.out.println("Conexão establecida com " + so.getInetAddress().getHostAddress());
			temp = new ConnectionToPeer(so, client, new User(ip, port, id));
			temp.start();
			return temp;
		} catch (Exception e) {
			return temp;
		}
	}

}
