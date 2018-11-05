package Connections;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import Users.Client;
import Users.User;

public class ServerConnection extends GeneralConnection {

	public ServerConnection(Socket so, Client client) throws IOException {
		super(so, client);
	}

	public void run() {
		while (!interrupted()) {
			try {
				String[] aux = ((String) in.readObject()).split(" ");

				if (aux[0].equals("CLT")) {
					LinkedList<User> tempList = new LinkedList<>();
					while (!aux[0].equals("END")) {
						tempList.add(new User(aux[1], Integer.parseInt(aux[2]), Integer.parseInt(aux[3])));
						aux = ((String) in.readObject()).split(" ");
					}
					mainClient.refreshPeersOnline(tempList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Conexão terminada");
			}
		}
	}

	public void registerOnServer() {
		try {
			String insc = "INSC " + InetAddress.getLocalHost().getHostAddress() + " " + mainClient.getClientPort();
			out.writeObject(insc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void requestClients() {
		try {
			out.writeObject("CLT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
